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
 * File Name   : AnalyseTask.java
 *
 * Created     : 03/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui.tasks;

import java.awt.Cursor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.IAnalysisMonitor;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.StatusBar;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class AnalyseTask extends UITask {

	private StatusBar statusBar;
	private Thread thread;
	private int length;
	private boolean all;
	// TODO see if we keep this
	// private int nbAnalysisDone = 0;
	private CheckListTable clt;
	private boolean shouldStop = false;
	private Campaign campaignToAnalyse;

	/**
	 * Task to launch analysis
	 * 
	 * @param statusBar
	 *            a monitor to increment and where to display messages
	 * @param clt
	 *            the concerned checklist table
	 * @param all
	 *            true means analyse all the checklist, false means only
	 *            selected step
	 */
	public AnalyseTask(StatusBar statusBar, CheckListTable clt, boolean all) {
		this.statusBar = statusBar;
		this.clt = clt;
		this.all = all;
		if (all) {
			this.length = clt.getCampaign().size();
		} else {
			this.length = clt.getSelectedCampaign().size();
		}
		thread = new Thread(this);
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.francetelecom.rd.matos.coregui.UITask#run()
	 */
	@Override
	public void run() {
		// Stop automatic detect before Starting test
		AutomaticPhoneDetection.getInstance().pauseDetection();
		boolean stop = false;

		// Mix Script
		// Campaign
		if (Campaign.isExecuteloop())
			clt.mixAll();
		int loop = 1;
		if (Campaign.isExecuteloop())
			loop = Campaign.getLoop();

		Campaign.setFirstloop(true);
		for (int i = 0; i < loop && !stop; i++) {
			if (Campaign.isExecuteloop()) {
				clt.mixAll();
				Campaign.setTemploop(i);
				Logger.getLogger(this.getClass()).debug("loop:" + i);
			}
			CoreGUIPlugin.mainFrame.enableUserActions(false);
			CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			for (int j = 0; j < CoreGUIPlugin.guiCommons.size(); j++) {
				CoreGUIPlugin.guiCommons.get(j).disableButtonsButStop();
			}
			CoreGUIPlugin.mainFrame.disableButtonsButStop();
			statusBar.setLength(length);
			// Yvain statusBar.setMessage("Analysing " + length +
			// " step(s)...");
			if (all) {
				campaignToAnalyse = clt.getCampaign();
			} else {
				campaignToAnalyse = clt.getSelectedCampaign();
			}
			doAnalysis();

			// Yvain statusBar.clearJob(nbAnalysisDone + " step(s) analysed.");

			CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			CoreGUIPlugin.mainFrame.enableUserActions(true);
			stop = CoreGUIPlugin.mainFrame.statusBar.isStop();
		}
		// Start Auto detect after en of Test list
		AutomaticPhoneDetection.getInstance().resumeDetection();
	}

	public void doAnalysis() {
		try {

			// prepare outputs
			// int i=1;
			// boolean exist = true;
			/*
			 * Yvain while (exist){ MatosGUI.destDir = new
			 * File(MatosGUI.outputDir
			 * +File.separator+"AnalysesResults"+File.separator+"Results"+i); //
			 * MatosGUI.destDir = new
			 * File(MatosGUI.outputDir+File.separator+"Results"+i); if
			 * (MatosGUI.destDir.exists()){ i++; }else{ exist = false; } }
			 */

			MatosGUI.destDir = new File(MatosGUI.outputDir);
			// perform analysis
			analyseCheckListCamp(campaignToAnalyse, MatosGUI.destDir, new IAnalysisMonitor() {

				public boolean isStop() {
					shouldStop = statusBar.isStop();
					return shouldStop;
				}

				public void notifyAllAnalysisDone() {
					// TODO DB update check-list
					/*
					 * if (CoreGUI.configuration.bool(Configuration.
					 * allowRetrievingPreviousResults)) { int length =
					 * clt.table.getRowCount(); if (length>0) {
					 * UpdateCheckListTableTask task = new
					 * UpdateCheckListTableTask(statusBar, clt, length); // wait
					 * for end of task try { task.thread.join(); } catch
					 * (InterruptedException e) { e.printStackTrace(Out.log); }
					 * clt.manageEnableButton(); } } else {
					 * clt.manageEnableButton(); }
					 */
					CoreGUIPlugin.mainFrame.updateButtons();
				}

				public void notifyStepAnalysed(Step step) {
					// check if should stop
					shouldStop = statusBar.isStop();
					// update GUI
					try {
						SwingUtilities.invokeAndWait(new VerdictUpdater(step));
					} catch (InterruptedException e) {
						Logger.getLogger(this.getClass()).error(e);
					} catch (InvocationTargetException e) {
						Logger.getLogger(this.getClass()).error(e);
					}

					statusBar.increment();
					// nbAnalysisDone++;
				}
			});
		} catch (Alert a) {
			Logger.getLogger(this.getClass()).error(a);
		}

	}

	/**
	 * Utility class to perform GUI update a la invokeLater
	 */
	class VerdictUpdater implements Runnable {
		private Step step;

		public VerdictUpdater(Step step) {
			this.step = step;
		}

		public void run() {
			updateVerdict(step);
		}
	}

	/**
	 * Updates a step in a check list table and centers the table on this step
	 * 
	 * @param step
	 *            the step to use to update
	 */
	private void updateVerdict(Step step) {

		String outFilePath = step.getOutFilePath();

		if ((outFilePath != null) && (step.getOutFilePath().indexOf(File.separator) == -1)) {
			outFilePath = MatosGUI.destDir.getAbsolutePath() + File.separator
					+ step.getOutFilePath();
		}
		step.setOutFilePath(outFilePath);

		try {
			clt.updateStep(step);
		} catch (Alert e) {
			Logger.getLogger(this.getClass()).error(e);;
		}
	}

	/**
	 * Execute the analysis of a campaign from a check list.
	 * 
	 * @param campaign
	 *            The campaign to analyse.
	 * @param destDir
	 *            The directory for report output.
	 * @param mon
	 *            an analysis monitor to informed about the analysis progression
	 *            and status. Can be null.
	 */
	public void analyseCheckListCamp(Campaign campaign, File destDir, IAnalysisMonitor mon)
			throws Alert {
		campaign.analyse(destDir, mon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.francetelecom.rd.matos.coregui.IProgressMonitor#increment(java.lang
	 * .String)
	 */
	public void increment(String message) {
		statusBar.increment(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.francetelecom.rd.matos.coregui.IProgressMonitor#setMessage(java.lang
	 * .String)
	 */
	public void setMessage(String message) {
		statusBar.setMessage(message);
	}

	/**
	 * @return the shouldStop
	 */
	public boolean isShouldStop() {
		return shouldStop;
	}

}
