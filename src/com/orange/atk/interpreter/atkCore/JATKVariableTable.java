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
 * File Name   : JATKVariableTable.java
 *
 * Created     : 27/11/2008
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.interpreter.atkCore;

import java.util.Hashtable;

/**
 * This class is used to store and manipulate the variables which could appear
 * during the interpretation of a tst file.
 */


public class JATKVariableTable extends Hashtable<String, Variable> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Set the value and type of a variable. If it does not exist, the variable
	 * is created and defined. If the variable previously exists, type and value
	 * are remplaced by String type and value.
	 * 
	 * @param variableName
	 *            name of the variable
	 * @param value
	 *            value of the variable
	 */
	public void addStringVariable(String variableName, String value) {
		put( variableName, Variable.createString(value));
	}

	/**
	 * Set the value and type of a variable. If it does not exist, the variable
	 * is created and defined. If the variable previously exists, type and value
	 * are remplaced by Integer type and value.
	 * 
	 * @param variableName
	 *            name of the variable
	 * @param value
	 *            value of the variable
	 */
	public void addIntegerVariable(String variableName, Integer value) {
		put( variableName, Variable.createInteger(value));
	}

	/**
	 * Indicate if the variable has been previously defined
	 * 
	 * @param variableName
	 *            name of the variable (must start with an "_")
	 * @return true if the variable exists, false otherwise
	 */
	public boolean isVariable(String variableName) {
		if (variableName == null) {
			return false;
		}
		return containsKey(variableName);
	}

	/**
	 * Return the current value for the variable. If the variable does not
	 * exists, null is return. You should check if the variable exists before
	 * trying to use this function
	 * 
	 * @param variableName
	 *            name of the variable (must start with an "_")
	 * @return the current value of the variable, if exists, null otherwise
	 *         (variable does not exists, value == null, variableName does not
	 *         start with _), ...
	 * 
	 */
	public Variable getVariable(String variableName) {
		if (!isVariable(variableName)) {
			return null;
		}
		return get(variableName);
	}
}
