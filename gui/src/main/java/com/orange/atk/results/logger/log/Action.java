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
 * File Name   : Action.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.results.logger.log;

import java.awt.Font;
import java.awt.Paint;
import java.util.Date;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.ui.TextAnchor;

/**
 * This class stores an action sent to the phone
 */

public class Action {
	private String MsgType =null;
	private String szActionName = null;
	private Date startTime = null;
	private Date endTime = null;
    private IntervalMarker NewMarker =null; 
    private XYTextAnnotation annotation =null;
    
    
    
    /**
 	 * Set Annotation properties  used in Analysis Tool (keypress,log,screenshot...) 
 	 * For each action a marker and an Annotation is related
 	 * @param Xvalue StartTime of Marker Action
 	 * @param Yvalue Position where to display the comment 1 for the top 0 for the bottom
 	 * @param color Color of Marker
 	 */
    
    public void setAnnotation(double Xvalue, Paint color) {
    if(szActionName!=null)
    {
    annotation = new XYTextAnnotation(szActionName, Xvalue, 0.05);
    annotation.setFont(new Font("SansSerif", Font.PLAIN, 12));
    annotation.setRotationAngle(3 * Math.PI / 2);
    annotation.setRotationAnchor(TextAnchor.BOTTOM_LEFT);
    annotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
    annotation.setToolTipText(szActionName);
    annotation.setPaint(color);
    }
    }
    
    
     public XYTextAnnotation getAnnotation() {
    return annotation;
    }
     
     
     
     /**
 	 * Set Marker properties  used in Analysis Tool (keypress,log,screenshot...) 
 	 * For each action a marker and an Annotation is related
 	 * @param Xvalue StartTime of Marker Action
 	 * @param Xvalue2 EndTime of Marker Action
 	 * @param color Color of Marker
 	 */
      
     public void setMarker(double Xvalue,double Xvalue2,Paint color) {
         NewMarker = new IntervalMarker(Xvalue,Xvalue2);
         NewMarker.setPaint(color);

      }
     
     

 	/**
 	 * Get the get action marker
 	 * 
 	 * @return marker
 	 */
     
    public IntervalMarker getMarker() {
    return NewMarker;
    }
    
    
	/**
	 * Get the MsgType, should not be null
	 * 
	 * @return the actions name
	 */
	public String getMsgType() {
		return MsgType;
	}

	
	/**
	 * Get the actions name, should not be null
	 * 
	 * @return the actions name
	 */
	public String getActionName() {
		return szActionName;
	}
	
	/**
	 * Set the MsgType. Possibles values are names of the executed actions
	 * (like Exeption, keyPress, ...)
	 * 
	 * @param MsgType
	 *            Type de message
	 * @throws NullPointerException
	 *             if szActionName is Null
	 * @throws IllegalArgumentException
	 *             when szActionName is not valid (empty string, ...)
	 */
	public void setMsgType(String MsgType) {
		if(MsgType == null)
			throw new NullPointerException();
		if(MsgType.equals(""))
			throw new IllegalArgumentException();
		this.MsgType = MsgType;
	}
	
	
	
	
	
	/**
	 * Set the actionName. Possibles values are names of the executed actions
	 * (like key, keyDown, ...)
	 * 
	 * @param szActionName
	 *            name of an action
	 * @throws NullPointerException
	 *             if szActionName is Null
	 * @throws IllegalArgumentException
	 *             when szActionName is not valid (empty string, ...)
	 */
	public void setActionName(String szActionName) {
		if(szActionName == null)
			throw new NullPointerException();
		if(szActionName.equals(""))
			this.szActionName = "Empty String From Phone";
		this.szActionName = szActionName;
	}

	/**
	 * Get the time when an action starts
	 * 
	 * @return the start time of an action
	 */
	public Date getStartTime() {
		return (Date) startTime.clone();
	}

	/**
	 * Set the time when an action start
	 * 
	 * @param startTime
	 *            the start time of an action
	 * @throws NullPointerException
	 *             if startTime is Null
	 * @throws IllegalArgumentException
	 *             when startTime is not valid (empty string, ...)
	 */
	public void setStartTime(Date startTime) {
		this.startTime = (Date) startTime.clone();
	}

	/**
	 * Get the time when an action ends
	 * 
	 * @return the end time of an action
	 */
	public Date getEndTime() {
		return (Date) endTime.clone();
	}

	/**
	 * set the time when an action ends
	 * 
	 * @param endTime
	 * @throws NullPointerException
	 *             if endTime is Null
	 * @throws IllegalArgumentException
	 *             when endTime is not valid (empty string, ...)
	 */
	public void setEndTime(Date endTime) {
		this.endTime = (Date)endTime.clone();
	}

}
