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
 * File Name   : AddGraphDialog.java
 *
 * Created     : 24/07/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.utils.FileUtilities;

public class AddGraphDialog extends JDialog {


	private JTextField inputcsvfile = null;

	
	
	
	public String getInputcsvfile() {
		return inputcsvfile.getText();
	}

	public AddGraphDialog(Frame owner, boolean modal){
		super(owner, modal);
				this.setTitle("add a Graph...");
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Commons", getCommonsPanel());
		this.add(tabs, BorderLayout.CENTER);
		this.add(getOKCancelPanel(), BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true); 
	}
	
	/**
	 * Builds the panel that allow to modify commons congiguration parameters 
	 * @return a JPanel to config commons parameters
	 */
	private JPanel getCommonsPanel() {
		JPanel commons = new JPanel();
		commons.setLayout(new BoxLayout(commons, BoxLayout.Y_AXIS));
		
		
		
		JPanel results = new JPanel();
		results.setLayout(new BorderLayout());
		results.setBorder(BorderFactory.createTitledBorder("Add a Ref Graph"));

        //Select graph location
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(new JLabel(" csv File location: "));
		inputPanel.add(Box.createHorizontalStrut(5));

		String inputdir = null;
		try {
			inputdir = "";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass() ).debug(" no input dir set in configuration file [ConfigurtionDialog]");
		}
		inputcsvfile = new JTextField(inputdir, 20);
		inputPanel.add(inputcsvfile);
		inputPanel.add(Box.createHorizontalStrut(5));
		JButton browseinputdir = new JButton("Browse");
		browseinputdir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser(inputcsvfile, false);
			}
		});
		
		
		
		inputPanel.add(browseinputdir);
		results.add(inputPanel, BorderLayout.SOUTH);
		JPanel storagePane2 = new JPanel(new BorderLayout());
		storagePane2.add(inputPanel, BorderLayout.SOUTH);
		
		results.add(storagePane2, BorderLayout.NORTH);

		JPanel storagePanel = new JPanel(new BorderLayout());
		JPanel colorPanel = new JPanel(new BorderLayout());
		JPanel chooseColor = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JComboBox color = new JComboBox(new String[] { "blue","yellow", "red","green","black" });
		chooseColor.add(new JLabel(" select a color: "));
		chooseColor.add(color);
		chooseColor.add(Box.createHorizontalStrut(5));
		colorPanel.add(chooseColor, BorderLayout.SOUTH);
		storagePanel.add(colorPanel, BorderLayout.NORTH);
		results.add(storagePanel, BorderLayout.SOUTH);
		commons.add(results);

		return commons;
	}
	
	/**
	 * Action performed when user clicks on "OK" button or presses 
	 * the "Enter" key.
	 */
	protected void okAction() {
		Cursor lastCursor = AddGraphDialog.this.getCursor();
		AddGraphDialog.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		// save commons config parameters
	
Logger.getLogger(this.getClass() ).debug("OK");

		AddGraphDialog.this.setCursor(lastCursor);
		AddGraphDialog.this.dispose();
	}
	
	
	
	
	/**
	 * Builds the OK-Cancel panel
	 * @return ok and Cancel buttons in a panel panel
	 */
	private JPanel getOKCancelPanel() {
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				okAction();				
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddGraphDialog.this.dispose();
			}
		});
		JPanel OKCancelPanel = new JPanel();
		OKCancelPanel.add(ok);
		OKCancelPanel.add(cancel);

		return OKCancelPanel;
	}
	
	/**
	 * Open a file chooser initialized with the content of the given textfield and put the chosen path
	 * in it at the closing of the file chooser.
	 * @param textField text field which contains the file path.
	 */
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
		returnVal =  fileChooser.showDialog(AddGraphDialog.this, title);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String src = fileChooser.getSelectedFile().getAbsolutePath();
			if (!dir)
			src = FileUtilities.verifyExtension(src, ".csv");
			textField.setText(src);
		}
	}
	
}
