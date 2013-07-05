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

import com.android.uiautomator.OpenDialog;
import com.android.uiautomator.UiAutomatorModel;
import com.android.uiautomator.UiAutomatorViewer;


import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class OpenFilesAction {
	private UiAutomatorViewer mViewer;

	public OpenFilesAction(UiAutomatorViewer viewer) {
		mViewer = viewer;
	}
	public void openFilesAction() {
		OpenDialog d = new OpenDialog(mViewer);
		if(d.getXmlDumpFile()!=null){
			UiAutomatorModel model;
			try {
				model = new UiAutomatorModel(d.getXmlDumpFile());
			}catch (IllegalArgumentException e){
				JOptionPane.showMessageDialog(mViewer, "OpenFilesAction. Error While Loading File : Invalid ui automator hierarchy file","Error",
						JOptionPane.ERROR_MESSAGE);
				Logger.getLogger(this.getClass() ).debug("/****OpenFilesAction. Error While Loading File : Invalid ui automator hierarchy file***/");
				return;
			}
			Image img = null;
			File screenshot = d.getScreenshotFile();
			if (screenshot != null) {
				try {
					img  = ImageIO.read(screenshot);
				} catch (IOException e) {
					Logger.getLogger(this.getClass() ).debug("/****OpenFilesAction. Error While Reading ScreenshotFile***/");
					return;
				}    

			}
			mViewer.setModel(model, d.getXmlDumpFile(), img);
		}
	}
}