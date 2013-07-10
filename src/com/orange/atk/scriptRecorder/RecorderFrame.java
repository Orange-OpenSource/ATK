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
 * File Name   : RecorderFrame.java
 *
 * Created     : 16/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.android.uiautomator.DebugBridge;
import com.android.uiautomator.UiAutomatorViewer;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.error.ErrorListener;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.android.AndroidPlugin;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.scriptRecorder.scriptJpanel.ScriptJPanel;

public class RecorderFrame extends JFrame implements ErrorListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6201081729876731517L;

	public static String PackageName="";
	public static String MainActivityName="";
	public static String PackageSourceDir="";
	public static int Versioncode=-1;
	public InfiniteProgressPanel glassPane= new InfiniteProgressPanel();

	public static ImageIcon icon = null;
	private static final String icondescr = "ATK";
	private JMenuItem jmiRecord;
	private JMenuItem jmiStop;
	private JToolBar jtb;
	private JTabbedPane jtp;

	//private JComboBox jcbPhone;
	private JPhoneStatusButton jbtPhoneStatus;
	private JButton jbtRecord;
	private JButton jbtStop;
	/** Button to display Error Frame */
	private JButton errorButton = new JButton(ResourceManager.getInstance().getString("SEE"));
	/** Message displayed when an error occured */
	private static final String AN_ERROR_OCCURED = " "+ResourceManager.getInstance().getString("ERROR_FRAME_DEFAULT_TITLE")+" ";


	private ScriptJPanel jsPanel;
	private JLabel statusLabel;
	private JTextArea ConsoleArea;


	private JButton jbtPlay;

	private File scriptFile;
	private boolean isModified=false;
	private ScriptController controller;
	private JComboBox jcbPhonemode;
	private JButton jbtscreenshot;
	private JMenuItem jmiTakeSS;
	private static final String VERSION= "Script Recorder v1.0";
	private String inputdir;

	private JButton jbtGetViewsFromRobotium;

	public RecorderFrame(ScriptController sc) throws HeadlessException {
		super(VERSION);
		this.controller=sc;

		URL iconURL = CoreGUIPlugin.getMainIcon();
		icon = new ImageIcon(iconURL, icondescr);
		setIconImage(icon.getImage());

		/* MenuBar Creation */
		JMenuBar jmb = new JMenuBar();
		JMenu jmFile= new JMenu("File");
		/*JMenuItem jmiNew= new JMenuItem("New");
		jmiNew.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				newFile();
			}

		});*/
		JMenuItem jmiNew= new JMenuItem("Phone Test");
		jmiNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				newFile();
			}
		});

		JMenuItem jmiRobotiumTest= new JMenuItem("Robotium Test");
		jmiRobotiumTest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				newRobotiumTest();
			}
		});


		JMenu jmNewFile= new JMenu("New");
		jmNewFile.add(jmiNew);
		jmNewFile.add(jmiRobotiumTest);

		JMenuItem jmiOpen=new JMenuItem("Open...");
		jmiOpen.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				open();
			}

		});

		JMenuItem jmiSave=new JMenuItem("Save");
		jmiSave.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String sp =controller.getScriptPath();
				if(sp!=null)
					controller.save(sp);
				else
					save();

			}

		});

		JMenuItem jmiSaveas=new JMenuItem("Save As..");
		jmiSaveas.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				save();

			}

		});

		JMenuItem jmiExit=new JMenuItem("Exit");
		jmiExit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				exit();

			}

		});

		//jmFile.add(jmiNew);
		jmFile.add(jmNewFile);
		jmFile.add(jmiOpen);
		jmFile.add(jmiSave);
		jmFile.add(jmiSaveas);
		jmFile.add(jmiExit);


		JMenu jmScript=new JMenu("Script");
		jmiRecord=new JMenuItem("Record");
		jmiRecord.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				record();
			}
		});

		jmiStop=new JMenuItem("Stop");
		jmiStop.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				stop();
			}

		});
		jmScript.add(jmiRecord);
		jmScript.add(jmiStop);

		JMenu jmScreenshot=new JMenu("Screenshot");
		JMenuItem jmiScreenshotDir=new JMenuItem("Set Screenshot Directory");
		jmiScreenshotDir.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				controller.askScreenshotDir();
			}

		});

		jmiTakeSS=new JMenuItem("Take a ScreenShot");
		jmiTakeSS.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				takeScreenshot();
			}

		});
		jmScreenshot.add(jmiTakeSS);	
		jmScreenshot.add(jmiScreenshotDir);


		JMenu jmHelp=new JMenu("Help");
		JMenuItem jmiAbout=new JMenuItem("About...");
		jmiAbout.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				about();

			}

		});
		jmHelp.add(jmiAbout);

		JMenuItem jmiFirstStart=new JMenuItem("First Start...");
		jmiFirstStart.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				firstStart();
			}

		});
		jmHelp.add(jmiFirstStart);


		jmb.add(jmFile);
		jmb.add(jmScript);
		jmb.add(jmScreenshot);
		jmb.add(Box.createHorizontalGlue());
		jmb.add(jmHelp);
		this.setJMenuBar(jmb);

		//keyborad shortcut
		jmiExit.setAccelerator(KeyStroke.getKeyStroke("ctrl Q") ) ;
		jmiSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S") ) ;
		jmiOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O") ) ;
		jmiNew.setAccelerator(KeyStroke.getKeyStroke("ctrl N") ) ;



		/* ToolBar Creation*/
		jtb =new JToolBar();


		jbtPhoneStatus=new JPhoneStatusButton(this);

		jbtRecord=new JButton(new ImageIcon(this.getClass().getResource("record.png")));
		//jbtRecord=new JButton(new ImageIcon(CoreGUIPlugin.getIconURL("tango-l/record.png")));
		jbtRecord.setToolTipText("Record");
		jbtRecord.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				record();
			}

		});
		jbtStop= new JButton(new ImageIcon(this.getClass().getResource("stop.png")));
		jbtStop.setToolTipText("stop");
		jbtStop.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				stop();
			}

		});
		jbtPlay= new JButton(new ImageIcon(this.getClass().getResource("noatunplay.png")));
		jbtPlay.setToolTipText("Play Script on Device");
		jbtPlay.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				runScript();

			}
		});
		jbtscreenshot= new JButton(new ImageIcon(this.getClass().getResource("camera.png")));
		jbtscreenshot.setToolTipText("Take Screenshot from Device");
		jbtscreenshot.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				takeScreenshot();

			}
		});


		jbtGetViewsFromRobotium = new JButton(new ImageIcon(this.getClass().getResource("screenshot.png")));
		jbtGetViewsFromRobotium.setToolTipText("Displays current views");
		jbtGetViewsFromRobotium.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				startUiAtomatorViewer();
			}
		});




		String[] arg2 ={"-No Mode-"};
		jcbPhonemode=new JComboBox(arg2);
		jcbPhonemode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if (jcbPhonemode.getItemCount()==0||jcbPhonemode.getSelectedItem().equals("-No Mode-") ){
					jbtStop.setEnabled(false);
					jbtPlay.setEnabled(false);
					jbtRecord.setEnabled(false);
					jbtscreenshot.setEnabled(false);
					jmiTakeSS.setEnabled(false);
					jcbPhonemode.setEnabled(true);		
					jmiRecord.setEnabled(false);
					jmiStop.setEnabled(false);
				}else{
					jbtRecord.setEnabled(true);
					jmiRecord.setEnabled(true);
					jbtPlay.setEnabled(!jsPanel.isEmpty());
				}
			}

		});

		//jtb.add(jcbPhone);
		jtb.add(jbtPhoneStatus);
		jtb.add(jcbPhonemode);
		jtb.addSeparator();
		jtb.add(jbtRecord);
		jtb.add(jbtStop);
		jtb.addSeparator();
		jtb.add(jbtPlay);
		jtb.addSeparator();
		jtb.add(jbtscreenshot);

		jtb.add(jbtGetViewsFromRobotium);

		this.add(jtb, BorderLayout.NORTH);
		jbtStop.setEnabled(false);
		//	jcbPhone.setEnabled(true);
		jbtPlay.setEnabled(false);
		jbtRecord.setEnabled(false);
		jbtscreenshot.setEnabled(false);
		jmiTakeSS.setEnabled(false);
		jcbPhonemode.setEnabled(true);
		jmiRecord.setEnabled(false);
		jmiStop.setEnabled(false);
		jcbPhonemode.setEnabled(false);
		jcbPhonemode.setVisible(false);

		/*Central Tabbed Pane Creation*/
		jtp = new JTabbedPane();

		jsPanel = new ScriptJPanel(this);
		jtp.add(jsPanel);
		jtp.setIconAt(jtp.indexOfComponent(jsPanel),new ImageIcon(this.getClass().getResource("script2.png")));

		ConsoleArea = new JTextArea(" ");
		jtp.add(new JScrollPane(ConsoleArea) );
		jtp.setTitleAt(1, "Errors");
		jtp.setBackgroundAt(1,Color.GREEN);
		jtp.setEnabledAt(1, false);

		jtp.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JTabbedPane jtplistened = (JTabbedPane) e.getSource();
				//the user want see the console,
				//no more Usefull to color the tab pane in red
				if ( jtplistened.getSelectedIndex() ==1) 
					jtplistened.setBackgroundAt(1,Color.GREEN);

			}

		});

		this.add(jtp, BorderLayout.CENTER);


		//Create status bar
		JPanel statusbar = new JPanel();
		statusbar.setLayout(new BoxLayout(statusbar,BoxLayout.X_AXIS));
		statusbar.setBackground(new Color(220,220,240));

		statusLabel = new JLabel("status bar");
		statusbar.add(statusLabel);
		statusbar.add(errorButton);
		errorButton.setVisible(false);
		errorButton.setForeground(Color.RED);
		errorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ErrorManager.getInstance().displayErrorFrame();
				clearErrorMessage();
			}
		});

		//statusbar.add(Box.createHorizontalGlue());
		this.add(statusbar, BorderLayout.SOUTH);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent arg0) {}

			public void windowClosed(WindowEvent arg0) {}

			public void windowClosing(WindowEvent arg0) {
				exit();
			}

			public void windowDeactivated(WindowEvent arg0) {}

			public void windowDeiconified(WindowEvent arg0) {}

			public void windowIconified(WindowEvent arg0) {}

			public void windowOpened(WindowEvent arg0) {}

		});

		ErrorManager.getInstance().addErrorListener(this);
		this.setGlassPane(glassPane);
		pack();
	}

	protected void disableRecorder()
	{
		if (controller.isRecording() || controller.isRunning()) this.stop();
		jbtRecord.setEnabled(false);
		jbtscreenshot.setEnabled(false);
		jmiTakeSS.setEnabled(false);
		jcbPhonemode.setEnabled(false);		
		jmiRecord.setEnabled(false);
		jcbPhonemode.setEnabled(false);
		jbtPlay.setEnabled(false);
	}

	protected void enableRecorder()
	{
		controller.setPhone(AutomaticPhoneDetection.getInstance().getDevice());

		jcbPhonemode.removeAllItems();

		String[] items=controller.getPhone().getRecordPhoneMode(); 

		if(items!=null) {
			for(int i=0;i<items.length;i++)
				jcbPhonemode.addItem(items[i]);

			jbtRecord.setEnabled(true);
			jmiRecord.setEnabled(true);
			jcbPhonemode.setEnabled(true);
			if (items.length>1) jcbPhonemode.setVisible(true);
			else jcbPhonemode.setVisible(false);

			jbtPlay.setEnabled(!jsPanel.isEmpty());
			pack();
		} else {
			JOptionPane.showMessageDialog(this, "Record mode unsupported with this phone","Warning",JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void record() {
		ErrorManager.getInstance().clear();
		AutomaticPhoneDetection.getInstance().pauseDetection();
		//pause automatic detection
		if(controller.recordMode()){
			jbtStop.setEnabled(true);
			//	jcbPhone.setEnabled(false);
			jbtPlay.setEnabled(false);
			jbtRecord.setEnabled(false);
			jcbPhonemode.setEnabled(false);
			jbtscreenshot.setEnabled(true);
			jmiTakeSS.setEnabled(true);
			jmiRecord.setEnabled(false);
			jmiStop.setEnabled(true);	
		}

	}

	protected void stop() {
		controller.stop();
		jbtStop.setEnabled(false);
		//	jcbPhone.setEnabled(true);
		jbtPlay.setEnabled(! jsPanel.isEmpty());
		jbtRecord.setEnabled(true);
		jbtscreenshot.setEnabled(false);
		jmiTakeSS.setEnabled(false);
		jcbPhonemode.setEnabled(true);
		jmiRecord.setEnabled(true);
		jmiStop.setEnabled(false);

		//resume automatic detection
		AutomaticPhoneDetection.getInstance().resumeDetection();
	}

	protected void runScript() {

		String sp =controller.getScriptPath();
		if(sp==null)
			save();
		if(!UiAutomatorViewer.dumpXMLFirstTime){
			JOptionPane.showMessageDialog(this, "Error : You must Stop UiautomatorViewer.getViews before running script","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!(jcbPhonemode.getSelectedItem().equals("-No Mode-"))){
			//stop automatic detection
			AutomaticPhoneDetection.getInstance().pauseDetection();
			//Logger.getLogger(this.getClass() ).debug("/****RecorderFrame.runScript***/");
			jbtStop.setEnabled(true);
			//jcbPhone.setEnabled(false);
			jbtPlay.setEnabled(false);
			jbtRecord.setEnabled(false);
			jbtscreenshot.setEnabled(false);
			jmiTakeSS.setEnabled(false);
			jcbPhonemode.setEnabled(false);
			jmiRecord.setEnabled(false);
			jmiStop.setEnabled(true);
			ErrorManager.getInstance().clear();

			Thread t = new Thread(){
				@Override
				public void run() {
					//get selected lines by users
					int startline=0;

					//TODO : correct startlines and stopline	
					//HACK
					int stopline = startline+1;
					controller.runLines(startline, stopline-startline+1);
					if(controller.isRunning()){
						RecorderFrame.this.stop();
					}
				}
			};
			t.start();
		}else{
			JOptionPane.showMessageDialog(this, "Error : no Phone, or no Mode selected","Error",JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void takeScreenshot() {
		jbtStop.setEnabled(false);
		jmiStop.setEnabled(false);
		jbtscreenshot.setEnabled(false);
		jmiTakeSS.setEnabled(false);


		controller.takeScreenShot();
		jbtStop.setEnabled(true);
		jmiStop.setEnabled(true);
		jbtscreenshot.setEnabled(true);
		jmiTakeSS.setEnabled(true);

	}

	protected void activePlaybutton() {
		jbtPlay.setEnabled(true);
	}


	private void setModified(boolean b) {
		isModified=b;
		if (b&&!this.getTitle().startsWith("*")){
			this.setTitle("*"+this.getTitle());
		}else if (!b ){
			this.setTitle(this.getTitle().replace("*", ""));
		}

	}

	protected void about() {
		new AboutDialog(this,VERSION);

	}

	protected void firstStart() {
		new FirstStart(this,VERSION);
	}


	protected void newFile() {
		//		Logger.getLogger(this.getClass() ).debug("/****RecorderFrame.newFile***/");
		if (isModified){
			int r=JOptionPane.showConfirmDialog(this, "The script has been modified. Do you want to save this modifications?");
			if (r==JOptionPane.OK_OPTION){
				save();
			}else if(r==JOptionPane.CANCEL_OPTION){
				return;
			}
		}
		controller.newFile();
		jbtStop.setEnabled(false);
		//	jcbPhone.setEnabled(true);
		jbtPlay.setEnabled(false);
		jbtRecord.setEnabled(true);
		jbtscreenshot.setEnabled(false);
		jmiTakeSS.setEnabled(false);
		jcbPhonemode.setEnabled(true);
		jmiRecord.setEnabled(true);
		jmiStop.setEnabled(false);

		updateScript();

		setModified(false);
	}

	protected void open() {
		//		Logger.getLogger(this.getClass() ).debug("/****RecorderFrame.open****/");

		JFileChooser fc;
		String scriptPath = controller.getScriptPath();
		inputdir = getDefaultPath();
		fc = new JFileChooser(inputdir);
		fc.setFileFilter(new FileUtilities.Filter("Test files (.tst)",".tst"));
		int r= fc.showOpenDialog(this);
		if(r==JOptionPane.OK_OPTION){
			scriptFile=fc.getSelectedFile();
			scriptPath=scriptFile.getAbsolutePath();
			if (inputdir==null) saveDefaultPath(scriptFile.getParent());
			controller.openScript(scriptPath);
			setModified(false);
			jbtStop.setEnabled(false);
			//	jcbPhone.setEnabled(true);
			jbtscreenshot.setEnabled(false);
			jmiTakeSS.setEnabled(false);
			jbtPlay.setEnabled(!(jcbPhonemode.getSelectedItem().equals("-No Mode-")||jsPanel.isEmpty()));
			jbtRecord.setEnabled(!(jcbPhonemode.getSelectedItem().equals("-No Mode-")));
			jcbPhonemode.setEnabled(true);
			jmiRecord.setEnabled(!(jcbPhonemode.getSelectedItem().equals("-No Mode-")));
			jmiStop.setEnabled(false);
		}
	}

	protected void save() {
		//		Logger.getLogger(this.getClass() ).debug("/****RecorderFrame.save****/");
		String scriptPath = controller.getScriptPath();
		if(scriptPath!=null){
			inputdir = new File(scriptPath).getParent();
		}else {
			inputdir = getDefaultPath();
		}

		JFileChooser fc =new JFileChooser(inputdir);
		int r =fc.showSaveDialog(this);

		if (r==JFileChooser.APPROVE_OPTION){
			scriptPath=fc.getSelectedFile().getAbsolutePath();
			scriptPath = FileUtilities.verifyExtension(scriptPath, ".tst");
			saveDefaultPath(fc.getSelectedFile().getParent());
			scriptFile=new File(scriptPath);
			if (!scriptFile.exists()){
				try {
					scriptFile.createNewFile();
				} catch (IOException e) {
					Logger.getLogger(this.getClass() ).warn("Can't Create the File");
					e.printStackTrace();
				}
			}
			controller.save(scriptPath);
			setModified(false);
		}
	}

	protected void exit() {
		Logger.getLogger(this.getClass() ).debug("/****RecorderFrame.exit***/");
		if(controller.isRecording())
			stop();
		controller.close();
		AutomaticPhoneDetection.getInstance().stopDetection(jbtPhoneStatus);
		Frame frameList[] = Frame.getFrames();
		for(Frame frame : frameList){
			String frameName = frame.getClass().getName();
			if(frameName.contains("RecorderFrame") ||
					frameName.contains("ErrorFrame") ||
					frameName.contains("DeviceDetectionFrame")){
				frame.setVisible(false);
				frame.dispose();
			}
		}

	}
	protected void displayFrame() {
		Logger.getLogger(this.getClass() ).debug("/****RecorderFrame.display***/");
		AutomaticPhoneDetection.getInstance().addDeviceDetectionListener(jbtPhoneStatus);
		jbtPhoneStatus.initialize();
		setVisible(true);
	}



	private String getDefaultPath(){
		try {
			String path = Configuration.getProperty(Configuration.INPUTDIRECTORY);
			Logger.getLogger(this.getClass() ).debug("InputDir="+path);
			return path;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(this.getClass() ).debug(" no input dir set in configuration file [ConfigurtionDialog]");
			return null;
		}
	}

	private void saveDefaultPath(String path){
		Logger.getLogger(this.getClass() ).debug("New default path="+path);
		Configuration.setProperty(Configuration.INPUTDIRECTORY, path);
		Configuration.writeProperties();
	}

	public JComboBox getJcbPhonemode() {
		return jcbPhonemode;
	}


	public void addToConsole(String text) {
		jtp.setBackgroundAt(1, Color.red);
		jtp.setEnabledAt(1, true);

		Date date = new Date();

		ConsoleArea.append("\n/*************"+date.toString()+"*************/\n"+text);
	}

	public void updateScript() {

		//update the script
		jsPanel.update();

		//time estimator 
		int Timeinmili = ScriptController.getScriptController().estimateTimeExecution() ;
		int Timeins = Timeinmili /1000;
		//retain also 1/10"
		Timeinmili =  (Timeinmili/100) % 10; 
		statusLabel.setText("Supposed execution time : "+
				Timeins+ "\" "+Timeinmili);
	}


	public void setRunningNode(int nodeLineNumber) {
		if (controller.isRunning()) jsPanel.setRunningNode(nodeLineNumber);
	}

	public void errorOccured() {
		if (controller.isRecording() || controller.isRunning()) {
			this.stop();
		}
		displayErrorMessage();
	}

	public void warningOccured() {
		displayErrorMessage();
	}

	private void displayErrorMessage() {
		errorButton.setVisible(true);
		statusLabel.setText(AN_ERROR_OCCURED);
		statusLabel.setForeground(Color.RED);
	}

	private void clearErrorMessage() {
		errorButton.setVisible(false);
		statusLabel.setText("status bar");
		statusLabel.setForeground(Color.BLACK);
	}

	protected void  startUiAtomatorViewer(){
		new AndroidPlugin().getAdb();
		DebugBridge.setInitialised(new AndroidPlugin().getAdb());
		UiAutomatorViewer window = new UiAutomatorViewer();
		window.setRecorderFrame(this);
		window.setVisible(true);

	}

	protected ArrayList<String> getForegroundApp() {
		return controller.getForegroundApp();
	}

	public void selectAPK() {
		ArrayList <String> allApk = controller.getAllInstalledAPK();
		ArrayList <String> apk = controller.getForegroundApp();
		if(allApk!=null) { 
			glassPane.stop();
			new SelectAPKDialog(this,allApk).show();
			if(RecorderFrame.PackageName.equalsIgnoreCase("NONE")&&RecorderFrame.MainActivityName.equalsIgnoreCase("NONE")&&
					RecorderFrame.PackageSourceDir.equalsIgnoreCase("NONE")) {
				RecorderFrame.MainActivityName="";
				RecorderFrame.PackageName="";
				RecorderFrame.PackageSourceDir="";
				RecorderFrame.Versioncode=-1;
			} else if (RecorderFrame.PackageName.equalsIgnoreCase("CurrentApp")&&RecorderFrame.MainActivityName.equalsIgnoreCase("CurrentApp")
					&&RecorderFrame.PackageSourceDir.equalsIgnoreCase("CurrentApp")) {
				RecorderFrame.PackageName=apk.get(0);
				RecorderFrame.MainActivityName=apk.get(1);
				RecorderFrame.PackageSourceDir=apk.get(2);
				RecorderFrame.Versioncode=Integer.parseInt(apk.get(3));
			}
		} else {
			glassPane.stop();
			JOptionPane.showMessageDialog(this, "Empty List of installed apks","Warning",JOptionPane.WARNING_MESSAGE);
		}
	}

	protected void newRobotiumTest() {
		if (isModified) {
			int r=JOptionPane.showConfirmDialog(this, "The script has been modified. Do you want to save this modifications?");
			if (r==JOptionPane.OK_OPTION){
				save();
			}else if(r==JOptionPane.CANCEL_OPTION){
				return;
			}
		}
		Thread progress = new Thread(){
			@Override
			public void run() {
				glassPane.setText("Getting all installed APK");
				glassPane.start();
				selectAPK();
				if(!RecorderFrame.PackageName.equalsIgnoreCase("")&& !RecorderFrame.MainActivityName.equalsIgnoreCase("")&&
						!RecorderFrame.PackageSourceDir.equalsIgnoreCase("")) {
					controller.setTestAPKWithRobotiumParam(RecorderFrame.PackageName,RecorderFrame.MainActivityName,RecorderFrame.PackageSourceDir,RecorderFrame.Versioncode);
					controller.newFileForRobotium();
					jbtStop.setEnabled(false);
					jbtPlay.setEnabled(true);
					jbtRecord.setEnabled(true);
					jbtscreenshot.setEnabled(false);
					jmiTakeSS.setEnabled(false);
					jcbPhonemode.setEnabled(true);
					jmiRecord.setEnabled(true);
					jmiStop.setEnabled(false);
					updateScript();
					setModified(false);
				}
			}
		};
		progress.start();
	}

	public void updateAST () {
		jsPanel.update();
	}
	public int getSelectedNode(){
		return jsPanel.getSelectedNode();
	}

}
