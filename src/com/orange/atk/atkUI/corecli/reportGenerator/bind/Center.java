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
 * File Name   : Center.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.util.Vector;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * This class represents the <code>center</code> HTML element
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class Center {

	/**
	 * The content of the <code>center</code> HTML element.
	 */
	private Vector<HTMLElement> content;

	/**
	 * Sole constructor.
	 *
	 */
	public Center() {
		content = new Vector<HTMLElement>();
	}

	/**
	 * Returns the content of the <code>center</code> element.
	 * @return the content of the <code>center</code> element
	 */
	public Vector<HTMLElement> getContent() {
		return content;
	}

	/**
	 * Sets the content of the <code>center</code> element.
	 * @param content the content of the <code>center</code> element
	 */
	public void setContent(Vector<HTMLElement> content) {
		this.content = content;
	}

	/**
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Center(this);
	}

}
