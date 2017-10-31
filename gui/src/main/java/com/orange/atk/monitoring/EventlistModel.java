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
 * Store in the list all information corresponding to the marker
 */
public class EventlistModel extends DefaultListModel {

	private static final long serialVersionUID = -624127605307334812L;
	private transient List<String> events;
	private transient List<Double> marker;

	public EventlistModel() {
		super();
		events = new ArrayList<String>();
		marker = new ArrayList<Double>();
	}

	public Double getPosition(int index) {
		return marker.get(index);
	}

	public String getName(int index) {
		return events.get(index);
	}

	public Object getElementAt(int index) {
		return events.get(index);
	}

	public int getSize() {
		return events.size();
	}

	public Object remove(int index) {
		marker.remove(index);
		Object name = events.remove(index);
		fireIntervalRemoved(name, Math.max(events.size() - 2, 0), events.size());
		return name;
	}

	public void addEvent(String name, double position) {
		events.add(name);
		marker.add(position);
		fireIntervalAdded(name, Math.max(events.size() - 2, 0), events.size());
	}
}