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
 * File Name   : MyJTextArea.java
 *
 * Created     : 24/06/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.compUI;

import java.awt.Rectangle;

import javax.swing.JTextArea;
import javax.swing.JViewport;

public class MyJTextArea extends JTextArea {

	public MyJTextArea() {
		super();
	}
	
	public MyJTextArea(int row, int col) {
		super(row, col);
	}
	
	public void scrollRectToVisible(Rectangle aRect) {
	    if(hasFocus() || getParent() instanceof JViewport)
		super.scrollRectToVisible(aRect);
        }

}
