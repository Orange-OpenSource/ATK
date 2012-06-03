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
 * File Name   : MatosResolver.java
 *
 * Created     : 05/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.orange.atk.atkUI.corecli.Alert;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class MatosResolver implements EntityResolver {

//	final private String dtdURL;
	final private String dtdDirectory;
//	final private String dtdPath;
	final private String dtdURLprefix;
	
	public MatosResolver(String dtdURL, String libDir) {
//	    this.dtdURL = dtdURL;
	    this.dtdDirectory = libDir;
	    this.dtdURLprefix = dtdURL.substring(0, dtdURL.lastIndexOf('/'));
//	    this.dtdPath = dtdURL.substring(dtdURL.lastIndexOf('/')+1);
	}

	public InputSource resolveEntity (String publicId, String systemId) {
	    //if (systemId.equals(dtdURL)) {
   	    if (systemId.startsWith(dtdURLprefix)) {
	    	try {
	    	    String dtdPath = systemId.substring(systemId.lastIndexOf('/')+1);
	    		File matosDtdFile = new File(dtdDirectory,dtdPath);
	    		Reader matosDtd = new FileReader(matosDtdFile);
	    		return new InputSource(matosDtd);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		Alert.raise(e, "Problem in XMLParser");
	    		return null;
	    	}
	    } else {
	    	return null;
	    }
	}

}
