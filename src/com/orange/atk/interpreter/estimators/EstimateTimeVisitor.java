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
 * File Name   : EstimateTimeVisitor.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.estimators;


import org.apache.log4j.Logger;

import com.orange.atk.interpreter.ast.ASTCOMMENT;
import com.orange.atk.interpreter.ast.ASTFUNCTION;
import com.orange.atk.interpreter.ast.ASTINCLUDE;
import com.orange.atk.interpreter.ast.ASTLOOP;
import com.orange.atk.interpreter.ast.ASTNUMBER;
import com.orange.atk.interpreter.ast.ASTSETVAR;
import com.orange.atk.interpreter.ast.ASTSTRING;
import com.orange.atk.interpreter.ast.ASTStart;
import com.orange.atk.interpreter.ast.ASTTABLE;
import com.orange.atk.interpreter.ast.ASTVARIABLE;
import com.orange.atk.interpreter.ast.Node;
import com.orange.atk.interpreter.ast.SimpleNode;
import com.orange.atk.interpreter.atkCore.JATKVariableTable;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;

/**
 * Estimate the times of execution by the script based
 * on the number of sleep. 
 * Adjust the value depending of the loop and a factor.
 * @return the estimated time of execution.
 * @author ywil8421
 *
 */
public class EstimateTimeVisitor implements ATKScriptParserVisitor {
	
	/**
	 * Default Constructor, initialize Variable table.
	 * 
	 */
	JATKVariableTable vartable;
	
	public EstimateTimeVisitor() {
		vartable = new JATKVariableTable();
	}


	public Object visit(SimpleNode node, Object data) {
		
		return 0;
	}


	public Object visit(ASTStart node, Object data) {
		int sleepNumber = 0;

		for( int i=0; i<node.jjtGetNumChildren() ; i++ ) 
			sleepNumber += (Integer) node.jjtGetChild(i).jjtAccept(this, data);
		//Number of sleep  and 6s + 20% for init and interpretation of function
		
		sleepNumber = (int) ((Integer) sleepNumber*1.2 + 6000);
		return sleepNumber;
	}


	public Object visit(ASTCOMMENT node, Object data) {
		return (Integer) 0;
	}


	/**
	 * time estimator calcul : 
	 * if it's a sleep function  : the during of sleep
	 * otherwise 0 
	 */
	public Object visit(ASTFUNCTION node, Object data) {
		if(node.getValue().toLowerCase().equals("sleep")) {
			
			Node nodesleepvalue = node.jjtGetChild(0);
			
			if (nodesleepvalue instanceof ASTNUMBER) {
				return Integer.valueOf(((ASTNUMBER) nodesleepvalue).getValue());
				
			} else {
				return vartable.get( ((ASTVARIABLE) nodesleepvalue).getValue() ).getInteger();
				
			}
		}

		return 0;
	}


	/**
	 * Memorize variable, it could use.
	 */
	public Object visit(ASTSETVAR node, Object data) {
		String variableName = "_"+((ASTSTRING)node.jjtGetChild(0)).getValue().trim();
		Node nodevariablevalue = node.jjtGetChild(1);
		
		if (nodevariablevalue instanceof ASTNUMBER) {

			vartable.addIntegerVariable(variableName, Integer.valueOf(((ASTNUMBER) nodevariablevalue).getValue()));
		} else {
			// Variable type is a String
			vartable.addStringVariable(variableName, ((ASTSTRING) nodevariablevalue).getValue() );
		}
		
		return 0;
	}

	/**
	 * time estimator calcul : 
	 * Multiply the number of loop by the time estimation of lines inside loop
	 */
	public Object visit(ASTLOOP node, Object data) {
		int sleepNumber = 0;
		int repeatNumber = 0;
		
		//By building, number of loop is child 0
		Node fils = node.jjtGetChild(0);
		if(fils instanceof ASTNUMBER) 
			repeatNumber =  Integer.valueOf(((ASTNUMBER)fils).getValue()) ;	
		
		if(fils instanceof ASTVARIABLE) {
			try {
			repeatNumber = vartable.get(((ASTVARIABLE) fils).getValue()).getInteger();
			}catch(ClassCastException e) {
				Logger.getLogger(this.getClass() ).warn("WARNING : Variable of sleep doesn't contain an integer");
				repeatNumber = 1;
			} catch(NullPointerException e) {
				Logger.getLogger(this.getClass() ).warn("WARNING :  undefined Variable "+((ASTVARIABLE) fils).getValue());
				repeatNumber = 0;
			}
		}
			
		for( int i=1; i<node.jjtGetNumChildren() ; i++ ) 
			sleepNumber += (Integer) node.jjtGetChild(i).jjtAccept(this, data);
		
		
		return repeatNumber * sleepNumber;
	}

	public Object visit(ASTSTRING node, Object data) {
		return 0;
	}


	public Object visit(ASTNUMBER node, Object data) {
		return 0;
	}


	public Object visit(ASTTABLE node, Object data) {
		return 0;
	}

	public Object visit(ASTVARIABLE node, Object data) {
		return 0;
	}


	public Object visit(ASTINCLUDE node, Object data) {
		int sleepNumber = 0;
		for( int i=1; i<node.jjtGetNumChildren() ; i++ ) 
			sleepNumber += (Integer) node.jjtGetChild(i).jjtAccept(this, data);
		return sleepNumber;
	}

}
