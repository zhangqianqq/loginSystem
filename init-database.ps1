# 员工登录系统 - 数据库初始化脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "员工登录系统 - 数据库初始化" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$MysqlPath = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
$SqlFile = "k:\Cursor\loginSystem\database\igrations\V1__init_schema.sql"

# 检查 MySQL 是否存在
if (-not (Test-Path $MysqlPath)) {
    Write-Host "错误: 未找到 MySQL，请检查路径: $MysqlPath" -ForegroundColor Red
    exit 1
}

# 检查 SQL 文件是否存在
if (-not (Test-Path $SqlFile)) {
    Write-Host "错误: 未找到 SQL 文件: $SqlFile" -ForegroundColor Red
    exit 1
}

Write-Host "请输入 MySQL root 密码：" -ForegroundColor Yellow
$password = Read-Host -AsSecureString
$bstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($password)
$password = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)
[System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)

Write-Host ""
Write-Host "正在执行 SQL 脚本..." -ForegroundColor Green

# 执行 SQL 脚本
$command = "`"$MysqlPath`" -u root -p$password < `"$SqlFile`""
Invoke-Expression "cmd /c $command 2>&1"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "数据库初始化完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "默认账户信息：" -ForegroundColor Yellow
Write-Host "  用户名: admin" -ForegroundColor White
Write-Host "  密码: admin123" -ForegroundColor White
Write-Host ""
