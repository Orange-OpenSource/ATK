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
 * File Name   : MixScriptStepDialog.java
 *
 * Created     : 30/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiMixScript;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.coregui.AbstractStepDialog;
import com.orange.atk.atkUI.coregui.AuthenticationPanel;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 * This is a specialization of <code>AbstractStepDialog</code> dialogs for
 * <code>FlashStep</code>.
 * 
 * @author Aurore PENAULT, Nicolas MOTEAU
 * @since JDK5.0
 */
public abstract class MixScriptStepDialog extends AbstractStepDialog {

	private static final long serialVersionUID = 1L;

	protected JRadioButton fileRadio;
	protected JButton fileButton;
	protected JTextField fileTF;

	protected JRadioButton urlRadio;
	protected JTextField urlTF;

	protected JButton ok;
	protected JButton cancel;

	/**
	 * Path of last selected file (used to re-open the file chooser at the same
	 * place)
	 */
	protected String lastFilePath = null;

	protected File flashFile;
	protected String flashURI;
	protected String flashName;

	protected MixScriptCheckListTable clt;

	protected MatosGUI mainFrame = CoreGUIPlugin.mainFrame;

	/**
	 * Builds a JavaStep dialog.
	 */
	public MixScriptStepDialog() {
		super();

		authenticationPanel = new AuthenticationPanel();
		clt = (MixScriptCheckListTable) GuiMixScriptLink.getFlashGUI().getCheckListTable();
		fileRadio = new JRadioButton("Script File: ");
		fileButton = new JButton("Browse");
		fileButton.setVerticalTextPosition(AbstractButton.CENTER);
		fileButton.setHorizontalTextPosition(AbstractButton.LEADING);
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scriptFileChooser();
			}
		});
		fileTF = new JTextField(30);

		urlRadio = new JRadioButton("URL: http:// ");
		urlTF = new JTextField(33);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(fileRadio);
		buttonGroup.add(urlRadio);

		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filePanel.add(Box.createHorizontalStrut(10));
		filePanel.add(fileRadio);
		filePanel.add(fileButton);
		filePanel.add(fileTF);

		JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		urlPanel.add(Box.createHorizontalStrut(10));
		urlPanel.add(urlRadio);
		urlPanel.add(urlTF);

		JPanel globalFilePanel = new JPanel();
		globalFilePanel.setBorder(new TitledBorder("Select files"));
		globalFilePanel.setLayout(new BoxLayout(globalFilePanel, BoxLayout.Y_AXIS));
		globalFilePanel.add(filePanel);
		globalFilePanel.add(urlPanel);

		ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchAction();
			}
		});
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MixScriptStepDialog.this.dispose();
			}
		});

		urlRadio.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (urlRadio.isSelected()) {
					authenticationPanel.setEnabled(true);
					urlTF.setEnabled(true);
					fileButton.setEnabled(false);
					fileTF.setEnabled(false);
				} else {
					authenticationPanel.setEnabled(false);
					urlTF.setEnabled(false);
					fileButton.setEnabled(true);
					fileTF.setEnabled(true);
				}
			}
		});
		fileRadio.setSelected(true);
		urlTF.setEnabled(false);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(ok);
		buttonsPanel.add(cancel);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(globalFilePanel);
		mainPanel.add(authenticationPanel);
		mainPanel.add(buttonsPanel);

		getRootPane().setDefaultButton(ok);

		Container contentPaneFrame = this.getContentPane();
		contentPaneFrame.add(mainPanel, BorderLayout.CENTER);
		setLocationRelativeTo(CoreGUIPlugin.mainFrame);
		this.pack();
		ok.requestFocusInWindow();
	}

	/**
	 * Adds a step to the flash table.
	 * 
	 */
	protected void launchAction() {
		try {
			action();
			if (!fileError) {
				MixScriptStepDialog.this.dispose();
			}
			fileError = false;
		} catch (Alert a) {
			JOptionPane.showMessageDialog(MixScriptStepDialog.this, a.getMessage(), "Error !",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	protected abstract void action() throws Alert;

	/**
	 * Compute the short name of a file from its URI.
	 * 
	 * @param uri
	 *            file URI
	 * @return a string that is the short name.
	 */
	public static String guessName(String uri) {
		String name = "";
		if (uri.startsWith("http:") && (uri.length() - 1) > uri.lastIndexOf("/")) {
			name = uri.substring(uri.lastIndexOf("/") + 1);// , uri.length()-1);
		} else
			if (uri.lastIndexOf(File.separator) != -1
					&& (uri.length() - 1) > uri.lastIndexOf(File.separator)) {
				name = uri.substring(uri.lastIndexOf(File.separator) + 1);// ,
																			// uri.length()-1);
			} else {
				name = uri;
			}
		return name;
	}

	/**
	 * Opens a file chooser to select a flash file.
	 * 
	 */
	protected void scriptFileChooser() {
		JFileChooser fileChooser = null;
		String path = lastFilePath;

		if (fileTF.getText() != null && !fileTF.getText().equals("")) {
			String file = fileTF.getText();
			if (file.lastIndexOf(File.separator) != -1) {
				path = file.substring(0, file.lastIndexOf(File.separator));
			}
		}

		fileChooser = new JFileChooser(path);
		String extension = ".tst";

		if (AutomaticPhoneDetection.getInstance().isNokia())
			extension = ".xml";
		fileChooser.setFileFilter(new FileUtilities.Filter("ATK Script file [*" + extension + "]",
				extension));
		selectAndSetTF(fileChooser, extension);
	}

	/**
	 * Fills the text field with the selected file
	 * 
	 * @param fileChooser
	 * @param extension
	 *            extension of the selected file
	 */
	private void selectAndSetTF(JFileChooser fileChooser, String extension) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		String tmp = null;
		try {
			tmp = Configuration.getProperty(Configuration.INPUTDIRECTORY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			File file = new File(Configuration.getProperty(Configuration.OUTPUTDIRECTORY));
			if (file.exists()) {
				Configuration.setProperty(Configuration.INPUTDIRECTORY, file.getParent());
				Configuration.writeProperties();
				tmp = file.getParent();
			}
		}
		if (tmp != null)
			fileChooser.setCurrentDirectory(new File(tmp));
		int returnVal = fileChooser.showDialog(null, "Select");
		String file = "";
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile().getAbsolutePath();
			file = FileUtilities.verifyExtension(file, extension);
			lastFilePath = file;
			fileTF.setText(file);
			Configuration.setProperty(Configuration.INPUTDIRECTORY, fileChooser.getSelectedFile()
					.getParent());
		}
	}

	/**
	 * Verify correct filling of fields in the dialog box.
	 * 
	 */
	protected void verifyAndInitialize() {
		if (authenticationPanel.getLogin().length() > 0) {
			login = authenticationPanel.getLogin();
			password = authenticationPanel.getPassword();
		} else {
			login = null;
			password = null;
		}
		if (authenticationPanel.getUserAgent().length() > 0) {
			user_agent = authenticationPanel.getUserAgent();
		} else {
			user_agent = null;
		}
		if (fileRadio.isSelected()) {
			if (fileTF.getText() == null || fileTF.getText().equals("")) {
				showError("You must indicate the location of the Flash file.");
			} else {
				flashFile = new File(fileTF.getText());
				if (!flashFile.exists()) {
					showError("The specified Flash file can't be found.");
				} else {
					flashURI = fileTF.getText();
					flashName = guessName(flashURI);
				}
			}
		} else
			if (urlRadio.isSelected()) {
				if (urlTF.getText() == null || urlTF.getText().equals("")) {
					showError("You must indicate the location of the Flash file.");
				} else {
					flashURI = urlTF.getText();
					if (!flashURI.startsWith("http://"))
						flashURI = "http://" + flashURI;
					flashName = guessName(flashURI);
					String errorMsg = "";
					try {
						if (!flashURI.endsWith(".xml")) {
							flashFile = Configuration.fileResolver.getFile(flashURI, "tmpflash",
									".tst", login, password, user_agent);
						} else {
							flashFile = Configuration.fileResolver.getFile(flashURI, "tmpflash",
									".xml", login, password, user_agent);
						}
					} catch (Alert e) {
						errorMsg = e.getMessage();
						Logger.getLogger(this.getClass()).error(e);
					}
					if (flashFile == null || !flashFile.exists()) {
						showError("<html>Invalid URL for Flash file or problem when downloading Flash file.<br>"
								+ errorMsg + "</html>");
					}
				}
			}

	}

}
