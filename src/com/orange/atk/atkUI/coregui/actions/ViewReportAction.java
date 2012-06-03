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
 * File Name   : ViewReportAction.java
 *
 * Created     : 09/05/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.FileViewDialog;
import com.orange.atk.atkUI.coregui.MatosGUI;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class ViewReportAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	public ViewReportAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;
		
		Step sel = matosGui.getSelectedAnalysisPane().getCheckListTable().getSelectedStep();
		if (sel!=null) {
			String outFilePath = sel.getOutFilePath();
			if ((outFilePath!=null)&&(!outFilePath.trim().equals(""))) {
				new FileViewDialog(matosGui, outFilePath+File.separatorChar+"report.html", FileViewDialog.REPORT, 
								   sel.getLogin(), sel.getPassword(), sel.getUseragent());
			} else {
				JOptionPane.showMessageDialog(matosGui, "No report available", "View report", JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}

}
