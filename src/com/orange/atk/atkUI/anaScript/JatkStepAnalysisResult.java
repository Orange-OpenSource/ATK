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
 * File Name   : JatkStepAnalysisResult.java
 *
 * Created     : 29/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaScript;

import java.util.Calendar;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.StepAnalysisResult;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class JatkStepAnalysisResult extends StepAnalysisResult {

	private String flashDigest = null;
	private String flash_name = null;

	/**
	 *
	 * @param flashDigest
	 * @param reportPath
	 * @param flash_name
	 * @param profile_name
	 * @param profile_version
	 * @param analysisDate
	 * @param verdict
	 * @param user_verdict
	 * @param reason
	 * @param http_authorized
	 * @param matos_version
	 */
	public JatkStepAnalysisResult(String flashDigest, String reportPath,
			String flash_name, Calendar analysisDate, String verdict, String user_verdict,
			String reason, String http_authorized, String matos_version) {
		super(reportPath, analysisDate, verdict,
				user_verdict, reason, http_authorized, matos_version);
		this.flashDigest = flashDigest;
		this.flash_name = flash_name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.StepAnalysisResult#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if ((obj instanceof JatkStepAnalysisResult)&& super.equals(obj)) {
			JatkStepAnalysisResult res = (JatkStepAnalysisResult) obj;
			return (
					(flash_name.equals(res.flash_name))
					&& (flashDigest.equals(res.flashDigest))
			);

		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.StepAnalysisResult#toString()
	 */
	public String toString() {
		String st = "Result for :"+
		"\n flashname:"+ ((flash_name==null) ? "none" : flash_name)+
		"\n flashfilemd5:"+ flashDigest+
		super.toString();
		return st;

	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.StepAnalysisResult#toHTML(com.orange.atk.atkUI.corecli.Step)
	 */
	@Override
	public String toHTML(Step currentStep) {
		String st = "<html><center><u>Last result informations</u></center>";

		JatkStep step = (JatkStep) currentStep;

//		if (!flashDigest.equals(step.getFlashFileDigest())) {
		if (!same(flashDigest, step.getFlashFileDigest())) {

			 st = st + "<font color=red>";
		}
//		st = st + "<b>flash file</b>: "+ ((flash_name==null) ? "none" : flash_name);
		st = st + "<b>flash file</b>: "+ ((flashDigest==null) ? "none" : flash_name);
//		if (!flashDigest.equals(step.getFlashFileDigest())) {
		if (!same(flashDigest, step.getFlashFileDigest())) {
			 st = st + "</font>";
		}


		st = st + "<br> <b>verdict</b>: "+verdict;
		st = st + "<br> <b>user verdict</b>: "+ ((user_verdict==null) ? "none" : user_verdict);
		if (http_authorized!=null && http_authorized.length()!=0){
			if (!http_authorized.equals(step.getHttpAuthorized())) {
				st = st + "<font color=red>";
			}
			st = st + "<br> <b>authorized URLs</b>: " + http_authorized;
			if (!http_authorized.equals(step.getHttpAuthorized())) {
				st = st + "</font>";
			}
		}

		String currentMatosVersion = Configuration.getVersion();
		if (!matos_version.equals(currentMatosVersion)) {
			st = st + "<font color=red>";
		}
		st = st + "<br> <b>midlet analyser version</b>: "+matos_version;
		if (!matos_version.equals(currentMatosVersion)) {
			st = st + "</font>";
		}

		st = st + "<br> <b>on</b>: ";
		if (analysisDate==null) {
			st = st + "?";
		} else {
			st = st + analysisDate.getTime().toString();	}

		st = st + "</html>";
		return st;
	}

}
