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
 * File Name   : CommentUncommentAST.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.estimators;



import java.io.StringReader;
import java.util.List;

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
import com.orange.atk.interpreter.parser.ATKScriptParser;
import com.orange.atk.interpreter.parser.ATKScriptParserTreeConstants;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;
import com.orange.atk.interpreter.parser.ParseException;



/**
 * This visitor is invoke by UI when user click on
 *  comment//uncomment menu.
 *  The data parameter is a TreePath[] which contains
 *  paths of selected nodes to comment or uncomment.
 *  
 *  The EstimatorsUtils functions help to interpret treePath elements
 * 
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
public class CommentUncommentAST implements ATKScriptParserVisitor {

	private int treepathdepth =0;
	private List<Integer> treepath;
	public CommentUncommentAST(List<Integer> tp){
		treepathdepth=0;
		treepath =  tp;
	}
	
	/**
	 * if data not null, it's the position in parent node (int)
	 */
	public Object visit(SimpleNode node, Object data) {

		if( treepathdepth >= treepath.size() )
			return false;
		
		if(treepathdepth == treepath.size()-1) {
			
			int position = treepath.get(treepathdepth);
			Node child = node.jjtGetChild( position);
			//uncomment
			if(child instanceof ASTCOMMENT) {
				try {
					//substring because comment node value contains "//"
				   ATKScriptParser code = new ATKScriptParser(
						   		new StringReader( ((ASTCOMMENT) child).getValue().substring(2)) );
				
					ASTStart  astcomment = code.start();
					if (astcomment != null && astcomment.jjtGetNumChildren() >0) {

						node.jjtAddChild(astcomment.jjtGetChild(0), position);
						//and the following
						for(int i=1 ; i<astcomment.jjtGetNumChildren() ; i++)
							node.jjtInsertChild(astcomment.jjtGetChild(i), position+i);
					}
					
					
				} catch (ParseException e) {
					Logger.getLogger(this.getClass() ).debug(((ASTCOMMENT) node).getValue()+" non transformable en code");
					e.printStackTrace();
					return false;
				}
			
				//comment
			}else {
				ASTtoTSTVisitor commenter = new ASTtoTSTVisitor();
				String value = (String) child.jjtAccept(commenter, null);
				ASTCOMMENT newnode = new ASTCOMMENT(ATKScriptParserTreeConstants.JJTCOMMENT);
				newnode.setValue("//"+value);
				node.jjtAddChild(newnode, position);
			}
			
			//found the path until Node to comment
		}else {
			Node child = node.jjtGetChild( treepath.get(treepathdepth));
			treepathdepth++;
			child.jjtAccept(this,null);
		}
		return  true;
	}


	public Object visit(ASTStart node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTFUNCTION node, Object data) {
		return visit( (SimpleNode) node,data);
	}



	public Object visit(ASTSTRING node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTNUMBER node, Object data) {
		return visit( (SimpleNode) node,data);
	}



	public Object visit(ASTCOMMENT node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTSETVAR node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTLOOP node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTTABLE node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTVARIABLE node, Object data) {
		return null;
	}

	public Object visit(ASTINCLUDE node, Object data) {
		return visit( (SimpleNode) node,data);
	}

}
