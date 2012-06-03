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
 * File Name   : Mask.java
 *
 * Created     : 04/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compModel;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Masks are zones were differences between two images don't have to be notified.
 * 
 * @author bvqj2105
 *
 */

public class Mask implements Cloneable {

	private String label;
	private int id;
	private boolean [][] cells;
	private final static int CELL_HALF_SIZE = 7; // Cell width and height = 2*CELL_HALF_SIZE
	private int width;
	private int height;
	
	/**
	 * constructor.
	 * Initialize the Mask to a void one
	 * @param label
	 * @param id
	 * @param width
	 * @param height
	 */
	public Mask(String label, int id, int maskwidth, int maskheight) {
		super();
		this.label = label;
		this.id = id;
		this.width = maskwidth;
		this.height = maskheight;
		cells =new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.cells[i][j]=false;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public String toString(){
		return label;
	}

	/**
	 * @param x x-location of the cell
	 * @param y y-location of the cell
	 * @return true if the (x, y) cell is masked, else return false
	 */
	public boolean getCell(int x, int y){
		return cells[x][y];
	}
	/**
	 * @param x x-location of the cell
	 * @param y y-location of the cell
	 * @param b true if the cell is masked, else false
	 */
	public void setCell(int x, int y, boolean b) {
		cells[x][y] =b;
	}
	/**
	 * @return array of cell values
	 */
	public boolean[][] getCells() {
		return cells;
	}
	/**
	 * @param cells - array of cells values
	 */
	public void setCells(boolean[][] cells) {
		this.cells = cells;
	}
	/**
	 * @return label - name of the mask
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label - name of the mask 
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the mask ID
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
	public static int getCELL_HALF_SIZE() {
		return CELL_HALF_SIZE;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	protected Element toXML(Document doc){
		Logger.getLogger(this.getClass() ).debug("Mask.toXML");
		Element mask =doc.createElement("Mask");
		
		mask.setAttribute("label",     getLabel());
		mask.setAttribute("id",     ""+getId());

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (getCell(x, y)){
					Element cell=doc.createElement("cell");
					cell.setAttribute("x",""+x);
					cell.setAttribute("y",""+y);
					mask.appendChild(cell);
				}
			}
		}
		return mask;
	}

	/**
	 * Add a mask to an existing mask cell by cell.</br>
	 * Set a cell to True if any of the mask has True in the cell.
	 * @param newMask
	 */
	public void add(Mask newMask) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(newMask.getCell(i, j))
					this.cells[i][j]= true;
			}
		}
	}

}
