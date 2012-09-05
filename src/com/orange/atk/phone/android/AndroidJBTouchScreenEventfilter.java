package com.orange.atk.phone.android;

import java.util.HashMap;

import com.orange.atk.util.Position;

public class AndroidJBTouchScreenEventfilter extends
		DefaultTouchScreenEventfilter {

	public AndroidJBTouchScreenEventfilter(AndroidDriver aphone,
			AndroidConfHandler ges, HashMap<String, Position> softkeyMap) {
		super(aphone, ges, softkeyMap);
		// TODO Auto-generated constructor stub
	}


	protected String splitChar(){
		return "] ";
	}

	protected Long parseTimestamp(String s){
		String timestr = s.substring(1).replaceAll(" ","");		
		String timeTable[] = timestr.split("\\.");
		Long time = Long.parseLong(timeTable[0])*1000+Long.parseLong(timeTable[1])/1000;
		return time;
	}
}
