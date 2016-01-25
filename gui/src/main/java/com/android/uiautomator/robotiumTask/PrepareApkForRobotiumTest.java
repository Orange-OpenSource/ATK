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
 * File Name   : PrepareApkForRobotiumTest.java
 *
 * Created     : 05/06/2013
 * Author(s)   : D'ALMEIDA Joana
 */

package com.android.uiautomator.robotiumTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.platform.Platform;
import com.orange.atk.sign.apk.SignAPK;

public class PrepareApkForRobotiumTest {

	public static void prepareAPKForRobotiumGetViews(IDevice adevice ,String packName, String activityName, 
			String packsourceDir, String TestAPK,int versionCode) throws PhoneException {
		Logger.getLogger(PrepareApkForRobotiumTest.class).debug("/****prepare APK For RobotiumGetViews ***/ ");

		String adbLocation = Platform.getInstance().getDefaultADBLocation();
		String AndroidToolsDir = Platform.getInstance().getJATKPath() + Platform.FILE_SEPARATOR
				+ "AndroidTools";
		String buildTestApkFile = Platform.getInstance().getBuildApk();
		String removeSignBat = Platform.getInstance().getRemoveSignature();
		String TestDir = AndroidToolsDir + Platform.FILE_SEPARATOR + "UiautomatorViewerTask";
		String testApkSrcDir = AndroidToolsDir + Platform.FILE_SEPARATOR
				+ TestAPK.substring(0, TestAPK.indexOf(".apk"));
		String TempTestApkDir = TestDir + Platform.FILE_SEPARATOR
				+ TestAPK.substring(0, TestAPK.indexOf(".apk"));
		String TempInitFile = TempTestApkDir + Platform.FILE_SEPARATOR + "bin"
				+ Platform.FILE_SEPARATOR + "com" + Platform.FILE_SEPARATOR +
				"orange" + Platform.FILE_SEPARATOR + "atk" + Platform.FILE_SEPARATOR
				+ "soloGetViews" + Platform.FILE_SEPARATOR +
				"init.prop";

		boolean packageExistInCache = cacheForRobotiumTest(packName, versionCode);
		Runtime r = Runtime.getRuntime();

		BufferedReader errorStream=null;
		BufferedReader inputStream=null;
		if(!(new File(TestDir).exists())){
			(new File(TestDir)).mkdir();
		}
		if(!(new File(TestDir+Platform.FILE_SEPARATOR+"TempAPK").exists())){
			(new File(TestDir+Platform.FILE_SEPARATOR+"TempAPK")).mkdir();
		}
		removeDirectory(new File(TestDir+Platform.FILE_SEPARATOR+packsourceDir.substring(packsourceDir.lastIndexOf("/") + 1)));
		String [] pullapk = {adbLocation,"-s",adevice.getSerialNumber(), "pull" ,packsourceDir,TestDir};
		if(!packageExistInCache) {
			try {
				Process processPullApK = r.exec(pullapk);
				inputStream = new BufferedReader(new InputStreamReader(
						processPullApK.getInputStream()));
				errorStream = new BufferedReader(new InputStreamReader(
						processPullApK.getErrorStream()));
				String line = "";
				while ((line = errorStream.readLine()) != null) {

				}
				errorStream.close();
				while ((line = inputStream.readLine()) != null) {

				}
				inputStream.close();
			} catch (IOException e) {
				Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
						"/****error : " + e.getMessage());
				throw new PhoneException(e.getMessage());
			}

		}

