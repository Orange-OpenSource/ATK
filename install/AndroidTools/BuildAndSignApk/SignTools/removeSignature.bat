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
for /f %%a in ('..\build-tools\aapt list %1 ^| findstr "META-INF"') do ..\build-tools\aapt remove %1 %%a