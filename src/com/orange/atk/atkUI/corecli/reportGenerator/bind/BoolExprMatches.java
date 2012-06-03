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
 * File Name   : BoolExprMatches.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.bind;

import java.io.Serializable;

import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitable;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 * Represents a boolean expression that is evaluated to true if the value contained in the
 * <code>resultvalue</code> or the <code>result</code> match with the given pattern.
 * @author penaulau
 * @since JDK5.0
 */
public class BoolExprMatches extends BoolExpr implements Serializable, Visitable {

	private static final long serialVersionUID = 1L;

	public enum Matches {EXISTS, FORALL};

	/**
	 * Way to evaluate the expression
	 */
	private Matches matchesWay = Matches.FORALL;

	/**
	 * Represents a result value.
	 */
	private Resultvalue resultvalue;
	/**
	 * The tested pattern.
	 */
	private String pattern;
	/**
	 * Represents a variable whose value is a result value.
	 */
	private Var var;
	/**
	 * The result of the matching clause is true if the expression matches
	 * the given pattern and the used profile is the same than the profile
	 * given in this attribute.
	 * The name of the profile must be "midp10" or "midp20".
	 */
	private String profile;

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
	 * Implementation of the visitor design pattern
	 * @param v a concrete visitor
	 */
	public void accept(Visitor v) {
		v.visit_Matches(this);
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
	 * Returns the name of the profile.
	 * @return the name of the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * Sets the name of the profile name (midp10 or midp20).
	 * @param profile the name of the profile
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Matches getMatchesWay() {
		return matchesWay;
	}

	/**
	 * Set the way to determine the verdict.
	 *
	 * If the matches way is Matches.EXISTS then matches expression
	 * returns true if it exists a possible expression which matches
	 *
	 * Else if the matches way if Matches.FORALL then the evaluation
	 * of the matches expression returns true only if all possible
	 * expressions matches.
	 *
	 * @param matchesWay The way to determine the verdict
	 */
	public void setMatchesWay(Matches matchesWay) {
		this.matchesWay = matchesWay;
	}

}
