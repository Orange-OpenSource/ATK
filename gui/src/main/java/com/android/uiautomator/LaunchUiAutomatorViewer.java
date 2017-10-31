/*
 * Software Name : ATK - UIautomatorViewer Robotium Version
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
 * File Name   : LaunchUiAutomatorViewer.java
 *
 * Created     : 05/06/2013
 * Author(s)   : D'ALMEIDA Joana
 */
package com.android.uiautomator;

import org.apache.log4j.xml.DOMConfigurator;

import com.orange.atk.atkUI.corecli.Configuration;

public class LaunchUiAutomatorViewer {

	public static void main(String[] args) {

		//find log4j configfile
		if(!Configuration.loadConfigurationFile("config.properties"))
			return;
		DOMConfigurator.configure("log4j.xml");
		DebugBridge.init();
		UiAutomatorViewer window = new UiAutomatorViewer();
		window.setVisible(true);
	}

}
