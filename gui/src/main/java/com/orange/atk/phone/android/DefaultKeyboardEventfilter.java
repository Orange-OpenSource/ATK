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
 * File Name   : DefaultKeyboardEventfilter.java
 *
 * Created     : 26/11/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone.android;

import java.util.Map;

/**
 * Class used to filter output of very common keyboard,
 * 
 * It's found on HTC magic and G1, Samsung SPICA , MOtorola Morisson...
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
class DefaultKeyboardEventfilter extends EventFilter {
	
					

	private final Map<Integer,String> keycodeMap;
	private AndroidPhone driver;
	protected String splitChar = ": ";
	
	public DefaultKeyboardEventfilter(AndroidPhone aphone, Map<Integer,String> codemap) {
		driver = aphone;
		keycodeMap = codemap;
	}

	@Override
	public void processline( String line) {
		String commands[] = line.split(splitChar);
		String command;
		if(commands.length>1)
			command = commands[1];
		else
			return;
		if (command.startsWith("0001")) {
			int keycode = Integer.parseInt(command.substring(5,9),16);
			String key = keycodeMap.get(keycode);

			if (key !=null)
				if(command.substring(10).equals("00000001")) { // KEY PRESSED
					driver.phoneKeyPressed(key); 
				} else if(command.substring(10).equals("00000000")) { // KEY RELEASED
					driver.phoneKeyReleased(key);
				}
		}
	}
	
}