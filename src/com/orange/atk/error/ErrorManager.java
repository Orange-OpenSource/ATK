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
 * File Name   : ErrorManager.java
 *
 * Created     : 15/03/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.error;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.orange.atk.internationalization.ResourceManager;


public class ErrorManager {

	private static ErrorManager instance=null;
	private static List<StringBuffer> errorList = new ArrayList<StringBuffer>();
	private static List<StringBuffer> classList = new ArrayList<StringBuffer>();
	private ErrorFrame errorFrame = new ErrorFrame();
	private List<ErrorListener> errorListeners = new ArrayList<ErrorListener>();
	private static final String EXCEPTION_MESSAGE_PREFIX = ">> ";
	private static final String WARNING_MESSAGE_PREFIX = ResourceManager.getInstance().getString("WARNING")+" ";
	private static final String METHOD_FAILED = " "+ResourceManager.getInstance().getString("METHOD_FAILED");
	
	public static ErrorManager getInstance(){
		if(instance ==null) {
			instance = new ErrorManager();
		}
		return instance;
	}

	//default constructor
	private ErrorManager() {	
	}
	
	public void addErrorListener(ErrorListener listener) {
		errorListeners.add(listener);
	}
	
	public void clear() {
		errorList.clear();
	}

	public int getErrorsNumber() {
		return errorList.size();
	}

	public StringBuffer getLastError() {
		int size = errorList.size();
		if (errorList.size()>0) {
			return(errorList.get(size-1));
		}
		return null;
	}
	
	public StringBuffer getAllErrors() {
		StringBuffer allErrors = new StringBuffer();
		for (int i=errorList.size()-1; i>=0; i--) {
			allErrors.append("\n ");
			if (!classList.get(i).toString().equals("")) allErrors.append(classList.get(i)+": ");
			allErrors.append(errorList.get(i));
		}
		return allErrors;
	}
	
	public void addError(String className, String errorMessage, Exception e) {
		if (e.getMessage()!=null) {
			Logger.getLogger(className).error(e.getMessage());
			addToErrorManager(new StringBuffer(""),new StringBuffer(EXCEPTION_MESSAGE_PREFIX+e.getMessage()));
		}
		else {
			StackTraceElement[] stack = e.getStackTrace();
			if (stack.length>0) addToErrorManager(new StringBuffer(""),new StringBuffer(stack[0].getClassName()+"."+stack[0].getMethodName()+"()"+METHOD_FAILED));
		}
		e.printStackTrace();
		if (errorMessage!=null) addToErrorManager(new StringBuffer(className), new StringBuffer(errorMessage));
		Logger.getLogger(className).error(errorMessage);
		notifyError();
	}

	public void addError(String className, String errorMessage) {
		if (errorMessage!=null) addToErrorManager(new StringBuffer(className),new StringBuffer(errorMessage));
		Logger.getLogger(className).error(errorMessage);
		notifyError();
	}

	public void addWarning(String className, String warningMessage, Exception e) {
		if (e.getMessage()!=null) {
			Logger.getLogger(className).error(e.getMessage());
			addToErrorManager(new StringBuffer(""),new StringBuffer(EXCEPTION_MESSAGE_PREFIX+e.getMessage()));
		}
		else {
			StackTraceElement[] stack = e.getStackTrace();
			if (stack.length>0) addToErrorManager(new StringBuffer(""),new StringBuffer(stack[0].getClassName()+"."+stack[0].getMethodName()+"()"+METHOD_FAILED));
		}
		e.printStackTrace();
		
		if (warningMessage!=null) addToErrorManager(new StringBuffer(className),new StringBuffer(WARNING_MESSAGE_PREFIX+warningMessage));
		Logger.getLogger(className).warn(warningMessage);
		notifyWarning();
	}

	public void addWarning(String className, String warningMessage) {
		if (warningMessage!=null) addToErrorManager(new StringBuffer(className),new StringBuffer(warningMessage));
		Logger.getLogger(className).warn(WARNING_MESSAGE_PREFIX+warningMessage);
		notifyWarning();
	}

	private void addToErrorManager(StringBuffer className, StringBuffer message) {
		classList.add(className);
		errorList.add(message);
	}
	
	public void displayErrorFrame() {
		errorFrame.display();	
	}
	
	public void displayErrorFrame(String title) {
		errorFrame.display(title);
	}
		
	private void notifyError() {
		for (int i=0; i<errorListeners.size(); i++) {
			errorListeners.get(i).errorOccured();
		}
	}
	private void notifyWarning() {
		for (int i=0; i<errorListeners.size(); i++) {
			errorListeners.get(i).warningOccured();
		}
	}

	
}
