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
 * File Name   : PrepareApkForRobotiumTest.java
 *
 * Created     : 05/06/2013
 */
package com.orange.atk.phone.android;

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

public class PrepareApkForRobotiumTest {

	public static void prepareAPKForRobotiumGetViews(IDevice adevice ,String packName, String activityName, String packsourceDir, String TestAPK,int versionCode) throws PhoneException {
		Logger.getLogger(PrepareApkForRobotiumTest.class).debug("/****prepare APK For Robotium test ***/ ");
		String adbLocation = Platform.getInstance().getDefaultADBLocation();
		String AndroidToolsDir=Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"AndroidTools";
		String createAndbuildTestApkFile =AndroidToolsDir+ Platform.FILE_SEPARATOR+"BuildAndSignApk"+Platform.FILE_SEPARATOR+"build-tools"+
				Platform.FILE_SEPARATOR+"CreateDexFileAndBuildApk.bat";
		String resignApkUnderTest = AndroidToolsDir+Platform.FILE_SEPARATOR+"BuildAndSignApk"+Platform.FILE_SEPARATOR+"Sign-tools"+
				Platform.FILE_SEPARATOR+"ATKSignAPK.bat";
		String TestDir =AndroidToolsDir+Platform.FILE_SEPARATOR+"UiautomatorViewerTask";
		String testApkSrcDir=AndroidToolsDir+Platform.FILE_SEPARATOR+TestAPK.substring(0, TestAPK.indexOf(".apk"));
		String TempTestApkDir=TestDir+Platform.FILE_SEPARATOR+TestAPK.substring(0, TestAPK.indexOf(".apk"));
		String TempInitFile=TempTestApkDir+Platform.FILE_SEPARATOR+"bin"+Platform.FILE_SEPARATOR+"com"+Platform.FILE_SEPARATOR+
				"orange"+Platform.FILE_SEPARATOR+"atk"+Platform.FILE_SEPARATOR+"soloTest"+Platform.FILE_SEPARATOR+
				"init.prop";

		boolean packageExistInCache=cacheForRobotiumTest(packName,versionCode);
		Runtime r =Runtime.getRuntime();

		BufferedReader errorStream=null;
		BufferedReader inputStream=null;
		if(!(new File(TestDir).exists())){
			(new File(TestDir)).mkdir();
		}
		if(!(new File(TestDir+Platform.FILE_SEPARATOR+"TempAPK").exists())){
			(new File(TestDir+Platform.FILE_SEPARATOR+"TempAPK")).mkdir();
		}
		
		String [] pullapk = {adbLocation,"-s",adevice.getSerialNumber(), "pull" ,packsourceDir,TestDir};
		if(!packageExistInCache) {
			try {
				Process processPullApK = r.exec(pullapk);
				inputStream = new BufferedReader(new InputStreamReader(processPullApK.getInputStream()));
				errorStream = new BufferedReader(new InputStreamReader(processPullApK.getErrorStream()));
				String line ="";
				while ((line =errorStream.readLine()) != null){

				}
				errorStream.close();
				while ((line =inputStream.readLine()) != null){

				}
				inputStream.close();
			} catch (IOException e) {
				Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e.getMessage());
				throw new PhoneException(e.getMessage());
			}

		}

