/*
 * Software Name : ATK
 *
 * Copyright (C) 2007 - 2012 France TÃ©lÃ©com
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
 * File Name   : Variable.java
 *
 * Created     : 27/11/2008
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.interpreter.atkCore;

import java.util.List;

/**
 * This class represents a variable created in the test script.
 */

public class Variable {
	private static final int STRING_TYPE = 0;
	private static final int INT_TYPE = 1;
	private static final int TABLE_TYPE = 1;

	private static final int FLOAT_TYPE = 3;
	private static final int BOOLEAN_TYPE = 4;

	private int _type;
	private Object _value;

	protected Variable(int type, Object value) {
		_type = type;
		_value = value;
	}

	/**
	 * Create a new variable of type string.
	 * 
	 * @param value
	 *            value of the variable
	 * @return a new variable with type String and value value
	 * @throws NullPointerException
	 *             if value is null
	 */
	public static Variable createString(String value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException();
		}
		return new Variable(STRING_TYPE, value);
	}

	/**
	 * Create a new variable of type integer.
	 * 
	 * @param value
	 *            value of the variable
	 * @return a new variable with type Integer and value value
	 * @throws NullPointerException
	 *             if value is null
	 */
	public static Variable createInteger(Integer value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException();
		}
		return new Variable(INT_TYPE, value);
	}

	/**
	 * Create a new variable of type Float.
	 * 
	 * @param value
	 *            value of the variable
	 * @return a new variable with type Float and value value
	 * @throws NullPointerException
	 *             if value is null
	 */
	public static Variable createFloat(Float value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException();
		}
		return new Variable(FLOAT_TYPE, value);
	}
	/**
	 * Create a new variable of type Boolean.
	 * 
	 * @param value
	 *            value of the variable
	 * @return a new variable with type Boolean and value value
	 * @throws NullPointerException
	 *             if value is null
	 */
	public static Variable createBoolean(Boolean value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException();
		}
		return new Variable(BOOLEAN_TYPE, value);
	}
	/**
	 * Create a new variable of type Table.
	 * 
	 * @param value
	 *            value of the variable (a List of variable
	 * @return a new variable with type Integer and value value
	 * @throws NullPointerException
	 *             if value is null
	 */
	public static Variable createTable(List<Variable> value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException();
		}
		return new Variable(TABLE_TYPE, value);
	}

	/**
	 * Used to test if the current type of the variable is String
	 * 
	 * @return true if type is String, false otherwise
	 */
	public boolean isString() {
		return (_type == STRING_TYPE);
	}

	/**
	 * Used to test if the current type of the variable is Integer
	 * 
	 * @return true if type is Integer, false otherwise
	 */
	public boolean isInteger() {
		return (_type == INT_TYPE);
	}

	/**
	 * Used to test if the current type of the variable is Float
	 * 
	 * @return true if type is Float, false otherwise
	 */
	public boolean isFloat() {
		return (_type == FLOAT_TYPE);
	}
	/**
	 * Used to test if the current type of the variable is Boolean
	 * 
	 * @return true if type is Boolean, false otherwise
	 */
	public boolean isBoolean() {
		return (_type == BOOLEAN_TYPE);
	}
	/**
	 * Used to test if the current type of the variable is Table
	 * 
	 * @return true if type is Table, false otherwise
	 */
	public boolean isTable() {
		return (_type == TABLE_TYPE);
	}

	/**
	 * Get the value of the variable. The type of the variable must be String
	 * type.
	 * 
	 * @return the String value of the variable.
	 * @throws ClassCastException
	 *             if the type is not String
	 * @see #isString
	 */
	public String getString() throws ClassCastException {
		if (isString()) {
			String returned_value = (String) _value;
			returned_value = returned_value.replaceAll("'", "");
			return (String) returned_value;
		} else {
			throw new ClassCastException();
		}

	}

	/**
	 * Get the value of the variable. The type of the variable must be String
	 * type.
	 * 
	 * @return the String value of the variable.
	 * @throws ClassCastException
	 *             if the type is not String
	 * @see #isString
	 */
	public List<Variable> getTable() throws ClassCastException {
		if (isTable()) {
			return (List<Variable>) _value;
		} else {
			throw new ClassCastException();
		}
	}
	/**
	 * Get the value of the variable. The type of the variable must be Integer
	 * type.
	 * 
	 * @return the Integer value of the variable.
	 * @throws ClassCastException
	 *             if the type is not Integer
	 * @see #isInteger
	 */
	public Integer getInteger() {
		if (isInteger()) {
			return (Integer) _value;
		} else {
			throw new ClassCastException();
		}
	}

	/**
	 * Get the value of the variable. The type of the variable must be Float
	 * type.
	 * 
	 * @return the Float value of the variable.
	 * @throws ClassCastException
	 *             if the type is not Float
	 * @see #isFloat
	 */
	public float getFloat() throws ClassCastException {
		if (isFloat()) {
			float returned_value = (Float) _value;
			return (float) returned_value;
		} else {
			throw new ClassCastException();
		}

	}
	/**
	 * Get the value of the variable. The type of the variable must be Boolean
	 * type.
	 * 
	 * @return the Boolean value of the variable.
	 * @throws ClassCastException
	 *             if the type is not Boolean
	 * @see #isBoolean
	 */

	public boolean getBoolean() throws ClassCastException {
		if (isBoolean()) {
			Boolean returned_value = (Boolean) _value;
			return (Boolean) returned_value;
		} else {
			throw new ClassCastException();
		}

	}

	public String get_type() {

		switch (_type) {
			case 0 :
				return "string";
			case 1 :
				return "int";
			case 3 :
				return "float";
			case 4 :
				return "boolean";
			default :
				break;
		}
		return null;
	}
	public String get_value() {
		if (isString()) {
			String returned_value = (String) _value;
			returned_value = returned_value.replaceAll("'", "");
			return (String) returned_value;
		}
		return String.valueOf(_value);
	}

}
