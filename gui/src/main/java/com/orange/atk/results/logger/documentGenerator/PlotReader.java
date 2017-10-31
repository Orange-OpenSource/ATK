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
 * File Name   : PlotReader.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.results.logger.documentGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.orange.atk.results.measurement.PlotList;



/**
 * This class reads CSV files where data saved by the JATK interpreter ard stored.
 * The file format is : <br/>
 * # yyyy.MM.dd HH:mm:ss SSS-yMultiplicator<br/>
 * t1 m1<br/>
 * t2 m2<br/>
 * ...<br/>
 * with:<br/>
 * <ul>
 * 	<li>yyyy.MM.dd HH:mm:ss : time stamp of the first measure 	</li>
 *  <li>yMultiplicator : value used to divide measurements 	</li>
 *  <li>ti : relative time of the measurement. The absolute timestamp is equals in ms to: TimeInMs(yyyy.MM.dd HH:mm:ss)+ti*60000</li>
 *  <li>mi : relative value of the measurement. The absolute measurement is equals to : mi*yMultiplicator 	</li>
 * </ul>
 *
 */
public class PlotReader {

	/**
	 * Read the plot data for an input stream
	 * @param in input stream formatted in the specified format
	 * @return a plot list if the format is valid, null otherwise.
	 */
	public static PlotList read(BufferedReader in) {
		String measure=null;
		int index =0;

		//verification du pattern
		Pattern pat;
		Matcher mtc;
		SimpleDateFormat spf = null;	  
		PlotList plotList=null;
		try {
			plotList = new PlotList();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			measure = in.readLine();

			while(measure!=null)
			{	
				spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	  
				pat = Pattern.compile("([0-9]{4}).([0-9]{2}).([0-9]{2})\\s*,?\\s*([0-9]{2}):([0-9]{2}):([0-9]{2})\\s*,?\\s*(-?[0-9]*|-?[0-9]*.[0-9]*|-?[0-9]*.[0-9]*E[0-9]*)\\s*");
				mtc = pat.matcher(measure);
				if (mtc.matches()) 
				{
					String year = mtc.group(1) ;
					String month= mtc.group(2) ;
					String Day = mtc.group(3) ;
					String Hour = mtc.group(4) ;
					String minute = mtc.group(5) ;
					String second = mtc.group(6) ;
					String valuefromfile = mtc.group(7) ;

					long time = spf.parse(year+"-"+month+"-"+Day+" "+Hour+":"+minute+":"+second).getTime();
					float value = (float) (Float.valueOf(valuefromfile));
					plotList.addValue(time, value);

				}
				else {
					spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");	  
					pat = Pattern.compile("([0-9]{4}).([0-9]{2}).([0-9]{2})\\s*,?\\s*([0-9]{2}).([0-9]{2}).([0-9]{2}).([0-9]{3})\\s*,?\\s*(-?[0-9]*|-?[0-9]*.[0-9]*|-?[0-9]*.[0-9]*E[0-9]*)\\s*");
					mtc = pat.matcher(measure);
					if (mtc.matches())
					{
						String year = mtc.group(1) ;
						String month= mtc.group(2) ;
						String Day = mtc.group(3) ;
						String Hour = mtc.group(4) ;
						String minute = mtc.group(5) ;
						String second = mtc.group(6) ;
						String msecond = mtc.group(7) ;
						String valuefromfile = mtc.group(8) ;

						long time = spf.parse(year+"-"+month+"-"+Day+" "+Hour+":"+minute+":"+second+":"+msecond).getTime();
						try {
							float value = (float) (Float.parseFloat(valuefromfile));					
							plotList.addValue(time, value);
						} catch (NumberFormatException e) {
							Logger.getLogger(PlotReader.class ).debug("Skip line (comment or not well formated line"+measure);

						}	
					}
					else	
					{
						//comment or unknow line
						Logger.getLogger(PlotReader.class ).debug("Skip line (comment or not well formated line"+measure);
					}
				}

				measure = in.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plotList; 

	}

	public static PlotList readplt(BufferedReader in) {
		String measure=null;
		int index =0;

		try {
			measure = in.readLine();
			while(measure!=null&&measure.lastIndexOf('#')!=-1)
			{		
				index = measure.lastIndexOf('#');
				measure = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//index+1 due to space
		PlotList plotList=null;
		try {
			plotList = new PlotList();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while( measure != null){
			if(!measure.startsWith("#"))
			{
				String[] values = measure.substring(index).split(",");

				long time = (long) (Float.valueOf(values[0])*60*1000);
				float value = (float) (Float.valueOf(values[1]));
				plotList.addValue(time, value);
			}
			try {
				measure = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return plotList; 
	}
}
