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
 * File Name   : launchScreenShotComparison.java
 *
 * Created     : 04/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.compModel.DirectoryFileFilter;
import com.orange.atk.compModel.Model;
import com.orange.atk.compModel.ProgressListener;


public class launchScreenShotComparison  extends JFrame implements ProgressListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6504247048662617994L;
	
	public static ImageIcon icon = null;
	private static final String icondescr = "ATK";
	
	private String basePath="c:";
	private JTextField jrefdir;
	private JTextField jTestdir;
	private JProgressBar bar;
	private JButton jButGo;
	private ProgressListener progressListener;
	
	public launchScreenShotComparison(String refPath, String testPath) {
		super("Choose directory to compare screenshots");
		
		URL iconURL = CoreGUIPlugin.getMainIcon();
		icon = new ImageIcon(iconURL, icondescr);
		setIconImage(icon.getImage());
		
		progressListener = this;
		CompoundBorder innerCompound = new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new EmptyBorder(0,0,0,0));
		CompoundBorder outerCompound = new CompoundBorder(new LineBorder(Color.DARK_GRAY, 1), innerCompound);
		UIManager.put("ProgressBar.border",outerCompound);
		UIManager.put("ProgressBar.cellLength",new Integer(10));
		UIManager.put("ProgressBar.cellSpacing",new Integer(2));
	  	bar = new JProgressBar();
		if(refPath !=null) 
			basePath = refPath;
		
		JPanel mainpanel = (JPanel) getContentPane();
		mainpanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridheight=2;
		c.gridwidth =2;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,5,0,5);

		mainpanel.add(new JLabel("<html><b>Choose directories where screenshots" +
				" are compared one to one.</b><br/>" +
				"For each image in Test directory, we search an image with similar name <br/>" +
				"in Reference directory and show difference. <br/> <br/> </html>"),c);

		c.gridheight=1;
		c.gridy = 2;
		mainpanel.add(new JLabel("<html><u>Reference Directory :</u></html>"),c);

		c.gridy = 4;
		mainpanel.add(new JLabel("<html><u>Test Directory :</u></html>"),c);
		
		Insets panelInsets = new Insets(5,5,5,5);
		c.insets = panelInsets;
		c.gridy = 3;c.gridwidth=1;
		jrefdir = new JTextField(30);
		mainpanel.add(jrefdir,c);
		jrefdir.setText(refPath);
		
		c.gridx=1;
		Insets browseInsets = new Insets(5,0,5,5);
		c.insets = browseInsets;
		JButton jbutref = new JButton("Browse");
		jbutref.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String result = chooseDirectory("Reference");
				jrefdir.setText(result);
			}
			
		});
		add(jbutref,c);
		
		c.gridy = 5;c.gridx=0;
		jTestdir = new JTextField(30);
		jTestdir.setText(testPath);
		c.insets = panelInsets;
		mainpanel.add(jTestdir,c);
		
		c.gridx=1;
		c.insets = browseInsets;
		JButton jbuttest = new JButton("Browse");
		jbuttest.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String result = chooseDirectory("Test");
				jTestdir.setText(result);
			}
			
		});
		add(jbuttest,c);
		c.fill = GridBagConstraints.NONE;
		c.gridy=6; c.gridx=0;c.gridwidth=2;c.anchor = GridBagConstraints.SOUTH;
		c.insets = panelInsets;
		
		jButGo  = new JButton("<html><I><B>Compare</i></b></html>");
		jButGo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				compare();
			}
		});
		add(jButGo,c);
		c.gridy=7; c.gridx=0;c.gridwidth=2;c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,30,5,30);
		bar.setMinimum(0);
		bar.setMaximum(100);
		bar.setVisible(false);
		Dimension prefSize = bar.getPreferredSize();
		prefSize.height = 20;
		bar.setPreferredSize(prefSize);
	  	add(bar,c);
		pack();
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void compare() {
		String refPath = jrefdir.getText();
		String testPath = jTestdir.getText();
		
		if(refPath==null || !new File(refPath).exists()||
					testPath==null || !new File(testPath).exists())
		return;
		
		bar.setValue(0);
		bar.setVisible(true);
		pack();
		
		jButGo.setEnabled(false);
		Thread t = new Thread() {
			  public void run() {
				try {
					Model model = new Model(jrefdir.getText(), jTestdir.getText(), progressListener);
					ComparatorFrame comp = new ComparatorFrame(model);
					model.setProgressListener((ProgressListener) comp);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							e.toString(),
							"Warning",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					
				}	
				dispose();
			  }
		};
		t.start();
			
	}
	
	public void setProgressValue(int pourcent) {
		bar.setValue(pourcent);	
	}
	public void setNbFailed(int value) {
		// NOTHING TO DO	
	}

	
	private String chooseDirectory(String directoryType) {
		if ("Reference".equals(directoryType)) {
			if( new File(jrefdir.getText()).exists() )
				basePath = jrefdir.getText();
		} else {
			if( new File(jTestdir.getText()).exists() )
				basePath = jTestdir.getText();
		}
		
		JFileChooser fc = new JFileChooser(basePath);
		
		fc.setFileFilter(new DirectoryFileFilter());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int res = fc.showDialog(this,"Open as "+directoryType+" directory");
		if (res == JFileChooser.APPROVE_OPTION) {
			File fileRef = fc.getSelectedFile();
			basePath = fileRef.getAbsolutePath();
			
			return fileRef.getAbsolutePath();
		}
		return basePath;


	}

	public static void main(String args[]) {

		//find log4j configfile
		if(!Configuration.loadConfigurationFile("config.properties"))
				return;
		DOMConfigurator.configure("log4j.xml");
		
		switch(args.length){
		case 0:
			new launchScreenShotComparison(null,null);
			break;
		case 3:
		case 2:
			if(new File(args[0]).isDirectory() &&
					new File(args[1]).isDirectory()) {
				Model model = new Model(args[0], args[1]);
				model.printPDFReport();
				Logger.getLogger(launchScreenShotComparison.class ).
					debug("results write on directory : "+model.getTestDirectory());
			} else {
				Logger.getLogger(launchScreenShotComparison.class ).
						warn("At least one of the arguments is not a directory path");
			}
			break;
		default :
			Logger.getLogger(launchScreenShotComparison.class ).
							debug("argument 1: Reference Directory");
			Logger.getLogger(launchScreenShotComparison.class ).
								debug("argument 2: Test Directory");
		}
	}
	
}
