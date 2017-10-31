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
 * File Name   : StringUtilities.java
 *
 * Created     : 26/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.File;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class StringUtilities {

	/**
	 * Utility function that checks the suffix of a string
	 * (usually a file name )
	 * @param name string where the suffix is searched
	 * @param suffix the suffix
	 * @return true if the suffix is correct, false otherwise
	 */
	public static boolean checkSuffix(String name, String suffix) {
		int sz = suffix.length();
		return name.regionMatches(true,name.length() - sz, suffix, 0 , sz);
	}


	public static String guessName(String uri, String ext) {
		String name = "";
		if (uri.lastIndexOf(ext)!=-1){
			if (uri.startsWith("http:") && uri.lastIndexOf(ext)>uri.lastIndexOf("/")){
				name = uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf(ext)+3);
			}else if (uri.lastIndexOf(File.separator)!=-1 && uri.lastIndexOf(ext)>uri.lastIndexOf(File.separator)){
				name = uri.substring(uri.lastIndexOf(File.separator)+1, uri.lastIndexOf(ext)+3);
			}else{
				name = uri;
			}
		}else{
			name = uri;
		}
		return name;
	}

}
