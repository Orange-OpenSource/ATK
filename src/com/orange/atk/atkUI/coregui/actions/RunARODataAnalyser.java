package com.orange.atk.atkUI.coregui.actions;

import com.orange.atk.atkUI.corecli.Configuration;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class RunARODataAnalyser extends MatosAbstractAction {

	private static final String AROPATH = Configuration.getProperty(Configuration.AROPATH);
	private static final String LAUNCHARO = AROPATH + "\\bin\\aro.exe";

	public RunARODataAnalyser(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			Logger.getLogger(getClass()).info(LAUNCHARO + " from " + AROPATH);
            ProcessBuilder pb = new ProcessBuilder(LAUNCHARO);
            pb.directory(new File(AROPATH + "\\bin"));
            Process p = pb.start();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error("Unable to start ARODataAnalyzer", e);
		}  catch(Exception e) {
            Logger.getLogger(this.getClass()).error("Unable to start ARODataAnalyzer", e);
        }
	}

}
