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
 * File Name   : P.java
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
 * This class represents the <code>P</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class P extends Content implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the <code>P</code> HTML element.
	 */
	private Vector<Inline> content;

	/**
	 * Sole constructor.
	 *
	 */
	public P() {
		content = new Vector<Inline>();
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_P(this);
	}

	/**
	 * Returns the content of the <code>P</code> element.
	 * @return the content of the <code>P</code> element
	 */
	public Vector<Inline> getContent() {
		return content;
	}

	/**
	 * Sets the content of the <code>P</code> element.
	 * @param content the content of the <code>P</code> element
	 */
	public void setContent(Vector<Inline> content) {
		this.content = content;
	}

}
