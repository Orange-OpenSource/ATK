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
 * File Name   : DetectChannelEventfilter.java
 *
 * Created     : 01/03/2010
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.phone.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Class used to filter output of very common keyboard,
 * 
 * It's found on HTC magic and G1, Samsung SPICA , MOtorola Morisson...
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
class DetectChannelEventfilter extends EventFilter {

	private AndroidDriver phone;
	private String key_p;
	private String key2_p;
	private String key3_p;
	private String touch_p;


	public DetectChannelEventfilter(AndroidDriver aphone, String key_pattern,
			String key2_pattern, String key3_pattern, String touch_pattern) {
		phone = aphone;
		key_p = key_pattern;
		key2_p = key2_pattern;
		key3_p = key3_pattern;
		touch_p = touch_pattern;

	}				

	private Matcher mtc;
	private String channel;


	@Override
	public void processline( String line) {
		detectchannel(line);
		//Stop if both channel are detected
		if((touch_p==null || !phone.MOUSE_CHANNEL_EVENT.equals(""))
				&&(key_p==null || !phone.KEY_CHANNEL_EVENT.equals(""))
				&&(key2_p==null || !phone.KEY_CHANNEL_EVENT2.equals(""))
				&&(key3_p==null || !phone.KEY_CHANNEL_EVENT3.equals("")))
		{	

			this.setCancelled(true);
		}
	}



	public void detectchannel(String line)
	{
		//Check if reach end of parameters 
		if(line.startsWith("/dev"))
		{
			this.setCancelled(true);
		}

		mtc = Pattern.compile("\\s*add device \\s*.*\\s*:\\s*(.*)\\s*").matcher(line);
		if(mtc.matches()) 
		{ 
			channel=mtc.group(1);
		}
		else
		{

			mtc = Pattern.compile("\\s*name:\\s*(.*)\\s*").matcher(line);
			if(mtc.matches()) { 
				//	 Logger.getLogger(this.getClass()).info(("Detected name \""+mtc.group(1)+"\"" ));

				if (key_p != null) {
					if (key_p.equals("")) 
					{
						if ( mtc.group(1).equals("\"\"") && !channel.equals(phone.KEY_CHANNEL_EVENT ))
						{ 
							phone.KEY_CHANNEL_EVENT2=channel;
							Logger.getLogger(this.getClass()).info("keyboard channel "+mtc.group(1)+" :"+phone.KEY_CHANNEL_EVENT);
						} 
					} else if(mtc.group(1).contains(key_p) 
							&& phone.KEY_CHANNEL_EVENT.equals("") )
					{	
						phone.KEY_CHANNEL_EVENT=channel;
						Logger.getLogger(this.getClass()).info("keyboard1 channel "+mtc.group(1)+" :"+phone.KEY_CHANNEL_EVENT);
					}	
				}

				if (key2_p != null) { 
					if (key2_p.equals("")) 
					{
						if ( mtc.group(1).equals("\"\"") && !channel.equals(phone.KEY_CHANNEL_EVENT2))
						{
							phone.KEY_CHANNEL_EVENT2=channel;
							Logger.getLogger(this.getClass()).info("keyboard2 channel "+mtc.group(1)+" :"+phone.KEY_CHANNEL_EVENT2);
						} 
					} else if(mtc.group(1).contains(key2_p )
							&& !channel.equals(phone.KEY_CHANNEL_EVENT ))
					{ 
						phone.KEY_CHANNEL_EVENT2=channel;
						Logger.getLogger(this.getClass()).info("keyboard2 channel "+mtc.group(1)+" :"+phone.KEY_CHANNEL_EVENT2);
					} 
				}

				if (key3_p != null) { 
					if (key3_p.equals("")) 
					{
						if ( mtc.group(1).equals("\"\"") && !channel.equals(phone.KEY_CHANNEL_EVENT3))
						{
							//phone.KEY_CHANNEL_EVENT3=channel;
							Logger.getLogger(this.getClass()).info("keyboard3 channel "+mtc.group(1)+" :"+phone.KEY_CHANNEL_EVENT2);
						} 
					} else if(mtc.group(1).contains(key3_p )
							&& !channel.equals(phone.KEY_CHANNEL_EVENT ))
					{ 
						phone.KEY_CHANNEL_EVENT3=channel;
						Logger.getLogger(this.getClass()).info("keyboard3 channel "+mtc.group(1)+" :"+phone.KEY_CHANNEL_EVENT2);
					} 
				}

				
				
				if (touch_p != null) {
					if (touch_p.equals("")) {
						if ( mtc.group(1).equals("\"\""))
						{ 
							phone.MOUSE_CHANNEL_EVENT=channel;
							Logger.getLogger(this.getClass()).info("touchscreen channel "+mtc.group(1)+" :"+phone.MOUSE_CHANNEL_EVENT);
						} 
					} else if(mtc.group(1).contains(touch_p)){
						phone.MOUSE_CHANNEL_EVENT=channel;
						Logger.getLogger(this.getClass()).info("touchscreen channel "+mtc.group(1)+" :"+phone.MOUSE_CHANNEL_EVENT);
					}
				}
			} 
		}
	}
}