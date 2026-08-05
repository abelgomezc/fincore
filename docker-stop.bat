@echo off
setlocal

echo ========================================
echo   FinCore - Stop Docker Infrastructure
echo ========================================
echo.

echo Stopping FinCore containers...
docker-compose down

echo.
echo FinCore containers stopped.
echo.

pause
