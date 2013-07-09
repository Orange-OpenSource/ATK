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
 * File Name   : ScriptJPanel.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder.scriptJpanel;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.orange.atk.interpreter.ast.ASTFUNCTION;
import com.orange.atk.interpreter.ast.ASTNUMBER;
import com.orange.atk.interpreter.ast.ASTSTRING;
import com.orange.atk.interpreter.ast.ASTStart;
import com.orange.atk.interpreter.parser.ATKScriptParserTreeConstants;
import com.orange.atk.scriptRecorder.RecorderFrame;
import com.orange.atk.scriptRecorder.ScriptController;



public class ScriptJPanel extends JScrollPane {

	private static final long serialVersionUID = -4677424545754191554L;

	private DefaultMutableTreeNode root;
	private DefaultTreeModel treemodel;
	private ScriptTreeRenderer renderer;
	private Boolean isempty=true;
	private JPopupMenu rightPopup;
	private RecorderFrame recframe;

	protected JTree tree;

	/**
	 * Default constructor
	 */
	public ScriptJPanel(RecorderFrame rf) {
		super();
		recframe = rf;
		root = new DefaultMutableTreeNode("root", true);
		treemodel = new DefaultTreeModel(root);

		tree = new JTree(treemodel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		tree.setShowsRootHandles(true);

		tree.setRootVisible(false);
		tree.putClientProperty("JTree.lineStyle", "None");


		//remove icons on Jtree
		renderer = new ScriptTreeRenderer();
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);
		tree.setCellRenderer(renderer);



		//add the tree
		setPreferredSize(new Dimension(150,400));
		setAutoscrolls(true);
		setViewportView(tree);

		//detect when editing is finished
		TreeCellEditor tce = new DefaultCellEditor(new AutoCompleteJtextField(tree));
		tree.setCellEditor(tce);


		tree.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton()==MouseEvent.BUTTON3){ 
					//RightClick
					if(tree.getSelectionCount()<=1)
						tree.setSelectionPath(tree.getPathForLocation(
								arg0.getX(),
								arg0.getY() ));
					if (isSelectionInsideInclude())
						JOptionPane.showMessageDialog(null, "Include script lines can not be modified !");
					else {
						if (rightPopup ==null)	newRightPopup();
						rightPopup.setLocation(arg0.getXOnScreen(), arg0.getYOnScreen() );
						rightPopup.setVisible(true);
					}
				}

