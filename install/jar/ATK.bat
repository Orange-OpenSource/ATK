
@ECHO OFF

SET ATKParam=

:Loop

	IF "%1"=="" GOTO Continue

	SET ATKParam=%ATKParam% %1

	SHIFT

GOTO Loop

:Continue



java -classpath C:\Progra~1\ATK\jar\ATK.jar;C:\Progra~1\ATK\jar\avalon-framework-cvs-20020806.jar;C:\Progra~1\ATK\jar\castor-1.1-xml.jar;C:\Progra~1\ATK\jar\ddmlib.jar;C:\Progra~1\ATK\jar\dom4j-1.6.1.jar;C:\Progra~1\ATK\jar\fop.jar;C:\Progra~1\ATK\jar\iText-2.0.8.jar;C:\Progra~1\ATK\jar\jai_core.jar;C:\Progra~1\ATK\jar\jakarta-regexp.jar;C:\Progra~1\ATK\jar\jcommon-1.0.12.jar;C:\Progra~1\ATK\jar\jfreechart-1.0.9.jar;C:\Progra~1\ATK\jar\log4j-1.2.15.jar;C:\Progra~1\ATK\jar\swtgraphics2d.jar com.orange.atk.launcher.LaunchJATK %ATKParam%