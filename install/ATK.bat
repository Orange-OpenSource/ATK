
@ECHO OFF

setlocal ENABLEDELAYEDEXPANSION
SET ATKParam=
FOR /R jar %%G IN (*.jar) DO set CLASSPATH=!CLASSPATH!;%%G
Echo The Classpath definition is %CLASSPATH%

IF "%1" == "-gui" GOTO GUI
:Loop

	IF "%1"=="" GOTO NOGUI

	SET ATKParam=%ATKParam% %1

	SHIFT

GOTO Loop

:NOGUI

REM if defined CLASSPATH (set CLASSPATH=%CLASSPATH%;.) else (set CLASSPATH=.)
java com.orange.atk.launcher.LaunchJATK %ATKParam%
GOTO END

:GUI

java com.orange.atk.launcher.LaunchGUIJATK

:END
Echo Thanks for using ATK !