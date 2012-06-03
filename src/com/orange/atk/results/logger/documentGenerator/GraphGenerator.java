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
 * File Name   : GraphGenerator.java
 *
 * Created     : 28/03/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.results.logger.documentGenerator;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.orange.atk.graphAnalyser.RelativeDateFormat;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.measurement.PlotList;

public class GraphGenerator {

	/**
	 * Generate a graph in png from the provided plot list. The X axis of the
	 * graph is in minutes.
	 * 
	 * @param plotList
	 *            plotlist to save. Xvalues must be stored in milliseconds.
	 * @param associatedName
	 *            kind of the list
	 * @param folderWhereResultsAreSaved
	 *            folder where graph while be saved
	 * @param yLabel
	 *            name of the y label
	 * @param pictureFile
	 *            name of the file where the picture would be saved (path should
	 *            be absolute)
	 * @param yDivisor divisor of the measurements
	 */
	public static void generateGraph(PlotList plotList, String associatedName,
			String folderWhereResultsAreSaved, String yLabel,
			String pictureFile, float yDivisor) {
		Logger.getLogger("generateGraph").
			debug(folderWhereResultsAreSaved + associatedName + ".cmd");
		// Store measurements in a file
		try {
			dumpInFile(plotList, folderWhereResultsAreSaved + associatedName
					+ ".csv");

			// Create gnuplot scripts used to generate graphs
			if (plotList.isEmpty()) {
				Logger.getLogger(GraphGenerator.class )
						.warn(associatedName + " plot list is empty");
				return;
			}

			// create a .cmd which will be given to the gnuplot program
			File commandFile = new File(folderWhereResultsAreSaved
					+ associatedName + ".cmd");
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(commandFile)));
			// Picture will be saved in png
			bufferedWriter.write("set terminal png" + Platform.LINE_SEP);
			// Name of the picture
			bufferedWriter.write("set output '" + folderWhereResultsAreSaved
					+ associatedName + ".png'" + Platform.LINE_SEP);
			// format of the number on the y-axis
			bufferedWriter.write("set format y \"%.3s\"" + Platform.LINE_SEP);
			// Names of the axis
			bufferedWriter.write("set xlabel \"Time\""
					+ Platform.LINE_SEP);
			bufferedWriter.write("set ylabel \"" + yLabel + "\""
					+ Platform.LINE_SEP);
			// Set the range on y axis
			bufferedWriter.write("set yrange ["
					+ (plotList.getMin() / yDivisor) * 0.9998 + ":"
					+ (plotList.getMax() / yDivisor) * 1.0002 + "]"
					+ Platform.LINE_SEP);
			bufferedWriter.write("set xtics autofreq" + Platform.LINE_SEP);
			bufferedWriter.write("set ytics autofreq" + Platform.LINE_SEP);
			bufferedWriter.write("plot '" + folderWhereResultsAreSaved
					+ associatedName + ".csv' with lines" + Platform.LINE_SEP);
			bufferedWriter.flush();
			bufferedWriter.close();

