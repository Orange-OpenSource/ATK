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
 * File Name   : MatosGUI.java
 *
 * Created     : 25/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.actions.MatosAction;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 * Main class of Matos user interface.
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class MatosGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 133071390482737120L;

	/** path to icons */
	static public final String iconpath = Configuration.getProperty("iconDir");

	/** the menu bar, where to add menus from plugins */
	private static JMenuBar menuBar = null;

	/** a tabbed pane to receive panel from plugins */
	private static JTabbedPane tabbedPane = null;

	/** the status bar **/
	public StatusBar statusBar = null;

	private static boolean modified = false;

	public static ArrayList<AnalysisGUICommon> analysisPlugins = null;
	public static ArrayList<IGUICommon> othersPlugins = null;
	public static ArrayList<IGUICommon> allPlugins = null;

	private int locationX = 0;
	private int locationY = 0;
	public static ImageIcon icon = null;
	private static final String icondescr = "ATK";

	private static final String matosTitle = "ATK";
	private static String baseTitle = matosTitle;
	/** Check list file name */
	private static String checkListFileName = null;

	public static String outputDir = null;
	public static File destDir = null;

	// static because acces by a static way from other GUI elements
	public static JPhoneStatusButton phoneStatusButton;

	/** Constructor */
	public MatosGUI() {
		try {
			// init plugins which extends GUICommon extension point
			createAndShowGUI();
		} catch (Alert a) {
			String message = "Unable to start the application since:";
			String[] mesgs = a.getMessage().split("\\. ");
			for (String msg : mesgs) {
				message += "\n" + msg;
			}
			JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(CoreGUIPlugin.getIconURL("tango/messagebox_critical.png")));
			System.exit(1);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			String message = "Unable to start the application since:";
			String[] mesgs = e.getMessage().split("\\.");
			for (String msg : mesgs) {
				message += "\n" + msg;
			}
			JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE,
					new ImageIcon(CoreGUIPlugin.getIconURL("tango/messagebox_critical.png")));
		}
	}

	/**
	 * Returns the selected Analysis GUI pane
	 * 
	 * @return the selected Analysis GUI pane
	 */
	public AnalysisGUICommon getSelectedAnalysisPane() {
		int selected = tabbedPane.getSelectedIndex();
		if ((selected < 0) || (selected > MatosGUI.analysisPlugins.size()))
			return null;
		else
			return MatosGUI.analysisPlugins.get(selected);
	}

	/**
	 * Select a Analysis GUI pane
	 */
	public void setSelectedAnalysisPane(AnalysisGUICommon toBeSelected) {
		tabbedPane.setSelectedComponent(toBeSelected.getMainPanel());
	}

	public String getCheckListFileName() {
		return checkListFileName;
	}

	/**
	 * Create components and show the user interface
	 * 
	 * @throws Exception
	 */
	public void createAndShowGUI() throws Exception {

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		menuBar = new JMenuBar();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Component selectedComp = tabbedPane.getSelectedComponent();
				for (IGUICommon plugin : allPlugins) {
					JPanel panel = plugin.getMainPanel();
					if ((panel != null) && (panel.equals(selectedComp))) {
						plugin.notifySelected();
						if (CoreGUIPlugin.mainFrame != null)
							CoreGUIPlugin.mainFrame.updateButtons();
					}
				}
			}
		});
		analysisPlugins = new ArrayList<AnalysisGUICommon>();
		othersPlugins = new ArrayList<IGUICommon>();
		allPlugins = new ArrayList<IGUICommon>();
		int nbAnalysisPlugins = 0;
		for (IGUICommon guiCommon : CoreGUIPlugin.guiCommons) {
			if (guiCommon instanceof AnalysisGUICommon) {
				nbAnalysisPlugins++;
				analysisPlugins.add((AnalysisGUICommon) guiCommon);
				// Add to the campaign list
				Campaign.campaignsList.add(((AnalysisGUICommon) guiCommon).getCheckListTable()
						.getCampaign());
			} else {
				othersPlugins.add(guiCommon);
			}
			allPlugins.add(guiCommon);
		}

		JMenuItem itemNew = MatosAction.NEWCHECKLIST.getAsMenuItem("New");
		JMenuItem itemOpen = MatosAction.OPEN.getAsMenuItem("Open...");
		JMenuItem itemSave = MatosAction.SAVEALLALL.getAsMenuItem("Save");
		JMenuItem itemSaveAs = MatosAction.SAVEAS.getAsMenuItem("Save as...");
		JMenuItem itemSaveSelectionAs = MatosAction.SAVESELECTIONAS
				.getAsMenuItem("Save selection as...");

		// handla analysis plugins
		JMenu itemAddFile = new JMenu("Add file to analyse");
		for (AnalysisGUICommon guiCommon : analysisPlugins) {
			JMenuItem itemAddFilePlugin = guiCommon.getAddStepMenuItem();
			if (itemAddFilePlugin != null) {
				itemAddFile.add(itemAddFilePlugin);
			}
		}

		JMenuItem itemDirectory = MatosAction.ADDDIR.getAction().getAsMenuItem("Add directory...");
		JMenuItem itemCheckList = MatosAction.ADDCHECKLIST.getAction().getAsMenuItem(
				"Add check-list...");
		JMenuItem itemQuit = MatosAction.EXIT.getAction().getAsMenuItem("Exit");

		JMenu menuFile = new JMenu("File");
		menuFile.add(itemNew);
		menuFile.add(new JSeparator());
		menuFile.add(itemOpen);
		menuFile.add(itemSave);
		menuFile.add(itemSaveAs);
		menuFile.add(itemSaveSelectionAs);
		menuFile.add(new JSeparator());
		menuFile.add(itemAddFile);
		menuFile.add(itemDirectory);
		menuFile.add(itemCheckList);
		menuFile.add(new JSeparator());

		// handle other plugins in File menu
		int nbSubMenu = 0;
		for (IGUICommon gui : othersPlugins) {
			JMenuItem item = gui.getFileMenuItem();
			if (item != null) {
				menuFile.add(item);
				nbSubMenu++;
			}
		}
		if (nbSubMenu > 0) {
			menuFile.add(new JSeparator());
		}
		menuFile.add(itemQuit);

		menuBar.add(menuFile);

		JMenuItem itemSelectAll = MatosAction.SELECTALLALL.getAsMenuItem("Select all");
		JMenuItem itemUnselectAll = MatosAction.UNSELECTALLALL.getAsMenuItem("Unselect all");
		JMenuItem itemCopy = MatosAction.COPY.getAsMenuItem("Copy");
		JMenuItem itemPaste = MatosAction.PASTE.getAsMenuItem("Paste under");
		JMenuItem itemRemove = MatosAction.REMOVE.getAsMenuItem("Remove");
		JMenuItem itemProperties = MatosAction.PROPERTIES.getAsMenuItem("Properties...");
		JMenuItem itemConfiguration = MatosAction.CONFIGURATION.getAsMenuItem("Configuration...");

		JMenu menuEdit = new JMenu("Edit");
		menuEdit.add(itemSelectAll);
		menuEdit.add(itemUnselectAll);
		menuEdit.add(new JSeparator());
		menuEdit.add(itemCopy);
		menuEdit.add(itemPaste);
		menuEdit.add(itemRemove);
		menuEdit.add(new JSeparator());
		menuEdit.add(itemProperties);
		menuEdit.add(itemConfiguration);

		menuBar.add(menuEdit);

		JMenu menuTools = new JMenu("Tools");
		menuTools.add(MatosAction.VIEWREPORT.getAsMenuItem("View latest report"));
		menuTools.add(new JSeparator());

		// add analysis menus according to plugins
		if (nbAnalysisPlugins > 1) { // need sub-menus
			JMenu analyseAllSubMenu = new JMenu("Analyse all");
			JMenuItem itemAnalyseAllAll = MatosAction.ANALYSEALLALLTAB.getAsMenuItem("All"); /* "analyseAllAll" */
			analyseAllSubMenu.add(itemAnalyseAllAll);
			JMenu analyseSelectionSubMenu = new JMenu("Analyse selection");
			JMenuItem itemAnalyseSelAllAll = MatosAction.ANALYSESELECTIONALLTAB
					.getAsMenuItem("All"); /* "analyseSelectionAll" */
			analyseSelectionSubMenu.add(itemAnalyseSelAllAll);

			for (AnalysisGUICommon guiCommon : analysisPlugins) {
				JMenuItem analyseAllItem = guiCommon.getAnalyseAllMenuItem();
				if (analyseAllItem != null) {
					analyseAllSubMenu.add(analyseAllItem);
				}
				JMenuItem analyseSelectionItem = guiCommon.getAnalyseSelectionMenuItem();
				if (analyseSelectionItem != null) {
					analyseSelectionSubMenu.add(analyseSelectionItem);
				}
			}

			menuTools.add(analyseAllSubMenu);
			menuTools.add(analyseSelectionSubMenu);
		} else { // no need sub-menu
			JMenuItem itemAnalyseAll = MatosAction.ANALYSEALLALLTAB.getAsMenuItem("Analyse all");
			menuTools.add(itemAnalyseAll);
			JMenuItem itemAnalyseSelection = MatosAction.ANALYSESELECTIONALLTAB
					.getAsMenuItem("Analyse selection");
			menuTools.add(itemAnalyseSelection);
		}

		menuTools.add(new JSeparator());
		// menuTools.add(MatosAction.CONFIRMVERDICT.getAsMenuItem("Confirm the verdict"));
		// menuTools.add(
		// MatosAction.MODIFYVERDICT.getAsMenuItem("Modify the verdict"));
		menuTools.add(new JSeparator());
		menuTools.add(MatosAction.STATISTICSALL.getAsMenuItem("View statistics"));
		menuTools.add(new JSeparator());
		menuTools.add(MatosAction.VIEWLOG.getAsMenuItem("Open log file"));

		// handle other plugins in Tools menu
		nbSubMenu = 0;
		for (IGUICommon gui : othersPlugins) {
			JMenuItem item = gui.getToolsMenuItem();
			if (item != null) {
				menuTools.add(item);
				nbSubMenu++;
			}
		}
		// if (nbSubMenu>0) {
		// menuFile.add(new JSeparator());
		// }

		menuBar.add(menuTools);

		// add tabbed panes of analysis plugins
		for (AnalysisGUICommon guiCommon : analysisPlugins) {
			JPanel mainPanel = guiCommon.getMainPanel();
			if (guiCommon.getDisplayName() != null && mainPanel != null) {
				tabbedPane.addTab(guiCommon.getDisplayName(), mainPanel);
			}
		}

		// add tabbed panes of others plugins
		for (IGUICommon gui : othersPlugins) {
			JPanel mainPanel = gui.getMainPanel();
			if (gui.getDisplayName() != null && mainPanel != null) {
				tabbedPane.addTab(gui.getDisplayName(), mainPanel);
			}
		}

		JMenuItem aboutItem = MatosAction.ABOUT.getAsMenuItem("About...");
		JMenuItem userGuideItem = MatosAction.USERGUIDE.getAsMenuItem("ATK User Guide");

		JMenu menuHelp = new JMenu("Help");
		menuHelp.add(aboutItem);
		menuHelp.add(userGuideItem);

		// add help menu at rightmost
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(menuHelp);

		statusBar = new StatusBar("");

		// building the tool bar
		JToolBar toolBar = new JToolBar();

		toolBar.setFloatable(false);
		toolBar.add(MatosAction.NEWCHECKLIST.getAsJButton());
		toolBar.add(MatosAction.OPEN.getAsJButton());
		toolBar.add(MatosAction.ADDDIR.getAsJButton());
		toolBar.add(MatosAction.SAVEALLALL.getAsJButton());
		toolBar.add(MatosAction.SAVEAS.getAsJButton());
		toolBar.addSeparator(new Dimension(30, 30));

		toolBar.add(MatosAction.ADDTASK.getAsJButton());
		toolBar.add(MatosAction.COPY.getAsJButton());
		toolBar.add(MatosAction.PASTE.getAsJButton());
		toolBar.add(MatosAction.REMOVE.getAsJButton());
		toolBar.addSeparator(new Dimension(30, 30));
		toolBar.add(MatosAction.ANALYSEALLTASKS.getAsJButton());
		toolBar.add(MatosAction.ANALYSESELECTEDTASK.getAsJButton());
		toolBar.add(MatosAction.STOPTASK.getAsJButton());
		toolBar.add(MatosAction.VIEWREPORT.getAsJButton());

		toolBar.addSeparator(new Dimension(30, 30));
		toolBar.add(MatosAction.OPENRECORDER.getAsJButton());
		toolBar.add(MatosAction.MONITOR.getAsJButton());
		toolBar.add(MatosAction.BENCHMARK.getAsJButton());
		toolBar.add(MatosAction.ARODATANALYSER.getAsJButton());

		// adds an Exit nutton at rigthmost
		toolBar.add(Box.createHorizontalGlue());
		phoneStatusButton = new JPhoneStatusButton();
		toolBar.add(phoneStatusButton);

		setJMenuBar(menuBar);
		JPanel upperBars = new JPanel(new BorderLayout());
		upperBars.add(toolBar, BorderLayout.WEST);
		upperBars.add(phoneStatusButton, BorderLayout.EAST);
		pane.add(upperBars, BorderLayout.PAGE_START);
		pane.add(tabbedPane, BorderLayout.CENTER);
		pane.add(statusBar, BorderLayout.SOUTH);
		add(pane, BorderLayout.CENTER);

		// Graphics stuff
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsConfiguration[] gc = gs[0].getConfigurations();
		Rectangle bounds = gc[0].getBounds();
		int width = 800;
		int height = 600;
		locationX = bounds.x + (bounds.width - width) / 2;
		locationY = bounds.y + (bounds.height - height) / 2;
		// remember last dimension & location
		width = Integer.valueOf(Configuration.getProperty(Configuration.GUI_WIDTH, "" + width));
		height = Integer.valueOf(Configuration.getProperty(Configuration.GUI_HEIGTH, "" + height));
		locationX = Integer.valueOf(Configuration.getProperty(Configuration.GUI_LOCATION_X, ""
				+ locationX));
		locationY = Integer.valueOf(Configuration.getProperty(Configuration.GUI_LOCATION_Y, ""
				+ locationY));

		setSize(width, height);
		setLocation(locationX, locationY);
		addWindowListener(new WindowAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				AutomaticPhoneDetection.getInstance().stopDetection(phoneStatusButton);
				ActionEvent ae = new ActionEvent(e.getSource(), e.getID(),
						MatosAction.EXIT.getName());
				MatosAction.EXIT.getAction().actionPerformed(ae);
				dispose();
			}

		});
		// hide splash
		URL iconURL = CoreGUIPlugin.getMainIcon();
		icon = new ImageIcon(iconURL, icondescr);
		setIconImage(icon.getImage());
		updateButtons();
		setTitle(baseTitle);
		// show this frame
		setVisible(true);
		statusBar.uiPostInit();
	}
	/**
	 * Updates title of content tabs with the number of thep they contain.
	 */
	public void updateContentTabsTitle() {
		int i = 0;
		for (AnalysisGUICommon guiCommon : analysisPlugins) {
			String name = guiCommon.getDisplayName();
			CheckListTable clt = guiCommon.getCheckListTable();
			tabbedPane.setTitleAt(i, name + " [" + clt.getStepNumber() + "]");
			tabbedPane.setToolTipTextAt(i,
					name + ": " + clt.getStepNumber() + " element"
							+ (clt.getStepNumber() > 1 ? "s" : ""));
			i++;
		}
	}

	public int getLocationX() {
		return locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		MatosGUI.modified = modified;
		if (modified) {
			setTitle("*" + baseTitle);
		} else {
			setTitle(baseTitle);
		}
		updateButtons();
	}

	public static AnalysisGUICommon getSelectedGUI() {
		int indexSelected = tabbedPane.getSelectedIndex();
		return analysisPlugins.get(indexSelected);
	}

	/**
	 * Enable or disable menu in accordance with contents of current check-list
	 * 
	 */
	public void updateButtons() {
		int nbRows = getStepNumber();
		int indexSelected = tabbedPane.getSelectedIndex();
		if (indexSelected < 0 || indexSelected >= analysisPlugins.size()) {
			return;
		}
		AnalysisGUICommon guiCommonSelected = getSelectedAnalysisPane();
		int nbRowCurrentTabSelected = guiCommonSelected.getCheckListTable().getSelectedRowCount();
		int nbRowCurrentTab = guiCommonSelected.getCheckListTable().getStepNumber();
		boolean copiedRow = guiCommonSelected.hasCopiedRow();

		if (nbRows == 0) {
			MatosAction.SAVEALLALL.setEnabled(false);
			MatosAction.SAVEAS.setEnabled(false);
			MatosAction.SAVESELECTIONAS.setEnabled(false);
			MatosAction.NEWCHECKLIST.setEnabled(false);
			MatosAction.COPY.setEnabled(false);
			MatosAction.PASTE.setEnabled(false);
			MatosAction.REMOVE.setEnabled(false);
			MatosAction.PROPERTIES.setEnabled(false);
			MatosAction.VIEWREPORT.setEnabled(false);
			MatosAction.SELECTALLALL.setEnabled(false);
			MatosAction.UNSELECTALLALL.setEnabled(false);

			MatosAction.ANALYSEALLCURRENTTAB.setEnabled(false);
			MatosAction.ANALYSESELECTIONCURRENTTAB.setEnabled(false);
			MatosAction.ANALYSEALLALLTAB.setEnabled(false);
			MatosAction.ANALYSESELECTIONALLTAB.setEnabled(false);
			MatosAction.CONFIRMVERDICT.setEnabled(false);
			MatosAction.MODIFYVERDICT.setEnabled(false);
			MatosAction.STATISTICSALL.setEnabled(false);
			MatosAction.ANALYSEALLTASKS.setEnabled(false);
			MatosAction.ANALYSESELECTEDTASK.setEnabled(false);

		} else {
			MatosAction.NEWCHECKLIST.setEnabled(true);
			MatosAction.SAVEALLALL.setEnabled(isModified());
			MatosAction.SAVEAS.setEnabled(true);
			MatosAction.SELECTALLALL.setEnabled(true);
			MatosAction.UNSELECTALLALL.setEnabled(true);

			MatosAction.ANALYSEALLCURRENTTAB.setEnabled(nbRowCurrentTab > 0);
			MatosAction.ANALYSEALLALLTAB.setEnabled(true);
			MatosAction.ANALYSESELECTIONALLTAB.setEnabled(true);
			MatosAction.STATISTICSALL.setEnabled(true);
			MatosAction.ANALYSEALLTASKS.setEnabled(false);
			MatosAction.ANALYSESELECTEDTASK.setEnabled(false);

			if (nbRowCurrentTabSelected > 0) {
				MatosAction.SAVESELECTIONAS.setEnabled(true);
				MatosAction.COPY.setEnabled(true);
				if (copiedRow) {
					MatosAction.PASTE.setEnabled(true);
				} else {
					MatosAction.REMOVE.setEnabled(false);
				}
				MatosAction.REMOVE.setEnabled(true);

				MatosAction.PROPERTIES.setEnabled(nbRowCurrentTabSelected == 1);
				MatosAction.CONFIRMVERDICT.setEnabled(nbRowCurrentTabSelected == 1);
				MatosAction.MODIFYVERDICT.setEnabled(nbRowCurrentTabSelected == 1);

				MatosAction.ANALYSESELECTIONCURRENTTAB.setEnabled(true);
				MatosAction.VIEWREPORT.setEnabled(true);
			} else {
				MatosAction.SAVESELECTIONAS.setEnabled(false);
				MatosAction.COPY.setEnabled(false);
				MatosAction.PASTE.setEnabled(false);
				MatosAction.REMOVE.setEnabled(false);
				MatosAction.ANALYSESELECTIONCURRENTTAB.setEnabled(false);
				MatosAction.VIEWREPORT.setEnabled(false);
				MatosAction.PROPERTIES.setEnabled(false);
				MatosAction.CONFIRMVERDICT.setEnabled(false);
				MatosAction.MODIFYVERDICT.setEnabled(false);

			}
		}

		// selected analisys tab hav to to so
		guiCommonSelected.updateButtons();

		CheckListTable checkListTable = guiCommonSelected.getCheckListTable();
		if (checkListTable != null) {
			boolean hasRow = (checkListTable.getStepNumber() > 0);
			MatosAction.ANALYSEALLTASKS.setEnabled(hasRow);
			boolean isRowSelected = (checkListTable.getSelectedRowCount() > 0);
			MatosAction.ANALYSESELECTEDTASK.setEnabled(isRowSelected);

			if (checkListTable.getSelectedRowCount() == 1) {
				Step step = checkListTable.getSelectedStep();
				// REPORT
				String repPath = step.getOutFilePath();
				if ((repPath == null) || (repPath.trim().length() == 0)) {
					MatosAction.VIEWREPORT.setEnabled(false);
				} else {
					MatosAction.VIEWREPORT.setEnabled(true);
				}
			} else {
				MatosAction.VIEWREPORT.setEnabled(isRowSelected);
			}
		}
		MatosAction.STOPTASK.setEnabled(false);

		// no analysis GUI plugins have to do so...
		for (IGUICommon guiCommon : othersPlugins/* CoreGUIPlugin.guiCommons */) {
			guiCommon.updateButtons();
		}
	}

	/**
	 * Enable or disable user action on the GUI. This is used to prevent
	 * multiple actions since they use the same staus bar and overwrite each
	 * other's status.
	 * 
	 * @param b
	 *            the enable/disable status
	 */
	public void enableUserActions(boolean b) {
		for (MatosAction ma : MatosAction.values()) {
			ma.setEnabled(b);
		}

		for (IGUICommon common : CoreGUIPlugin.guiCommons) {
			common.enableUserActions(b);
		}

		tabbedPane.setEnabled(b);

		if (b) {
			updateButtons();
		} else {
			MatosAction.EXIT.setEnabled(true); // exit always available
			MatosAction.VIEWLOG.setEnabled(true);
		}
	}

	public void disableButtonsButStop() {
		enableUserActions(true);
		MatosAction.ADDTASK.setEnabled(false);
		MatosAction.ANALYSEALLTASKS.setEnabled(false);
		MatosAction.ANALYSESELECTEDTASK.setEnabled(false);
		MatosAction.STOPTASK.setEnabled(true);
	}

	/**
	 * @param newCLFileName
	 */
	public void setCheckListFileName(String newCLFileName) {
		checkListFileName = newCLFileName;
		if (checkListFileName == null) {
			setModified(false);
			baseTitle = matosTitle;
		} else {
			baseTitle = matosTitle + " - " + checkListFileName;
		}
		setTitle(baseTitle);
	}

	/**
	 * Get the global number of steps
	 * 
	 * @return
	 */
	public int getStepNumber() {
		int nbRows = 0;
		for (AnalysisGUICommon guiCommon : analysisPlugins) {
			nbRows += guiCommon.getCheckListTable().getStepNumber();
		}
		return nbRows;
	}

	public static Campaign getCampaign() {
		Campaign campaign = new Campaign();
		for (AnalysisGUICommon guiCommon : analysisPlugins) {
			campaign.addAll(guiCommon.getCheckListTable().getCampaign());
		}
		return campaign;
	}

	public static Campaign getSelectedCampaign() {
		Campaign selectedCampaign = new Campaign();
		for (AnalysisGUICommon guiCommon : analysisPlugins) {
			selectedCampaign.addAll(guiCommon.getCheckListTable().getSelectedCampaign());
		}
		return selectedCampaign;
	}
}
