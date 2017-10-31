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
 * File Name   : MyDisplayJAI.java
 *
 * Created     : 04/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compUI;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;

import com.orange.atk.compModel.Boolean2D;
import com.orange.atk.compModel.ComparaisonCouple;
import com.orange.atk.compModel.Mask;
import com.orange.atk.compModel.Model;
import com.sun.media.jai.widget.DisplayJAI;

public class MyDisplayJAI extends DisplayJAI implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MyDisplayJAI me;
	private BufferedImage bi;
	private boolean isTest =true;
	public static final Color colorSimilarPart = new Color(100,200,50);
	public static final Color colorMaskSelected = Color.YELLOW;
	public static final Color colorSumMask= Color.BLUE;
	int w;
	int h;

	private Mask mask;
	private Mask activeMask;
	ArrayList<Integer> listMask;
	private Boolean2D diff;
	private double zoom = 1.0;
	private BufferedImage img;
	private ComparatorFrame comparatorFrame;
	Model model;

	/**
	 * constructor
	 * @param model
	 * @param index
	 * @param isTest - if the display will paint mask and differences
	 */
	public MyDisplayJAI(ComparaisonCouple couple, Model model, boolean isTest) {
		super();
		model.addObserver(this);
		mask=couple.getMaskSum();
		this.isTest=isTest;
		me = this;
		this.model = model;
		try {
			if (isTest){
				img = ImageIO.read(couple.getImgTest());
			}else{
				img = ImageIO.read(model.getRefImage(couple.getImgRefId()).getImage());	
			}
			w = img.getWidth();
			h = img.getHeight();
			bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.getGraphics();

			Dimension dim = new Dimension(w,h);
			setPreferredSize(dim);
			setMinimumSize(dim);
			g.drawImage(img, 0, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		diff = couple.getDifWithMask();
	}

	public void setZoom(double zoom) {
		if (zoom != this.zoom) {
			this.zoom = zoom;
			w = (int) (img.getWidth() * zoom);
			h = (int) (img.getHeight() * zoom);
			bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.getGraphics();

			Dimension dim = new Dimension(w,h);
			setPreferredSize(dim);
			setMinimumSize(dim);
			if (zoom != 1.0) g.drawImage(img.getScaledInstance(w, h, Image.SCALE_DEFAULT), 0, 0, null);
			else g.drawImage(img, 0, 0, null);
		}
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	public void set(BufferedImage img, ComparaisonCouple cp) {
		//Logger.getLogger(this.getClass() ).debug("MyDisplay.set");
		w = (int) (img.getWidth() * zoom);
		h = (int) (img.getHeight() * zoom);
		diff = cp.getDifWithMask();
		mask = cp.getMaskSum();
		listMask = cp.getImgRef().getMaskListId();
		bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();

		setSize(w, h);
		setMinimumSize(new Dimension(w,h) );
		if (zoom != 1.0) g.drawImage(img.getScaledInstance(w, h, Image.SCALE_DEFAULT), 0, 0, null);
		else g.drawImage(img, 0, 0, null);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		/**
		 * Set to the active or to null if the active mask is not part of the active imageRef
		 */
		Mask activeMaskChecked = activeMask;
		g.clearRect(0, 0, w, h);

		g2d.drawImage(bi, null,0,0);
		g2d.setStroke(new BasicStroke(1.5f));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));

		if(activeMask!=null){
			if(!listMask.contains(activeMask.getId()))
				activeMaskChecked = null;
		}
		if(model!=null){
			for (int x=0; x<model.getMaskWidth(); x++){
				for (int y=0; y<model.getMaskHeight(); y++){

					if(isTest && !diff.get(x, y)){
						g2d.setColor(colorSimilarPart);
						g2d.fillRect(
								(int)((2*x)*Mask.getCELL_HALF_SIZE()*zoom),
								(int)((2*y)*Mask.getCELL_HALF_SIZE()*zoom),
								(int)(2*Mask.getCELL_HALF_SIZE()*zoom),
								(int)(2*Mask.getCELL_HALF_SIZE()*zoom));
					}
					else if (mask.getCell(x, y)){
						if(activeMaskChecked!= null){
							if (activeMaskChecked.getCell(x, y))
								g2d.setColor(colorMaskSelected);
							else
								g2d.setColor(colorSumMask);

						}else
							g2d.setColor(colorSumMask);

						g2d.fillRect(
								(int)((2*x)*Mask.getCELL_HALF_SIZE()*zoom),
								(int)((2*y)*Mask.getCELL_HALF_SIZE()*zoom),
								(int)(2*Mask.getCELL_HALF_SIZE()*zoom),
								(int)(2*Mask.getCELL_HALF_SIZE()*zoom));
					}
				}
			}
		}
	}


	public void update(Observable arg0, java.lang.Object arg1) {
		repaint();
	}

	public void setActiveMask(Mask selectedMask) {
		activeMask = selectedMask;
	}

	public void addListener(ComparatorFrame comparatorFrameArg){
		comparatorFrame = comparatorFrameArg;

		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton()==MouseEvent.BUTTON1){
					if(arg0.getClickCount() == 2)
						comparatorFrame.addLine(arg0);
					else
						comparatorFrame.addZone(arg0);
				}
				else if(arg0.getButton()==MouseEvent.BUTTON3){
					comparatorFrame.showInfoZone(arg0,me);
				}
			}
			public void mousePressed(MouseEvent arg0) {
				comparatorFrame.refScDescChanged();
				comparatorFrame.mousePressed(arg0);
			}
			public void mouseReleased(MouseEvent arg0) {
				comparatorFrame.mouseReleased(arg0);
			}
		});	

		this.addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseDragged(MouseEvent e) {
				comparatorFrame.addZoneDragged(e);				
			}
		});

	}

}
