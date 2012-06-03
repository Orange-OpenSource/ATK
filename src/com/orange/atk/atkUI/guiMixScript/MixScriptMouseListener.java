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
 * File Name   : MixScriptMouseListener.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiMixScript;


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.actions.MatosAction;
import com.orange.atk.graphAnalyser.LectureJATKResult;
import com.orange.atk.platform.Platform;


/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class MixScriptMouseListener extends MouseAdapter {

	static int ANALYSE_SELECTION = 0;
	static int VIEW_REPORT = 1;
	static int RUN_TOOL = 2;
	static int COPY = 4;
	static int PASTE = 5;
	static int REMOVE = 6;
	static int CONFIRM_VERDICT = 8;
	static int MODIFY_VERDICT = 9;
	static int PROPERTIES = 11;

	/**
	 * Popup menu
	 */
	private JPopupMenu _popup;
	private JTable _table;
	private MixScriptCheckListTable _Jatktable;

	/**
	 * Mouse coordinate at click time
	 */
	int x,y;

	public MixScriptMouseListener(JPopupMenu popupMenu, MixScriptCheckListTable flashtable) {
		_popup = popupMenu;
		_table = flashtable.getTable();
		_Jatktable = flashtable;
	}

	/**
	 * Called if the mouse is pressed
	 * @param e An event from the mouse
	 */
	public void mousePressed(MouseEvent e) {
		if (_Jatktable.isEnableUserActions()) {
			if (e.getButton() == MouseEvent.BUTTON3)  {
				x = e.getX();
				y = e.getY();
			}
			maybeShowPopup(e);
		}
	}

	/**
	 * Treat the right button clic to show the popup menu
	 * @param e An event from the mouse
	 */
	private void maybeShowPopup(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) { // rigth-click isPopupTrigger()
			int row = _table.rowAtPoint(new Point(x,y));
			int [] rowSelected = _table.getSelectedRows();
			boolean select = false;
			int i=0;
			while (i<rowSelected.length && !select){
				if (rowSelected[i] == row){
					select = true;
				}
				i++;
			}
			if (!select){
				try {
					_table.setRowSelectionInterval(row, row);
				} catch (IllegalArgumentException iaex) {
					// handles case where the click is on the table
					// but no row exists under it : nothing to do
					return;
					//Out.log.println(iaex.getMessage());
					//iaex.printStackTrace(Out.log);
				}
			}
			if (_table.getSelectedRowCount()>1){
				MatosAction.VIEWREPORT.setEnabled(false);
				MatosAction.PROPERTIES.setEnabled(false);
				// TODO _popup.getComponent(RUN_TOOL).setEnabled(false);
				if (_Jatktable.areRowModified(_table.getSelectedRows())) {
					MatosAction.CONFIRMVERDICT.setEnabled(false);
					MatosAction.MODIFYVERDICT.setEnabled(false);
				} else {
					MatosAction.CONFIRMVERDICT.setEnabled(true);
					MatosAction.MODIFYVERDICT.setEnabled(true);
				}
			}else{
				MatosAction.PROPERTIES.setEnabled(true);
				// TODO _popup.getComponent(RUN_TOOL).setEnabled(true);
				String verdict = (String)_table.getModel().getValueAt(row, MixScriptCheckListTable.COLUMN_VERDICT);
				if (verdict != null && !verdict.equals("")){
					MatosAction.VIEWREPORT.setEnabled(true);
					if (_Jatktable.isRowModified(row)) {
						MatosAction.CONFIRMVERDICT.setEnabled(false);
						MatosAction.MODIFYVERDICT.setEnabled(false);
					} else {
						// if not Modified, verdict is uptodate and can be modifed or confirmed)
						MatosAction.CONFIRMVERDICT.setEnabled(true);
						MatosAction.MODIFYVERDICT.setEnabled(true);
					}
				}else{
					MatosAction.VIEWREPORT.setEnabled(false);
					MatosAction.CONFIRMVERDICT.setEnabled(false);
					MatosAction.MODIFYVERDICT.setEnabled(false);
				}
			}
			MatosAction.PASTE.setEnabled( _Jatktable.getCopiedItems().size() > 0 );
			_popup.show(e.getComponent(), x,y);
		}else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2){
			int column = _table.columnAtPoint(new Point(e.getX(), e.getY()));
			//int row = _table.rowAtPoint(new Point(e.getX(), e.getY()));
			if (column == MixScriptCheckListTable.COLUMN_VERDICT){
				ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), MatosAction.EXIT.getName());
				MatosAction.VIEWREPORT.getAction().actionPerformed(ae);
			}else if (column == MixScriptCheckListTable.COLUMN_SCREENSHOT){

				//parse le fichier test a la recherche de "// ref screenchots : ....."

				int row = _table.rowAtPoint(new Point(e.getX(), e.getY()));

				String testname =(String) _table.getModel().getValueAt(row, MixScriptCheckListTable.COLUMN_TESTNAME);
				testname=testname.replace(".tst", "");
				testname=testname.replace(".xml", "");
				String testPath=MatosGUI.outputDir+Platform.FILE_SEPARATOR+testname+Platform.FILE_SEPARATOR+"screenshots";
				Logger.getLogger(this.getClass() ).debug("Test Dir:"+testPath);

				String testScriptPath =((MixScriptCheckListTable)GuiMixScriptLink.getFlashGUI().getCheckListTable()).getToolTipFlashFile().elementAt(row);

				File file =new File(testScriptPath);
				if(file.exists())
				{
					BufferedReader br =null;
					try {
						InputStream ips=new FileInputStream(testScriptPath); 
						InputStreamReader ipsr=new InputStreamReader(ips);
						 br=new BufferedReader(ipsr);
						String ligne;
						//ligne=br.readLine();
						String refPath=null;
						//Logger.getLogger(this.getClass() ).debug("ligne :"+ligne);
						while (ips!=null && br!=null){
							if ((ligne=br.readLine())==null){
								break;
							}

							Logger.getLogger(this.getClass() ).debug("ligne :"+ligne);

							if (ligne.contains("<ref directory>")&& ligne.contains("</ref directory>")){
								String [] arrayString = ligne.split("<ref directory>");
								ligne=arrayString[1];
								arrayString = ligne.split("</ref directory>");
								refPath=arrayString[0];
								Logger.getLogger(this.getClass() ).debug("refPath :"+refPath);

								break;
							}
						}

						//	Logger.getLogger(this.getClass() ).debug("Test ref:"+testScriptPath);


					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					finally{
						try {
							if(br!=null)
							br.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				else
				{
					//pop up
				}
			}
			
			
			
else if (column == MixScriptCheckListTable.COLUMN_ANALYSER){
			
			//parse le fichier test a la recherche de "// ref screenchots : ....."

int row = _table.rowAtPoint(new Point(e.getX(), e.getY()));

String testname =(String) _table.getModel().getValueAt(row, MixScriptCheckListTable.COLUMN_TESTNAME);
testname=testname.replace(".tst", "");
testname=testname.replace(".xml", "");
String testPath=MatosGUI.outputDir+Platform.FILE_SEPARATOR+testname;
Logger.getLogger(this.getClass() ).debug("Test Dir:"+testPath);

//String testScriptPath =((MixScriptCheckListTable)GuiMixScriptLink.getFlashGUI().getCheckListTable()).getToolTipFlashFile().elementAt(row);

LectureJATKResult analyser =new LectureJATKResult();
analyser.setParameters(testPath);	
analyser.setVisible(true);
//	Logger.getLogger(this.getClass() ).debug("Test ref:"+testScriptPath);
	

}
			
			else if (column != MixScriptCheckListTable.COLUMN_COMMENTS || column != MixScriptCheckListTable.COLUMN_SCREENSHOT|| column != MixScriptCheckListTable.COLUMN_ANALYSER){
				GuiMixScriptLink.getFlashGUI().editSelectedStepProperties();
				// TODO DB: _flashtable.updateModifiedRow(configuration.getArchivingDB, row);
				CoreGUIPlugin.mainFrame.updateButtons();
			}
		}
	}

}
