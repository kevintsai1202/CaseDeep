#!/usr/bin/env pwsh
[CmdletBinding()]
param(
  [string]$BaseUrl = 'http://localhost:8080',
  [string]$ClientUser = 'qa_client',
  [string]$ClientPass = 'Abc123',
  [string]$ProviderUser = 'qa_provider',
  [string]$ProviderPass = 'Abc123'
)

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Err($m){ Write-Host "[ERROR] $m" -ForegroundColor Red }

try {
  # login provider and create a template (do NOT set payment methods)
  Write-Info "Login as provider"
  $provBody = @{ username=$ProviderUser; password=$ProviderPass } | ConvertTo-Json
  $provLogin = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/login" -ContentType 'application/json' -Body $provBody
  $provToken = $provLogin.token; if(-not $provToken){ throw "Provider login failed" }
  $provHeaders = @{ Authorization = "Bearer $provToken" }

  $templateName = "NullGuardTpl-" + [guid]::NewGuid().ToString('N').Substring(0,6)
  $tpl = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/ordertemplates/$templateName" -Headers $provHeaders -ErrorAction Stop
  $otId = $tpl.otId
  if(-not $otId){ throw "Create template failed" }
  Write-Info "Created template otId=$otId (paymentMethods intentionally left null)"

  # optionally set deliverytype to SelectedByTheCustomer to avoid businessDays dependency
  $delReq = @{ deliveryType = 'SelectedByTheCustomer'; businessDays = $null } | ConvertTo-Json -Depth 4
  try { Invoke-RestMethod -Method Patch -Uri "$BaseUrl/api/ordertemplates/$otId/deliverytype" -Headers $provHeaders -ContentType 'application/json' -Body $delReq -ErrorAction Stop | Out-Null; Write-Info 'deliverytype set (SelectedByTheCustomer)' } catch { Write-Err "deliverytype failed: $($_.Exception.Message)" }

  # login client
  Write-Info "Login as client"
  $cliBody = @{ username=$ClientUser; password=$ClientPass } | ConvertTo-Json
  $login = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/login" -ContentType 'application/json' -Body $cliBody
  $token = $login.token; if(-not $token){ throw "Client login failed" }
  $headers = @{ Authorization = "Bearer $token" }

  # create order from template (with null paymentMethods)
  Write-Info "Create order from template with null paymentMethods"
  $createReq = @{ orderTemplateId = $otId; name = "NullGuard-Order"; orderType = "Promoted" } | ConvertTo-Json
  $createResp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders" -ContentType 'application/json' -Headers $headers -Body $createReq -ErrorAction Stop
  $orderNo = $createResp.orderNoBase62
  if(-not $orderNo){ throw "Create order failed (missing orderNo)" }
  Write-Info "OrderNo=$orderNo"

  # get order detail
  $order = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/orders/$orderNo" -Headers $headers -ErrorAction Stop
  $pmBlock = ($order.confirmations | Where-Object { $_.type -eq 'payment' } | Select-Object -First 1)
  $delBlock = ($order.confirmations | Where-Object { $_.type -eq 'delivery' } | Select-Object -First 1)
  Write-Info ("PAYMENT listItems count = {0}" -f ($pmBlock.listItems | Measure-Object | Select-Object -ExpandProperty Count))
  Write-Info ("DELIVERY context = {0}" -f $delBlock.context)

  Write-Host ($order | ConvertTo-Json -Depth 6)
  Write-Info "Null-guard check OK"
} catch {
  Write-Err $_.Exception.Message
  exit 1
}

