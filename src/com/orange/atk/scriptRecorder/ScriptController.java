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
 * File Name   : ScriptController.java
 *
 * Created     : 15/07/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder;


import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.orange.atk.interpreter.ast.ASTFUNCTION;
import com.orange.atk.interpreter.ast.ASTLOOP;
import com.orange.atk.interpreter.ast.ASTNUMBER;
import com.orange.atk.interpreter.ast.ASTSTRING;
import com.orange.atk.interpreter.ast.ASTStart;
import com.orange.atk.interpreter.ast.Node;
import com.orange.atk.interpreter.ast.SimpleNode;
import com.orange.atk.interpreter.estimators.ASTtoTSTVisitor;
import com.orange.atk.interpreter.estimators.CommentUncommentAST;
import com.orange.atk.interpreter.estimators.EstimateTimeVisitor;
import com.orange.atk.interpreter.estimators.InsertInAST;
import com.orange.atk.interpreter.estimators.RemoveNode;
import com.orange.atk.interpreter.estimators.SurroundLoopVisitor;
import com.orange.atk.interpreter.estimators.ValidateSyntax;
import com.orange.atk.interpreter.parser.ATKScriptParser;
import com.orange.atk.interpreter.parser.ATKScriptParserTreeConstants;
import com.orange.atk.interpreter.parser.ParseException;
import com.orange.atk.interpreter.parser.TokenMgrError;
import com.orange.atk.manageListener.RecordPhoneEventListener;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.DocumentGenerator;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.scriptRecorder.scriptJpanel.RecorderJATKInterpreter;


public class ScriptController {
	private PhoneInterface phone = new DefaultPhone();
	private RecorderFrame ui;
	private Emulator sf;
	private ASTStart ast;
	private RecordPhoneEventListener phoneListener;

	private String scriptPath;
	private String screenshotDirPath=null;
	private boolean ignoreFirstMessageFromPhone;
	private boolean recording=false;
	private String phonemode;
	private boolean isRunning=false;
	private int idScrenshot;
	private static String logDir = Platform.TMP_DIR ;
	private static String includeDir = "";
	// Default name for the report
	private static final String REPORT_FILENAME = "report.";

	// possible types for report
	private static final String PDF_TYPE = "pdf";
	private static final String TXT_TYPE = "txt";
	//private static final String EXT_PICTURE = "jpg";
	// store the type of log (txt/pdf)
	private static String logType = TXT_TYPE;
	private static boolean hasParseException = false;

	private static DocumentGenerator documentGenerator = null;
	private static ResultLogger resLogger = null;


	private static ScriptController singleton;

	//param for robotium test 
	public static String PackageName="";
	public static String MainActivityName="";
	public static String PackageSourceDir="";
	public static int Versioncode=-1;



	private ScriptController() {
		Logger.getLogger(this.getClass() ).debug("/********new sriptcontroller*************/");
		this.ui=new RecorderFrame( this);		
		//Screenshot frame
		newFile();
		//
		idScrenshot=0;
	}

	//singleton
	public static final ScriptController getScriptController() {
		if (singleton==null) {
			singleton = new ScriptController();
		}
		return singleton;
	}


	public void display() {
		if (!ui.isVisible()) ui.displayFrame();
	}

	public void runLines(final int start, final int nb)  {
		//		Logger.getLogger(this.getClass() ).debug("controler.runLines");
		initPhone();
		if (!isRecording()){
			isRunning =true;

			try {
				phone.startTestingMode();
			} catch (PhoneException e) { 
				phone.stopTestingMode();
				return;
			}
			launchTest(phone);
		}
	}

