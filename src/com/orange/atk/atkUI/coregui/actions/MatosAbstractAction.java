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
 * File Name   : MatosAbstractAction.java
 *
 * Created     : 10/05/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.coregui.actions;

import java.awt.Image;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public abstract class MatosAbstractAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private String name = null;

	public MatosAbstractAction(String name, Icon icon, String shortDescription) {
		super(name, icon);
		this.name = name;
		putValue(SHORT_DESCRIPTION, shortDescription);

	}

	/**
	 * Returns a menuItem that performs the given action
	 * @param label
	 * @return a new <code>JMenuItem</code> object
	 */
	public JMenuItem getAsMenuItem(String label) {
		JMenuItem menuItem = new JMenuItem(this);
		menuItem.setActionCommand(name);
		Icon icon = (Icon)getValue(Action.SMALL_ICON);
		if (icon!=null) {
			Image img = ((ImageIcon)icon).getImage();
			Image img16x16 = img.getScaledInstance(16, 16, Image.SCALE_AREA_AVERAGING);
			Icon icon16x16 = new ImageIcon(img16x16);
			menuItem.setIcon(icon16x16);
		}
		menuItem.setText(label);
		return menuItem;
	}

	/**
	 *  Returns a JButton that performs the given action
	 * @param action action do register to the Button
	 * @return a new <code>JButton</code> object
	 */
	public JButton getAsJButton() {
		JButton button = new JButton(this);
		button.setActionCommand(name);
		button.setText(""); // explicitly remove text from button
		return button;
	}

}
