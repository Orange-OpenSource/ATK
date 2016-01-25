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
 * File Name   : HelpAction.java
 *
 * Created     : 30/04/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.orange.atk.atkUI.coregui.HelpBrowser;

public class HelpAction extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	private String pageName;
	
	public HelpAction(String name, Icon icon, String shortDescription, String pageName) {
		super(name, icon, shortDescription);
		this.pageName = pageName;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		new HelpBrowser(pageName);
	}

}
