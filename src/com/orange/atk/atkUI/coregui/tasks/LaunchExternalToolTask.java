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
 * File Name   : LaunchExternalToolTask.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.coregui.tasks;

import java.awt.Cursor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.swing.JOptionPane;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.ExternalTool;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.utils.Out;
import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.StatusBar;


/**
 * This is a task that clears the checklist table.
 * @author moteauni
 *
 */
public class LaunchExternalToolTask extends UITask {

	private StatusBar statusBar;
	private ExternalTool tool;

	/**
	 * A task to run external tool.
	 * @param statusBar 
	 * @param tool the tool to run.
	 */
	public LaunchExternalToolTask(StatusBar statusBar, ExternalTool tool) {
		this.statusBar = statusBar;
		this.tool = tool;
		statusBar.setLength(-1);
		new Thread(this).start();

	}

	/**
	 * Increments completness counter. 
	 */
	public void increment(String message) {
		statusBar.increment(message);
	}
	public void setMessage(String message) {
		statusBar.setMessage(message);
	}

	/**
	 * The task's behaviour.
	 */
	public void run() {
		// prevent multiples actions
		CoreGUIPlugin.mainFrame.enableUserActions(false);
		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		statusBar.setMessage("Launching the " + tool.getName() + " tool...");

		//ExternalTool defaultExternalTool = CoreGUI.configuration.getDefaultExternalTool();
		ExternalTool defaultExternalTool = tool;
		if (defaultExternalTool != null) {
			CheckListTable currentCLT = CoreGUIPlugin.mainFrame.getSelectedAnalysisPane().getCheckListTable();

			int numRow = currentCLT.getTable().getSelectedRow();
			String rowNumber = currentCLT.getValueAt(numRow);
			int numInCampaign = new Integer(rowNumber).intValue()-1;
			Step selectedStep = (Step)currentCLT.getCampaign().get(numInCampaign);

			String application_cmd = null;
			try {
				application_cmd = selectedStep.completeExternalToolCommandLine(tool.getCmdLine());
				if (application_cmd!=null) {
					Out.log.println("");
					Out.log.println("=== Launching " + application_cmd);
					try {
						Process process = Runtime.getRuntime().exec(application_cmd);
						InputStream processErr = process.getErrorStream();
						InputStream processOut = process.getInputStream();
						Thread thread = new MonitorInputStreamThread(processOut);
						thread.start();
						thread = new MonitorInputStreamThread(processErr);
						thread.start();
					} catch (IOException e) {
						String message = "Error when running tool '" + tool.getName()+"'\n";
						message += "The command '" + application_cmd + "' seems invalid\n";
						message += "To check tools configuration: Edit->Configuration... and select the 'External tools' tab";
						e.printStackTrace(Out.log);
						//Out.log.println(message);
						JOptionPane.showMessageDialog(
								CoreGUIPlugin.mainFrame,
								message,
								"Error !",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					String message = "Unable to start the tool '"+tool.getName()+"' with the selected step\n"+
					"Check tool's definition.";
					Out.log.println(message);
					JOptionPane.showMessageDialog(
							CoreGUIPlugin.mainFrame,
							message,
							"Error !",
							JOptionPane.ERROR_MESSAGE);
				}

			} catch (Alert a) {
				Out.log.println(a.getMessage());
				JOptionPane.showMessageDialog(
						CoreGUIPlugin.mainFrame,
						a.getMessage(),
						"Error !",
						JOptionPane.ERROR_MESSAGE);

			}

		} else {
			String message = "No external tool found"; 
			Out.log.println(message);
			JOptionPane.showMessageDialog(
					CoreGUIPlugin.mainFrame,
					message,
					"Error !",
					JOptionPane.ERROR_MESSAGE);
			Out.log.println("No external tool found");
		}

		// clear job info in statusbar
		statusBar.clearJob(""); 

		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		CoreGUIPlugin.mainFrame.enableUserActions(true);
	}

	public static class MonitorInputStreamThread extends Thread {

		private Reader reader;
		private Writer writer;

		public MonitorInputStreamThread(InputStream in) {

			reader = new InputStreamReader(new BufferedInputStream(in));
			writer = new OutputStreamWriter(Out.log);
			setDaemon(true);
		}

		public void run() {

			try {
				int c;
				while ((c = reader.read()) != -1) {
					writer.write(c);
					writer.flush();
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace(Out.log);
			}
			Out.log.println("MonitorInputStreamThread exiting...");
		}
	}

}
