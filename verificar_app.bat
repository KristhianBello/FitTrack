@echo off
echo =========================================
echo SCRIPT DE VERIFICACION FITTRACK
echo =========================================

echo.
echo Paso 1: Limpiando proyecto...
call gradlew clean

echo.
echo Paso 2: Compilando proyecto...
call gradlew assembleDebug

echo.
echo Paso 3: Ejecutando tests unitarios...
call gradlew testDebugUnitTest

echo.
echo Verificacion completada.
echo Si no hay errores, la aplicacion deberia funcionar correctamente.
echo.
pause
