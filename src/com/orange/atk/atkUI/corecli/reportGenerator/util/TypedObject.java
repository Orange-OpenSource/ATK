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
 * File Name   : TypedObject.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.util;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Interpreter.SavedObjectType;

/**
 * This class defines typed objects.
 * @author penaulau
 * @since JDK5.0
 */
public class TypedObject {
	/**
	 * The type of the object.
	 */
	private SavedObjectType type;
	/**
	 * The concerned object.
	 */
	private Object object;

	/**
	 * Constructor.
	 * @param type The type of the object.
	 * @param object The concerned object.
	 */
	public TypedObject(SavedObjectType type, Object object) {
		super();
		this.object = object;
		this.type = type;
	}

	/**
	 * Returns the object.
	 * @return The object.
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Setter for the object.
	 * @param object
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * Getter for the property <code>type<code>.
	 * @return Value of property <code>type</code>.
	 */
	public SavedObjectType getType() {
		return type;
	}

	/**
	 * Setter for property <code>type</code>.
	 * @param type New value of property <code>type</code>.
	 */
	public void setType(SavedObjectType type) {
		this.type = type;
	}

}
