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
 * File Name   : ListMask.java
 *
 * Created     : 10/05/2010
 * Author(s)   : Gurvan LE QUELLENEC
 */
package com.orange.atk.compUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.orange.atk.compModel.ImageFileMask;
import com.orange.atk.compModel.Mask;


public class ListMask {
	ComparatorFrame comparatorFrame;	

	/**
	 * This listbox that holds the checkboxes
	 */
	private JList listCheckBox;
	private DefaultListModel listCheckBoxModel;  

	/**
	 * This listbox that holds the actual list of masks
	 */
	private JList listMask;
	private DefaultListModel listMaskModel;

	
	/*
	 * Border around each mask
	 */
	private static final Color colorBorder = Color.RED;
	private static final int sizeBorder = 2;
	
	/*
	 * the cells change colors when the mask is selected
	 */
	private static final Color colorCell = Color.BLACK;
	private static final Color colorCellSelected = Color.YELLOW;
	private static final int sizeCell = 3;
	private static final int sizeCellFill = 2;
	
	/*
	 * Interval around the mask
	 */
	private static final int sizeInterval = 5;
	private static final Color colorBackgroundSelected = new Color(250,250,125);
	

	private int imageHeight = 130;
	private int imageWidth = 100;
	
	/*
	 * Size of the mask 
	 */
	private int maskHeight = 0;
	private int maskWidth = 0;
	
