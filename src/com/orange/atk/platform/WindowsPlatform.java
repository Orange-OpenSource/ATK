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
 * File Name   : WindowsPlatform.java
 *
 * Created     : 05/03/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.platform;

import java.io.File;

import org.apache.log4j.Logger;

public class WindowsPlatform extends Platform {

	private static final String[] REG_COMMAND = new String[]{"REG.EXE", "QUERY",
			"HKLM\\HARDWARE\\DEVICEMAP\\SERIALCOMM"};
	private static final String REG_FNAMES_PATH = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E96D-E325-11CE-BFC1-08002BE10318}";
	private static final String[] REG_FNAMES_COMMAND = new String[]{"REG.EXE", "QUERY",
			REG_FNAMES_PATH};
	private static final String REG_EXTRAPNAMES_PATH = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E978-E325-11CE-BFC1-08002BE10318}";
	private static final String[] REG_EXTRAPNAMES_COMMAND = new String[]{"REG.EXE", "QUERY",
			REG_EXTRAPNAMES_PATH};

	private static String JATKPath;

	@Override
	public String getJATKPath() {
		if (JATKPath == null) {
			for (String key : new String[]{"HKLM\\SOFTWARE\\ATK\\Components",
					"HKLM\\SOFTWARE\\Wow6432Node\\ATK\\Components"}) {
				JATKPath = Winregister.getRegisterValue(key, "ATKpath", null, "REG_SZ");
				if (JATKPath != null)
					break;
			}
		}
		return JATKPath != null ? JATKPath : "C:\\Program Files\\ATK";
	}

	/**
	 * get adb executable location
	 */
	public String getDefaultADBLocation() {
		String path = System.getProperty("com.android.screenshot.bindir");
		if (path == null || path.length() == 0) {
			// default location
			String JATKpath = Platform.getInstance().getJATKPath();
			path = JATKpath + Platform.FILE_SEPARATOR + "AndroidTools" + Platform.FILE_SEPARATOR;
		}
		String adb = path + "adb.exe";
		File f = new File(adb);
		if (f.exists()) {
			// Logger.getLogger(WindowsPlatform).debug("adb = "+adb);
			return adb;
		}
		Logger.getLogger(WindowsPlatform.class).debug("No adb path found");
		return null;
	}
}
