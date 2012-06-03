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
 * File Name   : LicenceException.java
 *
 * Created     : 22/06/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.corecli;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class LicenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public LicenceException(String msg) {
    	super(msg);
    }

}
