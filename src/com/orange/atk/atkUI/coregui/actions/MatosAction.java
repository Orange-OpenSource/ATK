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
 * File Name   : MatosAction.java
 *
 * Created     : 10/05/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.coregui.actions;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;

/**
 * 
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public enum MatosAction {
	NEWCHECKLIST("new", new NewCheckListAction("new", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/filenew.png")), "Create a new check-list")),

	OPEN("open", new OpenAction("open", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/folder_green.png")), "Open a check-list.")),

	SAVEALLALL("saveAllAll", new SaveAction("saveAllAll", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/filesave.png")), "Save the current check-list.")),

	SAVEAS("saveAs", new SaveAsAction("saveAs", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/filesaveas.png")), "Save the current check-list as...")),

	SAVESELECTIONAS("saveSelectionAs", new SaveSelectionAsAction("saveSelectionAs", null,
			"Save the selection in a file.")), OPENRECORDER("openRecorder", new OpenRecorderAction(
			"openRecorder", new ImageIcon(CoreGUIPlugin.getIconURL("tango/record.png")),
			"Open the script recorder")),

	MONITOR("monitor", new MonitorAction("monitor", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/graph.png")),
			"Monitor the phone (without runnning a test)")),

	BENCHMARK("benchmark", new BenchmarkAction("benchmark", new ImageIcon(
			CoreGUIPlugin.getIconURL("gauge-22.png")), "Benchmark the phone")),

	ADDDIR("addDir", new AddDirectoryAction("addDir", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/add-dir.png")),
			"Add a directory to the current check-list.")),

	ADDCHECKLIST("addCheckList", new AddCheckListAction("addCheckList", null,
			"Add a checklist to the current check-list.")),

	SELECTALLALL("selectAllAll", new SelectAllAction("selectAllAll", null,
			"Select all items of the the current check-list.")),

	UNSELECTALLALL("unselectAllAll", new UnselectAllAction("unselectAllAll", null,
			"Unselect all items of the the current check-list.")),

	COPY("copy", new CopyAction("copy", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/edit_copy.png")),
			"Copy the selected steps of the current check-list.")),

	PASTE("paste", new PasteAction("paste", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/paste.png")),
			"Paste the copied steps under the current selection.")),

	REMOVE("remove", new RemoveAction("remove", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/edit-delete.png")),
			"Remove the selected steps of the current check-list.")),

	PROPERTIES("properties", new EditPropertiesAction("properties", null,
			"Modify the properties of the selected step(s).")),

	CONFIGURATION("configuration", new ConfigurationAction("configuration", null,
			"Set global parameters of the analysis process.")),

	// actionMap.put("addCheckList", new AddCheckListAction("addCheckList",
	// null, "Add a checklist to the current checklist."));
	VIEWREPORT("viewReport", new ViewReportAction("viewReport", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/view-rep.png")),
			"View latest report of the selected element.")),

	VIEWANALYZER("viewAnalyzer", new ViewAnalyzerAction("viewAnalyzer", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/graph.png")),
			"View latest analyze of the selected element.")),

	CONFIRMVERDICT("confirmVerdict", new ConfirmVerdictAction("confirmVerdict", null,
			"Confirm the given verdict of the selected step")),

	MODIFYVERDICT("modifyVerdict", new ModifyVerdictAction("modifyVerdict", null,
			"Modify the given verdict of the selected step")),

	STATISTICSALL("statisticsAll", new StatisticsAction("statisticsAll", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/colorscm.png")),
			"View statistics about the current check-list")),

	VIEWLOG("viewLog", new ViewLogAction("viewLog", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/txt2.png")), "Open log file")),

	ANALYSESELECTIONALLTAB("analyseSelectionAll", new AnalyseSelectionAllTabAction(
			"analyseSelectionAll", new ImageIcon(
					CoreGUIPlugin.getIconURL("tango/play-selection.png")),
			"Analyse all selected elements of the check-list (all tabs).")),

	ANALYSESELECTIONCURRENTTAB("analyseSelection", new AnalyseSelectionCurrentTabAction(
			"analyseSelection",
			new ImageIcon(CoreGUIPlugin.getIconURL("tango/play-selection.png")),
			"Analyse all selected elements of the current tab.")),

	ANALYSEALLCURRENTTAB("analyseAll", new AnalyseAllCurrentTabAction("analyseAll", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/noatunplay.png")),
			"Analyse all elements of the current check-list part.")),

	ANALYSEALLALLTAB("analyseAllAll", new AnalyseAllAllTabAction("analyseAllAll", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/noatunplay.png")),
			"Analyse all elements of the check-list (all tabs)")),

	EXIT("exit", new ExitAction("exit", new ImageIcon(CoreGUIPlugin.getIconURL("tango/exit.png")),
			"Quit the application.")),

	ABOUT("about", new AboutAction("about", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/messagebox_info.png")), "About ATK")),

	USERGUIDE("User Guide", new HelpAction("Sony Ericsson", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/messagebox_info.png")), "ATK User Guide", "index.html")),

	ADDTASK("add script", new AddTaskAction("Add a task", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/add.png")), "Add a task to the current check-list.")),

	ANALYSEALLTASKS("LaunchAllScript", new LaunchAllScript("LaunchAllScript", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/noatunplay.png")),
			"Launch all scripts of the check-list.")),

	ANALYSESELECTEDTASK("LaunchSelectedScript", new LaunchSelectedScript("LaunchSelectedScript",
			new ImageIcon(CoreGUIPlugin.getIconURL("tango/play-selection.png")),
			"Launch selected Script of the check-list.")),

	STOPTASK("StopScript", new StopScript("StopScript", new ImageIcon(
			CoreGUIPlugin.getIconURL("tango/noatunstop.png")), "Stop the current task.")),

	ARODATANALYSER("AroDataAnalyser", new RunARODataAnalyser("AroDataAnalyser", new ImageIcon(
			CoreGUIPlugin.getIconURL("aro_22.png")), "Run ARO Data Analyser"));

	// ------------------------------------------------------------------------------

	private String name = null;
	private MatosAbstractAction action = null;

	private MatosAction(String name, MatosAbstractAction action) {
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
	 * Enable/Disable this <code>Action</code> action (ie all its related GUI
	 * components)
	 * 
	 * @param enable
	 *            new status for matosAction
	 */
	public void setEnabled(boolean enable) {
		action.setEnabled(enable);
	}

	/**
	 * Returns a menuItem that performs the given action
	 * 
	 * @param label
	 * @return a new <code>JMenuItem</code> object
	 */
	public JMenuItem getAsMenuItem(String label) {
		return action.getAsMenuItem(label);
	}

	/**
	 * Returns a JButton that performs the given action
	 * 
	 * @param action
	 *            action do register to the Button
	 * @return a new <code>JButton</code> object
	 */
	public JButton getAsJButton() {
		return action.getAsJButton();
	}

}
