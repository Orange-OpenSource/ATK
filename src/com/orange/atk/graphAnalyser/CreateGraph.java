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
 * File Name   : CreateGraph.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import com.orange.atk.monitoring.Event;
import com.orange.atk.monitoring.Graph;
import com.orange.atk.monitoring.MonitoringConfig;
import com.orange.atk.phone.TcpdumpLineListener;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.GraphGenerator;
import com.orange.atk.results.logger.documentGenerator.PlotReader;
import com.orange.atk.results.logger.log.ActionsLogger;
import com.orange.atk.results.measurement.PlotList;
import com.orange.atk.util.NetworkAnalysisUtils;

public class CreateGraph implements TcpdumpLineListener {

	public static final String[] COLORS = {"black", "blue", "cyan", "gray", "green",
			"magenta", "orange", "pink", "red", "yellow"};
	public static final Color[] COLORS_ = {Color.decode("#000000"), Color.decode("#29A2C6"),
			Color.decode("#4FD5D6"), Color.decode("#666666"), Color.decode("#73B66B"),
			Color.decode("#660099"), Color.decode("#FF6D31"), Color.decode("#EF597B"),
			Color.decode("#EE0000"), Color.decode("#FFCB18")};
	private Map<String, PerformanceGraph> mapPerfGraph = null;
	private Map<String, GraphMarker> mapAction = null;
	private static Map<String, Color> mapColor = null;
	private List<Event> tempVectMarker = null;
	private List<Graph> tempVectGraph = null;
	private ChartPanel chartPanel = null;
	private XYPlot xyplot;
	private ActionsLogger actionslogger = null;

	private JFreeChart jfreechart;

	public CreateGraph() {
		// jfreechart = ChartFactory.createXYLineChart("Performances",
		// "time (min)", null, null, PlotOrientation.VERTICAL, false, true,
		// false);
		jfreechart = ChartFactory.createTimeSeriesChart("Performances", "Time (min:sec)", null,
				null, false, true, false);
		jfreechart.setBackgroundPaint(Color.white);
		xyplot = jfreechart.getXYPlot();
		xyplot.setOrientation(PlotOrientation.VERTICAL);
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.lightGray);
		xyplot.setRangeGridlinePaint(Color.lightGray);
		chartPanel = new ChartPanel(jfreechart);
		mapColor = new HashMap<String, Color>();
		setMapcolor();
		xyplot.setDomainCrosshairVisible(true);
		xyplot.setDomainCrosshairLockedOnData(false);
		xyplot.setRangeCrosshairVisible(false);

