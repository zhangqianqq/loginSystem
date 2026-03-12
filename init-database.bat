@echo off
echo ========================================
echo 员工登录系统 - 数据库初始化
echo ========================================
echo.

set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.4\bin
set SQL_FILE=k:\Cursor\loginSystem\database\igrations\V1__init_schema.sql

echo 请输入 MySQL root 密码：
echo.

"%MYSQL_PATH%\mysql.exe" -u root -p < "%SQL_FILE%"

echo.
echo ========================================
echo 数据库初始化完成！
echo ========================================
echo.
pause
