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
 * File Name   : PlotList.java
 *
 * Created     : 28/01/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.results.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.orange.atk.platform.Platform;


/**
 * PlotList class saves two lists of data: one for time, the other for
 * measurements.
 */

public class  PlotList {
	// Invariants : xList == yList
	// X values are growing
	public static int TYPE_AVG = 0;
	public static int TYPE_SUM = 1;
	
	private Vector<Long> xList = new Vector<Long>();
	private Vector<Float> yList = new Vector<Float>();
	File pltfile =null;
	String pngpath =null;
	private String xComment =null;
	private String folder=null;
	private String yComment =null;
	BufferedWriter os = null;
	private String color =null;
	private String unit = "";
	private int scale = 1;
	private int type = TYPE_AVG;
	long initialValue=0;	
	boolean issampled =true;


	public long getInitialValue() {
		return initialValue;
	}

	public PlotList(String pltfileName,String pngpath,String folderWhereResultsAreSaved,
			String xcomment,String ycomment,int scale,boolean issampled,String color, String unit) {
		this(pltfileName, pngpath, folderWhereResultsAreSaved,
			xcomment, ycomment, scale, issampled, color, unit, TYPE_AVG);
	}
	/**
	 * @param pltfileName
	 * @param pngpath
	 * @param folderWhereResultsAreSaved
	 * @param xcomment
	 * @param ycomment
	 * @param scale
	 * @param issampled
	 * @param color
	 * @param unit 
	 * @throws FileNotFoundException
	 */

