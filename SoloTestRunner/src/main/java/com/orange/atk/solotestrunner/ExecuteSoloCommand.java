/**
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
 * File Name   :ExecuteSoloCommand.aidl
 *
 * Created     : 02/05/2013
 * Author(s)   : France Telecom
 */
package com.orange.atk.solotestrunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;


public class ExecuteSoloCommand {
	private Solo solo;
	private Class<?> classSolo=null;
	private Method methodToCall=null;
	private static String logTag="ExecuteSoloCommand";

	ExecuteSoloCommand(Solo solo) {
		this.solo=solo;
		classSolo = solo.getClass();
	}


	public boolean execute(String [] commands) {
		String commandName = commands[0];
		char[] stringArray = commandName.toCharArray();
		stringArray[0] = Character.toLowerCase(stringArray[0]);
		commandName = new String(stringArray);
		int  numberOfArgs =Integer.parseInt(commands[1]);
		Object[] args =null;
		Class[] paramTypes=null;
		if(numberOfArgs>0) {
			paramTypes = new Class[numberOfArgs];
			args = new Object[numberOfArgs];
			for(int i=0;i<numberOfArgs;i++) {
				if(commands[2+i].equalsIgnoreCase("string")) {
					paramTypes[i]=String.class;	
					args[i]= commands[2+i+numberOfArgs];
					continue;
				} 
				if(commands[2+i].equalsIgnoreCase("boolean")) {
					paramTypes[i]=float.class;
					args[i]= Boolean.valueOf(commands[2+i+numberOfArgs]);
					continue;
				}
				if(commands[2+i].equalsIgnoreCase("char")) {
					paramTypes[i]=char.class;
					args[i]= (commands[2+i+numberOfArgs]).charAt(0);
					continue;
				} 
				if(commands[2+i].equalsIgnoreCase("byte")) {
					paramTypes[i]=byte.class;

					args[i]= (commands[2+i+numberOfArgs]).getBytes();
					continue;

				} 
				try{
					if(commands[2+i].equalsIgnoreCase("int")) {
						paramTypes[i]=int.class;
							args[i]= Integer.valueOf(commands[2+i+numberOfArgs]);
							continue;
					} 
					if(commands[2+i].equalsIgnoreCase("float")) {
						paramTypes[i]=float.class;
							args[i]= Float.valueOf(commands[2+i+numberOfArgs]);
							continue;
					}  
					if(commands[2+i].equalsIgnoreCase("double")) {
						paramTypes[i]=float.class;
							args[i]= Double.valueOf(commands[2+i+numberOfArgs]);
							continue;
					} 
					if(commands[2+i].equalsIgnoreCase("long")) {
						paramTypes[i]=long.class;
							args[i]= Long.valueOf(commands[2+i+numberOfArgs]);
							continue;
					}
				}catch(NumberFormatException e){
					Log.e(logTag,"unable to parse number",e);
					return false;
				}
			} 
		}
		try {
			methodToCall= classSolo.getMethod(commandName, paramTypes);
		} catch (SecurityException e1) {
			Log.e(logTag,e1.getMessage(),e1);
			return false;
		} catch (NoSuchMethodException e1) {
			Log.e(logTag,e1.getMessage(),e1);
			return false;
		}
		try {
			if(methodToCall!=null) {
				methodToCall.invoke(solo, args);
			}else{
				return false;
			}
		} catch (IllegalArgumentException e) {
			Log.e(logTag,e.getMessage(),e);
			return false;
		} catch (IllegalAccessException e) {
			Log.e(logTag,e.getMessage(),e);
			return false;
		} catch (InvocationTargetException e) {
			Log.e(logTag,e.getMessage(),e);
			return false;
		}
		return true;
	}


}
