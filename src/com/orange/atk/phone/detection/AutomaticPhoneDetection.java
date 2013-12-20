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
 * File Name   : AutomaticPhoneDetection.java
 *
 * Created     : 30/10/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.phone.detection;


import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.Plugin;
import com.orange.atk.phone.PluginManager;
import com.orange.atk.phone.android.AndroidPhone;
import com.orange.atk.platform.Platform;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;




public class AutomaticPhoneDetection {

	//singleton pattern 
	private PhoneInterface selectedPhone= null;
	private List<PhoneInterface> connectedDevices;
	private DeviceDetectionThread deviceDetectionThread;
	private List<DeviceDetectionListener> deviceDetectionListeners = new ArrayList<DeviceDetectionListener>();
	private static AutomaticPhoneDetection instance=null;
	private boolean launchThread = true;
	
	public static AutomaticPhoneDetection getInstance(){
		return AutomaticPhoneDetection.getInstance(true);
	}

	public static AutomaticPhoneDetection getInstance(boolean launchThread){
		if(instance ==null) {
			instance = new AutomaticPhoneDetection(launchThread);
			try {
                ClassLoader cl = ClassLoader.getSystemClassLoader();

                URL[] urls = ((URLClassLoader)cl).getURLs();

                for(URL url: urls){
                    Logger.getLogger("AutomaticPhoneDetection").info(url.getFile());
                }
				String currentDir = new File(".").getAbsolutePath();
				Logger.getLogger("AutomaticPhoneDetection").info("currentDir="+currentDir);
				File folder = new File("./plugin");//here to ease launching via eclipse
				File[] listOfFiles = folder.listFiles();
                if(listOfFiles!=null){
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            String filename=listOfFiles[i].getName();
                            if(filename.endsWith(".jar")){
                                Logger.getLogger("AutomaticPhoneDetection").info(("Filename " + filename));
                                String name = filename.substring(0, filename.lastIndexOf('.'));
                                Logger.getLogger("AutomaticPhoneDetection").info(("File " + name));
                                String classname="com.orange.atk.phone."+name+"."+name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase()+"Plugin";
                                Logger.getLogger("AutomaticPhoneDetection").info(("Loading " + classname));
                                Class.forName(classname);
                            }else{
                                Logger.getLogger("AutomaticPhoneDetection").info(("skipping " + filename));
                            }
                        } else if (listOfFiles[i].isDirectory()) {
                            Logger.getLogger("AutomaticPhoneDetection").info(("Directory " + listOfFiles[i].getName()));
                        }
                    }
                }  else {
                    Class.forName("com.orange.atk.phone.android.AndroidPlugin");
                }
			} catch (ClassNotFoundException e) {
				Logger.getLogger("AutomaticPhoneDetection").error("Unable to load plugin");
			}
		}
		return instance;
	}

	//default constructor
	private AutomaticPhoneDetection(boolean launchThread) {
		if (launchThread) {
			deviceDetectionThread = new DeviceDetectionThread();
			deviceDetectionThread.start();
		}
		connectedDevices = new ArrayList<PhoneInterface>();
	}

	public void addDeviceDetectionListener(DeviceDetectionListener listener) {
		deviceDetectionListeners.add(listener);
	}
	
	public void pauseDetection() {
		if(deviceDetectionThread!=null){
			deviceDetectionThread.pauseDetection();
		}
	}
	public void resumeDetection() {
		if(deviceDetectionThread!=null){
			deviceDetectionThread.resumeDetection();
		}
	}

	public void stopDetection(DeviceDetectionListener listener) {
		deviceDetectionListeners.remove(listener);
		if (deviceDetectionListeners.size()<=1){
			deviceDetectionThread.exit();
			close();
		}
	}

	/**
	 * Fabric which return the phone currently connected to the PC
	 * @return phoneInterface. It allows communicate with the phone detected.
	 */
	public PhoneInterface getDevice()
	{
		if (selectedPhone==null) return new DefaultPhone();
		return selectedPhone;

	}


	public List<PhoneInterface> getDevices(){
		synchronized (connectedDevices) {
			List<PhoneInterface> connectedEnabledDevices = new ArrayList<PhoneInterface>();
			for (int i=0; i<connectedDevices.size(); i++) {
					if (!connectedDevices.get(i).isDisabledPhone())  {
						connectedEnabledDevices.add(connectedDevices.get(i));
					}
			}
			return connectedEnabledDevices;
		}
	}

	public void setSelectedDevice(PhoneInterface phone) {
		if (phone != selectedPhone) {
			selectedPhone = phone;		
			for (int i=0; i<deviceDetectionListeners.size(); i++) {
				deviceDetectionListeners.get(i).deviceSelectedChanged();
			}
		}
	}


	@SuppressWarnings("unchecked")
	public void checkDevices() {
		boolean changed = false;
		List<PhoneInterface> newConnectedDevices = new ArrayList<PhoneInterface>();
		List<Plugin> plugins = PluginManager.getAll();
		for (int j=0; j<plugins.size(); j++) {
			//Logger.getLogger(this.getClass()).info("plugin "+plugins.get(j).getName());
			changed=changed || plugins.get(j).checkDevices(connectedDevices,newConnectedDevices);
		}
		
		
		synchronized(connectedDevices) {
			// the devices not present in newConnectedDevices are the one that have been disconnected
			for (int i=connectedDevices.size()-1; i>=0; i--) {
				PhoneInterface phone = connectedDevices.get(i);
				if (!newConnectedDevices.contains(phone)) {
					if (phone.getCnxStatus()!=PhoneInterface.CNX_STATUS_DISCONNECTED) {
						phone.setCnxStatus(PhoneInterface.CNX_STATUS_DISCONNECTED);
						if (phone instanceof AndroidPhone) {
							if (!((AndroidPhone)phone).isDisabledPhone()) changed = true;
						} else changed = true;
					}
					if (phone.isInRecordingMode()) phone.stopRecordingMode();
					if (phone.isInTestingMode()) phone.stopTestingMode();
					if (selectedPhone != phone) {
						connectedDevices.remove(phone);
						if (phone instanceof AndroidPhone) {
							if (!((AndroidPhone)phone).isDisabledPhone()) changed = true;
						} else changed = true;
					}
				} else newConnectedDevices.remove(phone);
			}
			// add the new device connected to the list
			for (int i=0; i<newConnectedDevices.size(); i++) {
				PhoneInterface phone = newConnectedDevices.get(i);
				connectedDevices.add(phone);
			}
		}
		if (changed) notifyDevicesConnectedChanged();
	}

	public void notifyDevicesConnectedChanged() {
		for (int i=0; i<deviceDetectionListeners.size(); i++) {
			deviceDetectionListeners.get(i).devicesConnectedChanged();
		}
	}
	
	/**
	 * Detect if actual phone is a nokia phone	
	 * @return true if phone connected is a nokia.
	 */
	public Boolean isNokia(){
		//update device
		getDevice();
		if (selectedPhone!=null) return (selectedPhone.getType() == PhoneInterface.TYPE_S60) ;
		else return false;
		
	}


	/**
	 * Search the config file path of the phone in parameters.
	 * prefer use {@link #getxmlfilepath()}.
	 *
	 * @return the config file path
	 */
	public String getxmlfilepath()
	{
		String JATKpath = Platform.getInstance().getJATKPath();	
		String xmlconfilepath = JATKpath+Platform.FILE_SEPARATOR+"ConfigFiles"+Platform.FILE_SEPARATOR;
		
		// Test to determine which config file to use
		PhoneInterface phone = AutomaticPhoneDetection.getInstance().getDevice();
		if (phone != null) {
			xmlconfilepath += phone.getConfigFile();
		}

		return 	xmlconfilepath;
	}

	/**
	 * close all fabric (like androidDebugBridge)
	 */
	private void close() {
		List<Plugin> plugins = PluginManager.getAll();
		for (int j=0; j<plugins.size(); j++) {
			Logger.getLogger(this.getClass()).info("closing plugin "+plugins.get(j).getName());
			plugins.get(j).close();
		}
	}


}
