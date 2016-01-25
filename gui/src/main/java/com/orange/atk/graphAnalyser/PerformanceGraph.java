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
 * File Name   : PerformanceGraph.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.orange.atk.results.logger.documentGenerator.PlotReader;
import com.orange.atk.results.measurement.PlotList;


/**
 *
 * @author ywil8421
 */
/**
 * @author  France Telecom R&D
 *(C) France Télécom, 2010.  
 */
public class PerformanceGraph {
	
	private XYDataset xydataset = null;
	private PlotList plts;
	private int graphNumber = 0;
	private String fileName = "";
	private  String serieName = "";
	private Color color = null;
	private AxisLocation location;
	private XYPlot xyplot = null;
	private boolean active = false;
	private NumberAxis numberaxis = null;
	private StandardXYItemRenderer standardxyitemrenderer = null;
	private double xvalue=0;
	private double nextXvalue = 0;
	private double prevousxValue = 0;
	private double yvalue = 0;
	private double nextyvalue = 0;
	private double prevousyValue = 0;
	private XYSeriesCollection series=null;
	private XYSeries data=null;
	private String unit=null;
	private int scale=1;
	
	public PerformanceGraph(int graphNumber, String fileName, String serieName, Color color,
			String axisName, XYPlot xyplot, String unit, int scale) {
		this.graphNumber = graphNumber+1;
		this.serieName = serieName;
		this.fileName = fileName;
		this.color = color;
		this.location = AxisLocation.BOTTOM_OR_RIGHT; // Default
		this.xyplot = xyplot;
		this.unit = unit;
		this.scale = scale;
		standardxyitemrenderer = new StandardXYItemRenderer();
		numberaxis = new NumberAxis(axisName);
		numberaxis.setLabelPaint(color);
		numberaxis.setTickLabelPaint(color);
		numberaxis.setFixedDimension(10D);
		numberaxis.setNumberFormatOverride(new DecimalFormat("###,###,##0.###"));
		numberaxis.setAutoRange(true);
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setLabel(serieName);
	}


	public void setColor(Color color) {
		this.color = color;
	}


	/**
	 * remove graph
	 */ 
	public void removeGraph() {
		xyplot.setDataset(graphNumber, null);
		//xyplot.setRangeAxis(graphNumber, null);
		numberaxis.setVisible(false);
		setActive(false);
	}


	/**
	 * set if axis need to be display
	 * @param activeaxis true to display  legend
	 * @param AxisLocation place to display legend (right or left)
	 */ 
	public void setAxis(boolean activeAxis, AxisLocation location) {
		xyplot.setRangeAxisLocation(graphNumber, location);
		numberaxis.setVisible(activeAxis);

	}


