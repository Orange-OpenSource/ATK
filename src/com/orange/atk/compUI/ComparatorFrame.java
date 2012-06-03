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
 * File Name   : ComparatorFrame.java
 *
 * Created     : 04/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compUI;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.xml.DOMConfigurator;

import com.orange.atk.compModel.ComparaisonCouple;
import com.orange.atk.compModel.DirectoryFileFilter;
import com.orange.atk.compModel.ImageFileMask;
import com.orange.atk.compModel.Mask;
import com.orange.atk.compModel.Model;
import com.orange.atk.compModel.ProgressListener;
import com.orange.atk.platform.Platform;



/**
 * This class uses a very simple, naive similarity algorithm to compare an image
 * with an other one.
 */

public class ComparatorFrame extends JFrame implements Observer, ProgressListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String VERSION = "ScreenShot Comparator - Beta 2.1";
	private static String basePath="src/images/";

	private JPanel mainPanel;
	private JMenuItem jmiPrintToPDF;
	private JLabel jlbLeft;
	private JLabel jlbRight;
	private JButton jbRight;
	private JButton jbLeft;
	private JButton jbRightDifferent;
	private JButton jbLeftDifferent;
	private JButton jbPass;
	private JButton jbFail;

	private JLabel jlnumberTotalFail;
	private JLabel jlbCounter;
	private MyDisplayJAI dispLeft;
	private MyDisplayJAI dispRight;
	private MyJTextArea taComment;
	private MyJTextArea taRefScDesc;
	private JButton jbRecalcul;
	private ProgressMonitor progressBar;


	//Variables used for the mask (for event click)
	private Date maskTimeLastEvent;
	private int maskLastX;
	private int maskLastY;

	//Variables used for the mask (for event drop)
	private int moussePressedX;
	private int moussePressedY;
	private int maskLeftX;
	private int maskTopY;
	private int maskRightX;
	private int maskBottomY;
	private int activeCell;
	
	private ListMask listMask;
	private JScrollPane scrollPaneMask; 
	
	Box topBox;
	private static Color colorOk = new Color(100,200,50);//green;
	private static Color colorKo = new Color(250,150,50);//orange;
	
	private MyJTextArea taSumary;
	
	private JMenuItem jmiChangeDir;
	private JMenuItem jmiAbout;
	private JMenu jmZoom;

	private int currentIndex=0;
	private ArrayList<ComparaisonCouple> couples;
	private Model model;
	private JLabel jlbMask;
	private Double zoom = 1.0;
	private int zoomLevel = Mask.getCELL_HALF_SIZE();
	private int taCommentDefaultWidth = 0;
	private int taRefScDescDefaultWidth = 0;
	private ComparatorFrame comp;
	/**
	 * The constructor, which creates the GUI.
	 * @param model 
	 */
	public ComparatorFrame(Model model) throws IOException {
		super("ScreenShot Comparator");
		UIManager.put("OptionPane.cancelButtonText", "Close");
		UIManager.put("ProgressMonitor.progressText", "Comparison progress ...");

		comp = this;
		
		// If comparison succeeds for all images, print report and exit
		this.model = model;
		couples=model.getCouplesComparaison();

		if (model.getNbFail()==0){
			int ret= JOptionPane.showConfirmDialog(this, 
					"All screenshot are similar.\nDo you want to print the PDF report?",
					"Report", JOptionPane.OK_CANCEL_OPTION);
			if (ret==JOptionPane.OK_OPTION){
				printReport();
			}
			//return;
		}

		//Initialize the variables 
		maskTimeLastEvent = new Date();
		maskLastX = -1;
		maskLastY = -1;
		maskLeftX = -1;
		maskTopY = -1;
		maskRightX = -1;
		maskBottomY = -1;
		activeCell = -1;

		//------------ Creates ScreenshotComparator UI --------------//

		//MENU BAR

		// File Menu
		JMenuBar jmb =new JMenuBar();
		JMenu jmFile= new JMenu("File");
		jmiChangeDir =new JMenuItem("Change Image Directories");
		jmiChangeDir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				changeDirectories();
			}
		});
		jmFile.add(jmiChangeDir);
		jmFile.add(new JSeparator());
		jmiPrintToPDF=new JMenuItem("Print Report in PDF ");
		jmiPrintToPDF.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				printReport();
			}

		});
		jmFile.add(jmiPrintToPDF);
		jmb.add(jmFile);

		// Zoom Menu
		jmZoom= new JMenu("Zoom");
		JMenuItem jmiNoZoom = new JMenuItem("No Zoom");		
		jmiNoZoom.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				noZoom();
			}
		});
		jmZoom.add(jmiNoZoom);
		JMenuItem jmiZoomIn = new JMenuItem("Zoom In");
		jmiZoomIn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				zoomIn();
			}
		});
		jmZoom.add(jmiZoomIn);
		JMenuItem jmiZoomOut = new JMenuItem("Zoom Out");
		jmiZoomOut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				zoomOut();
			}
		});
		jmZoom.add(jmiZoomOut);
		jmb.add(jmZoom);			


		// Help Menu
		JMenu jmHelp= new JMenu("Help");
		jmiAbout = new JMenuItem("About ScreenShot Comparator ...");
		jmiAbout.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				about();
			}

		});
		jmHelp.add(jmiAbout);
		jmb.add(Box.createHorizontalGlue());
		jmb.add(jmHelp);			
		this.setJMenuBar(jmb);



		// CONTENT PANEL
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());

		//Default constraints
		//Top left with no insets or a 0.1 weight (few move on resizing)
		GridBagConstraints gbc = new GridBagConstraints(
				0,0, //gridx, gridy
				1,1, //gridwidth, gridheight
				0,0, //weightx, weighty
				GridBagConstraints.CENTER, // anchor
				GridBagConstraints.NONE, // FILL
				new Insets(1,1,1,1), // padding top, left, bottom, right
				0,0); //ipadx, ipady
		
		//-----------------------------------------------// 
		//                 Information
		//-----------------------------------------------//
		topBox = Box.createHorizontalBox();
		updateNumberTotalFail();
		jlnumberTotalFail.setFont(new Font(null,1,20));
		gbc.gridwidth=3;
		gbc.gridx=0;
		gbc.anchor=GridBagConstraints.CENTER;
		topBox.add(jlnumberTotalFail);
		topBox.setOpaque(true);
		topBox.setMinimumSize(getMaximumSize());
		if(couples.get(currentIndex).isPass()){
			topBox.setBackground(colorOk);
		}
		else{
			topBox.setBackground(colorKo);
		}
		mainPanel.add(topBox,gbc);
		
		taSumary=new MyJTextArea(3,10);
		taSumary.setFont(new Font(null,1,15));
		taSumary.setEnabled(false);
		taSumary.setBorder(BorderFactory.createLineBorder(Color.black));
		taSumary.setBackground(Color.GRAY);
		taSumary.setText(model.getNbFail()+ " FAILED\n"+
				(model.getNbImages()-model.getNbFail())+ " PASSED\n"+
				model.getNbImages()+ " TOTAL");

		gbc.gridx++;
		gbc.anchor=GridBagConstraints.EAST;
		mainPanel.add(taSumary,gbc);
		

		
		//------------------  1ST LINE ------------------// 
		// Contains labels for the images and the masks
		//-----------------------------------------------// 
		gbc.gridy++;
		
		// Reference image labels
		Box jpTopL = Box.createVerticalBox();
		JLabel jlbRef=new JLabel("Reference Screen Shot" );
		jlbLeft=new JLabel(couples.get(currentIndex).getImgRefId());	
		jpTopL.add(jlbRef);
		jpTopL.add(jlbLeft);
		gbc.anchor=GridBagConstraints.CENTER;
		gbc.gridwidth=1;
		gbc.gridx=0;
		mainPanel.add(jpTopL, gbc);

		jlbCounter = new JLabel((currentIndex+1)+" / "+model.getNbImages());
		jlbCounter.setFont(new Font(null,1,16));
		gbc.gridwidth=3;
		gbc.gridx=0;
		mainPanel.add(jlbCounter, gbc);
		gbc.gridwidth=1;

		// Test image labels
		Box jpTopR = Box.createVerticalBox();
		JLabel jlbTest=new JLabel("Test Screen Shot");
		jlbRight=new JLabel(couples.get(currentIndex).getImgTest().getName());
		jpTopR.add(jlbTest);
		jpTopR.add(jlbRight);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx=2;
		mainPanel.add(jpTopR,gbc);


		// Mask labels
		Box jpTopM = Box.createVerticalBox();
		jpTopM.add(new JLabel("List of mask") );
		String imgRefId = couples.get(currentIndex).getImgRefId();
		int numberOfMask = model.getRefImage(imgRefId).getMaskListId().size();
		String message = "there ";
			message+=numberOfMask;
		if(numberOfMask<=1)
			message += " mask is currently selected";
		else
			message += " masks are currently selected";
		jlbMask = new JLabel(message);
		jpTopM.add(jlbMask);
		gbc.gridx=3;
		mainPanel.add(jpTopM, gbc);

		
		
		//------------------  2ND LINE ------------------// 
		//  Contains the images and the list of mask
		//-----------------------------------------------// 

		// Reference image
		dispLeft=new MyDisplayJAI(couples.get(currentIndex), model, false);
		dispLeft.setName("dispLeft");
		dispLeft.setBorder(BorderFactory.createLineBorder(Color.black));
		dispLeft.addListener(this);
		
		gbc.anchor=GridBagConstraints.CENTER;		
		gbc.gridx=0;			
		gbc.gridy++;
		mainPanel.add(dispLeft,gbc);

		// Test image
		dispRight=new MyDisplayJAI(couples.get(currentIndex), model,true);
		dispRight.setName("dispRight");
		dispRight.setBorder(BorderFactory.createLineBorder(Color.black));
		dispRight.addListener(this);
		
		gbc.insets=new Insets(0,0,0,5);
		gbc.gridx=2;
		mainPanel.add(dispRight,gbc);

		
		// List of masks
		listMask = new ListMask(model.getListMask(),this,model.getMaskWidth(),model.getMaskHeight());

		//Now create a scrollpane;
		scrollPaneMask = new JScrollPane();

		//Make the listBox with Checkboxes look like a rowheader. 
		//This will place the component on the left corner of the scrollpane
		scrollPaneMask.setRowHeaderView(listMask.getListCheckBox());

		//Now, make the listbox with actual descriptions as the main view
		scrollPaneMask.setViewportView(listMask.getListLabel());
		scrollPaneMask.setPreferredSize(new Dimension(300, model.getImageHeight()));
		scrollPaneMask.setBorder(BorderFactory.createLineBorder(Color.black));

		gbc.gridx=3;
		mainPanel.add(scrollPaneMask,gbc);


		//------------------  3RD LINE ------------------// 
		//        Arrow buttons, number of images 
		//            and Combobox for mask
		//-----------------------------------------------// 

		// Left arrow buttons
		Box jpButtonsLeft = Box.createHorizontalBox();
		
		Box jpcolorInformation = Box.createVerticalBox();
		
		int sizeIcon = 15;
		JLabel colorSelected = new JLabel("Mask selected");
		BufferedImage imageSelected = new BufferedImage(sizeIcon,sizeIcon, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) imageSelected.getGraphics();
		g2d.setColor(MyDisplayJAI.colorMaskSelected);
		g2d.fillRect(0,0,sizeIcon,sizeIcon);
		colorSelected.setIcon(new ImageIcon(imageSelected));
		jpcolorInformation.add(colorSelected);
		
		JLabel colorMask = new JLabel("Sum of the masks");
		BufferedImage imageSum = new BufferedImage(sizeIcon,sizeIcon, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) imageSum.getGraphics();
		g2d.setColor(MyDisplayJAI.colorSumMask);
		g2d.fillRect(0,0,sizeIcon,sizeIcon);
		colorMask.setIcon(new ImageIcon(imageSum));
		jpcolorInformation.add(colorMask);

		jpcolorInformation.setPreferredSize(new Dimension(150, 20));
		jpButtonsLeft.add(jpcolorInformation);
		
		
		jbLeftDifferent = new JButton(new ImageIcon(new File("res/tango/back_red.png").toURI().toURL()));
		jbLeftDifferent.setName("jbleftdifferent");
		jbLeftDifferent.setToolTipText("Go to the previous different images");
		jbLeftDifferent.setPreferredSize(new Dimension(40,40));
		jbLeftDifferent.setSize(40,40);
		jbLeftDifferent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previousDifferent();
			}
		});
		jpButtonsLeft.add(jbLeftDifferent);

		jbLeft = new JButton(new ImageIcon(new File("res/tango/back.png").toURI().toURL()));
		jbLeft.setName("jbleft");
		jbLeft.setToolTipText("Go to the previous images");
		jbLeft.setPreferredSize(new Dimension(40,40));
		jbLeft.setSize(40,40);
		jbLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previous();
			}
		});
		jpButtonsLeft.add(jbLeft);

		gbc.gridx=0;
		gbc.gridy++;
		gbc.insets=new Insets(5,10,0,10);
		gbc.anchor=GridBagConstraints.CENTER;
		mainPanel.add(jpButtonsLeft,gbc);

		// Right arrow buttons

		Box jpButtonsRight = Box.createHorizontalBox();

		jbRight = new JButton(new ImageIcon(new File("res/tango/forward.png").toURI().toURL()));
		jbRight.setName("jbright");
		jbRight.setToolTipText("Go to the next images");
		jbRight.setPreferredSize(new Dimension(40,40));
		jbRight.setSize(40,40);
		jbRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				next();
			}
		});
		jpButtonsRight.add(jbRight);

		jbRightDifferent = new JButton(new ImageIcon(new File("res/tango/forward_red.png").toURI().toURL()));
		jbRightDifferent.setName("jbrightdifferent");
		jbRightDifferent.setToolTipText("Go to the next different images");
		jbRightDifferent.setPreferredSize(new Dimension(40,40));
		jbRightDifferent.setSize(40,40);
		jbRightDifferent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextDifferent();
			}
		});
		jpButtonsRight.add(jbRightDifferent);

		
		JLabel colorSimilar = new JLabel("Similar part");
		BufferedImage imageSimilar = new BufferedImage(sizeIcon+20,sizeIcon, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) imageSimilar.getGraphics();
		g2d.setColor(MyDisplayJAI.colorSimilarPart);
		g2d.fillRect(20,0,sizeIcon,sizeIcon);
		colorSimilar.setIcon(new ImageIcon(imageSimilar));
		jpButtonsRight.add(colorSimilar);
		
		
		
		gbc.anchor=GridBagConstraints.WEST;
		gbc.gridx=2;
		mainPanel.add(jpButtonsRight,gbc);

		// Mask : Button ADD
		JButton addMask = new JButton("Add a mask");
		addMask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addMaskAction();
			}
		});
	
		// Mask : Button REMOVE
		gbc.gridx=3;
		JButton removeMask = new JButton("Remove a mask");
		removeMask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeMaskAction();
			}
		});
		// Mask : Inverse the mask
		JButton inverseMask = new JButton("Inverse a mask");
		inverseMask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inverseMaskAction();
			}
		});
		
		Box jpMaskButton = Box.createHorizontalBox();
		jpMaskButton.add(addMask);
		jpMaskButton.add(removeMask);
		jpMaskButton.add(inverseMask);
		gbc.gridx=3;
		gbc.anchor=GridBagConstraints.CENTER;
		mainPanel.add(jpMaskButton,gbc);


		//------------------  4TH LINE ------------------// 
		//                Action buttons
		//-----------------------------------------------// 

		gbc.gridy++;

		//Action buttons : Pass
		jbPass=new JButton("Pass");
		jbPass.setName("jbPass");
		jbPass.setSize(new Dimension(100, 50));
		jbPass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pass();
			}
		});
		gbc.gridx=0;
		gbc.anchor=GridBagConstraints.EAST;
		mainPanel.add(jbPass,gbc);

		//Action buttons : Fail
		jbFail= new JButton("Fail");
		jbFail.setName("jbFail");
		jbFail.setSize(new Dimension(100, 50));
		jbFail.setToolTipText("Please Insert Comment Before Click Fail");
		jbFail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fail();
			}
		});
		gbc.gridx=2;
		gbc.anchor=GridBagConstraints.WEST;
		mainPanel.add(jbFail,gbc);

		//Action buttons : Recompute differences
		jbRecalcul=new JButton("Recompute differences");
		jbRecalcul.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (progressBar!=null) progressBar.close();
				progressBar = null;
				progressBar = new ProgressMonitor(comp, null,"",0,101);
				progressBar.setMillisToPopup(0);
				jbRecalcul.setEnabled(false);
				pack();
				Thread t = new Thread() {
					  public void run() {
						  recompute();
					  }
				};
				t.start();
			}
		});
		gbc.gridx=3;
		gbc.anchor=GridBagConstraints.CENTER;
		mainPanel.add(jbRecalcul,gbc);

		//--------------  6TH and 7TH LINE --------------// 
		//                    Comments
		//-----------------------------------------------// 
		JLabel refScTitle = new JLabel("Ref. screenshot description:");
		gbc.gridx=0;
		gbc.gridy++;
		gbc.gridwidth=1;
		gbc.anchor=GridBagConstraints.WEST;
		mainPanel.add(refScTitle,gbc);
		
		taRefScDesc=new MyJTextArea();
		taRefScDesc.setRows(4);
		taRefScDesc.setVisible(true);
		taRefScDesc.setEditable(true);
		taRefScDesc.setLineWrap(true);
		CompoundBorder innerCompound = new CompoundBorder(new EmptyBorder(3, 3, 3, 3), new EmptyBorder(0,0,0,0));
		CompoundBorder outerCompound = new CompoundBorder(new LineBorder(Color.DARK_GRAY, 1), innerCompound);
		taRefScDesc.setBorder(outerCompound);
		gbc.gridy++;
		gbc.insets=new Insets(3,3,3,3);
		gbc.fill=GridBagConstraints.BOTH;
		mainPanel.add(taRefScDesc,gbc);
		taRefScDesc.addFocusListener(new FocusListener() {
		      public void focusGained(FocusEvent e) {
		      }

		      public void focusLost(FocusEvent e) {
		        	refScDescChanged();
		      }
		});


		JLabel commentTitle = new JLabel("Comment about Failed test screenshot:");
		gbc.gridx=1;
		gbc.gridy--;
		gbc.gridwidth=3;
		mainPanel.add(commentTitle,gbc);

		taComment=new MyJTextArea();
		taComment.setRows(4);
		taComment.setVisible(true);
		taComment.setEditable(true);
		taComment.setLineWrap(true);
		taComment.setBorder(outerCompound);
		if(couples.get(currentIndex).isPass()){
			taComment.setEnabled(false);
			taComment.setBackground(Color.GRAY);
		}
		else{
			taComment.setEnabled(true);
			taComment.setBackground(Color.WHITE);
		}

		taComment.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
				//commentChanged();
			}
			public void insertUpdate(DocumentEvent arg0) {
				commentChanged();
			}
			public void removeUpdate(DocumentEvent arg0) {
				commentChanged();
			}
		});

		gbc.gridy++;
		gbc.insets=new Insets(3,3,3,3);
		gbc.fill=GridBagConstraints.BOTH;
		mainPanel.add(taComment,gbc);

		// Add Scrollpanel
		setContentPane(new JScrollPane(mainPanel));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		pack();
		setVisible(true);
		update(this.getGraphics());
	}
	
	public void setProgressValue(int value) {
		progressBar.setProgress(value);
		if (value==100) {
			jbRecalcul.setEnabled(true);
		}
	}
	
	public void setNbFailed(int value) {
		progressBar.setNote(value+" / "+model.getNbImages()+" image(s) failed");
	}


	/************************************** MENU ACTIONS ********************************************/


	/****************** File Menu ********************/
	// "Change Image Directories" Menu Item action
	protected void changeDirectories() {
		ChangeDirectoriesDialog cdd = new ChangeDirectoriesDialog(null, model);
		if(cdd.getAction()==JOptionPane.OK_OPTION){
			model.setDirectories(cdd.getRefPath(), cdd.getTestPath());
		}
	}

	// "Print PDF Report" Menu Item action
	protected void printReport() {
		//First we save the report
		model.saveReport();

		//Then we print it in PDF
		model.printPDFReport();

		int ret= JOptionPane.showConfirmDialog(this, "The PDF report has been printed. Click OK and it will be open." , "PDF printed", JOptionPane.OK_CANCEL_OPTION);
		if (ret==JOptionPane.OK_OPTION){
			try {
				java.awt.Desktop.getDesktop().open(new File(model.getTestDirectory()+Platform.FILE_SEPARATOR+model.getPdfReportName()));
				if (model.getNbFail()<=0){
					this.setVisible(false);
					this.dispose();
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/****************** Zoom Menu ********************/
	protected void noZoom() {
		zoom = 1.0;
		zoomLevel = Mask.getCELL_HALF_SIZE();
		zoomUI(zoom);
		jmZoom.setText("Zoom");
		zoomTA(zoom);
		update(this.getGraphics());
		pack();
	}

	protected void zoomIn() {
		if ((zoomLevel + 1) < 2 * Mask.getCELL_HALF_SIZE()) {
			zoomLevel++;
			zoom = (double) zoomLevel / (double) Mask.getCELL_HALF_SIZE();
			zoomUI(zoom);
			if (!zoom.equals(1.0)) 
				jmZoom.setText("Zoom ["+ (int)(100*zoom) + "%]");
			else 
				jmZoom.setText("Zoom");
			zoomTA(zoom);
			update(this.getGraphics());
			pack();
		}
	}

	protected void zoomOut() {
		if ((zoomLevel - 1) > 0) {
			zoomLevel--;
			zoom = (double) zoomLevel / (double) Mask.getCELL_HALF_SIZE();
			zoomUI(zoom);
			if (!zoom.equals(1.0))
				jmZoom.setText("Zoom ["+ (int)(100*zoom) + "%]");
			else 
				jmZoom.setText("Zoom");
			zoomTA(zoom);
			update(this.getGraphics());
			pack();
		}
	}

	private void zoomTA(double zoom) {
		if (this.taCommentDefaultWidth==0) {
			taCommentDefaultWidth = taComment.getWidth();
			taRefScDescDefaultWidth = taRefScDesc.getWidth();
		}
		
		//taRefScDesc.setSize((int) (taRefScDescDefaultWidth*zoom), taRefScDesc.getHeight());
		Dimension taRefScDescDim = new Dimension((int) (taRefScDescDefaultWidth*zoom), taRefScDesc.getHeight());
		taRefScDesc.setPreferredSize(taRefScDescDim);
		taRefScDesc.setMaximumSize(taRefScDescDim);
		Dimension taCommentDim = new Dimension((int) (taCommentDefaultWidth*zoom), taComment.getHeight());
		taComment.setPreferredSize(taCommentDim);
		taComment.setMaximumSize(taCommentDim);

	}
	
	private void zoomUI(double zoom) {
		dispLeft.setZoom(zoom);
		dispRight.setZoom(zoom);
		scrollPaneMask.setPreferredSize(new Dimension(300, (int) (model.getImageHeight()*zoom)));
	}

	/****************** Help Menu ********************/
	protected void about() {
		new AboutDialog(this,VERSION);
	}



	/**
	 * called when the Pass Button is pressed
	 */
	protected void pass() {
		couples.get(currentIndex).setComment( taComment.getText());
		couples.get(currentIndex).setPass(Model.MANUALLY_PASS);
		model.saveReport();
		next();
	}


	/** 
	 * called when the Fail Button is pressed  
	 */
	protected void fail() {
		couples.get(currentIndex).setComment( taComment.getText());
		couples.get(currentIndex).setPass(Model.FAIL);
		model.saveReport();
		next();
	}


	/** 
	 * called when the previous button is pressed 
	 */
	protected void previous() {
		if (currentIndex>0)
			currentIndex--;
		update(this.getGraphics());
	}
	/** 
	 * called when the previousDifferent button is pressed 
	 */
	protected void previousDifferent() {
		int previousIndex = currentIndex;
		while (previousIndex>0){
			previousIndex--;
			if(!couples.get(previousIndex).isPass()){
				currentIndex = previousIndex;
				update(this.getGraphics());
				return;
			}
		}
		return;
	}

	/**
	 *  called when Next, Pass of Fail Button is pressed.
	 *  Goes to the next couple of image
	 */
	protected void next() {
		if(currentIndex<getBiggestIndex()) 
			currentIndex++;
		update(this.getGraphics());
	}
	/**
	 *  called when NextDifferent button is pressed.
	 *  Goes the next different couple 
	 */
	protected void nextDifferent() {
		int nextIndex = currentIndex;
		while (nextIndex<getBiggestIndex()){
			nextIndex++;
			if(!couples.get(nextIndex).isPass()){
				currentIndex = nextIndex;
				update(this.getGraphics());
				return;
			}
		}
		return;
	}
	/**
	 * Action to add a mask when the button is clicked
	 */
	private void addMaskAction() {

		String name =JOptionPane.showInputDialog(
				"Please, type of the name of the new mask");
		if(name==null)
			return;
		Mask mask = createMask(name);

		Object[] options = {"Set only to the current images",
        "Set for all"};

		int option =JOptionPane.showOptionDialog(null,
				"Do you want to set the new mask to all " +
				"or only the current images?",
				"Set to all masks?", 
				JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE,
				null, 
				options, 
				options[0]);		
		
		if(option == 0)
			getCurrentImgRef().addMask(mask.getId());
		else{
			for(String image : model.getListRefImage())
				model.getRefImage(image).addMask(mask.getId());
		}
		update(this.getGraphics());
	}
	
	/**
	 * Create a mask and add it in the list
	 * @param label
	 * @return new mask
	 */
	private Mask createMask(String label) {
		Integer nextId = 0;
		for(Integer Id : model.getListKeysetMask()){
			if(nextId<Id)
				nextId = Id;
		}
		nextId++;
		Mask mask = new Mask(label,nextId,listMask.getMaskWidth(),listMask.getMaskHeight());
		model.addRefMask(mask);
		listMask.add(mask);
		listMask.setSelectedMask(mask);
		return mask;
	}
	
	/**
	 * Action to remove a mask when the button is clicked
	 */
	private void removeMaskAction() {

		Mask mask = listMask.getSelectedMask();
		if(mask == null){
			JOptionPane.showMessageDialog(null, "You must select a mask from the list.");
			return;
		}
	
		int value = JOptionPane.showConfirmDialog(null, 
				"Do you really want to delete the mask \""+mask+"\" ?",
				"Delete the mask?", 
				JOptionPane.OK_CANCEL_OPTION);
		if(value == JOptionPane.OK_OPTION){
			//We remove the mask for every image
			for(String image : model.getListRefImage())
				model.getRefImage(image).removeMask(mask.getId());
			//We remove the mask from the list of reference mask
			model.removeRefMask(mask);
			//We remove the mask from the UI with the list of masks
			listMask.removeMask(mask);
			scrollPaneMask.repaint();
			model.saveMaskAssociations();
		}
	}
	
	/**
	 * Inverse a mask 
	 */
	private void inverseMaskAction() {

		Mask mask = listMask.getSelectedMask();
		if(mask == null){
			JOptionPane.showMessageDialog(null, "You must select a mask from the list.");
			return;
		}
	
		for(int x=0;x<listMask.getMaskWidth();x++){
			for(int y=0;y<listMask.getMaskHeight();y++){
				mask.setCell(x, y, !mask.getCell(x, y));
			}
		}
		model.saveMaskAssociations();
		scrollPaneMask.repaint();
		update(this.getGraphics());
	}
	
	/**
	 *  Return the biggest possible index for image
	 */
	int getBiggestIndex(){
		return (model.getNbImages()-1);
	}

	/** 
	 * called on changing in taComment
	 */
	protected void commentChanged() {
		couples.get(currentIndex).setComment(taComment.getText());
	}

	/** 
	 * called on changing in refScDescComment
	 */
	protected void refScDescChanged() {
		model.setRefScDescription(couples.get(currentIndex).getImgRefId(), taRefScDesc.getText());
	}

	/** 
	 * Add/remove a cell into/from the mask
	 */
	protected void addZone(MouseEvent arg0) {

		Mask activeMask = (Mask)listMask.getListLabel().getSelectedValue();
		
		int x = moussePressedX;
		int y = moussePressedY;

		Date newDate = new Date();
		//We check the same square already get an event in the last 1000ms
		//if it's the case, it will be ignored
		if((x==maskLastX)&&(y==maskLastY)){
			if((newDate.getTime()-maskTimeLastEvent.getTime())<1000){
				maskTimeLastEvent = new Date();
				return;
			}
		}

		activeMask.setCell(x, y, !activeMask.getCell(x, y));	

		update(this.getGraphics());
		model.saveMaskAssociations();

		//Update the values
		maskTimeLastEvent = new Date();
		maskLastX = x;
		maskLastY = y;
	}
	
	
	/** 
	 * Add/remove a zone which is dragged into/from the mask
	 */
	protected void addZoneDragged(MouseEvent arg0) {

		Mask activeMask = (Mask)listMask.getListLabel().getSelectedValue();

		int x0 = arg0.getX()/((int)(2*Mask.getCELL_HALF_SIZE()*zoom));
		int y0 = arg0.getY()/((int)(2*Mask.getCELL_HALF_SIZE()*zoom));

		Boolean valueCell = false;
		if(activeCell==1){
			valueCell = true;
		}

		//We clear the zone first (in case the zone is reduced
		for(int x=maskLeftX; x<=maskRightX; x++){
			for(int y=maskTopY; y<=maskBottomY; y++){
				if((y<model.getMaskHeight())&&(x<model.getMaskWidth()))
					activeMask.setCell(x, y, !valueCell);
			}
		}
		if(moussePressedX < x0){
			maskLeftX = moussePressedX;
			maskRightX = x0;
		}else{
			maskLeftX = x0;
			maskRightX = moussePressedX;
		}
		
		if(moussePressedY < y0){
			maskTopY = moussePressedY;
			maskBottomY = y0;
		}else{
			maskTopY = y0;
			maskBottomY = moussePressedY;
		}
		
		for(int x=maskLeftX; x<=maskRightX; x++){
			for(int y=maskTopY; y<=maskBottomY; y++){
				if((y<model.getMaskHeight())&&(x<model.getMaskWidth()))
					activeMask.setCell(x, y, valueCell);	
			}
		}
		
		update(this.getGraphics());
		model.saveMaskAssociations();

		//Update the values
		maskTimeLastEvent = new Date();
		maskLastX = x0;
		maskLastY = y0;
	}
	
	
	
	
	/** 
	 * Add/remove a line into/from the mask
	 */
	protected void addLine(MouseEvent arg0) {

		Mask activeMask = (Mask)listMask.getListLabel().getSelectedValue();

		int x0 = moussePressedX;
		int y = moussePressedY;
		Boolean active = !activeMask.getCell(x0, y);

		Date newDate = new Date();
		//For the doubleclick, we get an event with a click before
		//we need to inverse the value to ignore the first click
		if((x0==maskLastX)&&(y==maskLastY)){
			if((newDate.getTime()-maskTimeLastEvent.getTime())<500){
				maskTimeLastEvent = new Date();
				active = !active;
			}
		}
		
		for(int x = 0; x< listMask.getMaskWidth(); x++)
			activeMask.setCell(x, y, active);	

		update(this.getGraphics());
		model.saveMaskAssociations();
	}

	/**
	 * Function called from when the mouse is pressed on an image
	 * @param arg0
	 */
	public void mousePressed(MouseEvent arg0) {
		moussePressedX = arg0.getX()/((int)(2*Mask.getCELL_HALF_SIZE()*zoom)); 
		moussePressedY = arg0.getY()/((int)(2*Mask.getCELL_HALF_SIZE()*zoom));
		maskLeftX = moussePressedX;
		maskTopY = moussePressedY;
		maskRightX = maskLeftX; 
		maskBottomY = maskTopY;

		Mask activeMask = (Mask)listMask.getListLabel().getSelectedValue();
		if(activeMask == null){
			addMaskAction();
			return;
		}else{
			//We check the box (to be sure the selected mask is selected for this image)
			listMask.checkSelectedMask(activeMask);
			if(!activeMask.getCell(maskLeftX,maskTopY))
				activeCell = 1;
			else
				activeCell = 0;
		}

	}
	
	/**
	 * Function called from when the mouse is released on an image
	 * @param arg0
	 */
	public void mouseReleased(MouseEvent arg0) {
		maskLeftX = -1;
		maskTopY = -1;
		maskRightX = -1;
		maskBottomY = -1;
	}
	
	/**
	 * Show information about the cell (give the list of masks which contain the cell 
	 * @param arg0
	 * @param display 
	 */
	void showInfoZone(MouseEvent arg0, MyDisplayJAI display) {
		int x = moussePressedX;
		int y = moussePressedY;
		String message;
		if(!couples.get(currentIndex).getMaskSum().getCell(x, y))
			message = "The cell x="+x+" - y="+y+" isn't in any mask.";
		else{
			ArrayList<Integer> listMaskId = couples.get(currentIndex).getMaskList();
			message = "The cell x="+x+" - y="+y+" is in ";
			String listMask = "";
			int numberMask = 0;
			for(Integer Id : listMaskId){
				if(listMaskId.contains(Id)){
					
					if(model.getRefMask(Id).getCell(x, y)){
						listMask+="     - "+model.getRefMask(Id)+"\n";
						numberMask++;
					}
				}
			}
			message +=numberMask;
			if(numberMask==1)
				message+=" mask :\n"+listMask;
			else
				message+=" masks :\n"+listMask;
		}
		JOptionPane.showMessageDialog(display, message);
	}

	protected void recompute() {
		int nbFailed=0;

		nbFailed= model.recomputeAll();
		
		this.setNbFailed(nbFailed);
		update(this.getGraphics());
	}

	/**
	 * Update the main line
	 */
	private void updateNumberTotalFail() {
		String numberTotalFail;
		// Image index/total number
		numberTotalFail = "Screenshot "+(currentIndex+1)+" ";
		
		// Result
		numberTotalFail += couples.get(currentIndex).getPass();
		
		if(null==jlnumberTotalFail)
			jlnumberTotalFail=new JLabel(numberTotalFail);
		else
			jlnumberTotalFail.setText(numberTotalFail);
		
	}
	
	/**
	 * This method update the UI
	 */
	@Override
	public void update(Graphics arg0) {
		try {
			updateNumberTotalFail();
			couples.get(currentIndex).updateDifWithMask();
			jlbCounter.setText((currentIndex+1)+" / "+model.getNbImages());
			jlbLeft.setText(couples.get(currentIndex).getImgRefId());
			BufferedImage imageL=(BufferedImage) ImageIO.read(model.getRefImage(couples.get(currentIndex).getImgRefId()).getImage());
			dispLeft.set(imageL, couples.get(currentIndex) );
			dispLeft.setActiveMask(listMask.getSelectedMask());
			dispLeft.setSize(imageL.getWidth(),imageL.getHeight());
			dispLeft.repaint();

			jlbRight.setText(couples.get(currentIndex).getImgTest().getName());
			BufferedImage imageR=(BufferedImage) ImageIO.read(couples.get(currentIndex).getImgTest());
			dispRight.set(imageR, couples.get(currentIndex));
			dispRight.setSize(imageR.getWidth(),imageR.getHeight());
			dispRight.repaint();

			String imgRefId = couples.get(currentIndex).getImgRefId();
			int numberOfMask = model.getRefImage(imgRefId).getMaskListId().size();
			String message = ""+numberOfMask;
			if(numberOfMask<=1)
				message += " mask is currently selected";
			else
				message += " masks are currently selected";
				
			jlbMask.setText(message);
			listMask.setListCheckBox(couples.get(currentIndex).getImgRef().getMaskListId());
			
			taSumary.setText(model.getNbFail()+ " FAILED\n"+
					(model.getNbImages()-model.getNbFail())+ " PASSED\n"+
					model.getNbImages()+ " TOTAL");
			
			taRefScDesc.setText(model.getRefScDescription(couples.get(currentIndex).getImgRefId()));
			taComment.setText(couples.get(currentIndex).getComment());
			if(couples.get(currentIndex).isPass()){
				//resultCompare.setIcon(new ImageIcon("res/tango/ok.png"));
				taComment.setEnabled(false);
				taComment.setBackground(Color.GRAY);
				topBox.setBackground(colorOk);
			}
			else{
				//resultCompare.setIcon(new ImageIcon("res/tango/ko.png"));
				taComment.setEnabled(true);
				taComment.setBackground(Color.WHITE);
				topBox.setBackground(colorKo);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public ImageFileMask getCurrentImgRef() {
		return model.getRefImage(couples.get(currentIndex).getImgRefId());
	}


	public Model getModel() {
		return model;
	}


	public void update(Observable arg0, Object arg1) {
		update(this.getGraphics());

	}


	/**
	 * The entry point for the application, which opens a file with an image that
	 * will be used as reference and starts the application.
	 */
	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure("log4j.xml");

		JFileChooser fc = new JFileChooser(basePath);
		fc.setFileFilter(new DirectoryFileFilter());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int res = fc.showDialog(null,"Open as reference directory");
		// We have an image!
		if (res == JFileChooser.APPROVE_OPTION) {
			File fileRef = fc.getSelectedFile();
			res =fc.showDialog(null,"Open as test directory");
			if (res == JFileChooser.APPROVE_OPTION) {
				File fileTest=fc.getSelectedFile();

				Model model =new Model(fileRef.getPath(), fileTest.getPath());
				new ComparatorFrame(model);
			}
		}
		// Oops!
		else {
			JOptionPane.showMessageDialog(null,
					"You must select one directory to be the reference.", "Aborting...",
					JOptionPane.WARNING_MESSAGE);
		}
	}
}

