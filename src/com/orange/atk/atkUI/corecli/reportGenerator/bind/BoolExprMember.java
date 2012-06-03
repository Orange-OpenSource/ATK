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
 * File Name   : BoolExprMember.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;
import java.util.Vector;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a boolean expression that is evaluated to true if the left hand side belongs to
 * the set defined in the right hand side of the expression. If the left hand side of this expression
 * is a set, then we test if this set is included in the second set.
 * @author penaulau
 * @since JDK5.0
 */
public class BoolExprMember extends BoolExpr implements Visitable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The vector which contains the left and the right and side of the boolean expression.
	 */
	private Vector<Expr> exprPlusSetExpr;

	/**
	 * Sole constructor.
	 *
	 */
	public BoolExprMember() {
		exprPlusSetExpr = new Vector<Expr>();
	}

	/**
	 * Returns the vector which contains the left and the right and side of the boolean expression.
	 * @return the vector which contains the left and the right and side of the boolean expression
	 */
	public Vector<Expr> getExprPlusSetExpr() {
		return exprPlusSetExpr;
	}

	/**
	 * Sets the left and the right hand side of the boolean expression.
	 * @param exprPlusSetExpr the vector which contains the left and the right hand side of the boolean expression
	 */
	public void setExprPlusSetExpr(Vector<Expr> exprPlusSetExpr) {
		this.exprPlusSetExpr = exprPlusSetExpr;
	}

	/**
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Member(this);
	}

}
