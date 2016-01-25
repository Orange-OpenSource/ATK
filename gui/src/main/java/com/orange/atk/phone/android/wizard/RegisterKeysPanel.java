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
 * File Name   : RegisterKeysPanel.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;

import org.apache.log4j.Logger;

import com.android.ddmlib.IDevice;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.android.RecordingThread;

public class RegisterKeysPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AndroidWizard wizard;
	private Hashtable<String,String> detectedChannels; 
	private IDevice device;
	private JButton validateButton = new JButton();
	private JButton abortButton = new JButton();

	private JLabel infoLabel1 = new JLabel();
	private JLabel infoLabel2 = new JLabel();
	private JLabel infoLabel3 = new JLabel();
	private JTextArea registeredKeysArea;
	private JScrollPane registeredKeysScrollPane;
	private RecordingThread recordingThread;
	private int keyboardNb;
	private GridBagConstraints gbc;

	public RegisterKeysPanel(int keyboardNb, AndroidWizard wizard, IDevice device, Hashtable<String,String> detectedChannels, ProgressMonitor progressBar) throws PhoneException {
		super(new GridBagLayout());
		this.wizard = wizard;
		this.detectedChannels = detectedChannels;
		this.device = device;
		this.keyboardNb = keyboardNb;
		new KeyEventNames();
		
		gbc = new GridBagConstraints(
				0,0, //gridx, gridy
				2,1, //gridwidth, gridheight
				0,0, //weightx, weighty
				GridBagConstraints.WEST, // anchor
				GridBagConstraints.NONE, // FILL
				new Insets(1,1,1,1), // padding top, left, bottom, right
				0,0); //ipadx, ipady
		infoLabel1.setText("Keyboard "+wizard.getKeyboard(keyboardNb)+" keys registration :");
		if (keyboardNb==1) infoLabel2.setText("Please press all the keys of your phone, then validate.");
		else infoLabel2.setText("Please press all the unregistered keys left, then validate.");
		infoLabel3.setText("Some keys might not work with this keyboard channel.");
		registeredKeysArea = new JTextArea(8,40);		
		registeredKeysArea.setEditable(false);
		registeredKeysScrollPane = new JScrollPane(registeredKeysArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 

		validateButton.setText("Validate >>");
		validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					validateRegistration();
				} catch (PhoneException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		abortButton.setText("Abort");
		abortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abortATK();
			}
		});
		progressBar.setProgress(20);		
		this.add(infoLabel1, gbc);
		
		gbc.gridy=1;
		this.add(infoLabel2, gbc);
		
		gbc.gridy=2;
		this.add(registeredKeysScrollPane, gbc);
		
		gbc.gridy=3;
		this.add(infoLabel3, gbc);
		
		gbc.gridx=1;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(validateButton, gbc);
		Logger.getLogger(this.getClass()).debug(" RegisterKeysPanel ="+keyboardNb);
		recordingThread = new RecordingThread(device,detectedChannels.get(wizard.getKeyboard(keyboardNb)), new KeyboardEventfilter(wizard, this, progressBar));
		recordingThread.start();
		progressBar.setProgress(80);

	}
	
	public void registerKey(String keyName, int keyCode) {
		registeredKeysArea.setText(registeredKeysArea.getText()+keyName+" code : "+keyCode+"\n");
		repaint();
		wizard.registerKey(keyboardNb,keyName, keyCode);
	}
	
	public void registerKey(String keyName, int avgX, int avgY) {
		registeredKeysArea.setText(registeredKeysArea.getText()+keyName+" X : "+avgX+" Y :"+avgY+"\n");
		repaint();
		wizard.registerSoftKey(keyboardNb,keyName, avgX, avgY);
	}
	
	private void validateRegistration() throws PhoneException {
		int ret = JOptionPane.showConfirmDialog(null,
				"Have you registered all the phone keys?", 
				"Have you registered all the phone keys?",
				JOptionPane.YES_NO_CANCEL_OPTION);
		if(ret==JOptionPane.CANCEL_OPTION)
			return;
		
		if(ret==JOptionPane.YES_OPTION){
			if (recordingThread != null) 
				recordingThread.stoprecording();
			wizard.printReport();			
			return;
		}
		//We are in the case  where not all the button have been registered.
		if(keyboardNb<3){
			int nextKeyboardNb = keyboardNb+1;
			wizard.addStep(new KeyboardChannelPanel(nextKeyboardNb,wizard,device,detectedChannels),
					"Select the channel for the keyboard "+nextKeyboardNb);
		}else{
			JOptionPane.showMessageDialog(null,"The number of Keyboard is bigger than 3... " +
					"It should not happen.");
					
		}
	}
	
	public void info(String text) {
		infoLabel2.setText(text);
		repaint();
	}
	
	public void abort() {
		this.remove(validateButton);
		gbc.gridy=3;		
		gbc.gridx=1;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(abortButton, gbc);
		repaint();
	}
	
	private void abortATK() {
		if (recordingThread != null) recordingThread.stoprecording();
		wizard.exit(true);
	}
}
