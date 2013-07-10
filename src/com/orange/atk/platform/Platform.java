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
 * File Name   : Platform.java
 *
 * Created     : 09/04/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.platform;


/**
 * Class which stores all platform dependant values.
 */

public abstract class Platform {

	private static Platform instance;

	/**
	 * Equal to the default line separator
	 */
	public static final String LINE_SEP = System.getProperty("line.separator");
	/**
	 * Equal to the path to the temporary directory of the system
	 */
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	/**
	 * Equal to the default file separator
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	/**
	 * Equal to the OS name
	 */
	public static final String OS_NAME = System.getProperty("os.name");

	public static Platform getInstance() {
		if (instance == null) {
			if (Platform.OS_NAME.toLowerCase().contains("windows")) {
				instance = new WindowsPlatform();
			} else if (Platform.OS_NAME.contains("OS X")) {
				instance = new OSXPlatform();
			} else {
				instance = new LinuxPlatform();
			}
		}
		return instance;
	}

	public abstract String getJATKPath();
	public abstract String getDefaultADBLocation();

}
