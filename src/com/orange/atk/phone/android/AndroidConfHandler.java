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
 * File Name   : AndroidConfHandler.java
 *
 * Created     : 05/03/2010
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone.android;


import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.orange.atk.util.Position;

/**
 * @author Moreau Fabien 
 *
 */
class AndroidConfHandler extends DefaultHandler {

	private enum ContextEnum {SOFTKEY_MAPPING, KEY_MAPPING, CANAL_PATTERN ,TOUCHSCREEN_PATTERN,NO_CONTEXT};
	
	private ContextEnum ctx=ContextEnum.NO_CONTEXT;
	private String ctxCanal; // default keyboard canal
	private HashMap<String, Integer> keymap;
	private HashMap<String, Position> softkeymap;
	private HashMap<String, String> keycanal;
	private String keyboardchannel=null;
	private String keyboardSecondchannel=null;
	private String keyboardThirdchannel=null;
	private String touchscreenchannel=null;
	private String ypattern;
	private String xpattern;
	private String xypattern;
	private String downpattern;
	private String downmaxpattern;
	private String uppattern;
	private String flushpattern;
	private String flush2pattern = null;
	private int moveThreshold = 15; // default value
	private int longPressTime = 1000; // default value
	private boolean longPressMultiple = false; // default value
	private boolean useMonkeyForPress = false; // default value
	private boolean useSmartTouchDetection = false; // default value
	private boolean sendFlushTwiceForMouseUp = false; // default value
	private float ratioX=1.0f;
	private float ratioY=1.0f;
	private boolean sendMouseEventFirst = false; // default value
	private boolean sendMouseDownForMove = false; // default value
	private boolean sendSeparateFlush = false; // default value
	private boolean dontUseMonkey = false;
	
	public AndroidConfHandler() {
		softkeymap = new HashMap<String, Position>();
		keymap = new HashMap<String, Integer>();
		keycanal = new HashMap<String, String>();
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

		//fdetermine context
		if (rawName.equals("KeyMapping") ){
			ctx = ContextEnum.KEY_MAPPING;
			String canal = attributs.getValue("canal");
			if (canal!=null) ctxCanal=canal;
			else ctxCanal="keyboard"; // default keyboard canal
		} else if(rawName.equals("SoftKeyMapping") ){
			ctx = ContextEnum.SOFTKEY_MAPPING;

		} else if (rawName.equals("CanalPattern") ){
			ctx = ContextEnum.CANAL_PATTERN;

		} else if (rawName.equals("Touchscreen") ){
			ctx = ContextEnum.TOUCHSCREEN_PATTERN;
		}
		
		
		//analyse
		switch(ctx) {

		case KEY_MAPPING:
			if (rawName.equals("Key")) {
				 keymap.put(attributs.getValue("name"),
							Integer.parseInt(attributs.getValue("code")) ) ;
				keycanal.put(attributs.getValue("name"),ctxCanal);
			}
			break;
			
		case SOFTKEY_MAPPING:
			if (rawName.equals("Key")) {
				softkeymap.put(attributs.getValue("name"),
						new Position(Integer.parseInt(attributs.getValue("avgX")), Integer.parseInt(attributs.getValue("avgY")), 0) ) ;
			}
			break;
		
		case CANAL_PATTERN:
			if (rawName.equals("Pattern")) {
				if("keyboard".equals( attributs.getValue("canal") ) ) {
					keyboardchannel =attributs.getValue("value");
				}
				if("keyboard2".equals( attributs.getValue("canal") ) ) {
					keyboardSecondchannel=attributs.getValue("value");
				}
				if("keyboard3".equals( attributs.getValue("canal") ) ) {
					keyboardThirdchannel=attributs.getValue("value");
				}
				
				if("touchscreen".equals( attributs.getValue("canal")) ) {
					touchscreenchannel = attributs.getValue("value");
				}
			}
			break;
			
		case TOUCHSCREEN_PATTERN:
			if (rawName.equals("Pattern")) {
				if( "Y".equals( attributs.getValue("name") ) ) {
					ypattern =attributs.getValue("value");
					
				}else if("X".equals( attributs.getValue("name"))) {
					xpattern =attributs.getValue("value");
					
				}else if("XY".equals( attributs.getValue("name"))) {
					xypattern =attributs.getValue("value");
					
				}else if( "down".equals( attributs.getValue("name")) ) {
					downpattern  =attributs.getValue("value");
					
				}else if( "downmax".equals( attributs.getValue("name")) ) {
					downmaxpattern  =attributs.getValue("value");
					
				}else if( "up".equals( attributs.getValue("name")) ) {
					uppattern =attributs.getValue("value");
					
				}else if( "flush".equals( attributs.getValue("name")) ) {
					flushpattern =attributs.getValue("value");
					
				}else if( "flush2".equals( attributs.getValue("name")) ) {
					flush2pattern =attributs.getValue("value");
					
				}else if( "ratio".equals( attributs.getValue("name")) ) {
					ratioX = Float.parseFloat(attributs.getValue("value") );
					ratioY = ratioX;
				}else if( "ratioX".equals( attributs.getValue("name")) ) {
					ratioX = Float.parseFloat( attributs.getValue("value") );
				}else if( "ratioY".equals( attributs.getValue("name")) ) {
					ratioY = Float.parseFloat( attributs.getValue("value") );
				}
			} else if (rawName.equals("Threshold")) {
				if( "move".equals( attributs.getValue("name") ) ) {
					moveThreshold = Integer.parseInt(attributs.getValue("value"));
					
				}else if( "longPressTime".equals( attributs.getValue("name")) ) {
					longPressTime = Integer.parseInt( attributs.getValue("value") );
				}
			} else if (rawName.equals("Option")) {
				if( "sendMouseEventFirst".equals( attributs.getValue("name") ) ) {
					sendMouseEventFirst = Boolean.parseBoolean(attributs.getValue("value"));
					
				} else if( "sendMouseDownForMove".equals( attributs.getValue("name") ) ) {
					sendMouseDownForMove = Boolean.parseBoolean(attributs.getValue("value"));
					
				} else if( "sendSeparateFlush".equals( attributs.getValue("name") ) ) {
					sendSeparateFlush = Boolean.parseBoolean(attributs.getValue("value"));
					
				} else if( "longPressMultiple".equals( attributs.getValue("name")) ) {
					if(attributs.getValue("value").equals("true"))
						longPressMultiple = true;
				} else if( "useMonkeyForPress".equals( attributs.getValue("name")) ) {
					if(attributs.getValue("value").equals("true"))
						useMonkeyForPress = true;
				} else if( "dontUseMonkey".equals( attributs.getValue("name")) ) {
					if(attributs.getValue("value").equals("true"))
						dontUseMonkey = true;
				} else if( "useSmartTouchDetection".equals( attributs.getValue("name")) ) {
					if(attributs.getValue("value").equals("true"))
						useSmartTouchDetection = true;
				} else if( "sendFlushTwiceForMouseUp".equals( attributs.getValue("name")) ) {
					if(attributs.getValue("value").equals("true"))
						sendFlushTwiceForMouseUp = true;
				}

			}
			break;
		
		default:;;
		}

	}