	public PlotList(String pltfileName,String pngpath,String folderWhereResultsAreSaved,
			String xcomment,String ycomment,int scale,boolean issampled,String color, String unit, int type) {
		//if(!unit.equals(""))
			//ycomment+=" ("+unit+")";
		this.pngpath=pngpath;
		this.setFolder(folderWhereResultsAreSaved);
		this.setXComment(xcomment);
		this.setYComment(ycomment);
		this.scale =scale;
		this.issampled=issampled;
		this.color =color;
		this.unit = unit;
		this.type = type;
		
		pltfile = new File(pltfileName);	
		try {
			os = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(pltfile,true)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PlotList(String pltfileName,String pngpath,String folderWhereResultsAreSaved,
			String xcomment,String ycomment,int scale,boolean issampled,String color) {
		this(pltfileName,pngpath,folderWhereResultsAreSaved,
				xcomment,ycomment,scale,issampled,color,"");
	}
	
	public PlotList() throws FileNotFoundException{

	}

	public void setScale(int scale) {
		this.scale=scale;
	}
	public int getScale() {
		return scale;
	}
	/**
	 * Add a single measurement. Time is automatically added.
	 * 
	 * @param y
	 *            measurement data
	 */
	public synchronized void  addValue(Float y) {
		addValue(Long.valueOf((new Date()).getTime()), y);
	}

	/**
	 * Add a single measurement at a given interval. If getSize() > 0 then x >=
	 * getX(getSize()-1)). If not, the couple (x,y) is not added.
	 * 
	 * @param x
	 *            time in ms.
	 * @param y
	 *            measurement data
	 */
	public synchronized void addValue(Long x, Float y) {
		synchronized (xList) {
			synchronized (yList) {
				if (getSize() > 0) {
					if (x < getX(getSize() - 1)) {
						Logger.getLogger(this.getClass() ).debug("Invalid x value "+getSize());
						return;
					}
				}
				if(xList.size()==0)
					initialValue=x;				
				xList.add(x);
				yList.add(y);
			}
		}
	}

	public synchronized void changeTimeScale(Long x) {
		synchronized (xList) {
			int size =xList.size();
			for(int index=0;index<size;index++)
			{
				long value =xList.get(index);
				xList.set(index, value+x);

			}		
			initialValue=xList.get(0);
		}
	}


	/**
	 * Write data contains in lists in a file. Values are stored on each line as :<br/>
	 * x_i-x_min y <br/> format, the first value is shifted to 0.
	 * 
	 * @param fileName
	 *            file where values are stored
	 * @throws IOException
	 *             if file could not be created
	 */
	public synchronized void addNewlineinfile(long Xvalue, float YValue) {
		// get the first value to translate the x scale
		// x rep
		//Logger.getLogger(this.getClass() ).debug("X:"+Xvalue+" Y: "+YValue+" Initial Value"+initialValue);

		try {

			if(getSize()==1)
			{
				SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
				os.write("# "+ spf.format(new Date(getX(0)))+ "-" + Platform.LINE_SEP);	
			}

			//		SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			SimpleDateFormat year = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat ms = new SimpleDateFormat("SSS"); 

			os.write(year.format(new Date(Xvalue))+", " +hour.format(new Date(Xvalue)) +":"+ms.format(new Date(Xvalue))+"," + YValue + Platform.LINE_SEP);
			os.flush();
		} catch (IOException e) {
			Logger.getLogger(this.getClass() ).debug("Can't Write .csv file");
			e.printStackTrace();
		}
	}

	/**
	 * Write data contains in lists in a file. Values are stored on each line as :<br/>
	 * x_i-x_min y <br/> format, the first value is shifted to 0.
	 * 
	 * @param fileName
	 *            file where values are stored
	 * @throws IOException
	 *             if file could not be created
	 */
	public  void addNewlineinfile(long Xvalue,String YValue) {
		// get the first value to translate the x scale
		// x rep
		//Logger.getLogger(this.getClass() ).debug("X:"+Xvalue+" Y: "+YValue+" Initial Value"+initialValue);

		try {

			if(getSize()==1)
			{
				SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
				os.write("# "+ spf.format(new Date(getX(0)))+ "-" +   Platform.LINE_SEP);	
			}


			//		SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			SimpleDateFormat year = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat ms = new SimpleDateFormat("SSS"); 
			os.write(year.format(new Date(Xvalue))+", " +hour.format(new Date(Xvalue)) +":"+ms.format(new Date(Xvalue))+"," + YValue + Platform.LINE_SEP);
			os.flush();
		} catch (IOException e) {
			Logger.getLogger(this.getClass() ).debug("Can't Write .csv file");
			e.printStackTrace();
		}
	}
	public void closefile()
	{
		try {
			if(os!=null)
				os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * calculate (max_x-min_x)/range. If empty, the value is 0.
	 * 
	 * @param range
	 *            range
	 * @return (max_x-min_x)/range
	 */
	public Long getXScale(long range) {
		if (isEmpty()) {
			return 0L;
		}
		return Long
		.valueOf((((Long) Collections.max(xList)).longValue() - ((Long) Collections
				.min(xList)).longValue())
				/ range);
	}

	/**
	 * Get the min value from data. If no data is given, return Long.MAX_VALUE.
	 * 
	 * @return the min value
	 */
	public Long getMin() {
		if (isEmpty()) {
			return Long.valueOf(Long.MAX_VALUE);
		}
		return (Long) Collections.min(yList).longValue();
	}

	/**
	 * Get the max value from data. If no data is given, return Long.MIN_VALUE.
	 * 
	 * @return the max value
	 */
	public Long getMax() {
		if (yList.isEmpty()) {
			return Long.valueOf(Long.MIN_VALUE);
		}
		return (Long) Collections.max(yList).longValue();
	}

	/**
	 * Return the average value from data. If no data is given, return NaN;
	 * 
	 * @return average value, with two decimal
	 */
	public double getAverage() {
		if (getSize() == 0) {
			return Double.NaN;
		}
		long sum = 0;
		double size = getSize();
		for (int i = 0; i < size; i++) {
			sum += (getY(i)).longValue();
		}
		return new BigDecimal(((double) sum) / size).setScale(2,
				BigDecimal.ROUND_FLOOR).doubleValue();
	}

	/**
	 * Return the total value from data. If no data is given, return NaN;
	 * */
	public double getTotal() {
		if (getSize() == 0) {
			return Double.NaN;
		}
		long sum = 0;
		double size = getSize();
		for (int i = 0; i < size; i++) {
			sum += (getY(i)).longValue();
		}
		return sum;
	}

	/**
	 * Return the variance value from data If no data is given, return NaN;
	 * 
	 * @return variance value
	 */
	public double getDelta() {
		if (getSize() == 0) {
			return Double.NaN;
		}
		double average = getAverage();
		double sum = 0;
		double size = getSize();
		for (int i = 0; i < size; i++) {
			sum += Math.pow(average - (getY(i)).longValue(), 2);
		}
		return new BigDecimal(Math.sqrt(sum) / size).setScale(2,
				BigDecimal.ROUND_FLOOR).doubleValue();
	}

	/**
	 * Indicate if value(s) has(have) been saved
	 * 
	 * @return true if at least one value has been saved, false otherwise.
	 */
	public boolean isEmpty() {
		return (xList.size() == 0);
	}

	/**
	 * Return the current number of data saved
	 * 
	 * @return the number of data saved
	 */
	public int getSize() {
		return xList.size();
	}

	/**
	 * Return the i-th value of time
	 * 
	 * @param index
	 *            index
	 * @return the i-th value of time
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             if index < 0 or index >= getSize()
	 */
	public Long getX(int index) throws ArrayIndexOutOfBoundsException {
		return xList.get(index);
	}

	/**
	 * Return the i-th value of data
	 * 
	 * @param index
	 *            index
	 * @return the i-th value of data
	 * @throws ArrayIndexOutOfBoundsException
	 *             if index < 0 or index >= getSize()
	 * 
	 */
	public Float getY(int index) throws ArrayIndexOutOfBoundsException {
		return yList.get(index);
	}


	public void setFolder(String folder) {
		this.folder = folder;
	}


	public String getFolder() {
		return folder;
	}


	public void setYComment(String yComment) {
		this.yComment = yComment;
	}


	public String getYComment() {
		return yComment;
	}


	public void setXComment(String xComment) {
		this.xComment = xComment;
	}


	public String getXComment() {
		return xComment;
	}


	public File getPltfile() {
		return pltfile;
	}


	public void setPltfile(File pltfile) {
		this.pltfile = pltfile;
	}


	public String getPngpath() {
		return pngpath;
	}


	public void setPngpath(String pngpath) {
		this.pngpath = pngpath;
	}


	public boolean isSampled() {
		return issampled;
	}


	public String getColor() {
		return color;
	}


	public String getunit() {
		return unit;
	}
	
	public int getType() {
		return type;
	}
}
