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
 * File Name   : Td.java
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
 * This class represents the <code>Td</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class Td implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the <code>Table</code> HTML element.
	 */
	private Vector<HTMLElement> content;
	/**
	 * Represents the <code>align</code> attribute of the <code>Td</code> HTML element.
	 */
	private String align;
	/**
	 * Represents the <code>valign</code> attribute of the <code>Td</code> HTML element.
	 */
	private String valign;
	/**
	 * Represents the <code>height</code> attribute of the <code>Td</code> HTML element.
	 */
	private String height;
	/**
	 * Represents the <code>width</code> attribute of the <code>Td</code> HTML element.
	 */
	private String width;
	/**
	 * Represents the <code>rowspan</code> attribute of the <code>Td</code> HTML element.
	 */
	private String rowspan;
	/**
	 * Represents the <code>colspan</code> attribute of the <code>Td</code> HTML element.
	 */
	private String colspan;

	/**
	 * Sole constructor.
	 *
	 */
	public Td() {
		content = new Vector<HTMLElement>();
	}

	/**
	 * Getter for property <code>colspan</code>.
	 * @return Value of property <code>colspan</code>.
	 */
	public String getColspan() {
		return colspan;
	}

	/**
	 * Setter for property <code>colspan</code>.
	 * @param colspan New value of property <code>colspan</code>.
	 */
	public void setColspan(String colspan) {
		this.colspan = colspan;
	}

	/**
	 * Getter for property <code>height</code>.
	 * @return Value of property <code>height</code>.
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * Setter for property <code>height</code>.
	 * @param height New value of property <code>height</code>.
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * Getter for property <code>rowspan</code>.
	 * @return Value of property <code>rowspan</code>.
	 */
	public String getRowspan() {
		return rowspan;
	}

	/**
	 * Setter for property <code>rowspan</code>.
	 * @param rowspan New value of property <code>rowspan</code>.
	 */
	public void setRowspan(String rowspan) {
		this.rowspan = rowspan;
	}

	/**
	 * Getter for property <code>width</code>.
	 * @return Value of property <code>width</code>.
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * Setter for property <code>width</code>.
	 * @param width New value of property <code>width</code>.
	 */
	public void setWidth(String width) {
		this.width = width;
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
	 * Returns the content of the <code>Td</code> element.
	 * @return the content of the <code>Td</code> element
	 */
	public Vector<HTMLElement> getContent() {
		return content;
	}

	/**
	 * Sets the content of the <code>Td</code> element.
	 * @param content the content of the <code>Td</code> element
	 */
	public void setContent(Vector<HTMLElement> content) {
		this.content = content;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Td(this);
	}

}
