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
 * File Name   : GetinfoDialog.java
 *
 * Created     : 03/08/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.utils.FileUtilities;

@SuppressWarnings("serial")
public class GetinfoDialog extends JDialog {


	private JTextField inputcsvfile = null;
    private LectureJATKResult frameAnalyser =null;
	private String csvfilepath=null;
	//TODO usage ?
	//private JComboBox colorcombo;
	private String label=null;
	private String result =null;
	private String defaultname=null;
	
	
	public GetinfoDialog(LectureJATKResult owner, boolean modal,String label,String defaultname){
		super(owner, modal);

		this.frameAnalyser= owner;
        this.label=null; 
        this.defaultname=defaultname;
        this.setTitle(label);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
		results.setBorder(BorderFactory.createTitledBorder(label));

        //Select graph location
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(new JLabel(label));
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

		
		results.add(inputPanel, BorderLayout.SOUTH);
		JPanel storagePane2 = new JPanel(new BorderLayout());
		storagePane2.add(inputPanel, BorderLayout.SOUTH);
		results.add(storagePane2, BorderLayout.NORTH);
		JPanel storagePanel = new JPanel(new BorderLayout());
		JPanel colorPanel = new JPanel(new BorderLayout());
		JPanel chooseColor = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		Cursor lastCursor = GetinfoDialog.this.getCursor();
		GetinfoDialog.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		// save commons config parameters
        
        csvfilepath= inputcsvfile.getText();
		if(csvfilepath!=null&&!csvfilepath.equals("")&&(!defaultname.equals(inputcsvfile.getText())))
			{
			frameAnalyser.setName(this.inputcsvfile.getText());}
		GetinfoDialog.this.setCursor(lastCursor);
		GetinfoDialog.this.dispose();
	}
	
	
	
	
	public String getResult() {
		return result;
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
				GetinfoDialog.this.dispose();
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
		returnVal =  fileChooser.showDialog(GetinfoDialog.this, title);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String src = fileChooser.getSelectedFile().getAbsolutePath();
			if (!dir)
			src = FileUtilities.verifyExtension(src, ".csv");
			textField.setText(src);
		}
	}
	
	
	
	
}
