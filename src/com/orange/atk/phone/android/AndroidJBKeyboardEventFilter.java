package com.orange.atk.phone.android;

import java.util.Map;

public class AndroidJBKeyboardEventFilter extends DefaultKeyboardEventfilter {

	public AndroidJBKeyboardEventFilter(AndroidPhone aphone,
			Map<Integer, String> codemap) {
		super(aphone, codemap);
		splitChar = "] ";
	}
}
