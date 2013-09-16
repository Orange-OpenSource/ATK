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
 * File Name   : SoloMethods.java
 *
 * Created     : 05/06/2013
 * Author(s)   : D'ALMEIDA Joana
 */

package com.orange.atk.interpreter.ast;

import java.util.ArrayList;
import java.util.List;

public class SoloMethods {


	private  List<String[]> soloMethods;

	public SoloMethods() {
		soloMethods =  new ArrayList<String[]>();

		soloMethods.add( new String[]{"ExitSolo"} );
		soloMethods.add( new String[]{"AssertCurrentActivity","STRING","STRING","STRING"} );
		soloMethods.add( new String[]{"AssertCurrentActivity","STRING","STRING","STRING","BOOLEAN"} );
		soloMethods.add( new String[]{"AssertMemoryNotLow"} );
		soloMethods.add( new String[]{"ClearEditText","INTEGER"} );
		soloMethods.add( new String[]{"ClickInList","INTEGER"} );
		soloMethods.add( new String[]{"ClickInList","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"ClickLongInList","INTEGER"} );
		soloMethods.add( new String[]{"ClickLongInList","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"ClickLongInList","INTEGER","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"ClickLongOnScreen","FLOAT","FLOAT"} );
		soloMethods.add( new String[]{"ClickLongOnScreen","FLOAT","FLOAT","INTEGER"} );
		soloMethods.add( new String[]{"ClickLongOnText","STRING","INTEGER"} );
		soloMethods.add( new String[]{"ClickLongOnText","STRING","INTEGER","STRING"} );
		soloMethods.add( new String[]{"ClickLongOnText","STRING","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"ClickLongOnTextAndPress","STRING","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnActionBarHomeButtom"} );
		soloMethods.add( new String[]{"ClickOnActionBarItem","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnButton","STRING","STRING"} );
		soloMethods.add( new String[]{"ClickOnButton","STRING","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnCheckBox","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnEditText","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnImage","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnImageButton","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnMenuItem","STRING"} );
		soloMethods.add( new String[]{"ClickOnMenuItem","STRING","STRING"} );
		soloMethods.add( new String[]{"ClickOnRadioButton","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnScreen","FLOAT","FLOAT"} );
		soloMethods.add( new String[]{"ClickOnText","STRING"} );
		soloMethods.add( new String[]{"ClickOnText","STRING","INTEGER"} );
		soloMethods.add( new String[]{"ClickOnText","STRING","INTEGER","STRING"} );
		soloMethods.add( new String[]{"ClickOnToggleButton","STRING"} );
		soloMethods.add( new String[]{"Drag","FLOAT","FLOAT","FLOAT","FLOAT","INTEGER"} );
		soloMethods.add( new String[]{"EnterText","INTEGER","STRING"} );
		soloMethods.add( new String[]{"Finalize"} );
		soloMethods.add( new String[]{"FinishOpenedActivities"} );
		soloMethods.add( new String[]{"GetActivityMonitor"} );
		soloMethods.add( new String[]{"GetButton","INTEGER"} );
		soloMethods.add( new String[]{"GetButton","STRING"} );
		soloMethods.add( new String[]{"GetButton","STRING","STRING"} );
		soloMethods.add( new String[]{"GetCurrentActivity"} );
		soloMethods.add( new String[]{"GetCurrentViews"} );
		soloMethods.add( new String[]{"GetCurrentViews","STRING"} );
		soloMethods.add( new String[]{"GetEditText","INTEGER"} );
		soloMethods.add( new String[]{"GetEditText","STRING"} );
		soloMethods.add( new String[]{"GetEditText","STRING","STRING"} );
		soloMethods.add( new String[]{"GetImage","INTEGER"} );
		soloMethods.add( new String[]{"GetImageButton","INTEGER"} );
		soloMethods.add( new String[]{"GetString","INTEGER"} );
		soloMethods.add( new String[]{"GetText","INTEGER"} );
		soloMethods.add( new String[]{"GetText","STRING"} );
		soloMethods.add( new String[]{"GetText","STRING","STRING"} );
		soloMethods.add( new String[]{"GetTopParent","STRING"} );
		soloMethods.add( new String[]{"GetViews","STRING","INTEGER"} );
		soloMethods.add( new String[]{"GetViews","INTEGER"} );
		soloMethods.add( new String[]{"GetViews"} );
		soloMethods.add( new String[]{"GetViews","STRING"} );
		soloMethods.add( new String[]{"GoBack"} );
		soloMethods.add( new String[]{"GoBackToActivity","STRING"} );
		soloMethods.add( new String[]{"IsCheckBoxChecked","INTEGER"} );
		soloMethods.add( new String[]{"IsCheckBoxChecked","STRING"} );
		soloMethods.add( new String[]{"IsRadioButtonChecked","INTEGER"} );
		soloMethods.add( new String[]{"IsRadioButtonChecked","STRING"} );
		soloMethods.add( new String[]{"IsSpinnerTextSelected","INTEGER","STRING"} );
		soloMethods.add( new String[]{"IsSpinnerTextSelected","STRING"} );
		soloMethods.add( new String[]{"IsTextChecked","STRING"} );
		soloMethods.add( new String[]{"IstoggleButtonChecked","INTEGER"} );
		soloMethods.add( new String[]{"IstoggleButtonChecked","STRING"} );
		soloMethods.add( new String[]{"PressMenuItem","INTEGER"} );
		soloMethods.add( new String[]{"PressMenuItem","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"PressSpinnerItem","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"ScrollDown"} );
		soloMethods.add( new String[]{"ScrollDownList","INTEGER"} );
		soloMethods.add( new String[]{"ScrollListToBottom","INTEGER"} );
		soloMethods.add( new String[]{"ScrollListToLine","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"ScrollListToTop","INTEGER"} );
		soloMethods.add( new String[]{"ScrollToBottom"} );
		soloMethods.add( new String[]{"ScrollToSide","INTEGER"} );
		soloMethods.add( new String[]{"ScrollToTop"} );
		soloMethods.add( new String[]{"ScrollToUp"} );
		soloMethods.add( new String[]{"ScrollUpList","INTEGER"} );
		soloMethods.add( new String[]{"SearchButton","STRING"} );
		soloMethods.add( new String[]{"SearchButton","STRING","STRING"} );
		soloMethods.add( new String[]{"SearchButton","STRING","INTEGER"} );
		soloMethods.add( new String[]{"SearchButton","STRING","INTEGER","STRING"} );
		soloMethods.add( new String[]{"SearchEditText","STRING"} );
		soloMethods.add( new String[]{"SearchText","STRING"} );
		soloMethods.add( new String[]{"SearchText","STRING","STRING"} );
		soloMethods.add( new String[]{"SearchText","STRING","INTEGER"} );
		soloMethods.add( new String[]{"SearchText","STRING","INTEGER","STRING"} );
		soloMethods.add( new String[]{"SearchText","STRING","INTEGER","STRING","STRING"} );
		soloMethods.add( new String[]{"SearchToggleButton","STRING"} );
		soloMethods.add( new String[]{"SearchToggleButton","STRING","INTEGER"} );
		soloMethods.add( new String[]{"SendKey","INTEGER"} );
		soloMethods.add( new String[]{"SetActivityOrientation","INTEGER"} );
		soloMethods.add( new String[]{"SetDataPicker","INTEGER","INTEGER","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"SetProgressBar","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"SetSlidingDrawer","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"SetTimePicker","INTEGER","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"SleepSolo","INTEGER"} );
		soloMethods.add( new String[]{"TakeScreenshot"} );
		soloMethods.add( new String[]{"TakeScreenshot","STRING"} );
		soloMethods.add( new String[]{"TakeScreenshot","STRING","INTEGER"} );
		soloMethods.add( new String[]{"TypeText","INTEGER","STRING"} );
		soloMethods.add( new String[]{"WaitForActivity","STRING"} );
		soloMethods.add( new String[]{"WaitForActivity","STRING","INTEGER"} );
		soloMethods.add( new String[]{"WaitForDialogToClose","LONG"} );
		soloMethods.add( new String[]{"WaitForDialogToOpen","LONG"} );
		soloMethods.add( new String[]{"WaitForLogMessage","STRING"} );
		soloMethods.add( new String[]{"WaitForLogMessage","STRING","INTEGER"} );
		soloMethods.add( new String[]{"WaitForText","STRING"} );
		soloMethods.add( new String[]{"WaitForText","STRING","INTEGER","LONG"} );
		soloMethods.add( new String[]{"WaitForText","STRING","INTEGER","LONG","STRING"} );
		soloMethods.add( new String[]{"WaitForText","STRING","INTEGER","LONG","STRING","STRING"} );
		soloMethods.add( new String[]{"WaitForView","STRING","INTEGER","INTEGER"} );
		soloMethods.add( new String[]{"WaitForView","STRING","INTEGER","INTEGER","STRING"} );
		soloMethods.add( new String[]{"WaitForView","STRING","INTEGER","STRING"} );
		soloMethods.add( new String[]{"StartRobotiumTestOn","STRING","STRING","STRING","INTEGER"} );
	}


	public  boolean isSoloMethod(String Methodname) {
		for(String[] signature : soloMethods) {
			if( signature[0].toLowerCase().equals(Methodname.toLowerCase()) ) {
				return true;
			}
		} 
		return false;
	}



}
