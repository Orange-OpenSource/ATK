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
 * File Name   : AndroidICSDriver.java
 *
 * Created     : 06/03/2012
 * Author(s)   : Pierre Crepieux
 */
package com.orange.atk.phone.android;

import com.android.ddmlib.IDevice;
import com.orange.atk.phone.PhoneException;

public class AndroidICSDriver extends AndroidMonkeyDriver {

	public AndroidICSDriver(String phoneModel, String version, IDevice d) throws PhoneException {
		super(phoneModel, version, d);
	}

	@Override
	public void keyPress(String key, int keyPressTime, int delay)
			throws PhoneException {
		super.keyPress(key, keyPressTime, delay);
		if (keyCanal.get(key).equals("keyboard")) executeShellCommand("sendevent "+KEY_CHANNEL_EVENT+" 0 "+0+" 0", false);
		else if (keyCanal.get(key).equals("keyboard2")) executeShellCommand("sendevent "+KEY_CHANNEL_EVENT2+" 0 "+0+" 0", false);
		else executeShellCommand("sendevent "+KEY_CHANNEL_EVENT3+" 0 "+0+" 0", false);
	}
	
}
