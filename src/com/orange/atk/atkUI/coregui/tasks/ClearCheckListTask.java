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
 * File Name   : ClearCheckListTask.java
 *
 * Created     : 02/05/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.coregui.tasks;

import java.awt.Cursor;
import java.util.ArrayList;

import com.orange.atk.atkUI.coregui.AnalysisGUICommon;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.StatusBar;


/**
 * This is a task that clears the checklist table.
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class ClearCheckListTask extends UITask {

	private StatusBar statusBar;
	private ArrayList<AnalysisGUICommon> analysisPlugin;
	private int length;

	/**
	 * A task to clear the checklist table.
	 * @param dialog the UI dialog to display while the task is performed.
	 * @param clTable the checklist table.
	 */
 	public ClearCheckListTask(StatusBar statusBar, ArrayList<AnalysisGUICommon> analysisPlugin, int length) {
 		this.statusBar = statusBar;
		this.analysisPlugin = analysisPlugin;
		this.length = length;
		statusBar.setLength(length);
		new Thread(this).start();
	}

	/* (non-Javadoc)
	 * @see com.francetelecom.rd.matos.coregui.UITask#run()
	 */
	@Override
	public void run() {
		CoreGUIPlugin.mainFrame.enableUserActions(false);
		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		statusBar.setMessage("Removing "+length+" step(s)...");
		for (AnalysisGUICommon ana : analysisPlugin) {
			ana.getCheckListTable().clear();
		}
		CoreGUIPlugin.mainFrame.updateContentTabsTitle();
		// clear job info in statusbar
		statusBar.clearJob(length +" step(s) removed");
		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		CoreGUIPlugin.mainFrame.enableUserActions(true);
	}

	/* (non-Javadoc)
	 * @see com.francetelecom.rd.matos.coregui.IProgressMonitor#increment(java.lang.String)
	 */
	public void increment(String message) {
		statusBar.increment(message);
	}

	/* (non-Javadoc)
	 * @see com.francetelecom.rd.matos.coregui.IProgressMonitor#setMessage(java.lang.String)
	 */
	public void setMessage(String message) {
		statusBar.setMessage(message);
	}

}
