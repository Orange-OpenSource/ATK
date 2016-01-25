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
 * File Name   : StatisticDialog.java
 *
 * Created     : 30/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.jfree.chart.ChartPanel;

import com.orange.atk.atkUI.corecli.Campaign;


/**
 * Dialog to show a pie chart with percentage of steps in the current check-list
 * whose verdict are passed, failed, skipped or without verdict (not analysed).
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class StatisticDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates the dialog.
	 * @param campaign
	 */
	public StatisticDialog(Campaign campaign) {
		super(CoreGUIPlugin.mainFrame, true);
		StatisticTool statisticTool = new StatisticTool(campaign);
		ChartPanel chartpanel = new ChartPanel(statisticTool.getJfreechart());
        chartpanel.setPreferredSize(new Dimension(550, 270));
        getContentPane().add(chartpanel);
        setLocationRelativeTo(CoreGUIPlugin.mainFrame);
		this.setTitle("Statistics");
		this.pack();

		int dec_x = (CoreGUIPlugin.mainFrame.getWidth()-this.getWidth())/2;
		int dec_y = (CoreGUIPlugin.mainFrame.getHeight()-this.getHeight())/2;
		this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+dec_x,
						 CoreGUIPlugin.mainFrame.getLocationY()+dec_y);

		this.setVisible(true);
	}

}