	//TODO:clean phone relation
	protected boolean recordMode() {
		if (!initPhone())
			return false;

		if (ast==null)
			ast = new ASTStart(ATKScriptParserTreeConstants.JJTSTART);

		phonemode =(String) ui.getJcbPhonemode().getSelectedItem();
		//Check if we record from pc or from phone
		if (phonemode.equals("Phone")){

			//first init	
			if(phoneListener!=null)
				phone.removePhoneKeyListener(phoneListener);
			phoneListener=new RecordPhoneEventListener();
			phone.addPhoneKeyListener(phoneListener);

			try {
				phone.startRecordingMode();
			} catch (PhoneException e) {
				phone.stopRecordingMode();
				phone.removePhoneKeyListener(phoneListener);
				return false;
			}

			//ferme app
		}
		else { //record from PC
			sf = new Emulator(ui, this);
		}

		recording=true;

		//first instruction, start main log	
		//if not first destroy last stopmainlog
		if (ast.jjtGetNumChildren()==0) {
			//empty ast, so first Instruction ...
			ASTFUNCTION startmainlog = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
			startmainlog.setValue("StartMainlog");
			ASTNUMBER param = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
			param.setValue("1000");
			startmainlog.jjtAddChild(param, 0);

			ast.jjtAddChild( startmainlog ,0);
		}else{
			Node lastnode = ast.jjtGetChild(ast.jjtGetNumChildren() -1);
			if(lastnode instanceof ASTFUNCTION &&
					"stopmainlog".equals( ((ASTFUNCTION)lastnode).getValue().toLowerCase() ))
				ast.jjtRemoveChild(ast.jjtGetNumChildren() -1);

		} 

		return true;

	}


	public boolean initPhone() {
		String JATKpath = Platform.getInstance().getJATKPath();
		//Create PortCommand object to send AT CMD
		includeDir=JATKpath+phone.getIncludeDir();
		return true;

	}

	public boolean isRunning() {
		return isRunning;
	}

	protected void stop() {
		ui.setRunningNode(0);
		if (isRunning){
			if (phone.isInTestingMode()) {
				ScriptController.resLogger.setStopATK(true); 
				phone.stopTestingMode();
			}
			isRunning=false;
			//olddriver=null; // insure the creation of a new phone.
		} else if (phonemode.equals("Emulator")){
			sf.setVisible(false);
		} else {
			phone.stopRecordingMode();	
			phone.removePhoneKeyListener(phoneListener);
		}

		if (recording) {
			//LAst instruction : stop main log
			addEvent("StopMainlog" );
			ui.updateScript();
		}
		recording=false;
	}

	public boolean isRecording() {
		return recording;
	}

	private void launchTest( PhoneInterface runningPhone) {
		// Document logger
		if(scriptPath !=null  && new File(scriptPath).exists() )
			logDir=scriptPath.replace(".tst", Platform.FILE_SEPARATOR);

		File folder=new File(logDir);

		File output=new File(logDir);
		if (!output.exists()&& !output.mkdir())
			Logger.getLogger(this.getClass() ).warn("Can't  make dir  "+output.getPath());			

		if(folder.exists()&& folder.canWrite()){
			//Create Logger
			getLogger();
			Logger.getLogger(this.getClass() ).debug("put in logger:"+logDir);

			// expr.dump("");
			if (ast.jjtGetNumChildren() != 0) {
				//JATKInterpreter interpreter= new JATKInterpreter(runningPhone,Infolog, scriptPath,logDir,includeDir);
				RecorderJATKInterpreter interpreter= new RecorderJATKInterpreter(this.ui,runningPhone,resLogger, scriptPath,logDir,includeDir);
				// Start of interpreter
				ast.jjtAccept(interpreter, null);
			}

		} else {
			if (!folder.canWrite()){
				Logger.getLogger(this.getClass() ).warn(logDir+" can't write");
			}
			if(!folder.exists()){
				Logger.getLogger(this.getClass() ).warn(logDir+" doesn't exist !");
			}
		}
	}


	/**
	 * Method call by RecordPhoneEventListener 
	 * @param action
	 * @param param
	 */
	public void addEvent(String code) {
		//		Logger.getLogger(this.getClass() ).debug("/********controler.append("+action+","+parameters[0]+")********/");
		if (ignoreFirstMessageFromPhone){
			ignoreFirstMessageFromPhone=false;
		}
		else{
			ATKScriptParser parse = new ATKScriptParser(new StringReader(code));
			ASTStart newnodes = new ASTStart(ATKScriptParserTreeConstants.JJTSTART);
			try {
				newnodes = parse.start();
			} catch (ParseException e) {
				Logger.getLogger(this.getClass() ).warn("WARNING : unknow code for :"+code+"\r\n"+e.getMessage());
			}

			//insert the nodes at the last place
			for( int i=0; i< newnodes.jjtGetNumChildren() ;i++) {
				SimpleNode functionode = (SimpleNode) newnodes.jjtGetChild(i);
				ast.jjtInsertChild(functionode, ast.jjtGetNumChildren());
			}	

			if(!code.toLowerCase().startsWith("sleep"))
				ui.updateScript();
		}
	}



