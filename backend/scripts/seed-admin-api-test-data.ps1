# Admin API test data import script
# Purpose: Import Admin API test data (test-data/admin-api-test-data.sql) to MySQL test database
# Requirements:
# - MySQL Client (mysql command available), or manual DB execution
# - Windows PowerShell environment

[CmdletBinding()]
param(
  [string]$SqlFilePath = "test-data/admin-api-test-data.sql",
  [string]$DbHost = $null,
  [int]$DbPort = 0,
  [string]$DbName = $null,
  [string]$DbUser = $null,
  [string]$DbPass = $null,
  [string]$AppYaml = "src/main/resources/application.yml",
  [switch]$FromAppConfig
)

function Write-Info($msg){ Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-Warn($msg){ Write-Host "[WARN] $msg" -ForegroundColor Yellow }
function Write-Err($msg){ Write-Host "[ERROR] $msg" -ForegroundColor Red }

Write-Info "SQL File: $SqlFilePath"
if(!(Test-Path $SqlFilePath)){
  Write-Err "Cannot find SQL file: $SqlFilePath"
  exit 1
}

# Auto-detect from application.yml if requested or params missing
if($FromAppConfig -or -not $DbHost -or -not $DbUser -or -not $DbName){
  if(Test-Path $AppYaml){
    Write-Info "Parsing DB config from $AppYaml"
    $yaml = Get-Content $AppYaml -Raw
    # crude parsing for spring.datasource.*
    $url = ($yaml | Select-String -Pattern "spring:\s*\n\s*datasource:\s*\n(.*\n)*?url:\s*(.+)" -AllMatches).Matches | ForEach-Object { $_.Groups[2].Value.Trim() } | Select-Object -First 1
    if(-not $url){ $url = ($yaml | Select-String -Pattern "datasource:\s*(.*\n)*?url:\s*(.+)" -AllMatches).Matches | ForEach-Object { $_.Groups[2].Value.Trim() } | Select-Object -First 1 }
    $user = ($yaml | Select-String -Pattern "username:\s*(.+)" -AllMatches).Matches | ForEach-Object { $_.Groups[1].Value.Trim() } | Select-Object -First 1
    $pass = ($yaml | Select-String -Pattern "password:\s*(.+)" -AllMatches).Matches | ForEach-Object { $_.Groups[1].Value.Trim() } | Select-Object -First 1
    if($url){
      # jdbc:mysql://host:port/db?params
      if($url -match "jdbc:mysql://([^:/]+)(?::(\d+))?/([^?\s]+)"){
        if(-not $DbHost){ $DbHost = $Matches[1] }
        if(-not $DbPort){ $DbPort = [int]($Matches[2] | ForEach-Object { if($_){$_} else {3306} }) }
        if(-not $DbName){ $DbName = $Matches[3] }
      }
    }
    if(-not $DbUser -and $user){ $DbUser = $user }
    if(-not $DbPass -and $pass){ $DbPass = $pass }
  } else {
    Write-Warn "application.yml not found at $AppYaml; falling back to defaults/parameters"
  }
}

if(-not $DbHost){ $DbHost = "127.0.0.1" }
if(-not $DbPort){ $DbPort = 3306 }
if(-not $DbName){ $DbName = "project" }
if(-not $DbUser){ $DbUser = "root" }
if(-not $DbPass){ $DbPass = "root" }

Write-Info "Target database: ${DbUser}@${DbHost}:${DbPort}/${DbName}"

$mysql = Get-Command mysql -ErrorAction SilentlyContinue
if(-not $mysql){
  Write-Warn "Cannot find mysql command. Please install MySQL Client or use other methods (e.g.: DBeaver/Workbench)"
  Write-Info "Manual import steps:"
  Write-Host "  1) Connect to DB using tool: ${DbHost}:${DbPort}, database ${DbName}"
  Write-Host "  2) Execute file: $SqlFilePath"
  exit 2
}

Write-Info "Starting test data import..."
$env:MYSQL_PWD = $DbPass
$args = @("-h", $DbHost, "-P", $DbPort, "-u", $DbUser, $DbName, "--default-character-set=utf8mb4")
Get-Content $SqlFilePath | & mysql @args
$exitCode = $LASTEXITCODE
if($exitCode -ne 0){
  Write-Err "Import failed (exit=$exitCode). Please check connection and permissions."
  exit $exitCode
}
Write-Info "Test data import completed."
