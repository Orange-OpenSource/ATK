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
 * File Name   : Step.java
 *
 * Created     : 16/02/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.corecli;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.dom4j.Element;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;

/**
 * The Step class represents a step in the checklist.
 * 
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public abstract class Step {

	/**
	 * Name of the used security profile.
	 */

	protected String outFilePath = null;
	protected Verdict verdict = Verdict.NONE;
	protected Verdict userVerdict = Verdict.NONE;
	protected Verdict screenshotVerdict = Verdict.NONE;

	protected String skippedMessage = "";
	protected String userComment = "";

	protected final static String type = "step";

	/**
	 * List of authorized URLs.
	 */
	protected String httpAuthorized = "";

	protected boolean initialized = false;
	private String xmlfilepath = null;
	public static String currentxmlfilepath = null;
	protected String profilePath = null;

	/** login&password to use to download files */
	protected String login = "";
	protected String password = "";
	/** User-Agent to use to download files */
	protected String useragent = "";

	protected String tempDir = null;

	// CID attributes
	protected int id_cid = -1;
	protected int id_location = -1;
	protected String cidName = null;
	protected String sms = null;
	protected String service = null;
	protected String version = null;
	protected String terminal = null;
	/** last analysis result */
	private StepAnalysisResult lastAnalysisResult = null;
	private String configurationPath;

	/** Definition of test verdicts */
	public enum Verdict {
		PASSED, FAILED, INITFAILED, TESTFAILED, SKIPPED, // something goes wrong
															// during analysis
		NONE; // not yet analysed
	}

	/** Verdicts as <code>String</code> */
	public static Map<Verdict, String> verdictAsString = new HashMap<Verdict, String>();
	{
		verdictAsString.put(Verdict.PASSED, "Passed");
		verdictAsString.put(Verdict.FAILED, "Failed");
		verdictAsString.put(Verdict.SKIPPED, "Skipped");
		verdictAsString.put(Verdict.INITFAILED, "Init Failed");
		verdictAsString.put(Verdict.TESTFAILED, "Test Failed");

		verdictAsString.put(Verdict.NONE, "");
	}

	/** Verdicts from <code>String</code> */
	public static Map<String, Verdict> verdictFromString = new HashMap<String, Verdict>();
	{
		verdictFromString.put("Passed", Verdict.PASSED);
		verdictFromString.put("Failed", Verdict.FAILED);
		verdictFromString.put("Skipped", Verdict.SKIPPED);
		verdictFromString.put("", Verdict.NONE);
		verdictFromString.put("Init Failed", Verdict.INITFAILED);
		verdictFromString.put("Test Failed", Verdict.TESTFAILED);

		verdictFromString.put("None", Verdict.NONE);
	}

	/**
	 * Get back the last analysis result.
	 * 
	 * @return the last analysis result
	 */
	public StepAnalysisResult getLastAnalysisResult() {
		return lastAnalysisResult;
	}

	/**
	 * Declares a new analysis result. Notify registered analysis results
	 * managers
	 */
	// TODO: verify
	public void newLastAnalysisResult(StepAnalysisResult res) {
		lastAnalysisResult = res;
		/*
		 * List<IAnalysisResultsManager> arManagers =
		 * Matos.getInstance().getAnalysisResultsManagers(); for
		 * (IAnalysisResultsManager arManager : arManagers) {
		 * arManager.notify(this, res); }
		 */
	}

	/**
	 * Update analysis result. Do not Notify registered analysis results
	 * managers
	 */
	public void updateLastAnalysisResult(StepAnalysisResult res) {
		lastAnalysisResult = res;

		outFilePath = res.getReportPath();
		verdict = verdictFromString.get(res.getVerdict());
		userVerdict = verdictFromString.get(res.getUser_verdict());
		skippedMessage = res.getReason();
		userComment = res.getComment();
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void resetInitialysed() {
		initialized = false;
	}

	public abstract void init();

	/**
	 * Tells wheter this step reference file located on this computer or distant
	 * ones
	 * 
	 * @return true iff files are on this computer
	 */
	public abstract boolean isLocal();

	/**
	 * Check licence validity.
	 * 
	 * @throws LicenceException
	 *             if a problem is detected with the licence.
	 */
	protected void check() {

	}

	/**
	 * Run analysis on this step. Analysis kind is step dependent
	 * 
	 * @param profileName
	 * @param monitor
	 * @return analysis verdict
	 * @throws LicenceException
	 *             if a problem is detected with the licence.
	 */
	// public abstract Verdict analyse(StatusBar statusbar,String profileName,
	// IAnalysisMonitor monitor) throws LicenceException;
	public abstract Verdict analyse(IAnalysisMonitor monitor);

	/**
	 * Complete the specified external tool command line with step's
	 * specificities.
	 * 
	 * @param cmdline
	 *            the command line of the external tool, to be completed with
	 *            step information
	 * @return a 'ready to launch' command line or null if it cannot be
	 *         fullfilled with this Step's information
	 */
	public String completeExternalToolCommandLine(String cmdline) {
		// replace %REP% by coresponding
		if (cmdline.indexOf("%REP%") > 0) {
			if (lastAnalysisResult != null) {
				cmdline = cmdline.replaceAll("%REP%", lastAnalysisResult.reportPath);
				return cmdline;
			} else {
				throw new Alert("No report available");
			}
		} else {
			return cmdline;
		}
	}

	/**
	 * Add the step in the given campaign xml document. Format to use is Step
	 * dependent.
	 * 
	 * @param root
	 *            the root element in the document (usualy
	 *            <code>camp.root()</code>).
	 */
	public abstract void writeInCampaign(Element root);

	public abstract String getFlashFilePath();
	public abstract void setFlashFilePath(String flashFilePath);

	public boolean readfromelement(Element element, Step step, String flashFilePath) {
		Boolean testFileExist = true;
		if (step.getFlashFilePath() != null && step.getFlashFilePath().length() != 0) {
			step.setFlashFilePath(flashFilePath.trim());
			if (step.getFlashFilePath().startsWith("http:")) {
				try {
					new URL(step.getFlashFilePath());
				} catch (MalformedURLException e) {
					Alert.raise(e, "Campaign invalid URL: " + step.getFlashFilePath());
				}
			} else {
				File file = new File(flashFilePath);
				if (!file.exists()) {
					testFileExist = false;
				}
			}
		}
		step.setLogin(element.attributeValue("login"));
		step.setPassword(element.attributeValue("password"));
		step.setUseragent(element.attributeValue("useragent"));

		if (null != element.attributeValue("configfile")) {
			File configfile = new File(element.attributeValue("configfile"));
			if (!configfile.exists()) {
				// TODO: add test if file doesn't exist.
				JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, "The configuration file "
						+ configfile + " does not exist. It will be ignored.");
			} else {
				step.setXmlfilepath(configfile.toString());
			}
		}
		return testFileExist;
	}

	protected boolean isEmpty(String s) {
		return (s == null) || (s.length() == 0);
	}

	public boolean hasHttpAuthorized() {
		return !isEmpty(httpAuthorized);
	}

	public String getHttpAuthorized() {
		return httpAuthorized;
	}

	public void setHttpAuthorized(String httpAuthorized) {
		this.httpAuthorized = httpAuthorized;
	}

	/**
	 * @return the cidName
	 */
	public String getCidName() {
		return cidName;
	}

	/**
	 * @param cidName
	 *            the cidName to set
	 */
	public void setCidName(String cidName) {
		this.cidName = cidName;
	}

	/**
	 * @return the id_cid
	 */
	public int getId_cid() {
		return id_cid;
	}

	/**
	 * @param id_cid
	 *            the id_cid to set
	 */
	public void setId_cid(int id_cid) {
		this.id_cid = id_cid;
	}

	/**
	 * @return the id_location
	 */
	public int getId_location() {
		return id_location;
	}

	/**
	 * @param id_location
	 *            the id_location to set
	 */
	public void setId_location(int id_location) {
		this.id_location = id_location;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the sms
	 */
	public String getSms() {
		return sms;
	}

	/**
	 * @param sms
	 *            the sms to set
	 */
	public void setSms(String sms) {
		this.sms = sms;
	}

	/**
	 * @return the terminal
	 */
	public String getTerminal() {
		return terminal;
	}

	/**
	 * @param terminal
	 *            the terminal to set
	 */
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Builds a clone copy of this <code>Step</code>
	 * 
	 * @return a copy of this <code>Step</code>
	 */
	public abstract Object getClone();

	/**
	 * Save the step in XML file
	 * 
	 * @param root
	 */
	public abstract void save(Element root, int stepNumber);

	/** Common part for clone. To be completed by subclasses */
	public void clone(Step newCmdLine) {
		newCmdLine.httpAuthorized = httpAuthorized;
		newCmdLine.initialized = initialized;
		newCmdLine.configurationPath = configurationPath;
		newCmdLine.profilePath = profilePath;
		newCmdLine.login = login;
		newCmdLine.password = password;
		newCmdLine.useragent = useragent;
		newCmdLine.tempDir = tempDir;
		newCmdLine.outFilePath = outFilePath;
		newCmdLine.verdict = verdict;
		newCmdLine.xmlfilepath = xmlfilepath;
	}

	public String getOutFilePath() {
		return outFilePath;
	}

	public void setOutFilePath(String outFilePath) {
		this.outFilePath = outFilePath;
	}

	public Verdict getVerdict() {
		return verdict;
	}

	public void setVerdict(Verdict verdict) {
		this.verdict = verdict;
	}

	public Verdict getUserVerdict() {
		return userVerdict;
	}

	public void setUserVerdict(Verdict userVerdict) {
		this.userVerdict = userVerdict;
	}

	public Verdict getScreenshotVerdict() {
		return screenshotVerdict;
	}

	public void setScreenshotVerdict(Verdict screenshotVerdict) {
		this.screenshotVerdict = screenshotVerdict;
	}

	public String getSkippedMessage() {
		return skippedMessage;
	}

	public void setSkippedMessage(String skippedMessage) {
		this.skippedMessage = skippedMessage;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public void setXmlfilepath(String xmlfilepath) {
		this.xmlfilepath = xmlfilepath;
	}

	public String getXmlfilepath() {
		return xmlfilepath;
	}

	/**
	 * @return a short name for the step
	 */
	public abstract String getShortName();

}
