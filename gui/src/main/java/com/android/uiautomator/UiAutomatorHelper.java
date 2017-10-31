/*
 * Software Name : ATK - UIautomatorViewer Robotium Version
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
 * File Name   : UiAutomatorHelper.java
 *
 * Created     : 05/06/2013
 * Author(s)   : D'ALMEIDA Joana
 */

package com.android.uiautomator;


import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.TimeoutException;
import com.android.uiautomator.actions.ScreenshotAction;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class UiAutomatorHelper {
	private static RobotiumTaskForViewer robotiumTask=null;
	public static UiAutomatorViewer mViewer=null;

	public static boolean supportsUiAutomator(IDevice device) {
		String apiLevelString = device.getProperty("ro.build.version.sdk");
		int apiLevel;
		try {
			apiLevel = Integer.parseInt(apiLevelString);
		} catch (NumberFormatException e) {
			apiLevel = 16;
		}
		return apiLevel >= 16;
	}


	private static void getUiHierarchyFile(IDevice device, File dst, String cmd ) throws UiAutomatorException {

		Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.getUiHierarchyFile***/");

		if(UiAutomatorViewer.dumpXMLFirstTime){
			robotiumTask= new RobotiumTaskForViewer(device);
		}
		try {
			robotiumTask.getViewFromRobotium(cmd);
			if(RobotiumTaskForViewer.XmlViews==null){
				getUiHierarchyFile(device,dst);
				UiAutomatorViewer.dumpXMLFirstTime = false;
				return;
			}
			if(!RobotiumTaskForViewer.XmlViews.equalsIgnoreCase("KO")) {
				try {
					PrintWriter pw = new PrintWriter(new FileWriter(dst.getAbsolutePath(),true));
					pw.print(RobotiumTaskForViewer.XmlViews);
					pw.close();
				} catch(IOException e){
					String msg = "Exception while getUiHierarchyFile : " + e.getMessage();
					Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper. exception while getUiHierarchyFile***/");
					throw new UiAutomatorException(msg, e);
				}
			} else {
				dst=null;
				throw new UiAutomatorException("can't get views", null);

			}

		} catch (PhoneException e1) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper. exception while getUiHierarchyFile***/");
			String msg = "Exception while getUiHierarchyFile from Robotium : " + e1.getMessage();
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
		} catch (UiAutomatorException e) {
			String msg = e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error while obtaining UI hierarchy XML file:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}

		UiAutomatorModel model;
		try {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.load UiAutomatorModel ***/");
			model = new UiAutomatorModel(xmlDumpFile);
		} catch (Exception e) {
			String msg = "Error while parsing UI hierarchy XML file: \n" + e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error while parsing UI hierarchy XML file:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}

		UiAutomatorHelper.mViewer.glassPane.setText("Taking Screenshot");
		Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.getScreenshot***/");
		RawImage rawImage=null;
		try {
			rawImage = device.getScreenshot();
		} catch (TimeoutException e) {
			String msg = "Error taking device screenshot: " + e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error taking device screenshot:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}catch (IOException e) {
			String msg = "Error taking device screenshot: " + e.getMessage();
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error taking device screenshot:***/"+ e.getMessage());
			throw new UiAutomatorException(msg, e);
		}catch (AdbCommandRejectedException e) {
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
	/**
	 * call when device api level >= 16 
	 * @throws PhoneException 
	 */
	private static void getUiHierarchyFile(IDevice device, File dst) throws UiAutomatorException {
		Logger.getLogger(UiAutomatorHelper.class).debug( "/****views from uiautomator***/ ");
		String command = "rm /data/local/tmp/uidump.xml";
		try {
			CountDownLatch commandCompleteLatch = new CountDownLatch(1);
			device.executeShellCommand(command, new CollectingOutputReceiver(commandCompleteLatch));
			commandCompleteLatch.await(5L, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****error :  "+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (AdbCommandRejectedException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****error : "+  e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (ShellCommandUnresponsiveException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****error : "+  e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (IOException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****error : "+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (InterruptedException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****error : "+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		}
		command = String.format("%s %s %s", new Object[] { "/system/bin/uiautomator", "dump", "/data/local/tmp/uidump.xml" });

		CountDownLatch commandCompleteLatch = new CountDownLatch(1);
		try {
			device.executeShellCommand(command, new CollectingOutputReceiver(commandCompleteLatch), 40000);
			commandCompleteLatch.await(40L, TimeUnit.SECONDS);
			device.getSyncService().pullFile("/data/local/tmp/uidump.xml", dst.getAbsolutePath(), SyncService.getNullProgressMonitor());
		}catch (TimeoutException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error getviews from uiautomator:***/"+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (AdbCommandRejectedException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error getviews from uiautomator:***/"+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (ShellCommandUnresponsiveException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error getviews from uiautomator:***/"+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (IOException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error getviews from uiautomator:***/"+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		} catch (InterruptedException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error getviews from uiautomator:***/"+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		}	catch (SyncException e) {
			Logger.getLogger(UiAutomatorHelper.class).debug("/****UiAutomatorHelper.Error getviews from uiautomator:***/"+ e.getMessage());
			throw new UiAutomatorException(e.getMessage(),e);
		}
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