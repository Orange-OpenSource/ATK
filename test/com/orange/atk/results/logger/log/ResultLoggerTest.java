package com.orange.atk.results.logger.log;/*
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
 * File Name   : com.orange.atk.results.logger.log.TestLogger.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.Test;

import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.TextGenerator;
import com.orange.atk.results.logger.log.ResultLogger;


public class ResultLoggerTest {
	DefaultPhone dp = new DefaultPhone();
	@Test(expected = NullPointerException.class)
	public void testConstructor1() throws FileNotFoundException {
		new ResultLogger(null, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor2() throws FileNotFoundException {
		new ResultLogger(Platform.TMP_DIR, null,"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor3() throws FileNotFoundException {
		new ResultLogger("#!%q^^^^", new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
	}
	
	@Test(expected = NullPointerException.class)
	public void testLogResourceUsageWithoutProvidingAPhoneInterface() throws FileNotFoundException, PhoneException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.addResourcesInfoToDocumentLogger();
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetPhoneInterfaceWithNullParam() throws FileNotFoundException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.setPhoneInterface(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetInterpreterWithNullParam() throws FileNotFoundException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.setInterpreter(null);
	}
	@Test(expected = NullPointerException.class)
	public void testCallStartWithoutPhoneInterface() throws FileNotFoundException{
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");		
		l.start(10);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testStartWithNegativeValue() throws FileNotFoundException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.setPhoneInterface(dp);
		l.start(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testStartWith0Value() throws FileNotFoundException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.setPhoneInterface(dp);
		l.start(0);
	}
	
	@Test(expected = NullPointerException.class)
	public void testCallInterruptWithoutCallStart() throws FileNotFoundException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.interrupt();
	}
	
	@Test(expected = NullPointerException.class)
	public void testCallJoinWithoutCallStart() throws FileNotFoundException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.setPhoneInterface(dp);
		l.join();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testCallStartTwice() throws FileNotFoundException {
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.setPhoneInterface(dp);
		l.start(10);
		l.start(10);
	}
	
	@Test(expected=NullPointerException.class)
	public void testSaveScreenshotNullFileName() throws FileNotFoundException{
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.saveScreenshot(null, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testSaveScreenshotNullImage() throws FileNotFoundException{
		ResultLogger l = new ResultLogger(Platform.TMP_DIR, new TextGenerator(new FileOutputStream(new File(
				Platform.TMP_DIR + Platform.FILE_SEPARATOR + "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
		l.saveScreenshot("BOB", null );
	}
	
}
