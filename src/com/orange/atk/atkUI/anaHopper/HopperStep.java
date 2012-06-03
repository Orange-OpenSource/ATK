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
 * File Name   : HopperStep.java
 *
 * Created     : 23/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaHopper;




import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.IAnalysisMonitor;
import com.orange.atk.atkUI.corecli.LicenceException;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.utils.Digest;
import com.orange.atk.atkUI.corecli.utils.Out;
import com.orange.atk.atkUI.corecli.utils.StringUtilities;
import com.orange.atk.atkUI.corecli.utils.XMLOutput;
import com.orange.atk.graphAnalyser.CreateGraph;
import com.orange.atk.graphAnalyser.GraphMarker;
import com.orange.atk.graphAnalyser.PerformanceGraph;
import com.orange.atk.graphAnalyser.RealtimeGraph;
import com.orange.atk.launcher.LaunchJATK;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.platform.Platform;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HopperStep extends Step {

	/**
	 * Path to the flash file to analyse.
	 */
	Map<String, PerformanceGraph> mapPerfGraph;
	Map<String, GraphMarker> mapAction = null;
	private String jatkFilePath;
	private File flashFile;
	private CreateGraph JaTKCharts;
	private String JATKpath= Platform.getInstance().getJATKPath();
	protected RealtimeGraph realtime;  

	private static PhoneInterface currentPhone = null;

	private Hashtable<String,String> hopperStepParam= new Hashtable<String,String>();
	public static final String PARAM_TIME = "hopperparam"; // do not change this name, for backward compatibility purpose
	public static final String PARAM_NBEVENTS = "nbeventsparam";
	public static final String PARAM_THROTTLE = "throttleparam";

	public final static String type = "hopper"; 
	
	/**
	 * Builds a hopper step with the path to the hopper file
	 * 
	 * @param hopperFilePath
	 *            Path to the hopper file
	 **/
	public HopperStep(String hopperFilePath) {
		this.jatkFilePath = hopperFilePath;
		currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
	}



	public boolean isLocal() {
		return !jatkFilePath.startsWith("http");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.Step#analyse()
	 */
	@Override
	//	public Verdict analyse(StatusBar statusbar,String profileName, IAnalysisMonitor monitor)
	public Verdict analyse(  IAnalysisMonitor monitor)
	throws LicenceException {
		try {
			check();
			init();

		} catch (Alert a) {
			this.outFilePath = null;
			this.verdict = Verdict.SKIPPED;
			this.skippedMessage = a.getMessage().trim();
			newLastAnalysisResult(new HopperStepAnalysisResult(
					getFlashFileDigest(), outFilePath, getFlashFileName(),
					Calendar.getInstance(), verdictAsString.get(verdict), null,
					null, null, Configuration.getVersion()));
			return verdict;
		}
		String tempDir = Platform.TMP_DIR;
		XMLOutput jatkResults = new XMLOutput(tempDir, "jatkresults");
		Element flashResultsElem = jatkResults.root();
		flashResultsElem.addAttribute("flashfile", jatkFilePath);
		Calendar cal = new GregorianCalendar();
		String currentDate = cal.get(Calendar.DAY_OF_MONTH) + "."
		+ cal.get(Calendar.MONTH) + "." + cal.get(Calendar.YEAR);
		flashResultsElem.addAttribute("date", currentDate);
		flashResultsElem.addAttribute("matosversion", Configuration.getVersion());
		try {
			// on lance le test
			{
				Thread.sleep(4000);
				launchtest( );
			}




			// anaflashPhase.run();
			// //
			// //

		} catch (Exception e) {
			Out.log.println("Problem in analysis of the following hopper test: "
					+ jatkFilePath);
			e.printStackTrace();
		}
		String outFolder = this.outFilePath + File.separator + "report.html";

		jatkResults.generate();

		File reportFile = null;
		if (outFolder != null) {
			reportFile = new File(outFolder);
		} else {
			reportFile = new File(tempDir, "report.html");
		}		
		try {
			//
			// verdict = reportGenerator.generateResult();
		} catch (Exception e) {
			e.printStackTrace();
			Alert.raise(e, "Problem when generating results");
		}
		outFolder = reportFile.getAbsolutePath();
		//	this.verdict = verdict;

		newLastAnalysisResult(new HopperStepAnalysisResult(getFlashFileDigest(),
				outFolder, getFlashFileName(), Calendar.getInstance(),
				verdictAsString.get(verdict), null, null, null, Configuration
				.getVersion()));
		return verdict;
	}
	private void displayRealTimeGraph() {
		boolean isrealtime =Boolean.valueOf(Configuration.getProperty(Configuration.REALTIMEGRAPH, "true"));

		if(!(AutomaticPhoneDetection.getInstance().isNokia()) &&isrealtime)
		{
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					currentPhone.addTcpdumpLineListener(JaTKCharts);
					realtime = new RealtimeGraph(JaTKCharts );
					realtime.setVisible(true);
					if (currentPhone.isDeviceRooted() && Boolean.valueOf(Configuration.getProperty(Configuration.NETWORKMONITOR, "false"))){
						realtime.addUrlMarkerCheckBox();
					}
				}
			});
		}
		return;
	}

	private void stopRealTimeGraph() {
		boolean isrealtime =Boolean.valueOf(Configuration.getProperty(Configuration.REALTIMEGRAPH, "true"));
		if (realtime!=null && !AutomaticPhoneDetection.getInstance().isNokia()&&isrealtime)
		{
			realtime.close();
		}
		realtime=null;
	}

	public void launchtest()
	{

		Logger.getLogger(this.getClass() ).debug("outFilePath start"+outFilePath);		
		Logger.getLogger(this.getClass() ).debug("Random Test Paramaters:");
		Enumeration<String> paramKeys = hopperStepParam.keys();
		while(paramKeys.hasMoreElements()) {
			String key = paramKeys.nextElement();
			Logger.getLogger(this.getClass() ).debug("\tParam "+key+" = "+hopperStepParam.get(key));
		}

		Thread t = new Thread() {
			public void run() {

				//get test filename
				String tstfile = jatkFilePath;

				//get UID and TestName
				PhoneInterface phone = AutomaticPhoneDetection.getInstance().getDevice();
				if ( phone.getType() == PhoneInterface.TYPE_SE )
				{
					String[] elements = tstfile.split(",");
					String AppName=elements[0]+elements[2];
					AppName =AppName.replace(" ", "_");
					AppName =AppName.replace(".", "_");
					Logger.getLogger(this.getClass() ).debug("Name: "+AppName);
					outFilePath = outFilePath + File.separator + AppName;
					tstfile=elements[0];
				}	   
				else if (phone.getType() == PhoneInterface.TYPE_ANDROID )
				{
					outFilePath = outFilePath + File.separator + tstfile;
				}		
				else if (phone.getType() == PhoneInterface.TYPE_S60 )
				{
					Logger.getLogger(this.getClass() ).debug("outpath3 :"+outFilePath);
					String[] temp =tstfile.split(":");
					outFilePath = outFilePath + File.separator + temp[0].replace(" ", "_");
				}

				outFilePath = outFilePath.replace(".", "_");
				outFilePath = outFilePath.replace(",", "_");
				File outputdirF =new File(outFilePath);
				if(!outputdirF.exists())
					if(!outputdirF.mkdir())
						Logger.getLogger(this.getClass() ).debug("Can't Create dir "+outputdirF.getPath());
				verdict = Verdict.NONE;

				// LaunchJATK  =  new LaunchJATK();
				LaunchJATK exec =  
					new LaunchJATK(outFilePath,JATKpath,tstfile,tstfile,LaunchJATK.PDF_TYPE);
				Campaign.setLaunchExec(exec);
				
				JaTKCharts = new CreateGraph();
				String xmlconfilepath=getXmlfilepath();
				currentxmlfilepath=xmlconfilepath;
				boolean empty = JaTKCharts.createPerfGraphsAndMarkers(xmlconfilepath);
				JaTKCharts.createEmptyDataset();
				JaTKCharts.initializeTimeAxis();
				mapPerfGraph = JaTKCharts.getMapPerfGraph();
				mapAction = JaTKCharts.getMapAction();

				exec.setMapPerfGraph(mapPerfGraph);
				exec.setMapAction(mapAction);
				try {
					if(!empty)
						displayRealTimeGraph();

					if(exec.launchRandomTest(xmlconfilepath, hopperStepParam).equals(PhoneInterface.STATUS_PASSED))
						verdict = Verdict.PASSED;
					else
						verdict = Verdict.FAILED;
						    
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PhoneException pe) {				
					verdict = Verdict.TESTFAILED;
				}
				stopRealTimeGraph();
				exec.stopExecution();
				exec=null;
				Logger.getLogger(this.getClass() ).debug("End of Thread Exec JATK");
			}


		};
		t.setName("lanceJATK");
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public boolean copyfile(File newfile, File originalFile) {

		if (originalFile.exists()) {

			if (newfile.exists()) {
				if(!newfile.delete())
					Logger.getLogger(this.getClass() ).debug("Can't delete  "+newfile.getPath());

			}

			// copy file to output dir
			try {
				newfile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			FileChannel in = null; // canal d'entrée
			FileChannel out = null; // canal de sortie

			try {
				// Init
				in = new FileInputStream(originalFile).getChannel();
				out = new FileOutputStream(newfile).getChannel();

				// Copie depuis le in vers le out
				in.transferTo(0, in.size(), out);
			} catch (Exception e) {
				e.printStackTrace(); // n'importe quelle exception
			} finally { // finalement on ferme
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
					}
				}
			}
		}

		return true;

	}

	/**
	 * Initialize this FlashStep so that it is ready to analyse. If it has
	 * already been initialysed, nothing is done.
	 */
	public void init() {
		if (!initialized) {
			if (jatkFilePath != null
					&& (flashFile == null || !flashFile.exists())) {
				String extension = ".tst";

				if (AutomaticPhoneDetection.getInstance().isNokia())
					extension = ".xml";
				if (jatkFilePath.endsWith(extension)) {
					flashFile = Configuration.fileResolver.getFile(jatkFilePath,
							"tmpjatk", extension, login, password, useragent);
				}

			}
			// TODO if .sis extract .swf
			initialized = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.Step#getClone()
	 */
	@Override
	public Object getClone() {
		HopperStep newFlashStep = new HopperStep(jatkFilePath);
		clone(newFlashStep);
		if (flashFile != null) {
			newFlashStep.flashFile = new File(flashFile.getAbsolutePath());
		}
		return newFlashStep;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.corecli.Step#completeExternalToolCommandLine
	 * (java.lang.String)
	 */
	@Override
	public String completeExternalToolCommandLine(String cmdline) {
		String sup_cmdLine = super.completeExternalToolCommandLine(cmdline);
		if (sup_cmdLine != null) {
			cmdline = sup_cmdLine;
		}

		// replace %SWF% by coresponding
		if (cmdline.indexOf("%SWF%") > 0) {
			init(); // just to be sure that files are resolved
			cmdline = cmdline.replaceAll("%SWF%", flashFile.getAbsolutePath());
		} else {
			return null; // not for this Step
		}

		if (cmdline.indexOf("%") > 0) { // unable to complet all...
			return null;
		} else {
			return cmdline;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.Step#getShortName()
	 */
	@Override
	public String getShortName() {
		String shortName = "";
		if (jatkFilePath.endsWith("tst")) {
			shortName = StringUtilities.guessName(jatkFilePath, ".tst");
		} else {
			shortName = StringUtilities.guessName(jatkFilePath, ".sis");
		}
		if (shortName.lastIndexOf(".") != -1) {
			shortName = shortName.substring(0, shortName.lastIndexOf("."));
		}
		return shortName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.Step#save(org.dom4j.Element, int)
	 */
	@Override
	public void save(Element root, int stepNumber) {
		Element anaElem = root.addElement(type);
		anaElem.addAttribute("name", "hopperstep_" + stepNumber);
		anaElem.addAttribute("file", getFlashFilePath());
		if (getLogin() != null) {
			anaElem.addAttribute("login", getLogin());
			anaElem.addAttribute("password", getPassword());
		}
		if (getUseragent() != null) {
			anaElem.addAttribute("useragent", getUseragent());
		}
		if (getXmlfilepath()!= null){
			anaElem.addAttribute("configfile", getXmlfilepath());
		}
		Enumeration<String> paramKeys = hopperStepParam.keys();
		while(paramKeys.hasMoreElements()) {
			String key = paramKeys.nextElement();
			anaElem.addAttribute(key, hopperStepParam.get(key));
		}
	}
	
/*	public void addParam(String key, String value) {
		this.hopperStepParam.put(key, value);
	}
	
	public String getParam(String key) {
		return this.hopperStepParam.get(key);
	}*/
	
	public Hashtable<String,String> getParam() {
		return this.hopperStepParam;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.corecli.Step#writeInCampaign(org.dom4j.Element
	 * )
	 */
	@Override
	public void writeInCampaign(Element root) {
		save(root, (int) System.currentTimeMillis());
	}

	public String getFlashFilePath() {
		return jatkFilePath;
	}

	public void setFlashFilePath(String flashFilePath) {
		this.jatkFilePath = flashFilePath;
		resetInitialysed();
	}

	public String getFlashFileName() {
		if (jatkFilePath == null) {
			return null;
		}
		return jatkFilePath
		.substring(jatkFilePath.lastIndexOf(File.separator) + 1);
	}

	public File getFlashFile() {
		return flashFile;
	}

	/**
	 * Computes a SHA-1 digest of the referenced flash file.
	 * 
	 * @return a SHA-1 digest or null if no file found.
	 */
	public String getFlashFileDigest() {
		String digest = null;
		if (flashFile != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(flashFile);
				digest = Digest.runSHA1(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return digest;
	}
}
