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
 * File Name   : DocumentLogger.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.results.logger.log;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.orange.atk.graphAnalyser.PerformanceGraph;
import com.orange.atk.monitoring.Graph;
import com.orange.atk.monitoring.MonitoringConfig;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.measurement.PlotList;

/**
 * This class contains all needed informations to create a report document. It
 * contains measurement data, which have been logged during the test session,
 * logged messages, generated when an error happened, paths to generated graphs.
 */

public class DocumentLogger {

	private Map<String, Color> mapColor = null;
	private Map<String, PlotList> mapint = null;
	private List<Message> msgsLoggued = new ArrayList<Message>();
	private String folderWhereResultsAreSaved = null;
	private Map<String, PerformanceGraph> mapPerfGraph = null;

	/**
	 * Constructor
	 * 
	 * @param folderWhereResultsAreSaved
	 *            folder where file will be saved, must not be null and must be
	 *            valid.
	 */
	public DocumentLogger(String folderWhereResultsAreSaved) {

		this.folderWhereResultsAreSaved = folderWhereResultsAreSaved;
		mapint = new HashMap<String, PlotList>();

	}

	public void addPlotlistObject() {
		Set<String> cles = mapPerfGraph.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
			graph.setPlts(mapint.get(cle));
		}

	}

	public void readconffile(String path) {
		load(path);

	}
	/**
	 * Add a data to a given list
	 * 
	 * @param ListIndice
	 *            corresponding list (BATTERY, CPU, MEMORY, STORAGE)
	 * @param x
	 *            increased value
	 * @param y
	 *            data
	 */
	public void addDataToList(String cle, Long x, Float y) {
		// Add value in Temp file and in plot list
		if (null != getPlotList(cle)) {
			getPlotList(cle).addValue(x, y);
			getPlotList(cle).addNewlineinfile(x, y);
		}

		// Display on Real Time Performance Graph
		double yval = (double) y;

		if ((null != mapPerfGraph) && (null != mapPerfGraph.get(cle))) {
			mapPerfGraph.get(cle).addDatasetValue(x, yval);
		}

	}

	/**
	 * Get min value from a given list.
	 * 
	 * @param ListIndice
	 * @return return the min value from the list, Long.MAX_VALUE is the list is
	 *         empty
	 * @throws ArrayIndexOutOfBoundsException
	 *             if ListIndice is not equals to (BATTERY, CPU, MEMORY,
	 *             STORAGE)
	 */
	public long getMinValueFromList(String cle) {
		return getPlotList(cle).getMin();
	}

	/**
	 * Get max value from a given list.
	 * 
	 * @param ListIndice
	 *            corresponding list (BATTERY, CPU, MEMORY, STORAGE)
	 * @return return the max value from the list, Long.MIN_VALUE is the list is
	 *         empty
	 * @throws ArrayIndexOutOfBoundsException
	 *             if ListIndice is not equals to (BATTERY, CPU, MEMORY,
	 *             STORAGE)
	 */
	public long getMaxValueFromList(String cle) {
		return getPlotList(cle).getMax();
	}

	/**
	 * Get average value from a given list.
	 * 
	 * @param ListIndice
	 *            corresponding list (BATTERY, CPU, MEMORY, STORAGE)
	 * @return return the average value from the list, Double.NaN is the list is
	 *         empty
	 * @throws ArrayIndexOutOfBoundsException
	 *             if ListIndice is not equals to (BATTERY, CPU, MEMORY,
	 *             STORAGE)
	 */
	public double getAveValueFromList(String cle) {
		return getPlotList(cle).getAverage();
	}

	/**
	 * This function returns a list which contains the measurements data.
	 * 
	 * @param ListIndice
	 *            list that we want to get. Only correct values for ListIndice
	 *            are BATTERY, CPU, MEMORY and STORAGE. If ListIndice is equal
	 *            to BATTERY, we get the list which contains all the
	 *            measurements made on the battery.
	 * @return the associated measurement list.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if ListIndice is not equals to (BATTERY, CPU, MEMORY,
	 *             STORAGE)
	 */
	public PlotList getPlotList(String cle) {
		return mapint.get(cle);
	}

	/**
	 * Log an information message. It is generated by Log and screenshot
	 * 
	 * @param s
	 *            message
	 * @param line
	 *            line where this message is generated
	 * @param currentScript
	 *            script where this message is generated
	 */
	public void addInfoToLog(String s, int line, String currentScript) {
		synchronized (msgsLoggued) {
			msgsLoggued
					.add(Message.createInfoMessage(s, new Date().getTime(), line, currentScript));
		}
	}

	/**
	 * Log a warning message. Bad number of parameters, invalid function name,
	 * include file not found, ... produce this kind of message
	 * 
	 * @param s
	 * @param line
	 * @param currentScript
	 */
	public void addWarningToLog(String s, int line, String currentScript) {
		synchronized (msgsLoggued) {
			msgsLoggued.add(Message.createWarningMessage(s, new Date().getTime(), line,
					currentScript));
		}
	}

	/**
	 * Log an error message. Parse error in the main file, ... produce this kind
	 * of message.
	 * 
	 * @param s
	 * @param line
	 * @param currentScript
	 */
	public void addErrorToLog(String s, int line, String currentScript) {
		synchronized (msgsLoggued) {
			msgsLoggued
					.add(Message.createErrorMessage(s, new Date().getTime(), line, currentScript));
		}
	}

	/**
	 * Return the list of loggued messages since the beginning
	 * 
	 * @return a Vector of message, not null
	 */
	public List<Message> getMsgsLogged() {
		return msgsLoggued;
	}

	/**
	 * return png path
	 * 
	 * @return png path
	 */
	public String getPNGpath(String cle) {
		return folderWhereResultsAreSaved + Platform.FILE_SEPARATOR + cle + ".png";
	}

	/**
	 * return plt file
	 * 
	 * @return plt path
	 */
	public String getpltpath(String cle) {
		return folderWhereResultsAreSaved + Platform.FILE_SEPARATOR + cle + ".csv";
	}

	public void setMapcolor() {
		if (mapColor == null) {
			mapColor = new HashMap<String, Color>();
		}
		mapColor.put("blue", Color.blue);
		mapColor.put("red", Color.red);
		mapColor.put("yellow", Color.yellow);
		mapColor.put("black", Color.black);
		mapColor.put("gray", Color.gray);
		mapColor.put("green", Color.green);
		mapColor.put("cyan", Color.cyan);
		mapColor.put("magenta", Color.magenta);
		mapColor.put("orange", Color.orange);
		mapColor.put("pink", Color.pink);
	}

	public Map<String, PlotList> getMapint() {
		return mapint;
	}

	/**
	 * load xml file containing action
	 * 
	 * @param GraphName
	 *            File file to load
	 */
	public boolean load(String filename) {

		MonitoringConfig config;
		try {
			config = MonitoringConfig.fromFile(filename);
			init(config);
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
			return false;
		}

		return true;
	}

	private void init(MonitoringConfig config) {
		for (Graph g : config.getGraphs()) {
			if (!mapint.containsKey(g.getName())) {

				int type = PlotList.TYPE_AVG;
				if (g.getType() != null && g.getType().equals("sum")) {
					type = PlotList.TYPE_SUM;
				}
				PlotList pl = new PlotList(getpltpath(g.getName()),
						getPNGpath(g.getName()), folderWhereResultsAreSaved, g.getXcomment(),
						g.getYcomment(), Integer.parseInt(g.getScale()), g.getSampled(),
						g.getColor(),
						g.getUnit(), type);
				mapint.put(g.getName(), pl);
			}
		}
	}

	public void setMapint(Map<String, PlotList> mapint) {
		this.mapint = mapint;
	}

	public Map<String, PerformanceGraph> getMapPerfGraph() {
		return mapPerfGraph;
	}

	public void setMapPerfGraph(Map<String, PerformanceGraph> mapPerfGraph) {
		this.mapPerfGraph = mapPerfGraph;
	}

}
