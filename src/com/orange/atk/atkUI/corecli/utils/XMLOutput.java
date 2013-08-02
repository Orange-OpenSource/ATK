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
 * File Name   : XMLOutput.java
 *
 * Created     : 20/02/2007
 * Author(s)   : Pierre CREGUT
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.File;
import java.io.FileWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.orange.atk.atkUI.corecli.Alert;

/**
 * 
 * @author Pierre CREGUT
 * @since JDK5.0
 */
public class XMLOutput {

	private Element root;
	private Document doc;
	private File file;

	public XMLOutput(String dir, String rootElt) {
		try {
			File file = new File(dir, rootElt + ".xml");
			this.file = file;
			doc = DocumentHelper.createDocument();
			root = doc.addElement(rootElt);
		} catch (Exception e) {
			Alert.raise(e, "Problem while preparing " + file);
		}
	}

	public Element root() {
		return root;
	}

	public Document document() {
		return doc;
	}

	public void generate() {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			Alert.raise(e, "While generating " + file);
		}
	}

	public File getFile() {
		return file;
	}

}
