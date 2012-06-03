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
 * File Name   : Elseif.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents the XML element <code>Else</code>. This element represents the performed actions if
 * the condition of the conditional structure is evaluated to false. This element introduces a
 * new conditional structure.
 * @author penaulau
 * @since JDK5.0
 */
public class Elseif implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The boolean expression or variable which
	 * represent a boolean expression.
	 */
	private Expr expr;
	/**
	 * Element which contains actions if the boolean expression is evaluated to true.
	 */
	private Then then;
	/**
	 * Element which contains actions if the boolean expression is evaluated to false.
	 */
	private Else else1;
	/**
	 * Element which contains actions if the boolean expression is evaluated to false.
	 */
	private Elseif elseif;

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Elseif(this);
	}

	/**
	 * Returns the boolean expression.
	 * @return the boolean expression
	 */
	public Expr getExpr() {
		return expr;
	}

	/**
	 * Sets the boolean expression.
	 * @param expr the boolean expression
	 */
	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	/**
	 * Returns actions if the condition is evaluated to false.
	 * @return the <code>Else</code> element
	 */
	public Else getElse1() {
		return else1;
	}

	/**
	 * Sets actions if the condition is evaluated to false.
	 * @param else1 the <code>Else</code> element
	 */
	public void setElse1(Else else1) {
		this.else1 = else1;
	}

	/**
	 * Returns actions if the condition is evaluated to false.
	 * @return the <code>Elseif</code> element
	 */
	public Elseif getElseif() {
		return elseif;
	}

	/**
	 * Sets actions if the condition is evaluated to false.
	 * @param elseif the <code>Elseif</code> element
	 */
	public void setElseif(Elseif elseif) {
		this.elseif = elseif;
	}

	/**
	 * Returns actions if the condition is evaluated to true.
	 * @return the <code>Then</code> element
	 */
	public Then getThen() {
		return then;
	}

	/**
	 * Sets actions if the condition is evaluated to true.
	 * @param then the <code>Then</code> element
	 */
	public void setThen(Then then) {
		this.then = then;
	}

}
