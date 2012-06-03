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
 * File Name   : SimpleNode.java
 *
 * Created     : 21/05/2010
 * Author(s)   : HENAFF Mari-Mai
 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orange.atk.interpreter.ast;

import java.util.Stack;

import com.orange.atk.interpreter.parser.ATKScriptParser;
import com.orange.atk.interpreter.parser.ATKScriptParserTreeConstants;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;

public
class SimpleNode extends TokenValue implements Node {

  protected Node parent;
  protected Node[] children;
  protected int id;
  protected Object value;
  protected ATKScriptParser parser;

  public SimpleNode(int i) {
    id = i;
  }

  public SimpleNode(ATKScriptParser p, int i) {
    this(i);
    parser = p;
  }

  public void jjtOpen() {
  }

  public void jjtClose() {
  }

  public void jjtSetParent(Node n) { parent = n; }
  public Node jjtGetParent() { return parent; }

  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new Node[i + 1];
    } else if (i >= children.length) {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
    n.jjtSetParent(this);// line MANUALLY added
  }

  public Node jjtGetChild(int i) {
    return children[i];
  }

  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }

  public void jjtSetValue(Object value) { this.value = value; }
  public Object jjtGetValue() { return value; }

  /** Accept the visitor. **/
  public Object jjtAccept(ATKScriptParserVisitor visitor, Object data)
{
    return visitor.visit(this, data);
  }

  /** Accept the visitor. **/
  public Object childrenAccept(ATKScriptParserVisitor visitor, Object data)
{
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        children[i].jjtAccept(visitor, data);
      }
    }
    return data;
  }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() { return ATKScriptParserTreeConstants.jjtNodeName[id]; }
  public String toString(String prefix) { return prefix + toString(); }

  /* Override this method if you want to customize how the node dumps
     out its children. */

  public void dump(String prefix) {
    System.out.println(toString(prefix));
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        SimpleNode n = (SimpleNode)children[i];
        if (n != null) {
          n.dump(prefix + " ");
        }
      }
    }
  }


//BEGIN - Methods MANUALLY added
public void jjtRemoveChild( int i) {
  if (children != null && i < children.length) {
    Node c[] = new Node[children.length - 1];
    System.arraycopy(children, 0, c, 0,  i );
    System.arraycopy(children, i+1, c, i, children.length - i -1);
    children = c;
  }

}


public void jjtInsertChild(Node n, int i) {
    if (children == null) { 
        jjtAddChild(n,i);
        
    } else   if ( i < children.length) {
      Node c[] = new Node[children.length + 1];
      System.arraycopy(children, 0, c, 0,  i );
      System.arraycopy(children, i, c, i+1, children.length - i );
      c[i]=n;
      children = c;
    } else {
  	  jjtAddChild(n, i);
    }
    n.jjtSetParent(this);
}
	
public Stack<Integer> jjtGetPosition( ) {
	  Stack<Integer> visitStack = new Stack<Integer>();
	  Node currentNode = this; 
	  while (currentNode.jjtGetParent() != null) {
		  Node parent  = currentNode.jjtGetParent();
		  for(int i=0 ; i <parent.jjtGetNumChildren() ; i++)
			  if (parent.jjtGetChild(i) == currentNode)
				  visitStack.add(i);
		  currentNode = parent;
	  }
	  
	  return visitStack;
}

public int jjtGetID( ) {
	  return id;
}

public int getLineNumber() {
	return calculateLineNumber();
}

private int calculateLineNumber() {
	int lineNum = 0;
	Node node = (Node) this;
	Node nodeParent = node.jjtGetParent();
	while (nodeParent!=null) {
		lineNum += calculateIndex(node, nodeParent);
		node = nodeParent;
		nodeParent = node.jjtGetParent();
	}
	return lineNum+1;
}

private int calculateIndex(Node child, Node parent) {
	int numChildren = parent.jjtGetNumChildren();
	int lineNum=0;
	int i = 0;
	while (i<numChildren && child != parent.jjtGetChild(i)) {
		lineNum += getNumAllChildren(parent.jjtGetChild(i));
		i++;
	}
	return lineNum;
}

private int getNumAllChildren(Node node) {
	int numChildren = node.jjtGetNumChildren();
	int numAllChildren = 0;
	if (node instanceof ASTLOOP) {
		for (int i=0; i<numChildren; i++) numAllChildren += getNumAllChildren(node.jjtGetChild(i));
		return numAllChildren;
	} else if (node instanceof ASTINCLUDE) {
		for (int i=0; i<numChildren; i++) numAllChildren += getNumAllChildren(node.jjtGetChild(i));
		return numAllChildren;
	} else return 1;
}
}
// END - Methods MANUALLY added

/* JavaCC - OriginalChecksum=43afa20fe548720ac7b05705d167b0fb (do not edit this line) */
