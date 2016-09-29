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
 * File Name   : MonitorAction.java
 *
 * Created     : 19/08/2010
 * Author(s)   : Gurvan LE QUELLENEC
 */
package com.orange.atk.atkUI.coregui.actions;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;

import com.orange.atk.atkUI.coregui.*;
import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.graphAnalyser.CreateGraph;
import com.orange.atk.graphAnalyser.GraphMarker;
import com.orange.atk.graphAnalyser.LectureJATKResult;
import com.orange.atk.graphAnalyser.PerformanceGraph;
import com.orange.atk.graphAnalyser.RealtimeGraph;
import com.orange.atk.launcher.LaunchJATK;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.TcpdumpLineListener;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.DocumentGenerator;
import com.orange.atk.results.logger.documentGenerator.PDFGenerator;
import com.orange.atk.results.logger.log.DocumentLogger;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.util.NetworkAnalysisUtils;

public class MonitorAction extends MatosAbstractAction {
	private boolean running = false;
	private String xmlfilepath = null;
	private static PhoneInterface currentPhone = null;
	private static DocumentGenerator documentGenerator = null;
	// Directory where results would be saved
	private String logDir = null;
	private ResultLogger logger;
	private RealtimeGraph realtime;
	private DocumentLogger documentLogger;
	private CreateGraph JaTKCharts;
	private Map<String, PerformanceGraph> mapPerfGraph;
	private Map<String, GraphMarker> mapAction = null;
	private JFrame frame;
	private JButton buttonStart, buttonStop, buttonCancel, buttonReport, buttonGraph;
    public InfiniteProgressPanel glassPane= new InfiniteProgressPanel();

	public MonitorAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
		running = false;
		logDir = Platform.TMP_DIR + Platform.FILE_SEPARATOR;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {

		LaunchJATK exec = new LaunchJATK();
		if (exec.getCurrentPhone() instanceof DefaultPhone)
		{
			JOptionPane.showMessageDialog(null, "Can't detect device.");
			return;
		}

		currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
		if (currentPhone.getType() == PhoneInterface.TYPE_S60)
		{
			JOptionPane.showMessageDialog(null, "Not supported for S60.");
			return;
		}

		// Create the combobox and initialize the values
		Vector<String> readListPhoneConfig = CheckListTable.readListPhoneConfig();
		final JComboBox comboBoxPhoneConfig = (readListPhoneConfig == null)
				? new JComboBox()
				: new JComboBox(readListPhoneConfig);
		comboBoxPhoneConfig.addItem(CheckListTable.ADD_NEW_CONFIG_FILE);
		comboBoxPhoneConfig.addItem(CheckListTable.NOT_SELECTED);

		String defaultConfigFileName = Configuration.getInstance().getDefaultMonitoringConfig()
				.get(currentPhone.getClass().getName());
		comboBoxPhoneConfig.setSelectedItem(CheckListTable.NOT_SELECTED);
		for (String name : readListPhoneConfig) {
			if (name.equals(defaultConfigFileName)) {
				comboBoxPhoneConfig.setSelectedItem(defaultConfigFileName);
				xmlfilepath = Configuration.getMonitoringConfigDir() + File.separator + defaultConfigFileName;
				break;
			}
		}

		JATKcomboBoxListener comboBoxListener = new JATKcomboBoxListener(comboBoxPhoneConfig, this);
		comboBoxPhoneConfig.addActionListener(comboBoxListener);
		comboBoxPhoneConfig.addMouseListener(comboBoxListener);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("You have to select a configuration file"));
		panel.add(new JLabel("which contains the ressources to monitor."));
		panel.add(comboBoxPhoneConfig);
		panel.add(new JLabel("<html><i>You can doubleclick to see and edit the file.</i></html>"));

