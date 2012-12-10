package com.orange.atk.phone.android;

import java.util.List;

import org.apache.log4j.Logger;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.Plugin;
import com.orange.atk.phone.PluginManager;
import com.orange.atk.platform.Platform;

public class AndroidPlugin implements Plugin{
	static {
		PluginManager.register(AndroidPlugin.class.getName());
	}
	private  AndroidDebugBridge bridge;
	private static String defaultAdbLocation = Platform.getInstance().getDefaultADBLocation();
	private static String adbLocation;

	/**
	 * Use adb location set by user in com.android.screenshot.bindir properties
	 * or use default location (<i>Install_dir</i>/AndroidTools/adb.exe)
	 * @return null or Device detected
	 */
	public IDevice[] initddmlib() {

		String newAdbLocation = null;
		if (Boolean.valueOf(Configuration.getProperty(Configuration.SPECIFICADB, "false"))) 
			newAdbLocation = Configuration.getProperty(Configuration.ADBPATH)+ Platform.FILE_SEPARATOR+"adb";
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

	public boolean checkDevices(List<PhoneInterface> connectedDevices, List<PhoneInterface> newConnectedDevices) {
		//ANDROID Devices detection
		boolean changed = false;
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
		return changed;
	}

	public void close() {
		if (bridge!=null) AndroidDebugBridge.terminate();
	}
	
	@Override
	public String getName() {
		return "Android";
	}


}
