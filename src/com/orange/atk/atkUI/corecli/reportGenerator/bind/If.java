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
 * File Name   : If.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a conditional construct.
 * @author penaulau
 * @since JDK5.0
 */
public class If extends Action implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Represents the condition.
	 */
	private Expr expr;
	/**
	 * Represents a variable which contains a boolean expression.
	 */
	private Var var;

	/**
	 * Represents the XML element which contains the applied directive if the condition is evaluated to true.
	 */
	private Then then;
	/**
	 * If the condition is evaluated to false, the else1 attribute represents the performed treatments.
	 */
	private Else else1;
	/**
	 * If the condition is evaluated to false, the elseif attribute represents the performed treatments with another
	 * conditional construct.
	 */
	private Elseif elseif;

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_If(this);
	}

	/**
	 * Gets the {@link reportGenerator.bind.Else} used in this clause.
	 * @return <code>Else</code> that will be used.
	 */
	public Else getElse1() {
		return else1;
	}

	/**
	 * Sets the {@link reportGenerator.bind.Else} used in this clause.
	 * @param else1 <code>Else</code> to be used.
	 */
	public void setElse1(Else else1) {
		this.else1 = else1;
	}

	/**
	 * Gets the {@link reportGenerator.bind.Elseif} used in this clause.
	 * @return <code>Elseif</code> that will be used.
	 */
	public Elseif getElseif() {
		return elseif;
	}

	/**
	 * Sets the {@link reportGenerator.bind.Elseif} used in this clause.
	 * @param elseif <code>Elseif</code> to be used.
	 */
	public void setElseif(Elseif elseif) {
		this.elseif = elseif;
	}

	/**
	 * Gets the {@link reportGenerator.bind.Then} used in this clause.
	 * @return Value of the <code>Then</code> attribute which represents the performed actions.
	 * if the condition is evaluated to true.
	 */
	public Then getThen() {
		return then;
	}

	/**
	 * Sets the {@link reportGenerator.bind.Then} used in this clause.
	 * @param then New value of the <code>Then</code> attribute which represents the performed actions.
	 * if the condition is evaluated to true
	 */
	public void setThen(Then then) {
		this.then = then;
	}

	/**
	 * Gets the {@link reportGenerator.bind.Expr} used in this clause.
	 * @return Value of the <code>BoolExpr</code> attribute which represents a boolean expression.
	 */
	public Expr getExpr() {
		return expr;
	}

	/**
	 * Sets the {@link reportGenerator.bind.Expr} used in this clause.
	 * @param expr <code>Expr</code> to be used.
	 */
	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	/**
	 * Gets the {@link reportGenerator.bind.Var} used in this clause.
	 * @return <code>Var</code> that will be used.
	 */
	public Var getVar() {
		return var;
	}

	/**
	 * Sets the {@link reportGenerator.bind.Var} used in this clause.
	 * @param var <code>Var</code> to be used.
	 */
	public void setVar(Var var) {
		this.var = var;
	}

}
