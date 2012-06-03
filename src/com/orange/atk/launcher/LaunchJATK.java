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
 * File Name   : LaunchJATK.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.launcher;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.android.ddmlib.AndroidDebugBridge;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.error.ErrorListener;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.graphAnalyser.GraphMarker;
import com.orange.atk.graphAnalyser.PerformanceGraph;
import com.orange.atk.interpreter.ast.ASTStart;
import com.orange.atk.interpreter.atkCore.JATKInterpreter;
import com.orange.atk.interpreter.estimators.ValidateSyntax;
import com.orange.atk.interpreter.parser.ATKScriptParser;
import com.orange.atk.interpreter.parser.ParseException;
import com.orange.atk.interpreter.parser.TokenMgrError;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.android.TcpdumpLineListener;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.DocumentGenerator;
import com.orange.atk.results.logger.documentGenerator.PDFGenerator;
import com.orange.atk.results.logger.documentGenerator.TextGenerator;
import com.orange.atk.results.logger.log.DocumentLogger;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.FileUtil;
import com.orange.atk.util.NetworkAnalysisUtils;


/**
 * Entry point of JATK interpreter.
 * 
 * @author  France Telecom R&D
 *(C) France Telecom, 2008. 
 */

public class LaunchJATK  implements ErrorListener  {
	// Default name for the report
	private static final String REPORT_FILENAME = "report.";

	// possible types for report
	public static final String PDF_TYPE = "pdf";
	public static final String TXT_TYPE = "txt";
	private static  String JATKdir = Platform.getInstance().getJATKPath();

	// true if the parser must generate an conf file

	// Variables
	private String includeDir = "";
	private String testFile = "";
	private String realTestFile = "";
	//private static String JATKdir="C:\\Program Files\\JATK\\";
	private static String xmlconfilepath=null;



	private static boolean hasParseException = false;
	// private static boolean isHardStopNotDisable = true;
	private static PhoneInterface currentPhone = null;
	private static DocumentGenerator documentGenerator = null;

	private static ResultLogger logger = null;
	// value of the timeout in minutes
	private static int timeout = -1;
	private Map<String, PerformanceGraph> mapPerfGraph;
	private Map<String, GraphMarker> mapAction = null;

	private static int result = 0;

	private Hashtable hoppertestParam;

	public String[] listHopper;

	// store the type of log (txt/pdf)
	private  String logType = TXT_TYPE;

	// Directory where results would be saved
	private  String logDir;

	public LaunchJATK() {
		super();
		ErrorManager.getInstance().addErrorListener(this);
		this.logDir = Platform.TMP_DIR + Platform.FILE_SEPARATOR;
		this.includeDir = JATKdir;
		setCurrentPhone();
		this.init();
		Logger.getLogger(this.getClass() ).debug("LaunchJATK()");
	}

	public LaunchJATK(String logDir, String includeDir, String testFile, String realTestFile, 			
			String logType) 
	{
		this();
		this.logDir = logDir;
		this.includeDir = includeDir;
		this.testFile = testFile;
		this.realTestFile = realTestFile;
		this.logType = logType;
	}

	private String init() {
		//check in java path win32com.dll
		File win32 =new File(System.getenv("java.home")+Platform.FILE_SEPARATOR+"win32com.dll");
		
		if(!win32.exists()&&System.getenv("java.home")!=null)		
			FileUtil.copyfile(win32, new File(JATKdir+Platform.FILE_SEPARATOR+"win32com.dll"));

		return PhoneInterface.STATUS_PASSED;	
	}
	
	private TcpdumpLineListener tcpdumpLineListener = new TcpdumpLineListener() {
		public void newTcpDumpLine(String line) {
			Date theDate = NetworkAnalysisUtils.extractTcpdumpLineDate(line);
			String url = NetworkAnalysisUtils.extractTcpdumpLineUrl(line);
			if (logger != null && theDate != null && url != null) {
				logger.addInfotoActionLogger("URL", url, theDate, theDate);
			}
		}
	};

