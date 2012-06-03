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
 * File Name   : SetExprProperties.java
 *
 * Created     : 03/07/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a set expression which returns a set of results getting back from the XML file
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class SetExprProperties extends SetExpr implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;
	
	private String prefix;

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Properties(this);
	}

	/**
	 * Getter for property <code>prefix</code>.
	 * @return Value of property <code>prefix</code>.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Setter for property <code>prefix</code>.
	 * @param probe New value of property <code>prefix</code>.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
