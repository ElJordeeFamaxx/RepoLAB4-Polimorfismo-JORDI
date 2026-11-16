@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul
if not exist out mkdir out
javac -encoding UTF-8 -d out @fuentes.txt
if errorlevel 1 (
  pause
  exit /b 1
)
java -Dfile.encoding=UTF-8 -cp out acopiogt.Pruebas
pause
