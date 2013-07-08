@echo off
REM don't modify the caller's environment
setlocal

REM Locate dx.jar in the directory where dx.bat was found and start it.

REM Set up prog to be the path of this script, including following symlinks,
REM and set up progdir to be the fully-qualified pathname of its directory.
set prog=%~f0

REM Change current directory to where dx is, to avoid issues with directories
REM containing whitespaces.
cd /d %~dp0

REM create .apk unsigned and unaligned
call aapt package -v -f -M %1\AndroidManifest.xml -S %1\res -I lib\android.jar --rename-instrumentation-target-package %3 -F %1\bin\AtkTestRobotium.apk %1\bin

REM sign and align APk
call ..\Sign-tools\ATKSignAPK.bat %1\bin\AtkTestRobotium.apk %2
