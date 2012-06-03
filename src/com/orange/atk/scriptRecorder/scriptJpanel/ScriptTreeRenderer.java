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
 * File Name   : ScriptTreeRenderer.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder.scriptJpanel;



import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

class ScriptTreeRenderer extends DefaultTreeCellRenderer {
	public static final long serialVersionUID = 0;
    private ArrayList<TreeNode> errorNodelist ;
    private TreeNode runningNode = null;
    
    public ScriptTreeRenderer() {
    	super();
    	errorNodelist = new ArrayList<TreeNode>();
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
    	
	   if (runningNode != null && runningNode == value) {
	        	setFont(getFont().deriveFont(Font.BOLD));   		
	    } else {
	    	if (getFont() == null) setFont(tree.getFont().deriveFont(Font.PLAIN));
	    	else if (getFont().getStyle()!= Font.PLAIN) setFont(getFont().deriveFont(Font.PLAIN));
	    }
	
	    if ( errorNodelist.contains(value) ) {
	    	//setBackgroundNonSelectionColor(Color.red);
	    	setTextNonSelectionColor(Color.red);
	    	setTextSelectionColor(Color.red);	
	    } else if (value instanceof CommentNode ) {
			setTextSelectionColor(Color.getHSBColor(0.305f, 0.607f, 0.625f));
			setTextNonSelectionColor(Color.getHSBColor(0.305f, 0.607f, 0.625f));
			
		} else if(value instanceof LoopNode ){
	    	if (isChildOfIncludeNode((DefaultMutableTreeNode) value)) {
	    		setFont(getFont().deriveFont(getFont().getStyle()+Font.ITALIC));   
	    	}
			setTextSelectionColor(Color.getHSBColor(0.082f, 0.76f, 0.702f));
			setTextNonSelectionColor(Color.getHSBColor(0.082f, 0.76f, 0.702f));
		} else if(value instanceof IncludeNode ){
			setTextSelectionColor(Color.getHSBColor(0.082f, 0.76f, 0.702f));
			setTextNonSelectionColor(Color.getHSBColor(0.082f, 0.76f, 0.702f));
			
		} else if(value instanceof FunctionNode ){
	    	if (isChildOfIncludeNode((DefaultMutableTreeNode) value)) {
	    		setFont(getFont().deriveFont(getFont().getStyle()+Font.ITALIC));   
	    	}
			setTextSelectionColor(Color.blue);
			setTextNonSelectionColor(Color.blue);
		}
		
		super.getTreeCellRendererComponent(tree, value,
		        sel, expanded,
		        leaf, row, hasFocus);
		return this; 
	} 	   
    
    private boolean isChildOfIncludeNode(DefaultMutableTreeNode node) {  	
    	node = (DefaultMutableTreeNode) node.getParent();
  		while (node!=null) {
     		if (node.toString().startsWith("Include")) return true;
       		node =  (DefaultMutableTreeNode) node.getParent();
  		}
    	return false;
    }
    
    public void setRunningNode(TreeNode runningNode) {
		this.runningNode = runningNode;
	}

	public void addToErrorList(TreeNode node) {
    	errorNodelist.add(node);
    }   
    
    public void RemoveToErrorList(TreeNode node) {
    	errorNodelist.remove(node);
    }  
    
    public void FlushErrorList(TreeNode node) {
    	errorNodelist = new ArrayList<TreeNode>();
    }
}
