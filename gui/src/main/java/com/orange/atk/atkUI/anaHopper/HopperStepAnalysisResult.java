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
 * File Name   : HopperStepAnalysisResult.java
 *
 * Created     : 29/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaHopper;

import java.util.Calendar;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.StepAnalysisResult;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HopperStepAnalysisResult extends StepAnalysisResult {

	private String flashDigest = null;
	private String flashName = null;

	public HopperStepAnalysisResult(String flashDigest, String reportPath, String flashName,
			Calendar analysisDate, String verdict, String userVerdict, String reason,
			String httpAuthorized, String matosVersion) {
		super(reportPath, analysisDate, verdict, userVerdict, reason, httpAuthorized, matosVersion);
		this.flashDigest = flashDigest;
		this.flashName = flashName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.corecli.StepAnalysisResult#toString()
	 */
	public String toString() {
		String st = "Result for :" + "\n flashname:" + ((flashName == null) ? "none" : flashName)
				+ "\n flashfilemd5:" + flashDigest + super.toString();
		return st;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.corecli.StepAnalysisResult#toHTML(com.orange.atk
	 * .atkUI.corecli.Step)
	 */
	@Override
	public String toHTML(Step currentStep) {
		String st = "<html><center><u>Last result informations</u></center>";

		HopperStep step = (HopperStep) currentStep;

		if (!same(flashDigest, step.getFlashFileDigest())) {
			st = st + "<font color=red>";
		}
		st = st + "<b>flash file</b>: " + ((flashDigest == null) ? "none" : flashName);

		if (!same(flashDigest, step.getFlashFileDigest())) {
			st = st + "</font>";
		}

		st = st + "<br> <b>verdict</b>: " + verdict;
		st = st + "<br> <b>user verdict</b>: " + ((user_verdict == null) ? "none" : user_verdict);
		if (http_authorized != null && http_authorized.length() != 0) {
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
		st = st + "<br> <b>midlet analyser version</b>: " + matos_version;
		if (!matos_version.equals(currentMatosVersion)) {
			st = st + "</font>";
		}

		st = st + "<br> <b>on</b>: ";
		if (analysisDate == null) {
			st = st + "?";
		} else {
			st = st + analysisDate.getTime().toString();
		}

		st = st + "</html>";
		return st;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flashDigest == null) ? 0 : flashDigest.hashCode());
		result = prime * result + ((flashName == null) ? 0 : flashName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HopperStepAnalysisResult other = (HopperStepAnalysisResult) obj;
		if (flashDigest == null) {
			if (other.flashDigest != null) {
				return false;
			}
		} else
			if (!flashDigest.equals(other.flashDigest)) {
				return false;
			}
		if (flashName == null) {
			if (other.flashName != null) {
				return false;
			}
		} else
			if (!flashName.equals(other.flashName)) {
				return false;
			}
		return true;
	}

}
