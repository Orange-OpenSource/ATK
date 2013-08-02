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
 * File Name   : StepAnalysisResult.java
 *
 * Created     : 22/05/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.corecli;

import java.util.Calendar;

/**
 * This class represents an analysis result.
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public abstract class StepAnalysisResult {
	
	protected String reportPath = null;
	protected Calendar analysisDate = null;
	protected String verdict = null;
	protected String user_verdict = null;
	protected String reason = null;
	protected String comment = null;
	protected String http_authorized = null;
	protected String matos_version = null;
	
	/**
	 * 
	 * @param reportPath
	 * @param profile_name
	 * @param profile_version
	 * @param analysisDate
	 * @param verdict
	 * @param user_verdict
	 * @param reason
	 * @param http_authorized
	 * @param matos_version
	 */
	public StepAnalysisResult(
			  String reportPath,
			  Calendar analysisDate, String verdict, String user_verdict,
			  String reason,
			  String http_authorized, String matos_version) {
		
		this.reportPath = reportPath;
		this.analysisDate = analysisDate;
		this.verdict = verdict;
		this.user_verdict = user_verdict;
		this.reason = reason;
		this.http_authorized = http_authorized;
		this.matos_version = matos_version;
	}

	/**
	 * Compares this ResultStep to the given object. The result is true if and only if 
	 * the argument is not null and is a ResultStep object that represents the same 
	 * result step as this object.
	 * 
	 * 	WARNING : There is a precision difference between 
	 *  timestamp (YYYYMMDDHHMMSS) and calendar (same+milliseconds)
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result
				+ ((analysisDate == null) ? 0 : analysisDate.getTimeInMillis()/1000));
		result = prime * result
				+ ((http_authorized == null) ? 0 : http_authorized.hashCode());
		result = prime * result
				+ ((matos_version == null) ? 0 : matos_version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StepAnalysisResult other = (StepAnalysisResult) obj;
		if (analysisDate == null) {
			if (other.analysisDate != null)
				return false;
		} else if (!((analysisDate.getTimeInMillis()/1000)==(other.analysisDate.getTimeInMillis()/1000)))
			return false;
		if (http_authorized == null) {
			if (other.http_authorized != null)
				return false;
		} else if (!http_authorized.equals(other.http_authorized))
			return false;
		if (matos_version == null) {
			if (other.matos_version != null)
				return false;
		} else if (!matos_version.equals(other.matos_version))
			return false;
		return true;
	}

	/**
	 * Build a string representation of this result
	 */
	public String toString() {
		String st = 
			"\n report:"+reportPath+
			"\n verdict:"+verdict+
			"\n reason:"+reason+
			"\n user_verdict:"+user_verdict+
			"\n http_authorized:"+http_authorized+
			"\n comment:"+comment+
			"\n midlet analyser version:"+matos_version; 
			if (analysisDate==null) { 
				st = st + "\n on ?";
			} else { 
				st = st + "\n on "+analysisDate.getTime().toString();
			}
		return st; 

	}

	/**
	 * Build an HTML representation of this result
	 */
	public abstract String toHTML(Step currentStep);

	/**
	 * Determines if two digest are equals or not. It is 'null value safe'
	 * @param digest1 
	 * @param digest2
	 * @return true iff the two digest are the same
	 */
	protected boolean same(String digest1, String digest2) {
		if (digest1==null && digest2==null) return true;
		if (digest1==null || digest2==null) return false;
		if (digest1.equals(digest2)) return true;
		return false;
	}

	/**
	 * @return the analysisDate
	 */
	public Calendar getAnalysisDate() {
		return analysisDate;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the http_authorized
	 */
	public String getHttp_authorized() {
		return http_authorized;
	}

	/**
	 * @return the matos_version
	 */
	public String getMatos_version() {
		return matos_version;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @return the reportPath
	 */
	public String getReportPath() {
		return reportPath;
	}

	/**
	 * @return the user_verdict
	 */
	public String getUser_verdict() {
		return user_verdict;
	}

	/**
	 * @return the verdict
	 */
	public String getVerdict() {
		return verdict;
	}


	
}