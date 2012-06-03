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
 * File Name   : BoolExprIsempty.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a boolean expression that is evaluated to true if the set defined in the <code>In</code>
 * clause is empty.
 * @author penaulau
 * @since JDK5.0
 */
public class BoolExprIsempty extends BoolExpr implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Represents the tested set.
	 */
	private SetExpr setExpr;
	/**
	 * Gives the name of a variable whose its value is a set.
	 */
	private Var var;

	/**
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_IsEmpty(this);
	}

	/**
	 * Returns the set.
	 * @return the set
	 */
	public SetExpr getSetExpr() {
		return setExpr;
	}

	/**
	 * Sets the set.
	 * @param setExpr the set
	 */
	public void setSetExpr(SetExpr setExpr) {
		this.setExpr = setExpr;
	}

	/**
	 * Returns the name of the variable.
	 * @return the name of the variable
	 */
	public Var getVar() {
		return var;
	}

	/**
	 * Sets the name of the variable.
	 * @param var the name of the variable.
	 */
	public void setVar(Var var) {
		this.var = var;
	}

}
