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
 * File Name   : guihopperLink.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.IGUICommon;


/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class guihopperLink {

	/** The plugin id, as defined in the plugin.xml file*/
	public static final String PLUGIN_ID = "guihopper";

	private static HopperGUI flashGUI = null;

	/**
	 * Retrieves the instance of FlashGUI plugin
	 * @return the instance of FlashGUI plugin
	 */
	public static HopperGUI getFlashGUI() {
		if (flashGUI == null) {
			for (IGUICommon guiCommon : CoreGUIPlugin.guiCommons) {
				if (guiCommon instanceof HopperGUI) {
					flashGUI = (HopperGUI)guiCommon;
					break;
				}
			}
		}
		return flashGUI;
	}



}
