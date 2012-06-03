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
 * File Name   : TestJATKInterpreterStackClass.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.orange.atk.interpreter.atkCore.JATKInterpreterStack;


/**
 * Unit tests associated to {@link com.orange.atk.interpreter.atkCore.JATKInterpreterStack}
 * class.
 * 
 */

public class TestJATKInterpreterStackClass{
	JATKInterpreterStack stack = null;
	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestJATKInterpreterStackClass.class);
	}
	@Before
	public void setUp(){
		stack = new JATKInterpreterStack();
	}
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	public void testPushPopString(){
		stack.pushString("aValue");
		assertEquals("aValue", stack.popString());
	}
	
	@Test
	public void testPushPopInteger(){
		stack.pushInteger(123);
		assertEquals(123, stack.popInteger().intValue());
	}
	
	@Test
	public void testIsEmpty(){
		assertTrue(stack.isEmpty());
		stack.pushString("aValue");
		assertFalse(stack.isEmpty());
		stack.popString();
		assertTrue(stack.isEmpty());
	}
	
	@Test
	public void testPopEmptyStack(){
		assertEquals(null, stack.popInteger() );
		assertEquals(null, stack.popString() );
	}
	
	@Test
	public void testIsTopString(){
		stack.pushInteger(32);
		stack.pushString("aValue");
		assertTrue(stack.isTopString());
		stack.pushInteger(132);
		assertFalse(stack.isTopString());
	}
	
	@Test
	public void testIsTopInteger(){
		stack.pushString("aValue1");
		stack.pushInteger(132);
		assertTrue(stack.isTopInteger());
		stack.pushString("aValue2");
		assertFalse(stack.isTopInteger());
	}
	
	@Test
	public void testIsTopWithEmptyStack(){
		assertFalse(stack.isTopString());
		assertFalse(stack.isTopInteger());
	}
}
