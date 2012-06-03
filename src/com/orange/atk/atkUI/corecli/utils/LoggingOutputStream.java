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
 * File Name   : LoggingOutputStream.java
 *
 * Created     : 06/11/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * Class used for catch output stream as System.out and System.err
 * and redirect in log4j.
 * @author Fabien Moreau - FMOREAU@gfi.fr
 */
public class LoggingOutputStream extends FilterOutputStream {
    private Logger internallog;
	private Pattern pat;

    /**
     * This class can catch output Stream and redirect in Logger.
     * 
     *  Exemple : <Br/>
     *  Use for redirect system.out
     *  <code>System.setOut(new PrintStream(
	    		  new LoggingOutputStream("System.out.log", new ByteArrayOutputStream())));</code>
     * 
     * @param namelog the name of logger used to manage this stream
     * @param aStream the Output stream to log..
     */
	public LoggingOutputStream(String namelog, OutputStream aStream) {
      super(aStream);
	    if (namelog == null) {
	      throw new IllegalArgumentException("cat == null");
	    }
	    internallog = Logger.getLogger(namelog);

	    //To detect empty lines
	    pat = Pattern.compile("\\s*");
	}
    

	/**
	 * Redefinition of write method in order to log what's happened
	 */
    public void write(byte b[]) throws IOException {
      String aString = new String(b);
 
      Matcher mtc = pat.matcher(aString);
      if(!mtc.matches())
    	  internallog.info(aString);
    }

	/**
	 * Redefinition of write method in order to log what's happened
	 */
    public void write(byte b[], int off, int len) throws IOException {
      String aString = new String(b, off, len);
      Matcher mtc = pat.matcher(aString);
          if(!mtc.matches())
        	  internallog.info(aString);
          
    }
  }
