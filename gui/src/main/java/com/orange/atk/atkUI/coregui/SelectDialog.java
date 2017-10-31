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
 * File Name   : SelectDialog.java
 *
 * Created     : 03/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;


/**
 * Dialog to select an output directory for the results.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class SelectDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JLabel inputLabel;
	private JTextField inputTF;
	private JButton inputButton;

	private JButton ok;
	private JButton cancel;
	
	public static int OK_OPTION = 1;
	public static int CANCEL_OPTION = 2;
	private int state = -1;
	private String selection = null;

	private SelectDialog(Frame parentFrame, boolean modal){
		//super(CoreGUIPlugin.mainFrame, true);
		super(parentFrame, modal);
		String outputDirPath = null;
		if (MatosGUI.outputDir == null || MatosGUI.outputDir.equals("")){
			outputDirPath = Configuration.getProperty(Configuration.OUTPUTDIRECTORY);
		}else{
			outputDirPath = MatosGUI.outputDir;
		}
		inputTF = new JTextField(30);
		inputButton = new JButton("Choose...");
		inputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser(inputTF);
			}
		});
		inputTF.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent e) {
				File outputDir = new File(inputTF.getText());
				if (outputDir.exists()){
					ok.setEnabled(true);
				}else{
					ok.setEnabled(false);
				}
			}
		});

		ok = new JButton("OK");
		ok.setEnabled(false);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = OK_OPTION;
				Cursor lastCursor = SelectDialog.this.getCursor();
				SelectDialog.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

				String src = inputTF.getText();
				if (src==null || src.equals("")) {
					JOptionPane.showMessageDialog(
							SelectDialog.this,
							"You must indicate the target location for your results directory.",
							"Error !",
							JOptionPane.ERROR_MESSAGE);
				} else {
					MatosGUI.outputDir = src;
					selection = src;
					Configuration.setProperty(Configuration.OUTPUTDIRECTORY, src);
					SelectDialog.this.dispose();
				}
				try {
				 	Configuration.writeProperties();
				} catch (Alert a) {
					JOptionPane.showMessageDialog(
							
							SelectDialog.this,
							"Problem while writing to the configuration file.",
							"Error !" ,
							JOptionPane.ERROR_MESSAGE);
				}
				SelectDialog.this.setCursor(lastCursor);
				
			}
		});

		if (outputDirPath == null || outputDirPath.equals("")){
			inputLabel = new JLabel("<html>You haven't set the target location for the global results directory.<br>"+
					"Please set it now.<br><br>"+
			"Location of the <i>AnalysesResults</i> directory is:</html>");
			ok.setEnabled(false);
		}else{
			File outputDir = new File (outputDirPath);
			if (!outputDir.exists()){
				inputLabel = new JLabel("The target location "+outputDirPath+" is incorrect. Please select an existing one.");
				ok.setEnabled(false);
			}else{
				inputLabel = new JLabel("<html>Target location for the results directory (<i>AnalysesResults</i>): </html>");
				inputTF.setText(outputDirPath);
				ok.setEnabled(true);
			}
		}

		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = CANCEL_OPTION;
				SelectDialog.this.dispose();
			}
		});

		JPanel panel1 = new JPanel();
		JPanel panelInput1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelInput1.add(inputLabel);
		JPanel panelInput2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelInput2.add(Box.createHorizontalStrut(5));
		panelInput2.add(inputTF);
		panelInput2.add(inputButton);


		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel1.setBorder(BorderFactory.createTitledBorder("Results"));
		panel1.add(panelInput1);
		panel1.add(panelInput2);

		JPanel buttons = new JPanel();
		buttons.add(ok);
		buttons.add(cancel);

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.add(panel1);
		main.add(Box.createRigidArea(new Dimension(1,15)));

		Container contentPaneFrame = this.getContentPane();
		contentPaneFrame.add(main, BorderLayout.CENTER);
		contentPaneFrame.add(buttons, BorderLayout.SOUTH );
//		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+100,CoreGUIPlugin.mainFrame.getLocationY()+100);
		this.setTitle("Select...");
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.pack();
		//setLocationRelativeTo(parentFrame);
		int dec_x = (CoreGUIPlugin.mainFrame.getWidth()-this.getWidth())/2;
		int dec_y = (CoreGUIPlugin.mainFrame.getHeight()-this.getHeight())/2;
		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+dec_x,
						 CoreGUIPlugin.mainFrame.getLocationY()+dec_y);

	}

	public int getState() {
		return state;
	}

	public String getSelection() {
		return selection;
	}

	/**
	 * Show a select dialog
	 * @param parentFrame
	 * @param modal
	 * @return 
	 */
	public static int showDialog(Frame parentFrame, boolean modal) {
		SelectDialog sd = new SelectDialog(parentFrame, modal);
		sd.setVisible(true);
		
		return sd.getState();
	}
	
	protected void openFileChooser(JTextField textField) {
		JFileChooser fileChooser = null;
		if (textField.getText()!=null && !textField.getText().equals("")){
			fileChooser = new JFileChooser(textField.getText());
		}else{
			fileChooser = new JFileChooser();
		}
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setFileFilter(new FileUtilities.FilterDir());
		int returnVal = 0;
		returnVal =  fileChooser.showDialog(SelectDialog.this, "Select location of results directory");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String src = fileChooser.getSelectedFile().getAbsolutePath();
			textField.setText(src);
		}
	}

}
