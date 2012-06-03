/*
 * Software Name : ATK
 *
 * Copyright (C) 2007 - 2012 France Télécom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------
 * File Name   : ActionToExecute.java
 *
 * Created     : 27/11/2008
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.interpreter.atkCore;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.interpreter.ast.ASTFUNCTION;
import com.orange.atk.interpreter.ast.ASTINCLUDE;
import com.orange.atk.interpreter.ast.ASTStart;
import com.orange.atk.interpreter.ast.SimpleNode;
import com.orange.atk.interpreter.parser.ATKScriptParser;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;
import com.orange.atk.interpreter.parser.ParseException;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.PhoneRecorder;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.Position;


/**
 * This class is used to separate actions on the phone and the visit specific
 * elements of the language (definition of variables, string, ... ).
 */


public class ActionToExecute {
	private static final String ERR_MSG_FIRST_PARAMETER_IS_NOT_CORRET = "First Parameter is not corret possible value";
	// type of saved pictures
	// private static final String EXT_PICTURE = "png";
	// ERROR & INFO MESSAGES
	private static final String ERRMSG_SLEEP_VALUE_IS_NEGATIVE = "Sleep value is negative";
	private static final String ERRMSG_PARAMETER_OF_SET_ORIENTATION_IS_NOT_CORRECT = "Parameter of SetOrientation is not correct, should be equals to : ";
	private static final String ERRMSG_INTERNAL_ERROR_LOG_THREAD_IS_NOT_ALIVE = "Internal error : log thread is not alive";
	private static final String ERRMSG_STOP_MAIN_LOG_HAS_BEEN_CALLED_WITHOUT_CALLING_START_MAIN_LOG = "StopMainLog has been called without calling StartMainLog";
	private static final String ERRMSG_START_MAIN_LOG_HAS_BEEN_CALLED_TWICE = "StartMainLog has been called twice";
	private static final String INFMSG_TAKE_A_SCREENSHOT_AND_SAVE_IT_UNDER = "Take a screenshot and save it under ";
	private static final String ERRMSG_NOT_ABLE_TO_SAVE_THE_SS = "Not able to save the screenshot...";
	private static final String ERRMSG_NOT_BE_ABLE_TO_READ_THE_PICTURE = "Not be able to read the picture...";
	// private static final String ERRMSG_COULD_NOT_OPENED = "could not opened";
	private static final String ERRMSG_INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";
	private static final String FIRST = "First";
	private static final String SECOND = "Second";
	private static final String THIRD = "Third";
	private static final String FOURTH = "Fourth";
	private static final String FIFTH = "Fifth";
	private static final String SIXTH = "Sixth";
	private static final String PARAMETER_IS_NOT = " parameter is not ";
	private static final String AN_INTEGER = "an Integer";
	private static final String A_STRING = "a String";

	// Special values
	private static final String LOOP = "loop";
	private static final String FINAL_STATE = "final_state";
	private static final String INITIAL_STATE = "initial_state";

	// Represents the current log system
	private ResultLogger mainLogger;
	// Stack used to store temporary informations
	// private OCTKInterpreterStack stack;
	// represents the internal state of the interpreter
	private JATKInterpreterInternalState internalState;
	private JATKInterpreter interpreter;

	/**
	 * Constructor
	 * 
	 * @param interpreter
	 *            interpreter
	 */
	public ActionToExecute(JATKInterpreter interpreter) {
		this.mainLogger = interpreter.getMainLogger();
		this.internalState = interpreter.getInternalState();
		this.interpreter = interpreter;
	}

	private PhoneInterface getPhoneInterface() {
		return interpreter.getPhoneInterface();
	}

	private JATKInterpreterInternalState getInternalState() {
		return internalState;
	}

	/****************************** LOGGER methods **********************************/
	private ResultLogger getLogger() {
		return mainLogger;
	}

	private String PaddFile(String inputvalue, int pad)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(inputvalue);

