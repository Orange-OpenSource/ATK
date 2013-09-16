/*
 * Software Name : ATK
 *
 * Copyright (C) 2013 Orange SA
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
 * File Name   : ViewLogAction.java
 *
 * Created     : 05/07/2013
 */

package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.graphAnalyser.LectureJATKResult;

public class ViewAnalyzerAction extends MatosAbstractAction {

	public ViewAnalyzerAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;

		Step sel = matosGui.getSelectedAnalysisPane().getCheckListTable().getSelectedStep();
		if (sel != null) {
			String outFilePath = sel.getOutFilePath();
			Logger.getLogger(this.getClass()).debug("output:" + outFilePath);
			if ((outFilePath != null) && (!outFilePath.trim().equals(""))) {
				LectureJATKResult analyser = new LectureJATKResult();
				analyser.setParameters(outFilePath);
				analyser.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(matosGui, "No report available", "View report",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}
}
