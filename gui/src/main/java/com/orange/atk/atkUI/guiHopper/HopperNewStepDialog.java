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
 * File Name   : HopperNewStepDialog.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper;

import com.orange.atk.atkUI.anaHopper.HopperStep;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HopperNewStepDialog extends HopperStepDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * Builds a new step dialog
	 *
	 */
	public HopperNewStepDialog() {
		super();

		this.setTitle("New Script");
		this.setVisible(true);
	}

	/**
	 * Verifies the filled fields and adds a row in the flash table.
	 *
	 */
	protected void action() {
	  verifyAndInitialize();
		if (!fileError){
			HopperStep cmdLine = new HopperStep(testName);
			if(authenticationPanel.getLogin().length()>0){
				cmdLine.setLogin(login);
				cmdLine.setPassword(password);
			}
			if (authenticationPanel.getUserAgent().trim().length()>0) {
				cmdLine.setUseragent(user_agent);
			}
			cmdLine.init();
			clt.addRow(cmdLine, -1, true, true);
			mainFrame.setModified(true);
			mainFrame.updateContentTabsTitle();
			mainFrame.updateButtons();

		}
	}


}
