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
 * File Name   : NewCheckListAction.java
 *
 * Created     : 09/05/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.orange.atk.atkUI.coregui.AnalysisGUICommon;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.tasks.ClearCheckListTask;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class NewCheckListAction extends MatosAbstractAction {

	//private static int counter = 0;

	private static final long serialVersionUID = 1L;

	public NewCheckListAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;
		int length = matosGui.getStepNumber();
		if (matosGui.isModified() && length!=0) {
			int n = JOptionPane.showConfirmDialog(
					matosGui,
					"The current check-list (all tabs) is not saved and is going to be cleared, do you want to save it? ",
					"INFORMATION !",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (n==0) {//YES_OPTION
				MatosAction.SAVEALLALL.getAction().actionPerformed(e);

				new ClearCheckListTask(matosGui.statusBar, MatosGUI.analysisPlugins, length);
				matosGui.setCheckListFileName(null);
			} else if (n==1) {//NO_OPTION
				new ClearCheckListTask(matosGui.statusBar, MatosGUI.analysisPlugins, length);
				matosGui.setCheckListFileName(null);
			}
		} else {
			new ClearCheckListTask(matosGui.statusBar, MatosGUI.analysisPlugins, length);
			matosGui.setCheckListFileName(null);
		}

		for (AnalysisGUICommon guiCommon : MatosGUI.analysisPlugins) {
			guiCommon.newChecklist();
		}
	}

}
