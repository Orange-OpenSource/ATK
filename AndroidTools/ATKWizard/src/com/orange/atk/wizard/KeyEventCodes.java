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
 * File Name   : KeyEventCodes.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.wizard;

import java.util.Hashtable;

public class KeyEventCodes {
	
	public static final Hashtable<Integer,String> keyEventCodes = new Hashtable<Integer,String>();
	
	public KeyEventCodes() {
		keyEventCodes.put(new Integer(7), "KEYCODE_0");
		keyEventCodes.put(new Integer(8), "KEYCODE_1");
		keyEventCodes.put(new Integer(9), "KEYCODE_2");
		keyEventCodes.put(new Integer(10), "KEYCODE_3");
		keyEventCodes.put(new Integer(11), "KEYCODE_4");
		keyEventCodes.put(new Integer(12), "KEYCODE_5");
		keyEventCodes.put(new Integer(13), "KEYCODE_6");
		keyEventCodes.put(new Integer(14), "KEYCODE_7");
		keyEventCodes.put(new Integer(15), "KEYCODE_8");
		keyEventCodes.put(new Integer(16), "KEYCODE_9");
		keyEventCodes.put(new Integer(29), "A");
		keyEventCodes.put(new Integer(57), "ALT_LEFT");
		keyEventCodes.put(new Integer(58), "ALT_RIGHT");
		keyEventCodes.put(new Integer(75), "APOSTROPHE");
		keyEventCodes.put(new Integer(77), "AT");
		keyEventCodes.put(new Integer(30), "B");
		keyEventCodes.put(new Integer(4), "BACK");
		keyEventCodes.put(new Integer(73), "BACKSLASH");
		keyEventCodes.put(new Integer(31), "C");
		keyEventCodes.put(new Integer(5), "CALL");
		keyEventCodes.put(new Integer(27), "CAMERA");
		keyEventCodes.put(new Integer(28), "CLEAR");
		keyEventCodes.put(new Integer(55), "COMMA");
		keyEventCodes.put(new Integer(32), "D");
		keyEventCodes.put(new Integer(67), "DEL");
		keyEventCodes.put(new Integer(23), "DPAD_CENTER");
		keyEventCodes.put(new Integer(20), "DPAD_DOWN");
		keyEventCodes.put(new Integer(21), "DPAD_LEFT");
		keyEventCodes.put(new Integer(22), "DPAD_RIGHT"); 
		keyEventCodes.put(new Integer(19), "DPAD_UP");
		keyEventCodes.put(new Integer(33), "E");
		keyEventCodes.put(new Integer(6), "ENDCALL");
		keyEventCodes.put(new Integer(66), "ENTER");
		keyEventCodes.put(new Integer(65), "ENVELOPE");
		keyEventCodes.put(new Integer(70), "EQUALS");
		keyEventCodes.put(new Integer(64), "EXPLORER");
		keyEventCodes.put(new Integer(34), "F");
		keyEventCodes.put(new Integer(80), "FOCUS");
		keyEventCodes.put(new Integer(35), "G");
		keyEventCodes.put(new Integer(68), "GRAVE");
		keyEventCodes.put(new Integer(36), "H");
		keyEventCodes.put(new Integer(79), "HEADSETHOOK");
		keyEventCodes.put(new Integer(3), "HOME");
		keyEventCodes.put(new Integer(37), "I");
		keyEventCodes.put(new Integer(38), "J");
		keyEventCodes.put(new Integer(39), "K");
		keyEventCodes.put(new Integer(40), "L");
		keyEventCodes.put(new Integer(71), "LEFT_BRACKET"); 
		keyEventCodes.put(new Integer(41), "M");
		keyEventCodes.put(new Integer(90), "MEDIA_FAST_FORWARD");
		keyEventCodes.put(new Integer(87), "MEDIA_NEXT");
		keyEventCodes.put(new Integer(85), "MEDIA_PLAY_PAUSE");
		keyEventCodes.put(new Integer(88), "MEDIA_PREVIOUS"); 
		keyEventCodes.put(new Integer(89), "MEDIA_REWIND");
		keyEventCodes.put(new Integer(86), "MEDIA_STOP");
		keyEventCodes.put(new Integer(82), "MENU");
		keyEventCodes.put(new Integer(69), "MINUS");
		keyEventCodes.put(new Integer(91), "MUTE"); 
		keyEventCodes.put(new Integer(42), "N");
		keyEventCodes.put(new Integer(83), "NOTIFICATION");
		keyEventCodes.put(new Integer(78), "NUM");
		keyEventCodes.put(new Integer(43), "O");
		keyEventCodes.put(new Integer(44), "P");
		keyEventCodes.put(new Integer(56), "PERIOD"); 
		keyEventCodes.put(new Integer(81), "PLUS");
		keyEventCodes.put(new Integer(18), "POUND");
		keyEventCodes.put(new Integer(26), "POWER");
		keyEventCodes.put(new Integer(45), "Q");
		keyEventCodes.put(new Integer(46), "R");
		keyEventCodes.put(new Integer(72), "RIGHT_BRACKET");
		keyEventCodes.put(new Integer(47), "S");
		keyEventCodes.put(new Integer(84), "SEARCH");
		keyEventCodes.put(new Integer(74), "SEMICOLON");
		keyEventCodes.put(new Integer(59), "SHIFT_LEFT");
		keyEventCodes.put(new Integer(60), "SHIFT_RIGHT");
		keyEventCodes.put(new Integer(76), "SLASH");
		keyEventCodes.put(new Integer(1), "SOFT_LEFT");
		keyEventCodes.put(new Integer(2), "SOFT_RIGHT");
		keyEventCodes.put(new Integer(62), "SPACE");
		keyEventCodes.put(new Integer(17), "STAR");
		keyEventCodes.put(new Integer(63), "SYM");
		keyEventCodes.put(new Integer(48), "T");
		keyEventCodes.put(new Integer(61), "TAB");
		keyEventCodes.put(new Integer(49), "U");
		keyEventCodes.put(new Integer(0), "UNKNOWN");
		keyEventCodes.put(new Integer(50), "V");
		keyEventCodes.put(new Integer(25), "VOLUME_DOWN");
		keyEventCodes.put(new Integer(24), "VOLUME_UP");
		keyEventCodes.put(new Integer(51), "W");
		keyEventCodes.put(new Integer(52), "X");
		keyEventCodes.put(new Integer(53), "Y");
		keyEventCodes.put(new Integer(54), "Z");
	}
}
