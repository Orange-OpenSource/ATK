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
 * File Name   : SaveSelectionAsAction.java
 *
 * Created     : 09/05/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class SaveSelectionAsAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	private String lastChkLstFilePath = null;

	public SaveSelectionAsAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;
		JFileChooser fileChooser = new JFileChooser(lastChkLstFilePath);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileUtilities.Filter("MCL file [*.mcl]", ".mcl"));
		int returnVal = fileChooser.showDialog(matosGui, "Save selection");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String src = fileChooser.getSelectedFile().getAbsolutePath();
			File f = new File(src);
			if (f.exists()) {
				//WARNING : selected file already exists !
				int res = JOptionPane.showConfirmDialog(
						matosGui,
						src + " already exists.\n"
							+ " Do you want to overwrite ?",
						fileChooser.getDialogTitle(),
						JOptionPane.YES_NO_OPTION);
				if (res==(JOptionPane.NO_OPTION) || (res==JOptionPane.CLOSED_OPTION)) {
					//openChooserSave(all);
					actionPerformed(e);
					return;
				}
			}
			src = FileUtilities.verifyExtension(src, ".mcl");
//			if (all){
//				frame.setCheckListFileName(src);
//				try {
//					Campaign camp = MatosGUI.getCampaign();
//					Campaign.save(MatosGUI.clFileName, camp);
//				} catch (Alert e) {
//					JOptionPane.showMessageDialog(
//							frame,
//							"Problem during the check-list save.",
//							"Error !",
//							JOptionPane.ERROR_MESSAGE);
//				}
//			}else{
				try {
					Campaign selectedCampaign = MatosGUI.getSelectedCampaign();
					Campaign.save(src, selectedCampaign);
				} catch (Alert a) {
					JOptionPane.showMessageDialog(
							matosGui,
							"Problem during the check-list save." + a.getMessage(),
							"Error !",
							JOptionPane.ERROR_MESSAGE);
				}
//			}
			lastChkLstFilePath = src;
			matosGui.setModified(false);
		}

	}

}