	public ListMask(Mask[] listMasks, ComparatorFrame frame,int width, int height){
		maskWidth = width;
		maskHeight = height;
		imageHeight = maskHeight*sizeCell + (sizeInterval+sizeBorder)*2;
		imageWidth = maskWidth*sizeCell + (sizeInterval+sizeBorder)*2;
				
		comparatorFrame = frame;
		listCheckBoxModel = new DefaultListModel();
		listCheckBox = new JList(listCheckBoxModel);
		listMaskModel = new DefaultListModel();
		listMask = new JList(listMaskModel);
		this.add(listMasks);
		listMask.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listMask.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				comparatorFrame.update(comparatorFrame.getGraphics());
			}
		});

		listCheckBox.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int selectedIndex = listCheckBox.locationToIndex(me.getPoint());
				if (selectedIndex < 0)
					return;
				CheckBoxItem item = (CheckBoxItem)listCheckBoxModel.getElementAt(selectedIndex);
				item.setChecked(!item.isChecked());
				listMask.setSelectedIndex(selectedIndex);
				listCheckBox.repaint();
				updateCurrentImage(((Mask)listMaskModel.get(selectedIndex)).getId(),item.isChecked());
			}
		});

		// Align both the checkbox height and width
		listMask.setFixedCellHeight(imageHeight);
		listCheckBox.setFixedCellHeight(listMask.getFixedCellHeight());
		listCheckBox.setFixedCellWidth(20);
		
		listMask.setCellRenderer(new ListMaskRenderer(listCheckBoxModel));
		listCheckBox.setCellRenderer(new CheckBoxRenderer());
		listCheckBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}

	public int getMaskHeight() {
		return maskHeight;
	}

	public int getMaskWidth() {
		return maskWidth;
	}

	public void add(Mask mask) {
		listCheckBoxModel.addElement(new CheckBoxItem());
		listMaskModel.addElement(mask);
	}

	private void add(Mask[] listMask) {
		for(Mask mask : listMask){
			listCheckBoxModel.addElement(new CheckBoxItem());
			listMaskModel.addElement(mask);
		}
	}
	
	
	public void removeMask(Mask mask) {
		int index = listMaskModel.indexOf(mask);
		listCheckBoxModel.remove(index);
		listMaskModel.remove(index);
		
	}
	
	public JList getListCheckBox() {
		return listCheckBox;
	}

	public JList getListLabel() {
		return listMask;
	}
	/**
	 * Get at list of Mask Id and update the list of mask by
	 * checking or unchecking the selected masks.
	 * @param listMaskId
	 */
	public void setListCheckBox(ArrayList<Integer> listMaskId){
		for(int i=0; i<listMaskModel.getSize();i++){
			if(listMaskId.contains(((Mask)listMaskModel.get(i)).getId()))
				((CheckBoxItem)listCheckBoxModel.getElementAt(i)).setChecked(true);				
			else
				((CheckBoxItem)listCheckBoxModel.getElementAt(i)).setChecked(false);
		}
		listCheckBox.repaint();
	}
	/**
	 * Update the list of masks for the current Image of reference (in the main Frame) 
	 * from the list of masks selected.
	 */
	private void updateCurrentImage(Integer Id, Boolean check) {
		ImageFileMask imageMask = comparatorFrame.getCurrentImgRef();
		if(check)
			imageMask.addMask(Id);
		else
			imageMask.removeMask(Id);
		comparatorFrame.update(comparatorFrame.getGraphics());
		comparatorFrame.getModel().saveMaskAssociations();
	}

	public void setSelectedMask(Mask mask){
		listMask.setSelectedIndex(listMaskModel.indexOf(mask));
	}
	public void checkSelectedMask(Mask mask){

		int index = listMaskModel.indexOf(mask);
		if (index < 0)
			return;
		CheckBoxItem item = (CheckBoxItem)listCheckBoxModel.getElementAt(index);
		item.setChecked(true);
		listCheckBox.repaint();
		updateCurrentImage(((Mask)listMaskModel.get(index)).getId(),item.isChecked());
	}

	public Mask getSelectedMask(){
		if(listMask.getSelectedIndex()<0)
			return null;
		return (Mask) listMaskModel.elementAt(listMask.getSelectedIndex());
	}

	/**
	 * Inner class to hold data for JList with checkboxes
	 * @author Gurvan LE QUELLENEC  
	 */
	private static class CheckBoxItem {
		private boolean isChecked;

		public CheckBoxItem() {
			isChecked = false;
		}
		public boolean isChecked() {
			return isChecked;
		}
		public void setChecked(boolean value) {
			isChecked = value;
		}
	}
	/**
	 * Repaint the list of mask 
	 */
	void repaint(){
		listCheckBox.repaint();
	}
	/**
	 * Inner class that renders JCheckBox to JList
	 * @author Gurvan LE QUELLENEC 
	 */
	@SuppressWarnings("serial")
	private static class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {

		public CheckBoxRenderer() {
			setBackground(UIManager.getColor("List.textBackground"));
			setForeground(UIManager.getColor("List.textForeground"));
		}

		public Component getListCellRendererComponent(JList listBox, Object obj, int currentindex, 
				boolean isChecked, boolean hasFocus) {
			setSelected(((CheckBoxItem)obj).isChecked());
			return this;
		}
	}
	/** 
	 * Inner class that renders the list of the masks 
	 * @author Gurvan LE QUELLENEC 
	 *
	 */
	private class ListMaskRenderer implements ListCellRenderer {
		private DefaultListModel listCheckBoxM;  


		public ListMaskRenderer(DefaultListModel listCheckBox) {
			listCheckBoxM = listCheckBox;
		}

		public Component getListCellRendererComponent(JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean hasFocus) {
			JLabel label = new JLabel(value.toString());
			BufferedImage image = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			
			Boolean isChecked = (Boolean)((CheckBoxItem)listCheckBoxM.get(index)).isChecked;
			
			//set default value
			g2d.setColor(Color.lightGray);
			g2d.fillRect((int) sizeInterval,
					(int) sizeInterval,
					(int) maskWidth*sizeCell+2*sizeBorder,
					(int) maskHeight*sizeCell+2*sizeBorder);
			
			//We set the border in RED 
			g2d.setColor(colorBorder);
			g2d.fillRect((int) sizeInterval,
					(int) sizeInterval,
					(int) maskWidth*sizeCell+2*sizeBorder,
					(int) sizeBorder);
			g2d.fillRect((int) sizeInterval,
					(int) sizeInterval,
					(int) sizeBorder,
					(int) maskHeight*sizeCell+2*sizeBorder);
			g2d.fillRect((int) sizeInterval,
					(int) sizeInterval+maskHeight*sizeCell+sizeBorder,
					(int) maskWidth*sizeCell+2*sizeBorder,
					(int) sizeBorder);
			g2d.fillRect((int) sizeInterval+maskWidth*sizeCell+sizeBorder,
					(int) sizeInterval,
					(int) sizeBorder,
					(int) maskHeight*sizeCell+2*sizeBorder);

			//We create the image with the mask
			if(isSelected && isChecked)
				g2d.setColor(colorCellSelected);
			else
				g2d.setColor(colorCell);
			for (int x=0; x<maskWidth; x++){
				for (int y=0; y<maskHeight; y++){
					if (((Mask)value).getCell(x,y)){
						g2d.fillRect(
								(int) ((x)*sizeCell+sizeBorder+sizeInterval),
								(int) ((y)*sizeCell+sizeBorder+sizeInterval) ,
								sizeCellFill,
								sizeCellFill);
					}
				}
			}
			if(isSelected){
				label.setFont(new Font(null,Font.BOLD,22));
				label.setOpaque(true);
				label.setBackground(colorBackgroundSelected );
			}else
				label.setFont(new Font(null,Font.PLAIN,18));
			label.setIcon(new ImageIcon(image));
			return(label);
		}
	}

}
