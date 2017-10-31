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
 * File Name   : ChannelPanel.java
 *
 * Created     : 25/08/2010
 * Author(s)   : Gurvan LE QUELLENEC
 */
package com.orange.atk.phone.android.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.android.ddmlib.IDevice;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.android.RecordingThread;

abstract class ChannelPanel extends JPanel {
	protected AndroidWizard wizard;
	protected Hashtable<String,String> detectedChannels; 
	protected IDevice device;
	protected JButton validateButton = new JButton();

	protected JLabel infoLabel1 = new JLabel();
	protected JLabel infoLabel2 = new JLabel();
	protected JLabel infoLabel3 = new JLabel();
	private JLabel iconLabel = new JLabel();
	private ImageIcon noEventIcon = new ImageIcon(this.getClass().getResource("images/phonenotready.PNG"));
	private ImageIcon eventIcon = new ImageIcon(this.getClass().getResource("images/phone.PNG"));
	protected JComboBox channelsCombo;
	protected RecordingThread recordingThread;
	private static final long serialVersionUID = 1L;
	protected SimpleEventFilter eventFilter;
	
	public ChannelPanel(AndroidWizard wizard, IDevice device, Hashtable<String,String> detectedChannels) throws PhoneException {
		super(new GridBagLayout());
		this.wizard = wizard;
		this.detectedChannels = detectedChannels;
		this.device = device;
		GridBagConstraints gbc = new GridBagConstraints(
				0,0, //gridx, gridy
				3,1, //gridwidth, gridheight
				0,0, //weightx, weighty
				GridBagConstraints.WEST, // anchor
				GridBagConstraints.NONE, // FILL
				new Insets(1,1,1,1), // padding top, left, bottom, right
				0,0); //ipadx, ipady
		
		Vector<String> channelNames = new Vector<String>();
		Enumeration<String> enumChannels = detectedChannels.keys();
		while(enumChannels.hasMoreElements()) {
			channelNames.add((String)enumChannels.nextElement());
		}
		channelsCombo = new JComboBox(channelNames);
		channelsCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					testChannel((String)e.getItem());		
		        }    
			}
		});
		
		validateButton.setText("Validate >>");
		validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					validateMethod();
				} catch (PhoneException e1) {
					ErrorManager.getInstance().addError(getClass().getName(),"Error in Android Wizard", e1);
				}
			}
		});
		
		iconLabel.setIcon(noEventIcon);
		this.add(infoLabel1, gbc);

		gbc.gridy = 1;
		this.add(infoLabel2, gbc);

		gbc.gridy = 2;
		gbc.gridwidth = 1;
		this.add(iconLabel, gbc);

		gbc.gridx = 1;
		this.add(channelsCombo, gbc);

		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(validateButton, gbc);

		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		this.add(infoLabel3, gbc);
	
		if (channelNames.size()!=0) testChannel(channelNames.get(0));
	}
	
	private void testChannel(String channel) {
		Logger.getLogger(this.getClass()).debug(" test channel ="+channel);
		if (recordingThread!=null) {
			notifyChannelNoEvent();
			recordingThread.stoprecording();
		}
		eventFilter = new SimpleEventFilter(this);
		recordingThread = new RecordingThread(device,detectedChannels.get(channel), eventFilter);
		recordingThread.start();
	}
	
	public synchronized void notifyChannelEvent() {
		iconLabel.setIcon(eventIcon);
		this.repaint();
	}
	
	public synchronized void notifyChannelNoEvent() {
		iconLabel.setIcon(noEventIcon);
		this.repaint();
	}

	abstract void validateMethod() throws PhoneException;
	
}
