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
 * File Name   : com.orange.atk.results.logger.log.TestLogMessage.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */

import static org.junit.Assert.assertEquals;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;


public class MessageTest {
	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(MessageTest.class);
	}
	@Test
	public void testCreateErrorMessage() {
		long date = new Date().getTime();
		Message msg = Message.createErrorMessage("aErrorMsg", date, 2, "here/aScript.txt");
		assertEquals(Message.ERROR_MSG, msg.getType());
		assertEquals("aErrorMsg", msg.getMessage());
		assertEquals(date, msg.getTimestamp());
		assertEquals(2, msg.getLine());
		assertEquals("here/aScript.txt", msg.getScriptName());
	}

	@Test
	public void testCreateInfoMessage() {
		long date = new Date().getTime();
		Message msg = Message.createInfoMessage("aInfoMessage", date, 20, "here/aScript1.txt");
		assertEquals(Message.INFO_MSG, msg.getType());
		assertEquals("aInfoMessage", msg.getMessage());
		assertEquals(date, msg.getTimestamp());
		assertEquals(20, msg.getLine());
		assertEquals("here/aScript1.txt", msg.getScriptName());
	}

	@Test
	public void testCreateWarningMessage() {
		long date = new Date().getTime();
		Message msg = Message.createWarningMessage("aWarningMessage", date, -1, "here/aScript2.txt");
		assertEquals(Message.WARN_MSG, msg.getType());
		assertEquals("aWarningMessage", msg.getMessage());
		assertEquals(date, msg.getTimestamp());
		assertEquals(-1, msg.getLine());
		assertEquals("here/aScript2.txt", msg.getScriptName());
	
	}
}