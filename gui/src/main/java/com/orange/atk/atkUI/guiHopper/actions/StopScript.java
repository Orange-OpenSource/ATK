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
 * File Name   : StopScript.java
 *
 * Created     : 18/05/2010
 * Author(s)   : Gurvan LE QUELLENEC
 */

package com.orange.atk.atkUI.guiHopper.actions;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.actions.MatosAbstractAction;
import com.orange.atk.launcher.LaunchJATK;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

@SuppressWarnings("serial")
public class StopScript extends MatosAbstractAction {
	/** A flag indicating the need to stop the task. */
	private boolean shouldStop = false;

	/**
	 * @param name
	 * @param icon
	 * @param shortDescription
	 */
	public StopScript(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	public void actionPerformed(ActionEvent arg0) {
		LaunchJATK exec=null;	
		if(Campaign.getLaunchExec()!=null)
		{
			exec=Campaign.getLaunchExec();
			CoreGUIPlugin.mainFrame.statusBar.setStop();
			this.setEnabled(false);
			//Stop test
			if(exec.getCurrentPhone()!=null)
				Logger.getLogger(this.getClass() ).debug("Stop JATK softly ");
				exec.cancelExecution();
		} else
		{
			Logger.getLogger(this.getClass() ).debug("Can't Stop JATK softly");

		}
		//Launch Autodetect after a Cancel
		AutomaticPhoneDetection.getInstance().resumeDetection();
	}
	
	/**
	 * Raises a flag indicating the task should stop as soon as possible.
	 */
	public void setStop() {
		shouldStop = true;
		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		CoreGUIPlugin.mainFrame.enableUserActions(true);
	}
	
	/**
	 * Tests the stop flag.
	 * @return true if tha task have to stop as soon as possible, false otherwise.
	 */
	public boolean isStop() {
		return shouldStop;
	}

}
