@echo off
title Best Sarkari Result Native Lite APK Builder
cd /d "%~dp0"
echo ==============================================
echo BEST SARKARI RESULT - NATIVE LITE APK BUILD
echo ==============================================
call gradlew.bat clean assembleRelease
if errorlevel 1 goto failed
echo.
echo APK READY:
echo app\build\outputs\apk\release\app-release-unsigned.apk
echo.
echo Play/website release se pahle Android Studio se Signed APK banayein.
pause
exit /b 0
:failed
echo.
echo Build failed. Android Studio me project open karke Gradle Sync karein.
pause
exit /b 1
