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
 * File Name   : MixScriptStep.java
 *
 * Created     : 23/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaMixScript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.IAnalysisMonitor;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.utils.Digest;
import com.orange.atk.atkUI.corecli.utils.StringUtilities;
import com.orange.atk.atkUI.corecli.utils.XMLOutput;
import com.orange.atk.graphAnalyser.CreateGraph;
import com.orange.atk.graphAnalyser.GraphMarker;
import com.orange.atk.graphAnalyser.PerformanceGraph;
import com.orange.atk.launcher.LaunchJATK;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.platform.Platform;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class MixScriptStep extends Step {

	/**
	 * Path to the flash file to analyse.
	 */
	private String mixScriptFilePath;
	private File flashFile;
	private File realFlashFile;
	private String jATKpath = "C:" + File.separator + "Program Files" + File.separator + "JATK";

	public static final String TYPE = "mixscript";

	/**
	 * Builds a flash step with the path to the flash file and the name of the
	 * security profile
	 * 
	 * @param flashFilePath
	 *            Path to the flash file
	 * @param securityProfileName
	 *            Name of the security profile
	 */
	public MixScriptStep(String flashFilePath, File realFlashFile) {
		this.mixScriptFilePath = flashFilePath;
		this.realFlashFile = realFlashFile;
	}

	public boolean isLocal() {
		return !mixScriptFilePath.startsWith("http");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.Step#analyse()
	 */
	@Override
	// public Verdict analyse(StatusBar statusBar, String profileName,
	// IAnalysisMonitor monitor)
	public Verdict analyse(IAnalysisMonitor monitor) {

		try {
			check();
			init();

		} catch (Alert a) {
			this.outFilePath = null;
			this.verdict = Verdict.SKIPPED;
			this.skippedMessage = a.getMessage().trim();
			newLastAnalysisResult(new MixScriptStepAnalysisResult(getFlashFileDigest(),
					outFilePath, getFlashFileName(), Calendar.getInstance(),
					verdictAsString.get(verdict), null, null, null, Configuration.getVersion()));
			return verdict;
		}
		String tempDir = Platform.TMP_DIR;
		XMLOutput mixScriptResults = new XMLOutput(tempDir, "mixScriptresults");
		Element flashResultsElem = mixScriptResults.root();
		flashResultsElem.addAttribute("flashfile", mixScriptFilePath);
		Calendar cal = new GregorianCalendar();
		String currentDate = cal.get(Calendar.DAY_OF_MONTH) + "." + cal.get(Calendar.MONTH) + "."
				+ cal.get(Calendar.YEAR);
		flashResultsElem.addAttribute("date", currentDate);
		flashResultsElem.addAttribute("matosversion", Configuration.getVersion());

		try {
			// on lance le test
			verdict = launchtest();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(
					"Problem in analysis of the following flash file: " + mixScriptFilePath, e);
		}
		mixScriptResults.generate();

		File reportFile = null;
		if (outFilePath != null) {
			reportFile = new File(outFilePath);
		} else {
			reportFile = new File(tempDir, "report.html");
		}

		this.outFilePath = reportFile.getAbsolutePath();

		newLastAnalysisResult(new MixScriptStepAnalysisResult(getFlashFileDigest(), outFilePath,
				getFlashFileName(), Calendar.getInstance(), verdictAsString.get(verdict), null,
				null, null, Configuration.getVersion()));
		return verdict;
	}

	public Verdict launchtest() {

		// get test filename
		File file = new File(mixScriptFilePath);
		String testfilename = file.getName();
		testfilename = testfilename.replace(".tst", "");
		testfilename = testfilename.replace(".xml", "");
		int loop = Campaign.getTemploop();

		outFilePath = outFilePath + Platform.FILE_SEPARATOR + "MixScript" + Platform.FILE_SEPARATOR
				+ "Loop" + loop + Platform.FILE_SEPARATOR + testfilename;
		File tmpfile = new File(outFilePath);

		if (!tmpfile.exists() && !tmpfile.mkdirs()) {
			Logger.getLogger(this.getClass()).warn("Can't Create dir " + tmpfile.getPath());
		}

		tmpfile = new File(new File(new File(outFilePath).getParent()).getParent()
				+ Platform.FILE_SEPARATOR + "MixScriptExecution.txt");
		if (Campaign.isFirstloop()) {
			if (tmpfile.exists() && !tmpfile.delete()) {
				Logger.getLogger(this.getClass()).warn("Can't Create dir " + tmpfile.getPath());
			}

			Campaign.setFirstloop(false);
		}
		if (!tmpfile.exists()) {
			try {
				tmpfile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Logger.getLogger(this.getClass()).error(e1);
			}
		}

		try {
			PrintStream ps = new PrintStream(new FileOutputStream(tmpfile, true));
			SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			ps.println(spf.format(new Date()) + " Loop " + loop + " " + file.getName());
			ps.flush();
			ps.close();
			ps = null;

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass()).error(e1);
		}

		Verdict verdict = Verdict.NONE;

		LaunchJATK exec = new LaunchJATK(outFilePath, jATKpath, mixScriptFilePath,
				this.realFlashFile.getAbsolutePath(), LaunchJATK.PDF_TYPE);
		Campaign.setLaunchExec(exec);

		if (exec.getCurrentPhone() instanceof DefaultPhone) {
			JOptionPane.showMessageDialog(null, "Can't Detect device");
			return Verdict.INITFAILED;
		}

		// copy test file
		File outputdirF = null;
		String tempoutputdir = outFilePath;
		File filetst = new File(mixScriptFilePath);
		if (realFlashFile.exists()) {
			outputdirF = new File(tempoutputdir);
			if (!outputdirF.exists() && !outputdirF.mkdir()) {
				Logger.getLogger(this.getClass()).warn("Can't make dir " + outputdirF.getPath());
			}
			Logger.getLogger(this.getClass()).debug("outputdirF" + outputdirF);
			Logger.getLogger(this.getClass()).debug("testfilename" + testfilename);
			Logger.getLogger(this.getClass()).debug("filetst.getName()" + filetst.getName());
			Logger.getLogger(this.getClass()).debug(
					"all" + outputdirF + Platform.FILE_SEPARATOR + filetst.getName());

			Logger.getLogger(this.getClass()).debug(
					"try to copy " + mixScriptFilePath + " to " + outputdirF
							+ Platform.FILE_SEPARATOR + filetst.getName());
			copyfile(new File(outputdirF + Platform.FILE_SEPARATOR + filetst.getName()),
					realFlashFile);

		}
		CreateGraph jatkCharts = new CreateGraph();
		String xmlconfilepath = getXmlfilepath();
		currentxmlfilepath = this.getXmlfilepath();
		Logger.getLogger(this.getClass()).debug("xmlconfilepath:" + xmlconfilepath);

		jatkCharts.createPerfGraphsAndMarkers(xmlconfilepath);
		jatkCharts.createEmptyDataset();
		jatkCharts.initializeTimeAxis();
		Map<String, PerformanceGraph> mapPerfGraph = jatkCharts.getMapPerfGraph();
		Map<String, GraphMarker> mapAction = jatkCharts.getMapAction();

		exec.setMapPerfGraph(mapPerfGraph);
		exec.setMapAction(mapAction);
		String result = null;
		try {
			result = exec.launchNewTest(xmlconfilepath, false);
		} catch (FileNotFoundException e) {
			// TODO handle exception
			Logger.getLogger(this.getClass()).error(e);
		} catch (PhoneException pe) {
			Logger.getLogger(this.getClass()).error(pe);
		}
		if (result != null) {
			verdict = (result.contains("PASSED")) ? Verdict.PASSED : Verdict.TESTFAILED;
		}
		exec.stopExecution();
		exec = null;
		return verdict;
	}

	public boolean copyfile(File newfile, File originalFile) {

		if (originalFile.exists()) {

			if (newfile.exists() && !newfile.delete()) {
				Logger.getLogger(this.getClass()).debug("Can't delete " + newfile.getPath());
			}

			// copy file to output dir
			try {
				newfile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Logger.getLogger(this.getClass()).error(e1);
			}
			FileChannel in = null;
			FileChannel out = null;

			try {
				// Init
				in = new FileInputStream(originalFile).getChannel();
				out = new FileOutputStream(newfile).getChannel();

				// Copie depuis le in vers le out
				in.transferTo(0, in.size(), out);
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			} finally {
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
			if (mixScriptFilePath != null && (flashFile == null || !flashFile.exists())) {
				String extension = ".tst";

				if (mixScriptFilePath.endsWith(extension)) {
					flashFile = Configuration.fileResolver.getFile(mixScriptFilePath,
							"tmpmixScript", extension, login, password, useragent);
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
		MixScriptStep newFlashStep = new MixScriptStep(mixScriptFilePath, realFlashFile);
		clone(newFlashStep);
		if (flashFile != null) {
			newFlashStep.flashFile = new File(flashFile.getAbsolutePath());
		}
		return newFlashStep;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.Step#completeExternalToolCommandLine
	 * (java.lang.String)
	 */
	@Override
	public String completeExternalToolCommandLine(String cmdline) {
		String cmdToExecute = cmdline;
		String supCmdLine = super.completeExternalToolCommandLine(cmdToExecute);
		if (supCmdLine != null) {
			cmdToExecute = supCmdLine;
		}

		// replace %SWF% by coresponding
		if (cmdToExecute.indexOf("%SWF%") > 0) {
			// just to be sure that files are resolved
			init();
			cmdToExecute = cmdToExecute.replaceAll("%SWF%", flashFile.getAbsolutePath());
		} else {
			// not for this Step
			return null;
		}

		if (cmdToExecute.indexOf('%') > 0) {
			// unable to complet all...
			return null;
		} else {
			return cmdToExecute;
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
		if (mixScriptFilePath.endsWith("tst")) {
			shortName = StringUtilities.guessName(mixScriptFilePath, ".tst");
		} else {
			shortName = StringUtilities.guessName(mixScriptFilePath, ".sis");
		}
		if (shortName.lastIndexOf('.') != -1) {
			shortName = shortName.substring(0, shortName.lastIndexOf('.'));
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
		Element anaElem = root.addElement(TYPE);
		anaElem.addAttribute("name", "MixScriptstep_" + stepNumber);
		anaElem.addAttribute("file", getFlashFilePath());
		if (getLogin() != null) {
			anaElem.addAttribute("login", getLogin());
			anaElem.addAttribute("password", getPassword());
		}
		if (getUseragent() != null) {
			anaElem.addAttribute("useragent", getUseragent());
		}
		if (getXmlfilepath() != null) {
			anaElem.addAttribute("configfile", getXmlfilepath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.Step#writeInCampaign(org.dom4j.Element
	 * )
	 */
	@Override
	public void writeInCampaign(Element root) {
		save(root, (int) System.currentTimeMillis());
	}

	public String getFlashFilePath() {
		return mixScriptFilePath;
	}

	public void setFlashFilePath(String flashFilePath) {
		this.mixScriptFilePath = flashFilePath;
		resetInitialysed();
	}

	public String getFlashFileName() {
		if (mixScriptFilePath == null) {
			return null;
		}
		return mixScriptFilePath.substring(mixScriptFilePath.lastIndexOf(File.separator) + 1);
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
		if (realFlashFile != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(realFlashFile);
				digest = Digest.runSHA1(fis);
			} catch (FileNotFoundException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return digest;
	}

}
