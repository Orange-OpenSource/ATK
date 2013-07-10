/*
 * Copyright (C) 2012 The Android Open Source Project
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
 */

package com.android.uiautomator;
import com.android.uiautomator.UiAutomatorHelper.UiAutomatorException;
import com.android.uiautomator.actions.ExpandAllAction;
import com.android.uiautomator.actions.OpenFilesAction;
import com.android.uiautomator.actions.ScreenshotAction;
import com.android.uiautomator.tree.AttributePair;
import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.UiNode;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.interpreter.ast.ASTFUNCTION;
import com.orange.atk.interpreter.ast.ASTNUMBER;
import com.orange.atk.interpreter.ast.ASTSTRING;
import com.orange.atk.interpreter.ast.ASTStart;
import com.orange.atk.interpreter.parser.ATKScriptParserTreeConstants;
import com.orange.atk.scriptRecorder.InfiniteProgressPanel;
import com.orange.atk.scriptRecorder.RecorderFrame;
import com.orange.atk.scriptRecorder.ScriptController;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;



public class UiAutomatorViewer extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton jbtOpenFolder;
	private JButton jbtScreenshot;
	private JButton jbtExpandAll;
	private JButton jbtStop;
	private JTree mTreeViewer;
	private JTable mTableViewer;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel treemodel;
	private UiAutomatorModel mModel;
	private Image mScreenshot=null;
	private static final int IMG_BORDER = 2;
	private float mScale = 1.0f;
	private int mDx, mDy;
	private ScrenshotCanvas screenshotPanel;
	private RecorderFrame recorderFrame =null;
	public  InfiniteProgressPanel glassPane= new InfiniteProgressPanel();
	private JPopupMenu rightPopup =null;
	private ArrayList<BasicTreeNode> listeOfnodeForThisClass=null;
	public static boolean dumpXMLFirstTime =true;
	private ScreenshotAction screenshotAction=null;
	private OpenFilesAction openFilesAction=null;
	private static final String icondescr = "ATK";
	public static ImageIcon icon = null;

	public UiAutomatorViewer() {

		super("UI Automator Viewer");
		this.setSize(800, 600);
		URL iconURL = CoreGUIPlugin.getMainIcon();
		icon = new ImageIcon(iconURL, icondescr);
		setIconImage(icon.getImage());


		screenshotAction = new  ScreenshotAction(UiAutomatorViewer.this);
		openFilesAction = new OpenFilesAction(UiAutomatorViewer.this);

		JToolBar jtbuttonbar =new JToolBar();

		jbtOpenFolder=new JButton(new ImageIcon(this.getClass().getResource("open-folder.png")));
		jbtOpenFolder.setToolTipText("Open");
		jbtOpenFolder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(!dumpXMLFirstTime) {
					screenshotAction.screenshotAction("exit","Stop instrumentation");
					glassPane.stop();
					jbtStop.setEnabled(false);
				}
				openFilesAction.openFilesAction();
			}
		});

		jbtScreenshot=new JButton(new ImageIcon(this.getClass().getResource("screenshot.png")));
		jbtScreenshot.setToolTipText("Device Screenshot");
		jbtScreenshot.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				screenshotAction.screenshotAction("views","Get UI Dump File and Screenshot");
			}
		});

		jbtExpandAll=new JButton(new ImageIcon(this.getClass().getResource("expandall.png")));
		jbtExpandAll.setToolTipText("Expand All");
		jbtExpandAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new ExpandAllAction().expandTree(mTreeViewer) ;
			}
		});

		jbtStop= new JButton(new ImageIcon(this.getClass().getResource("stop.png")));
		jbtStop.setToolTipText("stop instrumentation");
		jbtStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				screenshotAction.screenshotAction("exit","Stop instrumentation");
				glassPane.stop();
				jbtStop.setEnabled(false);

			}
		});

		jtbuttonbar.add(jbtOpenFolder);
		jtbuttonbar.add(jbtScreenshot);
		jtbuttonbar.add(jbtStop);
		jbtStop.setEnabled(false);

		screenshotPanel = new ScrenshotCanvas();
		screenshotPanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				if (mModel != null && mModel.isExploreMode()) {
					BasicTreeNode node = mModel.updateSelectionForCoordinates(
							getInverseScaledSize(e.getX() - mDx),
							getInverseScaledSize(e.getY() - mDy));
					if (node != null) {
						updateTreeSelection(node);
						loadAttributeTable();
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
			}
		});
		screenshotPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton()==MouseEvent.BUTTON3){
					int index=-1;
					String className=null;
					String texte=null;
					BasicTreeNode node = mModel.getSelectedNode();

					if(node!=null) {
						Object [] attibutes=  node.getAttributesArray();
						for(int i=0; i<attibutes.length; i++) {
							if(((AttributePair)attibutes[i]).key.equalsIgnoreCase("class")){
								className=((AttributePair)attibutes[i]).value;
								className= className.substring(className.lastIndexOf(".")+1);
								continue;
							}
							if(((AttributePair)attibutes[i]).key.equalsIgnoreCase("text")){
								texte=((AttributePair)attibutes[i]).value;
							}
						}
						ArrayList <String> methods= getMethodsList(node,texte,className)	;
						index = getIndexOfthisNode(node);
						if(methods.size()>0) {
							rightPopup = createRightpopup(index, texte, className, methods);
							rightPopup.show(screenshotPanel,arg0.getX(),arg0.getY());
						} else {
							JOptionPane.showMessageDialog(UiAutomatorViewer.this, "no methods available","Warning",JOptionPane.WARNING_MESSAGE);
						}
					}

				}

			}
		});
		JPanel upper_right = new JPanel(new BorderLayout());
		JToolBar toolBarManager= new JToolBar();
		toolBarManager.add(jbtExpandAll);
		upper_right.add(toolBarManager, BorderLayout.PAGE_START);


		root = new DefaultMutableTreeNode("", true);
		treemodel = new DefaultTreeModel(root);
		mTreeViewer = new JTree(treemodel);
		mTreeViewer.setEditable(true);
		mTreeViewer.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
		mTreeViewer.setShowsRootHandles(true);
		mTreeViewer.setRootVisible(false);
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) mTreeViewer.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		mTreeViewer.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				BasicTreeNode selectedNode = null;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) mTreeViewer.getLastSelectedPathComponent();
				if (node == null)     
					return;
				Object o = node.getUserObject();
				if (o instanceof BasicTreeNode) {
					selectedNode = (BasicTreeNode) o;
				}
				mModel.setSelectedNode(selectedNode);
				mTreeViewer.setRootVisible(true);
				redrawScreenshot();
				if (selectedNode != null) {
					loadAttributeTable();
				}
			}
		});

		mTreeViewer.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton()==MouseEvent.BUTTON3){
					int index=-1;
					String className=null;
					String texte=null;

					if(mTreeViewer.getSelectionCount()<=1)
						mTreeViewer.setSelectionPath(mTreeViewer.getPathForLocation(
								arg0.getX(),
								arg0.getY() ));
					TreePath[] tps = mTreeViewer.getSelectionPaths();
					if (tps !=null) {
						TreePath selected = mTreeViewer.getSelectionPaths()[mTreeViewer.getSelectionCount()-1];
						if(selected!=null) {
							DefaultMutableTreeNode selectednode = (DefaultMutableTreeNode) selected.getLastPathComponent();
							Object o = selectednode.getUserObject();
							BasicTreeNode bNode =null;
							if (o instanceof BasicTreeNode) {
								bNode = (BasicTreeNode) o;
							}
							if(bNode!=null){
								Object [] attibutes=  bNode.getAttributesArray();
								for(int i=0; i<attibutes.length; i++) {
									if(((AttributePair)attibutes[i]).key.equalsIgnoreCase("class")){
										className=((AttributePair)attibutes[i]).value;
										className= className.substring(className.lastIndexOf(".")+1);
										continue;
									}
									if(((AttributePair)attibutes[i]).key.equalsIgnoreCase("text")){
										texte=((AttributePair)attibutes[i]).value;
									}
								}
								ArrayList <String> methods= getMethodsList(bNode,texte,className)	;
								index = getIndexOfthisNode(bNode);
								if(methods.size()>0) {
									rightPopup = createRightpopup(index, texte, className, methods);
									rightPopup.show(mTreeViewer,arg0.getX(),arg0.getY());
								} else {
									JOptionPane.showMessageDialog(UiAutomatorViewer.this, "no methods available","Warning",JOptionPane.WARNING_MESSAGE);
								}
							}
						}

					} 

				}


			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

		});

		JScrollPane mtreeViewerpane = new JScrollPane(mTreeViewer, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		upper_right.setBackground(Color.WHITE);
		upper_right.add(mtreeViewerpane, BorderLayout.CENTER);


		JPanel lower_right = new JPanel(new BorderLayout());
		mTableViewer = new JTable(20,2);
		mTableViewer.setTableHeader(null);
		JScrollPane tableContainer = new JScrollPane(mTableViewer,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		lower_right.add(tableContainer, BorderLayout.CENTER);
		tableContainer.setBorder(BorderFactory.createTitledBorder("Node Detail"));

		JSplitPane rightbase= new  JSplitPane(JSplitPane.VERTICAL_SPLIT,upper_right, lower_right);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, screenshotPanel, rightbase);

		splitPane.setOneTouchExpandable(true);
		rightbase.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.625);
		splitPane.setDividerLocation(500);
		rightbase.setResizeWeight(0.5); 

		this.add(jtbuttonbar, BorderLayout.NORTH);
		this.add(splitPane, BorderLayout.CENTER);
		this.setGlassPane(glassPane);

		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}
			@Override
			public void windowClosing(WindowEvent arg0) {
				if(!dumpXMLFirstTime){
					screenshotAction.screenshotAction("exit","Stopping instrumentation");
				}
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
			}
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});

	}

	public void setModel(UiAutomatorModel model, File modelBackingFile, Image screenshot) {
		Logger.getLogger(this.getClass() ).debug("/****UiAutomatorViewer.setModel***/");
		mModel = model;
		mScreenshot = screenshot;
		redrawScreenshot();
		BasicTreeNode rootn=mModel.getXmlRootNode();
		root = (DefaultMutableTreeNode) treemodel.getRoot();
		root.removeAllChildren();
		root.setUserObject(rootn);
		createNodes(rootn,root);
		treemodel.reload(root);
		mTreeViewer.setRootVisible(true);
		this.repaint();
		glassPane.stop();
		if(!dumpXMLFirstTime) {
			jbtStop.setEnabled(true);
		}
	}


	public void loadAttributeTable() {
		if(mModel.getSelectedNode()==null) {
			Logger.getLogger(this.getClass() ).debug("/****UiAutomatorViewer.loadAttributeTable selectNode ==null***/");
			return;
		}
		Object [] attibutes=  mModel.getSelectedNode().getAttributesArray();
		String [][] attibutesPair = new String[attibutes.length][2] ;
		for(int i=0; i<attibutes.length; i++) {
			attibutesPair [i][0]=((AttributePair)attibutes[i]).key;
			attibutesPair [i][1]=((AttributePair)attibutes[i]).value;
		}
		String[] titles = {"", ""};
		((DefaultTableModel) mTableViewer.getModel()).setDataVector(attibutesPair, titles);


	}

	public void createNodes (BasicTreeNode bnode, DefaultMutableTreeNode parent){
		BasicTreeNode[] listenodes = bnode.getChildren();
		for(int i=0; i<listenodes.length; i++) {
			DefaultMutableTreeNode tn= new DefaultMutableTreeNode(((UiNode)listenodes[i]));
			createNodes (listenodes[i],tn);
			parent.add(tn);
		}
	}


	public void updateTreeSelection(BasicTreeNode bnode) {
		DefaultMutableTreeNode root =
				(DefaultMutableTreeNode)mTreeViewer.getModel().getRoot();
		Enumeration e = root.breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
			if(node.getUserObject().equals(bnode)) {
				TreePath path = new TreePath(node.getPath());
				if(node.isLeaf()) {
					DefaultMutableTreeNode parent =
							(DefaultMutableTreeNode)node.getParent();
					expandNode(new TreePath(parent.getPath()));
				} else {
					expandNode(path);
				}
				mTreeViewer.setSelectionPath(path);
				break;
			}
		}
	}


	private void expandNode(TreePath parent) {
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			Enumeration e = node.children();
			while(e.hasMoreElements()) {
				TreeNode n = (TreeNode)e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandNode(path);
			}
		}
		mTreeViewer.expandPath(parent);
	}




	private void redrawScreenshot() {
		screenshotPanel.removeAll();
		screenshotPanel.repaint();
		this.repaint();
	}

	public class ScrenshotCanvas extends JPanel {
		private static final long serialVersionUID = 1L;
		public ScrenshotCanvas() {
			super(new BorderLayout());
			setLayout(null);
		}
		public void paintComponent(Graphics g) {
			if(mScreenshot!=null) {
				Graphics2D g2d = (Graphics2D) g;
				updateScreenshotTransformation();
				g2d.translate(mDx, mDy);
				g2d.scale(mScale, mScale);
				g2d.drawImage(mScreenshot, 0,0, null);
				if(mModel.getSelectedNode()!=null) {
					Rectangle rect = mModel.getCurrentDrawingRect();
					if (rect != null) {
						g2d.setStroke(new BasicStroke(2.0f));
						g2d.setColor(Color.RED);
						g2d.drawRect(rect.x, rect.y,  rect.width, rect.height);
						g2d.setColor(Color.WHITE);
					}
				}

			}
		}

	}



	private void updateScreenshotTransformation() {
		Rectangle canvas = screenshotPanel.getBounds();
		float scaleX = (canvas.width - 2 * IMG_BORDER - 1) / (float)mScreenshot.getWidth(null);
		float scaleY = (canvas.height - 2 * IMG_BORDER - 1) / (float)mScreenshot.getHeight(null);
		// use the smaller scale here so that we can fit the entire screenshot
		mScale = Math.min(scaleX, scaleY);
		// calculate translation values to center the image on the canvas
		mDx = (canvas.width - getScaledSize(mScreenshot.getWidth(null)) - IMG_BORDER * 2) / 2 + IMG_BORDER;
		mDy = (canvas.height - getScaledSize(mScreenshot.getHeight(null)) - IMG_BORDER * 2) / 2 + IMG_BORDER;
	}
	private int getScaledSize(int size) {
		if (mScale == 1.0f) {
			return size;
		} else {
			return new Double(Math.floor((size * mScale))).intValue();
		}
	}
	private int getInverseScaledSize(int size) {
		if (mScale == 1.0f) {
			return size;
		} else {
			return new Double(Math.floor((size / mScale))).intValue();
		}
	}



	public void setRecorderFrame(RecorderFrame recorderFrame) {
		this.recorderFrame = recorderFrame;
	}

	public void addNodeToASTScript(String action, int index, String text) {
		char[] stringArray = action.toCharArray();
		String command=action;
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		action = new String(stringArray);

		ASTFUNCTION function = new ASTFUNCTION(ATKScriptParserTreeConstants.JJTFUNCTION);
		function.setValue(action);
		if(text!=null && text.length()>0){
			command+=",1"+",string,"+text;
			ASTSTRING param= new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
			param.setValue("'"+text+"'");
			function.jjtAddChild(param, 0);
			Logger.getLogger(this.getClass() ).debug("Selected Action: " +action+"('"+text+"'"+")");
		} else {
			command+=",1"+",int,"+index;
			ASTNUMBER param = new ASTNUMBER(ATKScriptParserTreeConstants.JJTNUMBER);
			param.setValue(String.valueOf(index));
			function.jjtAddChild(param, 0);
			if(action.equalsIgnoreCase("enterText")){
				command+=",2"+",int"+",string,"+index+", ";
				ASTSTRING param1= new ASTSTRING(ATKScriptParserTreeConstants.JJTSTRING);
				param1.setValue("''");
				function.jjtAddChild(param1, 1);
			}
		}
		try {
			if(!dumpXMLFirstTime){
				Logger.getLogger(this.getClass()).debug("/****UiAutomatorViewer.command to send ***/ "+ command);
				UiAutomatorHelper.executeRobotiumCommand(command);
				if(!action.equalsIgnoreCase("enterText")){
					screenshotAction.screenshotAction("views","Refreshing Views");
				}
			}
		} catch (UiAutomatorException e) {
			Logger.getLogger(this.getClass()).debug("/****UiAutomatorViewer. exception while sendCommandToRobotiumTest***/");
		}
		if(recorderFrame!=null){
			ASTStart ast = ScriptController.getScriptController().getAST();
			int selectedIndex =recorderFrame.getSelectedNode();
			if(selectedIndex<0){
				JOptionPane.showMessageDialog(UiAutomatorViewer.this, "You must select a node in the script panel before adding new action/node","Error",JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				int numberOfchild =ast.jjtGetNumChildren();
				for(int i =numberOfchild; i>selectedIndex+1; i-- ) {
					ast.jjtAddChild(ast.jjtGetChild(i-1),i);
				}
				ast.jjtAddChild(function,selectedIndex+1);
				recorderFrame.updateAST();
			}
		}
	}

	protected void getListOfViews(String classe,BasicTreeNode rootNode){
		BasicTreeNode[] listenodes = rootNode.getChildren();
		for(int i=0; i<listenodes.length; i++) {
			Object [] attibutes= listenodes[i].getAttributesArray();
			for(int j=0; j<attibutes.length; j++) {
				if(((AttributePair)attibutes[j]).key.equalsIgnoreCase("class")) {
					if(((AttributePair)attibutes[j]).value.equalsIgnoreCase(classe)) {
						listeOfnodeForThisClass.add(listenodes[i]);
						break;
					}
				}
			}
			getListOfViews(classe,listenodes[i]);

		}
	}
	protected int getIndexOfthisNode(BasicTreeNode node){
		listeOfnodeForThisClass = new ArrayList<BasicTreeNode>();
		Object [] attibutes=node.getAttributesArray();
		String className=null;
		for(int j=0; j<attibutes.length; j++) {
			if(((AttributePair)attibutes[j]).key.equalsIgnoreCase("class")) {
				className =((AttributePair)attibutes[j]).value;
				continue;
			}
		}
		if(className!=null){
			getListOfViews(className,mModel.getXmlRootNode());
		}
		return listeOfnodeForThisClass.indexOf(node);
	}

	protected JPopupMenu createRightpopup (int index, String texte, String className,ArrayList <String> methods){
		rightPopup = new JPopupMenu();
		JMenuItem menuItem = null;
		final int indexN=index;
		final String texteN=texte;
		final String classname =className;
		for (String m : methods) {
			menuItem=new JMenuItem(m);
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(classname.toLowerCase().contains("edittext")){
						addNodeToASTScript(e.getActionCommand(),indexN,null);  
					} else {
						addNodeToASTScript(e.getActionCommand(),indexN,texteN);
					}
					rightPopup.setVisible(false);
				}
			});
			rightPopup.add(menuItem);
		}
		return rightPopup;
	}

	protected ArrayList <String> getMethodsList (BasicTreeNode node,String texte,String className){
		Class<?> c = null;
		ListMethodSolo list = new ListMethodSolo();
		c =list.getClass();
		Method[] allMethods = c.getDeclaredMethods();
		ArrayList <String> methods = new  ArrayList<String>();
		for (Method m : allMethods) {
			if(className.toLowerCase().contains("list")) {
				if(m.getName().toLowerCase().contains("list")){
					if(!methods.contains(m.getName())) {
						methods.add(m.getName());
					}
				}
				continue;
			}
			if(className.toLowerCase().contains("button")) {
				if(m.getName().toLowerCase().contains("clickonbutton")){
					if(!methods.contains(m.getName())) {
						methods.add(m.getName());
					}
				}
				continue;
			}

			if(m.getName().toLowerCase().contains(className.toLowerCase())){
				if(!methods.contains(m.getName())) {
					methods.add(m.getName());
				}
			}
		}
		if(texte!=null && texte.length()>0) {
			methods.add("clickOnText");
			methods.add("isTextChecked");
		}
		if(className!=null && className.toLowerCase().contains("edittext")) {
			methods.add("enterText");
		}
		return methods;
	}

}

