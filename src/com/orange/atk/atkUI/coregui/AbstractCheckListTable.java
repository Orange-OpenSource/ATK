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
 * File Name   : AbstractCheckListTable.java
 *
 * Created     : 25/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.orange.atk.atkUI.corecli.Campaign;

/**
 * AbstractCkeckListTable is the parent class of all kind of Check List table.
 * The composite Design pattern is used for checklist tables.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class AbstractCheckListTable extends JPanel {

//	protected JPanel panel = null;
	
	private static final long serialVersionUID = 1L;

	public AbstractCheckListTable() {
		//panel = new JPanel(new GridLayout(1,0));
		super(new GridLayout(1,0));
	}

//	public JPanel getPanel() {
//		return panel;
//	}
	
	/**
	 * Returns a campaign containing all steps.
	 * @return a campaign with all steps
	 */
	public abstract Campaign getCampaign();

}