	private  void createPhone() throws PhoneException {
		//add config
		currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
		try {
			currentPhone.setvariable(realTestFile, logDir);
			currentPhone.addTcpdumpLineListener(tcpdumpLineListener);
			currentPhone.startTestingMode();
		} catch (PhoneException e) {
			currentPhone.stopTestingMode();
			throw new PhoneException(e.getMessage());
		}	

	}
	
	
	

	private void createPDFFile()
	{
		try {
			//Create Document generator object
			if (PDF_TYPE.equals(logType)) {
				documentGenerator = new PDFGenerator(new FileOutputStream(
						new File(logDir + Platform.FILE_SEPARATOR
								+ REPORT_FILENAME + "pdf")), logDir , "", "Orange FR.", testFile,
								false);



			} else {
				documentGenerator = new TextGenerator(new FileOutputStream(
						new File(logDir + Platform.FILE_SEPARATOR
								+ REPORT_FILENAME + TXT_TYPE)));
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}

	}


	private  void setTimeOut()
	{
		if (timeout > 0) {
			Runnable r = new Runnable() {
				public void run() {
					try {
						long duration = timeout * 60L * 1000L;
						Thread.sleep(duration);
					} catch (InterruptedException ex) {
						// system has generate an interrupt exception
						// just exit
						return;
					}
					// End of sleep, timeout reach
					Logger.getLogger(this.getClass() ).debug("Timeout...");
					stopInfoLog();
					writeLogAndExitPhoneHandling();
					throw new RuntimeException("Timeout...");
				}
			};
			new Thread(r).start();
		}
	}


	private  int interpretTestFile () throws FileNotFoundException
	{
		int result=0;
		//start Parsing .tst file
		try {
			ATKScriptParser parser = new ATKScriptParser(new java.io.FileReader(realTestFile));
			ASTStart expr = parser.start();
			// TODO:include a if and an argument for dump
			// expr.dump("");
			if (expr.jjtGetNumChildren() == 0) {
				logger.addInfoToDocumentLogger("Empty file...", 0, realTestFile);
			} else {
				
				ValidateSyntax vs = new ValidateSyntax(realTestFile, false);
				String output = (String) expr.jjtAccept(vs, "");

				if (output.length() >=1) {
					logger.addErrorToDocumentLogger(output, 0, null);
					result = 3;
					Logger.getLogger(this.getClass() ).warn("Script not valid, please check the report file");
				} else {
					JATKInterpreter interpreter = new JATKInterpreter(currentPhone,
							logger, realTestFile, logDir, includeDir);
	
					// Start of interpreter
					Boolean res = (Boolean) expr.jjtAccept(interpreter, null);
					// End of interpreter
	
					if (res.booleanValue()) {
						logger.addInfoToDocumentLogger("Test passed", -1, "");
						result = 0;
					} else {
						logger.addErrorToDocumentLogger("Test failed", 0, null);
						result = 1;
						Logger.getLogger(this.getClass()).
						warn("Test failed, please check the report file");
					}
					stopInfoLog();
				}
			}
		} catch (ParseException e) {
			logger.addErrorToDocumentLogger(e.getMessage(), 0, null);
			logger.addErrorToDocumentLogger("Test failed", 0, null);
			result = 3;
			hasParseException = true;
			Logger.getLogger(this.getClass() ).warn("Parse error, please check the report file");
		} catch (TokenMgrError e) {
			logger.addErrorToDocumentLogger(e.getMessage(), 0, null);
			logger.addErrorToDocumentLogger("Test failed", 0, null);
			result = 3;
			hasParseException = true;
			Logger.getLogger(this.getClass() ).warn("Lexical error, please check the report file");
		}

		return result;
	}

	public String  launchNewTest(String xmlconfilepath, boolean noGUI) throws FileNotFoundException, PhoneException
	{
		String result = PhoneInterface.STATUS_PASSED;
		System.setSecurityManager(null);
		// TODO  handle exception
		FileUtil.createOrDeleteDir(logDir);
		//TODO LG est-ce bien utile ?
		createPhone();	

		
		{	
			createPDFFile();

			logger = new ResultLogger(logDir, documentGenerator,xmlconfilepath);	
			if (noGUI || !CoreGUIPlugin.mainFrame.statusBar.isStop()) {
				//passage de la hashmap graph dans documentlogger
				DocumentLogger dl = logger.getDocumentLogger();
				
				dl.setMapPerfGraph(mapPerfGraph);
				setTimeOut();
	
				if (!noGUI) logger.getDocumentLogger().addPlotlistObject();
	
				int res = interpretTestFile ();		
				if (res!=0) result = PhoneInterface.STATUS_FAILED;
				
				stopExecution();
				writeLogAndExitPhoneHandling();
			} else result = PhoneInterface.STATUS_FAILED;
			stopExecution();
			

		}
		return result;
	}

	public String  launchRandomTest(String xmlconfilepath, Hashtable randomTestParam) throws FileNotFoundException, PhoneException
	{
		boolean result = false;
		System.setSecurityManager(null);
		FileUtil.createOrDeleteDir(logDir);
		createPhone();	
		
		{	
			// Document logger
			createPDFFile();

			logger = new ResultLogger(logDir, documentGenerator,xmlconfilepath);
			
			//passage de la hashmap graph dans documentlogger
			DocumentLogger dl = logger.getDocumentLogger();
			dl.setMapPerfGraph(mapPerfGraph);
			setTimeOut();

			logger.getDocumentLogger().addPlotlistObject();
			logger.setPhoneInterface(currentPhone);
			logger.start(1000);
			logger.getActionsLogger().createHopperTstfile();
			result = currentPhone.startRandomTest(realTestFile,logDir,logger, randomTestParam);
			
			logger.interrupt();
			stopExecution();
			writeLogAndExitPhoneHandling();
		}
		if(result)
			return PhoneInterface.STATUS_PASSED;
		else
			return PhoneInterface.STATUS_FAILED;
			
	}
	
	public void cancelExecution() {
		if (currentPhone!=null)  currentPhone.stopTestingMode();
		if (logger != null) {
			logger.setStopATK(true);
			if (logger.isAlive()) logger.interrupt();
		}
	}
	
	public void stopExecution() {
		if (currentPhone!=null)  {
			if (currentPhone.isInTestingMode()) currentPhone.stopTestingMode();
			currentPhone = null;
		}
		documentGenerator=null;
		currentPhone=null;
		mapPerfGraph=null;
		mapAction=null;
	}

	/**
	 * Return the result of the execution
	 * 
	 * @return the result of the execution. Used in beanshell script. O if the
	 *         execution succeeds, 1 if an interpretation error happens, 2 if a
	 *         parse error happens, ...
	 */
	public int getResult() {
		return result;
	}

	/**
	 * Stop the main log system
	 */
	private static void stopInfoLog() {
		//Logger.getLogger(this.getClass() ).debug("Stop the logger");
		if (logger != null) {
			if (logger.isAlive()) {
				logger.interrupt();
				//Logger.getLogger(this.getClass() ).debug("Waiting for loop");
				logger.join();
				//Logger.getLogger(this.getClass() ).debug("End of loop");
			}
		}
	}

	/**
	 * 
	 */
	public void writeLogAndExitPhoneHandling() {
		stopInfoLog();
		if (logger != null) {
			//write file action.log
			logger.writeActionLogFile();
			logger.generateGraphFile();
			logger.generatepltFile();
			//write Error File pdf or Txt 
			logger.dumpInStream(hasParseException);
			logger = null;

		}
	}


	public  void setIncludeDir(String dir) {

		JATKdir=dir;
		includeDir = dir+currentPhone.getIncludeDir();		
	}


	private  String getLogDir() {
		return logDir;
	}


	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}


