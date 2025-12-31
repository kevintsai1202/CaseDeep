# 用途：以 Newman 執行 Admin API 自動化測試並產出報告
# 需求：
# - Node.js 與 npm（若無 newman 將嘗試安裝）
# - 服務已啟動並可由環境檔連線

[CmdletBinding()]
param(
  [string]$Collection = "postman-collections/P1-Admin-API-Tests.postman_collection.json",
  [string]$EnvFile = "postman-collections/environments/Admin-Local.postman_environment.json",
  [string]$ExtendedCollection = "postman-collections/P1-Admin-API-Tests-Extended.postman_collection.json",
  [string]$ReportDir = "postman-collections/reports",
  [switch]$SkipExtended,
  [switch]$Seed,
  [string]$AdminUser = "admin",
  [string]$AdminPass = "admin123",
  [string]$BaseUrlOverride
)

function Write-Info($msg){ Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-Warn($msg){ Write-Host "[WARN] $msg" -ForegroundColor Yellow }
function Write-Err($msg){ Write-Host "[ERROR] $msg" -ForegroundColor Red }

if(!(Test-Path $Collection)){ Write-Err "找不到集合檔：$Collection"; exit 1 }
if(!(Test-Path $EnvFile)){ Write-Err "找不到環境檔：$EnvFile"; exit 1 }
if(-not $SkipExtended -and !(Test-Path $ExtendedCollection)){
  Write-Warn "找不到延伸集合：$ExtendedCollection，將略過"
  $SkipExtended = $true
}

if(!(Test-Path $ReportDir)){ New-Item -ItemType Directory -Path $ReportDir | Out-Null }

$newman = Get-Command newman -ErrorAction SilentlyContinue
if(-not $newman){
  Write-Info "未偵測到 newman，嘗試使用 npm 安裝（需要網路）..."
  npm install -g newman | Out-Null
  $newman = Get-Command newman -ErrorAction SilentlyContinue
  if(-not $newman){ Write-Err "newman 安裝失敗，請手動安裝後重試"; exit 2 }
}

function Run-Newman($colPath, $name){
  $junit = Join-Path $ReportDir ("$name.junit.xml")
  $html  = Join-Path $ReportDir ("$name.html")
  Write-Info "執行集合：$colPath"
  newman run $colPath `
    -e $EnvFile `
    --reporters cli,junit,html `
    --reporter-junit-export $junit `
    --reporter-html-export  $html
  if($LASTEXITCODE -ne 0){ Write-Err "集合 $name 測試失敗 (exit=$LASTEXITCODE)"; exit $LASTEXITCODE }
}

# Optionally seed DB before running
if($Seed){
  Write-Info "Seeding admin API test data..."
  ./scripts/seed-admin-api-test-data.ps1 -FromAppConfig | Out-Null
}

# Pre-login to obtain admin token and update environment
if(Test-Path $EnvFile){
  try{
    $envJson = Get-Content $EnvFile -Raw | ConvertFrom-Json
    if($BaseUrlOverride){
      foreach($v in $envJson.values){ if($v.key -eq 'baseUrl'){ $v.value = $BaseUrlOverride } }
    }
    $baseUrl = ($envJson.values | Where-Object key -eq 'baseUrl' | Select-Object -First 1).value
    if(-not $baseUrl){ $baseUrl = 'http://localhost:8080' }
    Write-Info "Attempting admin login at $baseUrl"
    $loginBody = @{ username = $AdminUser; password = $AdminPass } | ConvertTo-Json
    $resp = Invoke-RestMethod -Method Post -Uri "$baseUrl/api/auth/login" -ContentType 'application/json' -Body $loginBody -ErrorAction Stop
    $token = $resp.token
    if($token){
      $updated = $false
      foreach($v in $envJson.values){ if($v.key -eq 'token'){ $v.value = $token; $updated=$true } }
      if(-not $updated){
        $envJson.values += [pscustomobject]@{ key='token'; value=$token; enabled=$true }
      }
      $envJson | ConvertTo-Json -Depth 10 | Set-Content -Encoding UTF8 $EnvFile
      Write-Info "Admin token updated to environment file."
    } else {
      Write-Warn "Login response did not contain token; please verify AuthController response schema."
    }
  } catch {
    Write-Warn "Admin login pre-step failed: $($_.Exception.Message). Continue with existing environment token."
  }
}

Run-Newman -colPath $Collection -name "admin-core"
if(-not $SkipExtended){ Run-Newman -colPath $ExtendedCollection -name "admin-extended" }

Write-Info "全部測試完成。報告輸出於：$ReportDir"
