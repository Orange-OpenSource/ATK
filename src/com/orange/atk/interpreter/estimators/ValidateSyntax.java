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
 * File Name   : ValidateSyntax.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.estimators;



import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
import com.orange.atk.interpreter.ast.FunctionDictionnary;
import com.orange.atk.interpreter.ast.Node;
import com.orange.atk.interpreter.ast.SimpleNode;
import com.orange.atk.interpreter.atkCore.ActionToExecute;
import com.orange.atk.interpreter.parser.ATKScriptParserTreeConstants;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;
import com.orange.atk.interpreter.parser.ParseException;
import com.orange.atk.platform.Platform;

/**
 * This visitor try to prevent from error during interpretation of the script.
 * It verifies : <br>
 * _ Negatives values for loop
 * _ Undefined Variables
 * _ Redefined Variables
 * _ Correct 
 * @author tsyv5124
 *
 */
public class ValidateSyntax implements ATKScriptParserVisitor {
	private HashSet<String> definedVariables;
	
	
	//The string[] represent the signature of the function
	//string[0] is name of the function
	//string[1] ..string[n] is arguments quality of the function 
	//   keyword STRING or INTEGER or INTEGERPOSITIVE 
	private FunctionDictionnary functionDictionnary;


	//Set it at True when The Verification is on the full Tree.
	//Forget Variables Verification.
	private boolean partialVerification;
	
	private String scriptPath;
	
	public ValidateSyntax(String scriptPath, Boolean partial) {
		definedVariables = new HashSet<String>();
		functionDictionnary = new FunctionDictionnary();
		partialVerification = partial;
		this.scriptPath = scriptPath;
	}
	

	public Object visit(SimpleNode node, Object data) {
		// nothing
		return null;
	}

	/**
	 * return the text which sums the errors
	 */
	public Object visit(ASTStart node, Object data) {
		String errortext = "";
		//TODO:the first node should be a StartMainlog
		
		for (int i=0; i<node.jjtGetNumChildren() ; i++)
			errortext += node.jjtGetChild(i).jjtAccept(this, data);
		return errortext;
	}

	
	//nothing to do
	public Object visit(ASTCOMMENT node, Object data) {
		return "";
	}

	
	
	
	

	public Object visit(ASTFUNCTION node, Object data) {
		String childerrortext="";
		String functionname = node.getValue();
		String errortext ="";
		
	/*	if (functionname.contains(ASTtoTSTVisitor.COUNTLINESEP))
			functionname = functionname.substring(functionname.indexOf(ASTtoTSTVisitor.COUNTLINESEP)+ASTtoTSTVisitor.COUNTLINESEP.length());
		
		Logger.getLogger(this.getClass() ).debug("VALIDATE ===========>"+functionname);*/
		
		//build the method signature
		List<String> signature = new ArrayList<String>();
		signature.add(functionname);
		
		for (int i=0; i<node.jjtGetNumChildren(); i++){ 			
			Node fils = node.jjtGetChild(i);
			if(fils.jjtGetID()==ATKScriptParserTreeConstants.JJTVARIABLE)  {
				childerrortext += (String) fils.jjtAccept(this, data);
				signature.add("UNKNOWN");
			}
			if( fils.jjtGetID()==ATKScriptParserTreeConstants.JJTNUMBER) 
				signature.add("INTEGER");
				
			if(fils.jjtGetID()==ATKScriptParserTreeConstants.JJTSTRING )	 
				signature.add("STRING");
		}
		  
//conclusion
		if (! functionDictionnary.isExist( signature )) {
			errortext="function "+functionname+" unknow. Nearly signatures  : \r\n";

			for(String [] el: functionDictionnary.getSignatures(signature.get(0))) {
				errortext+="   "+el[0]+"(";	
				for (int i=1; i<el.length; i++){
					if ( i!=1 )
						errortext+=" , ";
					errortext+=el[i];
				}
				errortext += ")\r\n";
			}
		}
		return childerrortext + errortext ;
	}


	/**
	 * Add new defined variables to the table.
	 * Verify that's not a redefinition.
	 */

	public Object visit(ASTSETVAR node, Object data) {
		String errortext="";
		String variable = "_"+((ASTSTRING)node.jjtGetChild(0)).getValue().trim();
		if (!definedVariables.add(variable))
			errortext = "Variable \""+variable+
						"\" already defined. \r\n";
		return errortext;
	}

	/**
	 * Verify that loop repetition aren't negative.
	 * Continue the vérification inside the loop body
	 */

	public Object visit(ASTLOOP node, Object data) {
		String errortext = "";

		try {
			int loopnumber = Integer.valueOf(((ASTNUMBER) node.jjtGetChild(0)).getValue());
		
		if (loopnumber < 0)
			errortext = "Negative loop Number ("+loopnumber+"). \r\n";
		} catch(ClassCastException e){
			ASTVARIABLE nodechild = ((ASTVARIABLE) node.jjtGetChild(0));
			if (  !partialVerification &&
					  !definedVariables.contains(nodechild.getValue()  ) )
					errortext = "Variable "+nodechild.getValue()+" Undefined.\r\n";
		}
		//continue in body of the loop
		for (int i=1; i<node.jjtGetNumChildren() ; i++)
			errortext += node.jjtGetChild(i).jjtAccept(this, data);
		return errortext;
	}

	
	//nothing to do
	public Object visit(ASTSTRING node, Object data) {
		return "";
	}

	
	//nothing to do
	public Object visit(ASTNUMBER node, Object data) {
		return "";
	}
	
	//nothing to do
	public Object visit(ASTTABLE node, Object data) {
		return "";
	}

 /**
  * Verify that the variable is already defined
  */
	public Object visit(ASTVARIABLE node, Object data) {
		String errorString = "";
		if (  !partialVerification &&
			  !definedVariables.contains(node.getValue().trim()) )
			errorString = "Variable "+node.getValue().trim()+" undefined.\r\n";
		return errorString;
	}

	public Object visit(ASTINCLUDE node, Object data) {
		
		String errorString = "";
		String include = ((ASTSTRING)node.jjtGetChild(0)).getValue();
		try {
			ActionToExecute.fetchIncludeScript(this, node, scriptPath, include);
		} catch (FileNotFoundException e) {
			errorString = "File "
					+ include
					+ " could not be include. Possible reasons are : "
					+ Platform.LINE_SEP
					+ " - The named file does not exist"
					+ Platform.LINE_SEP
					+ " - The named file is a directory rather than a regular file"
					+ Platform.LINE_SEP
					+ " - You do not have enough rights to read the file";
		} catch (ParseException e) {
			errorString = "File " + include + " is not valid";
		}
		return errorString;
	}

}
