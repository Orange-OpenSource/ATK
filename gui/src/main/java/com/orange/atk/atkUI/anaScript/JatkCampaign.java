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
 * File Name   : JatkCampaign.java
 *
 * Created     : 23/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaScript;

import java.io.File;

import javax.swing.JOptionPane;

import org.dom4j.Element;

import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.utils.XMLParser;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
@SuppressWarnings("serial")
public class JatkCampaign extends Campaign {

	public Campaign readCampaign(XMLParser parser) {
		Campaign camp = new JatkCampaign();
		Element root = parser.getRoot();
		Element[] flashStepList = parser.getElements(root, JatkStep.TYPE);
		for (int i=0; i<flashStepList.length; i++) {
			String flashFilePath = flashStepList[i].attributeValue("file");

			JatkStep step = new JatkStep(flashFilePath, new File(flashFilePath));
			String stepName = flashStepList[i].attributeValue("name");

			if(step.readfromelement(flashStepList[i],step,flashFilePath)){

				// Output file : re-uses the 'name' attribute, after
				// testing its validity
				initOutputFile(step, stepName, i);

				// add to campaign
				camp.add(step);
			}
			else{
				JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame,
						"The test file "+flashFilePath+" does not exist.\nThe test will not be added to the list.");
			}
		}
		return camp;
	}

}
