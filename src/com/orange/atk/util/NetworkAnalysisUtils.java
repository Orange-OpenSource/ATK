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
 * File Name   : NetworkAnalysisUtils.java
 *
 * Created     : 24/11/2011
 * Author(s)   : Rcheze Ext
 */
package com.orange.atk.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NetworkAnalysisUtils {
	
	public final static String regexToMatch = "\\b\\d{2}:\\d{2}:\\d{2}\\.\\d{6} .*";
	
	/**
	 * Extract Date from tcpdump line
	 * @param input tcpdump line
	 * @return the Date object or null if input does not match required pattern
	 */
	public static Date extractTcpdumpLineDate(String input){
		boolean matches = input.matches(regexToMatch);
		if (matches == false){
			return null;
		}
		String[] values = input.split(" ");
		String date = values[0];
		
		date = date.replaceAll("\\.", ":");
		String[] time = date.split(":");
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.MINUTE,new Integer(time[1]));
		calendar.set(Calendar.SECOND,new Integer(time[2]));
		calendar.set(Calendar.MILLISECOND,new Integer(time[3].substring(0, 3)));
		return calendar.getTime();
	}
	
	/**
	 * Extract Url from tcpdump line
	 * @param input tcpdump line
	 * @return the url as String or null if input does not match required pattern
	 */
	public static String extractTcpdumpLineUrl(String input){
		boolean matches = input.matches(regexToMatch);
		if (matches == false){
			return null;
		}
		String[] values = input.split(" ");
		return values[1];
	}
	
	public static void main(String[] args) {
		String toTest = "08:01:04.123456 www.google.fr";
		System.out.println(toTest.matches(regexToMatch));
	}
	
}
