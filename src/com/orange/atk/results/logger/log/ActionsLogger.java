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
 * File Name   : ActionsLogger.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.results.logger.log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.orange.atk.platform.Platform;

public class ActionsLogger {
	private Vector<Action> listaction = new Vector<Action>();


	private Date startTime = null;
	private String folderWhereResultsAreSaved = null;
	private String tempActionFile = null;
	private boolean StopJATK = false;
	File fichier = null;
	BufferedWriter os = null;
	
	private String hopperPath = null;
	File fichierHopper = null;
	BufferedWriter os_Hopper = null;

	public ActionsLogger() {
	}



	public Date getStartTime() {
		return startTime;
	}

	public ActionsLogger(String folder) {

		folderWhereResultsAreSaved = folder;
		tempActionFile = folderWhereResultsAreSaved + Platform.FILE_SEPARATOR
				+ "action_temp.log";
		fichier = new File(tempActionFile);
		try {
			os = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tempActionFile)));
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass()).
				debug("Error Generation tempFileAction");
			e.printStackTrace();
		}

	}

	
	
	public void createHopperTstfile () {

		hopperPath = folderWhereResultsAreSaved + Platform.FILE_SEPARATOR
				+ "HopperTest.tst";
		
		fichierHopper = new File(hopperPath);
		
		if(!fichierHopper.exists())
			try {
				fichierHopper.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			os_Hopper = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fichierHopper)));
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass()).
				debug(" Error Generation tst Hopper file");
			e.printStackTrace();
		}

	}
	
	public void closetempfiles() {

		try {
			if(os_Hopper!=null)
				os_Hopper.close();

			if(os!=null)
				os.close();
			if(fichier!=null){
				if(!fichier.delete()){
					Logger.getLogger(this.getClass() ).debug("Can't delete "+fichier.getPath());
				}else{	
					fichier = null;
				}
			}

			if(fichierHopper!=null){
				if(!fichierHopper.delete()){
					Logger.getLogger(this.getClass() ).debug("Can't delete "+fichierHopper.getPath());
				}else{	
					fichierHopper = null;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	
	/**
	 * Write the actions in the provided output stream
	 * 
	 * @param out
	 *            stream where the data will be saved
	 * @return if the data have been well saved, false otherwise
	 */
	final synchronized public boolean writeHopperTstFile(String MsgType) {
		// format:
		// <actions>
		// <action name="keypress" starttime="yyyy.MM.dd HH:mm:ss SSS"
		// endtime="yyyy.MM.dd HH:mm:ss SSS" />
		// <action name="screenshot" starttime="yyyy.MM.dd HH:mm:ss SSS"
		// endtime="yyyy.MM.dd HH:mm:ss SSS" />
		// </actions>
		try {

			os_Hopper.write(MsgType+ Platform.LINE_SEP);
				
			os_Hopper.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	
	
	
	
	
	/**
	 * Write the actions in the provided output stream
	 * 
	 * @param out
	 *            stream where the data will be saved
	 * @return if the data have been well saved, false otherwise
	 */
	final synchronized public boolean writetempFile(String MsgType, String actionName,
			Date startTimeaction, Date endTime) {
		// format:
		// <actions>
		// <action name="keypress" starttime="yyyy.MM.dd HH:mm:ss SSS"
		// endtime="yyyy.MM.dd HH:mm:ss SSS" />
		// <action name="screenshot" starttime="yyyy.MM.dd HH:mm:ss SSS"
		// endtime="yyyy.MM.dd HH:mm:ss SSS" />
		// </actions>
		SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
		try {

			os.write("<actions " + " MsgType=\"" + MsgType + "\"" + " Msg=\""
					+ actionName + "\"" + " starttime=\""
					+ spf.format(startTimeaction) + "\" " + " endtime=\""
					+ spf.format(endTime) + "\" />" + Platform.LINE_SEP);

			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Add an action to the current set of executed actions
	 * 
	 * @param action
	 *            action to add
	 * @throws NullPointerException
	 *             if action is null
	 * @throws IllegalArgumentException
	 *             if action is not valid
	 */
	public void addAction(String MsgType, String actionName,
			Date startTimeaction, Date endTime) {

		if ((endTime == null) || (startTimeaction == null))
			throw new IllegalArgumentException(
					"At startTime or  endTime variables is null");
		if (actionName == null) {
			Logger.getLogger(this.getClass() ).debug("Action Name is NULL MsgType:"
					+ MsgType + " Date :" + startTimeaction);
			Logger.getLogger(this.getClass() ).debug(" Stop measure");
			this.setStopJATK(true);
		} else if (actionName.equals(" null")) {
			Logger.getLogger(this.getClass() ).debug("Action Name is ' null' MsgType:"
							+ MsgType + " Date :" + startTimeaction);
			Logger.getLogger(this.getClass() ).debug(" Stop measure");
			this.setStopJATK(true);
		

	} else if (actionName.equals("")) {
		actionName="Empty String sent by device as std output or ERR";
		Logger.getLogger(this.getClass() )
					.debug("Action Name is '"+actionName+"' MsgType:"
						+ MsgType + " Date :" + startTimeaction);
	}
		
		try {
			Action a = new Action();
			a.setMsgType(MsgType);
			a.setActionName(actionName);
			a.setStartTime(startTimeaction);
			a.setEndTime(endTime);
			listaction.add(a);
			writetempFile(MsgType, actionName, startTimeaction, endTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/**
	 * Add an action to the current set of executed actions
	 * 
	 * @param action
	 *            action to add
	 * @throws NullPointerException
	 *             if action is null
	 * @throws IllegalArgumentException
	 *             if action is not valid
	 */
	public void addAction(Action action) {
		if (action == null) {
			throw new NullPointerException();
		}
		if ((action.getActionName() == null) || (action.getEndTime() == null)
				|| (action.getStartTime() == null))
			throw new IllegalArgumentException(
					"At least one of the actions variables is null");
		// action.getActionName().equals("") could not happened
		if (action.getEndTime().getTime() < action.getStartTime().getTime()) {
			throw new IllegalArgumentException();
		}
			listaction.add(action);
		if (getStartTime() == null)
			startTime = action.getStartTime();

	}

	
	/**
	 * Get the actions created from addActions or load
	 * 
	 * @return actions, not null
	 */
	public Vector<Action> getSpecificActionsVect(String cle) {
		
		Vector<Action> shortactionlist =new Vector<Action>();
		for(int i=0;i<listaction.size();i++)
		{
			Action action =listaction.get(i);
			String MsgType = action.getMsgType();
			if(MsgType.equals(cle))
				shortactionlist.add(action);
		}
		return shortactionlist;
	}
	
	/**
	 * Get the actions created from addActions or load
	 * 
	 * @return actions, not null
	 */
	public Vector<Action> getActions() {
		return listaction;
	}

	/**
	 * Write the actions in the provided output stream
	 * 
	 * @param out
	 *            stream where the data will be saved
	 * @return if the data have been well saved, false otherwise
	 */
	public boolean save(OutputStream out) {
		// format:
		// <actions>
		// <action name="keypress" starttime="yyyy.MM.dd HH:mm:ss SSS"
		// endtime="yyyy.MM.dd HH:mm:ss SSS" />
		// <action name="screenshot" starttime="yyyy.MM.dd HH:mm:ss SSS"
		// endtime="yyyy.MM.dd HH:mm:ss SSS" />
		// </actions>
		SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
		int size = listaction.size();
		PrintStream ps = new PrintStream(out);
		ps.println("<AllAction>");
		for (int i = 0; i < size; i++) {
			Action a = listaction.get(i);
			
			ps.println("<actions " + " MsgType=\"" + a.getMsgType() + "\""
					+ " Msg=\"" + a.getActionName().replace("\"","") + "\"" + " starttime=\""
					+ spf.format(a.getStartTime()) + "\" " + " endtime=\""
					+ spf.format(a.getEndTime()) + "\" />");
			
			Logger.getLogger(this.getClass() ).debug("<actions " + " MsgType=\"" + a.getMsgType() + "\""
					+ " Msg=\"" + a.getActionName() + "\"" + " starttime=\""
					+ spf.format(a.getStartTime()) + "\" " + " endtime=\""
					+ spf.format(a.getEndTime()) + "\" />");
			
		}
		ps.println("</AllAction>");
		ps.flush();
		ps.close();
		ps=null;
		return true;
	}
	
	
	
	
	
	
	

	/**
	 * Get the actions saved from the input stream and add it to the current
	 * list of Actions.
	 * 
	 * @param in
	 * @return if the data have been well loaded, false otherwise
	 */

	public boolean load(File file) {

		try {
			SAXParserFactory fabrique = SAXParserFactory.newInstance();
			SAXParser parseur = fabrique.newSAXParser();

			ActionsHandler gestionnaire = new ActionsHandler(this);
			parseur.parse(file, gestionnaire);
		} catch (SAXException ex) {
			// Logger.getLogger(ActionsLogger.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (IOException ex) {
			// Logger.getLogger(ActionsLogger.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (ParserConfigurationException ex) {
			// Logger.getLogger(SimpleSaxParser.class.getName()).log(Level.SEVERE,
			// null, ex);
		}

		return true;
	}

	
	/**
	 * Stop JATK
	 * 
	 * @param stopJATK true to stop JATK
	 */
	
	public void setStopJATK(boolean stopJATK) {
		StopJATK = stopJATK;
	}

	
	/**
	 * check if JATK is still running
	 * 
	
	 * @return if boolean is true JATK is stopped
	 */
	public boolean isStopJATK() {
		return StopJATK;
	}


}
