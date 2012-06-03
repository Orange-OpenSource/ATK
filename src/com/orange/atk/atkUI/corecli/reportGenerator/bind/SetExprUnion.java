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
 * File Name   : SetExprUnion.java
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
 * Represents the union of sets.
 * @author penaulau
 * @since JDK5.0
 */
public class SetExprUnion extends SetExpr implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The vector which contains the sets.
	 */
	private Vector<Expr> content;

	/**
	 * Sole constructor.
	 *
	 */
	public SetExprUnion() {
		content = new Vector<Expr>();
	}

	/**
	 * Getter for vector which contains sets.
	 * @return Vector which contains sets.
	 */
	public Vector<Expr> getContent() {
		return content;
	}

	/**
	 * Setter for vector which contains sets.
	 * @param content Vector which contains sets.
	 */
	public void setContent(Vector<Expr> content) {
		this.content = content;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Union(this);
	}

}
