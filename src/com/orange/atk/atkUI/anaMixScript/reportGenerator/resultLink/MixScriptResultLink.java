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
 * File Name   : MixScriptResultLink.java
 *
 * Created     : 24/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaMixScript.reportGenerator.resultLink;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.orange.atk.atkUI.corecli.reportGenerator.bind.Resultvalue;
import com.orange.atk.atkUI.corecli.reportGenerator.resultLink.Result;
import com.orange.atk.atkUI.corecli.reportGenerator.resultLink.ResultLink;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class MixScriptResultLink extends ResultLink{

	private Hashtable<String, Vector<MixScriptResult>> resultsTable;

	/**
	 * @param resultsFile
	 */
	public MixScriptResultLink(File resultsFile) {
		resultsTable = new Hashtable<String, Vector<MixScriptResult>>();
		SAXReader reader = new SAXReader();
		try {
			doc = reader.read(resultsFile);
			root = doc.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public String getFlashFilePath() {
		return root.attributeValue("flashfile");
	}

	/**
	 * Get version of the analyser
	 * @return version of the analyser
	 */
	public String getAnalyserVersion() {
		String value = root.attributeValue("matosversion");
		return value;
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.resultLink.ResultLink#getResults(java.lang.String)
	 */
	@Override
	public Vector<? extends Result> getResults(String ruleName) {
		Vector<MixScriptResult> results = null;
		if (resultsTable.containsKey(ruleName)) {
			results = resultsTable.get(ruleName);
		} else {
			results = new Vector<MixScriptResult>();
			List<? extends Node> resultElems = root.selectNodes("//result[@name='"+ruleName+"']");
			Iterator<? extends Node> it = resultElems.iterator();
			while (it.hasNext()) {
				Element resultElem = (Element)it.next();
				MixScriptResult result = new MixScriptResult(ruleName);
				result.setFunction(resultElem.attributeValue("function"));
				Resultvalue resultvalue = getResultvalue(resultElem, unmarshaller);
				result.setResultvalue(resultvalue);
				results.add(result);
			}
			resultsTable.put(ruleName, results);
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.resultLink.ResultLink#isEmptyResults()
	 */
	//@Override
	public boolean isEmptyResults() {
		Enumeration<Vector<MixScriptResult>> en = resultsTable.elements();
		while (en.hasMoreElements()) {
			Vector<MixScriptResult> res = en.nextElement();
			if (res.size() != 0) return false;
		}
		return true;
	}

}
