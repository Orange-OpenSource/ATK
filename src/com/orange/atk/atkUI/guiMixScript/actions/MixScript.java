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
 * File Name   : MixScript.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiMixScript.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.actions.MatosAbstractAction;
import com.orange.atk.atkUI.guiMixScript.GuiMixScriptLink;
import com.orange.atk.atkUI.guiMixScript.MixScriptGUI;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class MixScript extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;
	
	MatosGUI matosGui=null;

	/**
	 * @param name
	 * @param icon
	 * @param shortDescription
	 */
	public MixScript(String name, Icon icon, String shortDescription) {		
		super(name, icon, shortDescription);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		MixScriptGUI flashGUI = GuiMixScriptLink.getFlashGUI();
		CheckListTable checkListTable = flashGUI.getCheckListTable();
		checkListTable.mixAll();
		
}
	
}
