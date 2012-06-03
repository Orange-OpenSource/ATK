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
 * File Name   : IAnalysisResultsManager.java
 *
 * Created     : 22/06/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.corecli;


/**
 * This interface defines an API to notify about new analysis results
 * and to request for previous ones
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public interface IAnalysisResultsManager {

	/**
	 * Notify that a new analysis result is available 
	 * for the given <code>Step</code>
	 * @param step the concerned <code>Step</code>
	 * @param result the new analysis result
	 */
	public void notify(Step step, StepAnalysisResult result);
	
	/**
	 * Asks for an analysis result for the given <code>Step</code>
	 * @param step the concerned <code>Step</code>
	 * @return an analysis result or null if none avaible
	 */
	public StepAnalysisResult getPreviousAnalysisResult(Step step);

}
