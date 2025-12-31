#!/usr/bin/env pwsh
[CmdletBinding()]
param(
  [string]$BaseUrl = 'http://localhost:8080',
  [string]$AdminUser = 'admin',
  [string]$AdminPass = 'admin123'
)

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Err($m){ Write-Host "[ERROR] $m" -ForegroundColor Red }

try{
  Write-Info "Auth: login"
  $body = @{ username=$AdminUser; password=$AdminPass } | ConvertTo-Json
  $login = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/login" -ContentType 'application/json' -Body $body
  $token = $login.token
  if(-not $token){ throw "Login response missing token" }
  Write-Info "Got token: $($token.Substring(0,8))..."

  $headers = @{ Authorization = "Bearer $token" }

  Write-Info "Auth: isExpired"
  $isExpired = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/auth/isexpired" -Headers $headers -ErrorAction Stop
  Write-Host ($isExpired | ConvertTo-Json -Depth 6)

  Write-Info "Auth flow OK"
} catch {
  Write-Err $_.Exception.Message
  exit 1
}