		removeDirectory( new File(TestDir+Platform.FILE_SEPARATOR+"TempAPK"+Platform.FILE_SEPARATOR+TestAPK));
		try {
			copyFolder(new File(testApkSrcDir),new File(TempTestApkDir));
		} catch (IOException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());
		}
		createInitFile(TempInitFile,activityName,packName);
		String buildAndSignTestApk []={createAndbuildTestApkFile,TempTestApkDir,TestDir+Platform.FILE_SEPARATOR+"TempAPK"+Platform.FILE_SEPARATOR+TestAPK, packName };  
		try{
			Process p =  r.exec(buildAndSignTestApk, null, new File(AndroidToolsDir));
			inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line ="";
			while ((line =inputStream.readLine()) != null) {
				Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("building test apk : " + line);
			}
			inputStream.close();
		}  catch (IOException e1){
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e1.getMessage());
			throw new PhoneException(e1.getMessage());
		}
		if(!packageExistInCache) { 

			String appapk = packsourceDir.substring(packsourceDir.lastIndexOf("/")+1);
			String reSignAPP []={resignApkUnderTest, TestDir+Platform.FILE_SEPARATOR+appapk,TestDir+Platform.FILE_SEPARATOR+"TempAPK"+Platform.FILE_SEPARATOR+appapk };  
			try {
				Process p =  r.exec(reSignAPP, null, new File(TestDir));
				inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
				errorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line ="";
				while ((line =inputStream.readLine()) != null) {
					Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("resigning apk under test : " + line);
				}
				inputStream.close();
			} catch (IOException e1){
				Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e1.getMessage());
				throw new PhoneException(e1.getMessage());

			}

			pushPackage(adevice,packName,TestDir+Platform.FILE_SEPARATOR+"TempAPK"+Platform.FILE_SEPARATOR+appapk) ;
			File cacheDir = new  File(TestDir+Platform.FILE_SEPARATOR+"Cache"+Platform.FILE_SEPARATOR+packName+"_"+versionCode );
			if(!cacheDir.exists()){
				cacheDir.mkdir();
			}
			File f1= new File(TestDir+Platform.FILE_SEPARATOR+"TempAPK"+Platform.FILE_SEPARATOR+appapk);
			File f2= new File(cacheDir+Platform.FILE_SEPARATOR+appapk);
			if(f1.renameTo(f2)) {

			} else {
			}

			removeDirectory( new File(TestDir+Platform.FILE_SEPARATOR+appapk));
			removeDirectory( new File(TestDir+Platform.FILE_SEPARATOR+"TempAPK"+Platform.FILE_SEPARATOR+appapk));

		}else {
			String apkPath= TestDir+Platform.FILE_SEPARATOR+"Cache"+Platform.FILE_SEPARATOR+
					packName+"_"+versionCode;
			String appapk = packsourceDir.substring(packsourceDir.lastIndexOf("/")+1);
			pushPackage(adevice,packName,apkPath+Platform.FILE_SEPARATOR+appapk) ;
		}

		removeDirectory( new File(TempTestApkDir));

	}


	protected static  void removeDirectory(File dir) {
		if(dir.exists()){
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

	protected static boolean  createInitFile(String filename,String activityName, String packageName) throws PhoneException{

		File initFile=new File(filename);
		initFile.delete();
		if (!initFile.exists()) {
			try {
				initFile.createNewFile();
			} catch (IOException e) {

			}
		}
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(filename,true));
			pw.println(activityName);
			pw.flush();
			pw.println(packageName);
			pw.close();
		} catch(IOException e){
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());
		}
		return false;
	}

	protected static boolean  cacheForRobotiumTest(String packgName, int versionCode) throws PhoneException{
		String cachePath=Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"AndroidTools"+Platform.FILE_SEPARATOR+"UiautomatorViewerTask";
		File cacheDir = new  File(cachePath+Platform.FILE_SEPARATOR+"Cache" );
		if(!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		File cacheFile=new File(cachePath+Platform.FILE_SEPARATOR+"Cache"+Platform.FILE_SEPARATOR+"cache.txt");
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
			while ((strLine = br.readLine()) != null){
				if(strLine.contains(packgName+" "+versionCode)) {
					br.close();
					return true;
				}
			} 
			br.close();
		}catch (FileNotFoundException e) { 
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		} catch (IOException e){
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		}
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(cachePath+Platform.FILE_SEPARATOR+"Cache"+Platform.FILE_SEPARATOR+"cache.txt",true));
			pw.println(packgName+" "+versionCode);
			pw.close();
		} catch(IOException e){
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());
		}
		return false;
	}


	public static void copyFolder(File src, File dest) throws IOException {
		if(src.isDirectory()){
			if(!dest.exists()){
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile,destFile);
			}
		}else{
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest); 
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0){
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}


	private static void pushPackage(IDevice adevice,String PackageName, String apkDir) throws PhoneException {
		Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("reinstalling pack "+PackageName);
		try {
			String result=null;
			if(new File(apkDir).exists()) {
				result = adevice.uninstallPackage(PackageName);
			} else {
				throw new PhoneException("error while reinstalling apk under test");
			}
			if(result!=null){
				Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****resul of uninstall : " + result);
			}
			result = adevice.installPackage(apkDir, true);
			if(result!=null){
				Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****resul of install : " + result);
			}
		}catch (InstallException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("/****error : " + e.getMessage());
			throw new PhoneException(e.getMessage());

		} 
	}

}