	public  String getLogType() {
		return logType;
	}


	public  PhoneInterface getCurrentPhone() {
		return currentPhone;
	}


	private  void setCurrentPhone() {
		// TODO null if no phone connected ?
		LaunchJATK.currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
	}




	private static int getTimeout() {
		return timeout;
	}






	public void setMapPerfGraph(Map<String, PerformanceGraph> mapPerfGraph) {
		this.mapPerfGraph = mapPerfGraph;
	}


	public void setMapAction(Map<String, GraphMarker> mapAction) {
		this.mapAction = mapAction;
	}

	public void errorOccured() {
		this.cancelExecution();
		CoreGUIPlugin.mainFrame.statusBar.setStop();
		//Launch Autodetect after a Cancel
		AutomaticPhoneDetection.getInstance().resumeDetection();
		CoreGUIPlugin.mainFrame.statusBar.displayErrorMessage();
	}
	
	public void warningOccured() {
		CoreGUIPlugin.mainFrame.statusBar.displayErrorMessage();
	}
	
	
	/******************************** METHOD TO LAUNCH ATK FROM LINE COMMAND **********************************/
	public static void main(String[] args) {
		checkargs(args);
	}
	
	/**
	 * This method check arguments and parse arguments
	 * @param args arguments list */

	private static void checkargs(String[] args)
	{
		// TODO : -c should be optional, in that case default phone config file should be used
		// TODO : -pm should be specifiable
		String usage = "Usage : \n"
			+"test <test_options> WHERE test_options are :\n"
			+"\t -tf <test_file.tst> -c <monitoring_config.xml>\n"
			+"\t -td <test_dir> -c <monitoring_config.xml>\n"
			+"\t -rd <result_dir>\n";
		
		if (args.length == 0 || (args.length==1 && args[0].equals("-h"))) {
			System.out.println(usage);
			result = 3;
			return;
		}	
		// Default ATK logs are stored in file <ATK_install>/log/logJATK.log
		// For command line debugging, just replace <ATK_install>/log4j.xml, by the Eclipse_projet_ATK/log4j.xml
		DOMConfigurator.configure(Platform.getInstance().getJATKPath()+"\\log4j.xml");

		AutomaticPhoneDetection.getInstance(false).checkDevices();
		
		List<PhoneInterface> devices = AutomaticPhoneDetection.getInstance().getDevices();
		if (devices.size()==0) {
			System.out.println("FAILED: no device detected.");
		} else {
			AutomaticPhoneDetection.getInstance().setSelectedDevice(devices.get(0));
			LaunchJATK launcher = new LaunchJATK();
			String result = launcher.parseArguments(args);
			if (result!=null) {
				Logger.getLogger(LaunchJATK.class).debug(result);
				System.out.println(result);
			}
			
		}
		AndroidDebugBridge.disconnectBridge();
		AndroidDebugBridge.terminate();
	}

