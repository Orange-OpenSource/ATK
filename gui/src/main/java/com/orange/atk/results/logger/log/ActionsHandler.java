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
 * File Name   : ActionsHandler.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.results.logger.log;



import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * This class read XML file to get action
 * 
 * @author  France Telecom R&D
 */


class ActionsHandler extends DefaultHandler {
	private ActionsLogger _actionsLogger;


	/**
	 * 
	 * @param actionsLogger
	 */
	public ActionsHandler(ActionsLogger actionsLogger) {
              super();

              _actionsLogger = actionsLogger;

	}

        public ActionsHandler() {

	}
        
        
        
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
        

         
        
      
        /**
         * @throws SAXException 
         * @see org.xml.sax.ContentHandler#startDocument()
         */
        public void startDocument() throws SAXException {
        }

        /**
         * @throws SAXException 
         * @see org.xml.sax.ContentHandler#endDocument()
         */
        public void endDocument() throws SAXException {
            //    Logger.getLogger(this.getClass() ).debug("End of Document Analysis" );
        }

        /**
         * @param prefixe 
         * @param URI 
         * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
         */
        public void startPrefixMapping(String prefix, String URI) throws SAXException {
        }

        /**
         * @param prefixe
         * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
         */
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        /**
         * @param nameSpaceURI 
         * @param localName 
         * @param rawName
         * @throws SAXException 
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {
		if(rawName.equals("AllAction"))
                {
			//Logger.getLogger(this.getClass() ).debug(" FirstBalise");
			return;
                }
                        
                            
                            
		Action action = new Action();
		int size = attributs.getLength();
		SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
		for (int i = 0; i < size; i++) {
                    
                    String attribut=attributs.getLocalName(i);
                    if (attribut.equals("MsgType")||attribut.equals("Msg")||attribut.equals("starttime")||
   							attribut.equals("endtime")) {
   					//	Logger.getLogger(this.getClass() ).debug("Read getLocalName"+attribut+":  "+attributs.getValue(i));
   					} 
                    
                    else {
   					 attribut=attributs.getQName(i);
					//	Logger.getLogger(this.getClass() ).debug("Read getQName "+attribut+":  "+attributs.getQName(i));
		
                    }
   					
   					
   					
			if (attribut.equals("MsgType")) {
				action.setMsgType(attributs.getValue(i));
                     //           Logger.getLogger(this.getClass() ).debug("Read MsgType:  "+attributs.getValue(i));
			} 
                        
                        
                        else if (attribut.equals("Msg")) {
					action.setActionName((attributs.getValue(i)));
				//Logger.getLogger(this.getClass() ).debug("Read Msg:  "+attributs.getValue(i));

                        
                        
                        }else if (attribut.equals("starttime")) {
				try {
					action.setStartTime(spf.parse(attributs.getValue(i)));
				//	Logger.getLogger(this.getClass() ).debug("Read starttime:  "+attributs.getValue(i));

                                } catch (ParseException e) {
					Logger.getLogger(this.getClass() ).warn("Invalid attribute  : "
							+ attributs.getValue(i));
					throw new SAXException();
				}
			} else if (attribut.equals("endtime")) {
				try {
					action.setEndTime(spf.parse(attributs.getValue(i)));
				//	Logger.getLogger(this.getClass() ).debug("Read endtime:  "+attributs.getValue(i));

                                } catch (ParseException e) {
                                   Logger.getLogger(this.getClass() ).debug(" Invalid time"+attributs.getValue(i));

					Logger.getLogger(this.getClass() ).warn("Invalid attribute : "
							+ attributs.getValue(i));
					throw new SAXException();
				}
			} else {
                                                    Logger.getLogger(this.getClass() ).debug("Size: "+size+" index :"+i+"  Invalid  attribut"+attribut+":  " +"   getlocalName:"+attributs.getLocalName(i));
				//Logger.getLogger(this.getClass() ).warn("Unssuported attribute : "
				//		+ attributs.getLocalName(i));
				//throw new SAXException();
			}
		}
		_actionsLogger.addAction(action);
        }

        /**
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
          

        }

        /**
         * @param ch 
         * @param start 
         * @param end 
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] ch, int start, int end) throws SAXException {
        }

        /**
         * @param ch 
         * @param start 
         * @param end 
         * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
         */
        public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {
        }

        /**
         * @param target 
         * @param data 
         * d'une serie de paires nom/valeur.
         * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
         */
        public void processingInstruction(String target, String data) throws SAXException {
        }

        /**
         * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
         */
        public void skippedEntity(String arg0) throws SAXException {
       //    Logger.getLogger(this.getClass() ).debug("SkippedEntity");             
        }
       
}
