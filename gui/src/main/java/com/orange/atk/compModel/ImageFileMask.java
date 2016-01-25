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
 * File Name   : ImageFileMask.java
 *
 * Created     : 10/05/2010
 * Author(s)   : Gurvan LE QUELLENEC
 */
package com.orange.atk.compModel;

import java.io.File;
import java.util.ArrayList;


/**
 * @author Gurvan Le Quellenec
 * Combination of a file and a list of masks
 *
 */
public class ImageFileMask {
	private File image;
	private Model model;
	private ArrayList<Integer> maskListId;
	
	public ImageFileMask(File image, Model model) {
		super();
		this.image = image;
		this.model = model;
		this.maskListId = new ArrayList<Integer>();
	}

	public File getImage() {
		return image;
	}

	public String getId(){
		return image.getName();
	}

	public ArrayList<Integer> getMaskListId() {
		return maskListId;
	}

	public void addMask(Integer Id){
		if(!maskListId.contains(Id))
			maskListId.add(Id);
	}

	public void removeMask(Integer Id){
		maskListId.remove(Id);
	}

	public Mask getMaskSum() {
		//TODO : create the mask sum!!!
		Mask maskSum;
		int width = model.getMaskWidth();
		int height = model.getMaskHeight();
		if (maskListId.size()==0){
			maskSum = new Mask("Mask empty", 0, width, height);
		}else{
			maskSum = new Mask("Label", 0, width, height);
			for(Integer Id : maskListId){
				maskSum.add(model.getRefMask(Id));
			}
		}
		return maskSum;
	}
	
}
