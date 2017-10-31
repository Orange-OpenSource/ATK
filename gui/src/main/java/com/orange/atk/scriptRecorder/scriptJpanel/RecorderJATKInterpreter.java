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
 * File Name   : RecorderJATKInterpreter.java
 *
 * Created     : 29/01/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.scriptRecorder.scriptJpanel;

import com.orange.atk.interpreter.ast.ASTFUNCTION;
import com.orange.atk.interpreter.atkCore.JATKInterpreter;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.scriptRecorder.RecorderFrame;


// MMH
public class RecorderJATKInterpreter extends JATKInterpreter {
	RecorderFrame ui;
	
	public RecorderJATKInterpreter(RecorderFrame ui, PhoneInterface p, ResultLogger l,
			String currentScript, String logDir, String includeDir) {
		super(p, l, currentScript, logDir, includeDir);
		this.ui = ui;
	}
	
	public Object visit(ASTFUNCTION node, Object data) {
		ui.setRunningNode(node.getLineNumber());
		if (node.getValue().equals("StartMainlog") || node.getValue().equals("StopMainlog"))
			return Boolean.TRUE;
		return super.visit(node, data);
	}
}
