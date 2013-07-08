package com.orange.atk.phone;

import java.util.List;

import com.android.ddmlib.AndroidDebugBridge;

public interface Plugin {
	public String getName();
	public boolean checkDevices(List<PhoneInterface> connectedDevices, List<PhoneInterface> newConnectedDevices);
	public void close();
	public AndroidDebugBridge getAdb();
}
