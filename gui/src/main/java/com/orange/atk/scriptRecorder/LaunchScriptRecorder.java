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
 * File Name   : LaunchScriptRecorder.java
 *
 * Created     : 15/07/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder;

import org.apache.log4j.xml.DOMConfigurator;

import com.orange.atk.atkUI.corecli.Configuration;


public class LaunchScriptRecorder {

	
	/** Launch Script Recorder Interface.
	 * @param args . Args[0], if exists, is Test file path to Open
	 */
	public static void main(String args[]){

		//find log4j configfile
		if(!Configuration.loadConfigurationFile("config.properties"))
			return;
		DOMConfigurator.configure("log4j.xml");
		
		ScriptController sc = ScriptController.getScriptController();
		sc.display();
		//we need to open a file
		if (args.length==1) 
			sc.openScript(args[0]);
	}
}
