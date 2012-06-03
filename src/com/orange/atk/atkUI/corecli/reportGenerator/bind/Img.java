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
 * File Name   : Img.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * This class represents the <code>Img</code> HTML element
 * @author penaulau
 * @since JDK5.0
 */
public class Img extends Inline implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Represents the <code>alt</code> attribute of the <code>Img</code> HTML element.
	 */
	private String alt;
	/**
	 * Represents the <code>border</code> attribute of the <code>Img</code> HTML element.
	 */
	private String border;
	/**
	 * Represents the <code>height</code> attribute of the <code>Img</code> HTML element.
	 */
	private String height;
	/**
	 * Represents the <code>src</code> attribute of the <code>Img</code> HTML element.
	 */
	private String src;
	/**
	 * Represents the <code>width</code> attribute of the <code>Img</code> HTML element.
	 */
	private String width;

	/**
	 * Getter for property <code>alt</code>.
	 * @return Value of property <code>alt</code>.
	 */
	public String getAlt() {
		return alt;
	}

	/**
	 * Setter for property <code>alt</code>.
	 * @param alt New Value of property <code>alt</code>.
	 */
	public void setAlt(String alt) {
		this.alt = alt;
	}

	/**
	 * Getter for property <code>border</code>.
	 * @return Value of property <code>border</code>.
	 */
	public String getBorder() {
		return border;
	}

	/**
	 * Setter for property <code>border</code>.
	 * @param border New value of property <code>border</code>.
	 */
	public void setBorder(String border) {
		this.border = border;
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
	 * Getter for property <code>src</code>.
	 * @return Value of property <code>src</code>.
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * Setter for property <code>src</code>.
	 * @param src Nex value of property <code>src</code>.
	 */
	public void setSrc(String src) {
		this.src = src;
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
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Img(this);
	}

}
