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
 * File Name   : AnalysisGUICommon.java
 *
 * Created     : 27/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.coregui.actions.MatosAbstractAction;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class AnalysisGUICommon implements IGUICommon {

	protected JPanel mainPanel = null;
	protected CheckListTable checkListTable;
	protected boolean hasCopied = false;
	protected JToolBar toolBar = null;

	protected JLabel loopLabel = null;
	/**
	 * To get back the check-list from each plugin.
	 * 
	 * @return
	 */
	public abstract CheckListTable getCheckListTable();

	public void addInToolbar(Component comp) {
		if (toolBar == null) {
			toolBar = new JToolBar();
		}
		toolBar.add(comp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.francetelecom.rd.matos.coregui.IGUICommon#getMainPanel(int)
	 */
	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			JPanel upPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

			if (toolBar != null) {
				// upPanel.add(Box.createHorizontalStrut(5));
				// upPanel.add(toolBar);
			}
			// upPanel.setBorder(new EmptyBorder(2, 0, 0, 0));
			// mainPanel.add(upPanel, BorderLayout.NORTH);
			mainPanel.add(getCheckListTable()/* .getPanel() */, BorderLayout.CENTER);
		}
		return mainPanel;
	}

	/**
	 * Create a new checklist (Menu File -> New)
	 */
	public void newChecklist() {
		checkListTable.newCheckList();
	}

	/**
	 * @return true if a row has been copied in the checklist table
	 */
	public boolean hasCopiedRow() {
		return (getCheckListTable().getCopiedItems().size() != 0);
	}

	/**
	 * Opens a properties edition dialog for the selected step
	 */
	public abstract void editSelectedStepProperties();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.francetelecom.rd.matos.coregui.IGUICommon#enableUserActions(boolean)
	 */
	public void enableUserActions(boolean b) {
		checkListTable/* .getPanel() */.setEnabled(b);
	}

	/**
	 * Get back a menu to add a step in the check-list.
	 * 
	 * @return a JMenuItem that is able to launch process for adding a step
	 */
	public abstract JMenuItem getAddStepMenuItem();

	/**
	 * Get back a menu to analyse all steps of current tab of the check-list.
	 * 
	 * @return a JMenuItem that is able to launch process for analysing all step
	 */
	public abstract JMenuItem getAnalyseAllMenuItem();

	/**
	 * Get back a menu to analyse selected steps of current tab of the
	 * check-list.
	 * 
	 * @return a JMenuItem that is able to launch process for analysing selected
	 *         step
	 */
	public abstract JMenuItem getAnalyseSelectionMenuItem();

	/**
	 * @param dir
	 * @return
	 */
	public abstract Campaign buildCampaignFromDirectory(File dir);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.francetelecom.rd.matos.coregui.IGUICommon#getMenuItem()
	 */
	public JMenuItem getFileMenuItem() {
		JMenu subMenu = new JMenu(getDisplayName());
		subMenu.add(getAddStepMenuItem());
		subMenu.add(getAnalyseAllMenuItem());
		subMenu.add(getAnalyseSelectionMenuItem());
		return subMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.francetelecom.rd.matos.coregui.IGUICommon#getToolsMenuItems()
	 */
	public JMenuItem getToolsMenuItem() {
		return null;
	}

	public abstract MatosAbstractAction getAddScriptAction();

}
