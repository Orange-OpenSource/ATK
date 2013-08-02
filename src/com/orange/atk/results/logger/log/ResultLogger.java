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
 * File Name   : ResultLogger.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.results.logger.log;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.orange.atk.interpreter.atkCore.JATKInterpreter;
import com.orange.atk.manageListener.IMeasureListener;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.DocumentGenerator;
import com.orange.atk.results.logger.documentGenerator.GraphGenerator;
import com.orange.atk.results.measurement.MeasurementThread;
import com.orange.atk.results.measurement.PlotList;
import com.orange.atk.util.FileUtil;

/**
 * This class is used to manage measurements and log informations
 */

// TODO : separate the thread and the render system
public class ResultLogger {

	private final static EventListenerList LISETENERS = new EventListenerList();

	// type of saved pictures
	private static final String EXT_PICTURE = "png";
	// document where data and messages would be saved
	private DocumentLogger documentLogger = null;
	// class which would created the document
	private DocumentGenerator documentGenerator = null;
	// folder and files where pictures are stored
	private String folderWhereResultsAreSaved = null;
	// Thread which periodically saves data
	private MeasurementThread logThread = null;
	private PhoneInterface phoneInterface = null;
	private ActionsLogger actionsLogger = null;
	private boolean isResourceslogged = true;
	private String confilepath;

	private boolean stopATK = false;

	/**
	 * Constructor
	 * 
	 * @param folderWhereResultsAreSaved
	 *            folder where files will be saved
	 * @param documentGenerator
	 *            generator for the document
	 * @throws NullPointerException
	 *             if documentGenerator is null of if folderWhereResultsAreSaved
	 *             is null
	 * @throws IllegalArgumentException
	 *             if folderWhereResultsAreSaved is not a valid folder (exists
	 *             and write right)
	 */
	public ResultLogger(String folderWhereResultsAreSaved,
			DocumentGenerator documentGenerator, String confilepath, boolean isResourceslogged) {
		this.isResourceslogged = isResourceslogged;
		this.confilepath = confilepath;
		if (folderWhereResultsAreSaved == null) {
			throw new NullPointerException(
					"folderWhereResultsAreSaved is null in Logger.<init>");
		}
		File folder = new File(folderWhereResultsAreSaved);
		if (!(folder.exists() && folder.canWrite())) {
			throw new IllegalArgumentException(folderWhereResultsAreSaved
					+ " does not exist or can be written.");
		}
		this.folderWhereResultsAreSaved = folderWhereResultsAreSaved;
		this.documentGenerator = documentGenerator;
		actionsLogger = new ActionsLogger(folderWhereResultsAreSaved);
		if (isResourceslogged) {
			documentLogger = new DocumentLogger(folderWhereResultsAreSaved);
			documentLogger.readconffile(confilepath);
		}
	}
	public ResultLogger(String folderWhereResultsAreSaved,
			DocumentGenerator documentGenerator, String confilepath) {
		this(folderWhereResultsAreSaved, documentGenerator, confilepath, true);
	}
	/**
	 * generateGraphFile
	 */
	public void generateGraphFile() {
		// log the min/max/avg values from measurements
		if (isResourceslogged) {
			Map<String, PlotList> mapgraph = documentLogger.getMapint();

			Set<String> cles = mapgraph.keySet();
			Iterator<String> it = cles.iterator();
			while (it.hasNext()) {
				String cle = (String) it.next();
				PlotList plotlist = documentLogger.getPlotList((cle));
				GraphGenerator.generateGraphWithJFreeChart(
						plotlist,
						cle, plotlist.getFolder(),
						plotlist.getYComment(),
						plotlist.getPngpath(),
						plotlist.getScale());
			}
		}
	}

