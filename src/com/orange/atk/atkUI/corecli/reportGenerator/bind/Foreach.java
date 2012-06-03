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
 * File Name   : Foreach.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a repetitive construct.
 * @author penaulau
 * @since JDK5.0
 */
public class Foreach extends Action implements Serializable, Visitable {

	static final long serialVersionUID = 1L;

	/**
	 * Specifies the HTML text between two elements of the enumeration.
	 */
	private Separator separator;

	/**
	 * Treatments are specified in the <code>do</do> element represented by the @link{Do} class.
	 */
    private Do doo;

    /**
     * The iteration is made on the set specify in the <code>in</code> element.
     */
    private In in;

    /**
     * Represents the name of the variable
     */
    private java.lang.String var;

    /**
     * This attribute specifies an optional integer that represents the maximum number of elements considered.
     */
    private java.lang.String count;

    /**
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Foreach(this);
	}

	/**
	 * Returns the maximum number of elements considered.
	 * @return the maximum number of elements considered
	 */
	public java.lang.String getCount() {
		return count;
	}

	/**
	 * Sets the maximum number of elements considered.
	 * @param count the maximum number of elements considered.
	 */
	public void setCount(java.lang.String count) {
		this.count = count;
	}

	/**
	 * Returns the performed actions in this iterative structure.
	 * @return the performed actions in this iterative structure
	 */
	public Do getDoo() {
		return doo;
	}

	/**
	 * Sets the performed actions in this iterative structure.
	 * @param doo the performed actions in this iterative structure
	 */
	public void setDoo(Do doo) {
		this.doo = doo;
	}

	/**
	 * Returns the <code>In</code> object corresponding to the <code>In</code> XML element.
	 * @return the <code>In</code> object corresponding to the <code>In</code> XML element
	 */
	public In getIn() {
		return in;
	}

	/**
	 * Sets the <code>In</code> object corresponding to the <code>In</code> XML element.
	 * @param in the <code>In</code> object corresponding to the <code>In</code> XML element
	 */
	public void setIn(In in) {
		this.in = in;
	}

	/**
	 * Returns the object corresponding to the separator between two elements of the result.
	 * @return the object corresponding to the separator between two elements of the result
	 */
	public Separator getSeparator() {
		return separator;
	}

	/**
	 * Sets the object corresponding to the separator between two elements of the result.
	 * @param separator the object corresponding to the separator between two elements of the result
	 */
	public void setSeparator(Separator separator) {
		this.separator = separator;
	}

	/**
	 * Returns the name of the variable whose value is a set.
	 * @return the name of the variable whose value is a set
	 */
	public java.lang.String getVar() {
		return var;
	}

	/**
	 * Sets the name of the variable whose value is a set.
	 * @param var the name of the variable whose value is a set
	 */
	public void setVar(java.lang.String var) {
		this.var = var;
	}

}
