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
 * File Name   : JATKInterpreterStack.java
 *
 * Created     : 27/11/2008
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.interpreter.atkCore;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * This class represents an high level layer to a stack and provides utility
 * functions used by the interpreter for storing temporay values, ...
 */


public class JATKInterpreterStack extends Stack<Variable> implements Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This function extracts an integer from the top of the stack.
	 * 
	 * @return an integer from the top of the stack. If the stack is empty, this
	 *         function returns null.
	 * @exception ClassCastException
	 *                if the top of the stack is not an integer (the top could
	 *                be a string)
	 */
	public Integer popInteger() {
		if (empty()) {
			Logger.getLogger(this.getClass() ).warn("Internal error : empty stack");
			return null;
		}
		// Logger.getLogger(this.getClass() ).debug("pop integer");
		return pop().getInteger();
	}

	
	/**
	 * This function extracts a float from the top of the stack.
	 * 
	 * @return an integer from the top of the stack. If the stack is empty, this
	 *         function returns null.
	 * @exception ClassCastException
	 *                if the top of the stack is not an integer (the top could
	 *                be a string)
	 */
	public Float popFloat() {
		if (empty()) {
			Logger.getLogger(this.getClass() ).warn("Internal error : empty stack");
			return null;
		}
		// Logger.getLogger(this.getClass() ).debug("pop integer");
		return pop().getFloat();
	}
	/**
	 * This function extracts a boolean from the top of the stack.
	 * 
	 * @return an integer from the top of the stack. If the stack is empty, this
	 *         function returns null.
	 * @exception ClassCastException
	 *                if the top of the stack is not an integer (the top could
	 *                be a string)
	 */
	public Boolean popBoolean() {
		if (empty()) {
			Logger.getLogger(this.getClass() ).warn("Internal error : empty stack");
			return null;
		}
		// Logger.getLogger(this.getClass() ).debug("pop integer");
		return pop().getBoolean();
	}

	/**
	 * This function extracts a String from the top of the stack.
	 * 
	 * @return a String from the top of the stack. If the stack is empty, this
	 *         function returns null.
	 * @exception ClassCastException
	 *                if the top of the stack is not a String (the top could be
	 *                an Integer)
	 */
	public String popString() {
		if (empty()) {
			Logger.getLogger(this.getClass() ).warn("Internal error : empty stack");
			return null;
		}
		return pop().getString();
	}

	/**
	 * This function extracts a Table from the top of the stack.
	 * 
	 * @return a String from the top of the stack. If the stack is empty, this
	 *         function returns null.
	 * @exception ClassCastException
	 *                if the top of the stack is not a String (the top could be
	 *                an Integer)
	 */
	public List<Variable> popTable() {
		if (empty()) {
			Logger.getLogger(this.getClass() ).warn("Internal error : empty stack");
			return null;
		}
		return pop().getTable();
	}

	/**
	 * Used to test if top element is an Integer
	 * 
	 * @return true if the top element is an Integer, false otherwise
	 */
	public boolean isTopInteger() {
		if(isEmpty()){
			return false;
		}
		return peek().isInteger();
	}

	/**
	 * Used to test if top element is a Float
	 * 
	 * @return true if the top element is a Float, false otherwise
	 */
	public boolean isTopFloat() {
		if(isEmpty()){
			return false;
		}
		return peek().isFloat();
	}
	
	/**
	 * Used to test if top element is a Boolean
	 * 
	 * @return true if the top element is a Boolean, false otherwise
	 */
	public boolean isTopBoolean() {
		if(isEmpty()){
			return false;
		}
		return peek().isBoolean();
	}
	/**
	 * Used to test if the top element is a String
	 * @return true if the top element is a String, false otherwise
	 */
	public boolean isTopString() {
		if(isEmpty()){
			return false;
		}
		return peek().isString();
	}

	/**
	 * Used to test if the top element is a Table
	 * @return true if the top element is a Table, false otherwise
	 */
	public boolean isTopTable() {
		if(isEmpty()){
			return false;
		}
		return peek().isTable();
	}

	/**
	 * Push an integer at the top of the stack
	 * 
	 * @param i
	 *            integer to push
	 */
	public void pushInteger(Integer i) {
		push(Variable.createInteger(i));
	}

	/**
	 * Push a string at the top of the stack
	 * 
	 * @param s
	 *            string to push
	 */
	public void pushString(String s) {
		push(Variable.createString(s));
	}
	
	/**
	 * Push a Float at the top of the stack
	 * 
	 * @param s
	 *            float to push
	 */
	public void pushFloat(Float s) {
		push(Variable.createFloat(s));
	}
	
	/**
	 * Push a Boolean at the top of the stack
	 * 
	 * @param s
	 *            boolean to push
	 */
	public void pushBoolean(Boolean s) {
		push(Variable.createBoolean(s));
	}


	/**
	 * Push a string at the top of the stack
	 * 
	 * @param s
	 *            string to push
	 */
	public void pushTable(List<Variable> t) {
		push(Variable.createTable(t) );
	}
	
	
}

