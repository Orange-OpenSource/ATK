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
 * File Name   : JatkResult.java
 *
 * Created     : 25/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaScript.reportGenerator.resultLink;

import com.orange.atk.atkUI.corecli.reportGenerator.resultLink.Result;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class JatkResult extends Result {

	private String function;

	/**
	 * @param ruleName
	 */
	public JatkResult(String ruleName) {
		this.name = ruleName;
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.resultLink.Result#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		// TODO Auto-generated method stub
		return false;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
