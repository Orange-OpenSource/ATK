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
 * File Name   : ReportGenerator.java
 *
 * Created     : 24/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.Step.Verdict;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Do;
import com.orange.atk.atkUI.corecli.utils.XMLParser;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class ReportGenerator {

	protected XMLParser profileParser;
	protected File resultsFile;
	protected File outputFile;
	protected Verdict verdict;
	/**
	 * To parse once and for all the XML file to map XML elements to java classes
	 */
	protected Unmarshaller unmar;

	public abstract Verdict generateResult() throws Exception;

	public Verdict getVerdict() {
		return verdict;
	}

	/**
	 * Create a temporary file which contains only the report part
	 * of the security profile.
	 * @throws DocumentException
	 */
	protected Document getReportPartOfProfile(){
        Document document = profileParser.getDoc();
        Element doElem = (Element)document.getRootElement().selectSingleNode("//do");
        Document documentReport = DocumentHelper.createDocument();
        documentReport.add(doElem.createCopy());
		return documentReport;
	}

	/**
	 * removes the mixed content of xml stream
	 * to map xml elements in java objects
	 * @param reader
	 */
	protected org.w3c.dom.Document removeMixedContent(Document doc){
		String corecliConfigPath = Configuration.getProperty("getConfigDirectory");
		TransformerFactory factory = TransformerFactory.newInstance();
		DocumentSource source = new DocumentSource(doc);
        Transformer x = null;
        try{
        	x = factory.newTransformer(new StreamSource(new File(corecliConfigPath + "mixedTransformation.xsl")));
        }catch(TransformerConfigurationException tce){
        	tce.printStackTrace();
        }
    	if(x==null)
		{Logger.getLogger(this.getClass() ).warn("x variable is still null");
		return null;
		}
        x.setOutputProperty(OutputKeys.INDENT, "yes");
        DocumentBuilderFactory factoryDoc = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
		try {
			builder = factoryDoc.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		if(builder==null)
		{Logger.getLogger(this.getClass() ).warn("builder variable is still null");
		return null;
		}
        org.w3c.dom.Document docResult = builder.newDocument();
        DOMResult streamResult = new DOMResult(docResult);
        try {
			x.transform(source, streamResult);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
        return docResult;
	}

	protected Do getDo() throws Exception{
		String corecliConfigPath = Configuration.getProperty("corecliConfigPath");
		Document tempDoc = getReportPartOfProfile();
		org.w3c.dom.Document tempDocResult = removeMixedContent(tempDoc);

		if (unmar == null) {
			Mapping mapping = new Mapping();
			// 1. Load the mapping information from the file
			mapping.loadMapping(corecliConfigPath + "mapping.xml");

			// 2. Unmarshal the data
			unmar = new Unmarshaller(mapping);
			unmar.setValidation(false);
		}
		Do doo = (Do)unmar.unmarshal(tempDocResult);
		return doo;
	}

}
