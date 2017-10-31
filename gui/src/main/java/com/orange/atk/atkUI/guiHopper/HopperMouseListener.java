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
 * File Name   : HopperMouseListener.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import com.orange.atk.atkUI.coregui.actions.MatosAction;
import com.orange.atk.atkUI.guiHopper.actions.JFrameList;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;


/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HopperMouseListener extends MouseAdapter {

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
	private HopperCheckListTable _flashtable;

	/**
	 * Mouse coordinate at click time
	 */
	int x,y;
	private MatosGUI matosGui;
	private int row;
	private HopperMouseListener instance;
	
	public HopperMouseListener(JPopupMenu popupMenu, HopperCheckListTable flashtable) {
		_popup = popupMenu;
		_table = flashtable.getTable();
		_flashtable = flashtable;
		instance = this;
	}

	/**
	 * Called if the mouse is pressed
	 * @param e An event from the mouse
	 */
	public void mousePressed(MouseEvent e) {
		if (_flashtable.isEnableUserActions()) {
			if (e.getButton() == MouseEvent.BUTTON3)  {
				x = e.getX();
				y = e.getY();
			}
			maybeShowPopup(e);
		}
	}

	public void notifySelectedIndices(int[] indices, String[] allUID) {
		String listProg="";
		for(int j=0;j<indices.length;j++)
		{
			PhoneInterface phone = AutomaticPhoneDetection.getInstance().getDevice();
			if(phone instanceof DefaultPhone)
				return;

			if(!listProg.equals("")) listProg=listProg+","	;
			listProg=listProg+allUID[indices[j]];	
		}
		Logger.getLogger(this.getClass() ).debug("Selected array"+listProg);
		if (!listProg.equals("")) {
			String prog = (String) _table.getModel().getValueAt(row, HopperCheckListTable.COLUMN_TESTNAME);
			_table.getModel().setValueAt(prog+","+listProg,row, HopperCheckListTable.COLUMN_TESTNAME);
		}
	}
	
	/**
	 * Treat the right button clic to show the popup menu
	 * @param e An event from the mouse
	 */
	private void maybeShowPopup(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) { // rigth-click isPopupTrigger()
			row = _table.rowAtPoint(new Point(x,y));
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
				if (_flashtable.areRowModified(_table.getSelectedRows())) {
					MatosAction.CONFIRMVERDICT.setEnabled(false);
					MatosAction.MODIFYVERDICT.setEnabled(false);
				} else {
					MatosAction.CONFIRMVERDICT.setEnabled(true);
					MatosAction.MODIFYVERDICT.setEnabled(true);
				}
			}else{
				MatosAction.PROPERTIES.setEnabled(true);
				// TODO _popup.getComponent(RUN_TOOL).setEnabled(true);
				String verdict = (String)_table.getModel().getValueAt(row, HopperCheckListTable.COLUMN_VERDICT);
				if (verdict != null && !verdict.equals("")){
					MatosAction.VIEWREPORT.setEnabled(true);
					if (_flashtable.isRowModified(row)) {
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
			MatosAction.PASTE.setEnabled( _flashtable.getCopiedItems().size() > 0 );
			_popup.show(e.getComponent(), x,y);
		}else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2){
			int column = _table.columnAtPoint(new Point(e.getX(), e.getY()));
			//row = _table.rowAtPoint(new Point(e.getX(), e.getY()));
			if (column == HopperCheckListTable.COLUMN_VERDICT){
				ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), MatosAction.EXIT.getName());
				MatosAction.VIEWREPORT.getAction().actionPerformed(ae);
			}
			else if(column == HopperCheckListTable.COLUMN_TESTNAME){
				row = _table.rowAtPoint(new Point(e.getX(), e.getY()));
				matosGui = CoreGUIPlugin.mainFrame;
				String[] allUID=AutomaticPhoneDetection.getInstance().getDevice().getRandomTestList();
				JFrameList fl=  new JFrameList(instance, allUID);
				fl.setVisible(true);
			}
			else if (column == HopperCheckListTable.COLUMN_PHONECONFIG){
				_table.rowAtPoint(new Point(e.getX(), e.getY()));
				int indexRow = _flashtable.getSelectedRow();
				Logger.getLogger("test").info("row :"+indexRow +"column :"+column +"selected :"+_flashtable.getComboBoxPhoneConfig().getSelectedIndex());
			}
			else if (column != HopperCheckListTable.COLUMN_TIME && column != HopperCheckListTable.COLUMN_THROTTLE){


				guihopperLink.getFlashGUI().editSelectedStepProperties();
				// TODO DB: _flashtable.updateModifiedRow(configuration.getArchivingDB, row);
				CoreGUIPlugin.mainFrame.updateButtons();
			}
		}
	}

}
