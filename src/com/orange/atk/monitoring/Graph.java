/*
 * Software Name : ATK
 *
 * Copyright (C) 2013 France Télécom
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
 *
 * Created     : 12/06/2013
 * Author(s)   : Pierre Crepieux
 */

package com.orange.atk.monitoring;

public class Graph {
	private String name;
	private String color;
	private String xcomment;
	private String ycomment;
	private String unit;
	private String scale;
	private Boolean sampled;
	private String type = "avg";

	public Graph(String graphname, String color, String commentX, String commentY, String unit,
			String scale, Boolean sampled, String type) {
		super();
		this.name = graphname;
		this.color = color;
		this.xcomment = commentX;
		this.ycomment = commentY;
		this.unit = unit;
		this.scale = scale;
		this.sampled = sampled;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String graphname) {
		this.name = graphname;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getXcomment() {
		return xcomment;
	}
	public void setXcomment(String commentX) {
		this.xcomment = commentX;
	}
	public String getYcomment() {
		return ycomment;
	}
	public void setYcomment(String commentY) {
		this.ycomment = commentY;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public Boolean getSampled() {
		return sampled;
	}
	public void setSampled(Boolean sampled) {
		this.sampled = sampled;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
