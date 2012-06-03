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
 * File Name   : Then.java
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
 * Represents treatments in a conditional structure where the condition is evaluated to true.
 * @author penaulau
 * @since JDK5.0
 */
public class Then implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The performed actions.
	 */
	private Vector<Action> actions;

	/**
	 * Sole constructor.
	 *
	 */
	public Then() {
		actions = new Vector<Action>();
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Then(this);
	}

	/**
	 * Getter for vector which contains actions.
	 * @return Vector which contains actions.
	 */
	public Vector<Action> getActions() {
		return actions;
	}

	/**
	 * Setter for vector which contains actions.
	 * @param actions Vector which contains actions.
	 */
	public void setActions(Vector<Action> actions) {
		this.actions = actions;
	}

}
