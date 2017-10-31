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
 * File Name   : GraphMarker.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;


import java.awt.Paint;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jfree.chart.plot.XYPlot;

import com.orange.atk.results.logger.log.Action;


/**
 *
 * @author ywil8421
 */
public class GraphMarker {

	private XYPlot xyplot = null;
    private Vector<Action> Vectaction = new Vector<Action>();
    

    private  Paint color=null;
    private  boolean isActivate =false;

    
    public GraphMarker(XYPlot xyplot, Vector<Action> Vectaction, Paint color){

        
        if(Vectaction==null)
        this.Vectaction=new Vector<Action>();
        else
        this.Vectaction = Vectaction;
       
        this.color=color;
        this.xyplot=xyplot;
    }

    
    
    
    /**
	 * Calculate marker legend position depending of the maximum legend
	 */
    
    public void setMarkerPosition() {
        Vector<Action> listActions = Vectaction;
        for (int i = 0; i < listActions.size(); i++) {
            Action action = listActions.get(i);            //remove 
            double XvalueStart = ((double) (action.getStartTime().getTime()));
            double XvalueEnd = ((double) (action.getEndTime().getTime()));
           
            action.setAnnotation(XvalueStart, color);
            action.setMarker(XvalueStart,XvalueEnd, color);

        }
    }
      

    /**
	 * refresh marker (remove,calculate position and draw)
	 */ 
public void refreshMarker()
{
    if(isActivate)
    {
      removeMarker();  
      setMarkerPosition();
      drawMarker();
    }
        
}
   
/**
 * draw marker
 */ 
    public final synchronized  void drawMarker() {
        setMarkerPosition();
        Vector<Action> listActions = Vectaction;
        for (int i = 0; i < listActions.size(); i++) {
            Action action = listActions.get(i);
            if(action.getMarker()!=null)
                xyplot.addDomainMarker(action.getMarker());
            else
                Logger.getLogger(this.getClass() ).debug("Erreur ActionName:"+ action.getActionName()+
                		" Remove MsgType "+action.getMsgType()+" StartTime "+action.getStartTime() +" EndTime "+action.getEndTime());	
            
            if(action.getAnnotation()!=null)
                xyplot.addAnnotation(action.getAnnotation());
            else
                Logger.getLogger(this.getClass() ).debug("Erreur ActionName:"+ action.getActionName()+
                		" Remove MsgType "+action.getMsgType()+" StartTime "+action.getStartTime() +" EndTime "+action.getEndTime());	 
        }
        isActivate=true;

    }

    
    
    public void setActivate(boolean isActivate) {
	this.isActivate = isActivate;
}




	public final synchronized void addEvent(String Msgtype,String ActionName,Date startTime,Date endTime)
    {   
    	
    	
		Action action = new Action();
		action.setMsgType(Msgtype);
		action.setActionName(ActionName);
		action.setStartTime(startTime);
		action.setEndTime(endTime);		
		Vectaction.add(action);		 
 
		double XvalueStart = ((double) (action.getStartTime().getTime()));
        double XvalueEnd = ((double) (action.getEndTime().getTime()));
        action.setAnnotation(XvalueStart, color);
        action.setMarker(XvalueStart,XvalueEnd, color);
        xyplot.addDomainMarker(action.getMarker());
        xyplot.addAnnotation(action.getAnnotation());
        
      
    }
    
    /**
     * remove marker
     */ 
    public void removeMarker() {
        Vector<Action> listActions = Vectaction;
        int size =listActions.size();
        
        for (int i = 0; i <size ; i++) {
            Action action = listActions.get(i);
            if(action.getMarker()!=null)
            xyplot.removeDomainMarker(action.getMarker());
            else
            Logger.getLogger(this.getClass() ).debug("Erreur ActionName:"+ action.getActionName()+
            		" Remove MsgType "+action.getMsgType()+" StartTime "+action.getStartTime() +" EndTime "+action.getEndTime());	
            if(action.getAnnotation()!=null)
            xyplot.removeAnnotation(action.getAnnotation());
            else
                Logger.getLogger(this.getClass() ).debug("Erreur ActionName:"+ action.getActionName()+
                		"Remove MsgType "+action.getMsgType()+" StartTime "+action.getStartTime() +" EndTime "+action.getEndTime());	
        }
                    isActivate=false;

    }


	public Paint getColor() {
		return color;
	}




	public boolean isActivate() {
		return isActivate;
	}

   
    
}
