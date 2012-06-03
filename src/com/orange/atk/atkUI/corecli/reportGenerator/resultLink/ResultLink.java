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
 * File Name   : ResultLink.java
 *
 * Created     : 24/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.resultLink;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;
import org.exolab.castor.xml.Unmarshaller;

import com.orange.atk.atkUI.corecli.reportGenerator.bind.Resultvalue;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class ResultLink {

	/**
	 * Results' document.
	 */
	protected Document doc;

	/**
	 * Root Element of javaresults.xml file
	 */
	protected Element root;

	protected Unmarshaller unmarshaller;

	/**
	 * Setter for the <code>unmarshaller</code> attribute
	 * @param unmar
	 */
	public void setUnmarshaller(Unmarshaller unmar) {
		this.unmarshaller = unmar;
	}

	/**
	 * Create <code>Resultvalue</code> object from result element
	 * @param elem
	 * @return the created <code>Resultvalue</code> object.
	 */
	public static Resultvalue getResultvalue(Element elem, Unmarshaller unmarshaller) {
		String kind = elem.attributeValue("kind");
		Resultvalue resultvalue = null;
		if (kind == null || !kind.equals("use")) {
			Element resultvalueElem = (Element)elem.elements().get(0);
			Document documentResultvalue = DocumentHelper.createDocument();
			documentResultvalue.add(resultvalueElem.createCopy());
			DOMWriter d4Writer = new org.dom4j.io.DOMWriter();
			try {
				org.w3c.dom.Document doc = d4Writer.write(documentResultvalue);
				// Unmarshal the data
				resultvalue = (Resultvalue)unmarshaller.unmarshal(doc);
			} catch (Exception e) {
				Logger.getLogger(ResultLink.class ).debug(e);
				e.printStackTrace();
			}
		}
		return resultvalue;
	}

	/**
	 * @param rule
	 * @return
	 */
	public abstract Vector<? extends Result> getResults(String rule);

	public abstract boolean isEmptyResults();

}
