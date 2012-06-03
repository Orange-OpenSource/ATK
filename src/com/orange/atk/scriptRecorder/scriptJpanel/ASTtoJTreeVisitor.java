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
 * File Name   : ASTtoJTreeVisitor.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder.scriptJpanel;


import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

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
import com.orange.atk.interpreter.estimators.ASTtoTSTVisitor;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;


/**Visit Fully the AST in order to print the AST in 
 * script recorder User Interface.
 * Render a list of node for building a JTREE.
 * 
 * The node represent loop, comment and functions
 * Visibility of constructor is limited to the package scriptJpanel.
 * 
 * 
 * This visitor
 * @author Moreau FABIEN - FMOREAU@gfi.fr
 *
 */
public class ASTtoJTreeVisitor implements ATKScriptParserVisitor {

	//use for build the label of Jtree node
	ASTtoTSTVisitor humanreader ;
	
	protected ASTtoJTreeVisitor() {
		humanreader = new ASTtoTSTVisitor();
	}
	
	public Object visit(SimpleNode node, Object data) {
		return null;
	}


	public Object visit(ASTStart node, Object data) {
	//		Logger.getLogger(this.getClass() ).debug("Noeud racine de l'AST interprete");
		List<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>();
		
		for (int i=0; i<node.jjtGetNumChildren() ; i++)
			list.add( (DefaultMutableTreeNode) node.jjtGetChild(i).jjtAccept(this, data));
		return list;
	}


	public Object visit(ASTFUNCTION node, Object data) {
		
		String label = (String) node.jjtAccept(humanreader, null);
		if (label.startsWith("Include")) {
			DefaultMutableTreeNode includenode = new IncludeNode(label);
			for (int i=1; i<node.jjtGetNumChildren() ; i++)
				includenode.add( (DefaultMutableTreeNode) 
									node.jjtGetChild(i).jjtAccept(this, data) );
			return includenode;
		}
		return new FunctionNode(label);
	}



	public Object visit(ASTSTRING node, Object data) {
		return null;
	}


	public Object visit(ASTNUMBER node, Object data) {
		return null;
	}



	public Object visit(ASTCOMMENT node, Object data) {
		
		String label = (String) node.jjtAccept(humanreader, null);
		return new CommentNode(label);
	}


	public Object visit(ASTSETVAR node, Object data) {
		String label = (String) node.jjtAccept(humanreader, null);
		return new FunctionNode(label);
	}


	public Object visit(ASTLOOP node, Object data) {
		
		String label = "Loop(";
		Node loopvalue = node.jjtGetChild(0);
		label += (String) loopvalue.jjtAccept(humanreader, null);

		label+=	")";
		
		
		DefaultMutableTreeNode loopnode = new LoopNode(label);
		
		for (int i=1; i<node.jjtGetNumChildren() ; i++)
			loopnode.add( (DefaultMutableTreeNode) 
								node.jjtGetChild(i).jjtAccept(this, data) );
		return loopnode;
	}


	public Object visit(ASTVARIABLE node, Object data) {
		return null;
	}

	public Object visit(ASTTABLE node, Object data) {
		return null;
	}

	public Object visit(ASTINCLUDE node, Object data) {
		String label = "Include(";
		Node loopvalue = node.jjtGetChild(0);
		label += (String) loopvalue.jjtAccept(humanreader, null);

		label+=	")";
		
		
		DefaultMutableTreeNode loopnode = new LoopNode(label);
		
		for (int i=1; i<node.jjtGetNumChildren() ; i++)
			loopnode.add( (DefaultMutableTreeNode) 
								node.jjtGetChild(i).jjtAccept(this, data) );
		return loopnode;
	}

}
