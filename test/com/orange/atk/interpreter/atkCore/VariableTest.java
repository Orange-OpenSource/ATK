package com.orange.atk.interpreter.atkCore;/*
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
 * File Name   : com.orange.atk.interpreter.atkCore.TestVariableClass.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests associated to the interpreter.Variable
 * class.
 */

public class VariableTest {
	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(VariableTest.class);
	}
	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testCreateString() {
		Variable v = Variable.createString("aValue");
		assertEquals("aValue", v.getString());
		v = Variable.createString("");
		assertEquals("", v.getString());
	}

	@Test
	public void testCreateInteger() {
		Variable v = Variable.createInteger(32);
		assertEquals(32, v.getInteger().intValue());
		v = Variable.createInteger(-1);
		assertEquals(-1, v.getInteger().intValue());
	}

	@Test(expected = NullPointerException.class)
	public void testNullPointerWithCreateString() {
		Variable.createString(null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullPointerWithCreateInteger() {
		Variable.createInteger(null);
	}

	@Test
	public void testIsString() {
		Variable v1 = Variable.createString("aValue");
		assertTrue(v1.isString());
		Variable v2 = Variable.createInteger(32);
		assertFalse(v2.isString());
	}
	
	@Test
	public void testIsInteger(){
		Variable v1 = Variable.createString("aValue");
		assertFalse(v1.isInteger());
		Variable v2 = Variable.createInteger(32);
		assertTrue(v2.isInteger());
	}
	
	@Test(expected=ClassCastException.class)
	public void testInvalidGetString(){
		Variable v = Variable.createInteger(32);
		v.getString();
	}
	
	
	@Test(expected=ClassCastException.class)
	public void testInvalidGetInteger(){
		Variable v = Variable.createString("aValue");
		v.getInteger();
	}
}