	private String parseArguments(String[] args) {
		if (!args[0].equals("test")) return ("FAILED: test command is missing.");
		String result_dir = null;
		Vector<String> test_files = new Vector<String>();
		Vector<String> config_files = new Vector<String>();
		for (int i=1; i< args.length; i++) {
			if (args[i].equals("-rd")) {
				i++;
				if (i == args.length) return ("FAILED: [-rd option] missing result directory path.");
				else {
					File dir = new File(args[i]);
					if (!dir.exists()) return ("FAILED: [-rd option] result directory does not exist.");
					else result_dir = args[i];
				}
			} else if (args[i].equals("-tf")) {
				i++;
				if (i == args.length) return ("FAILED: [-tf option] missing test file path.");
				else {
					File file = new File(args[i]);
					if (!file.exists()) return ("FAILED: [-tf option] test file "+args[i]+" not found.");
					else {
						test_files.add(args[i]);
						i++;
						if (i == args.length || !args[i].equals("-c")) return ("FAILED: -c <config_file_path> option missing.");
						else {
							i++;
							if (i == args.length) return ("FAILED: [-c option] missing config file path.");
							else {
								file = new File(args[i]);
								if (!file.exists()) return ("FAILED: [-c option] config file "+args[i]+" not found.");
								else {
									config_files.add(args[i]);
								}
							}
						}
					}
				} 
			} else if (args[i].equals("-td")) {
				i++;
				if (i == args.length) return ("FAILED: [-td option] missing test directory path.");
				else {
					File file = new File(args[i]);
					if (!file.exists()) return ("FAILED: [-td option] test directory does not exist.");
					else {
						String[] tfiles = file.list(new SuffixFilter("tst"));
						if (tfiles.length == 0) return ("FAILED: [-td option] test directory does not contain any .tst file.");
						for (int j=0; j<tfiles.length; j++) {
							test_files.add(args[i]+Platform.FILE_SEPARATOR+tfiles[j]);	
						}
						i++;
						if (i == args.length || !args[i].equals("-c")) return ("FAILED: -c <config_file_path> option missing.");
						else {
							i++;
							if (i == args.length) return ("FAILED: [-c option] missing config file path.");
							else {
								file = new File(args[i]);
								if (!file.exists()) return ("FAILED: [-c option] config file "+args[i]+" not found.");
								else {
									for (int j=0; j<tfiles.length; j++) {
										config_files.add(args[i]);
									}
								}
							}
						}

					}
				} 
			}
		}
		if (result_dir == null) return ("FAILED: -rd <result_directory> option must be specified.");
		if (test_files.size() == 0) return ("FAILED: at least one test file must be specified, using -tf and/or -td options.");
		
		for (int i=0; i< test_files.size(); i++) {
			this.testFile = test_files.get(i);
			this.realTestFile = testFile;
			String testFileName = (new File(testFile)).getName();
			this.logDir = result_dir+Platform.FILE_SEPARATOR+testFileName.substring(0,testFileName.lastIndexOf("."));
			try {
				String result = launchNewTest(config_files.get(i), true);
				System.out.println(result+": test "+testFile);
			} catch (FileNotFoundException e) {
				return("FAILED: test "+testFile);
				//e.printStackTrace();
			} catch (PhoneException e) {
				return("FAILED: test "+testFile );
				//e.printStackTrace();
			}		
		}
		return null;
	}

