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
 * File Name   : FunctionDictionnary.java
 *
 * Created     : 27/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.ast;

import java.util.ArrayList;
import java.util.List;


/**
 * This class contains all function signatures recognized by the interpreter.
 * 
 * There are commonly used in syntax Verification or Auto-completion.
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 *The function signature are represented by a String[].
 *String[0] is name of the function
 *String[1] ..String[n] are the parameters of the functions
 */
public class FunctionDictionnary {

	private List<String[]> functionDictionnary;

	public FunctionDictionnary() {
		functionDictionnary = new ArrayList<String[]>();


		functionDictionnary.add( new String[]{"Beep"} );

		functionDictionnary.add( new String[]{"DisableUSBCharge"} );
		functionDictionnary.add( new String[]{"Include","STRING"} );

		// TODO implement these features in at least one phone
		// WinMobile implement it, so uncomment when tested and update documentation
		//functionDictionnary.add( new String[]{"FreeStorage"} );
		//functionDictionnary.add( new String[]{"FillStorage", "INTEGER"} );

		functionDictionnary.add( new String[]{"Key","STRING"} );
		functionDictionnary.add( new String[]{"Key","STRING", "INTEGER"} );
		functionDictionnary.add( new String[]{"Key","STRING", "INTEGER", "INTEGER"} );

		functionDictionnary.add( new String[]{"KeyDown","STRING"} );
		functionDictionnary.add( new String[]{"KeyUp","STRING"} );

		functionDictionnary.add( new String[]{"Kill","STRING"} );
		functionDictionnary.add( new String[]{"Log","STRING"} );

		functionDictionnary.add( new String[]{"Run","STRING"} );
		functionDictionnary.add( new String[]{"Screenshot"} );
		functionDictionnary.add( new String[]{"Screenshot","STRING"} );

		// TODO implement this feature in at least one phone
		//functionDictionnary.add( new String[]{"SendEmail","STRING","STRING","STRING","STRING","STRING","STRING"} );

		functionDictionnary.add( new String[]{"SendSMS","STRING","STRING"} );
		functionDictionnary.add( new String[]{"SetFlightMode","STRING"} );
		functionDictionnary.add( new String[]{"SetVar","STRING","STRING"} );
		functionDictionnary.add( new String[]{"SetVar","STRING","INTEGER"} );

		// TODO implement these features in at least one phone
		//functionDictionnary.add( new String[]{"SetOrientation","INTEGER"} );	
		//functionDictionnary.add( new String[]{"Reset"} ); // reset phone to homescreen ? not clear
		//functionDictionnary.add( new String[]{"Sign", "INTEGER"} ); // Actually, check application signature

		functionDictionnary.add( new String[]{"Sleep","INTEGER"} );


		functionDictionnary.add( new String[]{"StartMainLog"} );
		functionDictionnary.add( new String[]{"StartMainLog","INTEGER"} );

		functionDictionnary.add( new String[]{"StopMainLog"} );

		functionDictionnary.add( new String[]{"TouchScreenDragnDrop","TABLE"} );
		functionDictionnary.add( new String[]{"TouchScreenPress","INTEGER","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"TouchScreenSlide","TABLE"} );

		// TODO implement these features in at least one phone
		//functionDictionnary.add( new String[]{"UseCPU","INTEGER"} );
		//functionDictionnary.add( new String[]{"WaitWindow"} );
		//functionDictionnary.add( new String[]{"WaitWindow","STRING","INTEGER"} );

		//TODO implement these features in at least one phone like solo
		functionDictionnary.add( new String[]{"ExitSolo"} );
		functionDictionnary.add( new String[]{"AssertCurrentActivity","STRING","STRING","STRING"} );
		functionDictionnary.add( new String[]{"AssertCurrentActivity","STRING","STRING","STRING","BOOLEAN"} );
		functionDictionnary.add( new String[]{"AssertMemoryNotLow"} );
		functionDictionnary.add( new String[]{"ClearEditText","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickInList","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickInList","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickLongInList","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickLongInList","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickLongInList","INTEGER","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickLongOnScreen","FLOAT","FLOAT"} );
		functionDictionnary.add( new String[]{"ClickLongOnScreen","FLOAT","FLOAT","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickLongOnText","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickLongOnText","STRING","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"ClickLongOnText","STRING","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickLongOnTextAndPress","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickOnActionBarHomeButtom"} );
		functionDictionnary.add( new String[]{"ClickOnActionBarItem","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickOnButton","STRING"});
		functionDictionnary.add( new String[]{"ClickOnButton","INTEGER"});
		functionDictionnary.add( new String[]{"ClickOnCheckBox","INTEGER"});
		functionDictionnary.add( new String[]{"ClickOnEditText","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickOnImage","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickOnImageButton","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickOnMenuItem","STRING"} );
		functionDictionnary.add( new String[]{"ClickOnMenuItem","STRING","STRING"} );
		functionDictionnary.add( new String[]{"ClickOnRadioButton","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickOnScreen","FLOAT","FLOAT"} );
		functionDictionnary.add( new String[]{"ClickOnText","STRING"} );
		functionDictionnary.add( new String[]{"ClickOnText","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"ClickOnText","STRING","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"ClickOnToggleButton","STRING"} );
		functionDictionnary.add( new String[]{"Drag","FLOAT","FLOAT","FLOAT","FLOAT","INTEGER"} );
		functionDictionnary.add( new String[]{"EnterText","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"Finalize"} );
		functionDictionnary.add( new String[]{"FinishOpenedActivities"} );
		functionDictionnary.add( new String[]{"GetActivityMonitor"} );
		functionDictionnary.add( new String[]{"GetButton","INTEGER"} );
		functionDictionnary.add( new String[]{"GetButton","STRING"} );
		functionDictionnary.add( new String[]{"GetButton","STRING","STRING"} );
		functionDictionnary.add( new String[]{"GetCurrentActivity"} );
		functionDictionnary.add( new String[]{"GetCurrentViews"} );
		functionDictionnary.add( new String[]{"GetCurrentViews","STRING"} );
		functionDictionnary.add( new String[]{"GetEditText","INTEGER"} );
		functionDictionnary.add( new String[]{"GetEditText","STRING"} );
		functionDictionnary.add( new String[]{"GetEditText","STRING","STRING"} );
		functionDictionnary.add( new String[]{"GetImage","INTEGER"} );
		functionDictionnary.add( new String[]{"GetImageButton","INTEGER"} );
		functionDictionnary.add( new String[]{"GetString","INTEGER"} );
		functionDictionnary.add( new String[]{"GetText","INTEGER"} );
		functionDictionnary.add( new String[]{"GetText","STRING"} );
		functionDictionnary.add( new String[]{"GetText","STRING","STRING"} );
		functionDictionnary.add( new String[]{"GetTopParent","STRING"} );
		functionDictionnary.add( new String[]{"GetViews","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"GetViews","INTEGER"} );
		functionDictionnary.add( new String[]{"GetViews"} );
		functionDictionnary.add( new String[]{"GetViews","STRING"} );
		functionDictionnary.add( new String[]{"GoBack"} );
		functionDictionnary.add( new String[]{"GoBackToActivity","STRING"} );
		functionDictionnary.add( new String[]{"IsCheckBoxChecked","INTEGER"} );
		functionDictionnary.add( new String[]{"IsCheckBoxChecked","STRING"} );
		functionDictionnary.add( new String[]{"IsRadioButtonChecked","INTEGER"} );
		functionDictionnary.add( new String[]{"IsRadioButtonChecked","STRING"} );
		functionDictionnary.add( new String[]{"IsSpinnerTextSelected","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"IsSpinnerTextSelected","STRING"} );
		functionDictionnary.add( new String[]{"IsTextChecked","STRING"} );
		functionDictionnary.add( new String[]{"IstoggleButtonChecked","INTEGER"} );
		functionDictionnary.add( new String[]{"IstoggleButtonChecked","STRING"} );
		functionDictionnary.add( new String[]{"PressMenuItem","INTEGER"} );
		functionDictionnary.add( new String[]{"PressMenuItem","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"PressSpinnerItem","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"ScrollDown"} );
		functionDictionnary.add( new String[]{"ScrollDownList","INTEGER"} );
		functionDictionnary.add( new String[]{"ScrollListToBottom","INTEGER"} );
		functionDictionnary.add( new String[]{"ScrollListToLine","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"ScrollListToTop","INTEGER"} );
		functionDictionnary.add( new String[]{"ScrollToBottom"} );
		functionDictionnary.add( new String[]{"ScrollToSide","INTEGER"} );
		functionDictionnary.add( new String[]{"ScrollToTop"} );
		functionDictionnary.add( new String[]{"ScrollToUp"} );
		functionDictionnary.add( new String[]{"ScrollUpList","INTEGER"} );
		functionDictionnary.add( new String[]{"SearchButton","STRING"} );
		functionDictionnary.add( new String[]{"SearchButton","STRING","STRING"} );
		functionDictionnary.add( new String[]{"SearchButton","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"SearchButton","STRING","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"SearchEditText","STRING"} );
		functionDictionnary.add( new String[]{"SearchText","STRING"} );
		functionDictionnary.add( new String[]{"SearchText","STRING","STRING"} );
		functionDictionnary.add( new String[]{"SearchText","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"SearchText","STRING","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"SearchText","STRING","INTEGER","STRING","STRING"} );
		functionDictionnary.add( new String[]{"SearchToggleButton","STRING"} );
		functionDictionnary.add( new String[]{"SearchToggleButton","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"SendKey","INTEGER"} );
		functionDictionnary.add( new String[]{"SetActivityOrientation","INTEGER"} );
		functionDictionnary.add( new String[]{"SetDataPicker","INTEGER","INTEGER","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"SetProgressBar","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"SetSlidingDrawer","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"SetTimePicker","INTEGER","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"SleepSolo","INTEGER"} );
		functionDictionnary.add( new String[]{"TakeScreenshot"} );
		functionDictionnary.add( new String[]{"TakeScreenshot","STRING"} );
		functionDictionnary.add( new String[]{"TakeScreenshot","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"TypeText","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"WaitForActivity","STRING"} );
		functionDictionnary.add( new String[]{"WaitForActivity","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"WaitForDialogToClose","LONG"} );
		functionDictionnary.add( new String[]{"WaitForDialogToOpen","LONG"} );
		functionDictionnary.add( new String[]{"WaitForLogMessage","STRING"} );
		functionDictionnary.add( new String[]{"WaitForLogMessage","STRING","INTEGER"} );
		functionDictionnary.add( new String[]{"WaitForText","STRING"} );
		functionDictionnary.add( new String[]{"WaitForText","STRING","INTEGER","LONG"} );
		functionDictionnary.add( new String[]{"WaitForText","STRING","INTEGER","LONG","STRING"} );
		functionDictionnary.add( new String[]{"WaitForText","STRING","INTEGER","LONG","STRING","STRING"} );
		functionDictionnary.add( new String[]{"WaitForView","STRING","INTEGER","INTEGER"} );
		functionDictionnary.add( new String[]{"WaitForView","STRING","INTEGER","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"WaitForView","STRING","INTEGER","STRING"} );
		functionDictionnary.add( new String[]{"StartRobotiumTestOn","STRING","STRING","STRING","INTEGER"} );
	}


	/**
	 * 
	 * @param signature String[0] is name of the signature
	 * String[0] is name of the signature
	 * String[1] ..String[n] are the parameters 
	 *  which could have value "UNKNOWN" "STRING" "INTEGER" "TABLE"
	 * @return true if signature correspond to a existing one
	 */
	public boolean isExist (List<String> signature) {

		//if parameter are "unknown" , verify without them.
		List<Integer> unknow_parameter_position  = new ArrayList<Integer>();

		for (int i=1; i<signature.size() ; i++) 
			if (signature.get(i).equals("UNKNOWN")) 
				unknow_parameter_position.add(i); 


		List<String[]> nearlymethod = getSignatures(signature);
		//return empty list if name doesn't correspond
		for(String[] nm : nearlymethod) {
			//special case of function with table in signature
			if(nm.length>1 && 
					"TABLE".equals(nm[1]) )
				return true;

			if (nm.length != signature.size())
				continue;

			boolean is_good = true;
			for (int i=1 ; i<signature.size() ; i++) {
				if (unknow_parameter_position.contains(i))
					continue;
				if( ! nm[i].startsWith(signature.get(i) )) 
					is_good=false;						
			}

			if(is_good ==true)
				return true;

		}
		return false;
	}



	/**
	 * 
	 * @param signature
	 * String[0] is name of the signature
	 * String[1] ..String[n] are the parameters 
	 *  which could have value "UNKNOWN" "STRING" "NUMBER"
	 * @return
	 */
	//TODO : improve?
	public List<String[]> getSignatures(List<String> signature) {
		return getSignatures(signature.get(0) );

	}

	/**
	 * 
	 * @param Methodname
	 * @returna List which contains all signature equal to method name
	 */
	public List<String[]> getSignatures(String Methodname) {

		List<String[] > result = new ArrayList<String[]>();

		for(String[] signature : functionDictionnary) {

			//compare name of the function
			if( !signature[0].toLowerCase().equals(Methodname.toLowerCase()) )
				continue;
			result.add(signature);
		} 

		return result;
	}

	/**
	 * specific to autocompletion
	 * @param Methodname
	 * @returna List which contains all signature equal to method name
	 */
	public List<String> getSignaturesforautocopmpletion(String Methodname) {

		List<String > result = new ArrayList<String>();

		for(String[] signature : functionDictionnary) {

			//compare name of the function
			if( !signature[0].toUpperCase().startsWith(Methodname.toUpperCase()) )
				continue;
			result.add(toString(signature) );
		} 

		return result;
	}

	/**
	 * specific to autocompletion
	 * @param Methodname
	 * @returna List which contains all signature equal to method name
	 */
	public String toString(String[] signature) {

		if (signature.length <1)
			return "";


		StringBuffer buf = new StringBuffer();
		buf.append(signature[0]);
		if (signature.length >1) {
			buf.append("( "+signature[1]);

			if ( signature.length >2)
				for(int i=2 ; i<signature.length ;i++) 
					buf.append(" , "+signature[i]);

			buf.append(" )");
		}
		String s = buf.toString();

		return s;
	}

}
