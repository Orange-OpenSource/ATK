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
 * File Name   : HopperGUI.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper;

import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;

import com.orange.atk.atkUI.anaHopper.HopperStep;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.AnalysisGUICommon;
import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.actions.MatosAbstractAction;
import com.orange.atk.atkUI.coregui.actions.MatosAction;
import com.orange.atk.atkUI.guiHopper.actions.HopperGUIAction;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HopperGUI extends AnalysisGUICommon {

	public HopperGUI() {

		// populate a flash toolBar with flash specifics feature
		// addInToolbar(HopperGUIAction.ADDFLASHANIMATION.getAsJButton());
		// addInToolbar(new JToolBar.Separator());
		// addInToolbar(MatosAction.VIEWREPORT.getAsJButton());
		// addInToolbar(new JToolBar.Separator());
		// addInToolbar(HopperGUIAction.ANALYSEALLFLASH.getAsJButton());
		// addInToolbar(HopperGUIAction.ANALYSESELECTIONFLASH.getAsJButton());
		// addInToolbar(HopperGUIAction.STOPSCRIPT.getAsJButton());
		// addInToolbar(HopperGUIAction.CHECCNX.getAsJButton());
		// addInToolbar(HopperGUIAction.STOPTEST.getAsJButton());

		// toolBar.setFloatable(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.AnalysisGUICommon#buildCampaignFromDirectory
	 * (java.io.File)
	 */
	@Override
	public Campaign buildCampaignFromDirectory(File dir) {
		Campaign campaign = new Campaign();
		// Jad files
		/*
		 * FilenameFilter swfFilter = new FilenameFilter(){ public boolean
		 * accept(File dir, String name){ String
		 * profile=AutomaticPhoneDetection.getDeviceType();
		 * if(profile!=null&&profile.equals("NokiaS60"))
		 * 
		 * return name.endsWith(".xml"); else return name.endsWith(".tst");
		 * 
		 * } };
		 * 
		 * String [] swfFiles = dir.list(swfFilter);
		 * 
		 * swfFiles=null; if (swfFiles != null) { for (int i=0;
		 * i<swfFiles.length; i++) { String swfName = swfFiles[i]; File swfFile
		 * = new File(dir.getAbsolutePath()+File.separator+swfName); Step step =
		 * new HopperStep(swfFile.getAbsolutePath(), getSelectedProfileName());
		 * campaign.add(step); } }
		 */
		return campaign;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.AnalysisGUICommon#editSelectedStepProperties
	 * ()
	 */
	@Override
	public void editSelectedStepProperties() {
		int indexRow = getCheckListTable().getTable().getSelectedRow();
		int numCampaign = getCheckListTable().getNumCampaign(indexRow);
		HopperStep hopperStep = (HopperStep) (getCheckListTable().getCampaign().get(numCampaign));

		new HopperPropertiesDialog(hopperStep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getAddStepMenuItem()
	 */
	@Override
	public JMenuItem getAddStepMenuItem() {
		return HopperGUIAction.ADDFLASHANIMATION.getAsMenuItem("Add Random Test ...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.AnalysisGUICommon#getAnalyseAllMenuItem()
	 */
	@Override
	public JMenuItem getAnalyseAllMenuItem() {
		return HopperGUIAction.ANALYSEALLFLASH.getAsMenuItem("Random Test");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.AnalysisGUICommon#getAnalyseSelectionMenuItem
	 * ()
	 */
	@Override
	public JMenuItem getAnalyseSelectionMenuItem() {
		return HopperGUIAction.ANALYSESELECTIONFLASH.getAsMenuItem("Random Test");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getCheckListTable()
	 */
	@Override
	public CheckListTable getCheckListTable() {
		if (checkListTable == null) {
			checkListTable = new HopperCheckListTable();
			checkListTable./* getPanel(). */setBorder(new EmptyBorder(0, 0, 0, 0));
		}
		return checkListTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getDisplayName()
	 */
	public String getDisplayName() {
		return "Random Test";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getDisplayName()
	 */
	public String getConfigPanelName() {
		return getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.IGUICommon#notifySelected()
	 */
	public void notifySelected() {
		updateButtons();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.IGUICommon#updateButtons()
	 */
	public void updateButtons() {
		if (checkListTable != null) {
			boolean hasRow = (checkListTable.getStepNumber() > 0);
			HopperGUIAction.ANALYSEALLFLASH.setEnabled(hasRow);
			boolean isRowSelected = (getCheckListTable().getSelectedRowCount() > 0);
			HopperGUIAction.ANALYSESELECTIONFLASH.setEnabled(isRowSelected);

			if (getCheckListTable().getSelectedRowCount() == 1) {
				Step step = getCheckListTable().getSelectedStep();
				// REPORT
				String repPath = step.getOutFilePath();
				if ((repPath == null) || (repPath.trim().length() == 0)) {
					MatosAction.VIEWREPORT.setEnabled(false);
				} else {
					MatosAction.VIEWREPORT.setEnabled(true);
				}
			} else {
				MatosAction.VIEWREPORT.setEnabled(isRowSelected);
			}
		}
		HopperGUIAction.STOPSCRIPT.setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.IGUICommon#enableUserActions(boolean)
	 */
	public void enableUserActions(boolean b) {
		super.enableUserActions(b); // profile && checklisttable
		for (HopperGUIAction fa : HopperGUIAction.values()) {
			fa.setEnabled(b);
		}

	}

	public void disableButtonsButStop() {
		enableUserActions(true);
		HopperGUIAction.ADDFLASHANIMATION.setEnabled(false);
		HopperGUIAction.ANALYSEALLFLASH.setEnabled(false);
		HopperGUIAction.ANALYSESELECTIONFLASH.setEnabled(false);
		HopperGUIAction.STOPSCRIPT.setEnabled(true);
	}

	@Override
	public MatosAbstractAction getAddScriptAction() {
		return HopperGUIAction.ADDFLASHANIMATION.getAction();
	}

}
