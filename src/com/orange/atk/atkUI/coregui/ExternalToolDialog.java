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
 * File Name   : ExternalToolDialog.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.coregui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import com.orange.atk.atkUI.corecli.ExternalTool;
import com.orange.atk.atkUI.coregui.utils.SpringUtilities;

/**
 * Allows to add a new external tool in the list of available external tools.
 * @author apenault
 *
 */
public class ExternalToolDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private static int NEW_IN_CONFIG = 1;
//	private static int NEW_IN_MAIN = 2;
	private static int EDIT = 3;
		
	private JTextField nameTF;
	private JTextField cmdLineTF;
	private JButton okButton;
	private ExternalTool externalToolToEdit;
	private ExternalTool newExternalTool;
	private int mode;

	/**
	 * Build and display the dialog.
	 * @param configDialog the parent dialog
	 */
	private ExternalToolDialog(ConfigurationDialog configDialog){
		super(configDialog, true);
		mode = NEW_IN_CONFIG;
		
		JPanel mainPanel = initComponents();
		
		this.getContentPane().add(mainPanel);
		this.setTitle("Add an external tool");
		this.pack();

		int dec_x = (CoreGUIPlugin.mainFrame.getWidth()-this.getWidth())/2;
		int dec_y = (CoreGUIPlugin.mainFrame.getHeight()-this.getHeight())/2;
		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+dec_x,
						 CoreGUIPlugin.mainFrame.getLocationY()+dec_y);
		
		this.setVisible(true); 
	}

	/**
	 * Build and display the dialog to edit properties of an emulator.
	 * @param configDialog the parent dialog
	 * @param emulator emulator to edit
	 */
	private ExternalToolDialog(ConfigurationDialog configDialog, ExternalTool emulator){
		super(configDialog, true);
		this.externalToolToEdit = emulator;
		mode = EDIT;
		
		JPanel mainPanel = initComponents();
		
		nameTF.setText(emulator.getName());
		cmdLineTF.setText(emulator.getCmdLine());
		
		this.getContentPane().add(mainPanel);
		this.setTitle("Edit tool");
		this.pack();

		int dec_x = (CoreGUIPlugin.mainFrame.getWidth()-this.getWidth())/2;
		int dec_y = (CoreGUIPlugin.mainFrame.getHeight()-this.getHeight())/2;
		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+dec_x,
						 CoreGUIPlugin.mainFrame.getLocationY()+dec_y);
		
		this.setVisible(true); 
	}
	
	/**
	 * Shows a dialog to edit an existing external tool definition
	 * @param configDialog 
	 * @param emulator
	 * @return
	 */
	public static ExternalTool showEditDialog(ConfigurationDialog configDialog, ExternalTool emulator) {
		ExternalToolDialog extToolDialog = new ExternalToolDialog(configDialog, emulator);
		return extToolDialog.getExternalToolToEdit();
	}

	/**
	 * @return the edited external tool, or null if not in Edit Mode
	 */
	private ExternalTool getExternalToolToEdit() {
		if (mode==EDIT) {
			return externalToolToEdit;
		} else {
			return null;
		}
	}

	/**
	 * Shows a dialog to add an external tool definition
	 * @param configDialog 
	 * @param emulator
	 * @return
	 */
	public static ExternalTool showAddDialog(ConfigurationDialog configDialog) {
		ExternalToolDialog extToolDialog = new ExternalToolDialog(configDialog);
		return extToolDialog.getNewExternalTool();
	}

	/**
	 * @return the edited external tool, or null if not in Edit Mode
	 */
	private ExternalTool getNewExternalTool() {
		if (mode==NEW_IN_CONFIG) {
			return newExternalTool;
		} else {
			return null;
		}
	}

	/**
	 * Action performed when the OK button is clicked or the "Enter" key is pressed.
	 *
	 */
	public void action() {
		String name = nameTF.getText();
		String cmdLine = cmdLineTF.getText();
		if (name.length()==0 || cmdLine.length()==0) {
			JOptionPane.showMessageDialog(ExternalToolDialog.this, 
					"The tool must have a name and a command line to launch it.", 
					"Warning!", 
					JOptionPane.ERROR_MESSAGE);
//		} else if ((cmdLineTF.getText().indexOf("%JAD%") == -1)&&
//				  (cmdLineTF.getText().indexOf("%JAR%") == -1)&&
//				  (cmdLineTF.getText().indexOf("%SWF%") == -1)) {
//			JOptionPane.showMessageDialog(ExternalToolDialog.this, 
//					"<html>Your command line must contain at least one of the following string: <br>" +
//					" \"%JAD%\" indicates where the JAD file name should be inserted in the command line to launch the tool. <br>" +
//					" \"%JAR%\" indicates where the JAR file name should be inserted in the command line to launch the tool. <br>" +
//					" \"%SWF%\" indicates where the SWF file name should be inserted in the command line to launch the tool." +
//					"</html>", 
//					"Warning!", 
//					JOptionPane.ERROR_MESSAGE);
//		} else if (((cmdLineTF.getText().indexOf("%JAD%") != -1)||(cmdLineTF.getText().indexOf("%JAR%") != -1))&&
//				  (cmdLineTF.getText().indexOf("%SWF%") != -1)) {
//			JOptionPane.showMessageDialog(ExternalToolDialog.this, 
//					"<html>Your command line cannot contain \"%JAD%\" or \"%JAR%\" if it contains \"%SWF%\".<br>" +
//					" \"%JAD%\" indicates where the JAD file name should be inserted in the command line to launch the tool. <br>" +
//					" \"%JAR%\" indicates where the JAR file name should be inserted in the command line to launch the tool. <br>" +
//					" \"%SWF%\" indicates where the SWF file name should be inserted in the command line to launch the tool." +
//					"</html>", 
//					"Warning!", 
//					JOptionPane.ERROR_MESSAGE);
//		}else if (CoreGUI.configuration.getIndexOfEmulator(name)!=-1 && mode != EDIT){
//			JOptionPane.showMessageDialog(ExternalToolDialog.this, 
//					"The list of tools already contains a tool named: "+name, 
//					"Warning!", 
//					JOptionPane.ERROR_MESSAGE);
		} else {
			if (mode == NEW_IN_CONFIG) {
				newExternalTool = new ExternalTool();
				newExternalTool.setName(name);
				newExternalTool.setCmdLine(cmdLine);
				//ExternalToolDialog.this.configDialog.addNewTool(name, cmdLine);
//			}else if (mode == NEW_IN_MAIN){
//				newExternalTool = new ExternalTool();
//				newExternalTool.setName(name);
//				newExternalTool.setCmdLine(cmdLine);
//				ArrayList emulators = CoreGUI.configuration.getExternalToolsList();
//				emulators.add(emulator);
//				CoreGUI.configuration.makeDefaultExternalTool(emulators, name);
			} else if (mode == EDIT) {
				externalToolToEdit.setName(name);
				externalToolToEdit.setCmdLine(cmdLine);
			}
		}
		ExternalToolDialog.this.dispose();
	}

	/**
	 * Initialize components of this dialog.
	 * @return the main panel
	 */
	private JPanel initComponents() {
		JLabel nameLabel = new JLabel("Tool's name: ");
		nameTF = new JTextField(20);
		JLabel cmdLineLabel = new JLabel("Command line: ");String osName = System.getProperty("os.name");
		if (osName.startsWith("Linux")) {
			cmdLineTF = new JTextField("/usr/local/WTK2.2/bin/emulator -Xdescriptor %JAD%");
		} else if (osName.startsWith("Windows")) {
			cmdLineTF = new JTextField("C:\\WTK22\\bin\\emulator -Xdescriptor %JAD%");
		} else { // try somethings ... X fingers 
			cmdLineTF = new JTextField();
		}
		JPanel entriesPanel = new JPanel(new SpringLayout());
		entriesPanel.add(nameLabel);
		entriesPanel.add(nameTF);
		entriesPanel.add(cmdLineLabel);
		entriesPanel.add(cmdLineTF);
		SpringUtilities.makeCompactGrid(entriesPanel, 2, 2, 6, 6, 6, 6);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ExternalToolDialog.this.dispose();
			}
		});
		okButton = new JButton("OK");
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(entriesPanel);
		mainPanel.add(buttonsPanel);
		
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				action();
			}
		});
		
		JComponent component = this.getRootPane();
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "actionOKConfiguration");
		component.getActionMap().put("actionOKConfiguration", new AbstractAction(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				action();
			}
		});
		
		return mainPanel;
	}
	
}
