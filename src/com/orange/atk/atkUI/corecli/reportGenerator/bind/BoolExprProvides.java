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
 * File Name   : BoolExprProvides.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a boolean expression that is evaluated to true if it is possible to say that
 * the value contained in the <code>resultvalue</code> or the <code>result</code> match
 * with the given pattern.
 * @author penaulau
 * @since JDK5.0
 */
public class BoolExprProvides extends BoolExpr implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Represents a java value.
	 */
	private Resultvalue resultvalue;
	/**
	 * The tested pattern.
	 */
	private String pattern;
	/**
	 * Represents a variable whose value is a java value.
	 */
	private Var var;

	/**
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Provides(this);
	}

	/**
	 * Returns the java value.
	 * @return the java value
	 */
	public Resultvalue getResultvalue() {
		return resultvalue;
	}

	/**
	 * Sets the java value.
	 * @param resultvalue the java value.
	 */
	public void setResultvalue(Resultvalue resultvalue) {
		this.resultvalue = resultvalue;
	}

	/**
	 * Returns the pattern.
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the tested pattern.
	 * @param pattern the tested pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Returns the variable which contains a java value.
	 * @return the name of the variable
	 */
	public Var getVar() {
		return var;
	}

	/**
	 * Sets the name of the variable whose value is a java value.
	 * @param var the name of the variable
	 */
	public void setVar(Var var) {
		this.var = var;
	}

}
