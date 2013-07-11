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

	@Override
	public String getBuildApk() {
		// TODO Auto-generated method stub
		return "buildApk";
	}

	@Override
	public String getRemoveSignature() {
		// TODO Auto-generated method stub
		return "removeSignature";
	}

	@Override
	public String getZipalignLocation() {
		// TODO Auto-generated method stub
		return "zipalign";
	}

	@Override
	public String getAtkKeyLocation() {
		// TODO Auto-generated method stub
		return "ATKKey.keystore";
	}

}
