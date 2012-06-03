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
 * File Name   : Font.java
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
 * This class represents the <code>Font</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class Font extends Inline implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the <code>Font</code> HTML element.
	 */
	private Vector<Inline> content;
	/**
	 * Represents the value of the attribute <code>color</code> of the <code>Font</code> HTML element.
	 */
	private String color;
	/**
	 * Represents the value of the attribute <code>face</code> of the <code>Font</code> HTML element.
	 */
	private String face;
	/**
	 * Represents the value of the attribute <code>size</code> of the <code>Font</code> HTML element.
	 */
	private String size;

	/**
	 * Sole constructor.
	 *
	 */
	public Font() {
		content=new Vector<Inline>();
	}

	/**
	 * Returns the value of the <code>Color</code> attribute.
	 * @return the value of the <code>Color</code> attribute
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Sets the value of the <code>Color</code> attribute.
	 * @param color the value of the <code>Color</code> attribute
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Returns the value of the <code>Face</code> attribute.
	 * @return the value of the <code>Face</code> attribute
	 */
	public String getFace() {
		return face;
	}

	/**
	 * Sets the value of the <code>Face</code> attribute.
	 * @param face the value of the <code>Face</code> attribute
	 */
	public void setFace(String face) {
		this.face = face;
	}

	/**
	 * Returns the value of the <code>Size</code> attribute.
	 * @return the value of the <code>Size</code> attribute
	 */
	public String getSize() {
		return size;
	}

	/**
	 * Sets the value of the <code>Size</code> attribute.
	 * @param size the value of the <code>Size</code> attribute
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Font(this);
	}

	public Vector<Inline> getContent() {
		return content;
	}

	public void setContent(Vector<Inline> content) {
		this.content = content;
	}
}
