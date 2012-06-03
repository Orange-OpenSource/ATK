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
 * File Name   : LectureJATKResult.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;

/*
 * LectureJATKResult.java
 *
 * Created on 18 novembre 2008, 15:38
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.NumberCellRenderer;

import com.orange.atk.interpreter.config.ConfigFile;
import com.orange.atk.platform.Platform;
import com.orange.atk.util.FileUtil;

/**
 *
 * @author  ywil8421
 */


public class LectureJATKResult extends JFrame implements ChartChangeListener, ChartProgressListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -913181162748664117L;
	private ChartPanel chartPanel;
	CreateGraph analyzerGraphs;
	String path = "";
	private DefaultListModel listModel;
	private DefaultListModel listModelMarker;
	Map<String, PerformanceGraph> mapPerfGraph;
	Map<String, GraphMarker> mapAction = null;
	DefaultComboBoxModel comboModelLeft = null;
	DefaultComboBoxModel comboModelRight = null;
	private DemoTableModel modeltable;
	private listenPerfGraphHandler listenPerf;
	private ListSelectionListener listenMarker;
	LectureJATKResult frame;
	
    public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	private String name =null;



	/** Creates new form NewJFrame */
	public LectureJATKResult() {
		listModel = new DefaultListModel();
		listModelMarker = new DefaultListModel();
		comboModelLeft = new DefaultComboBoxModel();
		comboModelRight = new DefaultComboBoxModel();
		analyzerGraphs = new CreateGraph();
		chartPanel = analyzerGraphs.getChartpanel();
		analyzerGraphs.getJfreechart().addChangeListener(this);
		analyzerGraphs.getJfreechart().addProgressListener(this);
		chartPanel.setDomainZoomable(true);
		chartPanel.setRangeZoomable(true);
		chartPanel.setAutoscrolls(true);
		frame = this;
		//init model table
		int SERIES_COUNT = 1;
		this.modeltable = new DemoTableModel(SERIES_COUNT);
		for (int row = 0; row < SERIES_COUNT; row++) {
			this.modeltable.setValueAt(
					"", row, 0);
			this.modeltable.setValueAt(new Double("0"), row, 1);
			this.modeltable.setValueAt(new Double("0"), row, 2);
			this.modeltable.setValueAt(new Double("0"), row, 3);

		}
		
		initComponents();
		
		jTable2.getColumnModel().getColumn(0).setCellRenderer(new ColorRenderertext());
		jListGraph.setCellRenderer(new MyCellRenderer());
		jComboBoxLeft.setRenderer(new MyCellRenderer());
		jComboBoxRight.setRenderer(new MyCellRenderer());

		
	}

	
	
	
	
	/**
	 * Receives notification of a {@link ChartChangeEvent}.
	 *
	 * @param event  the event.
	 */
	public void chartChanged(ChartChangeEvent event) {
		if (this.chartPanel != null) {
			JFreeChart chart = this.chartPanel.getChart();
			if (chart != null) {
				XYPlot plot = chart.getXYPlot();

				//recupere X 
				double xx = plot.getDomainCrosshairValue();
				if (xx != 0 && mapPerfGraph != null ) {
					Set<String> cles = mapPerfGraph.keySet();
					Iterator<String> it = cles.iterator();
					int index =0;
					while (it.hasNext()) {
						String cle = (String) it.next();
						PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
					  
						graph.getY(xx);
						double Xvalue =graph.getXvalue();
						double xvaluenext =graph.getNextXvalue();
						double xvalueprev =graph.getPrevousxValue();
						this.modeltable.setValueAt(graph.getSerieName().replace("Series ", ""), index, 0);
						this.modeltable.setValueAt(new Double(Xvalue), index,1 );
						this.modeltable.setValueAt(new Double(xvaluenext), index,2 );
						this.modeltable.setValueAt(new Double(xvalueprev), index,3 );
						index++;
					}
				}
			}
		}
	}

	/**
	 * Handles a chart progress event.
	 *
	 * @param event
	 *            the event.
	 */
	public void chartProgress(ChartProgressEvent event) {
		if (event.getType() != ChartProgressEvent.DRAWING_FINISHED) {
			return;
		}
		if (this.chartPanel != null) {
			JFreeChart c = this.chartPanel.getChart();
			if (c != null) {
				XYPlot plot = c.getXYPlot();
				double xx = plot.getDomainCrosshairValue();
				if (xx != 0 && mapPerfGraph != null ) {

					Set<String> cles = mapPerfGraph.keySet();
					Iterator<String> it = cles.iterator();
					int index =0;
					while (it.hasNext()) {
						String cle = (String) it.next();
						PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);

						String Name =graph.getSerieName();   
						graph.getY(xx);

						double Yvalue =graph.getYvalue();
						double Yvaluenext =graph.getNextyvalue();
						double Yvalueprev =graph.getPrevousyValue();
						Name= Name.replace("Series ", "");
						this.modeltable.setValueAt(Name, index, 0);
						this.modeltable.setValueAt(new Double(Yvalue), index,1 );
						this.modeltable.setValueAt(new Double(Yvaluenext), index,2 );
						this.modeltable.setValueAt(new Double(Yvalueprev), index,3 );
						index++;
					}
					// update the table...
				}
			}
		}
	}

	
	
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		jListGraph = new JList(listModel);
		jComboBoxLeft = new JComboBox(comboModelLeft);
		jComboBoxRight = new JComboBox(comboModelRight);
		jListMarker = new JList(listModelMarker);
		jTable2 = new javax.swing.JTable();
		jMenu1 = new javax.swing.JMenu();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jComboBoxLeft.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxLeftActionPerformed(evt);
			}
		});

		jComboBoxRight.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxRightActionPerformed(evt);
			}
		});

		jTable2.setModel(modeltable);


		jMenu1.setText("File");

		JMenuItem jMenuItem1 = new JMenuItem();
		jMenuItem1.setText("Open Directory");
		jMenuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openDirectoryAction(evt);
			}
		});

		JMenuItem jMenuItem2 = new JMenuItem();
		jMenuItem2.setText("Add a reference Graph");
		jMenuItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jMenuItemaddGraphActionPerformed(evt);
			}
		});

		JMenuItem jMenuItem3 = new JMenuItem();
		jMenuItem3.setText("set Graph color");
		jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemChangecolorActionPerformed(evt);
			}
		});

		JMenuItem jMenuItem4 = new JMenuItem();
		jMenuItem4.setText("save config file");
		jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemSaveConfigFileActionPerformed(evt);
			}
		});
		jMenu1.add(jMenuItem1);
		jMenu1.add(jMenuItem2);
		jMenu1.add(jMenuItem3);
		jMenu1.add(jMenuItem4);

		JMenuBar jMenuBar1 = new JMenuBar();
		jMenuBar1.add(jMenu1);

		setJMenuBar(jMenuBar1);

		
		 //organise JFRAME
        JPanel mainpanel = (JPanel) getContentPane();
        mainpanel.setLayout(new BorderLayout());
        
        mainpanel.add(chartPanel, BorderLayout.CENTER);
        
        
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout());
        
        toolPanel.add(jComboBoxLeft);
        
        Box graphbox = Box.createVerticalBox();
        graphbox.add(new JLabel("List of Graph "));
        JScrollPane jspaneGraph = new JScrollPane();
        jspaneGraph.setViewportView(jListGraph);
        jspaneGraph.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspaneGraph.setPreferredSize(new Dimension(150,100));
        
        graphbox.add( jspaneGraph );
  //      graphbox.setBorder(BorderFactory.createLineBorder(Color.black));
        toolPanel.add(graphbox);
        
        Box markerbox = Box.createVerticalBox();
        markerbox.add(new JLabel("List of Marker"));
        JScrollPane jspaneMarker = new JScrollPane();
        jspaneMarker.setViewportView(jListMarker);
        jspaneMarker.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspaneMarker.setPreferredSize(new Dimension(150,100));
        markerbox.add(jspaneMarker );
  //      markerbox.setBorder(BorderFactory.createLineBorder(Color.black));
        toolPanel.add(markerbox);
        
        JScrollPane jspane = new JScrollPane();
        jspane.setViewportView(jTable2);
        jspane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspane.setPreferredSize(new Dimension(300,100));
        toolPanel.add(jspane );
        toolPanel.add(jComboBoxRight);
        
       mainpanel.add(toolPanel, BorderLayout.NORTH);
       

		pack();
	}





	/**
	 * set parameters (used in jpf IHM).
	 *
	 * @param path location of the csv file and action.xml 
	 *            
	 */

	public void setParameters(String path) {                                           
		//this.setName(path);
		this.setTitle(path);
		//Try to load default dir
		ConfigFile configFile=null;

		String JATKpath = Platform.getInstance().getJATKPath();


		String pathihmconfig=JATKpath+Platform.FILE_SEPARATOR+"log"+Platform.FILE_SEPARATOR+"ConfigIHM.cfg";
		File ihmconfig =new File(pathihmconfig);
	  	configFile = new ConfigFile(ihmconfig);
	  	configFile.setOption("path_READGRAPH", path);
		configFile.saveConfigFile();
		//clean graph
		cleanlistElement();
		
		analyzerGraphs.createMyDataset(path);
		mapPerfGraph = analyzerGraphs.getMapPerfGraph();
		mapAction = analyzerGraphs.getMapAction();
		addPerformanceslist();
		addMarkerlist();
		//Add listener on list of graph and markers
		setListenerMarkerGraph();


		//Crosshair Value Renderer jet model
		setRendererandModelSeries();		

	}


	
	
	
	
	

	/**
	 * Jmenu add a Graph.
	 *
	 *            
	 */
	private void jMenuItemaddGraphActionPerformed(ActionEvent evt) {                                           

		new AddGraphDialog(this,true);

	}

	
	/**
	 * Change color.
	 *
	 *            
	 */
	private void jMenuItemChangecolorActionPerformed(ActionEvent evt) {                                           


		new ChangeColorDialog(this,true,analyzerGraphs.getMapPerfGraph());

	}
	
	


	/**
	 * save config file.
	 *
	 *            
	 */
	private void jMenuItemSaveConfigFileActionPerformed(ActionEvent evt) {                                           



		String JATKpath = Platform.getInstance().getJATKPath();
		String pathihmconfig=JATKpath+Platform.FILE_SEPARATOR+"log"+Platform.FILE_SEPARATOR+"ConfigIHM.cfg";
		//get a value from confile
		String Scriptpath =getvalueconfigfile(pathihmconfig, "path_READGRAPH");
		

				PrintStream ps=null;
				try {
					ps = new PrintStream(new FileOutputStream(Scriptpath+Platform.FILE_SEPARATOR+"Confile2.xml"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Logger.getLogger(this.getClass() ).warn("Can't Create config file");
					return;
				}
				ps.println("<confile>");
				ps.println("<graphlist>");


				 Set<String> cles = mapPerfGraph.keySet();
			     Iterator<String> it = cles.iterator();
			     while (it.hasNext()) {
		            String cle = (String) it.next();
		            PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);

		        	ps.println("<graph  name=\"" + cle+".csv" + "\""
							+ " color=\""+getcolor(graph.getColor())+"\""+"/>");
		        }
				ps.println("</graphlist>");
				ps.println("<markerlist>");
				ps.println("<marker  name=\"keyPress\" position=\"0.2\"  color=\"gray\"/>");
				ps.println("<marker  name=\"log\" position=\"0.4\"  color=\"gray\"/>");
				ps.println("<marker  name=\"Action\" position=\"0.5\"  color=\"gray\"/>");
				ps.println("<marker  name=\"Standard Out/Err\" position=\"0.7\"  color=\"gray\"/>");
				ps.println("<marker  name=\"ScreenShot\" position=\"0.9\"  color=\"gray\"/>");
				ps.println("<marker  name=\"Error JATK\" position=\"0.9\"  color=\"gray\"/>");
				ps.println("</markerlist>");
				
				
				
				
				ps.println("</confile>");
				ps.flush();
				ps.close();
	        
	        
		
	}
	
	public String getcolor(Color color)
	{
		Map<String, Color> mapColor =CreateGraph.getMapColor();
		
		if(mapColor.containsValue(color))
		{
		
			  Set<String> cles = mapColor.keySet();
		        Iterator<String> it = cles.iterator();
		        while (it.hasNext()) {
		            String cle = (String) it.next();
		            Color tempcolor =  mapColor.get(cle);
		            if(tempcolor.equals(color))
		            	return cle;
        
		        }
			
		}
		return null;
	}
	/**
	 * add a new graph
	 *
	 * @param path cvsfilepath  path of csv file
	 * @param path color  color of the graph
	 */


	public void addrefGraph(String cvsfilepath,String color ){
		//Try to load default dir
		mapPerfGraph = analyzerGraphs.getMapPerfGraph();
		mapAction = analyzerGraphs.getMapAction();
		File file =new File(cvsfilepath);
		cleanlistElement();
		
		String JATKpath = Platform.getInstance().getJATKPath();
		String pathihmconfig=JATKpath+Platform.FILE_SEPARATOR+"log"+Platform.FILE_SEPARATOR+"ConfigIHM.cfg";
		//get a value from confile
		String pclogOutputDir =getvalueconfigfile(pathihmconfig, "path_READGRAPH");
		
		//Check Name if allready exist change it
		if(mapPerfGraph.containsKey(file.getName().replace(".csv","")))
			new GetinfoDialog(this,  true,"Enter new name:",file.getName().replace(".csv",""));
		Logger.getLogger(this.getClass() ).debug("New Name is:"+this.getName());
		if(!this.getName().endsWith(".csv"))
		{
		setName(this.getName()+".csv");	
		}
		
		File newfile =new File(pclogOutputDir+Platform.FILE_SEPARATOR+this.getName());
    	FileUtil.copyfile(newfile, file);
		

		analyzerGraphs.addrefgraph( pclogOutputDir, newfile, color,this);	
		analyzerGraphs.fillAllDataset();
		
		//initialize time axis
	    analyzerGraphs.initializeTimeAxis();
	    //create Dataset
	    analyzerGraphs.createDatasets();
	     
		addPerformanceslist();
		addMarkerlist();
		//Add listener on list of graph and markers
		setListenerMarkerGraph();
		//Crosshair Value Renderer et model
		setRendererandModelSeries();		
	}     
	/**
	 * add all graph from a directory
	 *
	 */
	private void openDirectoryAction(ActionEvent evt) {                                           
		ConfigFile configFile=null;
		String JATKpath = Platform.getInstance().getJATKPath();
		String pathihmconfig=JATKpath+Platform.FILE_SEPARATOR+"log"+Platform.FILE_SEPARATOR+"ConfigIHM.cfg";
		File ihmconfig =new File(pathihmconfig);
	  	configFile = new ConfigFile(ihmconfig);
		//get a value from confile
		String Scriptpath =getvalueconfigfile(pathihmconfig, "path_READGRAPH");
		//open JfileChooser
		final JFileChooser fc = new JFileChooser(Scriptpath);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(jMenu1);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			Scriptpath=file.getAbsolutePath();
			if(Scriptpath!=null)
			configFile.setOption("path_READGRAPH", Scriptpath);
			configFile.saveConfigFile();
			//clean graph
			cleanlistElement();
			
			analyzerGraphs.createMyDataset(file.getPath());
			mapPerfGraph = analyzerGraphs.getMapPerfGraph();
			mapAction = analyzerGraphs.getMapAction();
			
			addPerformanceslist();
			addMarkerlist();
			
			//Add listener on list of graph and markers
			setListenerMarkerGraph();
			//Crosshair Value Renderer et model
			setRendererandModelSeries();		

		}
	}          
	
	
	
	
	/**
	 * clean list on with it is possible to select graph and markers to display
	 *
	 */
	
	
	public void cleanlistElement()
	{
		if(mapPerfGraph!=null)
			listModel.removeAllElements();

		if(mapAction!=null)
			listModelMarker.removeAllElements();		
	}
	
	
	
	
	/**
	 * add all graph from a directory
	 *
	 * @param pathihmconfig path of the config file
	 * @param value value to get in config file
	 */
	
	
	public String getvalueconfigfile(String pathihmconfig,String value)
	{
		
		File ihmconfig =new File(pathihmconfig);
		try {
			ConfigFile configFile;
			if(ihmconfig.exists())
			{
				configFile = new ConfigFile(ihmconfig);
				configFile.loadConfigFile();
					String result =configFile.getOption(value);
					return result;

			}
			else  {
				ihmconfig.createNewFile();
				Logger.getLogger(this.getClass() ).debug("New config file created");
		//		configFile = new ConfigFile(ihmconfig);

			}
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass() ).debug( e.getMessage());
		}	
		
		return null;
		
	}
	
	
	
	
	/**
	 * Add listener on perf Grapg and marker to be able to add and remove graph
	 *
	 */
	
	
	public void setListenerMarkerGraph(){
		listenPerf=  new listenPerfGraphHandler();
		listenMarker= new listenMarkerHandler();
		jListGraph.getSelectionModel().addListSelectionListener(listenPerf);
		jListMarker.getSelectionModel().addListSelectionListener(listenMarker);
		
	}
	
	
	
	/**
	 * set renderers
	 *
	 */
	
	
	public void setRendererandModelSeries(){

		int size = analyzerGraphs.getMapPerfGraph().size();		        
		this.modeltable = new DemoTableModel(size);

		int row = 0;
		if (mapPerfGraph != null) {
			Set<String> cles = mapPerfGraph.keySet();
			Iterator<String> it = cles.iterator();
			while (it.hasNext()) {
				String cle = (String) it.next();
				PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);

				this.modeltable.setValueAt(graph.getSerieName().replace("Series ", ""), row, 0);
				this.modeltable.setValueAt(new Double("0"), row, 1);
				this.modeltable.setValueAt(new Double("0"), row, 2);
				this.modeltable.setValueAt(new Double("0"), row, 3);
				//    this.modeltable.setValueAt(graph.getColor(), row, 4);          
				row++;
			}
		}

		
		jTable2.setModel(modeltable);
		TableCellRenderer renderer = new NumberCellRenderer();
		jTable2.getColumnModel().getColumn(0).setCellRenderer(new ColorRenderertext());
		jTable2.getColumnModel().getColumn(1).setCellRenderer(renderer);
		jTable2.getColumnModel().getColumn(2).setCellRenderer(renderer);
		jTable2.getColumnModel().getColumn(3).setCellRenderer(renderer);
		jListGraph.setCellRenderer(new MyCellRenderer());
		
	}
	
	/*
	 * 
	 * Liste gauche choix du graph
	 * 
	 */
	private void jComboBoxLeftActionPerformed(ActionEvent evt) {                                           
		if (mapPerfGraph != null) {
			if (comboModelLeft.getSize() != 0 && comboModelLeft.getSelectedItem() != null) {
				String cle = comboModelLeft.getSelectedItem().toString();
				PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
				graph.setAxis(false, AxisLocation.BOTTOM_OR_RIGHT);
				graph.setAxis(true, AxisLocation.BOTTOM_OR_LEFT);
			}
		}  
	}
	
	/* 
	 * Liste droite choix du graph
	 * 
	 */
	private void jComboBoxRightActionPerformed(ActionEvent evt) {                                           
		if (mapPerfGraph != null) {
		if (comboModelRight.getSize() != 0 && comboModelRight.getSelectedItem() != null) {
				String cle = comboModelRight.getSelectedItem().toString();
				PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
				graph.setAxis(false, AxisLocation.BOTTOM_OR_LEFT);
				graph.setAxis(true, AxisLocation.BOTTOM_OR_RIGHT);
			}
		}
	}                                       


	
	
	/**
	 * add list of performances graphs
	 *
	 */

	public void addPerformanceslist() {
		if (mapPerfGraph != null) {
			Set<String> cles = mapPerfGraph.keySet();
			Iterator<String> it = cles.iterator();
			while (it.hasNext()) {
				String cle = (String) it.next();
				listModel.addElement(cle);
			}
		}
	}

	
	
	/**
	 * add list markers
	 *
	 */
	public void addMarkerlist() {
		if (mapAction != null) {
			Set<String> cles = mapAction.keySet();
			Iterator<String> it = cles.iterator();
			while (it.hasNext()) {
				String cle = (String) it.next();
				listModelMarker.addElement(cle);
			}
		}
	}






	/**
	 * @param args the command line arguments
	 */
	 public static void main(String args[]) {
		 
		 //initialise log4j
		 DOMConfigurator.configure("log4j.xml");
			
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new LectureJATKResult().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	 private javax.swing.JComboBox jComboBoxLeft;
	 private javax.swing.JComboBox jComboBoxRight;
	 private javax.swing.JList jListGraph;
	 private javax.swing.JList jListMarker;
	 private javax.swing.JMenu jMenu1;	
	 private javax.swing.JTable jTable2;

	 // End of variables declaration



	 class listenPerfGraphHandler implements ListSelectionListener {




		 public void valueChanged(ListSelectionEvent e) {
			 
			 ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			 int index = 0;
			 if (mapPerfGraph != null && !e.getValueIsAdjusting()) {
				 Set<String> cles = mapPerfGraph.keySet();
				 Iterator<String> it = cles.iterator();
				 while (it.hasNext()) {
					 String cle = (String) it.next();
					 PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
					 if (lsm.isSelectedIndex(index)) {
						 //Now Active
						 if (!graph.isActive()) {
							 
							 analyzerGraphs.drawGraph(cle);
							 comboModelLeft.insertElementAt(cle, comboModelLeft.getSize());
							 comboModelRight.insertElementAt(cle, comboModelRight.getSize());
						 }
					 } else {
						 //now inactive 
						 if (graph.isActive()) {
							 analyzerGraphs.undrawGraph(cle);
							 comboModelLeft.removeElement(cle);
							 comboModelRight.removeElement(cle);
						 }
					 }
					 index++;
				 }
				 frame.pack();

			 }
		 }
	 }

	 class listenMarkerHandler implements ListSelectionListener {

		 public void valueChanged(ListSelectionEvent e) {
			 ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			 int index = 0;
			 if (mapAction != null && !e.getValueIsAdjusting()) {
				 Set<String> cles = mapAction.keySet();
				 Iterator<String> it = cles.iterator();
				 while (it.hasNext()) {
					 String cle = (String) it.next();
					 GraphMarker marker = (GraphMarker) mapAction.get(cle);
					 if (lsm.isSelectedIndex(index)) {
						 //Now Active  
						 if (!marker.isActivate()) {
							 analyzerGraphs.drawMarker(cle);
						 }
					 } else {
						 //now inactive
						 if (marker.isActivate()) {
							 analyzerGraphs.removeMarker(cle);
						 }
					 }
					 index++;
				 }
			 }
		 }
	 }

	 @SuppressWarnings("serial")
	class ColorRenderertext extends JLabel	 implements TableCellRenderer {
		 Border unselectedBorder = null;
		 Border selectedBorder = null;
		 boolean isBordered = true;

		 public ColorRenderertext() {

			 setOpaque(true); //MUST do this for background to show up.
		 }

		 public Component getTableCellRendererComponent(
				 JTable table, Object value,
				 boolean isSelected, boolean hasFocus,
				 int row, int column) {
			 if(value!=null){
				 setText(value.toString());         
				 if(isSelected )
					 setBackground(Color.LIGHT_GRAY);
				 else
					 setBackground(Color.white);

				 if (mapPerfGraph != null) {
					 Set<String> cles = mapPerfGraph.keySet();
					 Iterator<String> it = cles.iterator();
					 while (it.hasNext()) {
						 String cle = (String) it.next();
						 PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);

						 if(value.equals(graph.getSerieName().replace("Series ", "")))
						 {
							 setForeground(graph.getColor());
							 setToolTipText("RGB value: " + graph.getColor().getRed() + ", "
									 + graph.getColor().getGreen() + ", "
									 + graph.getColor().getBlue());

							 if(graph.isActive())
							 {
								 setBackground(Color.LIGHT_GRAY);

							 }
						 }
					 }
				 }
			 }



			 return this;
		 }
	 }

	 @SuppressWarnings("serial")
	static class DemoTableModel extends AbstractTableModel implements TableModel {

		 private Object[][] data;

		 /**
		  * Creates a new table model
		  *
		  * @param rows  the row count.
		  */
		 public DemoTableModel(int rows) {
			 this.data = new Object[rows][7];
		 }

		 /**
		  * Returns the column count.
		  *
		  * @return 7.
		  */
		 public int getColumnCount() {
			 return 4;
		 }

		 /**
		  * Returns the row count.
		  *
		  * @return The row count.
		  */
		 public int getRowCount() {
			 return this.data.length ;
		 }

		 /**
		  * Returns the value at the specified cell in the table.
		  *
		  * @param row  the row index.
		  * @param column  the column index.
		  *
		  * @return The value.
		  */
		 public Object getValueAt(int row, int column) {
			 return this.data[row][column];
		 }

		 /**
		  * Sets the value at the specified cell.
		  *
		  * @param value  the value.
		  * @param row  the row index.
		  * @param column  the column index.
		  */
		 public void setValueAt(Object value, int row, int column) {
			 this.data[row][column] = value;
			 fireTableDataChanged();
		 }

		 /**
		  * Returns the column name.
		  *
		  * @param column  the column index.
		  *
		  * @return The column name.
		  */
		 public String getColumnName(int column) {
			 switch(column) {
			 case 0 : return "Series Name:";
			 case 1 : return "Y:";
			 case 2 : return "Y (next):";
			 case 3 : return "Y (prev)";
			 //  case 4 : return "Color";

			 }
			 return null;
		 }



	 }

	 @SuppressWarnings("serial")
	class MyCellRenderer extends JLabel implements ListCellRenderer {

		 public MyCellRenderer() {
			 setOpaque(true);
		 }	

		 public Component getListCellRendererComponent(JList list,
				 Object value,
				 int index,
				 boolean isSelected,
				 boolean cellHasFocus)
		 {

			 if(value!=null){
				 setText(value.toString());         
				 if(isSelected )
					 setBackground(Color.LIGHT_GRAY);
				 else
					 setBackground(Color.white);


				 if (mapPerfGraph != null) {
					 Set<String> cles = mapPerfGraph.keySet();
					 Iterator<String> it = cles.iterator();
					 while (it.hasNext()) {
						 String cle = (String) it.next();
						 PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);

						 if(value.equals(cle))
						 {
							 
							 setForeground(graph.getColor());
						 }
					 }
				 }
			 }
			 return this;

		 }
	 }
}

