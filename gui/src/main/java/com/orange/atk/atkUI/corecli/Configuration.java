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
 * File Name   : Configuration.java
 *
 * Created     : 02/03/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.corecli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.utils.FileResolver;
import com.orange.atk.atkUI.corecli.utils.SortedProperties;
import com.orange.atk.platform.Platform;
import com.orange.atk.util.FileUtil;

/**
 * This class represents the configuration parameters.
 * 
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class Configuration {

	private static SortedProperties properties = new SortedProperties();
	private static Vector<String> defaultPhoneConfigs;
	private HashMap<String, String> defaultConfig;
	private static String configFileName = null;

	/** A file resolver to get back file from URI */
	public static FileResolver fileResolver;

	// properties's valid keys
	public static final String LOGFILENAME = "logFile";
	public static final String PROXYSET = "proxySet";
	public static final String REALTIMEGRAPH = "realtime";
	public static final String PROXYHOST = "proxyHost";
	public static final String PROXYPORT = "proxyPort";
	public static final String OUTPUTDIRECTORY = "outputDir";
	public static final String INPUTDIRECTORY = "inputDir";
	//public static final String CONFIGDIRECTORY = "configDir";
	public static final String CSS = "reportCSSFile";
	public static final String KEEPREPORT = "keepReport";
	public static final String OUTPUTDIRECTORYCONVERTS60 = "outputDirS60";
	public static final String SPECIFICADB = "useSpecificADB";
	public static final String NETWORKMONITOR = "useNetworkMonitor";
	public static final String ADBPATH = "ADBPath";
	public static final String SCROTATION = "screenshotRotation";
	public static final String BENCHMARKDIRECTORY = "benchmarkDir";
	public static final String AROPATH = "aroPath";

	// PDF properties name
	public static final String pdfEncryptionUserPassword = "pdf.encryption.userpasswd";
	public static final String pdfEncryptionOwnerPassword = "pdf.encryption.ownerpasswd";
	public static final String pdfSignature = "pdf.signature";
	public static final String keystore = "pdf.signature.keystore";
	public static final String typeKeystore = "pdf.signature.typeKeystore";
	public static final String passwordKeystore = "pdf.signature.passwordKeystore";
	public static final String aliasCertificate = "pdf.signature.aliasCertificate";

	public static final String httpMaxConnectionTime = "httpMaxConnectionTime";
	public static final String httpMaxDownloadTime = "httpMaxDownloadTime";
	public static final String httpMaxAttempts = "httpMaxAttempts";

	public static final String MATOS_VERSION = "matosVersion";
	public static final String MATOS_REVISION = "matosRevision";

	// valid keys for GUI config properties
	public static final String GUI_HEIGTH = "gui.height";
	public static final String GUI_WIDTH = "gui.width";
	public static final String GUI_LOCATION_X = "gui.locationX";
	public static final String GUI_LOCATION_Y = "gui.locationY";

    private static final String MONITORING_CONFIG_DIR = "ConfigFiles";
    private static final Object PHONE_CONFIG_DIR = "AndroidTools"+File.separator+"config";

	private static Configuration instance;

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

	private Configuration() {
		defaultConfig = new HashMap<String, String>();
		defaultConfig.put("com.orange.atk.phone.android.AndroidDriver", "android.xml");
		defaultConfig.put("com.orange.atk.phone.android.AndroidMonkeyDriver", "android.xml");
		defaultConfig.put("com.orange.atk.phone.android.AndroidICSDriver", "android.xml");
		defaultConfig.put("com.orange.atk.phone.android.AndroidJBDriver", "android.xml");
		defaultPhoneConfigs = new Vector<String>();
		Iterator<String> configNames = defaultConfig.values().iterator();
		while (configNames.hasNext()) {
			String name = configNames.next();
			if (!defaultPhoneConfigs.contains(name)) {
				defaultPhoneConfigs.add(name);
			}
		}

	}

    public static String getMonitoringConfigDir(){
        return Platform.getInstance().getUserConfigDir()+ File.separator + MONITORING_CONFIG_DIR;
    }
    public static String getPhoneConfigDir(){
        return Platform.getInstance().getUserConfigDir() + File.separator + PHONE_CONFIG_DIR;
    }

	public HashMap<String, String> getDefaultMonitoringConfig() {
		return defaultConfig;
	}

	public Vector<String> defaultMonitoringConfigNames() {
		return defaultPhoneConfigs;
	}

	public static boolean loadConfigurationFile(String configFileName) {
		
		String userConfigDirPath = Platform.getInstance().getUserConfigDir();
		Configuration.configFileName = userConfigDirPath+Platform.FILE_SEPARATOR+configFileName;
		String atkPath = Platform.getInstance().getJATKPath();	
		File atkConfigDir = new File(userConfigDirPath);
		
		try {
		if (!(atkConfigDir.exists())) { 
			// Create configuration directory in user home
			atkConfigDir.mkdir(); 
			Logger.getLogger(Configuration.class).info("Config directory created in " + atkConfigDir.getPath());
			// Copy the main configuration file into it
			File newFile = new File(userConfigDirPath + Platform.FILE_SEPARATOR + configFileName);
			File originalFile = new File(atkPath + Platform.FILE_SEPARATOR + configFileName);
			FileUtil.copyfile(newFile, originalFile);
			Logger.getLogger(Configuration.class).info(configFileName + " copied into " + atkConfigDir.getPath());
			// Create the configFile directory if necessary and copy the config files
			
		}
		
		Logger.getLogger(Configuration.class).info("Configuration.configFileName= " + Configuration.configFileName);

			FileInputStream fileInputStream = new FileInputStream(Configuration.configFileName);			
			properties.load(fileInputStream);			
			fileInputStream.close();

			
			fileResolver = new FileResolver(new File(Platform.TMP_DIR), 
					Integer.parseInt(getProperty(httpMaxConnectionTime)), 
					Integer.parseInt(getProperty(httpMaxDownloadTime)), 
					Integer.parseInt(getProperty(httpMaxAttempts)), 
					Boolean.parseBoolean(getProperty(PROXYSET)),
					getProperty(PROXYHOST),
					getProperty(PROXYPORT));
			
			String userConfigFilesDirPath = userConfigDirPath + Platform.FILE_SEPARATOR + "ConfigFiles" + Platform.FILE_SEPARATOR ; //properties.getProperty(CONFIGDIRECTORY);
			File userConfigFilesDir = new File (userConfigFilesDirPath); 
			
			if((!userConfigFilesDir.exists()) || (!userConfigFilesDir.isDirectory())){
				//create the configuration Directory 
				Logger.getLogger(Configuration.class).info("creating ConfigFiles directory in "+userConfigFilesDir.getPath());
						if(!userConfigFilesDir.mkdir()){
							Logger.getLogger("").debug("The installation folder with the configurations files" +
									"does not exits.\n It should be under "+userConfigFilesDir.getPath());

						}
						FilenameFilter filter = new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.endsWith(".xml");
							}
						};

						String atkConfigFilesDirPath = atkPath + Platform.FILE_SEPARATOR + "ConfigFiles" + Platform.FILE_SEPARATOR; 
						String[] listfiles = new File(atkConfigFilesDirPath).list(filter);
						for(String fileName : listfiles){
							File newFile = new File(userConfigFilesDirPath+fileName);
							File originalFile = new File(atkConfigFilesDirPath+fileName);
							FileUtil.copyfile(newFile, originalFile);
						}
						

			}
			return true;
		} catch (FileNotFoundException fnfe) {
			Alert.raise(fnfe, "Unable to find configuration file '" + configFileName + "'");
		} catch (IOException ioe) {
			Alert.raise(ioe, "Unable to access configuration file '" + configFileName + "'");
		}
		return false;
	}





	/**
	 * Retrieves a config property value by its name. Use static field of this
	 * class as keys.
	 * 
	 * @param key
	 *            the name of the property
	 * @return the value of the property.
	 * @throws Alert
	 *             if the property cannot be found.
	 */
	public static String getProperty(String key) {
		String val = properties.getProperty(key);
		if (val == null) {
			Alert.raise(null, "Unable to find config property named '" + key + "' in "
					+ Configuration.configFileName);
		}
		return val;
	}

	/**
	 * Retrieves a config property value by its name. Use static field of this
	 * class as keys.
	 * 
	 * @param key
	 *            the name of the property
	 * @param defaultValue
	 *            the value to use if the key is not found
	 * @return the value of the property, or the default value.
	 */
	public static String getProperty(String key, String defaultValue) {
		String val = null;
		try {
			val = getProperty(key);
		} catch (Alert a) {
			val = defaultValue;
		}
		return val;
	}

	/**
	 * Set a avalue to a property
	 * 
	 * @param property
	 * @param value
	 */
	public static void setProperty(String property, String value) {
		properties.setProperty(property, value);
	}

	/**
	 * Write properties in the configuration file
	 */
	public static void writeProperties() {
		try {
			Logger.getLogger(Configuration.class).info("Write properties");
			FileOutputStream fileOutputStream = new FileOutputStream(new File(configFileName));
			properties.store(fileOutputStream, "");
			fileOutputStream.close();

			// externalToolParser.writeInFile(externalToolsList,
			// Configuration.extToolsConfigFileName);
		} catch (FileNotFoundException fne) {
			Alert.raise(fne, "Cannot find configuration file: " + fne.getMessage());
		} catch (IOException ioe) {
			Alert.raise(ioe, "Cannot write configuration file: " + ioe.getMessage());
		} catch (Exception ex) {
			Alert.raise(ex, "Cannot write configuration file: " + ex.getMessage());
		}
	}

	public static String getVersion() {
		return getProperty("matosVersion");
	}

	public static String getRevision() {
		return getProperty("matosRevision");
	}

	public static String getConfigFileName() { 
		return Configuration.configFileName;
	}

}
