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
 * File Name   : B.java
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
 * This class represents the <code>B</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class B extends Inline implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of this HTML font style element.
	 */
	private Vector<Inline> content;

	/**
	 * Default constructor.
	 *
	 */
	public B() {
		content = new Vector<Inline>();
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_B(this);
	}

	/**
	 * Returns the content of this font style element.
	 * @return the content of the font style element
	 */
	public Vector<Inline> getContent() {
		return content;
	}

	/**
	 * Sets the content of the <code>B</code> HTML element.
	 * @param content the content of the <code>B</code> HTML element
	 */
	public void setContent(Vector<Inline> content) {
		this.content = content;
	}

}