		for(int i=inputvalue.length();i<pad;i++)
		{
			buf.append(" ");
		}
		inputvalue  = buf.toString();
		return inputvalue;
	}


	private boolean checkNumberArguments(int expected, int current,
			int lineNumber) {
		if (expected != current) {
			getLogger().addErrorToDocumentLogger(ERRMSG_INVALID_NUMBER_OF_ARGUMENTS,
					lineNumber, getInternalState().getCurrentScript());
			return false;
		} else {
			return true;
		}
	}

	private void generateError(SimpleNode node, String errorMsg) {
		getLogger().addErrorToDocumentLogger(
				"Syntax error - " + errorMsg + " - " + node.getValue(),
				node.getLineNumber(), getInternalState().getCurrentScript());
	}

	private void generateError(SimpleNode node, Exception e) {
		getLogger().addErrorToDocumentLogger(
				"Phone Handling error -" + e.getMessage() + " - " + node.getValue(),
				node.getLineNumber(), getInternalState().getCurrentScript());
	}


	private void generateWarning(SimpleNode node, String warnMsg) {
		getLogger().addWarningToDocumentLogger(
				"Syntax warning - " + warnMsg + " - " + node.getValue(),
				node.getLineNumber(), getInternalState().getCurrentScript());
	}

	private void generateWarning(SimpleNode node, Exception e) {
		getLogger().addWarningToDocumentLogger(
				"Phone Handling error - " + e.getMessage() + " - " + node.getValue(),
				node.getLineNumber(), getInternalState().getCurrentScript());
	}

	private void generateInfo(String infoMsg) {
		getLogger().addInfoToDocumentLogger(infoMsg, -1, "");

	}

	/********************************** SCRIPT FUNCTIONS ********************************/
	/**
	 * Call when ASTFUNCTION.getName() is equals to Beep. It's called the
	 * {@link PhoneInterface}.beep function
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "beep")
	 * @param args
	 *            array of parameter given to the beep function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionBeep(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(0, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}

		Date startTime = new Date();
		try {
			getPhoneInterface().beep();
		} catch (PhoneException e) {

			generateWarning(node, e);
			mainLogger.addInfotoActionLogger("Error JATK","action Beep", new Date(),new Date());
		}
		mainLogger.addInfotoActionLogger("Action",PaddFile("beep",40), startTime, new Date());


		return Boolean.TRUE;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to DisableUSBCharge. It's called the
	 * {@link PhoneInterface}.enableUSBcharge function with good parameters.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "DisableUSBCharge")
	 * @param args
	 *            array of parameter given to the DisableUSBCharge function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionDisableUSBCharge(ASTFUNCTION node, Variable[] args)
	{

		if(!checkNumberArguments(0, args.length, node.getLineNumber()))
		{
			return Boolean.FALSE;	
		}


		try {
			getPhoneInterface().disableUSBcharge();
		} catch (PhoneException e) {
			mainLogger.addInfotoActionLogger("Error JATK","DiseableUSB", new Date(),new Date());
			e.printStackTrace();
		}


		return true;
	}



	/**
	 * Call when ASTFUNCTION.getName() is equals to include. It loads, parses
	 * and interprets the include file. The parse AST is also include in 
	 * place of this node.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "Include")
	 * @param args
	 *            array of parameter given to the Include function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         file not found, unable to open file, ...)
	 */
	public Boolean actionInclude(ASTINCLUDE node, Variable[] args) {
		
		String include = args[0].getString();
		String scriptPath = interpreter.getInternalState().getCurrentScript();

		try {
			ActionToExecute.fetchIncludeScript(interpreter, node, scriptPath, include);
		} catch (FileNotFoundException e) {
			generateError(
					node,
					"File "
					+ include
					+ " could not be include. Possible reasons are : "
					+ Platform.LINE_SEP
					+ " - The named file does not exist"
					+ Platform.LINE_SEP
					+ " - The named file is a directory rather than a regular file"
					+ Platform.LINE_SEP
					+ " - You do not have enough rights to read the file");
			mainLogger.addInfotoActionLogger("Error JATK","action Include File not found", new Date(),new Date());
			return Boolean.FALSE;
		} catch (ParseException e) {
			generateError(node, "File " + include + " is not valid");
			mainLogger.addInfotoActionLogger("Error JATK","action include File not valid", new Date(),new Date());
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	
	public static void fetchIncludeScript(ATKScriptParserVisitor interpreter, ASTINCLUDE node, String scriptPath, String include) throws FileNotFoundException, ParseException {
		String includefile=null;

		//Test a relative path
		if(new File(new File(scriptPath).getParent()+Platform.FILE_SEPARATOR+include).exists())
		{
			includefile=new File(scriptPath).getParent()+ Platform.FILE_SEPARATOR + include;
		}
		else if(new File(include).exists())//Absolute path
		{
			includefile=include;
		}
		else{
			//ca ne peut jamais marcher????
			if (AutomaticPhoneDetection.getInstance().getDevice() instanceof PhoneRecorder)
				((PhoneRecorder)AutomaticPhoneDetection.getInstance().getDevice()).include(include);

			String Includedir = "include";
			PhoneInterface ph = AutomaticPhoneDetection.getInstance().getDevice();

			if(ph.getType() == PhoneInterface.TYPE_SE )
				Includedir +="SE";

			if( ph.getType() == PhoneInterface.TYPE_ANDROID )
				Includedir +="Android";

			if(ph.getType() == PhoneInterface.TYPE_WTK )
				Includedir +="WTK";

			// TODO : verify 
			//include = include.replace('\\', '/');
			// put the include file name in lower case
			// for windows compatibility
			include = include.toLowerCase();
			includefile=Includedir+Platform.FILE_SEPARATOR + include;
		}

		//check id file is present in local

		Logger.getLogger(ActionToExecute.class).info(includefile);
		ATKScriptParser parser = new ATKScriptParser(new java.io.FileReader(includefile));
		ASTStart includeast = parser.start();
		int nbChilds = includeast.jjtGetNumChildren();
		if (nbChilds == 0) 
			Logger.getLogger(ActionToExecute.class).warn("Warning : File " + include + " is empty");

		for (int i=0; i<nbChilds; i++) {
			//includeast.jjtGetChild(i).jjtAccept(interpreter, null);	
			node.jjtAddChild(includeast.jjtGetChild(i), i+1);
		}
		//continue in body of the include
		for (int i=1; i<node.jjtGetNumChildren() ; i++)
			 node.jjtGetChild(i).jjtAccept(interpreter, null);
	
	}
	
	/**
	 * Call when ASTFUNCTION.getName() is equals to FillStorage. Fill the phone RMS
	 * It calls the {@link PhoneInterface}.fillStorage function with good parameters.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "FillStorage")
	 * @param args
	 *            array of parameter given to the FillStorage function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionFillStorage(ASTFUNCTION node, Variable[] args) {
		int nbArgs = args.length;
		if (nbArgs == 0) {
			// if no argument is provided, must free all the memory
			try { 
				getPhoneInterface().fillStorage(-1);
			} catch (PhoneException e) {
				mainLogger.addInfotoActionLogger("Error JATK","action Fill Storage", new Date(),new Date());

				generateWarning(node, e);
			}
			return Boolean.TRUE;
		} else if (nbArgs == 1) {
			if (!args[0].isInteger()) {
				generateError(node, FIRST + PARAMETER_IS_NOT + AN_INTEGER);
				return Boolean.FALSE;
			}
			int memorySizeFreeAfterTheCall = args[0].getInteger();

			Date startTime = new Date();			
			try {
				ArrayList<String> l =new ArrayList<String>();
				l.add("Storage");
				long m = getPhoneInterface().getResources(l).get("Storage").longValue();
				long memorySizeToFill = m - memorySizeFreeAfterTheCall;
				if (0 > memorySizeToFill) {
					memorySizeToFill = 0;
				}
				getPhoneInterface().fillStorage(memorySizeToFill);
			} catch (PhoneException e) {
				mainLogger.addInfotoActionLogger("Error JATK","action Fill Storage", new Date(),new Date());
				generateWarning(node, e);
			}
			mainLogger.addInfotoActionLogger("Action",PaddFile("fillStorage",40), startTime, new Date());


			return Boolean.TRUE;
		} else {
			generateError(node, ERRMSG_INVALID_NUMBER_OF_ARGUMENTS);
			return Boolean.FALSE;
		}
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to FillStorage. Fill the phone RMS
	 * It calls the {@link PhoneInterface}.fillStorage function with good parameters.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "FillStorage")
	 * @param args
	 *            array of parameter given to the FillStorage function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionFreeStorage(ASTFUNCTION node, Variable[] args) {
		int nbArgs = args.length;
		if (nbArgs == 0) {
			// if no argument is provided, must free all the memory
			try { 
				getPhoneInterface().freeStorage();
			} catch (PhoneException e) {
				mainLogger.addInfotoActionLogger("Error JATK","action Fill Storage", new Date(),new Date());

				generateWarning(node, e);
			}
			return Boolean.TRUE;
		} else {
			generateError(node, ERRMSG_INVALID_NUMBER_OF_ARGUMENTS);
			return Boolean.FALSE;
		}
	}


	/**
	 * Call when ASTFUNCTION.getName() is equals to Key. It's called the
	 * {@link PhoneInterface}.keyPress function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "Key")
	 * @param args
	 *            array of parameter given to the Key function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionKey(ASTFUNCTION node, Variable[] args) {
		if (args.length==1)
		{
			String AT_Cmd ;
			try {
				AT_Cmd= args[0].getString();
			}catch(ClassCastException e) {
				try {
					AT_Cmd = args[0].getInteger().toString();
				}catch(ClassCastException fe) {
					generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
					return Boolean.FALSE;
				}
			}

			Date startTime = new Date();
			try {
				getPhoneInterface().keyPress(AT_Cmd, 0, 0);

			} catch (Exception e) {
				generateWarning(node, e);
				mainLogger.addInfotoActionLogger("Error JATK","action press key fail"+AT_Cmd, new Date(),new Date());
			}
			mainLogger.addInfotoActionLogger("keyPress",PaddFile("keyPress \'"+AT_Cmd+"\'",40), startTime, new Date());

			return Boolean.TRUE;
		}
		else if (args.length==2)
		{
			if (!args[1].isInteger()) {
				generateError(node, SECOND + PARAMETER_IS_NOT + AN_INTEGER);
				return Boolean.FALSE;
			}
			Integer TimePress = args[1].getInteger();
			String AT_Cmd = args[0].getString();
			Date startTime = new Date();
			try {
				getPhoneInterface().keyPress(AT_Cmd, TimePress, 0);

			} catch (Exception e) {
				generateWarning(node, e);
				mainLogger.addInfotoActionLogger("Error JATK","action press key fail"+AT_Cmd, new Date(),new Date());
			}
			mainLogger.addInfotoActionLogger("keyPress",PaddFile("keyPress \'"+AT_Cmd+"\'",40), startTime, new Date());

			return Boolean.TRUE;
		}

		else if (args.length==3)
		{
			if (!args[1].isInteger()) {
				generateError(node, SECOND + PARAMETER_IS_NOT + AN_INTEGER);
				return Boolean.FALSE;
			}
			Integer TimePress = args[1].getInteger();

			if (!args[2].isInteger()) {
				generateError(node, THIRD + PARAMETER_IS_NOT + AN_INTEGER);
				return Boolean.FALSE;
			}
			Integer delay	 = args[2].getInteger();
			String AT_Cmd = args[0].getString();
			Date startTime = new Date();
			try {
				getPhoneInterface().keyPress(AT_Cmd, TimePress, delay);

			} catch (Exception e) {
				generateWarning(node, e);
				mainLogger.addInfotoActionLogger("Error JATK","action press key fail"+AT_Cmd, new Date(),new Date());
			}
			mainLogger.addInfotoActionLogger("keyPress",PaddFile("keyPress \'"+AT_Cmd+"\'",40), startTime, new Date());

			return Boolean.TRUE;
		}
		if (!(checkNumberArguments(1, args.length, node.getLineNumber())||checkNumberArguments(2, args.length, node.getLineNumber())||checkNumberArguments(3, args.length, node.getLineNumber()))) {
			return Boolean.FALSE;
		}
		return Boolean.FALSE;

	}

	/**
	 *Get Standart output or Error and log it if Any 
	/**
	 * Call when ASTFUNCTION.getName() is equals to KeyDown. It's called the
	 * {@link PhoneInterface}.keyDown function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "KeyDown")
	 * @param args
	 *            array of parameter given to the KeyDown function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionKeyDown(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}

		if (args[0].isString()) {
			String AT_Cmd  = args[0].getString();

			Date startTime = new Date();
			try {
				getPhoneInterface().keyDown(AT_Cmd);
			} catch (Exception e) {
				generateWarning(node, e);
			}
			mainLogger.addInfotoActionLogger("Action",PaddFile("keyDown",40), startTime, new Date());


			return Boolean.TRUE;
		} else {
			generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to KeyUp. It's called the
	 * {@link PhoneInterface}.keyUp function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "KeyUp")
	 * @param args
	 *            array of parameter given to the KeyUp function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionKeyUp(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}

		if (args[0].isString()) {
			String AT_Cmd  = args[0].getString();

			Date startTime = new Date();
			try {
				getPhoneInterface().keyUp(AT_Cmd); 
			} catch (Exception e) {
				generateWarning(node, e);
			}
			mainLogger.addInfotoActionLogger("Action",PaddFile("keyUp",40), startTime, new Date());


			return Boolean.TRUE;
		} else {
			generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to KillWindow. It kills an application on the
	 * phone. It calls the {@link PhoneInterface}.killProcess function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "KillWindow")
	 * @param args
	 *            array of parameter given to the KillWindow function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionKill(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (!args[0].isString()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}
		String process = args[0].getString();
		Date startTime = new Date();
		try {
			getPhoneInterface().killMidlet(process);			
		} catch (PhoneException e) {
			mainLogger.addInfotoActionLogger("Error JATK","action kill windows", new Date(),new Date());
			generateError(node, e);
		}
		mainLogger.addInfotoActionLogger("Action",PaddFile("killProcess \'"+process+"\'",40), startTime, new Date());


		return Boolean.TRUE;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to Log. It saves the log information in 
	 * log system (It writes a log trace in the action.xml file). 
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "Log")
	 * @param args
	 *            array of parameter given to the Log function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 * @throws PhoneException 
	 */
	public Boolean actionLog(ASTFUNCTION node, Variable[] args) throws PhoneException {
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (getPhoneInterface() instanceof PhoneRecorder){
			((PhoneRecorder)getPhoneInterface()).log(args[0].getString());
		}
		if (args[0].isString()) {
			// log(comment)
			String comment = args[0].getString();
			if (INITIAL_STATE.equals(comment.toLowerCase())
					|| FINAL_STATE.equals(comment.toLowerCase())) {
				getLogger().addResourcesInfoToDocumentLogger();


			} else if (LOOP.equals(comment)) {
				if (getInternalState().getLoopValue() >= 0) {
					generateInfo("loop " + getInternalState().getLoopValue());
					// number of loop
					// TODO : else ERROR!!! or not -> integer value <0 accepted
				}
			} else {
				generateInfo(comment);
				mainLogger.addInfotoActionLogger("log",PaddFile("log \'"+comment+"\'",40), new Date(), new Date());

			}
		} else if (args[0].isInteger()) {
			Integer value = args[0].getInteger();
			generateInfo(value.toString());
			mainLogger.addInfotoActionLogger("log",PaddFile("log :"+value.toString(),40), new Date(), new Date());

		}
		return Boolean.TRUE;
	}


	/**
	 * Call when ASTFUNCTION.getName() is equals to Run. It launches an application on the phone.
	 * It calls the {@link PhoneInterface}.run function
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "Run")
	 * @param args
	 *            array of parameter given to the Run function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionRun(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (!args[0].isString()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}
		String path = args[0].getString();
		Date startTime = new Date();
		try {
			getPhoneInterface().runMidlet(path);
		} catch (PhoneException e) {
			generateWarning(node, e);
		}




		mainLogger.addInfotoActionLogger("Action",PaddFile("run \'"+path+"\'",40), startTime, new Date());

		return Boolean.TRUE;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to ScreenShoot. It calls the
	 * {@link PhoneInterface}.screenShot function and saves the picture under
	 * the log directory (log directory must already be created).
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "ScreenShot")
	 * @param args
	 *            array of parameter given to the ScreenShot function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionScreenshot(ASTFUNCTION node, Variable[] args) {
		SimpleDateFormat formatter = null;
		// Windows/Linux
		if (Platform.OS_NAME.toLowerCase().contains("windows")) {
			formatter = new SimpleDateFormat("HmmssSSS");
		} else {
			formatter = new SimpleDateFormat("H:mm:ssSSS");
		}
		String dateString = formatter.format(new Date());
		String output = null;
		String comment="";
		// String currentScript = internalState.getCurrentScript();
		// String scriptName =
		// currentScript.substring(currentScript.lastIndexOf(Platform.FILE_SEPARATOR
		// ));
		// String dir = getInternalState().getLogDir();//
		// log/"+scriptName+Platform.FILE_SEPARATOR ;
		// get the number of arguments
		int nbArgs = args.length;
		if (nbArgs == 0) {
			// output = dir + dateString + "." + EXT_PICTURE;
			output = dateString;
		} else if (nbArgs == 1) {
			if (!args[0].isString()) {
				generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
				return Boolean.FALSE;
			}
			comment = args[0].getString();
			// output = dir + comment + "-" + dateString + "." + EXT_PICTURE;
			output = comment + "-" + dateString;
		} else {
			generateError(node, ERRMSG_INVALID_NUMBER_OF_ARGUMENTS);
			return Boolean.FALSE;
		}
		// File fichier = new File(output);
		// if (fichier == null) {
		// generateError(node, output + ERRMSG_COULD_NOT_OPENED);
		// return Boolean.FALSE;
		// }

		if (getPhoneInterface() instanceof PhoneRecorder){
			try {
				((PhoneRecorder)getPhoneInterface()).screenShot(comment);
				return Boolean.TRUE;
			} catch (PhoneException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}else{


			Date startTime = new Date();
			BufferedImage read = null;
			try{
				read = getPhoneInterface().screenShot();
				int rotation = Integer.parseInt(Configuration.getProperty(Configuration.SCROTATION,"0"));
				if (rotation != 0) {
					read = rotate(read, rotation);
				}
				try {
					mainLogger.addInfotoActionLogger("ScreenShot",PaddFile("ScreenShot \'"+comment + "-" + dateString+"\'",40), startTime, new Date());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mainLogger.addInfotoActionLogger("Error JATK","action ScreenShot", new Date(),new Date());
					generateError(node, e);
				}

			} catch (PhoneException e) {
				e.printStackTrace();
				mainLogger.addInfotoActionLogger("Error JATK","action ScreenShot", new Date(),new Date());
				generateError(node, e);
			}


			if (read == null) {
				generateWarning(node, ERRMSG_NOT_BE_ABLE_TO_READ_THE_PICTURE);
				return Boolean.TRUE;
			}
			if (!getLogger().saveScreenshot(output, read)) {
				generateWarning(node, ERRMSG_NOT_ABLE_TO_SAVE_THE_SS);
				return Boolean.FALSE;
			}
			generateInfo(INFMSG_TAKE_A_SCREENSHOT_AND_SAVE_IT_UNDER + output);

			return Boolean.TRUE;
		}
	}

	private BufferedImage rotate(BufferedImage img, int angle) { 
        int w = img.getWidth();   
        int h = img.getHeight();   
        BufferedImage dimg;
        double sin = Math.abs(Math.sin(Math.toRadians(angle))), cos = Math.abs(Math.cos(Math.toRadians(angle))); 
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int)Math.floor(h*cos+w*sin); 
        dimg = new BufferedImage(neww, newh, BufferedImage.TYPE_INT_RGB);   
        Graphics2D g = dimg.createGraphics();   
        g.translate((neww-w)/2, (newh-h)/2); 
        g.rotate(Math.toRadians(angle), w/2, h/2); 
        g.drawImage(img, null, 0, 0);
        return dimg;   
    }  

	/**
	 * Call when ASTFUNCTION.getName() is equals to SendEmail. It sends an email.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="setFlightMode")
	 * @param args
	 *            array of parameter given to the setFlightMode function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionSendEmail(ASTFUNCTION node, Variable[] args)
	{
		if (!checkNumberArguments(6, args.length,node.getLineNumber())) {
			return Boolean.FALSE;
		}

		if (!args[0].isString()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

		if (!args[1].isString()) {
			generateError(node, SECOND + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

		if (!args[2].isString()) {
			generateError(node, THIRD + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

		if (!args[3].isString()) {
			generateError(node, FOURTH + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

		if (!args[4].isString()) {
			generateError(node, FIFTH + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

		if (!args[5].isString()) {
			generateError(node, SIXTH + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}


		String Subject =args[0].getString();
		String Msg =args[1].getString();
		String EmailDest =args[2].getString();
		String NameDest =args[3].getString();
		String NameSrc =args[4].getString();
		String EmailSrc =args[5].getString();

		if((!EmailDest.contains("@")||!EmailDest.contains(".")))
			generateError(node, "Email Dest "+EmailDest+"is not a valid Email");


		if((!EmailSrc.contains("@")||!EmailSrc.contains(".")))
			generateError(node, "Email Dest "+EmailSrc+"is not a valid Email");



		try {
			getPhoneInterface().sendEmail( Subject, Msg, EmailDest, NameDest, NameSrc, EmailSrc);
			mainLogger.addInfotoActionLogger("Action",PaddFile("send Email",40), new Date(), new Date());

		} catch (PhoneException e) {
			mainLogger.addInfotoActionLogger("Error JATK","action SendEmail", new Date(),new Date());
			generateWarning(node, e);
		}

		return true;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to SendSMS. It calls the
	 * {@link PhoneInterface}.sendSMS function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="setFlightMode")
	 * @param args
	 *            array of parameter given to the setFlightMode function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionSendSMS(ASTFUNCTION node, Variable[] args)
	{
		if (!checkNumberArguments(2, args.length,node.getLineNumber())) {
			return Boolean.FALSE;
		}

		if (!args[0].isString()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

		String PhoneNumber =args[0].getString();
		/* if (PhoneNumber.length()!=9)
        {
    		generateError(node, FIRST + PARAMETER_IS_NOT + " a valid phone number");
			return Boolean.FALSE;
        }
		 */
		if (!args[1].isString()) {
			generateError(node, SECOND + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}
		String MsG = args[1].getString();



		try {
			getPhoneInterface().sendSMS(PhoneNumber,MsG);
			mainLogger.addInfotoActionLogger("Action",PaddFile("send SMS",40), new Date(), new Date());

		} catch (PhoneException e) {
			mainLogger.addInfotoActionLogger("Error JATK","action SendSMS", new Date(),new Date());
			generateWarning(node, e);
		}

		return true;
	}



	/**
	 * Call when ASTFUNCTION.getName() is equals to setFlightMode. It calls the
	 * {@link PhoneInterface}.setFlightMode function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="setFlightMode")
	 * @param args
	 *            array of parameter given to the setFlightMode function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionSetFlightMode(ASTFUNCTION node, Variable[] args)
	{
		if (!checkNumberArguments(1, args.length,node.getLineNumber())) {
			return Boolean.FALSE;
		}

		if (!args[0].isString()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
			return Boolean.FALSE;
		}

		String fligtMode = args[0].getString();
		if (!((fligtMode.toLowerCase().equals("on"))
				|| (fligtMode.toLowerCase().equals("off")))) {
			generateError(node, ERR_MSG_FIRST_PARAMETER_IS_NOT_CORRET+   "0 , 1 ");
			return Boolean.FALSE;	
		}

		try {
			if (fligtMode.toLowerCase().equals("on")) getPhoneInterface().setFlightMode(true);
			else getPhoneInterface().setFlightMode(false);
			mainLogger.addInfotoActionLogger("Action",PaddFile("SetFlightMode",40), new Date(), new Date());

		} catch (PhoneException e) {
			mainLogger.addInfotoActionLogger("Error JATK","action SetFlightMode", new Date(),new Date());
			generateWarning(node, e);
		}

		return true;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to SetOrientation. It calls the
	 * {@link PhoneInterface}.setOrientation function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="setOrientation")
	 * @param args
	 *            array of parameter given to the setOrientation function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionSetOrientation(ASTFUNCTION node, Variable[] args) {
		// 0=DMDO_DEFAULT
		// 1=DMDO_90
		// 2=DMDO_180
		// 3=DMDO_270
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (!args[0].isInteger()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + AN_INTEGER);
			return Boolean.FALSE;
		}
		int direction = args[0].getInteger();
		if (!((direction == PhoneInterface.DMDO_DEFAULT)
				|| (direction == PhoneInterface.DMDO_90)
				|| (direction == PhoneInterface.DMDO_180) || (direction == PhoneInterface.DMDO_270))) {
			generateError(node,
					ERRMSG_PARAMETER_OF_SET_ORIENTATION_IS_NOT_CORRECT
					+ PhoneInterface.DMDO_DEFAULT + ","
					+ PhoneInterface.DMDO_90 + ","
					+ PhoneInterface.DMDO_180 + ","
					+ PhoneInterface.DMDO_270);
			return Boolean.FALSE;
		}
		Date startTime = new Date();

		try {
			getPhoneInterface().setOrientation(direction);
		} catch (PhoneException e) {
			generateWarning(node, e);
		}

		mainLogger.addInfotoActionLogger("Action",PaddFile("setOrientation",40), startTime, new Date());

		return Boolean.TRUE;
	}


	/**
	 * Call when ASTFUNCTION.getName() is equals to SetReset. It resets the phone to homescreen/initial state.
	 * It calls the {@link PhoneInterface}.reset function.
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "SetReset")
	 * @param args
	 *            array of parameter given to the SetReset function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionReset(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(0, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		Date startTime = new Date();
		try {
			getPhoneInterface().reset();
		} catch (PhoneException e) {
			generateError(node, e);
		}
		mainLogger.addInfotoActionLogger("Action",PaddFile("Reset",40), startTime, new Date());



		return Boolean.TRUE;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to Sign. It checks that the
	 * application signature is correct.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "Sign")
	 * @param args
	 *            array of parameter given to the Sign function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionSign(ASTFUNCTION node, Variable[] args) {
		// TODO : check signature....
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (!args[0].isInteger()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + AN_INTEGER);
			return Boolean.FALSE;
		}
		args[0].getInteger();
		return Boolean.TRUE;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to Sleep. At this moment, the
	 * execution is paused during the requested time.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="sleep")
	 * @param args
	 *            array of parameter given to the sleep function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionSleep(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (!args[0].isInteger()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + AN_INTEGER);
			return Boolean.FALSE;
		}
		int time = args[0].getInteger();
		// Logger.getLogger(this.getClass() ).debug("Sleep during " + time + " ms...");
		if (time < 0) {
			generateError(node, ERRMSG_SLEEP_VALUE_IS_NEGATIVE);
			return Boolean.FALSE;
		}
		if(getPhoneInterface() instanceof PhoneRecorder ){
			((PhoneRecorder)getPhoneInterface()).sleep(time);
		}else{
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to StartMainLog. It informs the
	 * log system to periodically save statistics values from the phone
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="StartMainLog")
	 * @param args
	 *            array of parameter given to the StartMainLog function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         StartMainLog already called...)
	 * @see ActionToExecute#actionStopmainlog
	 * 
	 */
	public Boolean actionStartMainLog(ASTFUNCTION node, Variable[] args) {
		/*
		 * Start to monitor the status of the device (in order to compute
		 * average) x is not mandatory, set the polling interval (in ms) for
		 * main log. (min and default : 500 ms)
		 */
		int defaultTime = 0;
		// get number of arguments
		int nbArgs = args.length;
		if (nbArgs == 0) {
			defaultTime = 500;
		} else if (nbArgs == 1) {
			if (!args[0].isInteger()) {
				generateError(node, FIRST + PARAMETER_IS_NOT + AN_INTEGER);
				return Boolean.FALSE;
			}
			defaultTime = args[0].getInteger();
		} else {
			generateError(node, ERRMSG_INVALID_NUMBER_OF_ARGUMENTS);
			return Boolean.FALSE;
		}
		if (getInternalState().isStartMainLogCalled() || getLogger().isAlive()) {
			generateError(node, ERRMSG_START_MAIN_LOG_HAS_BEEN_CALLED_TWICE);
			return Boolean.FALSE;
		}
		Logger.getLogger(this.getClass() ).debug("Start Main Log : Default time = " + defaultTime);
		if (getPhoneInterface() instanceof PhoneRecorder){
			((PhoneRecorder)getPhoneInterface()).startMainLog(defaultTime);
		}else{
			getLogger().start(defaultTime);
			getInternalState().setStartMainLogCalled(true);
		}


		return Boolean.TRUE;
	}


	/**
	 * Call when ASTFUNCTION.getName() is equals to StopMainLog. It informs the
	 * log system to stop the save process.
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="StopMainLog")
	 * @param args
	 *            array of parameter given to the StopMainLog function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         StartMainLog not called...)
	 * @see ActionToExecute#actionStartmainlog
	 */
	public Boolean actionStopMainLog(ASTFUNCTION node, Variable[] args) {

		if (!checkNumberArguments(0, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (getPhoneInterface() instanceof PhoneRecorder){
			((PhoneRecorder)getPhoneInterface()).stopMainLog();
			return Boolean.TRUE;
		}else{
			if (getInternalState().isStartMainLogCalled() && getLogger().isAlive()) {
				getLogger().interrupt();
				getLogger().join();
				return Boolean.TRUE;
			} else {
				if (getInternalState().isStartMainLogCalled()) {
					generateError(node,
							ERRMSG_INTERNAL_ERROR_LOG_THREAD_IS_NOT_ALIVE);
					Logger.getLogger(this.getClass()).
					warn(ERRMSG_INTERNAL_ERROR_LOG_THREAD_IS_NOT_ALIVE);
				} else {
					generateError(node,
							ERRMSG_STOP_MAIN_LOG_HAS_BEEN_CALLED_WITHOUT_CALLING_START_MAIN_LOG);
					Logger.getLogger(this.getClass()).
					warn(ERRMSG_STOP_MAIN_LOG_HAS_BEEN_CALLED_WITHOUT_CALLING_START_MAIN_LOG);
				}
				return Boolean.FALSE;
			}
		}
	}



	/**
	 * Call when ASTFUNCTION.getName() is equals to Mouse. It calls the
	 * {@link PhoneInterface}.mousePress function
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "Mouse")
	 * @param args
	 *            array of parameter given to the Mouse function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionTouchScreenPress(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(3, args.length,node.getLineNumber())) {
			return Boolean.FALSE;
		}

		if (!args[0].isInteger()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + AN_INTEGER);
			return Boolean.FALSE;
		}

		if (!args[1].isInteger()) {
			generateError(node, SECOND + PARAMETER_IS_NOT + AN_INTEGER);
			return Boolean.FALSE;
		}
		if (!args[2].isInteger()) {
			generateError(node, THIRD + PARAMETER_IS_NOT + AN_INTEGER);
			return Boolean.FALSE;
		}

		Integer x = args[0].getInteger();
		Integer y = args[1].getInteger();
		long time = args[2].getInteger();
		Date startTime = new Date();

		try { 
			getPhoneInterface().touchScreenPress(new Position(x, y, time));
		} catch (PhoneException e) {
			generateWarning(node, e);
		}
		mainLogger.addInfotoActionLogger("Action","mousePress X:"+x.intValue()+"Y:"+y.intValue(), startTime, new Date());
		return Boolean.TRUE;
	}



	/**
	 * Call when ASTFUNCTION.getName() is equals to MouseDown. It calls the
	 * {@link PhoneInterface}.mouseDown function
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "MouseDown")
	 * @param args
	 *            array of parameter given to the MouseDown function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionTouchScreenSlide(ASTFUNCTION node, Variable[] args) {
		ArrayList<Position> path = new ArrayList<Position>();

		Date startTime = new Date();
		try {
			for(Variable arg : args) 
				if(arg.isTable() ) {
					List<Variable> variableposition = arg.getTable();
					if(variableposition.size()>=2) {
						int x = variableposition.get(0).getInteger();
						int y = variableposition.get(1).getInteger();
						long time =variableposition.get(2).getInteger();
						path.add(new Position(x,y,time) );
					}
				}


			getPhoneInterface().touchScreenSlide(path);
		} catch (PhoneException e) {
			generateWarning(node, e);
		}catch (Exception e) {
			generateWarning(node, e);
		}
		mainLogger.addInfotoActionLogger("Action",PaddFile("TouchScreenSlide",40), startTime, new Date());


		return Boolean.TRUE;
	}



	/**
	 * Call when ASTFUNCTION.getName() is equals to MouseDown. It calls the
	 * {@link PhoneInterface}.mouseDown function
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName() ==
	 *            "MouseDown")
	 * @param args
	 *            array of parameter given to the MouseDown function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionTouchScreenDragnDrop(ASTFUNCTION node, Variable[] args) {
		ArrayList<Position> path = new ArrayList<Position>();

		Date startTime = new Date();
		try {
			for(Variable arg : args) 
				if(arg.isTable() ) {
					List<Variable> variableposition = arg.getTable();
					if(variableposition.size()>=2) {
						int x = variableposition.get(0).getInteger();
						int y = variableposition.get(1).getInteger();
						long time =variableposition.get(2).getInteger();
						path.add(new Position(x,y,time) );
					}
				}


			getPhoneInterface().touchScreenDragnDrop(path);
		} catch (PhoneException e) {
			generateWarning(node, e);
		}catch (Exception e) {
			generateWarning(node, e);
		}
		mainLogger.addInfotoActionLogger("Action",PaddFile("TouchScreenDragnDrop",40), startTime, new Date());


		return Boolean.TRUE;
	}


	/**
	 * Call when ASTFUNCTION.getName() is equals to UseCPU. It simulates an application using 
	 * a given purcent of CPU. It calls the {@link PhoneInterface}.useCpu function
	 * 
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="UseCPU")
	 * @param args
	 *            array of parameter given to the UseCPU function in the script.
	 *            args[0] is the first parameter, args[1] is the second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionUseCPU(ASTFUNCTION node, Variable[] args) {
		if (!checkNumberArguments(1, args.length, node.getLineNumber())) {
			return Boolean.FALSE;
		}
		if (!args[0].isInteger()) {
			generateError(node, FIRST + PARAMETER_IS_NOT + AN_INTEGER);
			return Boolean.FALSE;
		}
		Integer usage = args[0].getInteger();
		try {
			getPhoneInterface().useCpu(usage.intValue());
		} catch (PhoneException e) {
			mainLogger.addInfotoActionLogger("Error JATK","action Use CPU", new Date(),new Date());
			generateWarning(node, e);
		}
		// Logger.getLogger(this.getClass() ).debug("Use cpu for " + usage + "%");
		return Boolean.TRUE;
	}

	/**
	 * Call when ASTFUNCTION.getName() is equals to WaitWindow. It checks mainly
	 * the {@link PhoneInterface}.getCurrentProcess function.
	 * 
	 * @param node
	 *            Node of the AST which generates this call (node.getName()
	 *            =="WaitWindow")
	 * @param args
	 *            array of parameter given to the WaitWindow function in the
	 *            script. args[0] is the first parameter, args[1] is the
	 *            second...
	 * @return Boolean.TRUE if OK, Boolean.FALSE if the function is not
	 *         implemented or if a problem happens (invalid number of parameter,
	 *         ...)
	 */
	public Boolean actionWaitWindow(ASTFUNCTION node, Variable[] args) {
		// get number of arguments
		int nbArgs = args.length;
		if (nbArgs == 0) {
			if (getPhoneInterface() instanceof PhoneRecorder){
				((PhoneRecorder)getPhoneInterface()).waitWindow();
			}else{
				try {
					// wait until the process changed
					String previousProcess = getPhoneInterface().getCurrentMidlet();
					if (previousProcess == null) {
						generateWarning(node, "");
						return Boolean.TRUE;
					}

					while (previousProcess.equals(getPhoneInterface()
							.getCurrentMidlet())) {
						previousProcess = getPhoneInterface().getCurrentMidlet();
						if (previousProcess == null) {
							generateWarning(node, "");
							return Boolean.TRUE;
						}
						try {
							// Thread.sleep(100);
							Thread t = Thread.currentThread();
							synchronized (t) {
								t.wait(100);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							return Boolean.FALSE;
						}
					}
				} catch (PhoneException e) {
					mainLogger.addInfotoActionLogger("Error JATK","action Wait Windows", new Date(),new Date());
					generateError(node, e);
				}
			}
			return Boolean.TRUE;
		} else if (nbArgs == 2) {

			try {
				if (!args[1].isInteger()) {
					generateError(node, SECOND + PARAMETER_IS_NOT + AN_INTEGER);
					return Boolean.FALSE;
				}
				Integer timeout = args[1].getInteger();
				if (!args[0].isString()) {
					generateError(node, FIRST + PARAMETER_IS_NOT + A_STRING);
					return Boolean.FALSE;
				}
				String process = args[0].getString();
				if (getPhoneInterface() instanceof PhoneRecorder){
					((PhoneRecorder)getPhoneInterface()).waitWindow(args[0].getString(),args[1].getInteger());
				}else{
					// init
					int currentElapsedTime = 0;

					String currentProcess = getPhoneInterface().getCurrentMidlet();
					if (currentProcess == null) {
						generateWarning(node, "");
						return Boolean.TRUE;
					}

					/*
					 * inv = loop until the timeout is elapsed or the current process
					 * changed
					 */
					while (!((currentProcess.equals(process)) || (currentElapsedTime >= timeout))) {
						currentProcess = getPhoneInterface().getCurrentMidlet();
						if (currentProcess == null) {
							generateWarning(node, "");
							return Boolean.TRUE;
						}
						currentElapsedTime += timeout / 10;
						try {
							// Thread.sleep(timeout.intValue() / 10);
							Thread t = Thread.currentThread();
							synchronized (t) {
								t.wait(timeout / 10);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							return Boolean.FALSE;
						}
					}
					// if (currentProcess.equals(process)) {
					; // TODO : add a log message
					// } else {
					; // TODO : add a log message
					// }
				}
			} catch (PhoneException e) {
				mainLogger.addInfotoActionLogger("Error JATK","action Wait windows", new Date(),new Date());
				generateError(node,e);
			}

			return Boolean.TRUE;
		} else {
			generateError(node, ERRMSG_INVALID_NUMBER_OF_ARGUMENTS);
			return Boolean.FALSE;
		}
	}

}
