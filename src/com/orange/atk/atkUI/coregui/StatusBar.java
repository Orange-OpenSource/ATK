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
 * File Name   : StatusBar.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.coregui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;

import com.orange.atk.atkUI.corecli.IProgressMonitor;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.PhoneInterface;



public class StatusBar extends JPanel implements IProgressMonitor {

	private static final long serialVersionUID = 1L;
	/** Messages area */
	private JLabel messageLabel = new JLabel();
	/** Progress monitor */
	private JProgressBar progressBar = new JProgressBar();
	/** Button to display Error Frame */
	private JButton errorButton = new JButton(ResourceManager.getInstance().getString("SEE"));
	/** Message displayed when an error occured */
	private static final String AN_ERROR_OCCURED = " "+ResourceManager.getInstance().getString("ERROR_FRAME_DEFAULT_TITLE")+" ";
	/** the split pane*/
	private JSplitPane splitPane;
	/** The current progress of the task */
	private int current_progress = 0;
	/** Lenght of task */
	private int length = 0;
	/** A flag indicating the need to stop the task. */
	private boolean shouldStop = false;

	protected PhoneInterface phone =null;

	public StatusBar(String message) {
		this.messageLabel.setText(message);
		this.setLayout(new BorderLayout());

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
		messagePanel.add(messageLabel);
		messagePanel.add(errorButton);
		errorButton.setVisible(false);

		JPanel progressPanel = new JPanel(new BorderLayout());
		//		progressBar.setMaximum(100);
		//		progressBar.setValue(33);
		progressBar.setStringPainted(false);

		Font font1 = new Font("Sans Serif", Font.ITALIC, 11);
		progressBar.setFont(font1);
		Font font2 = new Font("Sans Serif", Font.PLAIN, 11);
		messageLabel.setFont(font2);
		Font font3 = new Font("Default",Font.PLAIN,11);
		errorButton.setFont(font3);
		errorButton.setForeground(Color.RED);
		errorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ErrorManager.getInstance().displayErrorFrame();
				clearErrorMessage();
			}
		});

		progressPanel.add(progressBar, BorderLayout.CENTER);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messagePanel, progressPanel);
		splitPane.setDividerSize(2);
		//Provide minimum sizes for the two components in the split pane
		messageLabel.setMinimumSize(new Dimension(400, 10));
		progressPanel.setMinimumSize(new Dimension(200, 10));
		progressPanel.setMaximumSize(new Dimension(200, 10));
		this.add(splitPane, BorderLayout.CENTER);
		//this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

	}

	/**
	 * Post UI initialization.
	 * To be called when the status bar is visible.
	 */
	public void uiPostInit() {
		splitPane.setDividerLocation(0.8);
	}

	public void setLength(int length) {
		this.length = length;
		if (length>0) progressBar.setMaximum(length);
		progressBar.setStringPainted( length>0 ); // to display progress perceantage
		progressBar.setIndeterminate( length<0 );
	}

	public void clearJob(String endMessage) {
		setMessage(endMessage);
		clearJob();
	}
	
	public void clearJob() {
		//clear messages & progressbar
		setLength(0);
		current_progress = 0;
		progressBar.setValue(0);
		shouldStop = false;
	}

	public void stopJob() {
		clearJob();
		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		CoreGUIPlugin.mainFrame.enableUserActions(true);
	}
	//-=-=-=- methods from IProgressMonitor -=-=-=-=-

	public void increment() {
		increment(null);
	}

	public void increment(String message) {
		if (length>0) {
			current_progress++;
			progressBar.setValue(current_progress);
			if(message!=null) messageLabel.setText(message);
			if (current_progress>=length) { // job done!
				//	clearJob("done");
			}
		}
	}

	public void setMessage(String message) {
		messageLabel.setText(message);
	}

	public void displayErrorMessage() {
		errorButton.setVisible(true);
		messageLabel.setText(AN_ERROR_OCCURED);
		messageLabel.setForeground(Color.RED);
	}

	private void clearErrorMessage() {
		errorButton.setVisible(false);
		messageLabel.setText("");
		messageLabel.setForeground(Color.BLACK);
	}

	/**
	 * Raises a flag indicating the task should stop as soon as possible.
	 */
	public void setStop() {
		shouldStop = true;
		setMessage("Task aborted");
	}
	/**
	 * Tests the stop flag.
	 * @return true if tha task have to stop as soon as possible, false otherwise.
	 */
	public boolean isStop() {
		return shouldStop;
	}

	//-=-=-=- End of methods from IProgressMonitor -=-=-=-=-

}