				if (arg0.getButton()==MouseEvent.BUTTON1&& rightPopup!=null)
					rightPopup.setVisible(false);	

			}

			public void mouseEntered(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseReleased(MouseEvent arg0) {}

		});
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

	private boolean isInsideInclude(MutableTreeNode node) {
		if (node == null) return false;
		if ((node.toString()).indexOf("Include")!=-1) return true;
		else return isInsideInclude((MutableTreeNode) node.getParent());
	}

	/**
	 * Method to call when the AST has been modified.
	 * Rebuild UI with ast manipulation classes.
	 * @return
	 */
	public synchronized boolean update() {

		//	Logger.getLogger(this.getClass() ).debug("update de l'ast est appellé");
		ASTStart ast = ScriptController.getScriptController().getAST();

		//build depanding the visitor
		ASTtoJTreeVisitor visitor = new ASTtoJTreeVisitor();

		root.removeAllChildren();

		//build the tree
		if (ast !=null) {
			List<DefaultMutableTreeNode> scripttree 
			=  (List<DefaultMutableTreeNode>) ast.jjtAccept(visitor,null);
			for(DefaultMutableTreeNode el : scripttree)
				root.add(el);
		}

		isempty =  root.isLeaf();

		//       Logger.getLogger(this.getClass() ).debug("estimation de l'arbre graphique :"+root.getLeafCount());

		treemodel.reload();

		expandTree();

		return false;
	}


	/**Convert TreePath in List
	 * 
	 * @param tp  a Tree Path
	 * @return List<Integer> which correspond to indexes of child Node on the way
	 */
	protected List<Integer> toList(TreePath tp) {
		//find the position of JTextfield in the tree
		List<Integer> Path = new ArrayList<Integer>();

		TreeNode parentnode = (TreeNode) tp.getPathComponent(0);
		for (int i=1 ; i<tp.getPathCount() ; i++) {
			TreeNode childnode = (TreeNode) tp.getPathComponent(i);
			//TODO : improve.
			//Hack for loop car parent.getIndex(0) correspond to number of loop in AST
			int offset=0;
			if ( ((DefaultMutableTreeNode) parentnode).toString().startsWith("Loop") ) 
				offset =1;

			Path.add(parentnode.getIndex(childnode)+offset);
			parentnode = childnode;
		}

		return Path;
	}

	protected void newRightPopup() {
		Logger.getLogger(this.getClass() ).debug("create right popup");
		rightPopup = new JPopupMenu();
		JMenuItem jmiDelete =new JMenuItem("Delete Selected Lines");
		jmiDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				TreePath[] tps = tree.getSelectionPaths();
				if (tps !=null) {
					Vector<List<Integer>> treepathsIndex = new Vector<List<Integer>> ();
					for( int i =0 ; i<tps.length ; i++)
						treepathsIndex.add( toList(tps[i]) );

					ScriptController.getScriptController().delete( treepathsIndex);
					recframe.updateScript();
				}
				rightPopup.setVisible(false);
			}
		});
		rightPopup.add(jmiDelete);

		//TODO: active these codes lines
		/*	JMenuItem jmiRun= new JMenuItem("Run Selected Lines");
		jmiRun.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				rightPopup.setVisible(false);
	//			runScript();
			}
		});
		rightPopup.add(jmiRun);
		 */
		JMenuItem jmiSurround=new JMenuItem("Surround with Loop");
		jmiSurround.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				TreePath[] tps = tree.getSelectionPaths();

				if (tps !=null) {
					Vector<List<Integer>> treepathsIndex = new Vector<List<Integer>> ();
					for( int i =0 ; i<tps.length ; i++)
						treepathsIndex.add( toList(tps[i]) );

					String nbLoop = JOptionPane.showInputDialog("Number of Loop :");
					ScriptController.getScriptController().surroundLoop(treepathsIndex ,nbLoop);
					recframe.updateScript();
				}
				rightPopup.setVisible(false);
			}

		});
		rightPopup.add(jmiSurround);

		JMenuItem jmiComment=new JMenuItem("Comment/Uncomment lines");
		jmiComment.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				TreePath[] tps = tree.getSelectionPaths();
				if (tps !=null) {
					for( TreePath tp : tps )
						ScriptController.getScriptController().comment(toList(tp) );

					recframe.updateScript();
				}
				rightPopup.setVisible(false);
			}
		});
		rightPopup.add(jmiComment);




		JMenuItem jmiInsertb=new JMenuItem("Insert line Before ");
		jmiInsertb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//			chooseAction("Insert");
				//after the node selected
				TreePath selected = tree.getSelectionPaths()[0];
				MutableTreeNode newnode = new DefaultMutableTreeNode(" new ");

				if(selected!=null) {
					MutableTreeNode selectednode = (MutableTreeNode) selected.getLastPathComponent();
					MutableTreeNode parent = (MutableTreeNode) selectednode.getParent();
					if (!isInsideInclude(parent)) {
						treemodel.insertNodeInto( newnode,
								parent,
								parent.getIndex(selectednode));

						tree.startEditingAtPath(selected.getParentPath()
								.pathByAddingChild(newnode));
						rightPopup.setVisible(false);
					} else {
						rightPopup.setVisible(false);
						JOptionPane.showMessageDialog(null, "Can not insert line inside Include script");
					}
					//The Tree is empty
				} else {
					root.add(newnode);
					treemodel.reload();
					Object[] path = { root , newnode};
					tree.startEditingAtPath(new TreePath(path) );
					rightPopup.setVisible(false);
				}	
			}
		});
		rightPopup.add(jmiInsertb);

		JMenuItem jmiInsert=new JMenuItem("Insert line after ");
		jmiInsert.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//			chooseAction("Insert");
				//after the node selected
				TreePath selected = tree.getSelectionPaths()[tree.getSelectionCount()-1];
				MutableTreeNode newnode = new DefaultMutableTreeNode(" new ");

				if(selected!=null) {
					MutableTreeNode selectednode = (MutableTreeNode) selected.getLastPathComponent();
					MutableTreeNode parent = (MutableTreeNode) selectednode.getParent();
					if (!isInsideInclude(parent)) {
						treemodel.insertNodeInto( newnode,
								parent,
								parent.getIndex(selectednode)+1);

						tree.startEditingAtPath(selected.getParentPath()
								.pathByAddingChild(newnode));


						rightPopup.setVisible(false);
					} else {
						rightPopup.setVisible(false);
						JOptionPane.showMessageDialog(null, "Can not insert line inside Include script");
					}
					//The Tree is empty
				} else {
					root.add(newnode);
					treemodel.reload();
					Object[] path = { root , newnode};
					tree.startEditingAtPath(new TreePath(path) );
					rightPopup.setVisible(false);
				}
			}
		});
		rightPopup.add(jmiInsert);

		/*	JMenuItem jmiModify=new JMenuItem("Modify this line");
		jmiInsert.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
	//			chooseAction("Modify");
				rightPopup.setVisible(false);
			}
		});
		rightPopup.add(jmiModify);*/
		/*	if(ScriptController.getScriptController().isRecording())
			jmiRun.setEnabled(false);*/
		JMenuItem jmiStartIntstrumetation=new JMenuItem("Start instrumentation");
		jmiStartIntstrumetation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {	
				Thread progress = new Thread(){
					@Override
					public void run() {
						recframe.glassPane.setText("Getting all installed APK");
						recframe.glassPane.start();
						Thread progress1 = new Thread() {
							@Override
							public void run() {
								// recframe.getViewsFunction();
								recframe.selectAPK();
								if(!RecorderFrame.PackageName.equalsIgnoreCase("")&& !RecorderFrame.MainActivityName.equalsIgnoreCase("")&&
										!RecorderFrame.PackageSourceDir.equalsIgnoreCase("")) {
									int index = tree.getSelectionRows()[tree.getSelectionCount()-1];
									ASTStart ast = ScriptController.getScriptController().getAST();

									ASTFUNCTION startTest = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
									startTest.setValue("StartRobotiumTestOn");
									ASTSTRING param1 = new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
									param1.setValue("'"+RecorderFrame.PackageName+"'");
									ASTSTRING param2 = new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
									param2.setValue("'"+RecorderFrame.MainActivityName+"'");
									ASTSTRING param3 = new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
									param3.setValue("'"+RecorderFrame.PackageSourceDir+"'");
									ASTNUMBER param4 = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
									param4.setValue(String.valueOf(RecorderFrame.Versioncode));
									startTest.jjtAddChild(param1, 0);
									startTest.jjtAddChild(param2, 1);
									startTest.jjtAddChild(param3, 2);
									startTest.jjtAddChild(param4, 3);


									ASTFUNCTION exitSolo = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
									exitSolo.setValue("ExitSolo");


									int numberOfchild =ast.jjtGetNumChildren();
									for(int i =numberOfchild+1; i>index+2; i-- ) {
										ast.jjtAddChild(ast.jjtGetChild(i-2),i);
									}

									ast.jjtAddChild(startTest,index+1);
									ast.jjtAddChild(exitSolo,index+2);
									update();
								}
							}
						};
						progress1.start();	
					}
				};
				progress.start();
				rightPopup.setVisible(false);
			}
		});


		rightPopup.add(jmiStartIntstrumetation);


	}



	/**
	 * Return true if the Jtree is Empty
	 * that's means there are no code
	 * @return
	 */
	//TODO : move this kind of verification in controller
	public Boolean isEmpty() {
		return isempty;		
	}

	public synchronized void setRunningNode(int nodeLineNumber) {
		TreeNode node = null;
		nodeLineNumber--;
		expandTree();
		if (nodeLineNumber>=0 && nodeLineNumber<tree.getRowCount()) {
			node = (TreeNode) tree.getPathForRow(nodeLineNumber).getLastPathComponent();
		}
		renderer.setRunningNode(node);
		treemodel.reload();
		expandTree();
		// TODO faire marcher le scroll automatique
		//if (node !=null) tree.scrollRectToVisible(tree.getRowBounds(nodeLineNumber));
	}

	private void expandTree() {
		for(int i=0;i<tree.getRowCount();i++)  
			tree.expandRow(i);   
	}

	public int getSelectedNode(){
		TreePath[] tps = tree.getSelectionPaths();
		if (tps !=null){
			TreePath selected = tree.getSelectionPaths()[tree.getSelectionCount()-1];
			if(selected!=null){
				MutableTreeNode selectednode = (MutableTreeNode) selected.getLastPathComponent();
				MutableTreeNode parent = (MutableTreeNode) selectednode.getParent();
				return parent.getIndex(selectednode);
			}
		}
		return -1;
	}
}