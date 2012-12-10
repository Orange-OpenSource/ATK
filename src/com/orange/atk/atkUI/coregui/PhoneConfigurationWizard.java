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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.orange.atk.graphAnalyser.CreateGraph;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.android.AndroidDriver;
import com.orange.atk.phone.android.AndroidMonkeyDriver;
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

	//process graph
	private JTable processtable;
	private processTableModel tablemodel;
	private JPopupMenu tablePopup;

	//String 
	private String configpath;
	//private static final String[] DEFAULT_MARKERS= {"Action","Error JATK", "Exception","Key Down", "KeyPress","Log","Standard Out/Err","Screenshot"};
	private static final String[] USUAL_GRAPH_LIST = {"Cpu","Memory","Data sent","Data received"};
	private boolean isDefaultConfig = false;

	public PhoneConfigurationWizard(String configFile, boolean isDefaultConfig) {
		this.isDefaultConfig = isDefaultConfig;
		phone = AutomaticPhoneDetection.getInstance().getDevice();
		this.configpath = configFile;
		
		setLayout(new GridBagLayout() );
		//default constraints
		//top left with no insets or a 0.1 weight (few move on resizing
		GridBagConstraints c = new GridBagConstraints(0,0,
				1,1,
				0.1,0.1,
				GridBagConstraints.BASELINE_LEADING,
				GridBagConstraints.BOTH,
				new Insets(1,1,1,1),
				0,0);
		add(buildGlobalGraphPanel(),c);

		c.gridx = 1;
		add (buildGlobalEventPanel(),c);


		//specific to Android
		if((phone.getName()!= null  && phone.getName().contains("Android")) && !this.isDefaultConfig) {
			c.gridy =1; c.gridx =0; c.gridwidth = 2;
			c.weightx =1; c.weighty =1;

			add(buildMemoryProcessTable(),c);
		}

		//parse actual xml file and fill GUI 
		try {
			SAXParser parseur = SAXParserFactory.newInstance().newSAXParser();
			File file = new File(configpath);
			ConfigSAXHandler gestionnaire = new ConfigSAXHandler(this);

			parseur.parse(file, gestionnaire);

		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}	

		//windows config
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
		setTitle("Phone Monitoring Configuration Wizard of "+configFile);
		ImageIcon ii = new ImageIcon(CoreGUIPlugin.getMainIcon(),"Phone analyser configuration");
		setIconImage(ii.getImage());
		setVisible(true);
	}

	/**
	 * in relation with the constructor
	 * @return the table which manage memory process graph
	 */
	private Component buildMemoryProcessTable() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(
				0,0, //gridx, gridy
				1,1, //gridwidth, gridheight
				0.5,0.5, //weightx, weighty
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.NONE, // FILL
				new Insets(1,1,1,1), // padding top, left, bottom, right
				0,0); //ipadx, ipady
		JScrollPane scrollPanel = new JScrollPane();
		processtable = new JTable();
		tablemodel = new processTableModel();
		processtable.setModel(tablemodel );
		processtable.setAutoCreateColumnsFromModel(true);
		processtable.setShowGrid(true);
		processtable.setFillsViewportHeight(true);
		//Set up Edtitor for each column

		//process column
		processtable.getColumnModel().getColumn(0).
		setCellEditor(new DefaultCellEditor(buildProcessList()));
		processtable.setFillsViewportHeight(true);

		//Cpu, Mem or Storage
		processtable.getColumnModel().getColumn(1).
		setCellEditor(new DefaultCellEditor(new JComboBox(USUAL_GRAPH_LIST)));

		//Color
		processtable.getColumnModel().getColumn(2).
		setCellEditor(new DefaultCellEditor(new JComboBox(CreateGraph.COLORS)));


		//popup menu
		processtable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton()==MouseEvent.BUTTON3){ 
					//RightClick
					if (tablePopup ==null)
						newtablePopup();
					tablePopup.setLocation(arg0.getXOnScreen(), arg0.getYOnScreen() );
					tablePopup.setVisible(true);
				}

				if (arg0.getButton()==MouseEvent.BUTTON1&& tablePopup!=null)
					tablePopup.setVisible(false);	
			}

		});

		// To center the window
		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+150,
				CoreGUIPlugin.mainFrame.getLocationY()+100);
		scrollPanel.getViewport().add(processtable);
		scrollPanel.setBorder(BorderFactory.createTitledBorder("Process table"));
		JButton jbmonitor =new JButton("Add Monitor Process");

		jbmonitor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				tablemodel.addRow();

			}
		});
		
		JButton jbProcessInfo =new JButton("Process informations");

		jbProcessInfo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				displayProcessInfo();

			}
		});
		panel.add(jbmonitor,gbc);
		gbc.gridx=1;
		panel.add(jbProcessInfo,gbc);
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=2;
		panel.add(scrollPanel,gbc);
		return panel;
	}

	private void displayProcessInfo() {
		JFrame processInfoFrame= new JFrame("Process informations");
		JTextPane text= new JTextPane();
		text.setEditable(false);
		Style defaultStyle = text.getStyle("default");
		Style boldStyle = text.addStyle("Bold", null);
		StyleConstants.setBold(boldStyle, true);
		StyledDocument doc = text.getStyledDocument();

		Hashtable<String,String> processInfo = ((AndroidPhone) phone).getProcessInfo();
		Enumeration<String> processNames = processInfo.keys();

		try {
			while (processNames.hasMoreElements()) {
				String process = processNames.nextElement();
				String packages = processInfo.get(process);
				String[] values = packages.split(",");
				if (values.length>1 || !process.equals(values[0])) {
					doc.insertString(doc.getLength(), "\n "+process, boldStyle);
					
					doc.insertString(doc.getLength(), " process is shared by packages:", defaultStyle);
					for (int i=0; i<values.length; i++) {
						doc.insertString(doc.getLength(), "\n     * "+values[i], defaultStyle);						
					}
					doc.insertString(doc.getLength(), "\n", defaultStyle);
				}
			}
			doc.insertString(doc.getLength(), "\n Other packages run in their own process (with the same name).", defaultStyle);
		} catch (BadLocationException e) { }
		processInfoFrame.setLayout(new BorderLayout());
		JScrollPane scrollPanel = new JScrollPane();
		processInfoFrame.setPreferredSize(new Dimension(400,500));
		processInfoFrame.add(scrollPanel);
		scrollPanel.getViewport().add(text);
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fSize = this.getSize();
		processInfoFrame.setLocation((sSize.width-fSize.width)/2, (sSize.height-fSize.height)/2);

		processInfoFrame.pack();
		processInfoFrame.setVisible(true);
	}

	private void newtablePopup() {

		tablePopup = new JPopupMenu();
		JMenuItem jmiDelete =new JMenuItem("Delete Selected Lines");
		jmiDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				for (int row : processtable.getSelectedRows() )
					tablemodel.removeRow(row);
				tablePopup.setVisible(false);
			}
		});
		tablePopup.add(jmiDelete);

		JMenuItem jmiadd=new JMenuItem("add a row");
		jmiadd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				tablemodel.addRow();
				tablePopup.setVisible(false);
			}
		});
		tablePopup.add(jmiadd);
	}


	private JComboBox buildProcessList() {
		if (phone !=null) {
			return new JComboBox(phone.getMonitorList());
		} else {
			JComboBox mybox = new JComboBox(USUAL_GRAPH_LIST);
			mybox.setEditable(true);
			return mybox;
		}
	}

	/**
	 * 
	 * @return a JPpanel to select which event we want following
	 */
	private Component buildGlobalEventPanel() {

		JPanel Eventpanel = new JPanel();
		Eventpanel.setBorder( BorderFactory.createTitledBorder("Events") );

		events = new EventlistModel();
		final JList eventlist = new JList(events);
		eventlist.setPreferredSize(new Dimension(100,100));

		eventlist.setBorder(BorderFactory.createTitledBorder("marked event"));
		Eventpanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0,0,
				1,3,
				1.,1.,
				GridBagConstraints.BASELINE_LEADING,
				GridBagConstraints.BOTH,
				new Insets(1,1,1,1),
				0,0);
		Eventpanel.add(eventlist,c);

		/*c.gridwidth = 2; c.gridheight=1;
		c.weightx=0.01; c.weighty=0.1;
		c.gridx=1;

		final JComboBox markername = new JComboBox(DEFAULT_MARKERS);
		markername.setEditable(true);
		Eventpanel.add(markername,c);

		c.gridy=1;
		final JTextField markerposition = new JTextField("position");
		//Eventpanel.add(markerposition,c);

		c.gridy = 2;c.gridwidth=1;
		JButton addmarker = new JButton("Add");
		addmarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String stringposition = markerposition.getText();
				Double position;
				try {
					position = Double.parseDouble(stringposition);
				}catch(NumberFormatException exception) {
					Logger.getLogger(this.getClass() ).warn("WARNING : unrecognized marker position. set a random one");
					position = Math.random();
				}
				events.addEvent( (String) markername.getSelectedItem(), position);

				saveconfig();
			}			
		});

		//Eventpanel.add(addmarker,c);*/

		// c.gridx = 2;
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		JButton removemarker = new JButton("Remove");
		removemarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				events.remove(eventlist.getSelectedIndex() );
			}
		});
		if (!this.isDefaultConfig) Eventpanel.add(removemarker,c);

		return Eventpanel;
	}


	/**
	 * 
	 * @return a JPANEL which contains all parameters for show or not devices graphs
	 */
	private Component buildGlobalGraphPanel() {
		final JPanel globalpanel = new JPanel();
		globalpanel.setBorder( BorderFactory.createTitledBorder("Device graphs") );

		globalpanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0,0,
				1,8,
				1.,1.,
				GridBagConstraints.BASELINE_LEADING,
				GridBagConstraints.BOTH,
				new Insets(1,1,1,1),
				0,0);

		graphs = new GraphlistModel();
		final JList graphslist = new JList(graphs);
		graphslist.setPreferredSize(new Dimension(100,100));

		graphslist.setBorder(BorderFactory.createTitledBorder("Graphics"));

		globalpanel.add(graphslist, c);

		c.gridwidth = 2; c.gridheight=1;
		c.weightx=0.01; c.weighty=0.1;
		c.gridx=1;
		final JTextField graphname = new JTextField("name");
		graphname.setEditable(false);
		globalpanel.add(graphname,c);

		c.gridy=1;
		final JComboBox graphcolor= new JComboBox(CreateGraph.COLORS);
		globalpanel.add(graphcolor,c);


		c.gridy=2;
		final JTextField graphcommentX = new JTextField("Comment X axis");
		graphcommentX.setEditable(false);
		globalpanel.add(graphcommentX,c);

		c.gridy=3;
		final JTextField graphcommentY = new JTextField("Comment Y axis");
		graphcommentY.setEditable(false);
		globalpanel.add(graphcommentY,c);

		c.gridy=4;
		final JTextField graphunit = new JTextField("Unit");
		graphunit.setEditable(false);
		globalpanel.add(graphunit,c);

		c.gridy=5;
		final JCheckBox graphsampled = new JCheckBox("Sampled");
		//globalpanel.add(graphsampled,c);

		c.gridy=6;
		final JTextField graphscale = new JTextField("Scale");
		graphscale.setEditable(false);
		globalpanel.add(graphscale,c);
		
		c.gridy=7;
		final JTextField graphtype = new JTextField("Type");
		graphtype.setEditable(false);
		globalpanel.add(graphtype,c);
		
		c.gridy = 8;c.gridwidth=1;
		JButton addmarker = new JButton("Add");
		addmarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphs.addgraph(graphname.getText(),
						(String) graphcolor.getSelectedItem(),
						graphcommentX.getText(),
						graphcommentY.getText(),
						graphunit.getText(),
						graphscale.getText(),
						graphsampled.isSelected(),
						graphtype.getText());

				saveconfig();
			}			
		});
		//globalpanel.add(addmarker,c);


		graphcolor.addActionListener(new ActionListener()
		{


			public void actionPerformed(ActionEvent e) {
				int i = graphslist.getSelectedIndex();
				if(-1==i)
					return;
				graphs.changecolor(
						(String) graphcolor.getSelectedItem(),i);
				saveconfig();
			}
		});


		c.gridx = 2;
		JButton removemarker = new JButton("Remove");
		removemarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphs.remove(graphslist.getSelectedIndex() );
				graphslist.update(graphslist.getGraphics());
			}
		});
		if (!this.isDefaultConfig) globalpanel.add(removemarker,c);

		c.gridx = 3;
		JButton savemarker = new JButton("Save");
		savemarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = graphslist.getSelectedIndex();
				if(-1==i)
					return;
				graphs.savegraph(graphname.getText(),
						(String) graphcolor.getSelectedItem(),
						graphcommentX.getText(),
						graphcommentY.getText(),
						graphunit.getText(),
						graphscale.getText(),
						graphsampled.isSelected(),
						graphtype.getText(),
						i);
				saveconfig();
			}			
		});
		//globalpanel.add(savemarker,c);

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

			}

			public void mouseExited(MouseEvent arg0) {

			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {

			}			
		});


		return globalpanel;
	}



	/**
	 * call when user has changed configuration in interface
	 */
	private void saveconfig() {
		//build xml
		Logger.getLogger(this.getClass() ).debug("save config");
		Document configxml = DocumentHelper.createDocument();
		Element root = configxml.addElement( "confile" );

		Element graphlist = root.addElement( "graphlist" );

		//global graph
		for(int i=0 ; i<graphs.getSize() ; i++) {	        	
			graphlist.addElement("graph")
			.addAttribute( "name", graphs.getName(i) )
			.addAttribute( "color", graphs.getColor(i) )
			.addAttribute( "xcomment", graphs.getcommentX(i) )
			.addAttribute( "ycomment", graphs.getcommentY(i) )
			.addAttribute( "unit", graphs.getunit(i) )
			.addAttribute( "scale", graphs.getscale(i) )
			.addAttribute( "sampled", graphs.getsampled(i) );
		}

		//table Graph
		if(tablemodel !=null)
			for(int i=0 ; i<tablemodel.getRowCount() ; i++) {
				//don't save non-fill row
				String processname = (String) tablemodel.getValueAt(i,0) ;
				String type  = (String) tablemodel.getValueAt(i,1) ;
				String color = (String) tablemodel.getValueAt(i,2) ;

				if ("process".equals(processname ) ||  "color".equals(color)||  "Cpu or Mem".equals(type) 	){
					JOptionPane.showMessageDialog(null, "You must select the process, the color AND the type! \nThe line will be ignored.");
					continue;
				}

				String unit = "";
				String scale = "1";
				if(type.equals("Cpu"))
					unit = "%";
				else if(type.equals("Memory")){
					unit = "KBytes";
				}
				else if(type.equals("Data sent")){
					unit = "KBytes"; 
					scale = "1000";
				}
				else if(type.equals("Data received")){
					unit = "KBytes";
					scale = "1000";
				}
				graphlist.addElement("graph")
				.addAttribute( "name", type+"_"+processname )
				.addAttribute( "color", color )
				.addAttribute( "xcomment",  "time (min)")
				.addAttribute( "ycomment",  type+" "+processname)
				.addAttribute( "unit",unit  )
				.addAttribute( "scale", scale )
				.addAttribute( "sampled", "true" );
			}


		//marker
		Element markerlist = root.addElement( "markerlist" );
		for (int i=0 ; i<events.getSize() ; i++) 
			markerlist.addElement("marker")
			.addAttribute( "name", events.getName(i) )
			.addAttribute( "color", "gray" )
			.addAttribute( "position", events.getPosition(i).toString() );

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer =null;
		try {
			writer = new XMLWriter( new FileWriter( configpath ), format  );
			writer.write( configxml );

			//
			//				XMLWriter out = new XMLWriter(System.out,format);
			//				out.write(configxml);

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer !=null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}


	/**
	 * the model of the table. 
	 */
	class processTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private ArrayList<String[]> data;
		private final String[] titres = new String[]{"Process","Resource","Color"};


		public processTableModel() {
			//super();
			data = new ArrayList<String[]>();
			//addRow();
		}

		public int getColumnCount() {
			return titres.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public Object getValueAt(int arg0, int arg1) {
			return data.get(arg0)[arg1];
		}

		@Override
		public String getColumnName(int col){
			return titres[col];
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return true;
		}

		@Override
		public void setValueAt(Object arg0, int arg1, int arg2) {
			data.get(arg1)[arg2] = (String) arg0;
			fireTableCellUpdated(arg1, arg2);
			//saveconfig();
		}

		public void addRow() {
			data.add(new String[]{"process","resource","color"});
			fireTableRowsInserted(data.size() -1, data.size());
		}

		public void removeRow(int ref) {
			data.remove(ref);
			fireTableRowsDeleted(ref -1, ref);
			saveconfig();
		}

		public void add(String name, String type, String color) {
			data.add(new String[]{name, type, color});
			fireTableRowsInserted(data.size() -1, data.size());

		}		

	}


	/**
	 * Store in the list all information corresponding to the marker
	 */
	private class EventlistModel extends DefaultListModel{

		/**
		 * 
		 */
		private static final long serialVersionUID = -624127605307334812L;
		//index and size should correspond
		private ArrayList<String> events;
		private ArrayList<Double> marker;

		public EventlistModel() {
			events = new ArrayList<String>();
			marker = new ArrayList<Double>();
		}

		public Double getPosition(int i) {
			return marker.get(i);
		}

		public String getName(int i) {
			return events.get(i);
		}

		public Object getElementAt(int index) {
			return events.get(index);
		}

		public int getSize() {
			return events.size();
		}

		public Object remove(int index) {
			marker.remove(index);
			Object name= events.remove(index);
			fireIntervalRemoved(name, Math.max(events.size()-2,0), events.size() );
			return name;
		}

		public void addEvent(String name, double position) {
			events.add(name);
			marker.add(position);
			fireIntervalAdded(name, Math.max(events.size()-2,0), events.size() );
		}

	}


	/**
	 * Store in the list all information corresponding to the graphs
	 */
	private class GraphlistModel extends DefaultListModel{

		private static final long serialVersionUID = -1912190623557488673L;
		//index and size should correspond
		private ArrayList<String> graphname;
		private ArrayList<String> color;
		private ArrayList<String> commentX;
		private ArrayList<String> commentY;
		private ArrayList<String> unit;
		private ArrayList<String> scale;
		private ArrayList<Boolean> sampled;
		private ArrayList<String> type;

		public GraphlistModel() {
			graphname = new ArrayList<String>();
			color = new ArrayList<String>();
			commentX = new ArrayList<String>();
			commentY = new ArrayList<String>();
			unit = new ArrayList<String>();
			scale = new ArrayList<String>();
			sampled = new ArrayList<Boolean>();
			type = new ArrayList<String>();
		}

		public String getcommentY(int i) {
			return commentY.get(i);
		}		

		public String getunit(int i) {
			return unit.get(i);
		}

		public String getscale(int i) {
			return scale.get(i);
		}

		public String gettype(int i) {
			return type.get(i);
		}
		
		public String getsampled(int i) {
			return sampled.get(i).toString();
		}

		public String getcommentX(int i) {
			return commentX.get(i);
		}

		public String getColor(int i) {
			return color.get(i);
		}

		public Object getElementAt(int index) {
			return graphname.get(index);
		}

		public int getSize() {
			return graphname.size();
		}

		//for save use
		public String getName(int i) {
			return graphname.get(i);
		}
		public Object remove (int i) {
			color.remove(i);
			commentX.remove(i);
			commentY.remove(i);
			unit.remove(i);
			scale.remove(i);
			sampled.remove(i);
			type.remove(i);
			Object name = graphname.remove(i);
			fireIntervalRemoved(name, Math.max(graphname.size()-2,0), graphname.size() );
			return name;
		}

		public void addgraph(String name, String color, String commentX, String commentY, String unit, String scale, Boolean sampled, String type) {
			this.graphname.add(name);
			this.color.add(color);
			this.commentX.add(commentX);
			this.commentY.add(commentY);
			this.unit.add(unit);
			this.scale.add(scale);
			this.sampled.add(sampled);
			this.type.add(type);

			fireIntervalAdded(name, Math.max(graphname.size()-2,0), graphname.size() );
		}

		public void changecolor(String color, Integer index) {
			this.color.set(index,color);

		}

		public void savegraph(String name, String color, String commentX, String commentY, String Unit, String scale, Boolean sampled, String type, Integer index) {
			this.graphname.set(index, name);
			this.color.set(index,color);
			this.commentX.set(index,commentX);
			this.commentY.set(index,commentY);
			this.unit.set(index,Unit);
			this.scale.add(scale);
			this.sampled.set(index,sampled);
			this.type.add(type);
		}
	}


	/**
	 * 
	 */
	private class ConfigSAXHandler extends DefaultHandler {

		PhoneConfigurationWizard gui;

		public ConfigSAXHandler(PhoneConfigurationWizard phoneConfigurationWizard) {
			gui = phoneConfigurationWizard;
		}

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			if(name.equals("graph")) {
				String lname="";
				String color="";
				String xcomment="";
				String ycomment="";
				String unit="";
				String scale="";
				Boolean sampled=true;
				String type="";
				for (int i=0 ; i<attributes.getLength() ; i++){
					if(attributes.getQName(i).equals("name"))
						lname = attributes.getValue(i);
					if(attributes.getQName(i).equals("color"))
						color = attributes.getValue(i);
					if(attributes.getQName(i).equals("xcomment"))
						xcomment = attributes.getValue(i);
					if(attributes.getQName(i).equals("ycomment"))
						ycomment = attributes.getValue(i);
					if(attributes.getQName(i).equals("unit"))
						unit = attributes.getValue(i);	
					if(attributes.getQName(i).equals("scale"))
						scale = attributes.getValue(i);	
					if(attributes.getQName(i).equals("sampled"))
						sampled = attributes.getValue(i).equals("true");	
					if(attributes.getQName(i).equals("type"))
						type = attributes.getValue(i);	
				}
				if(lname.startsWith("Cpu_")) {
					String[] tableLine = lname.split("_");
					tablemodel.add(tableLine[1], tableLine[0],color);
				}else if(lname.startsWith("Memory_")) {
					String[] tableLine = lname.split("_");
					tablemodel.add(tableLine[1], tableLine[0],color);
				}else if(lname.startsWith("Data sent_")) {
					String[] tableLine = lname.split("_");
					tablemodel.add(tableLine[1], tableLine[0],color);
				}else if(lname.startsWith("Data received_")) {
					String[] tableLine = lname.split("_");
					tablemodel.add(tableLine[1], tableLine[0],color);
				}else {
					gui.graphs.addgraph(lname, color, xcomment, ycomment, unit, scale, sampled, type);
				}

			}else if(name.equals("marker")) {
				String lname="";
				Double position=0.1;
				for (int i=0 ; i<attributes.getLength() ; i++){
					if(attributes.getQName(i).equals("name"))
						lname = attributes.getValue(i);
					if(attributes.getQName(i).equals("position"))
						position = Double.parseDouble( attributes.getValue(i) );

				}
				gui.events.addEvent(lname, position);
			}
		}
	}

}