	/**Fill the AST and JTree in Interface by a new script.
	 * 
	 * call by UI, the function prepare also phone interface.
	 * @param scriptPath 
	 */
	public void openScript(String scriptPath){
		//Logger.getLogger(this.getClass() ).debug("controller.openScript");
		String errortext="";
		this.scriptPath=scriptPath;

		//record mode and insertmode are exclusive
		if (isRunning()||isRecording()){
			stop();
		}

		//init Listener

		//TODO : verify script
		//What's happened when parsing error?
		ATKScriptParser parser;
		ASTStart oldast = ast;
		try {
			parser = new ATKScriptParser(new java.io.FileReader(new File (scriptPath)));
			ast = parser.start();


			//validate name of functions, nb of loop...
			ValidateSyntax vs = new ValidateSyntax(scriptPath, false);
			String output = (String) ast.jjtAccept(vs, "");

			if (output.length() >=1) 
				ui.addToConsole(output);

		} catch (FileNotFoundException e) {
			ui.addToConsole("File not Found : "+scriptPath+"\n");
			Logger.getLogger(this.getClass()).error("File Not Found: "+scriptPath );
			return ;
		} catch (ParseException e) {
			ui.addToConsole("Erreur de Parsing :\n"+e.getMessage());
			errortext ="Erreur de Parsing :\n"+e.getMessage();

		} finally {
			if (ast ==null) {
				ast = oldast;
				Logger.getLogger(this.getClass()).error(errortext);
				return;
			}

		}
		ui.updateScript();
		//launchtest
		//launchTest( phoneRecorder);

	}

	/**
	 * 
	 * @param tp a way to the node to deleted
	 */
	public void delete(Vector<List<Integer>> tps) {
		//Logger.getLogger(this.getClass() ).debug("Controller : Delete the node "+tp.toString());

		Object[] result = determineRangeOfSelection(tps);
		if( result ==null)
			return ;

		RemoveNode com = new RemoveNode(
				(List<Integer>) result[0],
				(Integer) result[1]);
		Boolean output = (Boolean) com.visit(ast, null);
		if (!output)
			ui.addToConsole("The can't be deleted.");
	}



	private ResultLogger getLogger() {
		String xmlconfilepath=AutomaticPhoneDetection.getInstance().getxmlfilepath();
		resLogger = new ResultLogger(logDir, documentGenerator,xmlconfilepath, false);
		return resLogger;
	}

	public void save(String scriptPath) {
		//		Logger.getLogger(this.getClass() ).debug("/*****Controller.save*******/");
		this.scriptPath=scriptPath;

		generateScriptFile(scriptPath );

	}

