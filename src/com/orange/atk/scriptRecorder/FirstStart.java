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
 * File Name   : FirstStart.java
 *
 * Created     : 30/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class FirstStart extends JDialog {

	private static final long serialVersionUID = 1L;

	public FirstStart(JFrame owner, String version) {
		super(owner); // to have the same icon than the owner	
		setTitle(version);
		
		JPanel frontPanel = new JPanel(new BorderLayout());
		String header = "<html>"+
		"<h2><i> This application can record, play and edit<br/> scenarii on a mobile phone.</i></h2>" +
		"<div>Theses scenarii are displayed on the screen in simple script format.<br/><br/>" +
		"To access in edit functions, use the right-click to show a popupmenu or<br/>"+
		"double-click to modify existing line."+
		"</div>"+
		"</html>";

		frontPanel.add(new JLabel(header, JLabel.CENTER), BorderLayout.CENTER);

		Container contentPaneFrame = this.getContentPane();
		contentPaneFrame.add(frontPanel, BorderLayout.NORTH);
		pack(); 

		/*int dec_x = (owner.getWidth()-this.getWidth())/2;
		int dec_y = (owner.getHeight()-this.getHeight())/2;
		this.setLocation(owner.getLocation().x+dec_x,
				owner.getLocation().y+dec_y);
*/
		setVisible(true);
	}
}
