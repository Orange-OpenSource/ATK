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
 * File Name   : SurroundLoopVisitor.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.estimators;


import java.util.List;

import javax.swing.text.BadLocationException;

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
import com.orange.atk.interpreter.parser.ATKScriptParserTreeConstants;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;

/**
 * 
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
public class SurroundLoopVisitor implements ATKScriptParserVisitor {

	private int treepathdepth =0;
	private List<Integer> firsttreepath;
	
	private Integer lasttreepathindex;
	private Node nbloop;
	/**
	 * 
	 * @param firstnode
	 * @param lastnode
	 * @param nbloopInt
	 * @throws BadLocationException When the selection coulnd't be surrounded
	 */
	public SurroundLoopVisitor(List<Integer> FirstPath, Integer lastIndex,
			int nbloopInt) {
		firsttreepath = FirstPath;
		lasttreepathindex = lastIndex;
		nbloop = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
		((ASTNUMBER) nbloop).setValue(String.valueOf(nbloopInt));
	}

	public SurroundLoopVisitor(List<Integer> firstPath, Integer lastIndex,
			String nbloopString) {
		
		firsttreepath = firstPath;
		lasttreepathindex = lastIndex;
		nbloop = new ASTVARIABLE(ATKScriptParserTreeConstants.JJTVARIABLE);
		((ASTVARIABLE) nbloop).setValue(nbloopString);
	}

	/**
	 * if data not null, it's the position in parent node (int)
	 */
	public Object visit(SimpleNode node, Object data) {
	
		if( treepathdepth >= firsttreepath.size() )
			return false;
		
		
		if (treepathdepth == firsttreepath.size()-1 ){
			int firstposition = firsttreepath.get(treepathdepth);
		
			
			ASTLOOP loop = new ASTLOOP(ATKScriptParserTreeConstants.JJTLOOP);

			loop.jjtAddChild(nbloop, 0);
			
			for (int i=0 ; i <= lasttreepathindex-firstposition ; i++){
				loop.jjtAddChild(node.jjtGetChild(firstposition), i+1);
				node.jjtRemoveChild(firstposition);
			}
			node.jjtInsertChild(loop, firstposition);
			return true;
			
			
		} else {
			//cross node depending the PathTree
					
			Node child = node.jjtGetChild( firsttreepath.get(treepathdepth));
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

	public Object visit(ASTTABLE node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTSETVAR node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTLOOP node, Object data) {
		return visit( (SimpleNode) node,data);
	}


	public Object visit(ASTVARIABLE node, Object data) {
		return null;
	}

	public Object visit(ASTINCLUDE node, Object data) {
		return visit( (SimpleNode)node,data);
	}

	public Object visit(ASTBOOLEAN node, Object data) {
		return visit( (SimpleNode) node,data);
	}

	public Object visit(ASTFLOAT node, Object data) {
		return visit( (SimpleNode) node,data);
	}


}
