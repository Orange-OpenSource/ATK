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
 * File Name   : ComparaisonCouple.java
 *
 * Created     : 18/12/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compModel;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Moreau Fabien 
 *
 */
public class ComparaisonCouple {
	/**
	 * Absolute difference between the 2 images
	 */
	private Boolean2D dif;
	/**
	 * Difference between the 2 images with the mask
	 * (where the mask is true, the difference is null)
	 */
	private Boolean2D difWithMask;
	
	private String imgRefId;
	private File imgTest;

	private String comment;
	private String Pass;
	
	private int maskWidth;
	private int maskHeight;
	private Model model;

	public ComparaisonCouple(String ref, File test, Boolean2D diff, String pass, Model model) {
		imgRefId = ref;
		imgTest = test;
		dif = diff;
		Pass = pass;
		comment= "";
		this.model = model;
		maskWidth = model.getMaskWidth();
		maskHeight = model.getMaskHeight();
		difWithMask = new Boolean2D(maskWidth,maskHeight);
		updateDifWithMask();
	}
	
	public void updateDifWithMask() {
		Mask maskSum = getMaskSum();
		for(int i=0;i<maskWidth;i++)
			for(int j=0;j<maskHeight; j++){
				if(maskSum.getCell(i, j))
					difWithMask.set(i, j, false);
				else
					difWithMask.set(i, j, dif.get(i, j));
			}
	}
	public void updateCellDifWithMask(int x,int y,Boolean valueMask){
		if(valueMask){
			//if the cell of the mask is set, we set the cell of difWithMask to false
			difWithMask.set(x, y, valueMask);
		}else{
			//if the cell of the mask is unset, we set the cell to the value of dif
			difWithMask.set(x, y, dif.get(x, y));
		}
			
	}
	
	public Boolean2D getDif() {
		return dif;
	}
	public Boolean2D getDifWithMask() {
		return difWithMask;
	}
	
	public ImageFileMask getImgRef() {
		return model.getRefImage(imgRefId);
	}
	
	public String getImgRefId() {
		return imgRefId;
	}
	
	public File getImgTest() {
		return imgTest;
	}
	
	public void setImgTest(File imgTest) {
		this.imgTest = imgTest;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getPass() {
		return Pass;
	}
	
	public void setPass(String pass) {
		Pass = pass;
	}

	public boolean isPass() {
		return !Pass.equals(Model.FAIL);
	}

	public Mask getMaskSum() {
		return model.getRefImage(imgRefId).getMaskSum();
	}

	public ArrayList<Integer> getMaskList() {
		return model.getRefImage(imgRefId).getMaskListId();
	}


}
