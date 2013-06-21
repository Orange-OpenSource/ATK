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
 * File Name   : LaunchGUIJATK.java
 *
 * Created     : 12/03/2010
 * Author(s)   : France Telecom
 */
package com.orange.atk.launcher;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.xml.DOMConfigurator;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.platform.Platform;
import com.orange.atk.system.WebServer;
import com.orange.atk.util.FileUtil;

public class LaunchGUIJATK {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// verify win32com.dll
		File win32 = new File(System.getenv("java.home") + Platform.FILE_SEPARATOR + "win32com.dll");

		if (!win32.exists() && System.getenv("java.home") != null)
			FileUtil.copyfile(win32, new File("win32com.dll"));

		// init configuration (jatk and log4j)
		if (!Configuration.loadConfigurationFile("config.properties"))
			return;
		DOMConfigurator.configure("log4j.xml");

		WebServer.run();
		// laucnh JATK
		try {
			try {
				// Set System L&F
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (UnsupportedLookAndFeelException e) {
				// handle exception
			} catch (ClassNotFoundException e) {
				// handle exception
			} catch (InstantiationException e) {
				// handle exception
			} catch (IllegalAccessException e) {
				// handle exception
			}
			new CoreGUIPlugin().doStart();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
