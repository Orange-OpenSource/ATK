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
package com.orange.atk.results.logger.log;


import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.orange.atk.graphAnalyser.CreateGraph;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.measurement.PlotList;


class ConfigHandler extends DefaultHandler {
	private CreateGraph _CreateGraph;
	HashMap<String, PlotList> hs =null;
	String folderWhereResultsAreSaved =null;

	/**
	 * Enable to read log file action.xml file
	 * @param actionsLogger
	 */
	public ConfigHandler(HashMap<String, PlotList> hs,String folderWhereResultsAreSaved) {
		super();
		this.hs=hs;
		this.folderWhereResultsAreSaved=folderWhereResultsAreSaved;	
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */

	/**
	 * 
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		//	Logger.getLogger(this.getClass() ).debug("Start to Analyse document");
	}

	/**
	 * 
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
	}

	/**
	 * 
	 * @param prefixe
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
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {
		int j=0;
		if(rawName.equals("graph"))
		{
			Vector<String> tempVect =new Vector<String>();
			int size = attributs.getLength();
			for (int i = 0; i < size; i++) {
				String attribut=attributs.getLocalName(i);
				if (attribut.equals("name")||attribut.equals("unit")
						||attribut.equals("color")||attribut.equals("xcomment")
						||attribut.equals("ycomment")||attribut.equals("scale")
						||attribut.equals("sampled")||attribut.equals("color")||attribut.equals("type")) {
					tempVect.add(attributs.getValue(i));
					//	Logger.getLogger(this.getClass() ).debug("Read"+attribut+":  "+attributs.getValue(i));
				} 
				else {

					attribut=attributs.getQName(i);
					if (attribut.equals("name")||attribut.equals("unit")
							||attribut.equals("color")||attribut.equals("xcomment")
							||attribut.equals("ycomment")||attribut.equals("scale")
							||attribut.equals("sampled")||attribut.equals("color")||attribut.equals("type")) {
						tempVect.add(attributs.getValue(i));
						//Logger.getLogger(this.getClass() ).debug("Read"+attribut+":  "+attributs.getValue(i));
					}
					else
					{
						Logger.getLogger(this.getClass() ).debug("Size: "+size+" index :"+i+" Read Invalid  attribut"+attribut+":  " +"   getlocalName:"+attributs.getLocalName(i));
						Logger.getLogger(this.getClass() ).warn("Unssuported attribute : "
								+ attributs.getLocalName(i));
						throw new SAXException();
					}
				}
			}

			boolean  issampled=false;
			if((tempVect.get(6).toLowerCase()).contains("true"))
				issampled=true;

			if(!hs.containsKey(tempVect.get(0)))
			{
				String ycomment = tempVect.get(3);
				String unit = tempVect.get(4);

				int type = PlotList.TYPE_AVG;
				if (tempVect.size()>=8 && tempVect.get(7).equals("sum")) type = PlotList.TYPE_SUM;
				PlotList pl=new PlotList(getpltpath(tempVect.get(0)),getPNGpath(tempVect.get(0)),
						folderWhereResultsAreSaved,tempVect.get(2),ycomment,
						Integer.parseInt(tempVect.get(5)),issampled,tempVect.get(1),unit, type);
				hs.put(tempVect.get(0),pl);
			}
		}

		else if(rawName.equals("marker"))
		{
			Vector tempVect =new Vector();
			int size = attributs.getLength();
			for (int i = 0; i < size; i++) {

				String attribut=attributs.getLocalName(i);
				if (attribut.equals("name")||attribut.equals("color")||attribut.equals("position")) {
					tempVect.add(attributs.getValue(i));
					//	Logger.getLogger(this.getClass() ).debug("Read "+attribut+":  "+attributs.getValue(i));
				} 
				else {

					attribut=attributs.getQName(i);
					if (attribut.equals("name")||attribut.equals("color")||attribut.equals("position")) {
						tempVect.add(attributs.getValue(i));
						//	Logger.getLogger(this.getClass() ).debug("Read "+attribut+":  "+attributs.getValue(i));
					} 
					else
					{
						Logger.getLogger(this.getClass() ).debug("Size: "+size+" index :"+i+" Read Invalid  attribut"+attribut+":  " +"   getlocalName:"+attributs.getLocalName(i));
						Logger.getLogger(this.getClass() ).warn("Unssuported attribute : "
								+ attributs.getLocalName(i));
						throw new SAXException();
					}
				}
			}       


		}


	}






	/**
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {


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
	 * 
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {
		Logger.getLogger(this.getClass() ).debug("SkippedEntity");             
	}

	/**
	 * return png path
	 * @return png path
	 */
	public String getPNGpath(String cle) {
		return folderWhereResultsAreSaved + Platform.FILE_SEPARATOR +cle+".png";
	}


	//  



	/**
	 * return plt file
	 * 
	 * @return plt path
	 */
	public String getpltpath(String cle) {
		return folderWhereResultsAreSaved + Platform.FILE_SEPARATOR +cle+".csv";
	}

}
