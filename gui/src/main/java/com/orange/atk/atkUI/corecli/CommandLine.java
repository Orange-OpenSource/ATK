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
 * File Name   : CommandLine.java
 *
 * Created     : 16/02/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli;


/**
 * CommandLine groups command line options.
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class CommandLine {
	
	/** The different possible user interfaces */
	public enum Modes { CLI, GUI}
	
	/** The mode to run the tool. Default is CLI */
	private Modes mode = Modes.CLI;
	
	/** Optional campaign */
	private Campaign campaign = new Campaign(); 
	
	/**
	 * Retrieves the campaign.
	 * @return the campaign
	 */
	public Campaign getCampaign() {
		return campaign;
	}
	
	/**
	 * Returns the user interface mode defined.
	 * @return the user interface mode
	 */
	public Modes getMode() {
		return mode;
	}

	/**
	 * Define the user interface mode
	 * @param mode the user interface mode to set
	 */
	public void setMode(Modes mode) {
		this.mode = mode;
	}
	
	/**
	 * Combines the given parameters with parameters of this commandline
	 * @param cl the new commanline to take into account
	 */
	public void agregate(CommandLine cl) {
		campaign.addAll(cl.campaign);
		if (cl.getMode()==Modes.CLI) { // CLI is stronger than GUI
			mode=Modes.CLI;
		}
	}

	/**
	 * Add a step
	 * @param stepToAdd
	 * @return true if the step has been added.
	 */
	public boolean addStep(Step stepToAdd) {
		return campaign.add(stepToAdd);
	}

}
