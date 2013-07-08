@ECHO OFF
REM don't modify the caller's environment
setlocal

REM Locate dx.jar in the directory where dx.bat was found and start it.

REM Set up prog to be the path of this script, including following symlinks,
REM and set up progdir to be the fully-qualified pathname of its directory.
set prog=%~f0

REM Change current directory to where dx is, to avoid issues with directories
REM containing whitespaces.
cd /d %~dp0

call removeSignature.bat %1

jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore ATKKey.keystore -storepass ATKKEY %1 ATKKEY

call zipalign.exe 4 %1 %2


