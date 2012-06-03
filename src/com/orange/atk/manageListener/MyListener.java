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
 * File Name   : MyListener.java
 *
 * Created     : 03/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.manageListener;


import java.util.Date;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.results.logger.log.ResultLogger;


public class MyListener {
	
	
private	ResultLogger logger;
private IMeasureListener listener=null;	
private	PhoneInterface phone;

	public MyListener(ResultLogger logger){
	this.logger=logger;	
	this.phone = logger.getPhoneInterface();
	}
	
	
public void addMyListeners()	
{	
	
	  listener=(new MeasurmentAdapter() {

        @Override public void LongValueChangee(final long newValue,final String cle) {
            // exécution dans l'EDT
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	if(!logger.isStopATK())
                	{
                		//Logger.getLogger(this.getClass() ).debug(cle+","+newValue);
                		logger.addToDocumentLogger(newValue,cle);
                	}
                }
            });
        }
        
        @Override public void StdOutputChangee(final String Stdoutput) {
            // ex�cution dans l'EDT
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	if(!logger.isStopATK())
                	logger.addInfotoActionLogger("Standard Out/Err",Stdoutput, new Date(),new Date());

                }
            });
        }
        
        
    	
        @Override public void FloatValueChangee(final float newValue,final String cle) {
            // exécution dans l'EDT
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	if(!logger.isStopATK())
                    logger.addToDocumentLogger(newValue,cle);
                }
            });
        }   
    });
    
	  phone.addPerfListener(listener);
    /*
    //Monitor logger Class
    logger.addPerfListener(new MeasurmentAdapter() {
        @Override public void addactionChangee(final String newValue) {
            // exécution dans l'EDT
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // à chaque fois que l'action change, on met � jour le textfield
                //	if(lauchIHM&&isRunning) 
                //	WJATJ.settextAreaJATKExec(""+String.valueOf(newValue));
                }
            });
        }
        
        
        
        @Override public void addOutputChangee(final String newValue) {
            // exécution dans l'EDT
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // à chaque fois que l'output change, on met à jour le textfield
                //	if(lauchIHM&&isRunning) 
                //	WJATJ.settextAreaJATKOutput(""+String.valueOf(newValue));
                }
            });
        } 
    });  
    
    
    //Monitor Loop
    
    IMeasureListener listener=new MeasurmentAdapter() {

        @Override public void addLoopChangee(final String newValue) {
            // exécution dans l'EDT
            SwingUtilities.invokeLater(new Runnable() {
            
                public void run() {

                    // à chaque fois que le loop change, on met à jour le textfield
                   
                	//if(lauchIHM&&isRunning) 
                	//	WJATJ.settextAreaJATKLoop(newValue);
                }
            });
        }
        
    };
    JATKInterpreterInternalState.addPerfListener(listener);
	*/
}


public void removeMylistener()
{
	try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
	}
	phone.removePerfListener(listener);
	Logger.getLogger(this.getClass() ).debug("Remove listener ");
}

}
