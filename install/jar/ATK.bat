
@ECHO OFF

setlocal ENABLEDELAYEDEXPANSION
SET ATKParam=

:Loop

	IF "%1"=="" GOTO Continue

	SET ATKParam=%ATKParam% %1

	SHIFT

GOTO Loop

:Continue

REM if defined CLASSPATH (set CLASSPATH=%CLASSPATH%;.) else (set CLASSPATH=.)
FOR /R . %%G IN (*.jar) DO set CLASSPATH=!CLASSPATH!;%%G
Echo The Classpath definition is %CLASSPATH%


java com.orange.atk.launcher.LaunchJATK %ATKParam%