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
 * File Name   : InsertInAST.java
 *
 * Created     : 27/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.estimators;


import java.util.List;

import com.orange.atk.interpreter.ast.ASTBOOLEAN;
import com.orange.atk.interpreter.ast.ASTCOMMENT;
import com.orange.atk.interpreter.ast.ASTFLOAT;
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
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;

/**
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 * 
 * Insert depanding of position of the Treenode
 *
 */
public class InsertInAST implements ATKScriptParserVisitor {

	private int treepathdepth ;
	private List<Integer> tp;
	private boolean modify;
	private SimpleNode modification;
	
	public InsertInAST(List<Integer> leadSelectionPath, Node result, boolean modify) {
		tp = leadSelectionPath;
		treepathdepth =0;
		modification = (SimpleNode)result;
		this.modify = modify;
		
	}

	public Object visit(SimpleNode node, Object data) {
		//Entry point

		if( treepathdepth >= tp.size() )
			return false;
		
	
		
		if (treepathdepth == tp.size()-1 ){
			if (modify) {
				if (node.jjtGetNumChildren() > tp.get(treepathdepth) ){
					Node tomodify = node.jjtGetChild(tp.get(treepathdepth));
					
					if (modification instanceof ASTLOOP &&
							 tomodify instanceof ASTLOOP) {
						for(int i=1 ; i<tomodify.jjtGetNumChildren() ; i++)
							modification.jjtAddChild(tomodify.jjtGetChild(i), i);
					}
				}
				
				node.jjtAddChild(modification, tp.get(treepathdepth));
			} else {
				node.jjtInsertChild(modification,  tp.get(treepathdepth));
			}
			
			return true;
		} else {
			//cross node depending the PathTree
					
			Node child = node.jjtGetChild( tp.get(treepathdepth));
			treepathdepth++;
			child.jjtAccept(this,null);
		}
		
		return true;
	}


	public Object visit(ASTStart node, Object data) {
			//Entry point
		return visit( (SimpleNode)node,data);
	}


	public Object visit(ASTFUNCTION node, Object data) {
		return visit( (SimpleNode)node,data);
	}

	public Object visit(ASTSTRING node, Object data) {
		return visit( (SimpleNode)node,data);
	}


	public Object visit(ASTNUMBER node, Object data) {
		return visit( (SimpleNode)node,data);
	}



	public Object visit(ASTCOMMENT node, Object data) {
		return visit( (SimpleNode)node,data);
	}


	public Object visit(ASTSETVAR node, Object data) {
		return visit( (SimpleNode)node,data);
	}


	public Object visit(ASTLOOP node, Object data) {
		return visit( (SimpleNode)node,data);
	}


	public Object visit(ASTTABLE node, Object data) {
		return visit( (SimpleNode)node,data);
	}


	public Object visit(ASTVARIABLE node, Object data) {
		return visit( (SimpleNode)node,data);
	}

	public Object visit(ASTINCLUDE node, Object data) {
		return visit( (SimpleNode)node,data);
	}

	public Object visit(ASTBOOLEAN node, Object data) {
		return visit( (SimpleNode)node,data);
	}

	public Object visit(ASTFLOAT node, Object data) {
		return visit( (SimpleNode)node,data);
	}

}
