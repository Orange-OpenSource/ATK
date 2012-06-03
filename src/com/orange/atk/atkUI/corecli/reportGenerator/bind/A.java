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
 * File Name   : A.java
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
 * This class represents the <code>A</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class A extends Inline implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the <code>A</code> HTML element.
	 */
	private Vector<Inline> content;
	/**
	 * Represents the target of the link
	 */
	private String href;
	/**
	 * This attribute names the anchor so that it may be the destination of another link.
	 */
	private String name;

	/**
	 * Sole constructor.
	 *
	 */
	public A() {
		super();
		content = new Vector<Inline>();
	}

	/**
	 * Returns the content of the <code>A</code> element.
	 * @return the content of the <code>A</code> element
	 */
	public Vector<Inline> getContent() {
		return content;
	}

	/**
	 * Sets the content of this element
	 * @param content content of this element
	 */
	public void setContent(Vector<Inline> content) {
		this.content = content;
	}

	/**
	 * Returns the target of the link.
	 * @return the target of the link
	 */
	public String getHref() {
		return href;
	}

	/**
	 * Sets the target of this link.
	 * @param href the target of this link
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * Returns the name of this anchor.
	 * @return the name of this anchor.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this anchor.
	 * @param name the name of this anchor.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_A(this);
	}

}
