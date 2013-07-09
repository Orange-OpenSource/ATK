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

package com.android.uiautomator;


import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.uiautomator.robotiumTask.RobotiumTaskForViewer;
import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.RootWindowNode;
import com.orange.atk.phone.PhoneException;



import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class UiAutomatorHelper {
	private static RobotiumTaskForViewer robotiumTask=null;
	public static UiAutomatorViewer mViewer=null;



	private static void getUiHierarchyFile(IDevice device, File dst, String cmd ) throws UiAutomatorException {

		Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.getUiHierarchyFile***/");

		if(UiAutomatorViewer.dumpXMLFirstTime){
			robotiumTask= new RobotiumTaskForViewer(device);
		}
		try {
			robotiumTask.getViewFromRobotium(cmd);
			if(!RobotiumTaskForViewer.XmlViews.equalsIgnoreCase("KO")){
				try {
					PrintWriter pw = new PrintWriter(new FileWriter(dst.getAbsolutePath(),true));
					pw.print(RobotiumTaskForViewer.XmlViews);
					pw.close();
				} catch(IOException e){
					String msg = "exception while getUiHierarchyFile : " + e.getMessage();
					Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper. exception while getUiHierarchyFile***/");
					throw new UiAutomatorException(msg, e);
				}
			} else {
				dst=null;
				throw new UiAutomatorException("cann't get view", null);

			}

		} catch (PhoneException e1) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper. exception while getUiHierarchyFile***/");
			String msg = "exception while getUiHierarchyFile from Robotium : " + e1.getMessage();
			throw new UiAutomatorException(msg, e1);
		}
	}


	public static UiAutomatorResult takeSnapshot(IDevice device, UiAutomatorViewer mViewer1,String cmd)
			throws UiAutomatorException {
		mViewer=mViewer1;
		File tmpDir = null;
		File xmlDumpFile = null;
		File screenshotFile = null;
		try {
			tmpDir = File.createTempFile("uiautomatorviewer_", "");
			tmpDir.delete();
			if (!tmpDir.mkdirs())
				throw new IOException("Failed to mkdir");
			xmlDumpFile = File.createTempFile("dump_", ".uix", tmpDir);
			screenshotFile = File.createTempFile("screenshot_", ".png", tmpDir);
		} catch (Exception e) {
			String msg = "Error while creating temporary file to save snapshot: "
					+ e.getMessage();
			throw new UiAutomatorException(msg, e);
		}

		tmpDir.deleteOnExit();
		xmlDumpFile.deleteOnExit();
		screenshotFile.deleteOnExit(); 

		//get xml dump file
		try {
			UiAutomatorHelper.getUiHierarchyFile(device, xmlDumpFile,cmd);
		} catch (Exception e) {
			String msg = "Error while obtaining UI hierarchy XML file: " + e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error while obtaining UI hierarchy XML file:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}

		UiAutomatorModel model;
		try {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.load UiAutomatorModel ***/");
			model = new UiAutomatorModel(xmlDumpFile);
		} catch (Exception e) {
			String msg = "Error while parsing UI hierarchy XML file: " + e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error while parsing UI hierarchy XML file:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}


		Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.getScreenshot***/");
		RawImage rawImage;
		try {
			rawImage = device.getScreenshot();
		} catch (Exception e) {
			String msg = "Error taking device screenshot: " + e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error taking device screenshot:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}

		BasicTreeNode root = model.getXmlRootNode();
		if ((root instanceof RootWindowNode)) {
			for (int i = 0; i < ((RootWindowNode)root).getRotation(); i++) {
				rawImage = rawImage.getRotated();
			}
		}


		BufferedImage bImage=null; 
		bImage = new BufferedImage(rawImage.width, rawImage.height,BufferedImage.TYPE_INT_ARGB);
		int index = 0;
		int increment = rawImage.bpp >> 3;

		for (int y = 0 ; y < rawImage.height ; y++) {
			for (int x = 0 ; x < rawImage.width ; x++) {
				bImage.setRGB(x, y, rawImage.getARGB(index));
				index+=increment;
			}
		}

		Image screenshot =null;
		try {
			ImageIO.write(bImage, "png", screenshotFile);
			screenshot  = ImageIO.read(screenshotFile);
		} catch (IOException e) {
			String msg = "Error while Reading  Screenshot File: " + e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error while Reading  Screenshot File file:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}

		return new UiAutomatorResult(xmlDumpFile, model, screenshot);
	}

	@SuppressWarnings("serial")
	public static class UiAutomatorException extends Exception {
		public UiAutomatorException(String msg, Throwable t) {
			super(msg, t);
		}
	}

	public static class UiAutomatorResult {
		public final File uiHierarchy;
		public final UiAutomatorModel model;
		public final Image screenshot;

		public UiAutomatorResult(File uiXml, UiAutomatorModel m, Image s) {
			uiHierarchy = uiXml;
			model = m;
			screenshot = s;
		}
	}

	public static void executeRobotiumCommand(String cmd) throws UiAutomatorException{
		try {
			robotiumTask.getViewFromRobotium(cmd);

		} catch (PhoneException e1) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper. exception while executing command***/ : "+cmd);
			String msg = "exception while executing command : " +cmd +" exception "+ e1.getMessage();
			throw new UiAutomatorException(msg, e1);
		}

	}
}