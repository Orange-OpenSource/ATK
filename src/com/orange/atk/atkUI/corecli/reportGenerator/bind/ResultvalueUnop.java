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
 * File Name   : ResultvalueUnop.java
 *
 * Created     : 03/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a unary operation.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class ResultvalueUnop extends Resultvalue implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the Unop element.
	 */
	private Resultvalue resultvalue;
	/**
	 * The operator
	 */
	private String operator;
	/**
	 * Id of the unary operation
	 */
	private String id;
	/**
	 * True if this operation is significant for the report.
	 */
	private String use;

	/**
	 * Sole constructor.
	 *
	 */
	public ResultvalueUnop() {}

	/**
	 * Getter for property <code>operator</code>.
	 * @return Value of property <code>operator</code>.
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * Setter for property <code>operator</code>.
	 * @param operator New value of property <code>operator</code>.
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * Getter for <code>id</code property.
	 * @return Value of <code>id</code> property.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter for <code>use</code> property.
	 * @param id New value of <code>use</code> property.
	 */
	public void setUse(String use) {
		this.use = use;
	}

	/**
	 * Getter for <code>use</code property.
	 * @return Value of <code>use</code> property.
	 */
	public String getUse() {
		return use;
	}

	/**
	 * Setter for <code>id</code> property.
	 * @param id New value of <code>id</code> property.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Unop(this);
	}

	public boolean equals(Object object){
		if (object==null) return false;
		if (!(object instanceof ResultvalueUnop)) return false;
		boolean equals = true;
		ResultvalueUnop other = (ResultvalueUnop)object;
		if (operator.equals(other.getOperator()) && id.equals(other.getId())
				&& use.equals(other.getUse()) && resultvalue.equals(other.getResultvalue())) {
			equals = true;
		} else {
			equals = false;
		}
		return equals;
	}

	public Resultvalue getResultvalue() {
		return resultvalue;
	}

	public void setResultvalue(Resultvalue resultvalue) {
		this.resultvalue = resultvalue;
	}

}
