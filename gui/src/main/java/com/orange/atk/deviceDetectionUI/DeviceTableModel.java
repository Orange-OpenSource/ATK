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
 * File Name   : DeviceTableModel.java
 *
 * Created     : 18/03/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.deviceDetectionUI;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DeviceTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	public DeviceTableModel(Vector columnNames, int rowCount) {
		super(columnNames, rowCount);
	}
	public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

	public boolean isCellEditable(int row, int col)
    { return false; }

	
}
