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
 * File Name   : ColorCellRenderer.java
 *
 * Created     : 29/03/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.deviceDetectionUI;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.lowagie.text.Font;

public class ColorCellRenderer extends DefaultTableCellRenderer {

	   public Component getTableCellRendererComponent(JTable table, Object value, 
	      boolean isSelected, boolean hasFocus, 
	      int row, int col)  
	   {
	      // get the DefaultCellRenderer to give you the basic component
	      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	      // apply your rules
    	  c.setFont(c.getFont().deriveFont(Font.BOLD));
    	  c.setForeground(Color.green);
	      String label = value.toString();
	      if (label.equals(DeviceDetectionFrame.AVAILABLE_STATUS_LABEL)) c.setForeground(new Color(29, 158, 245));
	      else if (label.equals(DeviceDetectionFrame.BUSY_STATUS_LABEL)) c.setForeground(Color.orange);
	      else if (label.equals(DeviceDetectionFrame.DISCONNECTED_STATUS_LABEL)) c.setForeground(Color.red);
	      else if (label.equals(DeviceDetectionFrame.FAILED_STATUS_LABEL)) {
	    	  c.setForeground(Color.red);
	      }
	      return c;
	   }



}