	/**
	 *Create jfreechart datasetS
	 */ 
	public void createPlots() {
		try {
			plts = PlotReader.read(new BufferedReader(new FileReader(
					fileName)));
			plts.setScale(scale);

		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass() ).debug("error" + e);
		}
	}


	public void createDataset()
	{
		Logger.getLogger(this.getClass() ).debug(fileName);
		xydataset = createSeries();
	}


	public long getmintimestamp()
	{
		long value;
		if (plts==null) return -1;
		try {
			value = plts.getX(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}	

		return value;
	}




	public XYDataset createEmptyDataset() {

		series = new XYSeriesCollection();
		data = new XYSeries(serieName);
		series.addSeries(data);
		xydataset= series;

		return xydataset;
	}



	public final synchronized void addDatasetValue(long xval,double yval)
	{
		double y_scaled = yval/scale;
		XYDataItem item =new XYDataItem(xval, y_scaled);
		data.add(item);
		/*if((numberaxis.getLabel().toLowerCase().contains(cpuLabel))&&active){
			Double maxRange;
			Double minRange;
			if(rangeCPU!=null)
				maxRange = rangeCPU.getUpperBound();
			else
				maxRange = numberaxis.getRange().getUpperBound();
			if(rangeCPU!=null)
				minRange = rangeCPU.getLowerBound();
			else
				minRange = numberaxis.getRange().getUpperBound();

			Double diff = 0.0;

			if(y_scaled>maxRange){
				maxRange = y_scaled;
				diff = (maxRange-minRange)*0.02;
			}
			if(y_scaled<minRange){
				minRange = y_scaled;
				diff = (maxRange-minRange)*0.02;
			}
			if((maxRange-minRange)<1){
				diff = maxRange * 0.0002;
			}
			
			rangeCPU = new Range((double)minRange-diff,(double)maxRange+diff);
			if (rangeCPU.getLength()==0) rangeCPU = Range.expand(new Range(0.0, 0.5),0.03,0.0);
			numberaxis.setRange(rangeCPU);
		}

		if((numberaxis.getLabel().toLowerCase().contains(memoryLabel))&&active){
			Double maxRange;
			Double minRange;
			if(rangeMEM!=null)
				maxRange = rangeMEM.getUpperBound();
			else
				maxRange = numberaxis.getRange().getUpperBound();
			if(rangeMEM!=null)
				minRange = rangeMEM.getLowerBound();
			else
				minRange = numberaxis.getRange().getUpperBound();
			
			Double diff = 0.0;

			if(y_scaled>maxRange){
				maxRange = y_scaled;
				diff = (maxRange-minRange)*0.02;
			}
			if(y_scaled<minRange){
				minRange = y_scaled;
				diff = (maxRange-minRange)*0.02;
			}
			if((maxRange-minRange)<1){
				diff = maxRange * 0.0002;
			}
			
			rangeMEM = new Range((double)minRange-diff,(double)maxRange+diff);
			if (rangeMEM.getLength()==0) rangeMEM = Range.expand(new Range(0.0, 0.5),0.03,0.0);
			numberaxis.setRange(rangeMEM);
		}*/

	}

	/**
	 *Create jfreechart series
	 */ 
	private XYDataset createSeries() {
		series = new XYSeriesCollection();
		data = new XYSeries(serieName);
		int size = plts.getSize();
		if (size == 0) {
			Logger.getLogger(this.getClass() ).warn("Nothing in graph");
			return null;
		}

		for (int i = 0; i < size; i++) {
			double xval = ((Long) plts.getX(i)).doubleValue();
			double yval = plts.getY(i).doubleValue() / scale;

			data.add(xval, yval);
		}

		series.addSeries(data);
		return series;
	}




	/**
	 *Draw Graph
	 */ 
	public final synchronized void drawGraph() {


		//on cree le dataset et on le lie au numero sur le xyplot idem pour range
		xyplot.setRangeAxis(graphNumber, numberaxis);
		xyplot.setDataset(graphNumber, xydataset);
		//on lie ensemble le rangeAxis et le dataset
		xyplot.mapDatasetToRangeAxis(graphNumber, graphNumber);
		standardxyitemrenderer.setSeriesPaint(0, color);
		//lie la couleur  a la serie sur le renrerer 
		xyplot.setRenderer(graphNumber, standardxyitemrenderer);
		setActive(true);
		setAxis(true, location);
	}

	public XYDataset getXydataset() {
		return xydataset;
	}


	/**
	 *check is graph is displayed
	 */ 
	public boolean isActive() {
		return active;
	}

	/**
	 *set boolean to indicate that graph is displayed
	 * @param active true if graph is display
	 */ 

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 *get nearest value for the graph
	 * @param initial value of cursor position
	 */ 
	public void getY(double initvalue) {
		resetposition();
		//get by dicotomie Initial Value
		int upint = xydataset.getItemCount(0)-1;

		int downint = 0;
		int index =0;
		int  mvalue=0;
		double dicoValue=0;
		while ((upint - downint) > 2) {
			mvalue =  (upint+downint) / 2;

			dicoValue = xydataset.getXValue(0, (int) mvalue);

			if (initvalue > dicoValue) {
				downint =mvalue;
			} else if (initvalue < dicoValue) {
				upint = mvalue;
			} else if (dicoValue == initvalue) {
				upint = mvalue;
				downint = mvalue;
				break;
			}
		}


		if (upint == downint) {
			index=upint;

		} else if ((xydataset.getXValue(0, upint) - initvalue) > (initvalue - xydataset.getXValue(0, downint))) {
			index=downint;
		} else {
			index=upint;
		}

		xvalue=xydataset.getXValue(0, index);
		yvalue=xydataset.getYValue(0, index);

		if(index<xydataset.getItemCount(0)-1)
		{
			nextXvalue=xydataset.getXValue(0, index+1);
			nextyvalue=xydataset.getYValue(0, index+1);
		}
		if(index>0)
		{
			prevousxValue=xydataset.getXValue(0, index-1);
			prevousyValue=xydataset.getYValue(0, index-1);
		}

	}





	public void resetposition()
	{
		xvalue=0;
		prevousxValue=0;
		nextXvalue=0;
		yvalue=0;
		prevousyValue=0;
		nextyvalue=0;       
	}


	/**
	 *get X value of cursor
	 */ 
	public double getXvalue() {
		return xvalue;
	}


	/**
	 *get next X value of cursor
	 */ 
	public double getNextXvalue() {
		return nextXvalue;
	}


	/**
	 *get previous X value of cursor
	 */ 
	public double getPrevousxValue() {
		return prevousxValue;
	}


	/**
	 *get Y value of cursor
	 */
	public double getYvalue() {
		return yvalue;
	}


	/**
	 *get next Y value of cursor
	 */ 
	public double getNextyvalue() {
		return nextyvalue;
	}


	/**
	 *get previous Y value of cursor
	 */ 
	public double getPrevousyValue() {
		return prevousyValue;
	}

	public String getSerieName() {
		return serieName;
	}

	public Color getColor() {
		return color;
	}

	public String getUnit() {
		return unit;
	}

	public PlotList getPlts() {
		return plts;
	}

	public void setPlts(PlotList plts) {
		this.plts = plts;
	}

	/*public static void setRangeCPU(Range rangeCPU) {
		PerformanceGraph.rangeCPU = rangeCPU;
	}



	public static void setRangeMEM(Range rangeMEM) {
		PerformanceGraph.rangeMEM = rangeMEM;
	}*/

}