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
 * File Name   : AutoCompleteJtextField.java
 *
 * Created     : 27/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder.scriptJpanel;


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.orange.atk.interpreter.ast.FunctionDictionnary;
import com.orange.atk.scriptRecorder.ScriptController;


public class AutoCompleteJtextField extends JTextField  implements KeyListener, FocusListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5774469221987410017L;
	private JTree tree;
	private List<Integer> path;
	private Boolean modify ;
	private JPopupMenu menuautocompletion;
	private FunctionDictionnary fd;
	
	public AutoCompleteJtextField(JTree containertree) {
		super();
		
		addKeyListener(this);
		addFocusListener(this);
		tree = containertree;
		fd = new FunctionDictionnary();
	}
	
	/* implement key listener 
	 * 
	 * used for autocompletion
	 */
	public void keyPressed(KeyEvent e) {
	}
	
	public void keyReleased(KeyEvent e) {
		if (this.isEnabled()) {
			if (menuautocompletion != null)
				menuautocompletion.setVisible(false);
			
			JTextField textField = (JTextField)  e.getSource();
		     
		    Point location = getLocationOnScreen();
		    menuautocompletion = new JPopupMenu();

		    //the Comment
		    if ("//".startsWith(textField.getText() ))  {
		    	JMenuItem jmiComment= new JMenuItem("// a comment");
				jmiComment.addActionListener(new AutoCompletionActionListener("// a comment" ));
				menuautocompletion.add(jmiComment);
		    }
		    	
		    
		    //the function
		    for(String signature : fd.getSignaturesforautocopmpletion(textField.getText() )) {
				JMenuItem jmi= new JMenuItem(signature);
				jmi.addActionListener(new AutoCompletionActionListener(signature ));
				menuautocompletion.add(jmi);
		    }
	
		    menuautocompletion.setLocation(location.x  , location.y +20  );
		    menuautocompletion.setVisible(true);
		}    
	}
	
	public void keyTyped(KeyEvent e) {}

	

	public void focusGained(FocusEvent e) {
		if (isSelectionInsideInclude()) {
			boolean wasEnabled = this.isEnabled();
			this.setEnabled(false);
			if (wasEnabled) JOptionPane.showMessageDialog(null, "Include script lines can not be modified !");
		} else {
			this.setEnabled(true);
			//find the position of JTextfield in the tree
			path = new ArrayList<Integer>();
			TreePath childpath = tree.getLeadSelectionPath();
			TreeNode parentnode = (TreeNode) childpath.getPathComponent(0);
			for (int i=1 ; i<childpath.getPathCount() ; i++) {
				TreeNode childnode = (TreeNode) childpath.getPathComponent(i);
				
				//TODO : improve because it's a copy of ScriptJpanel function.
				//Hack for loop car parent.getIndex(0) correspond to number of loop in AST
				int offset=0;
				if ( ((DefaultMutableTreeNode) parentnode).toString().startsWith("Loop") ) 
					offset =1;
				if ( ((DefaultMutableTreeNode) parentnode).toString().startsWith("Include") ) 
					offset =1;
				
				path.add(parentnode.getIndex(childnode)+offset);
				parentnode = childnode;
			}
			
			if( " new ".equals(childpath.getLastPathComponent().toString() ) ) {
				modify = false;
			} else {
				modify = true;
			}		
		}
	}
	
	private boolean isSelectionInsideInclude() {
		boolean isSelectionInsideInclude = true;
		for(int i=0; i<tree.getSelectionCount() && isSelectionInsideInclude ; i++) {
			int pathLength = tree.getSelectionPaths()[i].getPathCount();
			boolean includeFound=false;
			int j;
			for (j=0; j<pathLength && !includeFound; j++) {
				if (tree.getSelectionPaths()[i].getPathComponent(j).toString().startsWith("Include")) includeFound=true;
			}
			if (!includeFound || j==pathLength) isSelectionInsideInclude=false;
		}
		return isSelectionInsideInclude;
	}


	public void focusLost(FocusEvent e) {
		if (this.isEnabled()) {
			JTextField myself = (JTextField) e.getSource();
			if (menuautocompletion !=null)
				menuautocompletion.setVisible(false);
		    Boolean result = ScriptController.getScriptController().insertOrModify(
					path, //the position*
					myself.getText(),
					modify ); //and the content 
		    
		    if (!result) {
			    TreeNode noeud =(TreeNode) tree.getModel().getRoot();
			    Boolean areweinloop = false;
			    for (int index : path)
			    	if(areweinloop) 
			    		noeud = noeud.getChildAt(index-1);
			    	else {
			    		areweinloop = true;
			    		noeud = noeud.getChildAt(index);
			    	}
			    //TODO:HACKs  : consequence of hacks in focusGained function
			    
			   ((ScriptTreeRenderer) tree.getCellRenderer()).
			   			addToErrorList( noeud);
			   ((DefaultTreeModel) tree.getModel()).reload();
			   //expand the tree
		        for(int i=0;i<tree.getRowCount();i++)  
		             tree.expandRow(i);
		    }
		}
	}
	
	
	/**
	 * To manage when user click on a item of
	 * Auto completion menu
	 */
	private class AutoCompletionActionListener implements ActionListener{
		private String content;
		
		public AutoCompletionActionListener(String signature ) {
			content = signature;
		};
		public void actionPerformed(ActionEvent arg0) {
			setText(content);
			menuautocompletion.setVisible(false);
			
		}
	}

}
