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
 * File Name   : Result.java
 *
 * Created     : 25/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.resultLink;

import com.orange.atk.atkUI.corecli.reportGenerator.bind.Resultvalue;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class Result {

	protected String name;
	protected Resultvalue resultvalue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Resultvalue getResultvalue() {
		return resultvalue;
	}

	public void setResultvalue(Resultvalue resultvalue) {
		this.resultvalue = resultvalue;
	}

	public abstract boolean equals(Object obj);

}
