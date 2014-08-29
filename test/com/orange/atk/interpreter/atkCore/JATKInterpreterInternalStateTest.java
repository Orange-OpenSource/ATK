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
 * File Name   : com.orange.atk.interpreter.atkCore.TestJATKInterpreterInternalState.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.atkCore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JATKInterpreterInternalStateTest {

	JATKInterpreterInternalState oiis = null;

	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(JATKInterpreterInternalStateTest.class);
	}

	@Before
	public void setUp() {
		oiis = new JATKInterpreterInternalState();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetCurrentScript() {
		assertNull(oiis.getCurrentScript());
		oiis.setCurrentScript("test.tst");
		assertEquals("test.tst", oiis.getCurrentScript());
	}

	@Test
	public void testGetLoop() {
		assertEquals(-1,oiis.getLoopValue());
		oiis.setLoopValue(10);
		assertEquals(10, oiis.getLoopValue());
	}

	@Test
	public void testGetLogDir(){
		assertNull(oiis.getLogDir());
		oiis.setLogDir("/tmp");
		assertEquals("/tmp", oiis.getLogDir());		
	}
	
	@Test
	public void testIsStartMainLogCalled(){
		assertFalse(oiis.isStartMainLogCalled());
		oiis.setStartMainLogCalled(true);
		assertTrue(oiis.isStartMainLogCalled());
		oiis.setStartMainLogCalled(false);
		assertFalse(oiis.isStartMainLogCalled());
	}
	
}