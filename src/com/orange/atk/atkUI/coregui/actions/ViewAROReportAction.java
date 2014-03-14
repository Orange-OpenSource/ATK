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
 * File Name   : RunARODataAnalyser.java
 *
 * Created     : 19/07/13
 * Author(s)   : pcrepieux
 */
package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.coregui.AROLauncher;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.MatosGUI;
import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Configuration;

public class ViewAROReportAction extends MatosAbstractAction {
    private static final String AROPATH = Configuration.getProperty(Configuration.AROPATH);
    private static final String LAUNCHARO = AROPATH + "\\bin\\aro.exe";

    public ViewAROReportAction(String name, Icon icon, String shortDescription) {
        super(name, icon, shortDescription);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        MatosGUI matosGui = CoreGUIPlugin.mainFrame;

        Step sel = matosGui.getSelectedAnalysisPane().getCheckListTable().getSelectedStep();
        if (sel != null) {
            Logger.getLogger(getClass()).info(LAUNCHARO + " from " + AROPATH);
            String aroDataPath = sel.getOutFilePath();
            Logger.getLogger(getClass()).info("looking for a report in " +aroDataPath+File.separator+"ARO" );
            if ((aroDataPath != null) && (!aroDataPath.trim().equals(""))) {
                aroDataPath=aroDataPath+File.separator+"ARO";
                if(new File(aroDataPath).exists()){
                    AROLauncher.start(aroDataPath);
                }else{
                    JOptionPane.showMessageDialog(matosGui, "No report available", "View report",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(matosGui, "No report available", "View report",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
