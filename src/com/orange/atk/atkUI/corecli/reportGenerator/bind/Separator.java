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
 * File Name   : Separator.java
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
 * This class represents Html elements which must be inserted in the report between two elements of an iteration.
 * @author penaulau
 * @since JDK5.0
 */
public class Separator implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The HTML content of the operator element.
	 */
	private Vector<HTMLElement> contentOrInline;

	/**
	 * Sole constructor.
	 *
	 */
	public Separator() {
		contentOrInline = new Vector<HTMLElement>();
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Separator(this);
	}

	/**
	 * Getter for vector which contains elements of the separator element.
	 * @return Vector which contains elements of the separator element.
	 */
	public Vector<HTMLElement> getContentOrInline() {
		return contentOrInline;
	}

	/**
	 * Setter for vector which contains elements of the separator element.
	 * @param contentOrInline Vector which contains elements of the separator element.
	 */
	public void setContentOrInline(Vector<HTMLElement> contentOrInline) {
		this.contentOrInline = contentOrInline;
	}
}
