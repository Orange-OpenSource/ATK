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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Store in the list all information corresponding to the graphs
 */
public class GraphlistModel extends DefaultListModel {

	private static final long serialVersionUID = -1912190623557488673L;

	private List<String> graphname;
	private List<String> color;
	private List<String> xcomment;
	private List<String> ycomment;
	private List<String> unit;
	private List<String> scale;
	private List<Boolean> sampled;
	private List<String> type;

	public GraphlistModel() {
		super();
		graphname = new ArrayList<String>();
		color = new ArrayList<String>();
		xcomment = new ArrayList<String>();
		ycomment = new ArrayList<String>();
		unit = new ArrayList<String>();
		scale = new ArrayList<String>();
		sampled = new ArrayList<Boolean>();
		type = new ArrayList<String>();
	}

	public String getcommentY(int index) {
		return ycomment.get(index);
	}

	public String getunit(int index) {
		return unit.get(index);
	}

	public String getscale(int index) {
		return scale.get(index);
	}

	public String gettype(int index) {
		return type.get(index);
	}

	public String getsampled(int index) {
		return sampled.get(index).toString();
	}

	public String getcommentX(int index) {
		return xcomment.get(index);
	}

	public String getColor(int index) {
		return color.get(index);
	}

	public Object getElementAt(int index) {
		return graphname.get(index);
	}

	public int getSize() {
		return graphname.size();
	}

	// for save use
	public String getName(int index) {
		return graphname.get(index);
	}
	public Object remove(int index) {
		color.remove(index);
		xcomment.remove(index);
		ycomment.remove(index);
		unit.remove(index);
		scale.remove(index);
		sampled.remove(index);
		type.remove(index);
		Object name = graphname.remove(index);
		fireIntervalRemoved(name, Math.max(graphname.size() - 2, 0), graphname.size());
		return name;
	}

	public void addgraph(String name, String color, String commentX, String commentY, String unit,
			String scale, Boolean sampled, String type) {
		this.graphname.add(name);
		this.color.add(color);
		this.xcomment.add(commentX);
		this.ycomment.add(commentY);
		this.unit.add(unit);
		this.scale.add(scale);
		this.sampled.add(sampled);
		this.type.add(type);

		fireIntervalAdded(name, Math.max(graphname.size() - 2, 0), graphname.size());
	}

	public void changecolor(String color, Integer index) {
		this.color.set(index, color);

	}

	public void savegraph(String name, String color, String commentX, String commentY, String Unit,
			String scale, Boolean sampled, String type, Integer index) {
		this.graphname.set(index, name);
		this.color.set(index, color);
		this.xcomment.set(index, commentX);
		this.ycomment.set(index, commentY);
		this.unit.set(index, Unit);
		this.scale.add(scale);
		this.sampled.set(index, sampled);
		this.type.add(type);
	}
}