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
 * File Name   : Table.java
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
 * This class represents the <code>Table</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class Table extends Content implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the <code>Table</code> HTML element.
	 */
	private Vector<Tr> content;
	/**
	 * Represents the <code>height</code> attribute of the <code>Table</code> HTML element.
	 */
	private String height;
	/**
	 * Represents the <code>width</code> attribute of the <code>Table</code> HTML element.
	 */
	private String width;
	/**
	 * Represents the <code>align</code> attribute of the <code>Table</code> HTML element.
	 */
	private String align;
	/**
	 * Represents the <code>cellspacing</code> attribute of the <code>Table</code> HTML element.
	 */
	private String cellspacing;

	/**
	 * Sole constructor.
	 *
	 */
	public Table() {
		content = new Vector<Tr>();
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
	 * Getter for property <code>cellspacing</code>.
	 * @return Value of property <code>cellspacing</code>.
	 */
	public String getCellspacing() {
		return cellspacing;
	}

	/**
	 * Setter for property <code>cellspacing</code>.
	 * @param cellspacing New value of property <code>cellspacing</code>.
	 */
	public void setCellspacing(String cellspacing) {
		this.cellspacing = cellspacing;
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
	 * Returns the content of the <code>Table</code> element.
	 * @return the content of the <code>Table</code> element
	 */
	public Vector<Tr> getContent() {
		return content;
	}

	/**
	 * Sets the content of the <code>Table</code> element.
	 * @param content the content of the <code>Table</code> element
	 */
	public void setContent(Vector<Tr> content) {
		this.content = content;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Table(this);
	}
}
