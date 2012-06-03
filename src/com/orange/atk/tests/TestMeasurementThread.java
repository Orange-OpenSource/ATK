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
 * File Name   : TestMeasurementThread.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.tests;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.TextGenerator;
import com.orange.atk.results.logger.log.ResultLogger;
import com.orange.atk.results.measurement.MeasurementThread;


public class TestMeasurementThread {
	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestMeasurementThread.class);
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorWithNullPointer() {
		new MeasurementThread(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStartWithNegativeValue() throws FileNotFoundException {
		MeasurementThread m = new MeasurementThread(new ResultLogger(
				Platform.TMP_DIR, new TextGenerator(new FileOutputStream(
						new File(Platform.TMP_DIR + Platform.FILE_SEPARATOR
								+ "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml"));
		m.start(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testStartWith0Value() throws FileNotFoundException {
		MeasurementThread m = new MeasurementThread(new ResultLogger(
				Platform.TMP_DIR, new TextGenerator(new FileOutputStream(
						new File(Platform.TMP_DIR + Platform.FILE_SEPARATOR
								+ "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml"));
		m.start(0);
	}
	@Test(expected = NullPointerException.class)
	public void testInterruptWithoutStart() throws FileNotFoundException {
		MeasurementThread m = new MeasurementThread(new ResultLogger(
				Platform.TMP_DIR, new TextGenerator(new FileOutputStream(
						new File(Platform.TMP_DIR + Platform.FILE_SEPARATOR
								+ "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml"));
		m.interrupt();
	}
	@Test(expected = NullPointerException.class)
	public void testJoinWithoutStart() throws FileNotFoundException {
		MeasurementThread m = new MeasurementThread(new ResultLogger(
				Platform.TMP_DIR, new TextGenerator(new FileOutputStream(
						new File(Platform.TMP_DIR + Platform.FILE_SEPARATOR
								+ "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml"));
		m.join();
	}
	
	@Test
	public void testIsAliveWithoutCallingStart() throws FileNotFoundException {
		MeasurementThread m = new MeasurementThread(new ResultLogger(
				Platform.TMP_DIR, new TextGenerator(new FileOutputStream(
						new File(Platform.TMP_DIR + Platform.FILE_SEPARATOR
								+ "tmp.txt"))),"C:\\Program Files\\JATK\\Salome-script\\Confile.xml"));
		assertFalse(m.isAlive());
	}
}
