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
 * File Name   : ConfigurationDialog.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;

/**
 * This is a configuration dialog to configure Matos. 
 * This Dialog is composed with tabs.
 * A "Commons" tab is used to set commons options.
 * Each plugin provides a configuration panel that will have it's own tab 
 */
public class ConfigurationDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private JCheckBox enableProxyCB = null;
	//private JCheckBox powerMonitor = null;
	private JTextField voutTextField = null;
	private JTextField bsizeTextField = null;
	private JCheckBox savePT4FileCB = null;
	private JCheckBox realTimeGraph = null;
	
	private JTextField hostTF = null;
	private JTextField portTF = null;
	private JTextField cssTF = null;
	private JTextField inputdirTF = null;

	private JRadioButton keepReportRadioButton = null;
	private JTextField outTF = null;
	private JCheckBox useSpecADB = null;
	private JCheckBox useNetworkMonitor = null;
	private JTextField ADBPath = null;
	private String rotationValue = "0";
	private JTextField benchmarkDir;
	
	/**
	 * Construct a new configuration dialog.
	 *
	 */
	public ConfigurationDialog(){
		super(CoreGUIPlugin.mainFrame, true);
		
		JComponent component = this.getRootPane();
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "actionOKConfiguration");
		component.getActionMap().put("actionOKConfiguration", new AbstractAction(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				okAction();
			}
		});
		
		this.setTitle("Configuration...");
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Commons", getCommonsPanel());
	
		tabs.addTab("External tools", getExternalToolsPanel());
		tabs.addTab("Screenshots", getScreenshotsPanel());
		tabs.addTab("Benchmarks", getBenchmarksPanel());
		
		this.add(tabs, BorderLayout.CENTER);
		this.add(getOKCancelPanel(), BorderLayout.SOUTH);
		
		this.pack();

		int dec_x = (CoreGUIPlugin.mainFrame.getWidth()-this.getWidth())/2;
		int dec_y = (CoreGUIPlugin.mainFrame.getHeight()-this.getHeight())/2;
		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+dec_x,
						 CoreGUIPlugin.mainFrame.getLocationY()+dec_y);

		this.setVisible(true); 
	}
	/**
	 * Builds the panel that allow benchmarks reports configuration parameters 
	 * @return a JPanel to configure commons parameters
	 */
	private JPanel getBenchmarksPanel() {
		JPanel bench = new JPanel();
		bench.setLayout(new BoxLayout(bench, BoxLayout.Y_AXIS));
		JPanel reportPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel pathLabel = new JLabel("Path to benchmark reports:");
		benchmarkDir = new JTextField( Configuration.getProperty(Configuration.BENCHMARKDIRECTORY), 20);
		reportPathPanel.add(pathLabel);
		reportPathPanel.add(benchmarkDir);
		final JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser(benchmarkDir, true);
			}
		});
		reportPathPanel.add(browseButton);
		bench.add(reportPathPanel, BorderLayout.WEST);
		return bench;
	}
	/**
	 * Builds the panel that allow to External tools configuration parameters 
	 * @return a JPanel to configure commons parameters
	 */
	private JPanel getExternalToolsPanel() {
		JPanel externalTools = new JPanel();
		externalTools.setLayout(new BoxLayout(externalTools, BoxLayout.Y_AXIS));
		
		JPanel adbPanel = new JPanel();
		adbPanel.setLayout(new BorderLayout());
		adbPanel.setBorder(BorderFactory.createTitledBorder("Android Debug Bridge"));
		
		JPanel adbPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel pathLabel = new JLabel("Path to adb:");
		ADBPath = new JTextField( Configuration.getProperty(Configuration.ADBPATH), 20);
		adbPathPanel.add(pathLabel);
		adbPathPanel.add(ADBPath);
		final JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser(ADBPath, true);
			}
		});
		adbPathPanel.add(browseButton);
		adbPanel.add(adbPathPanel, BorderLayout.WEST);

		boolean useSpecADBValue=  Boolean.valueOf(Configuration.getProperty(Configuration.SPECIFICADB, "false"));
		useSpecADB = new JCheckBox("Use specific ADB", useSpecADBValue);
		useSpecADB.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pathLabel.setEnabled(useSpecADB.isSelected());
				ADBPath.setEnabled(useSpecADB.isSelected());
				browseButton.setEnabled(useSpecADB.isSelected());
			}
		});
		if (!useSpecADBValue) {
			pathLabel.setEnabled(false);
			ADBPath.setEnabled(false);
			browseButton.setEnabled(false);			
		}
		adbPanel.add(useSpecADB, BorderLayout.NORTH);

		JPanel networkPanel = new JPanel();
		networkPanel.setLayout(new BorderLayout());
		networkPanel.setBorder(BorderFactory.createTitledBorder("Network Monitor"));
		
		boolean useNetworkMonitorValue=  Boolean.valueOf(Configuration.getProperty(Configuration.NETWORKMONITOR, "false"));
		useNetworkMonitor = new JCheckBox("Use Network Monitor", useNetworkMonitorValue);
		networkPanel.add(useNetworkMonitor, BorderLayout.NORTH);
		
		externalTools.add(adbPanel);
		externalTools.add(networkPanel);
		return externalTools;
	}
	
	/**
	 * Builds the panel that allow to configure Screenshots parameters 
	 * @return a JPanel to configure commons parameters
	 */
	private JPanel getScreenshotsPanel() {
		JPanel screenshotsOptions = new JPanel();
		screenshotsOptions.setLayout(new BoxLayout(screenshotsOptions, BoxLayout.Y_AXIS));
		
		JPanel rotationOptionsPanel = new JPanel();
		rotationOptionsPanel.setLayout(new BorderLayout());
		rotationOptionsPanel.setBorder(BorderFactory.createTitledBorder("Rotation"));

		JPanel rotationbuttonsPanel = new JPanel();
		rotationbuttonsPanel.setLayout(new BoxLayout(rotationbuttonsPanel, BoxLayout.Y_AXIS));
	
		JRadioButton noRotationButton = new JRadioButton("No rotation");
		noRotationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotationValue = "0";
			}
		});
		JRadioButton rightRotationButton = new JRadioButton("Rotate 90° right");
		rightRotationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotationValue = "90";
			}
		});
		JRadioButton leftRotationButton = new JRadioButton("Rotate 90° left");
		leftRotationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotationValue = "270";
			}
		});
		JRadioButton downRotationButton = new JRadioButton("Rotate 180°");
		downRotationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotationValue = "180";
			}
		});
		rotationValue = Configuration.getProperty(Configuration.SCROTATION, "0");
		if (rotationValue.equals("0")) noRotationButton.setSelected(true);
		else if (rotationValue.equals("90")) rightRotationButton.setSelected(true);
		else if (rotationValue.equals("180")) downRotationButton.setSelected(true);
		else if (rotationValue.equals("270")) leftRotationButton.setSelected(true);
		ButtonGroup group = new ButtonGroup();
		group.add(noRotationButton);
		group.add(rightRotationButton);
		group.add(leftRotationButton);
		group.add(downRotationButton);

		rotationbuttonsPanel.add(noRotationButton);
		rotationbuttonsPanel.add(rightRotationButton);
		rotationbuttonsPanel.add(leftRotationButton);
		rotationbuttonsPanel.add(downRotationButton);

		rotationOptionsPanel.add(rotationbuttonsPanel);
		screenshotsOptions.add(rotationOptionsPanel);
		return screenshotsOptions;
	}

	public void setRotationOption(int rotationValue) {
		
	}
	
	public int getRotationOption() {
		
		return 0;
	}
	
	/**
	 * 
	 * Builds the panel that allow to modify commons configuration parameters 
	 * @return a JPanel to configure commons parameters
	 */
	private JPanel getCommonsPanel() {
		JPanel commons = new JPanel();
		commons.setLayout(new BoxLayout(commons, BoxLayout.Y_AXIS));
		
		JPanel proxy = new JPanel();
		proxy.setLayout(new BorderLayout());
		proxy.setBorder(BorderFactory.createTitledBorder("HTTP Proxy settings"));
		String enableProxy = Configuration.getProperty(Configuration.PROXYSET, "false");
		enableProxyCB = new JCheckBox("Enable HTTP Proxy", Boolean.valueOf(enableProxy));
		proxy.add(enableProxyCB, BorderLayout.NORTH);
		
		JPanel hostAndPortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		hostAndPortPanel.add(Box.createHorizontalStrut(5));
		final JLabel hostLabel = new JLabel("Host:");
		String proxyHost = Configuration.getProperty(Configuration.PROXYHOST); 
		hostTF = new JTextField(proxyHost, 15);
		final JLabel portLabel = new JLabel("Port:");
		String proxyPort = Configuration.getProperty(Configuration.PROXYPORT); 
		portTF = new JTextField(proxyPort, 5);
		hostAndPortPanel.add(hostLabel);
		hostAndPortPanel.add(hostTF);
		hostAndPortPanel.add(Box.createHorizontalStrut(5));
		hostAndPortPanel.add(portLabel);
		hostAndPortPanel.add(portTF);
		enableProxyCB.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				hostLabel.setEnabled(enableProxyCB.isSelected());
				hostTF.setEnabled(enableProxyCB.isSelected());
				portLabel.setEnabled(enableProxyCB.isSelected());
				portTF.setEnabled(enableProxyCB.isSelected());
			}
		});
		proxy.add(hostAndPortPanel, BorderLayout.SOUTH);

		commons.add(proxy);

		JPanel results = new JPanel();
		results.setLayout(new BorderLayout());
		results.setBorder(BorderFactory.createTitledBorder("Analysis results"));

		JPanel cssPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cssPanel.add(new JLabel("Report's look&feel: "));
		//cssPanel.setBorder(BorderFactory.createTitledBorder("Report's look&feel"));
		cssPanel.add(Box.createHorizontalStrut(5));
		String css = Configuration.getProperty(Configuration.CSS);
		cssTF = new JTextField(css, 20);
		cssPanel.add(cssTF);
		cssPanel.add(Box.createHorizontalStrut(5));
		JButton browseCss = new JButton("Browse");
		browseCss.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser(cssTF, false);
			}
		});
		cssPanel.add(browseCss);

		
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(new JLabel("Default Input Dir: "));
		inputPanel.add(Box.createHorizontalStrut(5));

		String inputdir = null;
		try {
			inputdir = Configuration.getProperty(Configuration.INPUTDIRECTORY);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass() ).debug(" no input dir set in configuration file [ConfigurtionDialog]");
		}
		inputdirTF = new JTextField(inputdir, 20);
		inputPanel.add(inputdirTF);
		inputPanel.add(Box.createHorizontalStrut(5));
		JButton browseinputdir = new JButton("Browse");
		browseinputdir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser(inputdirTF, true);
			}
		});
		inputPanel.add(browseinputdir);
		results.add(inputPanel, BorderLayout.SOUTH);
		JPanel storagePane2 = new JPanel(new BorderLayout());
	//	storagePane2.add(cssPanel, BorderLayout.NORTH);
		storagePane2.add(inputPanel, BorderLayout.SOUTH);
		
		results.add(storagePane2, BorderLayout.NORTH);

		
		//add checkbox
		
		String realtimeg = Configuration.getProperty(Configuration.REALTIMEGRAPH, "false");
		realTimeGraph = new JCheckBox("Real Time Graph", Boolean.valueOf(realtimeg));
		
		
		
		JPanel jPanecheck = new JPanel(new BorderLayout());
		JPanel jprealtime = new JPanel(new BorderLayout());
		
		jprealtime.add(realTimeGraph, BorderLayout.NORTH);	
		//no add in south

		jPanecheck.add(jprealtime, BorderLayout.SOUTH);

		
		results.add(jPanecheck, BorderLayout.NORTH);
		
		
		
		
		JPanel storagePanel = new JPanel(new BorderLayout());
		JPanel keep = new JPanel(new BorderLayout());
		JPanel keepLocation = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		keepReportRadioButton = new JRadioButton("Keep reports in default directory");
	//	JRadioButton dontKeepReportRadioButton = new JRadioButton("Don't keep reports");
		ButtonGroup grp = new ButtonGroup();
		grp.add(keepReportRadioButton);
	//	grp.add(dontKeepReportRadioButton);
		boolean keepConfVal = Boolean.parseBoolean(Configuration.getProperty(Configuration.KEEPREPORT));
		keepReportRadioButton.setSelected(keepConfVal);
	//	dontKeepReportRadioButton.setSelected(!keepConfVal);
		keep.add(keepReportRadioButton, BorderLayout.NORTH);
		String outputDirectory = Configuration.getProperty(Configuration.OUTPUTDIRECTORY);
		outTF = new JTextField(outputDirectory, 25);
		keepLocation.add(outTF);
		keepLocation.add(Box.createHorizontalStrut(5));
		final JButton browseOut = new JButton("Browse");
		browseOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser(outTF, true);
			}
		});
		keepReportRadioButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				outTF.setEnabled(keepReportRadioButton.isSelected());
				browseOut.setEnabled(keepReportRadioButton.isSelected());
			}
			
		});
		keepLocation.add(browseOut);
		keep.add(keepLocation, BorderLayout.SOUTH);
		storagePanel.add(keep, BorderLayout.NORTH);
	//	storagePanel.add(dontKeepReportRadioButton, BorderLayout.SOUTH);
		
		results.add(storagePanel, BorderLayout.SOUTH);
		
		commons.add(results);

		return commons;
	}

	/**
	 * Builds the OK-Cancel panel
	 * @return ok and Cancel buttons in a panel panel
	 */
	private JPanel getOKCancelPanel() {
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				okAction();				
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurationDialog.this.dispose();
			}
		});
		JPanel OKCancelPanel = new JPanel();
		OKCancelPanel.add(ok);
		OKCancelPanel.add(cancel);

		return OKCancelPanel;
	}

	/**
	 * Action performed when user clicks on "OK" button or presses 
	 * the "Enter" key.
	 */
	protected void okAction() {
		Cursor lastCursor = ConfigurationDialog.this.getCursor();
		ConfigurationDialog.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		// save commons config parameters
		Configuration.setProperty(Configuration.PROXYSET, Boolean.toString(enableProxyCB.isSelected()));
		Configuration.setProperty(Configuration.PROXYHOST, hostTF.getText());
		Configuration.setProperty(Configuration.PROXYPORT, portTF.getText());
		Configuration.setProperty(Configuration.REALTIMEGRAPH, Boolean.toString(realTimeGraph.isSelected()));

		// update proxy config now

		Properties sysProperties = System.getProperties();	
		sysProperties.put("proxySet", enableProxyCB.isSelected());
		sysProperties.put("http.proxySet", enableProxyCB.isSelected());
		sysProperties.put("proxyHost", hostTF.getText());
		sysProperties.put("http.proxyHost", hostTF.getText());
		sysProperties.put("proxyPort", portTF.getText());
		sysProperties.put("http.proxyPort", portTF.getText());
				
		Configuration.setProperty(Configuration.CSS, cssTF.getText());
		Configuration.setProperty(Configuration.KEEPREPORT, Boolean.toString(keepReportRadioButton.isSelected()));
		Configuration.setProperty(Configuration.OUTPUTDIRECTORY, outTF.getText());
		Configuration.setProperty(Configuration.INPUTDIRECTORY, inputdirTF.getText());

		// save external tools config parameters
		Configuration.setProperty(Configuration.SPECIFICADB, Boolean.toString(useSpecADB.isSelected()));
		Configuration.setProperty(Configuration.ADBPATH, ADBPath.getText());
		Configuration.setProperty(Configuration.BENCHMARKDIRECTORY, benchmarkDir.getText());
	
		Configuration.setProperty(Configuration.NETWORKMONITOR, Boolean.toString(useNetworkMonitor.isSelected()));
		
		Configuration.setProperty(Configuration.SCROTATION, rotationValue);

		try{
		 Configuration.writeProperties();
		}catch (Alert a){
			JOptionPane.showMessageDialog(
					ConfigurationDialog.this,
					"Problem while writing the configuration file.",
					"Error !" +a.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}		

		ConfigurationDialog.this.setCursor(lastCursor);
		ConfigurationDialog.this.dispose();
	}
		
	/**
	 * Open a file chooser initialized with the content of the given textfield and put the chosen path
	 * in it at the closing of the file chooser.
	 * @param textField text field which contains the file path.
	 */
	private void openFileChooser(JTextField textField, boolean dir) {
		JFileChooser fileChooser = null;
		if (textField.getText()!=null && !textField.getText().equals("")){
			fileChooser = new JFileChooser(textField.getText());
		}else{
			fileChooser = new JFileChooser();
		}
		String title = null;
		if (dir) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			title = "Select a directory";
		} else {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileUtilities.Filter("CSS file [*.css]", ".css"));
			title = "Select CSS file";
		}
		int returnVal = 0;
		returnVal =  fileChooser.showDialog(ConfigurationDialog.this, title);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String src = fileChooser.getSelectedFile().getAbsolutePath();
			if (!dir)
			src = FileUtilities.verifyExtension(src, ".css");
			textField.setText(src);
		}
	}
	
	
	
	
}