		mapPerfGraph = new HashMap<String, PerformanceGraph>();
		mapAction = new HashMap<String, GraphMarker>();
	}

	public void generateImage(String path)
	{
		for (int i = 0; i < tempVectGraph.size(); i++) {
			Graph temp = tempVectGraph.get(i);
			String name = temp.getName();
			float Scale;
			try {
				Scale = Float.parseFloat(temp.getScale());
			} catch (NumberFormatException e1) {
				Logger.getLogger(this.getClass()).warn("non float value for scale in config file");
				Scale = 1;
			}
			String ycomment = temp.getYcomment();

			// Check if file is empty
			File file = new File(path + Platform.FILE_SEPARATOR + name + ".csv");
			if (file.exists())
			{
				Logger.getLogger(this.getClass()).debug("Length" + file.length());
				if (file.length() == 0)
				{
					file.delete();
					Logger.getLogger(this.getClass()).debug("delete file" + name + ".csv");
				} else
				{

					try {
						PlotList plotlist = PlotReader.read(new BufferedReader(new FileReader(
								file.getAbsolutePath())));
						// Storage, blue, x Storage, y Storage, 1, true
						GraphGenerator.generateGraphWithJFreeChart(
								plotlist,
								name, path,
								ycomment,
								path + Platform.FILE_SEPARATOR + name + ".png", Scale);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						Logger.getLogger(this.getClass()).error(e);
					}
				}
			}
			file = null;
		}
	}

	/**
	 * add a PerformanceGraph in HashMap
	 * 
	 * @param path
	 *            repertory where files are stored stream where the data will be
	 *            saved
	 * @param name
	 *            Name of the file to add
	 * @param mycolor
	 *            color of the graph
	 * @param index
	 *            index for graph overlay in Jfreechart
	 */

	public void addPerfGraph(String path, String name, String mycolor, int index, String ycomment,
			String unit, int scale) {

		File file = new File(path + Platform.FILE_SEPARATOR + name + ".csv");
		if (file.length() != 0)
		{
			Color color = mapColor.get(mycolor);
			PerformanceGraph graph = new PerformanceGraph(index, path + Platform.FILE_SEPARATOR
					+ name + ".csv", ycomment, color, name, xyplot, unit, scale);
			mapPerfGraph.put(name, graph);
		}
		file = null;

	}

	/**
	 * True if url markers are displayed
	 */
	private boolean urlMarkersDisplayed = true;

	public void displayUrlMarkers(boolean shown) {
		urlMarkersDisplayed = shown;
		if (shown) {
			drawMarker("URL");
		} else {
			removeMarker("URL");
		}
	}

	/**
	 * Read all plt files and create dataset for jfreechart Read action.xml and
	 * create markers
	 * 
	 * @param path
	 *            repertory where files are stored stream where the data will be
	 *            saved
	 * @return if the data have been well saved, false otherwise
	 */
	public void createMyDataset(String path) {
		String realpath = path + Platform.FILE_SEPARATOR;
		mapPerfGraph = new HashMap<String, PerformanceGraph>();
		tempVectGraph = new ArrayList<Graph>();
		tempVectMarker = new ArrayList<Event>();
		mapAction = new HashMap<String, GraphMarker>();
		actionslogger = new ActionsLogger();

		MonitoringConfig config;
		try {
			config = MonitoringConfig.fromFile(realpath + "Confile.xml");
			init(config);
		} catch (IOException e1) {
			Logger.getLogger(this.getClass()).error(e1);
		}
		for (int i = 0; i < tempVectGraph.size(); i++) {
			Graph g = tempVectGraph.get(i);
			String name = (String) g.getName();
			String mycolor = (String) g.getColor();
			String ycomment = g.getYcomment();
			String unit = g.getUnit();
			if (!unit.equals(""))
				ycomment += " (" + unit + ")";
			int scale = Integer.parseInt(g.getScale());
			addPerfGraph(path, name, mycolor, i, ycomment, unit, scale);
		}
		// add data
		fillAllDataset();
		// initialize time axis
		initializeTimeAxis();
		// create Dataset
		createDatasets();

		// Calculate and add Markers
		actionslogger.load(new File(realpath + "actions.xml"));
		for (int i = 0; i < getTempVectMarker().size(); i++) {
			Event temp = getTempVectMarker().get(i);
			String name = (String) temp.getName();
			Color color = mapColor.get((String) temp.getColor());
			GraphMarker marker = new GraphMarker(xyplot,
					actionslogger.getSpecificActionsVect(name), color);
			mapAction.put(name, marker);
		}
		xyplot.getRangeAxis().setVisible(false);
	}

	/**
	 * Create all dataset
	 */
	public void fillAllDataset() {
		Set<String> cles = mapPerfGraph.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
			Logger.getLogger(this.getClass()).debug("create dataset of graph " + cle);
			graph.createPlots();
		}
	}

	public long initializeTimeAxis() {
		Set<String> cles = mapPerfGraph.keySet();
		Iterator<String> it = cles.iterator();
		long initialvalue = 0;
		while (it.hasNext()) {
			String cle = (String) it.next();
			PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
			if (graph.getmintimestamp() < initialvalue || initialvalue == 0
					&& graph.getmintimestamp() != -1) {
				initialvalue = graph.getmintimestamp();
			}
		}
		if (initialvalue == -1) {
			initialvalue = (new Date()).getTime();
		}
		DateAxis axis = (DateAxis) xyplot.getDomainAxis();
		// axis.setTickUnit(new DateTickUnit(DateTickUnit.SECOND, 10));
		RelativeDateFormat rdf = new RelativeDateFormat(initialvalue);
		rdf.setSecondFormatter(new DecimalFormat("00"));
		axis.setDateFormatOverride(rdf);

		return initialvalue;
	}

	public void createDatasets() {
		Set<String> cles = mapPerfGraph.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
			graph.createDataset();
		}
	}

	public long getInitialValue(String pclogOutputDir)
	{
		long initValue = 0;
		Set<String> cles = mapPerfGraph.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
			long graphInitValue = graph.getmintimestamp();
			if (graphInitValue != 0 && initValue < graphInitValue) {
				initValue = graphInitValue;
			}
		}
		return initValue;
	}

	public boolean addrefgraph(String pclogOutputDir, File file, String color,
			LectureJATKResult frame) {
		long initValue = getInitialValue(pclogOutputDir);
		// get value for new graph
		PlotList newplts;
		try {
			newplts = PlotReader.read(new BufferedReader(new FileReader(
					pclogOutputDir + Platform.FILE_SEPARATOR + file.getName())));

			// change date of value
			long initialValue = newplts.getInitialValue();
			newplts.changeTimeScale((initValue - initialValue));
			// Generate file with new timescale
			GraphGenerator.dumpInFile(newplts, pclogOutputDir + Platform.FILE_SEPARATOR
					+ Platform.FILE_SEPARATOR + file.getName()); // always save
																	// with 1
																	// scale
			// add graph
			// TODO read conffile associated with this CSV graph file
			addPerfGraph(file.getParent(), file.getName().replace(".csv", ""), color,
					getMapPerfGraph().size(), file.getName().replace(".csv", ""), "", 1);
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass()).error(e);
			JOptionPane.showMessageDialog(frame, "FileNotFoundException", "Add ref Chart",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			Logger.getLogger(this.getClass()).error(e);
			JOptionPane.showMessageDialog(frame, "ArrayIndexOutOfBoundsException", "Add ref Chart",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
			JOptionPane.showMessageDialog(frame, "IOException", "Add ref Chart",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Read all plt files and create dataset for jfreechart Read action.xml and
	 * create markers
	 * 
	 * @param path
	 *            repertory where files are stored stream where the data will be
	 *            saved
	 * @return if the data have been well saved, false otherwise
	 */
	public boolean createPerfGraphsAndMarkers(String confilepath) {
		boolean empty = true;
		mapPerfGraph = new HashMap<String, PerformanceGraph>();
		// simule la lecture d'un fichier de config
		tempVectGraph = new ArrayList<Graph>();
		tempVectMarker = new ArrayList<Event>();
		mapAction = new HashMap<String, GraphMarker>();
		actionslogger = new ActionsLogger();

		MonitoringConfig config;
		try {
			config = MonitoringConfig.fromFile(confilepath);
			init(config);
		} catch (IOException e1) {
			Logger.getLogger(this.getClass()).error(e1);
		}

		for (int i = 0; i < tempVectGraph.size(); i++) {
			Graph temp = tempVectGraph.get(i);
			String name = temp.getName();
			Color color = mapColor.get(temp.getColor());
			String unit = temp.getUnit();
			String ycomment = temp.getYcomment();
			if (!unit.equals("")) {
				ycomment += " (" + unit + ")";
			}
			int scale = Integer.parseInt(temp.getScale());
			PerformanceGraph graph = new PerformanceGraph(i, confilepath + name + ".csv", ycomment,
					color, name, xyplot, unit, scale);
			mapPerfGraph.put(name, graph);
			empty = false;
		}
		for (int i = 0; i < getTempVectMarker().size(); i++) {
			Event temp = getTempVectMarker().get(i);
			String name = (String) temp.getName();
			Color color = mapColor.get((String) temp.getColor());
			GraphMarker marker = new GraphMarker(xyplot,
					actionslogger.getSpecificActionsVect(name), color);
			marker.setActivate(true);
			mapAction.put(name, marker);
		}
		return empty;
	}

	public void createEmptyDataset() {
		Set<String> cles = mapPerfGraph.keySet();
		Iterator<String> it = cles.iterator();

		while (it.hasNext()) {
			String cle = (String) it.next();
			PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
			graph.createEmptyDataset();
		}
		xyplot.getRangeAxis().setVisible(false);
	}

	/**
	 * Create marker vector this vector will be composed of three information
	 * necessary to display
	 * 
	 * @param name
	 *            name of the plt file
	 * @param position
	 *            comment position
	 * @param color
	 *            comment color
	 * 
	 * @return if the data have been well saved, false otherwise
	 */
	public void createMarkerVector(String name, double position, String color) {
		getTempVectMarker().add(new Event(name, position, color));
	}

	/**
	 * Refresh display on graph to display
	 */
	public void refreshGraph() {
		Set<String> cles = mapAction.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			GraphMarker marker = (GraphMarker) mapAction.get(cle);
			marker.refreshMarker();
		}
	}

	/**
	 * Clear all marker and annotation on display
	 */
	public void clearMarker() {
		Set<String> cles = mapAction.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			GraphMarker marker = (GraphMarker) mapAction.get(cle);
			marker.removeMarker();
		}
	}

	/**
	 * Draw a specific set of marker
	 * 
	 * @param name
	 *            name of marker set
	 */
	public void drawMarker(String key) {
		mapAction.get(key).drawMarker();
	}

	/**
	 * Remove a specific set of marker
	 * 
	 * @param name
	 *            name of marker set
	 */
	public void removeMarker(String key) {
		mapAction.get(key).removeMarker();
	}

	/**
	 * Draw a specific graph
	 * 
	 * @param GraphName
	 *            Graph name
	 */
	public void drawGraph(String GraphName) {
		mapPerfGraph.get(GraphName).drawGraph();
		refreshGraph();
	}

	/**
	 * Unraw a specific graph
	 * 
	 * @param GraphName
	 *            Graph name
	 */
	public void undrawGraph(String GraphName) {
		mapPerfGraph.get(GraphName).removeGraph();
		refreshGraph();
	}

	/**
	 * Define initial vector to define a set of color available
	 * 
	 */
	private final void setMapcolor() {
		for (int i = 0; i < COLORS.length; i++) {
			mapColor.put(COLORS[i], COLORS_[i]);
		}
	}

	private void init(MonitoringConfig config) {
		for (Graph g : config.getGraphs()) {
			this.getTempVectGraph().add(g);
		}
		for (Event e : config.getEvents()) {
			this.getTempVectMarker().add(e);
		}

	}
	public ChartPanel getChartpanel() {
		return chartPanel;
	}

	public Map<String, PerformanceGraph> getMapPerfGraph() {
		if (mapPerfGraph == null) {
			mapPerfGraph = new HashMap<String, PerformanceGraph>();
		}
		return mapPerfGraph;
	}

	public Map<String, GraphMarker> getMapAction() {
		if (mapPerfGraph == null) {
			mapAction = new HashMap<String, GraphMarker>();
		}
		return mapAction;
	}

	public static Map<String, Color> getMapColor() {
		return mapColor;
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public ActionsLogger getActionslogger() {
		return actionslogger;
	}

	public JFreeChart getJfreechart() {
		return jfreechart;
	}

	public List<Graph> getTempVectGraph() {
		return tempVectGraph;
	}

	public List<Event> getTempVectMarker() {
		return tempVectMarker;
	}

	/**
	 * A new tcpdump line has been received
	 */
	public void newTcpDumpLine(String line) {
		Date theDate = NetworkAnalysisUtils.extractTcpdumpLineDate(line);
		String url = NetworkAnalysisUtils.extractTcpdumpLineUrl(line);
		if (theDate != null && url != null) {
			GraphMarker urlMarker = mapAction.get("URL");
			if (urlMarker != null) {
				urlMarker.addEvent("Type", url, theDate, theDate);
				// Set marker to hidden if urlMarkers checkbox is not checked
				if (urlMarkersDisplayed == false) {
					urlMarker.removeMarker();
				}
				mapAction.put("URL", urlMarker);
			}
		}
	}
}