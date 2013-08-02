/*
 * Copyright (C) 2012 The Android Open Source Project
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
 */

package com.android.uiautomator;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.orange.atk.atkUI.corecli.utils.FileUtilities;


/**
 * Implements a file selection dialog for both screen shot and xml dump file
 *
 * "OK" button won't be enabled unless both files are selected
 * It also has a convenience feature such that if one file has been picked, and the other
 * file path is empty, then selection for the other file will start from the same base folder
 *
 */
public class OpenDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField mScreenshotText;
	private JTextField mXmlText;
	private boolean mFileChanged = false;

	private static File sScreenshotFile;
	private static File sXmlDumpFile;
	private JButton ok;
	private JButton cancel;
	private JFrame parent;

	
	public OpenDialog(JFrame parentShell) {
		super(parentShell,ModalityType.APPLICATION_MODAL );
		this.setTitle("Open UI Dump Files");
		parent =parentShell;
		this.setSize(368, 233);

		ok= new JButton("OK");
		updateButtonState();
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				OpenDialog.this.dispose();
			}
		});

		cancel= new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				sScreenshotFile=null;
				sXmlDumpFile=null;
				OpenDialog.this.dispose();
			}
		});

		ok.setPreferredSize(cancel.getPreferredSize());

		mXmlText = new JTextField(25);
		mXmlText.setEnabled(false);
		JButton openXmlButton= new JButton("...");
		openXmlButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				handleOpenXmlDumpFile();
			}
		});

		mScreenshotText = new JTextField(25);
		mScreenshotText.setEnabled(false);
		JButton openScreenshotButton = new JButton("...");
		openScreenshotButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				handleOpenScreenshotFile();
			}
		});


		JPanel xmlPanel = new JPanel();
		xmlPanel.add(mXmlText);
		xmlPanel.add(openXmlButton);
		xmlPanel.setBorder(BorderFactory.createTitledBorder("UI XML Dump"));

		JPanel screenshotPanel = new JPanel();
		screenshotPanel.add(mScreenshotText);
		screenshotPanel.add(openScreenshotButton);
		screenshotPanel.setBorder(BorderFactory.createTitledBorder("Screenshot"));

		JPanel buttonsPanel = new JPanel();	
		buttonsPanel.add(ok);
		buttonsPanel.add(cancel);

		ok.requestFocusInWindow();
		ok.setEnabled(false);
		setLocationRelativeTo(parentShell);
		this.setLayout(new GridLayout(3,1));
		this.add(xmlPanel);
		this.add(screenshotPanel);
		this.add(buttonsPanel);
		this.setResizable(false);
		this.setVisible(true);

	}




	private void handleOpenScreenshotFile() {
		JFileChooser fd = new JFileChooser();
		File initialFile = sScreenshotFile;
		if (initialFile == null && sXmlDumpFile != null && sXmlDumpFile.isFile()) {
			initialFile = sXmlDumpFile.getParentFile();
		}
		if (initialFile != null) {
			if (initialFile.isFile()) {
				fd= new JFileChooser(initialFile.getAbsolutePath());
			} else if (initialFile.isDirectory()) {
				fd= new JFileChooser(initialFile.getAbsolutePath());
			}
		}
		fd.setToolTipText("Open Screenshot File");

		fd.setFileFilter(new FileUtilities.Filter("Screenshot  files (.png)",".png"));
		int r= fd.showOpenDialog(parent);
		if(r==JOptionPane.OK_OPTION){
			sScreenshotFile= fd.getSelectedFile();
			mScreenshotText.setText(sScreenshotFile.getAbsolutePath());
			mFileChanged = true;
		}
		updateButtonState();
	}

	private void handleOpenXmlDumpFile() {
		JFileChooser fd = new JFileChooser();
		File initialFile = sXmlDumpFile;
		if (initialFile == null && sScreenshotFile != null && sScreenshotFile.isFile()) {
			initialFile = sScreenshotFile.getParentFile();
		}
		if (initialFile != null) {
			if (initialFile.isFile()) {
				fd= new JFileChooser(initialFile.getAbsolutePath());
			} else if (initialFile.isDirectory()) {
				fd= new JFileChooser(initialFile.getAbsolutePath());
			}
		}
		String initialPath = mXmlText.getText();
		if (initialPath.isEmpty() && sScreenshotFile != null && sScreenshotFile.isFile()) {
			initialPath = sScreenshotFile.getParentFile().getAbsolutePath();
		}
		fd.setFileFilter(new FileUtilities.Filter("Xml  files (.xml)",".xml"));
		int r= fd.showOpenDialog(parent);
		if(r==JOptionPane.OK_OPTION){
			sXmlDumpFile= fd.getSelectedFile();
			mXmlText.setText(sXmlDumpFile.getAbsolutePath());
			mFileChanged = true;
		}

		updateButtonState();
	}

	private void updateButtonState() {
		ok.setEnabled(sXmlDumpFile != null && sXmlDumpFile.isFile());
	}

	public boolean hasFileChanged() {
		return mFileChanged;
	}

	public File getScreenshotFile() {
		return sScreenshotFile;
	}

	public File getXmlDumpFile() {
		return sXmlDumpFile;
	}
}