	public class SuffixFilter implements FilenameFilter {
	    private String suffix;
	    public SuffixFilter(String suffix) {
	        this.suffix = "." + suffix;
	    }
	    public boolean accept(File dir, String name) {
	        return name.endsWith(suffix);
	    }
	}

	/******************************** OLD METHODS TO LAUNCH ATK FROM LINE COMMAND **********************************/
	/***** NOT MAINTAINED - TO BE UPDATED
	public void launchNewTest(String[] args) throws FileNotFoundException, PhoneException
	{		
		System.setSecurityManager(null);
		checkargs(args);
		// TODO  handle exception
		//Logger.getLogger(this.getClass() ).debug("logDir : " + logDir);
		FileUtil.createOrDeleteDir(logDir);
		createPhone();	

		if(currentPhone instanceof NokiaS60Phone)
		{
			stopExecution();
		}
		else
		{
			createPDFFile();
			setTimeOut();
			xmlconfilepath = JATKdir+Platform.FILE_SEPARATOR+"log"+Platform.FILE_SEPARATOR+"Confile.xml";
			//Create logger (save at cmd,error,warning)
			logger = new ResultLogger(logDir, documentGenerator,xmlconfilepath);
			Logger.getLogger(this.getClass() ).debug(" include dir "+includeDir);

			if(!isRandomTest)
				interpretTestFile ();
			else
			{
				logger.getActionsLogger().createHopperTstfile();
				logger.setPhoneInterface(currentPhone);
				logger.start(1000);
				currentPhone.startRandomTest(realTestFile,logDir,logger);
				logger.interrupt();
			}

			//ecriture des fichiers
			writeLogAndExitPhoneHandling();
			stopExecution();			
		}
		return;
	}
	

	/**
	 * This method check arguments and parse arguments
	 * @param args arguments list

	private  void checkargs(String[] args)
	{
		if (args.length == 0) {
			/*	Logger.getLogger(this.getClass() ).warn("Usage : MainClass [" + OPTION_INCLUDE_FOLDER
					+ " includeDir] " + "[" + OPTION_LOG_FOLDER + " logDir] "
					+ "[" + OPTION_PHONE + " (" + PHONE_DEFAULT + "|"
					+ WTKPHONE_INTERFACE + "|" + SEPHONE + ")] " + "["
					+ OPTION_CONFIG_FILE + " conffile] " + "["
					+ OPTION_LOG_TYPE + " (" + TXT_TYPE + "|" + PDF_TYPE
					+ ")] " + "testFile.tst " + Platform.LINE_SEP + "or"
					+ Platform.LINE_SEP + " MainClass " + OPTION_CONFIG_FILE
					+ " conffile" + " [" + OPTION_PHONE + " (" + PHONE_DEFAULT
					+ "|" + WTKPHONE_INTERFACE + "|" + SEPHONE + ")] "
					+" ["+"-confile"+" xml confile]"
					+" ["+"-JATKdir"+" JATK default directory]"					
					+" ["+"-pm"+"ispowermonitor]");

			result = 3;
			return;
		}	

		String error = parseArguments(args,args[args.length - 1].length() == 0 ? args.length - 1: args.length);

		if (error != null) {
			Logger.getLogger(this.getClass() ).warn(error);
			stopExecution();
			return;
		}
	}

	/**
	 * Initialize arguments such as:
	 * isInitializeState: manage two mode Execution and init mode
	 * testFile: 
	 * @param args argiments list
	 * @param argc number of arguments
	 * @return

	private  String parseArguments(String[] args, int argc) {
		int i = 0;
		// int length = args.length;
		int length = argc;
		// case MainClass -c conffile
		if (length % 2 == 0) {
			//Should have only two arguments 
			//eg: MainClass -p SEPhoneJP8 -c config.cfg 
		} else {
			//Execution 
			Logger.getLogger(LaunchJATK.class ).debug("launch script state");

			//get .tst file (last arguments)
			testFile = args[length - 1];
			realTestFile = args[length - 1];

			//list of all arguments
			for(int j=0; j<args.length; j++) {
				Logger.getLogger(LaunchJATK.class ).debug("argument "+j+" : "+args[j]);
			}


			if (!new File(testFile).exists()) {
				return "Test file " + testFile + " does not exist...";
			}
			if (!new File(testFile).isFile()) {
				return "Test file " + testFile + " is not a file...";
			}
			length = length - 1;
		}


		while (!(i >= length)) {

			// folder d'include
			if (OPTION_INCLUDE_FOLDER.equals(args[i])) {
				includeDir = args[i + 1];
				if (!new File(includeDir).isDirectory()) {
					return includeDir
					+ " is not a valid directory for include file...";
				}

				// result folder 
			} else if (OPTION_LOG_FOLDER.equals(args[i])) {
				logDir = args[i + 1] + Platform.FILE_SEPARATOR;
				if (new File(logDir).exists()) {
					if (!new File(logDir).isDirectory()) {
						return logDir + " is not a directory...";
					}
				} else {
					// Create the folder if logDir does not exist
					if (!new File(logDir).mkdirs()) {
						return "Not able to create directory : " + logDir;
					}
				}

				//select report typr pdf or txt
			} else if (OPTION_LOG_TYPE.equals(args[i])) {
				logType = args[i + 1];
				if (!(TXT_TYPE.equals(logType) || PDF_TYPE.equals(logType))) {
					return logType + " output type is not supported ...";
				}

				//select if timout is set
			} else if (OPTION_TIMEOUT.equals(args[i])) {
				try {
					timeout = Integer.valueOf(args[i + 1]);
				} catch (NumberFormatException ex) {
					return args[i + 1] + " is not a valid value for timeout";
				}

			} 

			//set power monitor on
			else if ("-pm".equals(args[i])) {
				try {
					ispm = Boolean.valueOf(args[i + 1]);

				} catch (NumberFormatException ex) {
					return args[i + 1] + " is not a valid value for pm must be boolean";
				}
			}

			else if ("-confile".equals(args[i])) {
				xmlconfilepath = args[i + 1];

			}

			else if ("-JATKdir".equals(args[i])) {
				JATKdir = args[i + 1];

			}


			else if (OPTION_EXIT.equals(args[i])) {
				"no".equals(args[i + 1]);
			} else {
				return "Unknown option " + args[i] + "...";
			}
			i = i + 2;
		}
		return null;
	}

	 */

}
