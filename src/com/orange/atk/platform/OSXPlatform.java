package com.orange.atk.platform;

import org.apache.log4j.Logger;

import java.io.File;

public class OSXPlatform extends Platform {

	@Override
	public String getJATKPath() {
		return ".";
	}

	@Override
	public String getDefaultADBLocation() {
        String sdkPath = System.getenv("ANDROID_HOME");
        if(sdkPath == null){
            sdkPath="/Applications/Android Studio.app/sdk";
        }
        String path=sdkPath+Platform.FILE_SEPARATOR+"platform-tools/";
		return path+"adb";
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

	@Override
	public String getUserConfigDir() {
        String homePath = System.getenv("HOME");
        String userConfigDirPath = homePath + Platform.FILE_SEPARATOR + ".atk";
        Logger.getLogger(OSXPlatform.class).debug("HOME is : "+userConfigDirPath);
        return userConfigDirPath;
	}

}
