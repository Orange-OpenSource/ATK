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
 * File Name   : DownloadMonitor.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli.utils;

/**
 * This class is responsible for monitoring the status of a download session. A
 * user will typically create an instance of Downloader as well as an instance
 * of DownloadMonitor. The user will start the Downloader object, as an
 * independant thread, then call the DownloadMonitor.waitForEnd() method, which
 * is blocked until the status is changed to something else than DLOAD_ONGOING.
 * When created, the Downloader thread is passed a pointer to its
 * DownloadMonitor, to keep it informed of the evolution of the session's
 * status.
 */
public class DownloadMonitor {

	public static final int DLOAD_FAILED = 1;
	public static final int DLOAD_SUCCESS = 2;
	public static final int DLOAD_ONGOING = 3;
	public static final int DLOAD_TIMEOUT_C = 4; // during connection
	public static final int DLOAD_TIMEOUT_T = 5; // during transfer
	public static final int DLOAD_FAILED_NO_NEW_ATTEMPT = 6;

	private int status = DLOAD_ONGOING;

	int getDownloadStatus() {
		return status;
	};

	void setDownloadStatus(int status) {
		this.status = status;
	}

	/**
	 * Reset the session status to DLOAD_ONGOING
	 */
	void reset() {
		setDownloadStatus(DLOAD_ONGOING);
	}

	/**
	 * Allows to wait for the end (successful of or not) of the download
	 * session. The method returns only when the session is over (Active wait).
	 */
	void waitForEnd() {
		while (getDownloadStatus() == DLOAD_ONGOING) {
		}
	};

}
