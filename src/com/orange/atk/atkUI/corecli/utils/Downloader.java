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
 * File Name   : Downloader.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.URL;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Alert;

/**
 * A class to handle an HTTP download session, in an independant thread. The
 * class implements the TimerMonitor interface, to allow for abortion of the
 * session upon timeout. The Timers used just have to call the timeout() method
 * to make the session end. There's a timer to guard the connection phase
 * itself, then a second one to guard the data transfer.
 */
public class Downloader extends Thread implements TimerMonitor {

	private DownloadMonitor monitor;
	private String url;
	private File target;
	private int httpMaxConTime, httpMaxDwnTime;
	private Timer connectionTimer;
	private Timer downloadTimer;
	/**
	 * A login to access to the specified URL.
	 */
	private String login;
	/**
	 * A password to access to the specified URL.
	 */
	private String password;

	/**
	 * if the connection requires an authentification of the device.
	 */
	private String userAgent;

	/** stop condition for this thread */
	private boolean shouldStop = false;

	/**
	 * Error message
	 */
	private String errorMessage;

	/**
	 * Construct a Downloader to download the file located at the given URL, and
	 * save it to the target file passed. The max times allowed for timers are
	 * other parameters. The last parameter is the DownloadMonitor to whom
	 * report the evolution of the session).
	 * 
	 * @param url
	 *            The URL to open
	 * @param target
	 *            The target file where to save the downloaded file
	 * @param httpMaxConTime
	 *            The maximum number of seconds allowed before the connection
	 *            timeout
	 * @param httpMaxDwnTime
	 *            The maximum number of seconds allowed before the transfer
	 *            timeout
	 * @param monitor
	 *            The download monitor to use to report changes of the session
	 *            status
	 */
	Downloader(String url, File target, int httpMaxConTime, int httpMaxDwnTime,
			DownloadMonitor monitor) {
		this.url = url;
		this.target = target;
		this.httpMaxConTime = httpMaxConTime;
		this.httpMaxDwnTime = httpMaxDwnTime;
		this.monitor = monitor;
		connectionTimer = new Timer(httpMaxConTime, this);
		downloadTimer = new Timer(httpMaxDwnTime, this);
	}

	/**
	 * Terminates the download session. Dumps the passed message (will be
	 * "(stopped)" if null is passed). Also, changes the status of the
	 * DownloadMonitor provided at creation, as per the one passed. Finally, the
	 * thread is stopped.
	 * 
	 * @param message
	 *            The message to dump
	 * @param status
	 *            The status to report to the associated DownloadMonitor. Valid
	 *            values are the ones defined in the DownloadMonitor class.
	 * @throws Alert
	 */
	public void terminate(String message, int status) {
		if (message == null) {
			message = "(stopped)";
		}
		errorMessage = "Download: " + message + "(" + url + ")";
		monitor.setDownloadStatus(status);
		stopAsap();
	}

	/**
	 * Method of the TimerMonitor interface. A Timer that expires calls that
	 * method, passing a reference to itself. Depending on its identity, the
	 * corresponding message and status are passed to terminate().
	 * 
	 * @param t
	 *            The Timer object that calls that method, so the Downloader can
	 *            identify which timer expired
	 */
	public void timeout(Timer t) {
		if (t == connectionTimer) {
			terminate("Connection attempt aborted, timeout (" + httpMaxConTime + " s).",
					DownloadMonitor.DLOAD_TIMEOUT_C);
		} else
			if (t == downloadTimer) {
				terminate("File transfer aborted, timeout (" + httpMaxDwnTime + " s).",
						DownloadMonitor.DLOAD_TIMEOUT_T);
			} else {
				String msg = "An unknown timer has expired !";
				Logger.getLogger(this.getClass()).error(msg);
				stopAsap();
			}
	}

	/** Stop this thread as soon as possible. */
	public synchronized void stopAsap() {
		shouldStop = true;
		// also stop asap timers
		connectionTimer.stopAsap();
		downloadTimer.stopAsap();
	}

