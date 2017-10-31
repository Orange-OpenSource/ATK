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
 * File Name   : JatkStep.java
 *
 * Created     : 23/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaScript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
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
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
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
public class JatkStep extends Step {

	/**
	 * Path to the flash file to analyse.
	 */
	public String getJatktestFilePath() {
		return jatktestFilePath;
	}

	private static PhoneInterface currentPhone = null;

	private String jatktestFilePath;
	private File flashFile;
	private File realFlashFile;
	private CreateGraph jatkCharts;
	private RealtimeGraph realtime;

	public final static String TYPE = "jatk";

	/**
	 * Builds a flash step with the path to the flash file and the name of the
	 * security profile
	 * 
	 * @param flashFilePath
	 *            Path to the flash file
	 */
	public JatkStep(String flashFilePath, File realFlashFile) {
		this.jatktestFilePath = flashFilePath;
		this.realFlashFile = realFlashFile;
		currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
	}

	public boolean isLocal() {
		return !jatktestFilePath.startsWith("http");
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
			newLastAnalysisResult(new JatkStepAnalysisResult(getFlashFileDigest(), outFilePath,
					getFlashFileName(), Calendar.getInstance(), verdictAsString.get(verdict), null,
					null, null, Configuration.getVersion()));
			return verdict;
		}
		String tempDir = Platform.TMP_DIR;
		XMLOutput jatkResults = new XMLOutput(tempDir, "jatkresults");
		Element flashResultsElem = jatkResults.root();
		flashResultsElem.addAttribute("flashfile", jatktestFilePath);
		Calendar cal = new GregorianCalendar();
		String currentDate = cal.get(Calendar.DAY_OF_MONTH) + "." + cal.get(Calendar.MONTH) + "."
				+ cal.get(Calendar.YEAR);
		flashResultsElem.addAttribute("date", currentDate);
		flashResultsElem.addAttribute("matosversion", Configuration.getVersion());

		try {
			// init is done each time a test is launch
			verdict = launchtest();
		} catch (Exception e) {
			Logger.getLogger(this.getClass())
				.error("Problem running the following test file: " + jatktestFilePath, e);
		}
		jatkResults.generate();

		File reportFile = null;
		if (outFilePath != null) {
			reportFile = new File(outFilePath);
		} else {
			reportFile = new File(tempDir, "report.html");
		}

		this.outFilePath = reportFile.getAbsolutePath();

