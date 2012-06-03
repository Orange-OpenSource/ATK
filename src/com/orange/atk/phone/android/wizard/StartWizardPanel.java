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
 * File Name   : StartWizardPanel.java
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.orange.atk.phone.android.AndroidDriver;

public class StartWizardPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8710040730823988913L;
	private JLabel phoneLabel = new JLabel();
	private JLabel infoLabel = new JLabel();
	private JLabel questionLabel = new JLabel();
	private JButton yesButton = new JButton();
	private JButton noButton = new JButton();
	private AndroidWizard wizard;
	
	public StartWizardPanel(AndroidWizard wizard, AndroidDriver phone) {
		super(new GridBagLayout());
		this.wizard = wizard;
		GridBagConstraints gbc = new GridBagConstraints(
				0,0, //gridx, gridy
				2,1, //gridwidth, gridheight
				0.5,0.5, //weightx, weighty
				GridBagConstraints.WEST, // anchor
				GridBagConstraints.NONE, // FILL
				new Insets(1,1,1,1), // padding top, left, bottom, right
				0,0); //ipadx, ipady

		phoneLabel.setText("A new Android phone "+phone.getName()+" has been plugged, but is not configured in ATK.");
		infoLabel.setText("Android Wizard will help you to generate a configuration file template for your phone.");
		questionLabel.setText("Do you want to start it ?");
		yesButton.setText("Yes start >>");
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startWizard();
			}
		});
		noButton.setText("No");
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		
		this.add(phoneLabel, gbc);
		gbc.gridy = 1;
		this.add(infoLabel, gbc);
		gbc.gridy = 2;
		this.add(questionLabel, gbc);
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(noButton, gbc);
		gbc.gridx=1;
		gbc.anchor = GridBagConstraints.WEST;
		this.add(yesButton, gbc);
	}

	private void exit() {
		wizard.dispose();
	}
	
	private void startWizard() {
		wizard.nextStep();
	}
	
	
}
