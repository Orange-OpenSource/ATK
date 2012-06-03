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
 * File Name   : BoolExprIspseudoconstant.java
 *
 * Created     : 05/06/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a boolean expression that is evaluated to true if the value contained in the
 * <code>resultvalue</code> or the <code>result</code> is a pseudo-constant.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class BoolExprIspseudoconstant extends BoolExpr implements Visitable,
		Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Represents a result value.
	 */
	private Resultvalue resultvalue;

	/**
	 * Represents a variable whose value is a result value.
	 */
	private Var var;

	/**
	 * Returns the result value.
	 * @return the result value
	 */
	public Resultvalue getResultvalue() {
		return resultvalue;
	}

	/**
	 * Sets the result value.
	 * @param resultvalue the result value.
	 */
	public void setResultvalue(Resultvalue resultvalue) {
		this.resultvalue = resultvalue;
	}

	/**
	 * Returns the variable which contains a result value.
	 * @return the name of the variable
	 */
	public Var getVar() {
		return var;
	}

	/**
	 * Sets the name of the variable whose value is a result value.
	 * @param var the name of the variable
	 */
	public void setVar(Var var) {
		this.var = var;
	}

	/**
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Ispseudoconstant(this);
	}

}
