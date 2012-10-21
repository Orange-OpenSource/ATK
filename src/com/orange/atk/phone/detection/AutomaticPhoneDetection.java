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



import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.error.ErrorManager;
import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.android.AndroidDriver;
import com.orange.atk.phone.android.AndroidICSDriver;
import com.orange.atk.phone.android.AndroidJBDriver;
import com.orange.atk.phone.android.AndroidMonkeyDriver;
import com.orange.atk.phone.android.AndroidPhone;
import com.orange.atk.platform.Platform;




public class AutomaticPhoneDetection {

	//singleton pattern 
	private PhoneInterface selectedPhone= null;
	private List<PhoneInterface> connectedDevices;
	private DeviceDetectionThread deviceDetectionThread;
	private  AndroidDebugBridge bridge;
	private List<DeviceDetectionListener> deviceDetectionListeners = new ArrayList<DeviceDetectionListener>();
	private static AutomaticPhoneDetection instance=null;
	private static String adbLocation;
	private static String defaultAdbLocation = Platform.getInstance().getDefaultADBLocation();
	private boolean launchThread = true;
	
	public static AutomaticPhoneDetection getInstance(){
		return AutomaticPhoneDetection.getInstance(true);
	}

	public static AutomaticPhoneDetection getInstance(boolean launchThread){
		if(instance ==null) {
			instance = new AutomaticPhoneDetection(launchThread);
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
				if (connectedDevices.get(i) instanceof AndroidPhone) {
					if (!((AndroidPhone) connectedDevices.get(i)).isDisabledPhone())  {
						connectedEnabledDevices.add(connectedDevices.get(i));
					}
				}
				else {
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
		
		//ANDROID Devices detection
		IDevice[] androidDevices = initddmlib();
		for (int i=0 ; i<androidDevices.length ; i++) {
			IDevice androidDevice = androidDevices[i];
			if (androidDevice.isOnline()) {
				boolean found = false;
				for (int j=0; j<connectedDevices.size(); j++) {
					PhoneInterface phone = connectedDevices.get(j);
					String uid = AndroidPhone.getUID(androidDevice);
					String connectedPhoneUid = phone.getUID();
					if (connectedPhoneUid!=null && connectedPhoneUid.equals(uid)) {
						found = true;
						newConnectedDevices.add(phone);
						if (phone.getCnxStatus()!=PhoneInterface.CNX_STATUS_AVAILABLE) {
							phone.setCnxStatus(PhoneInterface.CNX_STATUS_AVAILABLE);
							changed = true;
							if(phone instanceof AndroidMonkeyDriver){
								Logger.getLogger(this.getClass()).info("refresh ddmlib handler for MonkeyDriver enabled device");
								try {
									((AndroidMonkeyDriver)phone).setDevice(androidDevice);
								} catch (PhoneException e) {
									Logger.getLogger(this.getClass()).error("unable to refresh ddmlib handler");
								}
							}
						}
					}
				}
				if (!found) {
					String vendor = AndroidPhone.getVendor(androidDevice);
					if (vendor!=null) vendor = vendor.toLowerCase();
					String model = AndroidPhone.getModel(androidDevice);
					String version = "";
					version = AndroidPhone.getVersion(androidDevice);
					if (model!=null) model = model.toLowerCase();
					if(vendor!=null && model!=null){
						PhoneInterface newPhone;
						try {
							float v=Float.parseFloat(version.substring(0, 3)); 
							Logger.getLogger(this.getClass()).info(v);
							if ( v >= 2.0f){
								if (v >= 4.0f){
									if (v >= 4.1f){
										Logger.getLogger(this.getClass()).info("Android Jelly bean detected !");
										newPhone = new AndroidJBDriver(vendor+"_"+model, version, androidDevice);
									}else{
										Logger.getLogger(this.getClass()).info("Android ICS detected !");
										newPhone = new AndroidICSDriver(vendor+"_"+model, version, androidDevice);
									}
								}else{
									newPhone = new AndroidMonkeyDriver(vendor+"_"+model, version, androidDevice);
								}
							}else{
								newPhone = new AndroidDriver(vendor+"_"+model, version, androidDevice);
							}
							newPhone.setCnxStatus(PhoneInterface.CNX_STATUS_AVAILABLE);
							newConnectedDevices.add(newPhone);
							Logger.getLogger(this.getClass()).info("New phone "+newPhone.getName()+" connected");
							if (!((AndroidPhone)newPhone).isDisabledPhone()) changed = true;
						} catch (PhoneException e) {
							// NOTHING TO DO HERE
						}
					} else {
						PhoneInterface newPhone = new AndroidPhone(androidDevice);
						newPhone.setCnxStatus(PhoneInterface.CNX_STATUS_AVAILABLE);
						newConnectedDevices.add(newPhone);
						Logger.getLogger(this.getClass()).info("New phone "+newPhone.getName()+" connected");
						changed = true;
					}
				}
			}
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
	 * Use adb location set by user in com.android.screenshot.bindir properties
	 * or use default location (<i>Install_dir</i>/AndroidTools/adb.exe)
	 * @return null or Device detected
	 */
	public IDevice[] initddmlib() {
		
		String newAdbLocation = null;
		if (Boolean.valueOf(Configuration.getProperty(Configuration.SPECIFICADB, "false"))) 
			newAdbLocation = Configuration.getProperty(Configuration.ADBPATH)+ Platform.FILE_SEPARATOR+"adb.exe";
		else newAdbLocation = defaultAdbLocation;
		if (bridge==null || !newAdbLocation.equals(adbLocation)) {
			Logger.getLogger(this.getClass()).debug("Initializing ADB bridge : "+newAdbLocation);
		   adbLocation = newAdbLocation;	
		   if (bridge!=null) AndroidDebugBridge.disconnectBridge();
		   AndroidDebugBridge.init(false /* debugger support */);
	
	
	       bridge = AndroidDebugBridge.getBridge();
	       if (bridge==null) bridge = AndroidDebugBridge.createBridge(adbLocation, true );
		
	       if (bridge==null) Logger.getLogger(this.getClass()).debug("bridge is null");
	       //AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(adbLocation, false /* forceNewBridge */);
	       // we can't just ask for the device list right away, as the internal thread getting
	       // them from ADB may not be done getting the first list.
	       // Since we don't really want getDevices() to be blocking, we wait here manually.
	       int count = 0;
	       while (bridge.hasInitialDeviceList() == false) {
	           try {
	               Thread.sleep(100);
	               count++;
	           } catch (InterruptedException e) { }
	           
	           // let's not wait > 10 sec.
	           if (count > 100) {
	               Logger.getLogger(this.getClass() ).warn("Timeout getting device list!");
	               return new IDevice[0];
	           }
	       }     
		}
		return  bridge.getDevices();
	}


	/**
	 * Search the config file path of the phone in parameters.
	 * prefer use {@link #getxmlfilepath()}.
	 * 
	 * @param phoneDefault the phone to find the config file
	 * @return the config file path
	 */
	public String getxmlfilepath()
	{
		String JATKpath = Platform.getInstance().getJATKPath();	
		String xmlconfilepath = JATKpath+Platform.FILE_SEPARATOR+"log"+
		Platform.FILE_SEPARATOR+"ConfigFiles"+Platform.FILE_SEPARATOR;
		
		// Test to determine which config file to use
		PhoneInterface phone = AutomaticPhoneDetection.getInstance().getDevice();
		if (phone != null) {
			xmlconfilepath += phone.getConfigFile();
		} else xmlconfilepath += "se.xml";

		return 	xmlconfilepath;
	}

	public String getADBLocation() {
		return adbLocation;
	}

	/**
	 * close all fabric (like androidDebugBridge)
	 */
	private void close() {
		if (bridge!=null) AndroidDebugBridge.terminate();
	}


}
