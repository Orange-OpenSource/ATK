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
 * File Name   : BoolExprExists.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents an element of the MAP language that allows knowing if it exists an element
 * of the set defined in the <code>In</code> clause that verifies the given boolean expression.
 * @author penaulau
 * @since JDK5.0
 */
public class BoolExprExists extends BoolExpr implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Represents the tested set.
	 */
	private In in;
	/**
	 * Represents a boolean expression or a variable
	 * which represents a boolean expression.
	 */
	private Expr expr;
	/**
	 * Gives the name of a variable whose its value is a boolean expression.
	 */
	private String var;

	/**
	 * Returns the boolean expression.
	 * @return boolean expression
	 */
	public Expr getExpr() {
		return expr;
	}

	/**
	 * Sets the boolean expression.
	 * @param boolExpr the boolean expression
	 */
	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	/**
	 * Returns the set.
	 * @return the set
	 */
	public In getIn() {
		return in;
	}

	/**
	 * Sets the set.
	 * @param in the set
	 */
	public void setIn(In in) {
		this.in = in;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Exists(this);
	}

	/**
	 * Returns the name of the variable.
	 * @return the name of the variable
	 */
	public String getVar() {
		return var;
	}

	/**
	 * Sets the name of the variable.
	 * @param var the name of the variable.
	 */
	public void setVar(String var) {
		this.var = var;
	}

}
