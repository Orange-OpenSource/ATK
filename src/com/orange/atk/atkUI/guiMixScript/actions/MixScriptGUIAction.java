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
 * File Name   : MixScriptGUIAction.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiMixScript.actions;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.actions.MatosAbstractAction;
import com.orange.atk.atkUI.guiHopper.actions.StopScript;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public enum MixScriptGUIAction {

	ADDFLASHANIMATION ("AddScriptAction", new AddScriptAction("AddScriptAction", new ImageIcon(CoreGUIPlugin.getIconURL("tango/add.png")), "Add a script to the current check-list.")),

	ANALYSEALLFLASH ("LaunchAllScript", new LaunchAllScript("LaunchAllScript", new ImageIcon(CoreGUIPlugin.getIconURL("tango/noatunplay.png")), "Launch all scripts of the check-list.")),

	ANALYSESELECTIONFLASH ("LaunchSelectedScript", new LaunchSelectedScript("LaunchSelectedScript", new ImageIcon(CoreGUIPlugin.getIconURL("tango/play-selection.png")), "Launch selected Script of the check-list.")),

	STOPSCRIPT ("StopScript", new StopScript("StopScript", new ImageIcon(CoreGUIPlugin.getIconURL("tango/noatunstop.png")), "Stop the current task.")),

	SETSCREENSHOTREFERENCEDIR ("SetScreenshotReferenceDir", new SetScreenShotReferenceDir("SetScreenshotReferenceDir", new ImageIcon(CoreGUIPlugin.getIconURL("tango/camera_icon.jpg")),"Set the Reference directory for Screenshot Comparator")),
	
	//CHECCNX ("CheckCnx", new CheckCnxGUIJatkAction("CheckCnx", new ImageIcon(CoreGUIPlugin.getIconURL("tango/reload32.png")), "Check Connexion ")),

	LOOP ("MixScript", new MixScript("MixScript", new ImageIcon(CoreGUIPlugin.getIconURL("tango/cache32.png")), "Mix Script")),

	ABOUT ("about", new AboutGUIMixScriptAction("about", new ImageIcon(CoreGUIPlugin.getIconURL("tando/messagebox_info.png")), "About the application."));

	private String name = null;
	private MatosAbstractAction action = null;

	
	
	private MixScriptGUIAction(String name, MatosAbstractAction action) {
		this.name = name;
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public MatosAbstractAction getAction() {
		return action;
	}

	/**
	 * Enable/Disable this <code>Action</code> action (ie all its related GUI components)
	 * @param enable new status for matosAction
	 */
	public void setEnabled(boolean enable) {
		action.setEnabled(enable);
	}

	/**
	 * Returns a menuItem that performs the given action
	 * @param label
	 * @param matosaction the action to register to the menuItem
	 * @return a new <code>JMenuItem</code> object
	 */
	public JMenuItem getAsMenuItem(String label) {
		return action.getAsMenuItem(label);
	}

	/**
	 * Returns a JButton that performs the given action
	 * @param action action do register to the Button
	 * @return a new <code>JButton</code> object
	 */
	public JButton getAsJButton() {
		return action.getAsJButton();
	}

}
