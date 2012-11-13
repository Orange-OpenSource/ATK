package com.orange.atk.phone.android;

import java.util.HashMap;
import java.util.Map;

import com.android.ddmlib.IDevice;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.util.Position;

public class AndroidJBDriver extends AndroidICSDriver {

	public AndroidJBDriver(String phoneModel, String version, IDevice d)
			throws PhoneException {
		super(phoneModel, version, d);
	}
	
	protected EventFilter createTouchScreenEventFilter(AndroidDriver d, AndroidConfHandler gestionnaire, HashMap<String,Position> softkeyMap){
		return new AndroidJBTouchScreenEventfilter(d,gestionnaire, softkeyMap);
	}
	
	protected EventFilter createKeyboardEventFilter(AndroidPhone aphone,Map<Integer, String> codemap){
		return new AndroidJBKeyboardEventFilter(aphone, codemap);
	}

}