		removeDirectory(new File(TestDir + Platform.FILE_SEPARATOR + "TempAPK"
				+ Platform.FILE_SEPARATOR + TestAPK));
		removeDirectory(new File(TempTestApkDir));
		try {
			copyFolder(new File(testApkSrcDir), new File(TempTestApkDir));
		} catch (IOException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
					"/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());
		}
		createInitFile(TempInitFile, activityName, packName);
		String buildApk[] = {buildTestApkFile, TempTestApkDir,packName};
		try {
			Process p = r.exec(buildApk, null, new File(AndroidToolsDir));
			inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = inputStream.readLine()) != null) {
				Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
						"building test apk : " + line);
			}
			inputStream.close();
		} catch (IOException e1) {
			Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
					"/****error : " + e1.getMessage());
			throw new PhoneException(e1.getMessage());
		}

		SignAPK.signApk(
				TempTestApkDir+ Platform.FILE_SEPARATOR+"bin"+Platform.FILE_SEPARATOR+"AtkTestRobotium.apk",
				TempTestApkDir + Platform.FILE_SEPARATOR + "bin" + Platform.FILE_SEPARATOR
				+ "NonAlignAtkTestRobotium.apk");
		SignAPK.zipAlignApk(
				TempTestApkDir + Platform.FILE_SEPARATOR + "bin" + Platform.FILE_SEPARATOR
				+ "NonAlignAtkTestRobotium.apk",
				TestDir + Platform.FILE_SEPARATOR + "TempAPK" + Platform.FILE_SEPARATOR
				+ TestAPK);

		if (!packageExistInCache) {

			String appapk = packsourceDir.substring(packsourceDir.lastIndexOf("/") + 1);
			String removeSign[] = {
					removeSignBat,
					TestDir + Platform.FILE_SEPARATOR + appapk};
			try {
				Process p = r.exec(removeSign, null, new File(TestDir));
				inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
				errorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = "";
				while ((line = inputStream.readLine()) != null) {
					Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
							"remove signature from Apk : " + line);
				}
				inputStream.close();
			} catch (IOException e1) {
				Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
						"/****error : " + e1.getMessage());
				throw new PhoneException(e1.getMessage());
			}
			SignAPK.signApk(
					TestDir + Platform.FILE_SEPARATOR + appapk,
					TempTestApkDir + Platform.FILE_SEPARATOR + "bin" + Platform.FILE_SEPARATOR
					+ "NonAlign"+appapk);
			SignAPK.zipAlignApk(
					TempTestApkDir + Platform.FILE_SEPARATOR + "bin" + Platform.FILE_SEPARATOR
					+ "NonAlign"+appapk,
					TestDir + Platform.FILE_SEPARATOR + "TempAPK" + Platform.FILE_SEPARATOR
					+ appapk);

			pushPackage(adevice, packName, TestDir + Platform.FILE_SEPARATOR + "TempAPK"
					+ Platform.FILE_SEPARATOR + appapk);
			File cacheDir = new File(TestDir + Platform.FILE_SEPARATOR + "Cache"
					+ Platform.FILE_SEPARATOR + packName + "_" + versionCode);
			if (!cacheDir.exists()) {
				cacheDir.mkdir();
			}
			File f1 = new File(TestDir + Platform.FILE_SEPARATOR + "TempAPK"
					+ Platform.FILE_SEPARATOR + appapk);
			File f2 = new File(cacheDir + Platform.FILE_SEPARATOR + appapk);
			if (f1.renameTo(f2)) {
				try {
					PrintWriter pw = new PrintWriter(new FileWriter(TestDir + Platform.FILE_SEPARATOR + "Cache"
							+ Platform.FILE_SEPARATOR + "cache.txt", true));
					pw.println(packName + "_" + versionCode);
					pw.close();
				} catch (IOException e) {
					Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
							"/****error : " + e.getMessage());
					throw new PhoneException(e.getMessage());
				}
			} else {
			}

			removeDirectory(new File(TestDir + Platform.FILE_SEPARATOR + appapk));
			removeDirectory(new File(TestDir + Platform.FILE_SEPARATOR + "TempAPK"
					+ Platform.FILE_SEPARATOR + appapk));

		} else {
			String apkPath = TestDir + Platform.FILE_SEPARATOR + "Cache" + Platform.FILE_SEPARATOR +
					packName + "_" + versionCode;
			String appapk = packsourceDir.substring(packsourceDir.lastIndexOf("/") + 1);
			pushPackage(adevice, packName, apkPath + Platform.FILE_SEPARATOR + appapk);
		}
		removeDirectory( new File(TempTestApkDir));
	}

	protected static void removeDirectory(File dir) {
		if (dir.exists()) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				if (files != null && files.length > 0) {
					for (File aFile : files) {
						removeDirectory(aFile);
					}
				}
				dir.delete();
			} else {
				dir.delete();
			}
		}
	}

	protected static boolean createInitFile(String filename, String activityName, String packageName)
			throws PhoneException {

		File initFile = new File(filename);
		initFile.delete();
		if (!initFile.exists()) {
			try {
				initFile.createNewFile();
			} catch (IOException e) {

			}
		}
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(filename, true));
			pw.println(activityName);
			pw.flush();
			pw.println(packageName);
			pw.close();
		} catch (IOException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
					"/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());
		}
		return false;
	}

	protected static boolean cacheForRobotiumTest(String packgName, int versionCode)
			throws PhoneException {
		String cachePath = Platform.getInstance().getJATKPath() + Platform.FILE_SEPARATOR
				+ "AndroidTools" + Platform.FILE_SEPARATOR + "UiautomatorViewerTask";
		File cacheDir = new File(cachePath + Platform.FILE_SEPARATOR + "Cache");
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		File cacheFile = new File(cachePath + Platform.FILE_SEPARATOR + "Cache"
				+ Platform.FILE_SEPARATOR + "cache.txt");
		if (!cacheFile.exists()) {
			try {
				cacheFile.createNewFile();
			} catch (IOException e) {

			}
		}
		try {
			FileInputStream fstream = new FileInputStream(cacheFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains(packgName + "_" + versionCode)) {
					br.close();
					return true;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
					"/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		} catch (IOException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
					"/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		}
		return false;
	}

	public static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}

	private static void pushPackage(IDevice adevice, String PackageName, String apkDir)
			throws PhoneException {
		Logger.getLogger(PrepareApkForRobotiumTest.class).debug("reinstalling package " + PackageName);
		try {
			String result = null;
			if (new File(apkDir).exists()) {
				result = adevice.uninstallPackage(PackageName);
			} else {
				throw new PhoneException("error while reinstalling apk under test: " + PackageName);
			}
			if (result != null) {
				Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
						"/****resul of uninstall : " + result);
			}
			result = adevice.installPackage(apkDir, true);
			if (result != null) {
				Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
						"/****resul of install : " + result);
			}
		} catch (InstallException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
					"/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		}
	}

}
