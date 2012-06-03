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
 * File Name   : ResourceManager.java
 *
 * Created     : 12/03/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.internationalization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;


public class ResourceManager {
	private static ResourceManager instance=null;
	private Locale locale = null;
	private ResourceBundle bundle;
	private static final String UNKNOWN_WORD = "N/A";
	private String packagePath;
	
	public static ResourceManager getInstance(){
		if(instance ==null) {
			new ResourceManager();
		}
		return instance;
	}

	private ResourceManager() {
		instance = this;
		locale = Locale.getDefault();
		packagePath = this.getClass().getName().substring(0,this.getClass().getName().lastIndexOf("."));
		bundle = ResourceBundle.getBundle(packagePath+".wording", locale);
	}
	
	public void setLocale(String language, String country) {
		locale = new Locale(language, country);
		bundle = ResourceBundle.getBundle(packagePath+".wording", locale);
	}
	
	public String getString(String key) {
		String result = null;
		try {
			result = bundle.getString(key);	
		} catch (Exception e) {e.printStackTrace();}
		if (result==null) return UNKNOWN_WORD;
		return result;
	}
	public String getString(String key, String arg1) {
		return getString(key,new String[]{arg1});
	}
	public String getString(String key, String arg1, String arg2) {
		return getString(key,new String[]{arg1, arg2});
	}

	public String getString(String key, String[] args) {
		String pattern = null;
		try {
			pattern = bundle.getString(key);	
		} catch (Exception e) {e.printStackTrace();}
		if (pattern==null) return UNKNOWN_WORD;
		MessageFormat format = new MessageFormat(pattern);
		String result = format.format(args);
		if (result==null) return UNKNOWN_WORD;
		return result;
	
	}
}