		newLastAnalysisResult(new JatkStepAnalysisResult(getFlashFileDigest(), outFilePath,
				getFlashFileName(), Calendar.getInstance(), verdictAsString.get(verdict), null,
				null, null, Configuration.getVersion()));
		return verdict;
	}

	public Verdict launchtest() {
		realtime = null;
		Verdict verdict = Verdict.NONE;

		// set output dir
		File testfile = new File(jatktestFilePath);
		outFilePath = outFilePath + File.separator
				+ testfile.getName().replace(".tst", "").replace(".xml", "");
		LaunchJATK exec = new LaunchJATK(outFilePath, Platform.getInstance().getJATKPath(),
				jatktestFilePath, this.realFlashFile.getAbsolutePath(), LaunchJATK.PDF_TYPE);
		Campaign.setLaunchExec(exec);

		// copy test file to output dir
		if (realFlashFile.exists()) {
			File outputdirF = new File(outFilePath + Platform.FILE_SEPARATOR);
			if (!outputdirF.exists()) {
				if (!outputdirF.mkdirs()) {
					Logger.getLogger(this.getClass()).debug(
							"Can't Create " + outputdirF.getParent());
				}
			}

			copyfile(new File(outFilePath + Platform.FILE_SEPARATOR + testfile.getName()),
					realFlashFile);

		}

		// Create Graph for Real Time
		jatkCharts = new CreateGraph();
		String xmlconfilepath = this.getXmlfilepath();
		currentxmlfilepath = this.getXmlfilepath();
		boolean empty = jatkCharts.createPerfGraphsAndMarkers(xmlconfilepath);
		jatkCharts.createEmptyDataset();
		jatkCharts.initializeTimeAxis();
		Map<String, PerformanceGraph> mapPerfGraph = jatkCharts.getMapPerfGraph();
		Map<String, GraphMarker> mapAction = jatkCharts.getMapAction();
		exec.setMapPerfGraph(mapPerfGraph);
		exec.setMapAction(mapAction);

		// no real time graph on NokiaS60
		if (!empty) {
			displayRealTimeGraph();
		}

		String result = null;

		try {
			verdict = Verdict.PASSED;
			if (!CoreGUIPlugin.mainFrame.statusBar.isStop()) {
				result = exec.launchNewTest(xmlconfilepath, false);
				if (result != null) {
					verdict = (result.contains("PASSED")) ? Verdict.PASSED : Verdict.TESTFAILED;
				}
			} else {
				verdict = Verdict.TESTFAILED;
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error During Execution",
					JOptionPane.ERROR_MESSAGE);
			verdict = Verdict.TESTFAILED;
			Logger.getLogger(this.getClass()).error(e);
		} catch (PhoneException pe) {

			JOptionPane.showMessageDialog(null, pe.getMessage(), "Error During Execution",
					JOptionPane.ERROR_MESSAGE);
			verdict = Verdict.TESTFAILED;
			Logger.getLogger(this.getClass()).error(pe);
		}

		if (exec != null) {
			exec.stopExecution();
		} else {
			verdict = Verdict.TESTFAILED;
		}
		stopRealTimeGraph();
		exec = null;
		return verdict;
	}

	private void displayRealTimeGraph() {
		boolean isrealtime = Boolean.valueOf(Configuration.getProperty(Configuration.REALTIMEGRAPH,
				"true"));

		if (!(AutomaticPhoneDetection.getInstance().isNokia()) && isrealtime) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					currentPhone.addTcpdumpLineListener(jatkCharts);
					realtime = new RealtimeGraph(jatkCharts);
					realtime.setVisible(true);
					if (currentPhone.isDeviceRooted()
							&& Boolean.valueOf(Configuration.getProperty(
									Configuration.NETWORKMONITOR, "false"))) {
						realtime.addUrlMarkerCheckBox();
					}
				}
			});
		}
		return;
	}

	private void stopRealTimeGraph() {
		boolean isrealtime = Boolean.valueOf(Configuration.getProperty(Configuration.REALTIMEGRAPH,
				"true"));
		if (realtime != null && !AutomaticPhoneDetection.getInstance().isNokia() && isrealtime) {
			realtime.close();
		}
		realtime = null;
	}

	public boolean copyfile(File newfile, File originalFile) {
		if (originalFile.exists()) {

			if (newfile.exists()) {
				if (!newfile.delete()) {
					Logger.getLogger(this.getClass()).debug("Can't delete " + newfile.getParent());
				}
			}

			// copy file to output dir
			try {
				newfile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Logger.getLogger(this.getClass()).error(e1);
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
				Logger.getLogger(this.getClass()).error(e); // n'importe quelle
															// exception
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
			if (jatktestFilePath != null && (flashFile == null || !flashFile.exists())) {
				String extension = ".tst";

				if (jatktestFilePath.endsWith(extension)) {
					flashFile = new File(jatktestFilePath);
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
		JatkStep newFlashStep = new JatkStep(jatktestFilePath, realFlashFile);
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
		if (jatktestFilePath.endsWith("tst")) {
			shortName = StringUtilities.guessName(jatktestFilePath, ".tst");
		} else {
			shortName = StringUtilities.guessName(jatktestFilePath, ".sis");
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
		anaElem.addAttribute("name", "flashstep_" + stepNumber);
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
		return jatktestFilePath;
	}

	public String getRealFlashFilePath() {
		return this.realFlashFile.getAbsolutePath();
	}

	public void setFlashFilePath(String flashFilePath) {
		this.jatktestFilePath = flashFilePath;
		resetInitialysed();
	}

	public String getFlashFileName() {
		if (jatktestFilePath == null) {
			return null;
		}
		return jatktestFilePath.substring(jatktestFilePath.lastIndexOf(File.separator) + 1);
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
