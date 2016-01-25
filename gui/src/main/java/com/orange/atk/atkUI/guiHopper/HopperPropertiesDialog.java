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
 * File Name   : HopperPropertiesDialog.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper;

import javax.swing.JTextField;

import com.orange.atk.atkUI.anaHopper.HopperStep;
import com.orange.atk.atkUI.coregui.CheckListTableModel;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;


/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HopperPropertiesDialog extends HopperStepDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * @param hopperStep
	 */
	public HopperPropertiesDialog(HopperStep hopperStep) {
		super();
		initializeFields(hopperStep);
		this.setTitle("Step properties editor");
		this.setVisible(true);
	}

	protected void initializeFields(HopperStep hopperStep) {
		authenticationPanel.setLogin(login);
		authenticationPanel.setPassword(password);
		authenticationPanel.setUserAgent(user_agent);
		if (hopperStep.getFlashFilePath()!=null && hopperStep.getFlashFilePath().length()!=0) {
			if (hopperStep.getFlashFilePath().startsWith("http:")) {
				authenticationPanel.setEnabled(true);
				fileFilter.setEnabled(false);
			} else {
				fileFilter.setText(hopperStep.getFlashFilePath());
				authenticationPanel.setEnabled(false);
				fileFilter.setEnabled(true);
			}
		}
	}

	protected void updateUrlTF(JTextField textField) {
		if (textField.getText().length() >=8 && textField.getText().startsWith("http://")){
			String url = textField.getText();
			textField.setText(url.substring(7, url.length()));
		}
	}

	protected void action() {
		verifyAndInitialize();
		if (!fileError) {
			CheckListTableModel model = (CheckListTableModel)(clt.getTable().getModel());
			if (clt.getTable().getSelectedRowCount() == 1) {
				int indexRow = clt.getTable().getSelectedRow();
				int numCampaign = clt.getNumCampaign(indexRow);
				HopperStep cmdLine = (HopperStep)(clt.getCampaign().get(numCampaign));
				// authentication
				if (authenticationPanel.getLogin().length()!=0) {
					cmdLine.setLogin(authenticationPanel.getLogin());
					cmdLine.setPassword(authenticationPanel.getPassword());
				}
				if (authenticationPanel.getUserAgent().length()!=0) {
					cmdLine.setUseragent(authenticationPanel.getUserAgent());
				}
				// get old value
				String flashURIOld = cmdLine.getFlashFilePath();
				if (nameIsChanged(flashURIOld, testName)) {
					cmdLine.setFlashFilePath(testName);
					model.setValueAt(flashName, indexRow, HopperCheckListTable.COLUMN_TESTNAME);
					String verdict = (String)model.getValueAt(indexRow, HopperCheckListTable.COLUMN_VERDICT);
					if (verdict != null && !verdict.equals("")) {
						CoreGUIPlugin.mainFrame.setModified(true);
						model.setValueAt("M ", indexRow, HopperCheckListTable.COLUMN_MODIFIED);
					}
					clt.getToolTipFlashFile().setElementAt(testName, numCampaign);
				}
			}
		}

	}

	private boolean nameIsChanged(String oldName, String newName) {
		if (oldName == null && newName == null) return false;
		if (oldName == null || newName == null)	return true;
		if (oldName.equals(newName))return false;
		return true;
	}

}
