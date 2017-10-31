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
 * File Name   : com.orange.atk.interpreter.atkCore.TestJATKVariableTableClass.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.atkCore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests associated to interpreter.JATKVariableTable
 * class
 */
public class JATKVariableTableTest {
	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(JATKVariableTableTest.class);
	}
	@Before
	public void setUp(){
		
	}
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	public void testGetIntegerValue(){
		JATKVariableTable table = new JATKVariableTable();
		table.addIntegerVariable("anIntVariable", 132);
		assertTrue(table.isVariable("_anIntVariable"));
		assertTrue(table.getVariable("_anIntVariable").isInteger());
		assertEquals(132, table.getVariable("_anIntVariable").getInteger().intValue());
	}
	
	@Test
	public void testGetStringValue(){
		JATKVariableTable table = new JATKVariableTable();
		table.addStringVariable("anStringVariable", "aValue");
		assertTrue(table.isVariable("_anStringVariable"));
		assertTrue(table.getVariable("_anStringVariable").isString());
		assertEquals("aValue", table.getVariable("_anStringVariable").getString());
	}
	
	@Test
	public void getANonVariable(){
		JATKVariableTable table = new JATKVariableTable();
		table.addStringVariable("aVariable1", "aValue");
		assertFalse(table.isVariable("_aVariable"));
		assertEquals(null, table.getVariable("_aVariable"));
	}

	@Test
	public void getAVariableWithout_(){
		JATKVariableTable table = new JATKVariableTable();
		table.addStringVariable("aVariable", "aValue");
		assertFalse(table.isVariable("aVariable"));
		assertEquals(null, table.getVariable("aVariable"));
	}
	
	@Test
	public void getNullVariable(){
		JATKVariableTable table = new JATKVariableTable();
		table.addStringVariable("aVariable", "aValue");
		assertFalse(table.isVariable(null));
		assertEquals(null, table.getVariable(null));
	}
}
