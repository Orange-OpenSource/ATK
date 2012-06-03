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
 * File Name   : Position.java
 *
 * Created     : 11/12/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.util;

/**
 * Simple class which represent a coordinate.
 * Use to transmit Touch screen event from model to controller.
 * @author Moreau Fabien 
 *
 */
public class Position {

	private int x;
	private int y;
	private long  time;
	
	
	
	
	public long getTime() {
		return time;
	}


	public void setTime(long time) {
		this.time = time;
	}


	public Position(int x,  int y,long time) {
		this.x = x;
		this.y = y;
		this.time=time;
	}

	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "{"+getX()+" "+getY()+" "+getTime()+"}";
	}
}
