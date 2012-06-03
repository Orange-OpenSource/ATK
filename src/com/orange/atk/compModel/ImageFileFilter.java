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
 * File Name   : ImageFileFilter.java
 *
 * Created     : 04/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.compModel;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * This class implements a generic file name filter that allows the listing/selection
 * of JPEG, GIF, PNG, JPG files.
 * @author bvqj2105
 *
 */
public class ImageFileFilter extends FileFilter implements java.io.FileFilter
{
	public boolean accept(File f)
	{
		if (f.getName().toLowerCase().endsWith(".gif")) return true;
		if (f.getName().toLowerCase().endsWith(".png")) return true;
		if (f.getName().toLowerCase().endsWith(".jpeg")) return true;
		if (f.getName().toLowerCase().endsWith(".jpg")) return true;
		return false;
	}
	public String getDescription()
	{
		return "Image files";
	}

}
