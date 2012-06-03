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
 * File Name   : Tr.java
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
 * This class represents the <code>Tr</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class Tr implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the <code>Tr</code> HTML element.
	 */
	private Vector<Td> content;
	/**
	 * Represents the <code>align</code> attribute of the <code>Tr</code> HTML element.
	 */
	private String align;
	/**
	 * Represents the <code>valign</code> attribute of the <code>Tr</code> HTML element.
	 */
	private String valign;

	/**
	 * Sole constructor.
	 *
	 */
	public Tr() {
		content = new Vector<Td>();
	}

	/**
	 * Getter for property <code>align</code>.
	 * @return Value of property <code>align</code>.
	 */
	public String getAlign() {
		return align;
	}

	/**
	 * Setter for property <code>align</code>.
	 * @param align New value of property <code>align</code>.
	 */
	public void setAlign(String align) {
		this.align = align;
	}

	/**
	 * Getter for property <code>valign</code>.
	 * @return Value of property <code>valign</code>.
	 */
	public String getValign() {
		return valign;
	}

	/**
	 * Setter for property <code>valign</code>.
	 * @param valign New value of property <code>valign</code>.
	 */
	public void setValign(String valign) {
		this.valign = valign;
	}

	/**
	 * Returns the content of the <code>Tr</code> element.
	 * @return the content of the <code>Tr</code> element
	 */
	public Vector<Td> getContent() {
		return content;
	}

	/**
	 * Sets the content of the <code>Tr</code> element.
	 * @param content the content of the <code>Tr</code> element
	 */
	public void setContent(Vector<Td> content) {
		this.content = content;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Tr(this);
	}

}
