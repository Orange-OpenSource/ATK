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
 * File Name   : HopperStepDialog.java
 *
 * Created     : 30/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.coregui.AbstractStepDialog;
import com.orange.atk.atkUI.coregui.AuthenticationPanel;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;


/**
 * This is a specialization of <code>AbstractStepDialog</code> dialogs
 * for <code>FlashStep</code>.
 * @author Aurore PENAULT, Nicolas MOTEAU
 * @since JDK5.0
 */
public abstract class HopperStepDialog extends AbstractStepDialog  implements ActionListener{

	private static final long serialVersionUID = 1L;
	protected JTextField fileFilter;
	protected JButton ok;
	protected JButton cancel;

	/**
	 * Path of last selected file (used to re-open the file chooser at the same place)
	 */
	protected String lastFilePath = null;

	protected File flashFile;
	protected String testName;
	protected String flashName;
	private String[] allUID;
	private String[] listUID;
	private JList jList1;
	protected HopperCheckListTable clt;

	protected MatosGUI mainFrame = CoreGUIPlugin.mainFrame;

	private JScrollPane jScrollPane1;

	/**
	 * Builds a JavaStep dialog.
	 */

	public HopperStepDialog() {
		super();

		authenticationPanel = new AuthenticationPanel();
		clt = (HopperCheckListTable)guihopperLink.getFlashGUI().getCheckListTable();

		fileFilter = new JTextField(30);
		fileFilter.setEditable(true);

		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filePanel.add(Box.createHorizontalStrut(10));

		filePanel.add(fileFilter);
		fileFilter.addActionListener(this);

		JPanel globalFilePanel = new JPanel();
		globalFilePanel.setLayout(new BoxLayout(globalFilePanel, BoxLayout.Y_AXIS));
		globalFilePanel.add(filePanel);

		PhoneInterface currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
		if(currentPhone instanceof DefaultPhone )
		{ 
			JOptionPane.showMessageDialog(null, "Can't Detect device");
				return;
		}

		allUID =null;
		AutomaticPhoneDetection.getInstance().pauseDetection();
		allUID = currentPhone.getRandomTestList();
		AutomaticPhoneDetection.getInstance().resumeDetection();
		
		if (allUID!=null) {
			jList1 = new JList(allUID);
			jList1.setDoubleBuffered(false);
			jScrollPane1 = new JScrollPane(jList1);
			listUID = allUID;

			globalFilePanel.setBorder(new TitledBorder("Start writing and press enter to filter"));

			ok = new JButton("OK");
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
	
					int[] indices =jList1.getSelectedIndices();
					String listProg="";
					for(int j=0;j<indices.length;j++)
					{
						if(!listProg.equals("")) listProg=listProg+","	;
						listProg=listProg+listUID[indices[j]];	
					}
					Logger.getLogger(this.getClass() ).debug("Selected array"+listProg);
	
					fileFilter.setText(listProg);
					launchAction();
				}
			});
		} else {
			globalFilePanel.setBorder(new TitledBorder("Please enter manually the application name(s)"));

			ok = new JButton("OK");
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					launchAction();
				}
			});
		}
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				HopperStepDialog.this.dispose();
			}
		});


		JPanel buttonsPanel = new JPanel();	

		buttonsPanel.add(ok);
		buttonsPanel.add(cancel);

		getRootPane().setDefaultButton(ok);

		Container contentPaneFrame = this.getContentPane();
		contentPaneFrame.add(globalFilePanel, BorderLayout.NORTH);
		if(jScrollPane1!=null)
			contentPaneFrame.add(jScrollPane1, BorderLayout.CENTER);
		contentPaneFrame.add(buttonsPanel, BorderLayout.SOUTH);
		
		setLocationRelativeTo(CoreGUIPlugin.mainFrame);
		if (allUID!=null) this.setSize(new Dimension(370,400));
		else this.setSize(new Dimension(370,130));
		ok.requestFocusInWindow();
	}

	
    // Show text when user presses ENTER. 
    public void actionPerformed(ActionEvent ae) { 
      filterList();
    } 
	
    private void filterList(){
    	String filterValue = fileFilter.getText();
    	int number = 0;
        for(String UID : allUID){
    		if(UID.contains(filterValue)){
    			number++;
    		}
    	}
        listUID =  new String[number];
        int i=0;
        for(String UID : allUID){
    		if(UID.contains(filterValue)){
    			listUID[i] = UID;
    			i++;
    		}
    	}

    	jList1.setListData(listUID);

    }
    
	/**
	 * Adds a step to the flash table.
	 *
	 */
	protected void launchAction() {
		try{
			action();
			if (!fileError){
				HopperStepDialog.this.dispose();
			}
			fileError = false;
		}catch (Alert a){
			JOptionPane.showMessageDialog(
					HopperStepDialog.this,
					a.getMessage(),
					"Error !",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	protected abstract void action() throws Alert;

	/**
	 * Compute the short name of a file from its URI.
	 * @param uri file URI
	 * @return a string that is the short name.
	 */
	public static String guessName(String uri) {
		String name = "";
		if (uri.startsWith("http:") && (uri.length()-1)>uri.lastIndexOf("/")){
			name = uri.substring(uri.lastIndexOf("/")+1);//, uri.length()-1);
		}else if (uri.lastIndexOf(File.separator)!=-1 && (uri.length()-1)>uri.lastIndexOf(File.separator)){
			name = uri.substring(uri.lastIndexOf(File.separator)+1);//, uri.length()-1);
		}else{
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

		if (fileFilter.getText()!=null && !fileFilter.getText().equals("")) {
			String file = fileFilter.getText();
			if (file.lastIndexOf(File.separator)!=-1){
				path = file.substring(0, file.lastIndexOf(File.separator));
			}
		}

		fileChooser = new JFileChooser(path);
		String extension=".tst";

		if(AutomaticPhoneDetection.getInstance()
				.isNokia())
			extension=".xml";	
		fileChooser.setFileFilter(new FileUtilities.Filter("ATK Script file [*"+extension+"]", extension));
		selectAndSetTF(fileChooser, extension);
	}

	/**
	 * Fills the text field with th selected file
	 * @param fileChooser
	 * @param extension extension of the selected file
	 */
	private void selectAndSetTF(JFileChooser fileChooser, String extension) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		String tmp = null;
		try {
			tmp = Configuration.getProperty(Configuration.INPUTDIRECTORY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(tmp!=null)
			fileChooser.setCurrentDirectory(new File(tmp));
		int returnVal = fileChooser.showDialog(null, "Select");
		String file="";
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile().getAbsolutePath();
			file = FileUtilities.verifyExtension(file, extension);
			lastFilePath = file;
			fileFilter.setText(file);
			Configuration.setProperty(Configuration.INPUTDIRECTORY, fileChooser.getSelectedFile().getParent());

		}
	}

	/**
	 * Verify correct filling of fields in the dialog box.
	 *
	 */
	protected void verifyAndInitialize() {

		testName = fileFilter.getText();
		flashName = guessName(testName);


	}

}
