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
 * File Name   : CheckListTableModel.java
 *
 * Created     : 25/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Class which represents a table model.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class CheckListTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private Object[] longValues;

	public Vector getColumnIdentifiers() {
        return columnIdentifiers;
    }

	// overwritten because it throw ArrayIndexOutOfBoundsException
	// that is not handled by swing ...
	public Object getValueAt(int row, int column) {
		try {
			return super.getValueAt(row, column);
		} catch (ArrayIndexOutOfBoundsException ae) {
			// mask it
			return null;
		}
	}

	public void initColumnSizes(CheckListTableModel model, JTable table, int nbColumn) {
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.getLongValues();
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        for (int i = 0; i < nbColumn; i++) {
            column = table.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, longValues[i],
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
	}

	public Object[] getLongValues() {
		return longValues;
	}

	public void setLongValues(Object[] longValues) {
		this.longValues = longValues;
	}

}
