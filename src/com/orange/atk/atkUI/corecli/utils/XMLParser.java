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
 * File Name   : XMLParser.java
 *
 * Created     : 19/02/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.orange.atk.atkUI.corecli.Alert;

/**
 * Tools to parse xml files.
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class XMLParser {

	private Document doc;
	private Element root;
	private File file;

	public XMLParser(File f, String dtdURL, String dtdDirectory) {
		this.file = f;
		SAXReader reader = new SAXReader();
		if (dtdURL != null && dtdDirectory != null) {
			reader.setEntityResolver(new MatosResolver(dtdURL,dtdDirectory));
		}
		try {
			doc = reader.read(f);
			root = doc.getRootElement();
		} catch (DocumentException e) {
			Alert.raise(e, "Error with file '"+f.getAbsolutePath()+"': \n" 
					+ e.getMessage() );
		}
	}

	/**
	 * get a given category of entries from the file (for anasoot mainly)
	 * @param kind the kind of elements analyzed
	 * @return an array of XML elements representing the rules
	 */
	public Element [] getKind(String kind) {
		return getElements(root,kind);
	}

	/**
	 * Extracts the set of XML elements having a given name in a given XML
	 * element.
	 * @param e the element to explore
	 * @param name the name of the elements searched
	 * @return an array of elements
	 */
	public Element [] getElements(Element e, String name) {
		List<?> list = e.elements(name);
		int l = list.size();
		Element r [] = new Element [l];
		for (int i=0; i<l; i++) {
			r[i] = (Element) list.get(i);
		}
		return r;
	}

	/** 
	 * Extracts a given XML element having a given name son of a given
	 * XML element. There should be only one such element:
	 * @param e the element to explore
	 * @param name the name of the elements searched
	 * @return an array of elements
	 */
	public Element getElement(Element e, String name) {
		List<?> list = e.elements(name);
		if (list.size()==1) return (Element) list.get(0);
		else return null;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public Element getRoot() {
		return root;
	}

	public void setRoot(Element root) {
		this.root = root;
	}

	public File getFile() {
		return file;
	}

}
