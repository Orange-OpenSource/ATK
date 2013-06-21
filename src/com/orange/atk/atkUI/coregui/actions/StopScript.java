package com.orange.atk.atkUI.coregui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.launcher.LaunchJATK;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

public class StopScript extends MatosAbstractAction {
	/** A flag indicating the need to stop the task. */
	private boolean shouldStop = false;

	/**
	 * @param name
	 * @param icon
	 * @param shortDescription
	 */
	public StopScript(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	public void actionPerformed(ActionEvent arg0) {
		LaunchJATK exec = null;
		if (Campaign.getLaunchExec() != null) {
			exec = Campaign.getLaunchExec();
			CoreGUIPlugin.mainFrame.statusBar.setStop();
			this.setEnabled(false);
			// Stop test
			if (exec.getCurrentPhone() != null)
				Logger.getLogger(this.getClass()).debug("Stop JATK softly ");
			exec.cancelExecution();
		} else {
			Logger.getLogger(this.getClass()).debug("Can't Stop JATK softly");

		}
		// Launch Autodetect after a Cancel
		AutomaticPhoneDetection.getInstance().resumeDetection();
	}

	/**
	 * Raises a flag indicating the task should stop as soon as possible.
	 */
	public void setStop() {
		shouldStop = true;
		CoreGUIPlugin.mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		CoreGUIPlugin.mainFrame.enableUserActions(true);
	}

	/**
	 * Tests the stop flag.
	 * 
	 * @return true if tha task have to stop as soon as possible, false
	 *         otherwise.
	 */
	public boolean isStop() {
		return shouldStop;
	}

}
