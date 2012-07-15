package com.orange.atk.monitor;

import com.orange.atk.monitor.IATKMonitorEventListener;


interface IATKMonitorCom {
    String getMem();
    String getCPU();
    String getConnection();
    void stop();

	// Add a listener for receiving callback events
	void addEventListener(in IATKMonitorEventListener listener);

	// Remove a listener
	void removeEventListener(in IATKMonitorEventListener listener);
	
	// Remove all listeners
	void removeAllEventListeners();
}
