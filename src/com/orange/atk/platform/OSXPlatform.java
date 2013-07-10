package com.orange.atk.platform;

public class OSXPlatform extends Platform {

	@Override
	public String getJATKPath() {
		return ".";
	}

	@Override
	public String getDefaultADBLocation() {
		// suppose it is in the PATH for now
		return "adb";
	}

}
