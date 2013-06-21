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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

public class WindowsPlatform extends Platform {

	private static final String[] REG_COMMAND = new String[] { "REG.EXE", "QUERY",
						"HKLM\\HARDWARE\\DEVICEMAP\\SERIALCOMM" };
	private static final String REG_FNAMES_PATH = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E96D-E325-11CE-BFC1-08002BE10318}";
	private static final String[] REG_FNAMES_COMMAND = new String[] { "REG.EXE", "QUERY", REG_FNAMES_PATH };
	private static final String REG_EXTRAPNAMES_PATH = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E978-E325-11CE-BFC1-08002BE10318}";
	private static final String[] REG_EXTRAPNAMES_COMMAND = new String[] { "REG.EXE", "QUERY", REG_EXTRAPNAMES_PATH };
	
	// LinkedHashMap keeps track of the keys order based on insertion order
	private static LinkedHashMap<String,String> knownIPAddressPhones = new LinkedHashMap<String,String> ();
	private static LinkedHashMap<String,String> knownPortPhones = new LinkedHashMap<String,String> ();
	private static LinkedHashMap<String,String> providers = new LinkedHashMap<String,String> ();
	private static HashMap<String,Integer> nbPhonePorts = new HashMap<String,Integer> ();
	private static String JATKPath;
	
	public WindowsPlatform() {
		// COM Port detected phones
		// The order of the keys inserted in knownPortPhones is important
		
		// LG
		knownPortPhones.put("LG", "com.orange.atk.phone.lg.LGphone");
		nbPhonePorts.put("LG", 2);

		// Nokia or Mediatek indeterminate
		//knownPortPhones.put("USBSER", "com.orange.atk.phone.nokiaS60.NokiaS60Phone");
		knownPortPhones.put("USBSER", "indeterminate");
		nbPhonePorts.put("USBSER", 1);

		//Samsung
		knownPortPhones.put("sscemdm", "com.orange.atk.phone.samsung.SamsungPhone"); // Ex : Samsung Lismore
		nbPhonePorts.put("sscemdm", 1);
		knownPortPhones.put("sscdmdm", "com.orange.atk.phone.samsung.SamsungPhone");
		nbPhonePorts.put("sscdmdm", 1);
		knownPortPhones.put("ss_bmdm", "com.orange.atk.phone.samsung.SamsungPhone"); // Ex : Samsung Prato
		nbPhonePorts.put("ss_bmdm", 1);
		knownPortPhones.put("ssm_mdm", "com.orange.atk.phone.samsung.SamsungPhone");
		nbPhonePorts.put("ssm_mdm", 1);
		//Samsung Android
		knownPortPhones.put("ssadmdm", ""); // No class name => must be ignored (detected through ddmlib)
		nbPhonePorts.put("ssadmdm", 1);

		//Sony Ericsson
		knownPortPhones.put("mdm", "com.orange.atk.phone.se.SEPhone");  
		nbPhonePorts.put("mdm", 2);  
		
		//Mediatek
		knownPortPhones.put("ProlificSerial", "com.orange.atk.phone.mediatek.MediatekPhone"); 
		nbPhonePorts.put("ProlificSerial", 1);  
		
		// IP Address detected phones //windows mobile
		knownIPAddressPhones.put("Windows Mobile-based Device", "com.orange.atk.phone.winmobile.WinmobilePhone");
		
		// Providers list
		providers.put("Nokia", "com.orange.atk.phone.nokiaS60.NokiaS60Phone");
		providers.put("S60", "com.orange.atk.phone.nokiaS60.NokiaS60Phone");
		providers.put("MTK", "com.orange.atk.phone.mediatek.MediatekPhone");
		providers.put("MediaTek", "com.orange.atk.phone.mediatek.MediatekPhone");
		providers.put("Prolific", "com.orange.atk.phone.mediatek.MediatekPhone");
		providers.put("LG Electronics", "com.orange.atk.phone.lg.LGphone");
		providers.put("Samsung", "com.orange.atk.phone.samsung.SamsungPhone");
		providers.put("Sony Ericsson", "com.orange.atk.phone.samsung.SamsungPhone");
		
	}
	
	private HashMap<String, String> getProviderNames() {
		HashMap<String, String> providerNames = new HashMap<String, String>();
		Vector<String> reg_list = new Vector<String>();
		// Use REG.EXE
		Process process;
		try {
			process = Runtime.getRuntime().exec(REG_FNAMES_COMMAND);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			System.out.flush();
			while (line != null) {
				reg_list.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		for (int i=0; i<reg_list.size(); i++) {
			try {
				process = Runtime.getRuntime().exec(new String[] { "REG.EXE", "QUERY", reg_list.get(i)});
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = reader.readLine();
				System.out.flush();
				boolean found = false;
				String port = "";
				String pname = "";
				while (line != null && !found) {
					//Logger.getLogger(this.getClass()).debug("***"+line);
					if (line.contains("AttachedTo")) {
						String[] res = line.split("REG_SZ");
						port = res[res.length-1].trim();
					}
					if (line.contains("FriendlyName")) {
						String[] res = line.split("REG_SZ");
						pname = res[res.length-1].trim();
					}
					if (line.contains("ProviderName")) {
						String[] res = line.split("REG_SZ");
						pname += res[res.length-1].trim();
						found=true;
					}
					line = reader.readLine();
				}
				if (found) {
					//Logger.getLogger(this.getClass()).debug("Port "+port+" : "+pname);
					providerNames.put(port, pname);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return providerNames;
	}
	
	// Just another way to get Com Port provider name
	// but more complicated. So it is called, when provider
	// is not found with first method ....
	private String getProviderName(String portName) {
		//Logger.getLogger(this.getClass()).debug("***getProviderName");
		Vector<String> reg_list = new Vector<String>();
		// Use REG.EXE
		Process process;
		try {
			process = Runtime.getRuntime().exec(REG_EXTRAPNAMES_COMMAND);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			System.out.flush();
			while (line != null) {
				if (line.startsWith("HKEY") && !line.equals(REG_EXTRAPNAMES_PATH)) reg_list.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		Vector<String> usbdevices_list = new Vector<String>();
		Vector<String> providers_list = new Vector<String>();
		for (int i=0; i<reg_list.size(); i++) {
			try {
				process = Runtime.getRuntime().exec(new String[] { "REG.EXE", "QUERY", reg_list.get(i)});
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = reader.readLine();
				System.out.flush();
				boolean found = false;
				String deviceId = "";
				String providerId = "";
				//Logger.getLogger(this.getClass()).debug("***"+line);
				while (line != null) {
					if (line.contains("ProviderName")) {
						String[] res = line.split("REG_SZ");
						String providerName = res[res.length-1].trim();
						Iterator<String> providerKeys = providers.keySet().iterator();
						while (providerKeys.hasNext()) {
							String providerKey = (String) providerKeys.next();
							if (providerName.contains(providerKey)) {
								providerId = providerKey;
								found = true;
							}
						}
						
					} else if (line.contains("MatchingDeviceId")) {
						String[] res = line.split("REG_SZ");
						String[] lineValue = res[res.length-1].trim().split("\\\\");
						if (lineValue.length>1) deviceId = lineValue[1];
					}
					line = reader.readLine();
				}
				if (found && !deviceId.equals("") && !providerId.equals("")) {
					usbdevices_list.add(deviceId);
					providers_list.add(providerId);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		boolean found = false;
		String providerId = null;
		for (int i=0; i<usbdevices_list.size() && !found; i++) {
			reg_list.clear();
			String REG_USBDEVICES_PATH = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Enum\\USB\\"+usbdevices_list.get(i);
			String[] REG_USBDEVICES_COMMAND = new String[] { "REG.EXE", "QUERY", REG_USBDEVICES_PATH };
			try {
				process = Runtime.getRuntime().exec(REG_USBDEVICES_COMMAND);
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = reader.readLine();
				System.out.flush();
				while (line != null) {
					if (line.startsWith("HKEY") && !line.equals(REG_USBDEVICES_PATH)) reg_list.add(line);
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			for (int j=0; j<reg_list.size() && !found; j++) {
				try {
					process = Runtime.getRuntime().exec(new String[] { "REG.EXE", "QUERY", reg_list.get(j)});
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = reader.readLine();
					System.out.flush();
					//Logger.getLogger(this.getClass()).debug("***"+line);
					while (line != null) {
						if (line.contains("FriendlyName")) {
							String[] res = line.split("REG_SZ");
							String fname = res[res.length-1].trim();
							if (fname.contains(portName)) {
								providerId = providers_list.get(i);
							}
						}
						line = reader.readLine();
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
		return providerId;
	}
	
	@Override
	public String getJATKPath() {
		if (JATKPath == null) {
			for (String key: new String[]{"HKLM\\SOFTWARE\\ATK\\Components","HKLM\\SOFTWARE\\Wow6432Node\\ATK\\Components"}){
				JATKPath =  Winregister.getRegisterValue(key ,"ATKpath",null,"REG_SZ");
				if(JATKPath!=null) break;
			}
		}
		return JATKPath!=null ? JATKPath:"C:\\Program Files\\ATK";
	}
	
	@Override
	public String getJAPKtoolPath() {
		// TODO Auto-generated method stub
		
		String ATKPath=getJATKPath() ;
		
		return ATKPath+Platform.FILE_SEPARATOR+ "AndroidTools"+Platform.FILE_SEPARATOR+
				"apktoolWin";
	}
	
	/**
	 * get adb executable location
	 */
	public  String getDefaultADBLocation() { 
		String path = System.getProperty("com.android.screenshot.bindir"); 	 
		if (path == null || path.length() == 0) {
			//default location
			String JATKpath =	Platform.getInstance().getJATKPath();
			path = JATKpath+ Platform.FILE_SEPARATOR+ "AndroidTools"+Platform.FILE_SEPARATOR; 
		}
		// TODO chercher dans le path
		String adb = path+"adb.exe";
		File f = new File(adb);
		if (f.exists()) {
			//Logger.getLogger(WindowsPlatform).debug("adb = "+adb);			
			return adb;
		}
		Logger.getLogger(WindowsPlatform.class).debug("No adb path found");			

		return null;
	}

	
}
