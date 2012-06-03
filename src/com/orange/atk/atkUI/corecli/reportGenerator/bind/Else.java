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
 * File Name   : Else.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;
import java.util.Vector;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents the XML element <code>Else</code>. This element represents the performed actions if
 * the condition of the conditional structure is evaluated to false.
 * @author penaulau
 * @since JDK5.0
 */
public class Else implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The vector which contains the performed actions.
	 */
	private Vector<Action> actions;

	/**
	 * Sole constructor.
	 *
	 */
	public Else() {
		actions = new Vector<Action>();
	}

	/**
	 * Returns the vector which contains performed actions.
	 * @return the vector which contains performed actions
	 */
	public Vector<Action> getActions() {
		return actions;
	}

	/**
	 * Sets the vector which contains performed actions.
	 * @param actions the vector which contains performed actions
	 */
	public void setActions(Vector<Action> actions) {
		this.actions = actions;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Else(this);
	}

}
