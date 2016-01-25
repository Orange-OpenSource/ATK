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
 * File Name   : PhoneException.java
 *
 * Created     : 04/08/2008
 * Author(s)   : Guillaume Chatelet
 */
package com.orange.atk.phone;

import org.apache.log4j.Logger;

public class PhoneException extends Exception {

	private static final long serialVersionUID = 1L;

	public PhoneException() {
		super();
	}

	public PhoneException(String message) {
		super(message);
		Logger.getLogger(this.getClass() ).debug(message);
	}
	
}
