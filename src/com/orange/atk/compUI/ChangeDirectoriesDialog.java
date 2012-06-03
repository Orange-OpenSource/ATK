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
 * File Name   : ChangeDirectoriesDialog.java
 *
 * Created     : 28/08/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compUI;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.compModel.Model;

public class ChangeDirectoriesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3108668204266345028L;
	private JTextField inputRefDir;
	private Model m;
	private JTextField inputTestDir;
	protected int action;

	public ChangeDirectoriesDialog(Frame owner, Model m){
		super(owner,true);
		this.m=m;
		this.setTitle("Change your directories");
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Directories", getChoosePanel());
		this.add(tabs, BorderLayout.CENTER);
		this.add(getOKCancelPanel(), BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true); 
	}

	private JPanel getChoosePanel() {
		
		JPanel jp= new JPanel();
		JPanel jpref=new JPanel();
		jpref.setBorder(BorderFactory.createTitledBorder("Reference Directory"));
		
		inputRefDir = new JTextField(m.getRefDirectory(), 20);
		jpref.add(inputRefDir);
		jpref.add(Box.createHorizontalStrut(5));
		JButton browseRefDir = new JButton("Browse");
		browseRefDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				openFileChooser(inputRefDir, true);
			}
		});
		
		jpref.add(browseRefDir);
		
		JPanel jptest=new JPanel();
		jptest.setBorder(BorderFactory.createTitledBorder("Test Directory"));
		
		inputTestDir = new JTextField(m.getTestDirectory(), 20);
		jptest.add(inputTestDir);
		jptest.add(Box.createHorizontalStrut(5));
		JButton browseTestDir = new JButton("Browse");
		browseTestDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				openFileChooser(inputTestDir, true);
			}
		});
		
		jptest.add(browseTestDir);
		jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=1;
		jp.add(jpref,gbc);
		gbc.gridy=2;
		jp.add(jptest,gbc);
		
		
		return jp;
	}

	private JPanel getOKCancelPanel() {
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				action=JOptionPane.OK_OPTION;
				ChangeDirectoriesDialog.this.dispose();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action=JOptionPane.CANCEL_OPTION;
				ChangeDirectoriesDialog.this.dispose();
			}
		});
		JPanel OKCancelPanel = new JPanel();
		OKCancelPanel.add(ok);
		OKCancelPanel.add(cancel);

		return OKCancelPanel;
	}
	


	
	private void openFileChooser(JTextField textField, boolean dir) {
		JFileChooser fileChooser = null;
		if (textField.getText()!=null && !textField.getText().equals("")){
			fileChooser = new JFileChooser(textField.getText());
		}else{
			fileChooser = new JFileChooser();
		}
		String title = null;
		if (dir) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			title = "Select a directory";
		} else {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileUtilities.Filter("csv file [*.csv]", ".csv"));
			title = "Select CSV file";
		}
		int returnVal = 0;
		returnVal =  fileChooser.showDialog(ChangeDirectoriesDialog.this, title);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String src = fileChooser.getSelectedFile().getAbsolutePath();
			textField.setText(src);
		}
	}

	public int getAction() {
		// TODO Auto-generated method stub
		return action;
	}

	public String getRefPath() {
		// TODO Auto-generated method stub
		return inputRefDir.getText();
	}

	public String getTestPath() {
		// TODO Auto-generated method stub
		return inputTestDir.getText();
	}
	
	
	


}
