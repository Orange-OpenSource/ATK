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
 * File Name   : ResultvalueBinop.java
 *
 * Created     : 03/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a binary operation.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class ResultvalueBinop extends Resultvalue implements Visitable, Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * The content of the Binop element.
	 */
	private Vector<Resultvalue> content;
	/**
	 * The used operator
	 */
	private String operator;

	/**
	 * Sole constructor.
	 *
	 */
	public ResultvalueBinop() {
		content = new Vector<Resultvalue>();
	}

	/**
	 * Getter for vector which contains elements of the binary operation.
	 * @return Vector which contains elements of the binary operation.
	 */
	public Vector<Resultvalue> getContent() {
		return content;
	}

	/**
	 * Setter for vector which contains elements of the binary operation.
	 * @param content Vector which contains elements of the binary operation.
	 */
	public void setContent(Vector<Resultvalue> content) {
		this.content = content;
	}

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
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Binop(this);
	}

	public boolean equals(Object object){
		if (object==null) return false;
		if (!(object instanceof ResultvalueBinop)) return false;
		ResultvalueBinop other = (ResultvalueBinop)object;
		boolean equals = true;
		if (operator.equals(other.getOperator())){
			Vector<Resultvalue> otherContent = other.getContent();
			if (content.size() == otherContent.size()) {
				int i=0;
				for (Iterator<Resultvalue> it = content.iterator();it.hasNext();i++) {
					equals &= it.next().equals(otherContent.get(i));
				}
			} else {
				equals = false;
			}
		} else {
			equals = false;
		}
		return equals;
	}

}
