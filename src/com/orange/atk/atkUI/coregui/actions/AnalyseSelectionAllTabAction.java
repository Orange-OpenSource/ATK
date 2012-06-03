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
 * File Name   : AnalyseSelectionAllTabAction.java
 *
 * Created     : 09/05/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.orange.atk.atkUI.coregui.AnalysisGUICommon;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.SelectDialog;
import com.orange.atk.atkUI.coregui.tasks.AnalyseTask;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class AnalyseSelectionAllTabAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	public AnalyseSelectionAllTabAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;

		if (MatosGUI.outputDir == null || MatosGUI.outputDir.equals("")){
			int ret = SelectDialog.showDialog(matosGui, true);
			if (ret!=SelectDialog.OK_OPTION) {
				return;
			}
		}

		for (AnalysisGUICommon analysisGUI : MatosGUI.analysisPlugins) {
			AnalyseTask at = new AnalyseTask(CoreGUIPlugin.mainFrame.statusBar, analysisGUI.getCheckListTable(), false);
			if (at.isShouldStop()) break; // "Cancel" have been pressed
		}
	}

}
