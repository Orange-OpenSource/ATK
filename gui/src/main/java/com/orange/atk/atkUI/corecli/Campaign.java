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
 * File Name   : Campaign.java
 *
 * Created     : 16/02/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.corecli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.orange.atk.atkUI.corecli.Step.Verdict;
import com.orange.atk.atkUI.corecli.utils.XMLParser;
import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.launcher.LaunchJATK;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 * A campaign is a kind of Step list.
 * 
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class Campaign extends ArrayList<Step> {

	private static final long serialVersionUID = 1L;

	// Allows to call the read method on specified Campaign object
	// (JavaCampaign, FlashCampaign...)
	public static ArrayList<Campaign> campaignsList = new ArrayList<Campaign>();

	private String name = null;
	private String author = null;
	private String date = null;

	public static boolean isExecute() {
		return execute;
	}

	public static void setExecute(boolean execute) {
		Campaign.execute = execute;
	}

	private static boolean execute = false;
	private static boolean firstloop = true;
	private static int loop = 1;
	public static int getTemploop() {
		return temploop;
	}

	public static void setTemploop(int temploop) {
		Campaign.temploop = temploop;
	}

	private static int temploop = 1;

	private static boolean executeloop = false;

	private String description = null;

	public static LaunchJATK exec;

	public static boolean isExecuteloop() {
		return executeloop;
	}

	public static void setExecuteloop(boolean executeloop) {
		Campaign.executeloop = executeloop;
	}

	public static LaunchJATK getLaunchExec() {

		return Campaign.exec;
	}

	public static void setLaunchExec(LaunchJATK exec) {
		Campaign.exec = exec;
	}
	public void setName(String s) {
		if (!isEmpty(s))
			name = s.trim();
	}

	public void setAuthor(String s) {
		if (!isEmpty(s))
			author = s.trim();
	}

	public void setDate(String s) {
		if (!isEmpty(s))
			date = s.trim();
	}

	public void setDescription(String s) {
		if (!isEmpty(s))
			description = s.trim();
	}

	private boolean isEmpty(String s) {
		return (s == null) || (s.trim().length() == 0);
	}

	public static int getLoop() {
		return loop;
	}

	public static void setLoop(int loop) {
		Campaign.loop = loop;
	}

	/**
	 * Runs the analysis method on each step of this campaign (in sequence)
	 * 
	 * @return the analysis's verdict for this campaign.
	 * @throws LicenceException
	 *             if a problem is detected with the licence.
	 */
	public Verdict analyse() {
		return analyse(null, null);
	}

	/**
	 * Runs the analysis method on each step of this campaign (in sequence)
	 * 
	 * @param profileName
	 * @param destDir
	 *            directory to place generated analysis reports. May be null (in
	 *            this case, the temporary directory will be used)
	 * @param mon
	 *            a monitor that allow to be informed of the analysis's progress
	 *            and to interrupt it.
	 * @return the analysis's verdict for this campaign.
	 * @throws LicenceException
	 *             if a problem is detected with the licence.
	 */
	// public Verdict analyse(StatusBar statusBar,String profileName, File
	// destDir, IAnalysisMonitor mon) throws LicenceException {
	public Verdict analyse(File destDir, IAnalysisMonitor mon) {

		Verdict campVerdict = Verdict.NONE;
		boolean stop = false;
		boolean initFailed = false;

		setExecute(true);
		Iterator<Step> it = this.iterator();
		if (AutomaticPhoneDetection.getInstance().getDevice() instanceof DefaultPhone) {
			JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, "Can't Detect device");
			initFailed = true;
		} else
			if (AutomaticPhoneDetection.getInstance().getDevice().getCnxStatus() != PhoneInterface.CNX_STATUS_AVAILABLE) {
				JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, "Can't Detect device");
				initFailed = true;
			}

		while (it.hasNext() && !stop) {
			Step step = it.next();
			Verdict step_verdict;
			if ((null == step.getXmlfilepath())
					|| (step.getXmlfilepath().contains(CheckListTable.NOT_SELECTED))) {
				JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame,
						"You must select the phone monitoring configuration for the test.");
				initFailed = true;
			}

			if (destDir != null) {
				// step.setOutFilePath(destDir+File.separator+name);
				step.setOutFilePath(destDir.getAbsolutePath());
			}
			// Verdict step_verdict = step.analyse(statusBar,profileName, mon);
			if (!initFailed) {
				step_verdict = step.analyse(mon);
			} else {
				step_verdict = Verdict.INITFAILED;
				step.setVerdict(step_verdict);
			}

			// yvain
			campVerdict = step_verdict;
			switch (campVerdict) {
				case NONE :
					campVerdict = step_verdict;
					break;
				case SKIPPED :
					if (step_verdict == Verdict.FAILED) {
						campVerdict = step_verdict;
					}
					break;
				case INITFAILED :
				case FAILED :
					CoreGUIPlugin.mainFrame.statusBar.setStop();
					break;
				case PASSED :
					if ((step_verdict != Verdict.PASSED) && (step_verdict != Verdict.NONE)) {
						campVerdict = step_verdict;
					}
					break;

				default :
					break;
			}
			if (mon != null) {
				stop = mon.isStop();
				mon.notifyStepAnalysed(step);
			}
		}
		setExecute(false);

		if (mon != null) {
			mon.notifyAllAnalysisDone();
		}
		CoreGUIPlugin.mainFrame.statusBar.stopJob();
		return campVerdict;
	}

	/**
	 * Read a campaign file, and return a simplified representation of what is
	 * to be done (abstraction of a campaign).
	 * 
	 * @param campaignFile
	 *            The campaign file to read.
	 * @return A campaign structure
	 */
	public static Campaign readCampaign(File campaignFile) throws Alert {
		XMLParser campaignParser = new XMLParser(campaignFile, null, null);
		Campaign camp = new Campaign();
		camp.readCampaign(campaignParser);
		return camp;
	}

	/**
	 * Populates this campaign using the given XMLParser
	 * 
	 * @param parser
	 *            the XML parser to use
	 * @return this campaign
	 */
	public Campaign readCampaign(XMLParser parser) {
		Element root = parser.getRoot();
		// parser.getElements(e, name)
		setName(root.attributeValue("name"));
		setAuthor(root.attributeValue("author"));
		setDate(root.attributeValue("date"));
		setDescription(root.attributeValue("description"));

		// Read the different type of campaign
		for (Campaign defCamp : campaignsList) {
			addAll(defCamp.readCampaign(parser));
		}
		return this;
	}

	/**
	 * Initializes the "outputFile" field of the "CmdLine" object with testing
	 * its validity.
	 * 
	 * @param step
	 *            the command line to update
	 * @param stepName
	 *            the name of the step
	 * @param stepNumber
	 *            the number of the step
	 * @throws Alert
	 */
	protected static void initOutputFile(Step step, String stepName, int stepNumber) throws Alert {
		try {
			new URI(stepName);
			step.setOutFilePath(stepName + ".html");
		} catch (URISyntaxException e) {
			Alert.raise(e, "Campaign step number " + stepNumber + ", invalid step name: "
					+ stepName + ". Can't create an output file based on that name.");
		}
	}

	/**
	 * Save current campaign in .mcl file
	 * 
	 * @param clFileName
	 * @throws IOException
	 */
	public static void save(String clFileName, Campaign camp) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("campaign");
		if (camp.getName() != null)
			root.addAttribute("name", camp.getName());
		if (camp.getAuthor() != null)
			root.addAttribute("author", camp.getAuthor());
		if (camp.getDate() != null)
			root.addAttribute("date", camp.getDate());
		if (camp.getDescription() != null)
			root.addAttribute("description", camp.getDescription());
		int stepNumber = 0;
		for (Step step : camp) {
			step.save(root, stepNumber);
			stepNumber++;
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(clFileName), format);
			writer.write(document);
			writer.close();
		} catch (IOException e) {
			Alert.raise(e, "Unable to save check-list in a file.");
		}
	}

	public void movesample(SortedSet<Integer> srcrow, int dest) {
		Campaign temptoadd = new Campaign();
		// Logger.getLogger(this.getClass() ).debug("src"+srcrow.first());
		// Logger.getLogger(this.getClass() ).debug("dest"+dest);
		Iterator<Integer> it = srcrow.iterator();
		int index = 0;
		while (it.hasNext()) {
			int numToRemove = it.next();
			temptoadd.add(this.get(numToRemove + index));
			this.remove(numToRemove + index);
			index--;
		}

		// copy to selected location
		if (dest < srcrow.first()) {
			this.addAll(dest, temptoadd);
		} else {
			this.addAll(dest - srcrow.size() + 1, temptoadd);
		}

	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public static void setFirstloop(boolean firstloop) {
		Campaign.firstloop = firstloop;
	}

	public static boolean isFirstloop() {
		return firstloop;
	}

}
