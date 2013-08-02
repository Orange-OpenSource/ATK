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
 * File Name   : FileResolver.java
 *
 * Created     : 01/03/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;

/**
 * This class allow to retrieve a file by its path, whatever it has to be
 * downloaded or not. It handles a cache of downloaded files to prevent
 * downloading twice the same file
 * 
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class FileResolver {

	/** the temporary directory to store downloaded files */
	private File tempDir = null;
	/** the maximum time to wait for connexion (in ms) */
	private int httpMaxConTime = 0;
	/** the maximum time to wait for download (in ms) */
	private int httpMaxDwnTime = 0;
	/** the maximun attempts for download */
	private int httpMaxAttempts = 0;
	/**
	 * A map used to prevent downloading twice the same file (same URI). Since
	 * it is static, it is shared among all FileResolvers (provided it is loaded
	 * by the same classloader!)
	 * */
	private static Map<String, File> downloadedFiles = new HashMap<String, File>();

	/**
	 * Build a new FileResolver with the given options
	 * 
	 * @param tempDir
	 *            the temporary directory to store downloaded files.
	 * @param httpMaxConTime
	 *            the maximum time to wait for connexion (in ms)
	 * @param httpMaxDwnTime
	 *            the maximum time to wait for download (in ms)
	 * @param proxySet
	 *            wether the proxy have to be set or net
	 * @param proxyHost
	 *            the name of the proxy
	 * @param proxyPort
	 *            the proxy port to use
	 */
	public FileResolver(File tempDir, int httpMaxConTime, int httpMaxDwnTime, int httpMaxAttempts,
			boolean proxySet, String proxyHost, String proxyPort) {
		this.tempDir = tempDir;
		if (!this.tempDir.isDirectory()) {
			Alert.raise(null, "Unable to find temporary directory '" + tempDir + "'");
		}
		this.httpMaxConTime = httpMaxConTime;
		this.httpMaxDwnTime = httpMaxDwnTime;
		this.httpMaxAttempts = httpMaxAttempts;

		if (proxySet) {
			// configure proxy for http
			System.getProperties().put("http.proxyHost", proxyHost);
			System.getProperties().put("http.proxyPort", proxyPort);
		}
	}

	public File getFile(String uri, String prefix, String ext, String login, String password,
			String userAgent, File tempDir) {
		File file = null;
		if (uri.startsWith("http:")) { // distant file
			// first look for the file in cache
			file = downloadedFiles.get(uri);
			if (file == null) {
				// build the base name and extention to give to the file
				try {
					file = File.createTempFile(prefix, ext, tempDir);
				} catch (IOException e) {
					Logger.getLogger(this.getClass()).error(e);
				}
				if (file != null) {
					String fileName = file.getName();
					int lastDotIndex = fileName.lastIndexOf('.');
					String baseFileName = fileName.substring(0, lastDotIndex);
					Logger.getLogger(this.getClass())
							.info("downloading file at '" + uri + "' to '" + file.getAbsolutePath()
									+ "'");
					file = httpDownload(uri, tempDir, baseFileName, ext, login, password, userAgent);
					Logger.getLogger(this.getClass()).info(
							"download ok to '" + file.getAbsolutePath() + "'");
					// add it in cache
					downloadedFiles.put(uri, file);
				}
			}
		} else { // local file
			file = new File(uri);

			if (!file.exists()) {
				file = null;
				Alert.raise(null, "Unable to find file '" + uri + "'");
			}
		}
		return file;
	}

	/**
	 * Retrieves a file located at the given URI. It the file is distant, it
	 * will be downloaded and put in cache.
	 * 
	 * @param uri
	 *            location of the file to retrieve.
	 * @param fileName
	 *            the name to give to the downloaded file (may be null).
	 * @param login
	 *            login to use to download the file.
	 * @param password
	 *            to use to download the file.
	 * @param userAgent
	 *            to use to download the file.
	 * @return the file or null if there is no file at the given uri.
	 * @throws Alert
	 *             if the file cannot be found.
	 */
	public File getFile(String uri, String prefix, String ext, String login, String password,
			String userAgent) {
		return getFile(uri, prefix, ext, login, password, userAgent, tempDir);
	}

	/**
	 * Download the file located at the given URL, and write it to the given
	 * target local directory. Return a File object on that file. Exit if there
	 * is any problem while downloading, or to store the downloaded file.
	 * 
	 * @param location
	 *            the URL of the file to fetch.
	 * @param targetDir
	 *            the directory where to save the downloaded file.
	 * @param baseName
	 *            the base name used to build the file name.
	 * @param ext
	 *            the extention of the file name
	 * @param login
	 *            login to use to download the file.
	 * @param password
	 *            to use to download the file.
	 * @param userAgent
	 *            to use to download the file.
	 * @return the downloaded file.
	 * @throws Alert
	 *             if a problem occurs during download.
	 */
	private File httpDownload(String location, File targetDir, String baseName, String ext,
			String login, String password, String userAgent) throws Alert {

		Downloader downloader = null;
		DownloadMonitor downloadMonitor = null;
		int attemptsDone = 0;

		// build a valid name to give to the file base on given baseName and ext
		String targetFileName = baseName;
		if (!ext.startsWith(".")) {
			targetFileName += ".";
		}
		targetFileName += ext;
		File target = new File(targetDir, targetFileName);
		int i = 1;
		while (target.exists()) {
			targetFileName = "" + i + baseName;
			if (!ext.startsWith(".")) {
				targetFileName += ".";
			}
			targetFileName += ext;
			target = new File(targetDir, targetFileName);
			i++;
		}
		target.deleteOnExit();

		// launch a download thread for that file, providing it with
		// a monitor to keep an eye on the status of the operation.
		boolean done = false;

		// used to implement a strategy à la windows : in case of failure,
		// timeout is double for each retry (until maxAttempts reached).
		int httpConTimeOut = httpMaxConTime;
		int httpDwnTime = httpMaxDwnTime;
		while (!done && (attemptsDone < httpMaxAttempts)) {
			downloadMonitor = new DownloadMonitor();
			downloader = new Downloader(location, target, httpConTimeOut/* httpMaxConTime */,
					httpDwnTime/* httpMaxDwnTime */, downloadMonitor);
			if (login != null && password != null && login.length() != 0) {
				downloader.setLogin(login);
				downloader.setPassword(password);
			}
			if ((userAgent != null) && (userAgent.trim().length() > 0)) {
				downloader.setUserAgent(userAgent);
			}
			downloader.start();
			downloadMonitor.waitForEnd();
			int status = downloadMonitor.getDownloadStatus();
			if (status == DownloadMonitor.DLOAD_SUCCESS) {
				done = true;
				attemptsDone++;
			} else {
				if (status == DownloadMonitor.DLOAD_TIMEOUT_C) {
					httpConTimeOut = httpConTimeOut * 2;
					Logger.getLogger(this.getClass()).warn("Connexion timeout increased");
					attemptsDone++;
				} else
					if (status == DownloadMonitor.DLOAD_TIMEOUT_T) {
						httpDwnTime = httpDwnTime * 2;
						Logger.getLogger(this.getClass()).warn("Download timeout increased");
						attemptsDone++;
					} else
						if (status == DownloadMonitor.DLOAD_FAILED) {
							attemptsDone++;
						} else
							if (status == DownloadMonitor.DLOAD_FAILED_NO_NEW_ATTEMPT) {
								attemptsDone = httpMaxAttempts;
							}
				// in all cases:
				downloader.stopAsap();
			}
		}
		if (!done) {
			Alert.raise(null, downloader.getErrorMessage());
		}
		return target;
	}

}
