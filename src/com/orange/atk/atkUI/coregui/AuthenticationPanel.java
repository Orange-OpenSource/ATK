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
 * File Name   : AuthenticationPanel.java
 *
 * Created     : 26/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.orange.atk.atkUI.coregui.utils.SpringUtilities;

/**
 * Panel to add login, password and user-agent for an authentication
 * to a download platform
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class AuthenticationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel loginLabel;
	private JTextField loginTF;
	private JLabel passwordLabel;
	private JTextField passwordTF;
	private JLabel userAgentLabel;
	private JTextField userAgentTF;

	/**
	 * Builds a <code>AuthenticationPanel</code>
	 *
	 */
	public AuthenticationPanel(){

		// Authentication to download platform
		loginLabel = new JLabel("Login: ");
		loginTF = new JTextField(30);
		loginLabel.setEnabled(false);
		loginTF.setEnabled(false);
		passwordLabel = new JLabel("Password: ");
		passwordTF = new JTextField(30);
		passwordLabel.setEnabled(false);
		passwordTF.setEnabled(false);
		userAgentLabel = new JLabel("User-Agent: ");
		userAgentTF = new JTextField(30);
		userAgentLabel.setEnabled(false);
		userAgentTF.setEnabled(false);

		setLayout(new SpringLayout());
		setBorder(BorderFactory.createTitledBorder("Authentication for download platform"));
		add(loginLabel);
		add(loginTF);
		add(passwordLabel);
		add(passwordTF);
		add(userAgentLabel);
		add(userAgentTF);
		SpringUtilities.makeCompactGrid(this, 3, 2, 6, 6, 6, 6);

	}

	/**
	 * Enables or disables the panel
	 */
	public void setEnabled(boolean enable){
		loginTF.setEnabled(enable);
		loginLabel.setEnabled(enable);
		passwordTF.setEnabled(enable);
		passwordLabel.setEnabled(enable);
		userAgentTF.setEnabled(enable);
		userAgentLabel.setEnabled(enable);
	}

	public String getLogin(){
		return loginTF.getText().trim();
	}

	public void setLogin(String login){
		loginTF.setText(login);
	}

	public String getPassword(){
		return passwordTF.getText().trim();
	}

	public void setPassword(String password){
		passwordTF.setText(password);
	}

	public String getUserAgent(){
		return userAgentTF.getText().trim();
	}

	public void setUserAgent(String userAgent){
		userAgentTF.setText(userAgent);
	}
}