		JPanel panelBox = new JPanel();
		// panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.X_AXIS));
		panelBox.setLayout(new GridLayout(1, 3, 5, 0));
		// panelBox.add(new JLabel(""));//Just to center the buttons
		buttonStart = new JButton("Start");
		buttonStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (xmlfilepath == null) {
					JOptionPane.showMessageDialog(null, "You must select a configuration file.");
					return;
				}
				if (running) {
					JOptionPane.showMessageDialog(null, "Already running...");
					return;
				}

				int ret = SelectDialog.showDialog(CoreGUIPlugin.mainFrame, true);
				if (ret != SelectDialog.OK_OPTION) {
					return;
				}
				startMonitoring();

			}
		});
		panelBox.add(buttonStart);

		buttonStop = new JButton("Stop");
		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stopMonitoring();
			}
		});
		buttonStop.setEnabled(false);
		panelBox.add(buttonStop);

		buttonCancel = new JButton("Cancel");
		panelBox.add(buttonCancel);
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				stopMonitoring();
				frame.dispose();
			}
		});

		buttonReport = new JButton("View Report");
		buttonReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ((logDir != null) && (!logDir.trim().equals(""))) {
					new FileViewDialog(CoreGUIPlugin.mainFrame, logDir + File.separatorChar
							+ "report.html", FileViewDialog.REPORT,
							"", "", "");
				} else {
					JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, "No report available",
							"View report", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		buttonReport.setEnabled(false);

		buttonGraph = new JButton("Graph Analyzer");
		buttonGraph.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				LectureJATKResult analyser = new LectureJATKResult();
				analyser.setParameters(logDir);
				analyser.setVisible(true);
                String aroDataPath=logDir+File.separator+"ARO";
                if(new File(aroDataPath).exists()){
                    AROLauncher.start(aroDataPath);
                }

			}
		});
		buttonGraph.setEnabled(false);

		panel.add(panelBox);

		panel.add(buttonReport);
		panel.add(buttonGraph);

		frame = new JFrame();
		frame.setTitle("Select a configuration file...");

        URL iconURL = CoreGUIPlugin.getMainIcon();
        ImageIcon icon = new ImageIcon(iconURL, "");
        frame.setIconImage(icon.getImage());

        frame.setSize(300, 200);
		frame.setLocationRelativeTo(CoreGUIPlugin.mainFrame);
		frame.setVisible(true);
		frame.add(panel);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		WindowListener windowListener = new WindowAdapter()
		{
			// anonymous WindowAdapter class
			public void windowClosing(WindowEvent w)
			{
				frame.setVisible(false);
				stopMonitoring();
				frame.dispose();
			}
		};
		frame.addWindowListener(windowListener);
        frame.setGlassPane(glassPane);
	}

	private TcpdumpLineListener tcpdumpLineListener = new TcpdumpLineListener() {

		public void newTcpDumpLine(String line) {
			Date theDate = NetworkAnalysisUtils.extractTcpdumpLineDate(line);
			String url = NetworkAnalysisUtils.extractTcpdumpLineUrl(line);
			if (logger != null && theDate != null && url != null) {
				logger.addInfotoActionLogger("URL", url, theDate, theDate);
			}
		}
	};

	private void startMonitoring() {
		running = true;
		buttonStart.setEnabled(false);
		buttonReport.setEnabled(false);
		buttonGraph.setEnabled(false);
		buttonReport.setToolTipText("");
		SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		logDir = MatosGUI.outputDir + Platform.FILE_SEPARATOR + "Manual_" + spf.format(new Date());
		File outputdirF = new File(logDir);
		if (!outputdirF.exists())
			if (!outputdirF.mkdir())
				Logger.getLogger(this.getClass()).debug("Can't Create dir " + outputdirF.getPath());

		JaTKCharts = new CreateGraph();
		boolean empty = JaTKCharts.createPerfGraphsAndMarkers(xmlfilepath);
		JaTKCharts.createEmptyDataset();
		JaTKCharts.initializeTimeAxis();
		mapPerfGraph = JaTKCharts.getMapPerfGraph();
		mapAction = JaTKCharts.getMapAction();

		// no real time graph on NokiaS60
		if (!empty)
			displayRealTimeGraph();

		try {
			if (currentPhone.isDeviceRooted() == false
					&& Boolean.valueOf(Configuration.getProperty(Configuration.NETWORKMONITOR,
							"false"))) {
				JOptionPane.showMessageDialog(null,
						"This device is not rooted, Network Data Analysis will not be available",
						"Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						realtime.addUrlMarkerCheckBox();
					}
				});
			}
			currentPhone.addTcpdumpLineListener(tcpdumpLineListener);
			currentPhone.startTestingMode(logDir, xmlfilepath);
		} catch (PhoneException e) {
			Logger.getLogger(this.getClass()).error("" + e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		createPDFFile();
		logger = new ResultLogger(logDir, documentGenerator, xmlfilepath);
		documentLogger = logger.getDocumentLogger();
		documentLogger.setMapPerfGraph(mapPerfGraph);
		logger.getDocumentLogger().addPlotlistObject();
		logger.setPhoneInterface(currentPhone);
		logger.start(1000);
		buttonStop.setEnabled(true);
	}
	private void stopMonitoring() {
		if (running) {
			buttonStop.setEnabled(false);
			currentPhone.stopTestingMode();
			stopRealTimeGraph();
			logger.interrupt();
			logger.join();
			writeLogAndExitPhoneHandling();
			running = false;
			buttonStart.setEnabled(true);
			buttonReport.setEnabled(true);
			buttonReport.setToolTipText(logDir);
			buttonGraph.setEnabled(true);
			buttonGraph.setToolTipText(logDir);
            postExecution();
		}

	}

    private void postExecution(){
        glassPane.setText("downloading ARO files");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Logger.getLogger(this.getClass()).error(e);
                }
                String testName = new File(logDir).getName();
                currentPhone.pullData("mnt/sdcard/ARO/"+testName,logDir+File.separator+"ARO");
                glassPane.stop();
            }
        }).start();
        glassPane.setVisible(true);
        glassPane.start();
    }

	private void createPDFFile()
	{
		try {
			// Create Document generator object
			documentGenerator = new PDFGenerator(new FileOutputStream(
					new File(logDir + Platform.FILE_SEPARATOR + "report.pdf")),
					logDir, "", "Orange FR.", "ManualMonitoring",
					false);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}

	}

	private void displayRealTimeGraph() {
		boolean isrealtime = Boolean.valueOf(Configuration.getProperty(Configuration.REALTIMEGRAPH,
				"true"));

		if (!(AutomaticPhoneDetection.getInstance().isNokia()) && isrealtime)
		{
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					currentPhone.addTcpdumpLineListener(JaTKCharts);
					realtime = new RealtimeGraph(JaTKCharts);
					realtime.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					WindowListener windowListener = new WindowAdapter()
					{
						// anonymous WindowAdapter class
						public void windowClosing(WindowEvent w)
						{
							stopMonitoring();
						}
					};
					realtime.addWindowListener(windowListener);
					realtime.setVisible(true);
				}
			});
		}
		return;

	}

	private void stopRealTimeGraph() {
		boolean isrealtime = Boolean.valueOf(Configuration.getProperty(Configuration.REALTIMEGRAPH,
				"true"));
		if (realtime != null && !AutomaticPhoneDetection.getInstance().isNokia() && isrealtime)
		{
			realtime.close();
		}
		realtime = null;
	}

	private void writeLogAndExitPhoneHandling() {
		if (logger != null) {
			if (logger.isAlive()) {
				logger.interrupt();
				logger.join();
			}
		}
		if (logger != null) {
			// write file action.log
			logger.writeActionLogFile(xmlfilepath);
			logger.generateGraphFile();
			logger.generatepltFile();

			// write Error File pdf or Txt
			if (documentGenerator != null)
				documentGenerator.dumpInStream(false, documentLogger);

			logger = null;

		}
	}

	public void setXmlfilepath(String xmlfilepath) {
		this.xmlfilepath = xmlfilepath;
	}
}
