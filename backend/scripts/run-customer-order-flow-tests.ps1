#!/usr/bin/env pwsh
[CmdletBinding()]
param(
  [string]$BaseUrl = 'http://localhost:8080',
  [string]$User = 'qa_client',
  [string]$Pass = 'Abc123',
  [int]$TemplateId = 0,
  [string]$ProviderUser = 'qa_provider',
  [string]$ProviderPass = 'Abc123'
)

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Err($m){ Write-Host "[ERROR] $m" -ForegroundColor Red }

try{
  # Login as customer
  Write-Info "Login as customer"
  $body = @{ username=$User; password=$Pass } | ConvertTo-Json
  $login = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/login" -ContentType 'application/json' -Body $body
  $token = $login.token; if(-not $token){ throw "Login failed" }
  $headers = @{ Authorization = "Bearer $token" }

  # List my templates (provider) or public templates (fallback)
  Write-Info "List order templates (public/all requires admin), fallback to provider create"
  $createdTemplateId = $null
  if(-not $TemplateId -or $TemplateId -eq 0){
    # login as provider to create a template
    Write-Info "Login as provider to create a template"
    $provBody = @{ username=$ProviderUser; password=$ProviderPass } | ConvertTo-Json
    $provLogin = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/login" -ContentType 'application/json' -Body $provBody
    $provToken = $provLogin.token; if(-not $provToken){ throw "Provider login failed" }
    $provHeaders = @{ Authorization = "Bearer $provToken" }
    $templateName = "AutoTemplate-" + [guid]::NewGuid().ToString('N').Substring(0,8)
    $tpl = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/ordertemplates/$templateName" -Headers $provHeaders -ErrorAction Stop
    $createdTemplateId = $tpl.otId
    if(-not $createdTemplateId){ throw "Create template failed" }
    Write-Info "Created template otId=$createdTemplateId"
    # configure payment methods
    $payReq = @{ paymentMethods = @('FullPayment','Installment2_1') } | ConvertTo-Json
    try { Invoke-RestMethod -Method Patch -Uri "$BaseUrl/api/ordertemplates/$createdTemplateId/paymentmethod" -Headers $provHeaders -ContentType 'application/json' -Body $payReq -ErrorAction Stop | Out-Null; Write-Info 'paymentmethod set' } catch { Write-Err "paymentmethod failed: $($_.Exception.Message)"; throw }
    # configure delivery type + business days
    $delReq = @{ deliveryType = 'SelectedByTheCustomer'; businessDays = 5 } | ConvertTo-Json
    try { Invoke-RestMethod -Method Patch -Uri "$BaseUrl/api/ordertemplates/$createdTemplateId/deliverytype" -Headers $provHeaders -ContentType 'application/json' -Body $delReq -ErrorAction Stop | Out-Null; Write-Info 'deliverytype set' } catch { Write-Err "deliverytype failed: $($_.Exception.Message)"; throw }
    # configure starting price
    $spReq = @{ startingPrice = 100 } | ConvertTo-Json
    try { Invoke-RestMethod -Method Patch -Uri "$BaseUrl/api/ordertemplates/$createdTemplateId/startingprice" -Headers $provHeaders -ContentType 'application/json' -Body $spReq -ErrorAction Stop | Out-Null; Write-Info 'startingprice set' } catch { Write-Err "startingprice failed: $($_.Exception.Message)"; throw }

    # Verify template fields before creating order
    $meList = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/ordertemplates/me" -Headers $provHeaders -ErrorAction SilentlyContinue
    $current = $meList | Where-Object { $_.otId -eq $createdTemplateId } | Select-Object -First 1
    Write-Info ("Template check => paymentMethods={0} deliveryType={1} startingPrice={2}" -f ($current.paymentMethods -join ','), $current.deliveryType, $current.startingPrice)
    # switch back to customer headers
    $headers = @{ Authorization = "Bearer $token" }
  } else {
    $createdTemplateId = $TemplateId
    Write-Info "Use provided TemplateId=$TemplateId"
  }

  # Create order from template
  Write-Info "Create order from template"
  $createReq = @{ orderTemplateId = $createdTemplateId; name = "自動測試訂單"; orderType = "Promoted" } | ConvertTo-Json
  $createResp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders" -ContentType 'application/json' -Headers $headers -Body $createReq
  $orderNo = $createResp.orderNoBase62
  if(-not $orderNo){ throw "Create order failed (missing orderNo)" }
  Write-Info "OrderNo=$orderNo"

  # Get order by orderNo
  Write-Info "Get order detail"
  $order = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/orders/$orderNo" -Headers $headers
  Write-Host ($order | ConvertTo-Json -Depth 6)

  # Update status to completed (controller expects UpdateStatusRequest with newStatus/reason)
  Write-Info "Update order status -> completed"
  $oId = $order.oId
  if($oId){
    $statusReq = @{ newStatus = "completed"; reason = "Auto test" } | ConvertTo-Json
    Invoke-RestMethod -Method Patch -Uri "$BaseUrl/api/orders/$oId/status" -Headers $headers -ContentType 'application/json' -Body $statusReq | Out-Null
  } else {
    Write-Info "oId not present in response; skip status update"
  }

  Write-Info "Customer order flow OK"
} catch {
  Write-Err $_.Exception.Message
  exit 1
}
