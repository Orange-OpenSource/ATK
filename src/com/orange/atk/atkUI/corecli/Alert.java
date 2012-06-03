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
 * File Name   : Alert.java
 *
 * Created     : 05/06/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli;

import java.io.PrintStream;

public class Alert extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private static PrintStream chan1=null;
    private static PrintStream chan2=null;
    private static boolean dumpMessage1=true;
    private static boolean dumpMessage2=true;
    private static boolean dumpExceptionTrace1=true;
    private static boolean dumpExceptionTrace2=true;

    public Alert(String msg) {
    	super(msg);
    }

    private static void setOutput(int chanNum, PrintStream stream, boolean dumpMessage, boolean dumpExceptionTrace) {
    	if (chanNum==1) {
    		chan1 = stream;
    		dumpMessage1 = dumpMessage;
    		dumpExceptionTrace1 = dumpExceptionTrace;
    	} else if (chanNum==2) {
    		chan2 = stream;
    		dumpMessage2 = dumpMessage;
    		dumpExceptionTrace2 = dumpExceptionTrace;
    	}
    }

    public static  void setOutput1(PrintStream stream) {
    	setOutput(1, stream, true, true);
    }

    public static  void setOutput2(PrintStream stream) {
    	setOutput(2, stream, true, true);
    }

    public static  void setOutput1(PrintStream stream, boolean dumpMessage, boolean dumpExceptionTrace) {
    	setOutput(1, stream, dumpMessage, dumpExceptionTrace);
    }

    public static  void setOutput2(PrintStream stream, boolean dumpMessage, boolean dumpExceptionTrace) {
    	setOutput(2, stream, dumpMessage, dumpExceptionTrace);
    }

    public static void raise(Exception e) {
    	raise(e,"");
    }

    public static void raise(Exception e, String msg) {
    	if (chan1!=null) {
    		if (dumpMessage1) {
    			chan1.println(msg);
    		}
    		if ((e!=null)&&(dumpExceptionTrace1)) {
    			e.printStackTrace(chan1);
    		}
    	}
    	if (chan2!=null) {
    		if (dumpMessage2) chan2.println(msg);
    		if ((e!=null)&&(dumpExceptionTrace2)) e.printStackTrace(chan2);
    	}
    	throw new Alert(msg);
    }
}