			// call gnuplot for generating graphs
			Runtime runtime = Runtime.getRuntime();
			String gnuplotName = null;
			if (Platform.OS_NAME.toLowerCase().contains("windows")) {
				gnuplotName = "wgnuplot";
			} else {
				gnuplotName = "gnuplot";
			}
			String[] cmdsCpu1 = { gnuplotName,
					folderWhereResultsAreSaved + associatedName + ".cmd" };
			if (!plotList.isEmpty()) {
				// Call gnuplot
				int returnValue = runtime.exec(cmdsCpu1).waitFor();
				if (returnValue != 0) {
					Logger.getLogger(GraphGenerator.class )
						.warn("Problem while creating graph. Does "
							+ gnuplotName + " program belongs to PATH?");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function creates the measurement graph by using the JFreeChart
	 * library. The X axis of the graph is in minutes.
	 * 
	 * @param plotList
	 *            plotlist to save. Xvalues must be stored in milliseconds.
	 * @param associatedName
	 *            Name of the list
	 * @param folderWhereResultsAreSaved
	 *            folder where results are saved
	 * @param yLabel
	 *            Name of the y label
	 * @param pictureFile
	 *            name of the file
	 * @param yDivisor
	 *            use to divide measurements stored in the plotlist by yDivisor
	 */
	public static void generateGraphWithJFreeChart(PlotList plotList,
			String associatedName, String folderWhereResultsAreSaved,
			String yLabel, String pictureFile, float yDivisor) {

		// Create a new XYSeries
		// XYSeries are used to represent couples of (x,y) values.
		XYSeries data = new XYSeries(associatedName);

		int size = plotList.getSize();
		if (size == 0) {
			// no element in graphics, exit
			//Logger.getLogger(this.getClass() ).warn("Nothing in graph");
			return;
		}

		// Find the initial value of the time
		// Due to the fact that getX(i) <= getX(i+1),
		// min({0<=i<size / getX(i)}) = getX(0)
		long initialValue =  plotList.getX(0);

		XYSeriesCollection series = new XYSeriesCollection(data);
		if(!plotList.getunit().equals(""))
			yLabel+=" ("+plotList.getunit()+")";
		// Create a new XY graph.
		//JFreeChart chart = ChartFactory.createXYLineChart("", "Time", yLabel, series, PlotOrientation.VERTICAL, true, true, false);
		JFreeChart chart =  ChartFactory.createTimeSeriesChart("", "Time (min:sec)", yLabel, series, true, true, false);
		// Set the graph format
		XYPlot plot = chart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        //axis.setTickUnit(new DateTickUnit(DateTickUnit.SECOND, 10));
        RelativeDateFormat rdf = new RelativeDateFormat(initialValue);
        rdf.setSecondFormatter(new DecimalFormat("00"));
        axis.setDateFormatOverride(rdf);
        
		// Fill the JFreeChart object which will be used to create the Graph
		for (int i = 0; i < size; i++) {
			// xvalue must be in
			double xval = ((Long) plotList.getX(i)).doubleValue();
			float yval = plotList.getY(i).floatValue() / yDivisor;
			// Logger.getLogger(this.getClass() ).debug(associatedName + " [" + (((Long)
			// plotList.getX(i)).floatValue() - initialValue)
			// / XDIVISOR +"] "+ yval);
			data.add(xval, yval);
		}
		

		ValueAxis rangeAxis = plot.getRangeAxis();
		Long min = plotList.getMin();
		Long max = plotList.getMax();
		double diff = (max-min)*0.02;
		if(diff == 0)
			diff = max * 0.0001;
		
		rangeAxis.setLowerBound((min-diff) / yDivisor);
		rangeAxis.setUpperBound((max+diff) / yDivisor);
//		Logger.getLogger(this.getClass() ).debug("(" + (min / yDivisor) * 0.98 + " - "
//				+ (min / yDivisor) * 0.98 + ")");
//		Logger.getLogger(this.getClass() ).debug("Bound = " + rangeAxis.getLowerBound() + " - "
//				+ rangeAxis.getUpperBound());
//		Logger.getLogger(this.getClass() ).debug("Margin = " + rangeAxis.getLowerMargin() + " - "
//				+ rangeAxis.getUpperMargin());
//		Logger.getLogger(this.getClass() ).debug("NB AXIS = " + plot.getRangeAxisCount());

		// save the chart in a picture file.
		BufferedImage bufImage = chart.createBufferedImage(640, 480);
		File fichier = new File(pictureFile);
		try {
			if (!ImageIO.write(bufImage, "png", fichier)) {
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Save the data of a plot list into a file. A divisor value coould be apply
	 * to X, ie values would be dived by Xdivisor value are
	 * stored on each line as :<br/> (x_i-x_0)/Xdivisor \t y <br/>
	 * format, the first value is shifted to 0.
	 * 
	 * @param plotList
	 *            plot list to save
	 * @param fileName
	 *            file name where plot list would be saved
	 * @param Xdivisor
	 *            divisor for the X axis
	 * @param Ydivisor
	 *            divisor for the Y axis
	 * @throws ArrayIndexOutOfBoundsException
	 * @throws IOException
	 */
	public static void dumpInFile(PlotList plotList, String fileName)
			throws ArrayIndexOutOfBoundsException, IOException {
		int iXListSize = plotList.getSize();
		if (iXListSize > 0) {
			// get the first value to translate the x scale
			// x rep
			SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			SimpleDateFormat year = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat ms = new SimpleDateFormat("SSS");

	//		double initialValue = ((Long) plotList.getX(0)).doubleValue();
			File fichier = new File(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fichier)));
			bufferedWriter.write("# "+ spf.format(new Date(plotList.getX(0)))+ "-" + Platform.LINE_SEP);
			bufferedWriter.write("# yyyy-MM-dd, HH:mm:ss:SSS,value"+   Platform.LINE_SEP);
			for (int i = 0; i < iXListSize; i++) {
				// Logger.getLogger(this.getClass() ).debug(((plotList.getX(i)).floatValue()) + " - "
				// + initialValue);
				long xval = (((Long) plotList.getX(i)));
				float yval = plotList.getY(i).floatValue();
				bufferedWriter.write(year.format(new Date(xval))+", " +hour.format(new Date(xval)) +":"+ms.format(new Date(xval))+"," + yval + Platform.LINE_SEP);
				
				// Logger.getLogger(this.getClass() ).debug(xval + "\t" + yval );
			}
			bufferedWriter.close();
		}
	}
}
