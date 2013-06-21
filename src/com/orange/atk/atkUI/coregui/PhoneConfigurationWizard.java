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
 * File Name   : PhoneConfigurationWizard.java
 *
 * Created     : 08/01/2010
 * Author(s)   : France Telecom
 */
package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import com.orange.atk.graphAnalyser.CreateGraph;
import com.orange.atk.monitoring.AroSettings;
import com.orange.atk.monitoring.Event;
import com.orange.atk.monitoring.EventlistModel;
import com.orange.atk.monitoring.Graph;
import com.orange.atk.monitoring.GraphlistModel;
import com.orange.atk.monitoring.MonitoringConfig;
import com.orange.atk.monitoring.ProcessTableModel;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.android.AndroidPhone;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 * @author Fabien Moreau - FMOREAU@gfi.fr
 * 
 */
public class PhoneConfigurationWizard extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6312160245575708505L;
	private PhoneInterface phone;

	private GraphlistModel graphs;
	private EventlistModel events;

	// process graph
	private JTable processtable;
	private ProcessTableModel tablemodel;
	private JPopupMenu tablePopup;
	private JCheckBox aroCheckbox;

	// String
	private String configpath;
	// private static final String[] DEFAULT_MARKERS= {"Action","Error JATK",
	// "Exception","Key Down",
	// "KeyPress","Log","Standard Out/Err","Screenshot"};
	private static final String[] USUAL_GRAPH_LIST = {"Cpu", "Memory", "Data sent", "Data received"};
	private boolean isDefaultConfig = false;

	public PhoneConfigurationWizard(String configFile, boolean isDefaultConfig) {
		super();
		this.isDefaultConfig = isDefaultConfig;
		phone = AutomaticPhoneDetection.getInstance().getDevice();
		this.configpath = configFile;

		setLayout(new GridBagLayout());

		// default constraints
		// top left with no insets or a 0.1 weight (few move on resizing
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0.1, 0.1,
				GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH,
				new Insets(1, 1, 1, 1), 0, 0);

		final JPanel AROpane = new JPanel();
		AROpane.setLayout(new BoxLayout(AROpane, BoxLayout.PAGE_AXIS));
		AROpane.setBorder(BorderFactory.createTitledBorder("Application Resource Optimizer"));
		aroCheckbox = new JCheckBox("enable ARO data collector");
		AROpane.add(aroCheckbox);
		c.gridwidth = 2;
		add(AROpane, c);
		c.gridy = 1;
		c.gridx = 0;
		add(buildGlobalGraphPanel(), c);

		c.gridx = 1;
		add(buildGlobalEventPanel(), c);

		// specific to Android
		if ((phone.getName() != null && phone.getName().contains("Android"))
				&& !this.isDefaultConfig) {
			c.gridy = 2;
			c.gridx = 0;
			c.gridwidth = 2;
			c.weightx = 1;
			c.weighty = 1;

			add(buildMemoryProcessTable(), c);
		}

		MonitoringConfig config;
		try {
			config = MonitoringConfig.fromFile(configpath);
			init(config);
		} catch (IOException e1) {
			Logger.getLogger(this.getClass()).error(e1);
		}

		pack();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveconfig();
			}

			public void windowClosed(WindowEvent arg0) {
				dispose();
			}

		});
		setTitle("Phone Monitoring Configuration Wizard of " + configFile);
		ImageIcon ii = new ImageIcon(CoreGUIPlugin.getMainIcon(), "Phone analyser configuration");
		setIconImage(ii.getImage());
		setVisible(true);
	}
	/**
	 * in relation with the constructor
	 * 
	 * @return the table which manage memory process graph
	 */
	private Component buildMemoryProcessTable() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, // gridx, gridy
				1, 1, // gridwidth, gridheight
				0.5, 0.5, // weightx, weighty
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.NONE, // FILL
				new Insets(1, 1, 1, 1), // padding top, left, bottom, right
				0, 0); // ipadx, ipady
		JScrollPane scrollPanel = new JScrollPane();
		processtable = new JTable();
		tablemodel = new ProcessTableModel();
		processtable.setModel(tablemodel);
		processtable.setAutoCreateColumnsFromModel(true);
		processtable.setShowGrid(true);
		processtable.setFillsViewportHeight(true);
		// Set up Edtitor for each column

		// process column
		processtable.getColumnModel().getColumn(0)
				.setCellEditor(new DefaultCellEditor(buildProcessList()));
		processtable.setFillsViewportHeight(true);

		// Cpu, Mem or Storage
		processtable.getColumnModel().getColumn(1)
				.setCellEditor(new DefaultCellEditor(new JComboBox(USUAL_GRAPH_LIST)));

		// Color
		processtable.getColumnModel().getColumn(2)
				.setCellEditor(new DefaultCellEditor(new JComboBox(CreateGraph.COLORS)));

		// popup menu
		processtable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON3) {
					// RightClick
					if (tablePopup == null) {
						newtablePopup();
					}
					tablePopup.setLocation(arg0.getXOnScreen(), arg0.getYOnScreen());
					tablePopup.setVisible(true);
				}

				if (arg0.getButton() == MouseEvent.BUTTON1 && tablePopup != null) {
					tablePopup.setVisible(false);
				}
			}

		});

		// To center the window
		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX() + 150,
				CoreGUIPlugin.mainFrame.getLocationY() + 100);
		scrollPanel.getViewport().add(processtable);
		scrollPanel.setBorder(BorderFactory.createTitledBorder("Process table"));
		JButton jbmonitor = new JButton("Add Monitor Process");

		jbmonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tablemodel.addRow();

			}
		});

		JButton jbProcessInfo = new JButton("Process informations");

		jbProcessInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				displayProcessInfo();

			}
		});
		panel.add(jbmonitor, gbc);
		gbc.gridx = 1;
		panel.add(jbProcessInfo, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		panel.add(scrollPanel, gbc);
		return panel;
	}

	private void displayProcessInfo() {
		JFrame processInfoFrame = new JFrame("Process informations");
		JTextPane text = new JTextPane();
		text.setEditable(false);
		Style defaultStyle = text.getStyle("default");
		Style boldStyle = text.addStyle("Bold", null);
		StyleConstants.setBold(boldStyle, true);
		StyledDocument doc = text.getStyledDocument();

		Hashtable<String, String> processInfo = ((AndroidPhone) phone).getProcessInfo();
		Enumeration<String> processNames = processInfo.keys();

		try {
			while (processNames.hasMoreElements()) {
				String process = processNames.nextElement();
				String packages = processInfo.get(process);
				String[] values = packages.split(",");
				if (values.length > 1 || !process.equals(values[0])) {
					doc.insertString(doc.getLength(), "\n " + process, boldStyle);

					doc.insertString(doc.getLength(), " process is shared by packages:",
							defaultStyle);
					for (int i = 0; i < values.length; i++) {
						doc.insertString(doc.getLength(), "\n     * " + values[i], defaultStyle);
					}
					doc.insertString(doc.getLength(), "\n", defaultStyle);
				}
			}
			doc.insertString(doc.getLength(),
					"\n Other packages run in their own process (with the same name).",
					defaultStyle);
		} catch (BadLocationException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		processInfoFrame.setLayout(new BorderLayout());
		JScrollPane scrollPanel = new JScrollPane();
		processInfoFrame.setPreferredSize(new Dimension(400, 500));
		processInfoFrame.add(scrollPanel);
		scrollPanel.getViewport().add(text);
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fSize = this.getSize();
		processInfoFrame.setLocation((sSize.width - fSize.width) / 2,
				(sSize.height - fSize.height) / 2);

		processInfoFrame.pack();
		processInfoFrame.setVisible(true);
	}

	private void newtablePopup() {

		tablePopup = new JPopupMenu();
		JMenuItem jmiDelete = new JMenuItem("Delete Selected Lines");
		jmiDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int row : processtable.getSelectedRows()) {
					tablemodel.removeRow(row);
				}
				tablePopup.setVisible(false);
			}
		});
		tablePopup.add(jmiDelete);

		JMenuItem jmiadd = new JMenuItem("add a row");
		jmiadd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tablemodel.addRow();
				tablePopup.setVisible(false);
			}
		});
		tablePopup.add(jmiadd);
	}

	private JComboBox buildProcessList() {
		if (phone == null) {
			JComboBox mybox = new JComboBox(USUAL_GRAPH_LIST);
			mybox.setEditable(true);
			return mybox;
		} else {
			return new JComboBox(phone.getMonitorList());

		}
	}

	/**
	 * 
	 * @return a JPpanel to select which event we want following
	 */
	private Component buildGlobalEventPanel() {

		JPanel Eventpanel = new JPanel();
		Eventpanel.setBorder(BorderFactory.createTitledBorder("Events"));

		events = new EventlistModel();
		final JList eventlist = new JList(events);
		eventlist.setPreferredSize(new Dimension(100, 100));

		eventlist.setBorder(BorderFactory.createTitledBorder("marked event"));
		Eventpanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 3, 1., 1.,
				GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH,
				new Insets(1, 1, 1, 1), 0, 0);
		Eventpanel.add(eventlist, c);

		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		JButton removemarker = new JButton("Remove");
		removemarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				events.remove(eventlist.getSelectedIndex());
			}
		});
		if (!this.isDefaultConfig) {
			Eventpanel.add(removemarker, c);
		}

		return Eventpanel;
	}

	/**
	 * 
	 * @return a JPANEL which contains all parameters for show or not devices
	 *         graphs
	 */
	private Component buildGlobalGraphPanel() {
		final JPanel globalpanel = new JPanel();
		globalpanel.setBorder(BorderFactory.createTitledBorder("Device graphs"));

		globalpanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 8, 1., 1.,
				GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH,
				new Insets(1, 1, 1, 1), 0, 0);

		graphs = new GraphlistModel();
		final JList graphslist = new JList(graphs);
		graphslist.setPreferredSize(new Dimension(100, 100));

		graphslist.setBorder(BorderFactory.createTitledBorder("Graphics"));

		globalpanel.add(graphslist, c);

		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0.01;
		c.weighty = 0.1;
		c.gridx = 1;
		final JTextField graphname = new JTextField("name");
		graphname.setEditable(false);
		globalpanel.add(graphname, c);

		c.gridy = 1;
		final JComboBox graphcolor = new JComboBox(CreateGraph.COLORS);
		globalpanel.add(graphcolor, c);

		c.gridy = 2;
		final JTextField graphcommentX = new JTextField("Comment X axis");
		graphcommentX.setEditable(false);
		globalpanel.add(graphcommentX, c);

		c.gridy = 3;
		final JTextField graphcommentY = new JTextField("Comment Y axis");
		graphcommentY.setEditable(false);
		globalpanel.add(graphcommentY, c);

		c.gridy = 4;
		final JTextField graphunit = new JTextField("Unit");
		graphunit.setEditable(false);
		globalpanel.add(graphunit, c);

		c.gridy = 5;
		final JCheckBox graphsampled = new JCheckBox("Sampled");
		// globalpanel.add(graphsampled,c);

		c.gridy = 6;
		final JTextField graphscale = new JTextField("Scale");
		graphscale.setEditable(false);
		globalpanel.add(graphscale, c);

		c.gridy = 7;
		final JTextField graphtype = new JTextField("Type");
		graphtype.setEditable(false);
		globalpanel.add(graphtype, c);

		c.gridy = 8;
		c.gridwidth = 1;
		JButton addmarker = new JButton("Add");
		addmarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphs.addgraph(graphname.getText(), (String) graphcolor.getSelectedItem(),
						graphcommentX.getText(), graphcommentY.getText(), graphunit.getText(),
						graphscale.getText(), graphsampled.isSelected(), graphtype.getText());

				saveconfig();
			}
		});
		// globalpanel.add(addmarker,c);

		graphcolor.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int i = graphslist.getSelectedIndex();
				if (-1 == i) {
					return;
				}
				graphs.changecolor((String) graphcolor.getSelectedItem(), i);
				saveconfig();
			}
		});

		c.gridx = 2;
		JButton removemarker = new JButton("Remove");
		removemarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphs.remove(graphslist.getSelectedIndex());
				graphslist.update(graphslist.getGraphics());
			}
		});
		if (!this.isDefaultConfig) {
			globalpanel.add(removemarker, c);
		}

		c.gridx = 3;
		JButton savemarker = new JButton("Save");
		savemarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = graphslist.getSelectedIndex();
				if (-1 == i) {
					return;
				}
				graphs.savegraph(graphname.getText(), (String) graphcolor.getSelectedItem(),
						graphcommentX.getText(), graphcommentY.getText(), graphunit.getText(),
						graphscale.getText(), graphsampled.isSelected(), graphtype.getText(), i);
				saveconfig();
			}
		});
		// globalpanel.add(savemarker,c);

		graphslist.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				int i = graphslist.getSelectedIndex();
				graphname.setText(graphs.getName(i));
				graphcolor.setSelectedItem(graphs.getColor(i));
				graphcommentX.setText(graphs.getcommentX(i));
				graphcommentY.setText(graphs.getcommentY(i));
				graphunit.setText(graphs.getunit(i));
				graphscale.setText(graphs.getscale(i));
				graphsampled.setText(graphs.getsampled(i));
				graphtype.setText(graphs.gettype(i));
			}

			public void mouseEntered(MouseEvent arg0) {
				// Do nothing
			}

			public void mouseExited(MouseEvent arg0) {
				// Do nothing
			}

			public void mousePressed(MouseEvent arg0) {
				// Do nothing
			}

			public void mouseReleased(MouseEvent arg0) {
				// Do nothing
			}
		});

		return globalpanel;
	}

	private void init(MonitoringConfig config) {
		for (Graph g : config.getGraphs()) {
			if (g.getName().contains("_")) {
				String[] tableLine = g.getName().split("_");
				tablemodel.add(tableLine[1], tableLine[0], g.getColor());
			} else {
				this.graphs.addgraph(g.getName(), g.getColor(), g.getXcomment(), g.getYcomment(),
						g.getUnit(), g.getScale(), g.getSampled(), g.getType());
			}
		}
		for (Event e : config.getEvents()) {
			this.events.addEvent(e.getName(), e.getPosition());
		}
		aroCheckbox.setSelected((config.getAroSettings() != null
				&& config.getAroSettings().isEnabled()));
	}

	private void saveconfig() {
		MonitoringConfig config = new MonitoringConfig();
		List<Graph> configGraphs = new ArrayList<Graph>();
		List<Event> configEvents = new ArrayList<Event>();

		for (int i = 0; i < graphs.getSize(); i++) {
			configGraphs.add(new Graph(graphs.getName(i),
					graphs.getColor(i),
					graphs.getcommentX(i),
					graphs.getcommentY(i),
					graphs.getunit(i),
					graphs.getscale(i),
					Boolean.valueOf(graphs.getsampled(i)),
					graphs.gettype(i)));
		}

		// table Graph
		if (tablemodel != null) {
			for (int i = 0; i < tablemodel.getRowCount(); i++) {

				// don't save non-fill row
				String processname = (String) tablemodel.getValueAt(i, 0);
				String type = (String) tablemodel.getValueAt(i, 1);
				String color = (String) tablemodel.getValueAt(i, 2);

				if ("process".equals(processname) || "color".equals(color)
						|| "Cpu or Mem".equals(type)) {
					JOptionPane
							.showMessageDialog(null,
									"You must select the process, the color AND the type! \nThe line will be ignored.");
					continue;
				}

				String unit = "";
				String scale = "1";
				if (type.equals("Cpu")) {
					unit = "%";
				}
				else if (type.equals("Memory")) {
					unit = "KBytes";
				} else if (type.equals("Data sent")) {
					unit = "KBytes";
					scale = "1000";
				} else if (type.equals("Data received")) {
					unit = "KBytes";
					scale = "1000";
				}
				configGraphs.add(new Graph(type + "_" + processname, color, "time (min)", type
						+ " " + processname, unit, scale, true, null));
			}
		}

		for (int i = 0; i < events.getSize(); i++) {
			configEvents.add(new Event(events.getName(i), events.getPosition(i), "gray"));
		}
		config.setAroSettings(new AroSettings(aroCheckbox.isSelected()));
		config.setGraphs(configGraphs);
		config.setEvents(configEvents);
		config.toFile(configpath);
	}
}
