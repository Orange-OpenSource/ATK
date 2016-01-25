package com.orange.atk.atkUI.coregui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.orange.atk.atkUI.coregui.BenchmarkDialog;


public class BenchmarkAction extends MatosAbstractAction {

	
	
	public BenchmarkAction(String name, Icon icon, String shortDescription) {
		super(name, icon, shortDescription);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {	
		new BenchmarkDialog();
	}

}
