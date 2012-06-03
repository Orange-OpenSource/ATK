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
 * File Name   : BoolExprIsImplemented.java
 *
 * Created     : 27/06/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class BoolExprIsImplemented extends BoolExpr implements Visitable, Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * Gives the name of the interface
	 */
	private String itf;

	/**
	 * Implementation of the visitor design pattern.
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_IsImplemented(this);
	}

	/**
	 * Returns the name of the interface
	 * @return the name of the interface
	 */
	public String getItf() {
		return itf;
	}

	/**
	 * Sets the name of the interface
	 * @param var the name of the interface
	 */
	public void setItf(String itf) {
		this.itf = itf;
	}

}
