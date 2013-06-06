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
 * File Name   : IAnalysisMonitor.java
 *
 * Created     : 03/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli;

/**
 * Analysis monitor API.
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public interface IAnalysisMonitor {

	/**
	 * Tells wether the activity monitored by this monitor should stop or not.
	 * 
	 * @return true if the activity should stop, false otherwise.
	 */
	boolean isStop();

	/**
	 * Callback to inform this monitor of the analysis result of a step.
	 * 
	 * @param step
	 *            the concerned step
	 */
	void notifyStepAnalysed(Step step);

	/**
	 * Callback to inform this monitor of the end of all analysis.
	 */
	void notifyAllAnalysisDone();

}
