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
 * File Name   : CoreGUIPlugin.java
 *
 * Created     : 16/02/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.Out;
import com.orange.atk.atkUI.guiHopper.HopperGUI;
import com.orange.atk.atkUI.guiMixScript.MixScriptGUI;
import com.orange.atk.atkUI.guiScript.JatkGUI;

/**
 * This class is the main class of the "coregui" plugin.
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class CoreGUIPlugin {

	
	public static ArrayList<IGUICommon> guiCommons = new ArrayList<IGUICommon>();

	public static MatosGUI mainFrame;


	public void doStart() throws Exception {
		Out.log.println("Starting CoreGUIPlugin");



		guiCommons.add(new JatkGUI() );
		guiCommons.add(new HopperGUI() );
		guiCommons.add(new MixScriptGUI());
	
		
		mainFrame = new MatosGUI();
	}


	/**
	 * Gets back application icon.
	 * @return URL of the application's icon
	 */
	public static URL getMainIcon() {
		return getIconURL("icon.png");
	}

	/**
	 * Gets back the URL of the specified icon.
	 * @param iconFileName icon file name
	 * @return icon's URL or null if file not in ressource directory
	 */
	public static URL getIconURL(String iconFileName) {
		File configFile = new File(Configuration.getProperty("iconDir")+iconFileName);
		URL iconURL = null;
		try {
			iconURL = configFile.toURI().toURL();
		} catch (MalformedURLException e) {
			Alert.raise(e, "Unable to get icon '"+iconFileName+"'");
		}
		return iconURL;
	}

}
