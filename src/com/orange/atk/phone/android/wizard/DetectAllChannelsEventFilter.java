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
 * File Name   : DetectAllChannelsEventFilter.java
 *
 * Created     : 13/08/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.phone.android.wizard;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.orange.atk.phone.android.EventFilter;

public class DetectAllChannelsEventFilter extends EventFilter implements TimeOutListener{
	private Hashtable<String,String> detectedChannels;
	private Hashtable<String,StringBuffer> channelEvents;
	private Matcher mtc;
	private String channel;
	private String name;
	EventTimerThread eventTimerThread;
	private boolean memorizeEvents = false;
	private StringBuffer eventsDescription = new StringBuffer();

	public DetectAllChannelsEventFilter(Hashtable<String,String> detectedChannels, Hashtable<String,StringBuffer> channelEvents) {
		this.detectedChannels = detectedChannels;
		this.channelEvents = channelEvents;
		eventTimerThread = new EventTimerThread(this,300);
		eventTimerThread.start();
	}				

	@Override
	public void processline( String line) {
		detectchannel(line);
		eventTimerThread.newEventTime(System.currentTimeMillis());
	}



	public void detectchannel(String line)
	{
		//Logger.getLogger(this.getClass()).info("*** "+line);
		
		//Check if reach end of parameters 
		if(line.startsWith("/dev"))
		{
			this.setCancelled(true);
			eventTimerThread.stopRunning();
		}

		mtc = Pattern.compile("\\s*add device \\s*.*\\s*:\\s*(.*)\\s*").matcher(line);
		if(mtc.matches()) 
		{ 
			channel=mtc.group(1);
			memorizeEvents= false;
			if (name!=null) {
				channelEvents.put(name,eventsDescription);
				eventsDescription = new StringBuffer();
			}
		}
		else
		{

			mtc = Pattern.compile("\\s*name:\\s*(.*)\\s*").matcher(line);
			if(mtc.matches()) { 
				//	 Logger.getLogger(this.getClass()).info(("Detected name \""+mtc.group(1)+"\"" ));
				name = mtc.group(1);
				detectedChannels.put(name, channel);
			}  else {
				mtc = Pattern.compile("\\s*events:\\s*").matcher(line);
				if(mtc.matches()) {
					memorizeEvents = true;
				} else if (memorizeEvents) {
					eventsDescription.append(line+"\n");
				}
			}
		}
	}

	public void notifyTimeOut() {
		this.setCancelled(true);
		eventTimerThread.stopRunning();
		if (name!=null) {
			channelEvents.put(name,eventsDescription);
			eventsDescription = new StringBuffer();
		}
	}

}
