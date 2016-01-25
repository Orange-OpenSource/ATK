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
 * File Name   : IconResizer.java
 *
 * Created     : 08/06/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.coregui.utils;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class IconResizer {

	/**
	 * Build a new <code>ImageIcon</code> with widthxheigth dimensions
	 * @param icon source <code>ImageIcon</code>
	 * @param width wanted width
	 * @param heigth wanted heigth
	 * @return a widthxheigth <code>ImageIcon</code>
	 */
	public static ImageIcon resize(ImageIcon icon, int width, int heigth) {
		Image img = icon.getImage();
		Image img2 = img.getScaledInstance(width, heigth, Image.SCALE_AREA_AVERAGING);
		return new ImageIcon(img2);
	}

	/**
	 * Build a new <code>ImageIcon</code> with 16x16 dimensions
	 * @param icon source <code>ImageIcon</code>
	 * @return a 16x16 <code>ImageIcon</code>
	 */
	public static ImageIcon resize16x16(ImageIcon icon) {
		return resize(icon, 16,16);
	}

	/**
	 * Build a new <code>ImageIcon</code> witch dimensions are a percentage of the source's ones
	 * @param icon source <code>ImageIcon</code>
	 * @param percentage a float. 0.5 for half dimensions, 2 for double dimensions
	 * @return an <code>ImageIcon</code>
	 */
	public static ImageIcon resize(ImageIcon icon, double percentage) {
		int width = (int) (icon.getIconWidth()*percentage);
		int heigth = (int) (icon.getIconHeight()*percentage);
		return resize(icon, width,heigth);
	}

}
