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
 * File Name   : PhoneConfigurationWizard.java
 *
 * Created     : 08/01/2010
 * Author(s)   : France Telecom
 */

package com.orange.atk.monitoring;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * the model of the table.
 */
public class ProcessTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private ArrayList<String[]> data;
	private final String[] titres = new String[]{"Process", "Resource", "Color"};

	public ProcessTableModel() {
		// super();
		data = new ArrayList<String[]>();
		// addRow();
	}

	public int getColumnCount() {
		return titres.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int arg0, int arg1) {
		return data.get(arg0)[arg1];
	}

	@Override
	public String getColumnName(int col) {
		return titres[col];
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		data.get(arg1)[arg2] = (String) arg0;
		fireTableCellUpdated(arg1, arg2);
	}

	public void addRow() {
		data.add(new String[]{"process", "resource", "color"});
		fireTableRowsInserted(data.size() - 1, data.size());
	}

	public void removeRow(int ref) {
		data.remove(ref);
		fireTableRowsDeleted(ref - 1, ref);
	}

	public void add(String name, String type, String color) {
		data.add(new String[]{name, type, color});
		fireTableRowsInserted(data.size() - 1, data.size());

	}
}
