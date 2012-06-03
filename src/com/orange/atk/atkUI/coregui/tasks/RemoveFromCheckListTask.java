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
 * File Name   : RemoveFromCheckListTask.java
 *
 * Created     : 27/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui.tasks;

import java.awt.Cursor;

import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.StatusBar;


/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class RemoveFromCheckListTask extends UITask {

	private StatusBar statusBar;
	private CheckListTable clTable;
	private int length;
	private MatosGUI mainFrame = CoreGUIPlugin.mainFrame;

	/**
	 * A task to remove selected steps from the checklist.
	 * @param dialog the UI dialog to display while the task is performed.
	 * @param clTable the checklist table.
	 */
	public RemoveFromCheckListTask(StatusBar statusBar, CheckListTable clTable, int length) {
		this.statusBar = statusBar;
		this.clTable = clTable;
		this.length = length;
		statusBar.setLength(length);
		new Thread(this).start();
	}

	public boolean isStop() {
		return statusBar.isStop();
	}

	/* (non-Javadoc)
	 * @see com.francetelecom.rd.matos.coregui.UITask#run()
	 */
	@Override
	public void run() {
		// prevent multiples actions
		mainFrame.enableUserActions(false);
		mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		setMessage("removing "+length+" step(s)...");
		
		clTable.removeSelection(this, true);
		
		mainFrame.updateContentTabsTitle();
		statusBar.clearJob(length + " step(s) removed");
		mainFrame.enableUserActions(true);
		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
