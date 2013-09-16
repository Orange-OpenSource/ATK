/*
 * Software Name : ATK - UIautomatorViewer Robotium Version
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
 * File Name   : LaunchUiAutomatorViewer.java
 *
 * Created     : 05/06/2013
 * Author(s)   : D'ALMEIDA Joana
 */

package com.android.uiautomator;

/**
 * contains the signatures of some methods of Solo.class
 */

public class ListMethodSolo {
	

	public boolean waitForDialogToOpen(long timeout) {
		return false;
	}

	public boolean waitForDialogToClose(long timeout) {
		return false;	
	}
	
	public void goBack(){
		
	}
	
	public void clickOnScreen(float x, float y) {
		
	}
	
	public void clickOnButton(String name) {

	}
	
	public void clickOnImageButton(int index) {
		
	}
	
	public void clickOnToggleButton(String name) {
		
	}
	
	public void clickOnText(String text) {
	}
	
	public void clickOnButton(int index) {
	}
	
	public void clickOnRadioButton(int index) {
	}
	
	public void clickOnCheckBox(int index) {
	}
	
	public void clickOnEditText(int index) {
	}

	public /*ArrayList<TextView>*/ void clickInList(int line) {
	}
	
	public void clickOnActionBarHomeButton() {
	}

	public void drag(float fromX, float toX, float fromY, float toY, 
			int stepCount) {
	}

	public boolean scrollDown() {
		return false;
	}

	public void scrollToBottom() {
	}

	public boolean scrollUp(){
		return false;
	}

	public void scrollToTop() {
	}

	public boolean scrollDownList(int index) {
		return false;
	}

	
	public boolean scrollListToBottom(int index) {
		return false;
	}
	
	
	public boolean scrollUpList(int index) {
		return false;
	}
	
	public boolean scrollListToTop(int index) {
		return false;
	}
	
	public void scrollListToLine(int index, int line){
	}

	public void scrollToSide(int side) {  
	}
	
	public void scrollViewToSide(/*View view,*/ int side) {
	}

	
	public void setDatePicker(int index, int year, int monthOfYear, int dayOfMonth) {
	}
	
	public void setDatePicker(/*DatePicker datePicker, */ int year, int monthOfYear, int dayOfMonth) {
	}
	
	public void setTimePicker(int index, int hour, int minute) {		
	}
	
	public void setTimePicker(/*TimePicker timePicker,*/ int hour, int minute) {
	}
	
	public void setProgressBar(int index, int progress){
	}

	public void setSlidingDrawer(int index, int status){
	}

	public void setSlidingDrawer(/*SlidingDrawer slidingDrawer,*/ int status){
	}

	public void enterText(int index, String text) {		
	}
	
	public void clearEditText(int index) {
	}
	
	public void clickOnImage(int index) {
	}
	
	public boolean isRadioButtonChecked(int index){
		return false;
	}
	public boolean isRadioButtonChecked(String text){
		return false;
	}
	
	public boolean isCheckBoxChecked(int index) {
		return false;
	}

	public boolean isToggleButtonChecked(String text){
		return false;
	}
	
	public boolean isToggleButtonChecked(int index) {
		return false;
	}
	
	public boolean isCheckBoxChecked(String text) {
		return false;
	}
	
	public boolean isTextChecked(String text){
		return false;
	}
	
	public boolean isSpinnerTextSelected(String text) {
		return false;
	}
	
	public void hideSoftKeyboard() {
	}
	
	public void sendKey(int key) {
	}


}
