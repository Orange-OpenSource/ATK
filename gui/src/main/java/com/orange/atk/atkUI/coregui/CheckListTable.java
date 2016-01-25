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
 * File Name   : CheckListTable.java
 *
 * Created     : 07/03/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.coregui;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.IProgressMonitor;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.Step.Verdict;

/**
 * This class define the concept of CheckListTable and the API to handle it.
 * It is a kind of JPanel that aims to present in a table a list of <code>Step</code>.
 * The composite Design pattern may be used for checklist tables if composite
 * checklist table are needed.
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public abstract class CheckListTable  extends AbstractCheckListTable {

	private static final long serialVersionUID = 1L;
	public static int COLUMN_NBROW = 0;
	public static final int COLUMN_TESTNAME = 1;
	public static final int COLUMN_PHONECONFIG = COLUMN_TESTNAME + 1;
	public static final int COLUMN_MODIFIED = COLUMN_PHONECONFIG + 1;
	public static final int COLUMN_VERDICT = COLUMN_MODIFIED + 1;

	/**
	 * The campaign specific to a checklist (java, flash, ...)
	 */
	protected Campaign campaign;

	protected CheckListTableModel model;

	/* ToolTips*/
	public Vector<String> toolTipFlashFile;
	public Vector<String> toolTipModified;
	
	//	-- Table management --
	protected JTable table;
	protected TableColumn nbStepColumn;
	protected TableColumn flashfileColumn;
	protected TableColumn modifiedColumn;
	protected TableColumn verdictColumn;
	protected TableColumn phoneconfigColumn;
	protected JScrollPane tablePane;

	protected Vector<String> toolTipReport;

	protected Vector<Step> copiedItems = new Vector<Step>();

	
	protected JComboBox comboBoxPhoneConfig;
	public static final String ADD_NEW_CONFIG_FILE = "Create a new config file...";
	public static final String NOT_SELECTED = "NOT SELECTED";

	protected CheckListTable() {
		super();
		
		table = new JTable(model) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean getScrollableTracksViewportHeight(){
				if (getParent() instanceof JViewport){
					return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
				}
				return false;
			}
		};

		//set ToolTip to column headers
		JTableHeader header = table.getTableHeader();
		ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
		for (int c=0; c<table.getColumnCount(); c++){
			TableColumn col = table.getColumnModel().getColumn(c);
			if (c == COLUMN_MODIFIED){
				tips.setToolTip(col, "An 'M' flag appears if the step has been modified since the last execution of this step.");
			}else{
				tips.setToolTip(col, "");
			}
		}
		header.addMouseMotionListener(tips);
		header.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				int numCol = table.columnAtPoint(e.getPoint());
				if (numCol == COLUMN_NBROW){
					sortAllRowsBy(numCol, true);
				}else{
					sortAllRowsBy(numCol, false);
				}
				model.fireTableStructureChanged();
			}
			public void mouseEntered(MouseEvent e) {}

			public void mouseExited(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {}

			public void mouseReleased(MouseEvent e) {}

		});
		
		//Create the combobox and initialize the values
		Vector<String> readListPhoneConfig = readListPhoneConfig();
		
		comboBoxPhoneConfig = (readListPhoneConfig == null) ? new JComboBox() : new JComboBox(readListPhoneConfig);
		comboBoxPhoneConfig.addItem(ADD_NEW_CONFIG_FILE);
		comboBoxPhoneConfig.addItem(NOT_SELECTED);
		comboBoxPhoneConfig.setSelectedItem(NOT_SELECTED);
		
		toolTipFlashFile = new Vector<String>();
		toolTipReport = new Vector<String>();
		toolTipModified = new Vector<String>();

	}

	protected void constructTable(){
		//set ToolTip to column headers
		JTableHeader header = table.getTableHeader();
		ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
		for (int c=0; c<table.getColumnCount(); c++){
			TableColumn col = table.getColumnModel().getColumn(c);
			if (c == COLUMN_MODIFIED){
				tips.setToolTip(col, "An 'M' flag appears if the step has been modified since the last execution of this step.");
			}else{
				tips.setToolTip(col, "");
			}
		}
		header.addMouseMotionListener(tips);
		header.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				int numCol = table.columnAtPoint(e.getPoint());
				if (numCol == COLUMN_NBROW){
					sortAllRowsBy(numCol, true);
				}else{
					sortAllRowsBy(numCol, false);
				}
				model.fireTableStructureChanged();
			}
			public void mouseEntered(MouseEvent e) {}

			public void mouseExited(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {}

			public void mouseReleased(MouseEvent e) {}

		});
		
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoCreateColumnsFromModel(false);
	}
	
	/**
	 * Returns the table object.
	 * @return the table object
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * Returns a campaign containing all steps.
	 * @return a campaign with all steps
	 */
	public Campaign getCampaign() {
		return campaign;
	}

	/**
	 * Selects all step of this checklisttable.
	 */
	public void selectAll() {
		table.selectAll();
	}

	/**
	 * Returns the number of steps in this checklisttable.
	 * @return the number of steps
	 */
	public int getStepNumber() {
		return table.getRowCount();
	}

	/**
	 * Returns the number of selected steps
	 */
	public int getSelectedRowCount() {
		return table.getSelectedRowCount();
	}

	/**
	 * Create a new check-list
	 */
	public void newCheckList() {}

	/**
	 * Adds a new row corresponding to the given "Step" object and at the row given in parameter.
	 * @param step The "Step" object to add in the corresponding table.
	 * @param rowNumberInGUI The number of row where the new step must be inserted.
	 * @param selectIt whether to select the new row in the table or not.
	 * @param checkPreviousResult if false, do not attemps to look for a previous result.
	 * if false, a check list update (updateModified operation) has to be performed to load previous results
	 */
	public abstract void addRow(Step step, int rowNumberInGUI, boolean selectIt, boolean checkPreviousResult);

	/**
	 * Copy selected rows.
	 */
	public void copySelection() {
		copiedItems = new Vector<Step>();
		int [] selectedRows = table.getSelectedRows();
		for (int i=0; i<selectedRows.length;i++){
			int numRow = selectedRows[i];
			String rowNumber = (String)model.getValueAt(numRow, COLUMN_NBROW);
			int numInCampaign = new Integer(rowNumber).intValue()-1;
			Step cmdLine = campaign.get(numInCampaign);
			copiedItems.add(cmdLine);
		}
		CoreGUIPlugin.mainFrame.updateButtons();
	}

	/**
	 * Paste copied rows under the selected rows
	 */
	public void pasteUnder() {
		int nbSelectedRow = table.getSelectedRowCount();
    	int [] selectedRows = table.getSelectedRows();
    	int lastRowSelected = selectedRows[nbSelectedRow -1];
    	int currentRow = lastRowSelected + 1;
    	for (Step step : copiedItems) {
    		Step cmdLine = (Step)step;
    		Step newCmdLine = (Step)cmdLine.getClone();
    		newCmdLine.setOutFilePath("");
    		newCmdLine.setVerdict(Verdict.NONE);
    		newCmdLine.setUserVerdict(Verdict.NONE);
    		newCmdLine.setSkippedMessage("");
    		newCmdLine.setUserComment("");
    		addRow(newCmdLine, currentRow, true, true);
			currentRow++;
    	}
    	CoreGUIPlugin.mainFrame.updateButtons();
    	CoreGUIPlugin.mainFrame.setModified(true);
    	CoreGUIPlugin.mainFrame.updateContentTabsTitle();
	}

	/**
	 * Removes the selected steps of this checklisttable.
	 * @param mon a progress monitor
	 * @param updateTable whether to update the table now or not
	 */
	public void removeSelection(IProgressMonitor mon, boolean updateTable) {
		if (table.getSelectedRowCount()!=0){
			CoreGUIPlugin.mainFrame.setModified(true);
		}
		// vector of campaign indexes of removed rows.
		Vector<Integer> campaignIndexRemovedRows = new Vector<Integer>();
		while ( ((mon!=null)&&(!mon.isStop())) &&(table.getSelectedRowCount()!=0)) {
			int numRow = table.getSelectedRow();
			campaignIndexRemovedRows.add( getNumCampaign(numRow) );
			model.removeRow(numRow);
			mon.increment();
		}
		if (updateTable) {
			updateAllAfterRemoving(campaignIndexRemovedRows);
		}
	}
	
	/**
	 * Remove the row having the given model index
	 * @param rowsToRemove
	 */
	public void removeRow(int rowToRemove) {
		model.removeRow(rowToRemove);
	}
	
	
	public void opyoneRow(int row) {
		copiedItems = new Vector<Step>();
			int numRow = row;
			String rowNumber = (String)model.getValueAt(numRow, COLUMN_NBROW);
			int numInCampaign = new Integer(rowNumber).intValue()-1;
			Step cmdLine = campaign.get(numInCampaign);
			copiedItems.add(cmdLine);
	}

	
	public void pasteatEnd() {
	//	int nbSelectedRow = table.getSelectedRowCount();
    //	int [] selectedRows = table.getSelectedRows();
    //	int lastRowSelected = selectedRows[nbSelectedRow -1];
    //	int currentRow = lastRowSelected + 1;
    	
		int currentRow =table.getRowCount();
    	for (Step step : copiedItems) {
    		Step cmdLine = (Step)step;
    		Step newCmdLine = (Step)cmdLine.getClone();
    		newCmdLine.setOutFilePath("");
    		newCmdLine.setVerdict(Verdict.NONE);
    		newCmdLine.setUserVerdict(Verdict.NONE);
    		newCmdLine.setSkippedMessage("");
    		newCmdLine.setUserComment("");
    		addRow(newCmdLine, currentRow, true, true);
			currentRow++;
    	}
    	CoreGUIPlugin.mainFrame.updateButtons();
    	CoreGUIPlugin.mainFrame.setModified(true);
    	CoreGUIPlugin.mainFrame.updateContentTabsTitle();
	}
	
	public void mixAll() {
		
	//Get Ramdom Size
    int rownb =table.getRowCount();
	
    
    for(int i=0;i<rownb*5;i++)
   {
    //get random int	
    	Random random = new java.util.Random();
        int randomnum=random.nextInt(rownb);
        Logger.getLogger(this.getClass() ).debug("random"+randomnum);
        random=null;
       // selectRows(randomnum,randomnum);
        opyoneRow(randomnum);
		Vector<Integer> campaignIndexRemovedRows = new Vector<Integer>();

		campaignIndexRemovedRows.add( getNumCampaign(randomnum) );
		model.removeRow(randomnum);
		updateAllAfterRemoving(campaignIndexRemovedRows);
	        pasteatEnd();
	        CoreGUIPlugin.mainFrame.updateButtons();
	    	CoreGUIPlugin.mainFrame.setModified(true);
	    	CoreGUIPlugin.mainFrame.updateContentTabsTitle();
    }
		
		
	}
	
	/**
	 * Update campaign associated with the checklist table and all tool tips
	 * @param removedRows (campaign indexes)
	 */
	public abstract void updateAllAfterRemoving(Vector<Integer> campRemovedRows);

	/**
	 * Update the first column of the table.
	 * @param campRemovedRows list of removed rows (campaign indexes)
	 */
	protected void updateTableAfterRemoving(Vector<Integer> campRemovedRows) {
		int nbRow = table.getRowCount();
		for (int i=0; i<nbRow; i++){
			int numInFirstColumn = new Integer((String)model.getValueAt(i, COLUMN_NBROW));
			int nbRemovedRowLower = calculateNbRemovedRowLower(campRemovedRows, numInFirstColumn);
			model.setValueAt(Integer.valueOf(numInFirstColumn - nbRemovedRowLower).toString(), i, COLUMN_NBROW);
		}
	}

	/**
	 * Calculates the number of removed rows (among the given Vector) with an index lower than the given one.
	 * @param removedRows list of campaign index of removed rows
	 * @param numInFirstColumn
	 * @return
	 */
	private int calculateNbRemovedRowLower(Vector<Integer> removedRows, int numInFirstColumn) {
		int nb = 0;
		for(Integer nbInVector : removedRows) {
			if (nbInVector<numInFirstColumn) {
				nb++;
			}
		}
		return nb;
	}

	/**
	 * Remove all campaign elements whose model index is in the 'removedRows' vector.
	 * @param removedRows
	 */
	protected void updateCampaign(Vector<Integer> campRemovedRows) {
		Iterator<Integer> it = campRemovedRows.iterator();
		while (it.hasNext()){
			int numToRemove = it.next();
			campaign.set(numToRemove, null);
		}
		Campaign newCampaign = new Campaign();
		for (Step step : campaign) {
			if (step!= null) newCampaign.add(step);
		}
		campaign = newCampaign;
	}

	/**
	 * Remove all elements whose index is in the 'removedRows' vector.
	 * @param toModify the vector to update
	 * @param removedRows the indexes of removed row (in correspondance with the toModify vector)
	 * @return the updated vector
	 */
	protected Vector<String> updateVector(Vector<String> toModify, Vector<Integer> removedRows) {
		Iterator<Integer> it = removedRows.iterator();
		while (it.hasNext()){
			int numToRemove = it.next();
			toModify.set(numToRemove, null);
		}
		Vector<String> newVector = new Vector<String>();
		for (String s : toModify) {
			if (s != null) {
				newVector.add(s);
			}
		}
		//toModify = newVector;
		return newVector;
	}

	/**
	 * Clear the current check-list
	 */
	public abstract void clear();

	public abstract String getValueAt(int numRow);

	/**
	 * Returns the index of step in campaign from its index in the model.
	 * This is usefull since the table may be ordered differently.
	 * @param indexRow model index (starting at 0)
	 * @return campaign index (starting at 0)
	 */
	public int getNumCampaign(int indexRow) {
		//NB: index displayed in table starts at '1'
		return new Integer((String)model.getValueAt(indexRow, COLUMN_NBROW)) -1 ;
	}

	public Vector<Step> getCopiedItems() {
		return copiedItems;
	}

	/**
	 * Returns a campaign containing all seleceted steps
	 * @return
	 */
	public Campaign getSelectedCampaign() {
		Campaign selection = new Campaign();
		int [] selectedRows = table.getSelectedRows();
		for (int i=0; i<selectedRows.length;i++){
			int numRow = selectedRows[i];
			String rowNumber = (String)model.getValueAt(numRow, COLUMN_NBROW);
			int numInCampaign = new Integer(rowNumber).intValue()-1;
			Step step = campaign.get(numInCampaign);
			selection.add(step);
		}
		return selection;
	}

	/**
	 * Returns the first selected Step, null if no Step is selected
	 * @return the first selected Step or null
	 */
	public Step getSelectedStep() {
		return campaign.get(table.getSelectedRow());
	}
	
	/**
	 * Returns the index of the first selected row, -1 if no row selected
	 * @return the index of the first selected row or -1
	 */
	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	protected Rectangle getRowBounds(int row){
    	Rectangle result = table.getCellRect(row, -1, true);
    	Insets i = table.getInsets();

    	result.x = i.left;
    	result.width = table.getWidth() - i.left - i.right;

    	return result;
    }

	/**
	 * Gets back model index from capaign index
	 * @param indexInCampaign campaign index 
	 * @return model index
	 */
	public int getIndexInTable(int indexInCampaign) {
		for (int i=0; i<table.getRowCount(); i++){
			//int numCampaignInTable = new Integer((String)model.getValueAt(i, COLUMN_NBROW)).intValue() -1;
			if (indexInCampaign == getNumCampaign(i)) {
//			if (indexInCampaign == numCampaignInTable) {
				return i;
			}
		}
		return -1;
	}

	public void centerRow(int numCampaign) {
		int i=0;
    	int row = 0;
		boolean finish = false;
		while (i<table.getRowCount() && !finish){
			int numCampaignInTable = new Integer((String)model.getValueAt(i, COLUMN_NBROW)).intValue() -1;
			if (numCampaign == numCampaignInTable){
				row = i;
				finish = true;
			}
			i++;
		}
        Scrolling.centerVertically(table, getRowBounds(row), false);
	}

	/**
	 * Update the given step.
	 * @param step step to update.
	 */
	public abstract void updateStep(Step step);

	/**
	 * Select a row
	 * @param row index of the row to select
	 */
	public void selectARow(int row) {
		selectRows(row, row);
	}

	/**
	 * Select several rows
	 * @param rowstart start index
	 * @param rowend end index
	 */
	public void selectRows(int rowstart, int rowend) {
		table.setRowSelectionInterval(rowstart, rowend);
	}

	protected void sortAllRowsBy(int colIndex, boolean integer) {
		Vector data = model.getDataVector();
        Collections.sort(data, new ColumnSorter(colIndex, integer));
        model.fireTableStructureChanged();
	}

	private static class ColumnSorter implements Comparator, Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int colIndex;
        boolean integer;
        ColumnSorter(int colIndex, boolean integer) {
            this.colIndex = colIndex;
            this.integer = integer;
        }
        public int compare(Object a, Object b) {
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);

            // Treat empty strains like nulls
            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }

            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
            	return 0;
            } else if (o1 == null) {
            	return 1;
            } else if (o2 == null) {
            	return -1;
            } else if (o1 instanceof Comparable) {
            	if (integer){
            		return ((Comparable<Integer>)new Integer((String)o1)).compareTo(new Integer((String)o2));
            	}else{
            		return ((Comparable<Object>)o1).compareTo(o2);
            	}
            } else {
            	return o1.toString().compareTo(o2.toString());
            }
        }
    }

	public static class  ColumnHeaderToolTips extends MouseMotionAdapter {
        // Current column whose tooltip is being displayed.
        // This variable is used to minimize the calls to setToolTipText().
        TableColumn curCol;

        // Maps TableColumn objects to tooltips
        Map<TableColumn,String> tips = new HashMap<TableColumn,String>();

        // If tooltip is null, removes any tooltip text.
        public void setToolTip(TableColumn col, String tooltip) {
            if (tooltip == null) {
                tips.remove(col);
            } else {
                tips.put(col, tooltip);
            }
        }

        public void mouseMoved(MouseEvent evt) {
            TableColumn col = null;
            JTableHeader header = (JTableHeader)evt.getSource();
            JTable table = header.getTable();
            TableColumnModel colModel = table.getColumnModel();
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());

            // Return if not clicked on any column header
            if (vColIndex >= 0) {
                col = colModel.getColumn(vColIndex);
            }

            if (col != curCol) {
                header.setToolTipText(tips.get(col));
                curCol = col;
            }
        }
    }

	/**
	 * Returns the table's model.
	 * @return the model
	 */
	public CheckListTableModel getModel() {
		return model;
	}



	public boolean areRowModified(int[] rows) {
		boolean modified=false;
		int i=0;
		while ((!modified)&&(i<rows.length)) {
			modified = isRowModified(i);
			i++;
		}
		return modified;
	}

	public abstract boolean isRowModified(int row);

	/**
	 * Confirm the verdict of selected rows
	 */
	public void confirmVerdict() {
		int [] selectedRows = table.getSelectedRows();
		for (int i=0; i<selectedRows.length;i++){
			int numRow = selectedRows[i];
			String rowNumber = (String)model.getValueAt(numRow, COLUMN_NBROW);
			int numInCampaign = new Integer(rowNumber).intValue()-1;
			Step cmdLine = campaign.get(numInCampaign);
			if (cmdLine.getVerdict() != Verdict.NONE){
				cmdLine.setUserVerdict(cmdLine.getVerdict());
				model.setValueAt(Step.verdictAsString.get(cmdLine.getVerdict()), numRow, getNumColumnVerdict());
				//TODO DB
				/*if (CoreGUI.connectedToDB){
					try {
						CoreGUI.configuration.getArchivingDB().updateJavaVerdict(cmdLine.idBDD, Step.verdicts[cmdLine.verdict], Step.verdicts[cmdLine.userVerdict]);
					} catch (Alert e) {
						Out.log.println("An error occurs during database update : " + e.getMessage() );
						e.printStackTrace(Out.log);
					}
				}*/
				CoreGUIPlugin.mainFrame.setModified(true);
			}
		}
	}

	/**
	 * Give the number of the column which contains verdict
	 * @return
	 */
	public abstract int getNumColumnVerdict();

	/**
	 * Modify verdict of selected rows
	 */
	public void modifyVerdict() {
		int [] selectedRows = table.getSelectedRows();
		Object [] possibilities = {Step.verdictAsString.get(Verdict.PASSED),
									Step.verdictAsString.get(Verdict.FAILED),
									Step.verdictAsString.get(Verdict.SKIPPED)};
		String s = null;
		if (selectedRows.length == 1) {
			s = (String)JOptionPane.showInputDialog(CoreGUIPlugin.mainFrame, "New verdict: ", "Modify the verdict",
					JOptionPane.PLAIN_MESSAGE,
					null,
					possibilities,
					model.getValueAt(table.getSelectedRow(), getNumColumnVerdict())
					);
		} else {
			s = (String)JOptionPane.showInputDialog(CoreGUIPlugin.mainFrame, "New verdict: ", "Modify the verdict",
					JOptionPane.PLAIN_MESSAGE,
					null,
					possibilities,
					Step.verdictAsString.get(Verdict.PASSED)
					);
		}
		for (int i=0; i<selectedRows.length; i++) {
			int numRow = selectedRows[i];
			String rowNumber = (String)model.getValueAt(numRow, COLUMN_NBROW);
			int numInCampaign = new Integer(rowNumber).intValue()-1;
			Step step = campaign.get(numInCampaign);
			if ((s!=null) && (s.length()>0)) {
				if (step.getVerdict() != Verdict.NONE) {
					model.setValueAt(s, numRow, getNumColumnVerdict());
					if (s.equals(Step.verdictAsString.get(Verdict.PASSED))) {
						step.setUserVerdict(Verdict.PASSED);
					} else if (s.equals(Step.verdictAsString.get(Verdict.FAILED))) {
						step.setUserVerdict(Verdict.FAILED);
					} else {
						step.setUserVerdict(Verdict.SKIPPED);
					}
					// TODO DB
					/*if (CoreGUI.connectedToDB){
						try {
							CoreGUI.configuration.getArchivingDB().updateFlashVerdict(step.idBDD, Step.verdicts[step.verdict], Step.verdicts[step.userVerdict]);
						} catch (Alert e) {
							Out.log.println("An error occurs during database update : " + e.getMessage() );
							e.printStackTrace(Out.log);
						}
					}*/
					CoreGUIPlugin.mainFrame.setModified(true);
				}
			}
		}
	}

	public static Vector<String> readListPhoneConfig() {
		File folder = new File(Configuration.getMonitoringConfigDir());
		if(!folder.exists()){
			if(!folder.mkdir())
				Logger.getLogger("CheckListTable").debug("Didn't manage to create the folder");
			return null;
		}

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		};
		String[] listFiles = folder.list(filter);
		
		folder = new File(Configuration.getMonitoringConfigDir());
		Logger.getLogger("CheckListTable").debug("defaultPhoneConfigPath="+folder.getAbsolutePath());
		String[] defaultFiles = folder.list(filter);
		
		Vector<String> resultFiles = new Vector<String>();
		for (int i=0; i<defaultFiles.length; i++) 
			resultFiles.add(defaultFiles[i]);
		
		if(listFiles != null){
			for (int i=0; i<listFiles.length; i++) {
				if (!resultFiles.contains(listFiles[i])) resultFiles.add(listFiles[i]);
			}
		} 
		Collections.sort(resultFiles,String.CASE_INSENSITIVE_ORDER);
		return resultFiles;
	}
	
	/**
	 * Loads external tools definitions and updates popup menu
	 */

	public abstract void enableUserActions(boolean b);

	public abstract boolean isEnableUserActions();

	public void updateHopperTestParam() {
		// TODO Auto-generated method stub
		
	}

	public JComboBox getComboBoxPhoneConfig() {
		return comboBoxPhoneConfig;
	}

}
