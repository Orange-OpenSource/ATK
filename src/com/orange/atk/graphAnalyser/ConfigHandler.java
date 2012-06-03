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
 * File Name   : ConfigHandler.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class ConfigHandler extends DefaultHandler {
	private CreateGraph createGraph;


	/**
	 * Enable to read log file action.xml file
	 * @param actionsLogger
	 */
	public ConfigHandler(CreateGraph actionsLogger) {
		super();
		createGraph = actionsLogger;
		// On definit le locator par defaut.
	}

	public ConfigHandler() {
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */

	/**
	 * Define locator
	 * 
	 * @param value
	 *            le locator a utiliser.
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator value) {
	}

	/**
	 * Event at start of document parsing
	 * 
	 * @throws SAXException

	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
	}

	/**
	 * Event at the end of document parsing.
	 * 
	 * @throws SAXException
	 *         
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		//Logger.getLogger(this.getClass() ).debug("End of document" );
	}

	/**
	 * 
	 * @param prefixe
	 *           
	 * @param URI
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String URI) throws SAXException {
	}

	/**
	 * 
	 * @param prefixe
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	/**
	 * 
	 * @param nameSpaceURI
	 * @param localName
	 * @param rawName
	 *            <code>nameSpaceURI + ":" + localName</code>
	 * @throws SAXException
	 *      
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {
		if(rawName.equals("graph"))
		{
			Vector<String> tempVect =new Vector<String>();
			int size = attributs.getLength();
			for (int i = 0; i < size; i++) {

				String attribut=attributs.getLocalName(i);
				if (attribut.equals("name")||attribut.equals("color")||attribut.equals("xcomment")||
						attribut.equals("ycomment")||attribut.equals("scale")||attribut.equals("unit")||
						attribut.equals("sampled")||attribut.equals("type")) {
					tempVect.add(attributs.getValue(i));
					//		Logger.getLogger(this.getClass() ).debug("Read "+attribut+":  "+attributs.getValue(i));
				} 
				else {
					attribut=attributs.getQName(i);
					if (attribut.equals("name")||attribut.equals("color")||attribut.equals("xcomment")||
							attribut.equals("ycomment")||attribut.equals("scale")||attribut.equals("unit")||
							attribut.equals("sampled")||attribut.equals("type")) {
						tempVect.add(attributs.getValue(i));
						//	Logger.getLogger(this.getClass() ).debug("Read "+attribut+":  "+attributs.getValue(i));
					} 
					else {
						Logger.getLogger(this.getClass() ).debug("Size: "+size+" index :"+i+"  Invalid  attribut"+attribut+":  " +"   getlocalName:"+attributs.getLocalName(i)+"getQName: "+attributs.getQName(i)+" Value: "+attributs.getValue(i));
					}				
				}
			}       
			createGraph.getTempVectGraph().add(tempVect);
		}
		else if(rawName.equals("marker"))
		{
			Vector<String> tempVect =new Vector<String>();
			int size = attributs.getLength();
			for (int i = 0; i < size; i++) {

				String attribut=attributs.getLocalName(i);
				if (attribut.equals("name")||attribut.equals("color")||attribut.equals("position")) {
					tempVect.add(attributs.getValue(i));
					//	System.out.println("Read "+attribut+":  "+attributs.getValue(i));
				} 
				else {
					attribut=attributs.getQName(i);
					if (attribut.equals("name")||attribut.equals("color")||attribut.equals("position")) {
						tempVect.add(attributs.getValue(i));
					}
					else							
						System.out.println("Size: "+size+" index :"+i+"  Invalid  attribut"+attribut+":  " +"   getlocalName:"+attributs.getLocalName(i));
					//System.err.println("Unssuported attribute : "
					//		+ attributs.getLocalName(i));
					//	throw new SAXException();
				}
			}       
			createGraph.getTempVectMarker().add(tempVect);
		}
	}


	/**
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
		//Logger.getLogger(this.getClass() ).debug();
		//Logger.getLogger(this.getClass() ).debug();
	}

	/**
	 * 
	 * @param ch
	 * @param start
	 * @param end
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int end) throws SAXException {
	}

	/**

	 * 
	 * @param ch
	 * @param start
	 * @param end
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {
	}

	/**
	 *
	 * @param target
	 * @param data
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void processingInstruction(String target, String data) throws SAXException {

	}

	/**

	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {
		Logger.getLogger(this.getClass() ).debug("SkippedEntity");             
	}

}
