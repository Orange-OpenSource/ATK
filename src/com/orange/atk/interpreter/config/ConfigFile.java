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
 * File Name   : ConfigFile.java
 *
 * Created     : 18/04/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import com.orange.atk.platform.Platform;


/**
 * Class used to manage configuration file
 * 
 */
public class ConfigFile {
	// Error messages
	private static final String ERRMSG_VALUE_EMPTY = "value.length() == 0 or value is invalid";
	private static final String ERRMSG_VALUE_IS_NULL = "value is null";
	private static final String ERRMSG_OPTION_NAME_EMPTY = "optionName.length() == 0 or optionName is invalid";
	private static final String ERRMSG_OPTION_NAME_IS_NULL = "optionName is null";
	private static final String DELIMITER = "=";
	// Regex of allowed chars for option and value strings
	private static final String REGEX_ALLOWED_WORDS = "[^"+DELIMITER+"]+";
	/**
	 * Delimiter of option and value when saved in config file
	 */
	
	// contains couples of option and value
	private Hashtable<String, String> options = new Hashtable<String, String>();
	// file where couples of options and values would be saved and loaded
	private File configFile = null;

	/**
	 * Constructor. Only used if you want to temporary saved options and if you
	 * don't want to saved it. If not, you should use {@link #ConfigFile(File)}.
	 */
	public ConfigFile() {

	}

	/**
	 * Constructor
	 * 
	 * @param configFile
	 *            File object associated to the config file. File should already
	 *            exist.
	 */
	public ConfigFile(File configFile) {
		this.configFile = configFile;
	}

	/**
	 * Load the config file
	 * 
	 * @return true if the file has been correctly parsed, false otherwise
	 */
	public boolean loadConfigFile() {
		if (configFile == null) {
			return false;
		}
		java.io.FileReader fr = null;
		try {
			fr = new java.io.FileReader(configFile);
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			return false;
		}
		BufferedReader in = new BufferedReader(fr);
		String line;
		try {
			line = in.readLine();
			while (line != null) {
				int pos = line.indexOf(DELIMITER);
				if (pos == -1) {
					in.close();
					return false;
				}
				String type = line.substring(0, pos);
				String value = line.substring(pos + 1);
				setOption(type, value);
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			// e.printStackTrace();
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}
		return true;
	}

	/**
	 * Save file
	 * 
	 * @return true if the file has been correctly saved, false otherwise
	 */
	public boolean saveConfigFile() {
		if (configFile == null) {
			return false;
		}
		java.io.FileWriter fw = null;
		try {
			fw = new java.io.FileWriter(configFile);
			Set<String> keys = options.keySet();
			Iterator<String> iter = keys.iterator();
			while (iter.hasNext()) {
				String option = iter.next();
				fw.write(option + DELIMITER + getOption(option)
						+ Platform.LINE_SEP);
			}
			fw.close();
		} catch (IOException e) {
			// e.printStackTrace();
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}
		return true;
	}

	/**
	 * Add or re-define an option to the config file. If optionName already
	 * exists, previous value is set to value
	 * 
	 * @param optionName
	 *            option, not null, contains alphanumerical
	 *            characters (slash, backslash, space and underscore are allowed) and
	 *            option.length != 0
	 * @param value
	 *            value associated to option, not null, contains alphanumerical
	 *            characters (slash, backslash, space and underscore are allowed) and
	 *            option.length != 0
	 * @ensure getOption(optionName).equals(value)
	 * @throws NullPointerException
	 *             if optionName or value is equal to null
	 * @throws IllegalArgumentException
	 *             if optionName or value size is equal to 0 or if one of them
	 *             contains an =
	 */
	public void setOption(String optionName, String value) {
		if (optionName == null) {
			throw new NullPointerException(ERRMSG_OPTION_NAME_IS_NULL);
		}
		if (optionName.length() == 0
				|| !optionName.matches(REGEX_ALLOWED_WORDS)) {
			throw new IllegalArgumentException(ERRMSG_OPTION_NAME_EMPTY);
		}
		if (value == null) {
			throw new NullPointerException(ERRMSG_VALUE_IS_NULL);
		}
		if (value.length() == 0 || !value.matches(REGEX_ALLOWED_WORDS)) {
			throw new IllegalArgumentException(ERRMSG_VALUE_EMPTY);
		}
		// Logger.getLogger(this.getClass() ).debug("New option : (" + optionName + "," + value +
		// ")");
		options.put(optionName, value);
	}

	/**
	 * Return the value associated to the option optionName
	 * 
	 * @param optionName
	 *            an option
	 * @return value associated to the option, null if this option does not
	 *         exist.
	 */
	public String getOption(String optionName) {
		return options.get(optionName);
	}
}
