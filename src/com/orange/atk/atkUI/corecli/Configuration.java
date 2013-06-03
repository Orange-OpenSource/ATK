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
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.utils.FileResolver;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.corecli.utils.SortedProperties;
import com.orange.atk.platform.Platform;
import com.orange.atk.util.FileUtil;

/**
 * This class represents the configuration parameters.
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class Configuration {

	private static SortedProperties properties = new SortedProperties();
	private static Vector<String> defaultPhoneConfigs;
	private HashMap <String,String> defaultConfig;
	public static String defaultPhoneConfigPath = Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"ConfigFiles"+Platform.FILE_SEPARATOR;
	private static String configFileName = null;

	/** A file resolver to get back file from URI */
	public static FileResolver fileResolver;


	/** List of tools */
	/** Parser to parse the file which contains the list of tools */
	private static ExternalToolXMLParser externalToolParser = null;


	// properties's valid keys
	public static final String LOGFILENAME = "logFile";
	public static final String PROXYSET = "proxySet";
	public static final String REALTIMEGRAPH = "realtime";
	public static final String PROXYHOST = "proxyHost";
	public static final String PROXYPORT = "proxyPort";
	public static final String OUTPUTDIRECTORY = "outputDir";
	public static final String INPUTDIRECTORY = "inputDir";
	public static final String CONFIGDIRECTORY = "configDir";
	public static final String CSS = "reportCSSFile";
	public static final String KEEPREPORT = "keepReport";
	public static final String OUTPUTDIRECTORYCONVERTS60 = "outputDirS60";
	public static final String SPECIFICADB = "useSpecificADB";
	public static final String NETWORKMONITOR = "useNetworkMonitor";
	public static final String ADBPATH = "ADBPath";
	public static final String SCROTATION = "screenshotRotation";
	public static final String BENCHMARKDIRECTORY = "benchmarkDir";
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

	private static Configuration instance;
	
	public static Configuration getInstance(){
		if(instance ==null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	private Configuration() {
		defaultConfig = new HashMap<String,String>();
		defaultConfig.put("com.orange.atk.phone.android.AndroidDriver","android.xml");
		defaultConfig.put("com.orange.atk.phone.android.AndroidMonkeyDriver","android.xml");
		defaultConfig.put("com.orange.atk.phone.android.AndroidICSDriver","android.xml");
		defaultConfig.put("com.orange.atk.phone.android.AndroidJBDriver","android.xml");
		defaultConfig.put("com.orange.atk.phone.mediatek.MediatekPhone","mediatek.xml");
		defaultPhoneConfigs = new Vector<String>();
		Iterator<String> configNames = defaultConfig.values().iterator();
		while (configNames.hasNext()) {
			String name = configNames.next();
			if (!defaultPhoneConfigs.contains(name)) defaultPhoneConfigs.add(name);
		}
		
	}

	public HashMap<String,String> getDefaultConfig() {
		return defaultConfig;
	}
	
	public Vector<String> defaultPhoneConfigNames() {
		return defaultPhoneConfigs;
	}
	
	public static boolean loadConfigurationFile(String configFileName) {
		Configuration.configFileName = configFileName;
		try {
			FileInputStream fileInputStream = new FileInputStream(configFileName);
			properties.load(fileInputStream);
			fileInputStream.close();
			fileResolver = new FileResolver(new File(Platform.TMP_DIR), 
					Integer.parseInt(getProperty(httpMaxConnectionTime)), 
					Integer.parseInt(getProperty(httpMaxDownloadTime)), 
					Integer.parseInt(getProperty(httpMaxAttempts)), 
					Boolean.parseBoolean(getProperty(PROXYSET)),
					getProperty(PROXYHOST), 
					getProperty(PROXYPORT));
			String configdir = properties.getProperty(CONFIGDIRECTORY);
			if((null==configdir)||(configdir.equals(""))){
				int option = JOptionPane. showConfirmDialog(null,
						"You must specify a folder to save the configuration files.\n"+
						"Please select a folder.\n" +
						"(A folder ConfigFiles will created in the folder you  select)",
						"Select a folder for the configuration files...",
						JOptionPane.OK_CANCEL_OPTION);
				if(JOptionPane.CANCEL_OPTION == option){
					return false;
				}
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setFileFilter(new FileUtilities.FilterDir());
				int returnVal = -1;
				do{
					returnVal =  fileChooser.showDialog(null, "Select location to create the directory for the configuration files.");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String selectedFolder = fileChooser.getSelectedFile().toString();
						selectedFolder += File.separator + "ConfigFiles" + File.separator;
						File folder = new File(selectedFolder);
						if(!folder.exists()){
							if(!folder.mkdir()){
								if(JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(null, 
										"Could not create the configuration folder in the selected folder.\n" +
										"Please selecte a new folder.",
										"Need to select another folder",
										JOptionPane.OK_CANCEL_OPTION)){
									return false;
								}
							}
						}

						properties.setProperty(CONFIGDIRECTORY, selectedFolder);
						writeProperties();

						//We have to copy the default configuration files
						String folderPath = Platform.getInstance().getJATKPath();	
						folderPath += Platform.FILE_SEPARATOR+Platform.FILE_SEPARATOR+"ConfigFiles"+ File.separator;
						
						File folderinstall = new File(folderPath);
						if(!folderinstall.exists()){
							Logger.getLogger("").debug("The installation folder with the configurations files" +
									"does not exits.\n It should be under "+folderPath);
						}
						FilenameFilter filter = new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.endsWith(".xml");
							}
						};
						String[] listfiles = folderinstall.list(filter);
						for(String fileName : listfiles){
							File newFile = new File(selectedFolder+fileName);
							File toCopy = new File(folderPath+fileName);
							FileUtil.copyfile(newFile, toCopy);
						}
						return true;

					}
				}while(true);
			}
			else{
				//TODO: need to verify the folder exists
			}
			return true;

		} catch (FileNotFoundException fnfe) {
			Alert.raise(fnfe, "Unable to find configuration file '"+configFileName+"'");
		} catch (IOException ioe) {
			Alert.raise(ioe, "Unable to access configuration file '"+configFileName+"'");
		}
		return false;
	}


	/**
	 * Saves definitions of external tools
	 * @throws Exception 
	 */
	public static void updateExternalToolList(List<ExternalTool> tools) throws Exception {
		if (externalToolParser==null) {
			externalToolParser = new ExternalToolXMLParser();
		}
	}

	public static String getConfigFileName() {
		return configFileName;
	}

	/**
	 * Retrieves a config property value by its name.
	 * Use static field of this class as keys.
	 * @param key the name of the property
	 * @return the value of the property.
	 * @throws Alert if the property cannot be found.
	 */
	public static String getProperty(String key) {
		String val = properties.getProperty(key);
		if (val==null) {
			Alert.raise(null, "Unable to find config property named '"+key+"' in "+Configuration.configFileName );
		}
		return val;
	}

	/**
	 * Retrieves a config property value by its name.
	 * Use static field of this class as keys.
	 * @param key the name of the property
	 * @param defaultValue the value to use if the key is not found
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

			//	externalToolParser.writeInFile(externalToolsList, Configuration.extToolsConfigFileName);
		} catch (FileNotFoundException fne) {
			Alert.raise(fne, "Cannot find configuration file: "+fne.getMessage());
		} catch (IOException ioe) {
			Alert.raise(ioe, "Cannot write configuration file: "+ioe.getMessage());
		} catch (Exception ex) {
			Alert.raise(ex, "Cannot write configuration file: "+ex.getMessage());
		}
	}

	public static String getVersion() {
		return getProperty("matosVersion");
	}

	public static String getRevision() {
		return getProperty("matosRevision");
	}

}
