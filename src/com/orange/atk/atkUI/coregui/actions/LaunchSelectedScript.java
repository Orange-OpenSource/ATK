package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.SelectDialog;
import com.orange.atk.atkUI.coregui.tasks.AnalyseTask;

public class LaunchSelectedScript extends MatosAbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * @param name
	 * @param icon
	 * @param shortDescription
	 */
	public LaunchSelectedScript(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if (MatosGUI.outputDir == null || MatosGUI.outputDir.equals("")) {
			int ret = SelectDialog.showDialog(CoreGUIPlugin.mainFrame, true);
			if (ret != SelectDialog.OK_OPTION) {
				return;
			}
		}
		new AnalyseTask(CoreGUIPlugin.mainFrame.statusBar, CoreGUIPlugin.mainFrame
				.getSelectedAnalysisPane().getCheckListTable(), false);

	}
}
