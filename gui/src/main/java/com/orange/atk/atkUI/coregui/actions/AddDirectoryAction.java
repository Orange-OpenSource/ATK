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
 * File Name   : AddDirectoryAction.java
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

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.tasks.AddDirTask;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class AddDirectoryAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;
	private String lastDirPath = null;

	public AddDirectoryAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
		lastDirPath = getDefaultPath();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MatosGUI matosGui = CoreGUIPlugin.mainFrame;

		JFileChooser fileChooser = new JFileChooser(lastDirPath);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setFileFilter(new FileUtilities.FilterDir());
		int returnVal =  fileChooser.showDialog(CoreGUIPlugin.mainFrame, "Add directory");
		String tmp=null;
		try {
			tmp = Configuration.getProperty(Configuration.INPUTDIRECTORY);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}
		if(tmp!=null)
		fileChooser.setCurrentDirectory(new File(tmp));
		
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final String src = fileChooser.getSelectedFile().getAbsolutePath();
			if (src==null || src.equals("")){
				JOptionPane.showMessageDialog(
						matosGui,
						"You must indicate the directory to load.",
						"Error !",
						JOptionPane.ERROR_MESSAGE);
			}else{
				File dir = new File(src);
				new AddDirTask(CoreGUIPlugin.mainFrame.statusBar, dir, false, 1);
				lastDirPath = src;
				Configuration.setProperty(Configuration.INPUTDIRECTORY, fileChooser.getSelectedFile().getParent());

			}

		}

	}
	private String getDefaultPath(){
		try {
			String path = Configuration.getProperty(Configuration.INPUTDIRECTORY);
			Logger.getLogger(this.getClass() ).debug("InputDir="+path);
			return path;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass() ).debug(" no input dir set in configuration file [ConfigurtionDialog]");
			return null;
		}
	}
}
