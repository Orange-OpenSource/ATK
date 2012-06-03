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
 * File Name   : Do.java
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
 * Represents the XML element <code>Do</code>. This element contains actions that will be performed sequentially.
 * @author penaulau
 * @since JDK5.0
 */
public class Do implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The actions contained in the <code>Do</code> element.
	 */
	private Vector<Action> content;

	/**
	 * Sole constructor.
	 *
	 */
	public Do() {
		content = new Vector<Action>();
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Do(this);
	}

	/**
	 * Returns the actions.
	 * @return vector that contains the actions
	 */
	public Vector<Action> getContent() {
		return content;
	}

	/**
	 * Sets the actions.
	 * @param content the vector that contains the actions
	 */
	public void setContent(Vector<Action> content) {
		this.content = content;
	}

}
