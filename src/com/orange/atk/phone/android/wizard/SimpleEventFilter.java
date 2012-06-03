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
 * File Name   : SimpleEventFilter.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android.wizard;

import com.orange.atk.phone.android.EventFilter;

public class SimpleEventFilter extends EventFilter implements TimeOutListener {
	ChannelPanel detectionPanel;
	EventTimerThread eventTimerThread;
	StringBuffer traces = new StringBuffer();
	boolean done = true;
	
	public SimpleEventFilter(ChannelPanel detectionPanel) {
		this.detectionPanel = detectionPanel;
		eventTimerThread = new EventTimerThread(this, 300);
		eventTimerThread.start();
	}
	
	@Override
	public void processline( String line) {
		if (done) {
			traces.delete(0, traces.length());
			done = false;
		} 
		traces.append(line+"\n");
		detectionPanel.notifyChannelEvent();
		eventTimerThread.newEventTime(System.currentTimeMillis());
	}

	public void setCancelled(boolean cancel) {
		super.setCancelled(cancel);
		eventTimerThread.stopRunning();
	}

	public void notifyTimeOut() {
		detectionPanel.notifyChannelNoEvent();
		done = true;
	}

	public StringBuffer getTraces() {
		return traces;
	}
}