	public HashMap<String, Position> getsoftkeymap() {
		return softkeymap;
	}
	public HashMap<String, Integer> getkeymap() {
		return keymap;
	}
	public HashMap<String, String> getkeycanal() {
		return keycanal;
	}


	public String getKeyboardchannel() {
		return keyboardchannel;
	}

	public String getTouchscreenchannel() {
		return touchscreenchannel;
	}

	public String getDownpattern() {
		return downpattern;
	}

	public String getDownMaxpattern() {
		return downmaxpattern;
	}

	public String getYpattern() {
		return ypattern;
	}

	public String getXpattern() {
		return xpattern;
	}

	public String getXYpattern() {
		return xypattern;
	}
	
	public String getUppattern() {
		return uppattern;
	}

	public String getFlushpattern() {
		return flushpattern;
	}
	
	public String getFlush2pattern() {
		return flush2pattern;
	}

	public String getKeyboardSecondchannel() {
		return keyboardSecondchannel;
	}	
	
	public String getkeyboardThirdchannel() {
		return keyboardThirdchannel;
	}

	public float getRatioX() {
		return ratioX;
	}
	
	public float getRatioY() {
		return ratioY;
	}

	public int getMoveThreshold() {
		return moveThreshold;
	}
	public int getLongPressTime() {
		return longPressTime;
	}
	public boolean getLongPressMultiple() {
		return longPressMultiple;
	}
	public boolean useMonkeyForPress() {
		return useMonkeyForPress;
	}
	public boolean useSmartTouchDetection() {
		return useSmartTouchDetection;
	}
	public boolean sendFlushTwiceForMouseUp() {
		return sendFlushTwiceForMouseUp;
	}
	public boolean dontUseMonkey() {
		return dontUseMonkey;
	}
	public boolean sendMouseEventFirst() {
		return sendMouseEventFirst;
	}
	public boolean sendMouseDownForMove() {
		return sendMouseDownForMove;
	}
	public boolean sendSeparateFlush() {
		return sendSeparateFlush;
	}

	
	
	
}
