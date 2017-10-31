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
 * File Name   : IGUICommon.java
 *
 * Created     : 16/02/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * IGUICommon is an extension point to allow other plugins adding 
 * elements to the user interface.
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public interface IGUICommon {

	/**
	 * To get back the main panel from each plugin.
	 * @return
	 */
	public JPanel getMainPanel();

	/**
	 * To get a name to display for a plugin.
	 * Used by GUI to diplay the identification of plugins
	 * @return the name to display for the plugin
	 */
	public String getDisplayName();

	/**
	 * Notify that the component is now on screen 
	 */
	public void notifySelected();
	
	/**
	 * Enable or disable menu or buttons of the concerned plugin.
	 */
	public void updateButtons();

	/**
	 * @param b
	 */
	public void enableUserActions(boolean b);

	/**
	 * Get back a <code>JMenuItem</code> that goes to 'File' menu defined 
	 * by the gui plugin.
	 * NB: One can return a <code>JMenu</code> (that extends <code>JMenuItem</code>) 
	 * in order to have a submenu.
	 * @return a <code>JMenuItem</code>
	 */
	public JMenuItem getFileMenuItem();

	/**
	 * Get back a <code>JMenuItem</code> that goes to 'Tools' menu defined 
	 * by the gui plugin
	 * NB: One can return a <code>JMenu</code> (that extends <code>JMenuItem</code>) 
	 * in order to have a submenu.
	 * @return a <code>JMenuItem</code>
	 */
	public JMenuItem getToolsMenuItem();
	
	/**
	 * Disable all the buttons but the Stop Button 
	 * @param boolean
	 */
	public void disableButtonsButStop();
}
