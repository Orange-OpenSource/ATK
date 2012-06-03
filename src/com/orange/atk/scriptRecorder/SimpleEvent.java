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
 * File Name   : SimpleEvent.java
 *
 * Created     : 15/07/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder;

public class SimpleEvent {

	public static String KEYPRESSED="Key_press";
	public static String KEYDOWN="Key_down";
	public static String KEYUP="Key_up";
	public static String MOUSECLICK="Mouse_click";
	public static String MOUSEDOWN="Mouse_down";
	public static String MOUSEUP="Mouse_up";
	public static String BEEP="Beep";
	public static String SCREENSHOT="ScreenShot";
		
	private String typeAction;
	private String params;

	
	public SimpleEvent(String typeAction, String params) {
		super();
		this.typeAction = typeAction;
		this.params = params;
	}
	
	public String getTypeAction() {
		return typeAction;
	}
	
	public void setTypeAction(String typeAction) {
		this.typeAction = typeAction;
	}
	
	public String getParams(int i) {
		String[] str = params.split(",");
		return str[i];
	}
	
	public void setParams(String params) {
		this.params = params;
	}
	
	public String getAllParams() {
		return params;
	}

	public String getInstruction() {
		if (getAllParams().equals("")){
			return getTypeAction();
		}
		return getTypeAction()+"("+getAllParams()+")";
	}
	
	
}
