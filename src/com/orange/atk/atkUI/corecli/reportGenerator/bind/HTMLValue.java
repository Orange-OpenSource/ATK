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
 * File Name   : HTMLValue.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents reporting of java values.
 * @author penaulau
 * @since JDK5.0
 */
public class HTMLValue extends HTMLElement implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Represents a java value.
	 */
	private Resultvalue resultvalue;
	/**
	 * Represents a variable whose value is a java value.
	 */
	private Var var;

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_HTMLValue(this);
	}

	/**
	 * Gets the {@link reportGenerator.bind.Resultvalue} used in this clause.
	 * @return <code>Resultvalue</code> that will be used.
	 */
	public Resultvalue getResultvalue() {
		return resultvalue;
	}

	/**
	 * Sets the {@link reportGenerator.bind.Resultvalue} used in this clause.
	 * @param resultvalue <code>Resultvalue</code> to be used.
	 */
	public void setResultvalue(Resultvalue resultvalue) {
		this.resultvalue = resultvalue;
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