	/**
	 * generate Plt File
	 */
	public void generatepltFile() {
		// log the min/max/avg values from measurements
		if (isResourceslogged) {
			Map<String, PlotList> mapgraph = documentLogger.getMapint();

			Set<String> cles = mapgraph.keySet();
			Iterator<String> it = cles.iterator();
			while (it.hasNext()) {
				String cle = (String) it.next();
				PlotList plotlist = documentLogger.getPlotList((cle));

				// Save measurements in a file
				try {
					Logger.getLogger(this.getClass()).
							debug("Logger: " + folderWhereResultsAreSaved + Platform.FILE_SEPARATOR
									+ cle
									+ ".csv");
					GraphGenerator.dumpInFile(plotlist, folderWhereResultsAreSaved
							+ Platform.FILE_SEPARATOR + cle
							+ ".csv");
					plotlist.closefile();
				} catch (ArrayIndexOutOfBoundsException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Store the action
	 * 
	 * @param actionName
	 *            name of the action
	 * @param startTime
	 *            time when the action has started
	 * @param endTime
	 *            time when the action has finished
	 */
	public void addInfotoActionLogger(String MsgType, String actionName, Date startTime,
			Date endTime) {

		actionsLogger.addAction(MsgType, actionName, startTime, endTime);
		// insert event on the graph

		if (actionsLogger.isStopJATK())
			phoneInterface.stopTestingMode();

	}

	/**
	 * Save actions made by the interpreter in the file actions.log uder the log
	 * folderwq
	 */
	public void writeActionLogFile() {
		// close temp file action logger
		this.actionsLogger.closetempfiles();
		File fichier = new File(folderWhereResultsAreSaved
				+ Platform.FILE_SEPARATOR + "actions.xml");

		File fichierconf = new File(folderWhereResultsAreSaved
				+ Platform.FILE_SEPARATOR + "Confile.xml");
		try {
			actionsLogger.save(new FileOutputStream(fichier));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Copy confile to allow the analyser to read csv files
		FileUtil.copyfile(fichierconf, new File(confilepath));

	}

	/**
	 * Save actions made by the interpreter in the file actions.log uder the log
	 * folderwq
	 */
	public void writeActionLogFile(String xmlfilepath) {

		// close temp file action logger
		this.actionsLogger.closetempfiles();

		File fichier = new File(folderWhereResultsAreSaved
				+ Platform.FILE_SEPARATOR + "actions.xml");

		File fichierconf = new File(folderWhereResultsAreSaved
				+ Platform.FILE_SEPARATOR + "Confile.xml");
		try {
			actionsLogger.save(new FileOutputStream(fichier));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Copy confile to allow the analyser to read csv files
		FileUtil.copyfile(fichierconf, new File(xmlfilepath));

	}

	/**
	 * Log an information message. It is generated by Log and screenshot
	 * 
	 * @param s
	 *            message
	 * @param line
	 *            line where this message is generated
	 * @param currentScript
	 *            script where this message is generated
	 */
	public void addInfoToDocumentLogger(String s, int line, String currentScript) {
		if (isResourceslogged)
			documentLogger.addInfoToLog(s, line, currentScript);
	}

	/**
	 * Log a warning message. Bad number of parameters, invalid function name,
	 * include file not found, ... produce this kind of message
	 * 
	 * @param s
	 *            message
	 * @param line
	 *            line where this message is generated
	 * @param currentScript
	 *            script where this message is generated
	 */
	public void addWarningToDocumentLogger(String s, int line, String currentScript) {
		if (isResourceslogged)
			documentLogger.addWarningToLog(s, line, currentScript);
		Logger.getLogger(this.getClass()).warn(currentScript + " (" + line + ") : " + s);
	}

	/**
	 * Log an error message. Parse error in the main file, ... produce this kind
	 * of message.
	 * 
	 * @param s
	 *            message
	 * @param line
	 *            line where this message is generated
	 * @param currentScript
	 *            script where this message is generated
	 */
	public void addErrorToDocumentLogger(String s, int line, String currentScript) {
		if (isResourceslogged)
			documentLogger.addErrorToLog(s, line, currentScript);
		Logger.getLogger(this.getClass()).warn(currentScript + " (" + line + ") : " + s);
	}

	/**
	 * Save cpu
	 * 
	 * @throws NullPointerException
	 *             if setInterpreter function has not been correctly called
	 *             before
	 */
	public void addToDocumentLogger(float value, String doctype) {
		if (getPhoneInterface() == null || actionsLogger.isStopJATK()) {
			setStopATK(true);
			this.interrupt();
		}
		if (isResourceslogged) {
			Date d = new Date();
			documentLogger.addDataToList(doctype, d.getTime(), value);
		}
	}

	/**
	 * Save memory, cpu, battery and storage used
	 * 
	 * @throws PhoneException
	 * 
	 * @throws NullPointerException
	 *             if setInterpreter function has not been correctly called
	 *             before
	 */
	public void addResourcesInfoToDocumentLogger() throws PhoneException {
		if (getPhoneInterface() == null || actionsLogger.isStopJATK()) {
			setStopATK(true);
			this.interrupt();
			// TODO return ?
		}
		if (isResourceslogged) {
			Date d = new Date();
			// TODO modifier pour rendre generic ??
			Map<String, PlotList> mapint = documentLogger.getMapint();
			Set<String> cles = mapint.keySet();
			Iterator<String> it = cles.iterator();
			List<String> sampledKeys = new ArrayList<String>();
			while (it.hasNext()) {
				String cle = (String) it.next();
				PlotList plotlist = mapint.get(cle);
				if (plotlist.isSampled())
					sampledKeys.add(cle);
			}

			if (getPhoneInterface() != null && logThread != null && logThread.isRunning()) {
				try {
					HashMap<String, Long> values = getPhoneInterface().getResources(sampledKeys);
					cles = values.keySet();
					it = cles.iterator();
					while (it.hasNext()) {
						String cle = (String) it.next();
						float v = values.get(cle).floatValue();
						documentLogger.addDataToList(cle, d.getTime(), v);
						// fireResourceChanged(String.valueOf(v));
					}
				} catch (PhoneException e) {
					addInfotoActionLogger("Error JATK", "Monitor connection lost", new Date(),
							new Date());
					getPhoneInterface().stopTestingMode();
					getPhoneInterface().startTestingMode();
				}

			}
		}
	}

	protected void fireResourceChanged(String newMemValue) {
		for (IMeasureListener listener : getPerfListeners()) {
			// listener.memoryChangee( newMemValue+ Platform.LINE_SEP);

		}
	}

	protected void fireStorageChanged(String newMemValue) {
		for (IMeasureListener listener : getPerfListeners()) {
			// listener.storageChangee( newMemValue+ Platform.LINE_SEP);

		}
	}

	protected void fireActionChanged(String newMemValue) {
		for (IMeasureListener listener : getPerfListeners()) {
			listener.addactionChangee(newMemValue);

		}
	}

	protected void fireoutputChanged(String newMemValue) {
		for (IMeasureListener listener : getPerfListeners()) {
			listener.addOutputChangee(newMemValue);

		}
	}

	public void addPerfListener(IMeasureListener listener) {
		LISETENERS.add(IMeasureListener.class, listener);
	}

	public void removePerfListener(IMeasureListener listener) {
		LISETENERS.remove(IMeasureListener.class, listener);
	}

	public IMeasureListener[] getPerfListeners() {
		return LISETENERS.getListeners(IMeasureListener.class);
	}

	/**
	 * Save the image image under the log folder with the name fileName
	 * 
	 * @param fileName
	 *            name of the picture, without the extension. Not null
	 * @param image
	 *            image to save. Not null.
	 * @return true if the screenshot has been correctly save, false otherwise *
	 * @throws NullPointerException
	 *             if fileName or image are null.
	 */
	public boolean saveScreenshot(String fileName, RenderedImage image) {
		if (fileName == null) {
			throw new NullPointerException("fileName is null");
		}
		if (image == null) {
			throw new NullPointerException("image is null");
		}
		File fichier = new File(folderWhereResultsAreSaved + Platform.FILE_SEPARATOR
				+ "screenshots" + Platform.FILE_SEPARATOR + fileName + "."
				+ EXT_PICTURE);
		File directory = new File(folderWhereResultsAreSaved + Platform.FILE_SEPARATOR
				+ "screenshots");
		if (!directory.exists()) {
			if (!directory.mkdirs())
				Logger.getLogger(this.getClass()).warn("Can't make dir " + directory.getPath());

		}
		try {
			if (!ImageIO.write(image, EXT_PICTURE, fichier)) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Set the currently {@link PhoneInterface} used.
	 * 
	 * @param phoneInterface
	 *            PhoneInterface
	 * @throws NullPointerException
	 *             if phoneInterface is null
	 */
	public void setPhoneInterface(PhoneInterface phoneInterface) {
		if (phoneInterface == null) {
			throw new NullPointerException(
					"A valid PhoneInterface should be provided");
		}
		this.phoneInterface = phoneInterface;
	}

	/**
	 * Set the currently {@link JATKInterpreter} used. This function must be
	 * called before calling the {@link ResultLogger}.start(int iInterval)
	 * function. This function also defines the good PhoneInterface.
	 * 
	 * @param interpreter
	 *            interpreter
	 * @throws NullPointerException
	 *             if interpreter is null
	 */
	public void setInterpreter(JATKInterpreter interpreter) {
		if (interpreter == null) {
			throw new NullPointerException(
					"A valid Interpreter should be provided");
		}
		setPhoneInterface(interpreter.getPhoneInterface());
	}

	/**
	 * Start the main log of measurements
	 * 
	 * @param iInterval
	 *            interval between each measurement
	 * @throws IllegalArgumentException
	 *             if iInterval is equal to 0 or less
	 * @throws NullPointerException
	 *             if a PhoneInterface has not been set before
	 * @throws IllegalStateException
	 *             if start the thread is always alive
	 */
	public void start(int iInterval) {
		if (getPhoneInterface() == null) {
			throw new NullPointerException(
					"A valid PhoneInterface should be provided before calling Logger.start");
		}
		if (logThread != null) {
			if (logThread.isAlive()) {
				throw new IllegalStateException(
						"The thread is always alive, called interrupt first");
			}
		}
		// lance le thread de mesure
		logThread = new MeasurementThread(this);
		logThread.start(iInterval);
	}

	/**
	 * Interrupt the log thread.
	 * 
	 * @throws NullPointerException
	 *             if start has not been called successfully before
	 */
	public void interrupt() {
		if (logThread != null) {
			try {
				logThread.interrupt();
			} catch (SecurityException ex) {
				Logger.getLogger(this.getClass())
						.
						warn("Internal error : Current Thread is not allow to interrupt the logThread in Logger.interrupt()");
			}
		}
	}

	/**
	 * Wait for log thread to be totally stopped. Logger.interrupt() should be
	 * called first.
	 */
	public void join() {
		if (logThread != null) {
			logThread.join();
			logThread = null;
		}
	}

	/**
	 * Indicate if the current log system is working.
	 * 
	 * @return true if the main log system is running, false otherwise.
	 */
	public boolean isAlive() {
		if (logThread == null) {
			return false;
		}
		return logThread.isAlive();
	}

	/**
	 * This function saves the informations logged in the DocumentLogger in a
	 * human readable file.
	 * 
	 * @param isParseErrorHappened
	 *            if true, prevent the file system from saving measurements
	 *            graphs
	 */
	public void dumpInStream(boolean isParseErrorHappened) {
		if (isResourceslogged && documentGenerator != null)
			documentGenerator.dumpInStream(isParseErrorHappened, documentLogger);
	}

	public PhoneInterface getPhoneInterface() {
		return phoneInterface;
	}

	public void setStopATK(boolean stopATK) {
		this.stopATK = stopATK;
	}

	public boolean isStopATK() {
		return stopATK;
	}

	public DocumentLogger getDocumentLogger() {
		return documentLogger;
	}

	public void setDocumentLogger(DocumentLogger documentLogger) {
		this.documentLogger = documentLogger;
	}

	public ActionsLogger getActionsLogger() {
		return actionsLogger;
	}

}
