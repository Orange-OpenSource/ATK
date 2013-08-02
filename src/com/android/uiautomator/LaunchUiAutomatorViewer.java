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
