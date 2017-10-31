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
 * File Name   : Boolean2D.java
 *
 * Created     : 03/08/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compModel;

public class Boolean2D {
	boolean[][] array;
	public Boolean2D(int width, int height) {
		array = new boolean[width][height];
		
	}

	public boolean[][] getArray() {
		return array;
	}	
	
	public boolean get(int x, int y){
		return array[x][y];
	}
	
	public void set(int x, int y, boolean b){
		array[x][y]=b;
	}
	
	public int getWidth(){
		return array.length;
	}
	public int getHeight(){
		return array[0].length;
	}
}
