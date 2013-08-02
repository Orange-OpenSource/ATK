/*
 * Copyright (C) 2012 The Android Open Source Project
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
 */

package com.android.uiautomator.actions;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.android.ddmlib.IDevice;
import com.android.uiautomator.DebugBridge;
import com.android.uiautomator.UiAutomatorHelper;
import com.android.uiautomator.UiAutomatorHelper.UiAutomatorException;
import com.android.uiautomator.UiAutomatorHelper.UiAutomatorResult;
import com.android.uiautomator.UiAutomatorViewer;

public class ScreenshotAction {
	private UiAutomatorViewer mViewer;
	private static IDevice adevice = null;

	public ScreenshotAction(UiAutomatorViewer viewer) {
		mViewer = viewer;
	}
	private IDevice pickDevice() {
		List<IDevice> devices = DebugBridge.getDevices();
		if (devices.size() == 0) {
			Logger.getLogger(this.getClass()).debug("/****  no device detected ***/");
			return null;
		} else if (devices.size() == 1) {
			return devices.get(0);
		} else {
			for (int i = 0; i < devices.size(); i++) {
				Logger.getLogger(this.getClass()).debug(devices.get(i).getName());
			}
			DevicePickerDialog dlg = new DevicePickerDialog(mViewer, devices);
			return dlg.getSelectedDevice();
		}
	}

	private static class DevicePickerDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final List<IDevice> mDevices;
		private final String[] mDeviceNames;
		private static int sSelectedDeviceIndex;
		private boolean cancelClicked = false;

		public DevicePickerDialog(JFrame parentShell, List<IDevice> devices) {
			super(parentShell, ModalityType.APPLICATION_MODAL);

			mDevices = devices;
			mDeviceNames = new String[mDevices.size()];
			for (int i = 0; i < devices.size(); i++) {
				mDeviceNames[i] = devices.get(i).getName();
			}
			JLabel jlabel = new JLabel("Select Device");
			JComboBox jc = new JComboBox(mDeviceNames);
			JButton ok;
			JButton cancel;
			ok = new JButton("OK");
			cancel = new JButton("Cancel");
			ok.setPreferredSize(cancel.getPreferredSize());
			int defaultSelection =
					sSelectedDeviceIndex < mDevices.size() ? sSelectedDeviceIndex : 0;
			jc.setSelectedIndex(defaultSelection);
			jc.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox) e.getSource();
					sSelectedDeviceIndex = cb.getSelectedIndex();
				}
			});
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DevicePickerDialog.this.dispose();
				}
			});
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelClicked = true;
					DevicePickerDialog.this.dispose();
				}
			});
			JPanel buttonsPanel = new JPanel();
			JPanel comboPanel = new JPanel(new FlowLayout());
			comboPanel.add(jlabel);
			comboPanel.add(jc);
			buttonsPanel.add(ok);
			buttonsPanel.add(cancel);
			getRootPane().setDefaultButton(ok);
			this.add(comboPanel, BorderLayout.CENTER);
			this.add(buttonsPanel, BorderLayout.SOUTH);
			this.setTitle("Select Device");
			this.setSize(300, 120);
			ok.requestFocusInWindow();
			setLocationRelativeTo(parentShell);
			this.setResizable(false);
			this.setVisible(true);

		}
		public IDevice getSelectedDevice() {
			if (cancelClicked) {
				return null;
			}
			return mDevices.get(sSelectedDeviceIndex);
		}
	}

	public void screenshotAction(String cmd, final String glassPaneMesg) {
		final String command = cmd;
		if (!DebugBridge.isInitialized()) {
			Logger.getLogger(this.getClass()).debug("/**** adb not initialized***/");
			return;
		}
		if (UiAutomatorViewer.dumpXMLFirstTime) {
			adevice = pickDevice();
		}
		if (adevice == null) {
			JOptionPane.showMessageDialog(mViewer, "no device detected ", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Thread thread = new Thread() {
			@Override
			public void run() {
				mViewer.glassPane.setText(glassPaneMesg);
				mViewer.glassPane.start();
				Thread progress1 = new Thread() {
					@Override
					public void run() {
						UiAutomatorResult result = null;
						try {
							if (command.equals("views")) {
								result = UiAutomatorHelper.takeSnapshot(adevice, mViewer, command);
								mViewer.setModel(result.model, result.uiHierarchy,
										result.screenshot);
							} else {
								UiAutomatorHelper.executeRobotiumCommand(command);
							}
						} catch (UiAutomatorException e) {
							JOptionPane.showMessageDialog(mViewer,
									"Error while executing " + e.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
							Logger.getLogger(this.getClass()).debug(
									"/**** Error while taking snapshot ***/" + e.getMessage());
							mViewer.glassPane.stop();
							return;
						}
					}
				};
				progress1.start();
			}
		};
		thread.start();
	}

}