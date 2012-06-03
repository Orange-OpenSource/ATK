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
 * File Name   : Report.java
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
 * Represents what it must be in the report.
 * @author penaulau
 * @since JDK5.0
 */
public class Report extends Action implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;

	/**
	 * Content of the report.
	 */
	private Vector<HTMLElement> content;

	/**
	 * Associated verdict
	 */
	private String verdict;

	/**
	 * Sole constructor.
	 *
	 */
	public Report() {
		content = new Vector<HTMLElement>();
	}

	/**
	 * Implementation of the visitor design pattern.
	 * @param v A concrete visitor.
	 */
	public void accept(Visitor v) {
		v.visit_Report(this);
	}

	/**
	 * Getter for vector which contains elements of the report.
	 * @return Vector which contains elements of the report.
	 */
	public Vector<HTMLElement> getContent() {
		return content;
	}

	/**
	 * Setter for vector which contains elements of the report.
	 * @param content Vector which contains elements of the report.
	 */
	public void setContent(Vector<HTMLElement> content) {
		this.content = content;
	}

	/**
	 * Getter for the verdict attribute.
	 * @return a string which represents the verdict.
	 */
	public String getVerdict() {
		return verdict;
	}

	/**
	 * Setter for the verdict attribute.
	 * @param verdict The verdict associated with the report.
	 */
	public void setVerdict(String verdict) {
		this.verdict = verdict;
	}

}
