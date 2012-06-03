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
 * File Name   : ExternalTool.java
 *
 * Created     : 05/06/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli;

import java.io.Serializable;

public class ExternalTool implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/** The name of the tool*/
	private String name;
	/** Whether it is considers as the default tool or not*/
	private boolean defaultEmulator;
	/** The command line used to launch it*/
	private String cmdLine;
	
	public ExternalTool(){
		defaultEmulator = false;
	}

	public String getCmdLine() {
		return cmdLine;
	}

	public void setCmdLine(String cmdLine) {
		this.cmdLine = cmdLine;
	}

	public boolean isDefaultEmulator() {
		return defaultEmulator;
	}

	public void setDefaultExternalTool(boolean defaultEmulator) {
		this.defaultEmulator = defaultEmulator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
}