	public void generateScriptFile(String scriptPath) {
		Logger.getLogger(this.getClass() ).debug("/*Controller.generateScriptFile*/");
		FileWriter fw=null;
		try {
			//init the filewriter for the .tst File
			fw = new FileWriter(new File(scriptPath));
			fw.write("");


			//visitor generate the tst file
			fw.write(getASTinText(false));

		} catch (IOException e) {
			e.printStackTrace();

		} finally{
			try {
				if(fw!=null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void newFile() {
		//		Logger.getLogger(this.getClass() ).debug("Controller.newFile");
		if (isRecording()){
			stop();
		}
		scriptPath=null;
		screenshotDirPath=null;
		ast = new ASTStart(ATKScriptParserTreeConstants.JJTSTART);
		//first instruction, start main log	
		if (ast.jjtGetNumChildren()==0) {
			//empty ast, so first Instruction ...
			ASTFUNCTION startmainlog = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
			startmainlog.setValue("StartMainlog");
			ASTNUMBER param = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
			param.setValue("1000");
			startmainlog.jjtAddChild(param, 0);
			ast.jjtAddChild( startmainlog ,0);
		}else{
			Node lastnode = ast.jjtGetChild(ast.jjtGetNumChildren() -1);
			if(lastnode instanceof ASTFUNCTION &&
					"stopmainlog".equals( ((ASTFUNCTION)lastnode).getValue().toLowerCase() ))
				ast.jjtRemoveChild(ast.jjtGetNumChildren() -1);
		}
	}



	/**
	 * Verify that the list represent a continuous range.
	 * Return the extremum of the range or null if it's not a range.
	 * 
	 * @param tps a tree path (list of index) List. 
	 * 	represent a range of selected node in GUI
	 * @return Object[0] is a List index which represent the tree path to access at minimal extrema.
	 * 		   OBJECT[1] ist the index of maximal extrema. the extremum are sibling.
	 */
	private Object[] determineRangeOfSelection(Vector<List<Integer>> tps) {
		//Find the first selected node
		int minPathLength=100000000;
		List<Integer> minimalPath=null;
		for (List<Integer> tp : tps)
			if (tp.size() <minPathLength ||
					(tp.size() == minPathLength && tp.get(minPathLength-1) <minimalPath.get(minPathLength-1) ) ) {
				minimalPath = tp;
				minPathLength = tp.size();
			} 
		//		Logger.getLogger(this.getClass() ).debug("minimal path lenth : "+minPathLength);
		//		Logger.getLogger(this.getClass() ).debug("path : "+minimalPath.toString());

		//all path must contains the minimal path except the last index
		//find the last selected node
		int maximalindex =0;
		for(List<Integer> tp : tps) {
			if( ! tp.subList(0, minPathLength-1).equals(minimalPath.subList(0, minPathLength-1) ) ) {
				ui.addToConsole("elements doesn't have the same minimal Path "+ minimalPath.toString());
				return null;
			}
			if (tp.size()==minimalPath.size() && tp.get(minPathLength-1)> maximalindex)
				maximalindex= tp.get(minPathLength-1);
		}

		Object[] result = {minimalPath, maximalindex};
		return result;
	}



	/**
	 * 
	 * @param tps The list of Tree Paths to be surrounded
	 * @param nbloop return of question nbloop
	 */
	public boolean surroundLoop(Vector<List<Integer>> tps, String nbloop) {
		//		Logger.getLogger(this.getClass() ).debug("Controller.surroundloop");
		SurroundLoopVisitor SLv;

		//the JTree select in order it want.
		Object[] result = determineRangeOfSelection(tps);
		if( result ==null)
			return false;


		Pattern pat = Pattern.compile("\\s*([0-9]+).*");
		Matcher mtc = pat.matcher(nbloop);
		if (mtc.matches()) {
			int nbloopInt = Integer.parseInt(mtc.group(1));	
			SLv= new SurroundLoopVisitor(
					(List<Integer>) result[0],
					(Integer) result[1],
					nbloopInt);

		} else {
			//nbloop should be a Variable... (it's not a integer)
			SLv= new SurroundLoopVisitor(
					(List<Integer>) result[0],
					(Integer) result[1],
					nbloop);
		}

		return (Boolean) SLv.visit(ast,null);
	}

	/**
	 * If these line are uncommented, comment them.
	 * If these line are commented, uncomment them
	 * @param startline : the first line to comment//uncomment
	 */
	public void comment(List<Integer> tp) {
		//Logger.getLogger(this.getClass() ).debug("Doit commenter le noeud "+tp.toString());
		CommentUncommentAST com = new CommentUncommentAST(tp);
		Boolean result = (Boolean) com.visit(ast, null);

		if (!result)
			ui.addToConsole("the line can't be commented or uncommented");

		//validate name of functions, nb of loop...
		ValidateSyntax vs = new ValidateSyntax(scriptPath, true);
		String output = (String) ast.jjtAccept(vs, "");

		if (output.length() >=1) 
			ui.addToConsole(output);
	}



	public void close() {
		//		Logger.getLogger(this.getClass() ).debug("Controller.close");
		if (phone!=null){
			if (isRecording()){
				phone.stopRecordingMode();
			}
			phone.stopTestingMode();
		}
	}

	public PhoneInterface getPhone() {
		return phone;
	}



	public void takeScreenShot() {

		Date stopRecordTime  = new Date();
		Date startRecordTime = phoneListener.getStartRecordTime();

		if(stopRecordTime.getTime()-startRecordTime.getTime()>0)
			addEvent("Sleep("+(stopRecordTime.getTime()-startRecordTime.getTime())+ ")");

		if (screenshotDirPath==null){
			int result =askScreenshotDir();
			if 	(result!=JFileChooser.APPROVE_OPTION){
				if(phonemode.equals("Phone"))
					try {
						// TODO MMH When is called phone.stopRecordingMode() ???
						phone.startRecordingMode();
					} catch (PhoneException e) {
						JOptionPane.showMessageDialog(ui, "Phone recording problem", e.getMessage(), JOptionPane.ERROR_MESSAGE);
						phone.stopRecordingMode();
					}
				return;
			}
		}

		SimpleDateFormat formatter = null;
		// Windows/Linux
		if (Platform.OS_NAME.toLowerCase().contains("windows")) {
			formatter = new SimpleDateFormat("HmmssSSS");
		} else {
			formatter = new SimpleDateFormat("H:mm:ssSSS");
		}
		String dateString = formatter.format(new Date());
		try {
			saveScreenShot("Screenshot_"+ idScrenshot +"-"+dateString);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(ui, "Phone recording problem", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			phone.stopRecordingMode();
			return;
		}
		addEvent("Screenshot(Screenshot_"+ (idScrenshot++) +")" );
		startRecordTime = new Date();
		phoneListener.setStartRecordTime(startRecordTime);
	}

	private void saveScreenShot(String filename) {
		if (screenshotDirPath==null){
			askScreenshotDir();
		}
		//		if (phonemode.equals("Phone")){
		//			phone.stopScriptRecording();
		//		}
		try {
			RenderedImage ss= phone.screenShot();
			if (ss == null) {
				throw new NullPointerException("image is null");
			}
			File fichier = new File(screenshotDirPath+Platform.FILE_SEPARATOR+filename + "."
					+ "png");
			File directory =new File (screenshotDirPath);
			if (!directory.exists()){
				if(!directory.mkdirs())
					Logger.getLogger(this.getClass() ).warn("Can't  make dir  "+directory.getPath());			
			}
			try {
				if (!ImageIO.write(ss, "png", fichier)) {
					JOptionPane.showMessageDialog(null, "Warning : the screenshot could not be saved");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*if(phonemode.equals("Phone")){
				phone.initScriptRecording(this);
			}*/
		} catch (PhoneException e) {
			e.printStackTrace();
		}

	}



	public int askScreenshotDir() {
		JFileChooser jfc= new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = jfc.showDialog(ui,"Select as directory for Reference Screenshots");
		if (result==JFileChooser.APPROVE_OPTION){
			setScreenshotDir(jfc.getSelectedFile().getAbsolutePath());
		}
		return result;
	}



	public void setScreenshotDir(String screenshotDir) {
		screenshotDirPath=	screenshotDir;	
	}

	public String getScriptPath(){
		return scriptPath;
	}

	/**translate the AST in a String.
	 * The string represent the AST in ".tst" language
	 * 
	 * @param Linemunber  : set it at True if you want to show the line number
	 * 
	 * @return a String which represent the script
	 */
	private String getASTinText(boolean lineNumber) {
		ASTtoTSTVisitor hrav = new ASTtoTSTVisitor(lineNumber);
		return (String) ast.jjtAccept(hrav, null);
	}



	public ASTStart getAST() {
		return ast;
	}

	/**
	 * 
	 * @param leadSelectionPath  : a treepath 
	 * 	representing the position of insertion or modification
	 * @param text. the text to insert.
	 * @return true if the AST has been modified, false if not.
	 */
	public boolean insertOrModify( List<Integer> leadSelectionPath, String text,boolean modify) {

		Boolean resultat = true;
		ASTStart result = null;

		try{
			//The loop Node doesn't respect grammar of the language (No EndLoop)
			Pattern Pat = Pattern .compile(" *Loop\\( *([0-9][0-9]*) *\\) *");
			Matcher mtc = Pat.matcher(text);
			if( mtc.matches()) {
				ASTLOOP loop = new ASTLOOP(ATKScriptParserTreeConstants.JJTLOOP);
				ASTNUMBER nbloopAST = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
				nbloopAST.setValue(mtc.group(1));
				loop.jjtAddChild(nbloopAST, 0);
				result = new ASTStart(ATKScriptParserTreeConstants.JJTSTART);
				result.jjtAddChild(loop, 0 );

			} else {
				ATKScriptParser parser = new ATKScriptParser(new StringReader(text));
				result = parser.start();
			}

			//Logger.getLogger(this.getClass() ).debug("scriptcontroller modify: "+text);
			if (result ==null ||result.jjtGetNumChildren() ==0) {
				ui.addToConsole("parsing fail : "+text+"\n");
				return false;	
			}
			if (result.jjtGetNumChildren() !=1 ) {
				Logger.getLogger(this.getClass() ).warn("more than one children, not normal");
				return false;
			} 

			//validate name of functions, nb of loop...
			ValidateSyntax vs = new ValidateSyntax(scriptPath, true);
			String output = (String) result.jjtAccept(vs, " unused arg");

			if (output.length() >=1) {
				resultat = false;
				ui.addToConsole(output);
			}
			InsertInAST iia = new InsertInAST(leadSelectionPath, result.jjtGetChild(0), modify); 
			resultat = (Boolean) ast.jjtAccept(iia, null) && resultat ;			

			//active playbutton in UI
			ui.activePlaybutton();

		} catch(ParseException e) {
			ui.addToConsole("Parse exception during modification of "+text+"\n"+e.getMessage());


		} catch(TokenMgrError e) {
			ui.addToConsole("Unallowed token in expression : "+text+"\n"+e.getMessage());

		} finally {
			ui.updateScript();
		}

		return resultat;

	}

	public int estimateTimeExecution() {
		EstimateTimeVisitor est = new EstimateTimeVisitor();
		int time = (Integer) ast.jjtAccept(est, 0);
		return time;
	}



	public static  int estimateTimeExecution(String filepath) {

		int time =0;
		try {
			ATKScriptParser parser = new ATKScriptParser(new java.io.FileReader(new File (filepath)));
			ASTStart asttemp = parser.start();
			EstimateTimeVisitor est = new EstimateTimeVisitor();
			time  = (Integer) asttemp.jjtAccept(est, 0);
		} catch (FileNotFoundException e) {
			Logger.getLogger(ScriptController.class ).warn("File not Found : "+filepath+"\n");
		} catch (ParseException e) {
			Logger.getLogger(ScriptController.class ).warn("Erreur de Parsing :\n"+e.getMessage());

		} 

		return time;
	}



	public void setPhone(PhoneInterface device) {
		phone = device;

	}

	public void newFileForRobotium() {
		if (isRecording()){
			stop();
		}
		scriptPath=null;
		screenshotDirPath=null;
		ast = new ASTStart(ATKScriptParserTreeConstants.JJTSTART);

		if (ast.jjtGetNumChildren()==0) {
			//empty ast, so first Instruction ...
			ASTFUNCTION startmainlog = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
			startmainlog.setValue("StartMainlog");
			ASTNUMBER param = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
			param.setValue("1000");
			startmainlog.jjtAddChild(param, 0);
			ast.jjtAddChild(startmainlog, 0);

			ASTFUNCTION startTest = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
			startTest.setValue("StartRobotiumTestOn");
			ASTSTRING param1 = new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
			param1.setValue("'"+PackageName+"'");
			ASTSTRING param2 = new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
			param2.setValue("'"+MainActivityName+"'");
			ASTSTRING param3 = new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
			param3.setValue("'"+PackageSourceDir+"'");
			ASTNUMBER param4 = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
			param4.setValue(String.valueOf(Versioncode));
			startTest.jjtAddChild(param1, 0);
			startTest.jjtAddChild(param2, 1);
			startTest.jjtAddChild(param3, 2);
			startTest.jjtAddChild(param4, 3);
			ast.jjtAddChild(startTest, 1);

			ASTFUNCTION exitSolo = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
			exitSolo.setValue("ExitSolo");
			ast.jjtAddChild(exitSolo, 2);

			PackageName="";
			MainActivityName="";
			PackageSourceDir="";
			Versioncode=-1;
		}else{
			Node lastnode = ast.jjtGetChild(ast.jjtGetNumChildren() -1);
			if(lastnode instanceof ASTFUNCTION &&
					"stopmainlog".equals( ((ASTFUNCTION)lastnode).getValue().toLowerCase() ))
				ast.jjtRemoveChild(ast.jjtGetNumChildren() -1);
		} 
	}

	public void setTestAPKWithRobotiumParam(String packName, String activityName, String packsourceDir,int versioncode) {

		PackageName=packName;
		MainActivityName=activityName;
		PackageSourceDir=packsourceDir;
		Versioncode= versioncode;
	}

	public ArrayList<String> getAllInstalledAPK() {
		ArrayList<String> allapk=null;
		initPhone();
		try {
			allapk= phone.getAllInstalledAPK();
		} catch (PhoneException e) {
			Logger.getLogger(this.getClass() ).error(e.getMessage()+" in function getAllInstalledAPK");
		}
		return allapk;
	}

	public ArrayList<String> getForegroundApp() {
		ArrayList<String> allapk=null;
		initPhone();
		try {
			allapk= phone.getForegroundApp();
		}catch (PhoneException e) {
			Logger.getLogger(this.getClass() ).error(e.getMessage()+" in function getForegroundApp");
		}
		return allapk;
	}



}
