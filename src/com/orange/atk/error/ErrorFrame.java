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
 * File Name   : ErrorFrame.java
 *
 * Created     : 15/03/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.error;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.lowagie.text.Font;
import com.orange.atk.internationalization.ResourceManager;

@SuppressWarnings("serial")
class ErrorFrame extends JFrame {
	MultiLineLabel errorLabel;
	JButton detailButton;
	JButton okButton;
	JTextArea detailArea;
	JScrollPane detailScrollPane;
	private final String detailArrowsRight = ResourceManager.getInstance().getString("DETAIL_BUTTON_TITLE")+" >>";
	private final String detailArrowsLeft = "<< "+ResourceManager.getInstance().getString("DETAIL_BUTTON_TITLE");
	private final String defaultTitle = ResourceManager.getInstance().getString("ERROR_FRAME_DEFAULT_TITLE");
	
	public ErrorFrame() {
		super();
		setTitle(defaultTitle);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}

			public void windowClosing(WindowEvent arg0) {
				close();
			}

			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});

		//default constraints
		//top left with no insets or a 0.1 weight (few move on resizing
		GridBagConstraints gbc = new GridBagConstraints(
						 0,0, //gridx, gridy
						 2,1, //gridwidth, gridheight
						 0,0, //weightx, weighty
						 GridBagConstraints.CENTER, // anchor
						 GridBagConstraints.NONE, // FILL
						 new Insets(10,10,5,10), // padding top, left, bottom, right
						 0,0); //ipadx, ipady
		
		// Error message
		errorLabel = new MultiLineLabel(UIManager.getIcon("OptionPane.errorIcon"));
		panel.add(errorLabel, gbc);
		
		// ok Button
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5,10,10,5);
		okButton = new JButton(ResourceManager.getInstance().getString("OK_BUTTON_TITLE"));
		okButton.setFont(okButton.getFont().deriveFont(Font.BOLD,11));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});

		panel.add(okButton, gbc);

		// Detail Button
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5,5,10,10);
		detailButton = new JButton(detailArrowsRight);
		detailButton.setFont(okButton.getFont().deriveFont(Font.BOLD,11));
		detailButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				detailPerformed();
			}
		});
		panel.add(detailButton, gbc);
		
		// Detail Area
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0,10,10,10);
		detailArea = new JTextArea(8,40);		
		detailScrollPane = new JScrollPane(detailArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		detailArea.setEditable(false);
		detailScrollPane.setVisible(false);
		panel.add(detailScrollPane, gbc);

		this.setContentPane(panel);
		this.setMaximumSize(new Dimension(500,500));
		setOnMiddleOfTheScreen();
		this.setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void setOnMiddleOfTheScreen() {
	    // Get the size of the screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    // Determine the new location of the window
	    int w = getSize().width;
	    int h = getSize().height;
	    int x = (dim.width-w)/2;
	    int y = (dim.height-h)/2;
	    
	    // Move the window
	    setLocation(x, y); 
	}
	 


	public void display() {
		display(defaultTitle);
	}
	
	public void display(String title) {
		// update ErrorFrame according to ErrorManager contents
		ErrorManager errorManager = ErrorManager.getInstance();
		int errorNb = errorManager.getErrorsNumber();
		if (errorNb!=0) {
			errorLabel.setText(errorManager.getLastError().toString());
			detailArea.setText(errorManager.getAllErrors().toString());
			if (errorNb>1) detailButton.setEnabled(true);
			else detailButton.setEnabled(false);
			// by default, the detail scroll pane is always hidden
			if (detailScrollPane.isVisible()) {
				detailScrollPane.setVisible(false);
				detailButton.setText(detailArrowsRight);
			}
			pack();
			setVisible(true);
		}
	}

	private void close() {
		setVisible(false);
		dispose();
	}
	
	private void detailPerformed() {
		if (detailScrollPane.isVisible()) {
			detailScrollPane.setVisible(false);
			detailButton.setText(detailArrowsRight);
			pack();
		} else {
			detailScrollPane.setVisible(true);
			detailButton.setText(this.detailArrowsLeft);
			pack();
		}
	}
	
	class MultiLineLabel extends JPanel { 
		JPanel labelsPane;
		JLabel iconLabel;
		
		public MultiLineLabel(Icon icon) { 
		      super(); 
		      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		      iconLabel = new JLabel();
		      iconLabel.setIcon(icon);
		      this.add(iconLabel);
		      labelsPane = new JPanel();
		      labelsPane.setLayout(new BoxLayout(labelsPane, BoxLayout.Y_AXIS));
		      this.add(labelsPane);
		}

		public void setText(String text) {
			labelsPane.removeAll();
		    StringTokenizer st = new StringTokenizer(text, "\n" ); 
		    while(st.hasMoreTokens()) { 
		    	labelsPane.add(new JLabel(st.nextToken())); 
		    } 
		}
	 
	} 

}
