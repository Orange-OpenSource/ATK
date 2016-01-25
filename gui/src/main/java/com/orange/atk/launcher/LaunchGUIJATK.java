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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.orange.atk.atkUI.coregui.MatosGUI;
import org.apache.log4j.xml.DOMConfigurator;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.AboutDialog;
import com.orange.atk.platform.Platform;
import com.orange.atk.system.WebServer;
import com.orange.atk.util.FileUtil;

public class LaunchGUIJATK {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//init log4j
		DOMConfigurator.configure("log4j.xml");
		//verify win32com.dll
		File win32 =new File(System.getenv("java.home")+Platform.FILE_SEPARATOR+"win32com.dll");
		
		if(!win32.exists()&&System.getenv("java.home")!=null)		
			FileUtil.copyfile(win32, new File("win32com.dll"));
		
		//init configuration atk
		if(!Configuration.loadConfigurationFile("config.properties"))
			return;
	
		
		WebServer.run();
		//launch ATK

		try {
			try {
                if (System.getProperty("os.name").contains("Mac")) {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    System.setProperty(
                            "com.apple.mrj.application.apple.menu.about.name", "ATK");
                    try {
                        Object app = Class.forName("com.apple.eawt.Application").getMethod("getApplication",
                                (Class[]) null).invoke(null, (Object[]) null);

                        Object al = Proxy.newProxyInstance(Class.forName("com.apple.eawt.AboutHandler")
                                .getClassLoader(), new Class[] { Class.forName("com.apple.eawt.AboutHandler") },
                                new AboutListener());
                        app.getClass().getMethod("setAboutHandler", new Class[] {
                                Class.forName("com.apple.eawt.AboutHandler") }).invoke(app, new Object[] { al });
                    } catch (Exception e) {
                        //fail quietly
                    }
                }
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
    private static class AboutListener implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) {
            new AboutDialog(null).setVisible(true);
            return null;
        }
    }

}
