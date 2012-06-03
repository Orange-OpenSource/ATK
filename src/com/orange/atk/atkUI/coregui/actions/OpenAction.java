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
 * File Name   : OpenAction.java
 *
 * Created     : 09/05/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class OpenAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	public OpenAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;

		MatosAction.NEWCHECKLIST.getAction().actionPerformed(e);
		MatosAction.ADDCHECKLIST.getAction().actionPerformed(e);

		matosGui.setModified(false);
	}

//	public void openCheckList() {
//		MatosGUI matosGui = CoreGUIPlugin.mainFrame;
//
//		JFileChooser fileChooser = new JFileChooser(lastChkLstFilePath);
//		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		fileChooser.setFileFilter(new FileUtilities.Filter("MCL file [*.mcl]", ".mcl"));
//		int returnVal = fileChooser.showDialog(matosGui, "Open");
//		String src = "";
//		if (returnVal == JFileChooser.APPROVE_OPTION) {
//			src = fileChooser.getSelectedFile().getAbsolutePath();
//			src = FileUtilities.verifyExtension(src, ".mcl");
//			if (src==null || src.equals("")){
//				JOptionPane.showMessageDialog(
//						matosGui,
//						"You must indicate the file which contains the check-list description.",
//						"Error !",
//						JOptionPane.ERROR_MESSAGE);
//			} else {
//				try{
//					Campaign camp = Campaign.readCampaign(new File(src));
//					matosGui.setCheckListFileName(src);
//
//					int tasklength = matosGui.getStepNumber() + camp.size();
//					new LoadCheckListTask(matosGui.statusBar, camp, -1 /*ie at the end*/, true, tasklength);
//					lastChkLstFilePath = src;
//				}catch (Alert a){
//					JOptionPane.showMessageDialog(
//							matosGui,
//							a.getMessage(),
//							"Error !",
//							JOptionPane.ERROR_MESSAGE);
//				}
//			}
//		}
//
//	}
}
