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
 * File Name   : MixScriptGUI.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiMixScript;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretListener;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.anaMixScript.MixScriptStep;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.AnalysisGUICommon;
import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.actions.MatosAction;
import com.orange.atk.atkUI.guiMixScript.actions.MixScriptGUIAction;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class MixScriptGUI extends AnalysisGUICommon {

	public MixScriptGUI() {

		// populate a flash toolBar with flash specifics feature
		addInToolbar(MixScriptGUIAction.ADDFLASHANIMATION.getAsJButton() );
		addInToolbar(new JToolBar.Separator());
		addInToolbar(MatosAction.VIEWREPORT.getAsJButton() );
		addInToolbar(new JToolBar.Separator());
		addInToolbar(MixScriptGUIAction.ANALYSEALLFLASH.getAsJButton());
		addInToolbar(MixScriptGUIAction.ANALYSESELECTIONFLASH.getAsJButton());
		addInToolbar(MixScriptGUIAction.STOPSCRIPT.getAsJButton());
		addInToolbar(new JToolBar.Separator());
		addInToolbar(MixScriptGUIAction.LOOP.getAsJButton());

		final JTextField loop = new JTextField("1");
		CaretListener caretupdate = new CaretListener() {
			public void caretUpdate(javax.swing.event.CaretEvent e) {
				JTextField text = (JTextField)e.getSource();
				String loopnumber=text.getText();

				try {
					Integer.parseInt(loopnumber);
					Campaign.setLoop(Integer.parseInt(loopnumber));
					Logger.getLogger(this.getClass() ).debug("loop is now "+loopnumber);
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					Logger.getLogger(this.getClass() ).debug("you should enter an integer");
				}

			}
		};

		loop.addCaretListener(caretupdate);

		loopLabel = new JLabel("Loop:");
		addInToolbar(loopLabel);
		addInToolbar(loop);

		toolBar.setFloatable(false);
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#buildCampaignFromDirectory(java.io.File)
	 */
	@Override
	public Campaign buildCampaignFromDirectory(File dir) {
		Campaign campaign = new Campaign();
		// Jad files

		FilenameFilter swfFilter = new FilenameFilter(){

			public boolean accept(File dir, String name){
				if(AutomaticPhoneDetection.getInstance()
						.isNokia() )

					return name.endsWith(".xml");
				else
					return name.endsWith(".tst");

			}
		};
		String [] swfFiles = dir.list(swfFilter);


		if (swfFiles != null) {
			for (int i=0; i<swfFiles.length; i++) {
				String swfName = swfFiles[i];
				File swfFile = new File(dir.getAbsolutePath()+File.separator+swfName);
				Step step = new MixScriptStep(swfFile.getAbsolutePath(), swfFile );
				campaign.add(step);
			}
		}

		return campaign;
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#editSelectedStepProperties()
	 */
	@Override
	public void editSelectedStepProperties() {
		int indexRow = getCheckListTable().getTable().getSelectedRow();
		int numCampaign = getCheckListTable().getNumCampaign(indexRow);
		MixScriptStep flashStep = (MixScriptStep)(getCheckListTable().getCampaign().get(numCampaign));

		new MixScriptPropertiesDialog(flashStep);
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getAddStepMenuItem()
	 */
	@Override
	public JMenuItem getAddStepMenuItem() {
		return MixScriptGUIAction.ADDFLASHANIMATION.getAsMenuItem("Add Script in Mix Mode...");
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getAnalyseAllMenuItem()
	 */
	@Override
	public JMenuItem getAnalyseAllMenuItem() {
		return MixScriptGUIAction.ANALYSEALLFLASH.getAsMenuItem("MixScript");
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getAnalyseSelectionMenuItem()
	 */
	@Override
	public JMenuItem getAnalyseSelectionMenuItem() {
		return MixScriptGUIAction.ANALYSESELECTIONFLASH.getAsMenuItem("MixScript");
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getCheckListTable()
	 */
	@Override
	public CheckListTable getCheckListTable() {
		if (checkListTable == null) {
			checkListTable = new MixScriptCheckListTable();
			checkListTable./*getPanel().*/setBorder( new EmptyBorder(0,0,0,0));
		}
		return checkListTable;
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getDisplayName()
	 */
	public String getDisplayName() {
		return "MixScript Test";
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.AnalysisGUICommon#getDisplayName()
	 */
	public String getConfigPanelName() {
		return getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.IGUICommon#notifySelected()
	 */
	public void notifySelected() {
		updateButtons();
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.IGUICommon#updateButtons()
	 */
	public void updateButtons() {
		if (checkListTable!=null) {
			boolean hasRow = (checkListTable.getStepNumber()>0);
			MixScriptGUIAction.ANALYSEALLFLASH.setEnabled(hasRow);
			boolean isRowSelected = (getCheckListTable().getSelectedRowCount()>0);
			MixScriptGUIAction.ANALYSESELECTIONFLASH.setEnabled(isRowSelected);

			if (getCheckListTable().getSelectedRowCount()==1) {
				Step step = getCheckListTable().getSelectedStep();
				// REPORT
				String repPath = step.getOutFilePath();
				if ((repPath==null)||(repPath.trim().length()==0)) {
					MatosAction.VIEWREPORT.setEnabled(false);
				} else {
					MatosAction.VIEWREPORT.setEnabled(true);
				}
			} else {
				MatosAction.VIEWREPORT.setEnabled(isRowSelected);
			}
		}
		MixScriptGUIAction.STOPSCRIPT.setEnabled(false);

	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.coregui.IGUICommon#enableUserActions(boolean)
	 */
	public void enableUserActions(boolean b) {
		super.enableUserActions(b); // checklisttable
		for (MixScriptGUIAction fa : MixScriptGUIAction.values()) {
			fa.setEnabled(b);
		}

	}

	public void disableButtonsButStop() {
		enableUserActions(true);
		MixScriptGUIAction.ADDFLASHANIMATION.setEnabled(false);
		MixScriptGUIAction.ANALYSEALLFLASH .setEnabled(false);
		MixScriptGUIAction.ANALYSESELECTIONFLASH.setEnabled(false);
		MixScriptGUIAction.STOPSCRIPT.setEnabled(true);
	}
	

}
