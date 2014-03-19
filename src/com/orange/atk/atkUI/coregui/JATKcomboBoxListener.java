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
 * File Name   : JATKcomboBoxListener.java
 *
 * Created     : 12/03/2010
 * Author(s)   : France Telecom
 */
package com.orange.atk.atkUI.coregui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.actions.MonitorAction;
import com.orange.atk.atkUI.guiScript.JatkCheckListTable;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.util.FileUtil;

public class JATKcomboBoxListener implements ActionListener, MouseListener {
	private JComboBox _jcomBox;
	private CheckListTable _table = null;
	private MonitorAction _monitorAction = null;

	public JATKcomboBoxListener(JComboBox jcomBox, CheckListTable table) {
		super();
		_jcomBox = jcomBox;
		_table = table;
	}
	public JATKcomboBoxListener(JComboBox jcomBox, MonitorAction monitorAction) {
		super();
		_jcomBox = jcomBox;
		_monitorAction = monitorAction;
	}
	public void actionPerformed(ActionEvent E) {
		//TODO Add a test if double click to be able to modify the file!

		int selectredRow = -1;
		Step step = null;
		if(_table!=null){
			selectredRow = _table.getSelectedRow();
			if(selectredRow==-1)
				return;
			step = (Step)_table.getCampaign().get(selectredRow);
		}

		String selectedItem = (String) _jcomBox.getSelectedItem();
		File file;
		if(JatkCheckListTable.ADD_NEW_CONFIG_FILE == selectedItem){
			//We need to create a new file!
			if(AutomaticPhoneDetection.getInstance().getDevice() instanceof DefaultPhone){
				JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, "You need to connect a phone before to create a configuration file.");
				return;
			}

			String nameFile = (String)JOptionPane.showInputDialog(CoreGUIPlugin.mainFrame,
			"Name of the new file to create:");
			if(null == nameFile)
				return;
			if(!nameFile.endsWith(".xml"))
				nameFile+=".xml";
			file = new File(Configuration.getMonitoringConfigDir()+File.separator+nameFile);

			String defaultFileName = AutomaticPhoneDetection.getInstance().getxmlfilepath();

			File defaultFile = new File(defaultFileName);

			FileUtil.copyfile(file, defaultFile);

			// Loop to determine where to insert the new element at the correct place
			for (int i = 0; i < _jcomBox.getItemCount();i++) {
				if((nameFile.compareToIgnoreCase((String)_jcomBox.getItemAt(i))<0)||
						(JatkCheckListTable.ADD_NEW_CONFIG_FILE == (String)_jcomBox.getItemAt(i))){
					_jcomBox.insertItemAt(nameFile,i);
					break;
				}
			}

			_jcomBox.setSelectedItem(nameFile);
			new PhoneConfigurationWizard(file.toString(), false);
			if(step!=null)
				step.setXmlfilepath(file.toString());
			else if(_monitorAction!=null)
				_monitorAction.setXmlfilepath(file.toString());
		}
		else{
			if (Configuration.getInstance().defaultMonitoringConfigNames().contains(((String)_jcomBox.getSelectedItem())))
				file = new File(Configuration.getMonitoringConfigDir()+File.separator+_jcomBox.getSelectedItem());
			else file = new File(Configuration.getMonitoringConfigDir()+File.separator+_jcomBox.getSelectedItem());
			if(step!=null)
				step.setXmlfilepath(file.toString());
			else if(_monitorAction!=null)
				_monitorAction.setXmlfilepath(file.toString());
		}
		Logger.getLogger(this.getClass()).info("Xmlfilepath set to :"+file);

		CoreGUIPlugin.mainFrame.setModified(true);
	}


	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2){
			String selectedItem = (String) _jcomBox.getSelectedItem();
			if(JatkCheckListTable.ADD_NEW_CONFIG_FILE == selectedItem)
				return;
			if (Configuration.getInstance().defaultMonitoringConfigNames().contains(selectedItem))
				new PhoneConfigurationWizard(Configuration.getMonitoringConfigDir()+selectedItem, true);
			else new PhoneConfigurationWizard(Configuration.getMonitoringConfigDir()+File.separator+selectedItem, false);

		}
	}


	public void mouseEntered(MouseEvent e) {
	}


	public void mouseExited(MouseEvent e) {
	}


	public void mousePressed(MouseEvent e) {
	}


	public void mouseReleased(MouseEvent e) {
	}
}