	/**
	 * Main entry point of the thread, when started.
	 */
	public void run() {
		if (isAuthenticate()) {
			Authenticator.setDefault(new MyAuthenticator());
		}
		PrintStream outstream = null;
		HttpURLConnection huc = null;
		OutputStream out = null;
		Socket socket = null;
		InputStream in = null;
		try {
			if (target == null) {
				Logger.getLogger(this.getClass()).warn("target null");
			}
			outstream = new PrintStream(new FileOutputStream(target));
			URL urlObj = new URL(url);

			if ((userAgent != null) && (userAgent.trim().length() > 0)) {
				if (userAgent.indexOf('\n') > 0) {
					Logger.getLogger(this.getClass()).warn(
							"INVALID user_agent: '" + userAgent
									+ "' : contains a newline character --> No user agent used");
				} else {
					System.getProperties().put("http.agent", userAgent);
				}
			}

			huc = (HttpURLConnection) urlObj.openConnection();
			connectionTimer.start();
			huc.connect();
			if (!shouldStop) { // no timeout occurs during connection
				connectionTimer.stopAsap();
				int code = huc.getResponseCode();
				if (code != 200) {
					huc.disconnect();
					if (code == 404 || code == 401 || code == 502) {
						terminate("Connection failed. Server returned HTTP code " + code + ".",
								DownloadMonitor.DLOAD_FAILED_NO_NEW_ATTEMPT);
					} else {
						terminate("Connection failed. Server returned HTTP code " + code + ".",
								DownloadMonitor.DLOAD_FAILED);
					}
				} else {
					// Get an input stream for reading
					in = huc.getInputStream();
				}
			}
			if (in != null) {
				BufferedInputStream bufIn = new BufferedInputStream(in);

				// Read bytes until end of stream, and write them to local file
				downloadTimer.start();
				int data = bufIn.read();
				while ((!shouldStop) && (data != -1)) {
					outstream.write(data);
					data = bufIn.read();
				}
				bufIn.close();
				if (!shouldStop) { // no timeout occurs during download
					downloadTimer.stopAsap();
					terminate("Completed.", DownloadMonitor.DLOAD_SUCCESS);
				}
			}
			if (huc != null) {
				huc.disconnect();
			}
		} catch (MalformedURLException e) {
			terminate("Malformed URL", DownloadMonitor.DLOAD_FAILED);
		} catch (FileNotFoundException e) {
			terminate("Can't create file:" + target.getAbsolutePath(), DownloadMonitor.DLOAD_FAILED);
		} catch (IOException e) {
			terminate("I/O Error - " + e, DownloadMonitor.DLOAD_FAILED);
		} catch (SecurityException e) {
			terminate("Can't create file (security issue):" + target.getAbsolutePath(),
					DownloadMonitor.DLOAD_FAILED);
		} finally { // do it what ever exception occurs or not

			System.getProperties().remove("http.agent");

			try {
				if (out != null)
					out.close();
				if (socket != null)
					socket.close();
				if (in != null)
					in.close();
				if (outstream != null)
					outstream.close();
			} catch (IOException e) {
				terminate("I/O Error - " + e, DownloadMonitor.DLOAD_FAILED);
			}
		}
	}

	/**
	 * This class allows making a connection with an authentication (login and
	 * password).
	 * 
	 * @author apenault
	 * 
	 */
	public class MyAuthenticator extends Authenticator {
		// This method is called when a password-protected URL is accessed
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(login, password.toCharArray());
		}
	}

	/**
	 * To know if the connection must be authenticated by a login and a password
	 * 
	 * @return true if the connection to the given URL requires a login and a
	 *         password
	 */
	public boolean isAuthenticate() {
		return (login != null && password != null);
	}

	/**
	 * Sets the value of User-Agent to use for the HTTP connection.
	 * 
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}