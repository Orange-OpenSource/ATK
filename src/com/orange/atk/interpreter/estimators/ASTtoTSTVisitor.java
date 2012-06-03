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
 * File Name   : ASTtoTSTVisitor.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.estimators;

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
import com.orange.atk.interpreter.ast.SimpleNode;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;



/**Visit Fully the AST in order to print the AST in Standard Output.
 * Render the script as way it could be parse again.
 * 
 * This visitor
 * @author ywil8421
 *
 */

//TODO : a line management with lignenumber parameter
public class ASTtoTSTVisitor implements ATKScriptParserVisitor {

	private int tabulation =0;
	private Boolean count_line = false;
	public static final String COUNTLINESEP = " - ";
	/**
	 * Default constructor
	 * it don't insert line Number before the code.
	 */
	public ASTtoTSTVisitor() {
		tabulation = 0;
	}
	
	public ASTtoTSTVisitor(boolean countLine) {
		count_line = countLine;
		tabulation = 0;
	}
	
	public Object visit(SimpleNode node, Object data) {
		//Logger.getLogger(this.getClass() ).debug("Noeud simple");
		return "";
	}


	public Object visit(ASTStart node, Object data) {
	//		Logger.getLogger(this.getClass() ).debug("Noeud racine de l'AST interpreté");
		tabulation = 0;
		String codetext ="";
		for (int i=0; i<node.jjtGetNumChildren() ; i++)
			codetext += node.jjtGetChild(i).jjtAccept(this, data)+"\n";
		return codetext;
	}


	public Object visit(ASTFUNCTION node, Object data) {
		String codetext="";
		if (count_line)
			codetext += node.getLineNumber()+COUNTLINESEP;
		
		codetext += tabulate() +node.getValue();
		if(node.jjtGetNumChildren()!=0) {
			codetext += "(";
			codetext += node.jjtGetChild(0).jjtAccept(this, data);
			
			if (node.jjtGetNumChildren() >=2)
				for (int i=1; i<node.jjtGetNumChildren() ; i++)
					codetext += ", "+node.jjtGetChild(i).jjtAccept(this, data);
			
			
			codetext += ")";
		} 
		
		return codetext;
	}
	
	
	public Object visit(ASTTABLE node, Object data) {
		String codetext="{ ";

		for (int i=0; i<node.jjtGetNumChildren() ; i++)
			codetext += node.jjtGetChild(i).jjtAccept(this, data)+" ";

		codetext += "}";

		return codetext;
	}



	public Object visit(ASTSTRING node, Object data) {
		return node.getValue();
	}


	public Object visit(ASTNUMBER node, Object data) {
		return node.getValue();
	}



	public Object visit(ASTCOMMENT node, Object data) {
		String codetext = "";
		if (count_line)
			codetext += node.getLineNumber()+COUNTLINESEP;
		
		codetext += node.getValue();
		
		return codetext;
	}


	public Object visit(ASTSETVAR node, Object data) {

		String codetext = "";
		if (count_line)
			codetext += node.getLineNumber()+COUNTLINESEP;
		codetext += tabulate() +"SetVar(" ;

		//variables
		codetext += node.jjtGetChild(0).jjtAccept(this, data);
		//value
		codetext += ", "+node.jjtGetChild(1).jjtAccept(this, data);
		codetext +=")";
		return codetext;
	}


	public Object visit(ASTLOOP node, Object data) {

		String codetext = "";
		int count_line_length = 0;
		if (count_line) {
			codetext += node.getLineNumber()+COUNTLINESEP;
			count_line_length = codetext.length()-COUNTLINESEP.length();
		}
		codetext += tabulate() +" Loop(";
		
		codetext += node.jjtGetChild(0).jjtAccept(this, data) +")\n" ;
		tabulation++;
		for (int i=1; i<node.jjtGetNumChildren() ; i++)
			codetext += node.jjtGetChild(i).jjtAccept(this, data)+"\n";
		tabulation--;
		if (count_line) {
			for (int i=0; i<count_line_length; i++) codetext += " ";
			codetext += " - ";			
		}
		codetext +=tabulate()+" EndLoop\n";
		return codetext;
	}


	public Object visit(ASTVARIABLE node, Object data) {
		return node.getValue();
	}
	
	
	private String tabulate() {
		String prefix="";
		for (int i=0; i<tabulation;i++)
			prefix +="    ";
		return prefix;
	}

	public Object visit(ASTINCLUDE node, Object data) {

		String codetext="";
		if (count_line)
			codetext += node.getLineNumber()+COUNTLINESEP;
		
		codetext += tabulate() +" Include(";
		
		codetext += node.jjtGetChild(0).jjtAccept(this, data) +")" ;
		return codetext;
	}

}
