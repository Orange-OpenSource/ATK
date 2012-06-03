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
 * File Name   : ExternalToolXMLParser.java
 *
 * Created     : 05/06/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.orange.atk.atkUI.corecli.utils.Out;

/**
 * Parser of file which contains the list of tools.
 * @author apenault
 *
 */
public class ExternalToolXMLParser {

	private Document doc;
	private Element root;
	
	/** Constructor */
	public ExternalToolXMLParser() {
	}
	
	/**
	 * Read an XML file and intialize the doc and the root attributes.
	 * @param f the XML file
	 * @throws Alert to spread exceptions
	 */
	public void readEmulatorFile(File f) throws Alert{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder docbuild = factory.newDocumentBuilder();
			EmulatorResolver emulResolver = new EmulatorResolver();
			docbuild.setEntityResolver(emulResolver);
			docbuild.setErrorHandler(emulResolver);
			doc = docbuild.parse(f);
			root = doc.getDocumentElement();
		} catch (SAXParseException e) {
			Alert.raise(e,"Error at line" +  e.getLineNumber() + " - col " +
							e.getColumnNumber() + " - Entity " 
							+ e.getPublicId() + " in file "+ f.getAbsolutePath() + ".");
		} catch (Exception e) {
			Alert.raise(e, e.getMessage());
		}
	}
	
	/**
	 * Save the list of emulators in the "emulator.xml" file.
	 * @throws Alert 
	 * @throws TransformerFactoryConfigurationError 
	 * @throws Exception 
	 */
	public void writeInFile(List<ExternalTool> emulators, String fileName) throws Exception {
		Document doc = createDocument(emulators);
		File emulatorsFile = new File(fileName);
		Source source = new DOMSource(doc);
	    
        // Prepare the output file
        Result result = new StreamResult(emulatorsFile);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "emulator.dtd");
        xformer.transform(source, result);
	}
	
	/**
	 * Create the XML document
	 * @param emulators the list of emulators
	 * @return the created DOM document
	 * @throws Alert 
	 */
	private Document createDocument(List<ExternalTool> emulators) throws Alert {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Alert.raise(e);
		}
		if(builder==null)
			{Logger.getLogger(this.getClass() ).warn("builder variable is still null");
			return null;
			}
		Document doc = builder.newDocument();
		Element root = doc.createElement("emulators");
		doc.appendChild(root);
		Iterator<ExternalTool> it = emulators.iterator();
		while(it.hasNext()){
			ExternalTool emulator = it.next();
			Element emulatorElem = doc.createElement("emulator");
			emulatorElem.setAttribute("default", Boolean.toString(emulator.isDefaultEmulator()));
			Element emulatorNameElem = doc.createElement("name");
			Text nameText = doc.createTextNode(emulator.getName());
			emulatorNameElem.appendChild(nameText);
			Element emulatorCmdLineElem = doc.createElement("cmdLine");
			Text cmdLineText = doc.createTextNode(emulator.getCmdLine());
			emulatorCmdLineElem.appendChild(cmdLineText);
			emulatorElem.appendChild(emulatorNameElem);
			emulatorElem.appendChild(emulatorCmdLineElem);
			root.appendChild(emulatorElem);
//Out.main.println("writing tool def: "+nameText+"("+emulator.isDefaultEmulator()+") - "+cmdLineText);			
		}
		return doc;
	}
	
	/**
	 * Retrieve the list of emulators the parsed file.
	 * @throws Alert
	 */
	public List<ExternalTool> initEmulators(){
		ArrayList<ExternalTool> emulators = new ArrayList<ExternalTool>();
		Element [] emulatorsElem = getElements(root, "emulator");
		for (int i=0; i<emulatorsElem.length; i++){
			ExternalTool emulator = new ExternalTool();
			Element emulatorElem = emulatorsElem[i];
			emulator.setDefaultExternalTool(Boolean.valueOf(emulatorElem.getAttribute("default")));
			Element nameElem = getElement(emulatorElem, "name");
			emulator.setName(nameElem.getFirstChild().getNodeValue());
			Element cmdLineElem = getElement(emulatorElem, "cmdLine");
			emulator.setCmdLine(cmdLineElem.getFirstChild().getNodeValue());
//Out.main.println("emulator added: "+emulator.getName()+" default"+emulator.isDefaultEmulator()+" cmdLine: "+emulator.getCmdLine());
			emulators.add(emulator);
		}
		return emulators;
	}
	
	/** 
	 * Extracts the set of XML elements having a given name in a given XML element
	 * @param e the element to explore
	 * @param name the name of the elements searched
	 * @return an array of elements
	 */
	public Element [] getElements(Element e, String name) {
		NodeList listNodes = e.getElementsByTagName(name);
		int l = listNodes.getLength();
		Element r [] = new Element [l];
		for(int i=0; i < l; i++) r[i] = (Element) listNodes.item(i);
		return r;
	}
	
	/** 
	 * Extracts a given XML element having a given name son of a given
	 * XML element. There should be only one such element:
	 * @param e the element to explore
	 * @param name the name of the elements searched
	 * @return an array of elements
	 */
	public Element getElement(Element e, String name) {
		NodeList listNodes = e.getElementsByTagName(name);
		if (listNodes.getLength() == 1) return (Element) listNodes.item(0);
		else return null;
	}
	
	private static class EmulatorResolver implements EntityResolver, ErrorHandler {
		
		// load the DTD for campaign validation. Expected to be
		// found in the LIB directory (defined at launch time of Matos). 
		public InputSource resolveEntity (String publicId, String systemId) {	
			if (systemId.endsWith("campaign.dtd")) {
				//String libDir = Configuration.getConfiguration().libDir;
				String libDir = Configuration.getProperty("corecliConfigPath");
				try {
					File emulatorDtdFile = new File(libDir,"emulator.dtd");
					if (!emulatorDtdFile.exists()) {
						// Can't make the method throw Alert, since it is an
						// overridden method. Writing to Out channels directly.
						String msg = "Can't find DTD file to validate campaign: "+emulatorDtdFile.getAbsolutePath();
						Out.main.println(msg);
						Out.log.println(msg);
						// Tester si mode GUI (introduire var globale qui le
						// dit); si oui forcer luverture
						// d'un dialogue au lieu de faire un exit violent.
						System.exit(1);
					}
					Reader emulatorDtd = new FileReader(emulatorDtdFile);
					return new InputSource(emulatorDtd);
				} catch (Exception e) {
					// Overriden method, can't make it throw Alert...
					Out.main.println(e.getMessage());
					Out.log.println(e.getMessage());
					e.printStackTrace(Out.main);
					e.printStackTrace(Out.log);
					return null;
				}
			} 
			return null;
		}
		
		public void error(SAXParseException exception) throws SAXException {
			Out.log.println(exception.getMessage());
			exception.printStackTrace(Out.log);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			Out.main.println(e.getMessage());
			Out.log.println(e.getMessage());
			e.printStackTrace(Out.main);
			e.printStackTrace(Out.log);
		}

		public void warning(SAXParseException exception) throws SAXException {
			Out.log.println(exception.getMessage());
			exception.printStackTrace(Out.log);
		}
	}

}
