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
 * File Name   : TestActionsLogger.java
 *
 * Created     : 10/07/2008
 * Author(s)   : PHELIZOT Yvan
 */
package com.orange.atk.results.logger.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import com.orange.atk.platform.Platform;

public class ActionsLoggerTest {
	ActionsLogger actionLogger = null;

	/**
	 * setUp
	 */
	@Before
	public void setUp() {
		actionLogger = new ActionsLogger(Platform.TMP_DIR);
	}

	/**
	 * Test the initial state of getActions. No action should be available at
	 * the beginning
	 */
	@Test
	public void testGetActionsInitial() {
		assertNotNull(actionLogger.getActions());
		assertEquals(0, actionLogger.getActions().size());
	}

	/**
	 * Test a valid usage of addAction
	 */
	@Test
	public void testAddValidAction() {
		Action a = new Action();
		a.setActionName("anAction");
		Date dstart = new Date();
		a.setStartTime(dstart);
		Date dend = new Date();
		a.setEndTime(dend);
		actionLogger.addAction("MsgType","anAction",new Date(),new Date());
		assertEquals(1, actionLogger.getActions().size());
		assertEquals("anAction", actionLogger.getActions().get(0)
				.getActionName());
		assertEquals(dstart.getTime(), actionLogger.getActions().get(0)
				.getStartTime().getTime());
		assertEquals(dend.getTime(), actionLogger.getActions().get(0)
				.getEndTime().getTime());
	}

	/**
	 * Test a valid usage of addAction add two actions with the same name
	 */
	@Test
	public void testAddValidActionWithSameName() {
		Action a = new Action();
		a.setActionName("anAction");
		a.setStartTime(new Date());
		a.setEndTime(new Date());
		actionLogger.addAction("MsgType","anAction",new Date(),new Date());
		a.setActionName("anAction");
		a.setStartTime(new Date());
		a.setEndTime(new Date());
		actionLogger.addAction("MsgType","anAction",new Date(),new Date());
		assertEquals(2, actionLogger.getActions().size());
	}

	
	
	

	
	
	
	/**
	 * Test startTime == endTime
	 */
	@Test
	public void testAddValidActionWithEqualTimes() {
		Action a = new Action();
		a.setActionName("anAction");
		Date date = new Date();
		a.setStartTime(date);
		a.setEndTime(date);
		actionLogger.addAction("MsgType","anAction",new Date(),new Date());
		assertEquals(1, actionLogger.getActions().size());
	}
	
	/**
	 * Test addAction with parameter of action equals to null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddInvalidAction1() {
		actionLogger.addAction("MsgType","anAction",null,null);

	}

	/**
	 * Test addAction with parameter of actions invalid (startTime > endTime)
	 * @throws InterruptedException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddInvalidAction2() throws InterruptedException {
		Action a = new Action();
		a.setEndTime(new Date());
		a.setActionName("anAction");
		Thread.sleep(1000);
		a.setStartTime(new Date());
		actionLogger.addAction("MsgType","anAction",new Date(),new Date());
		
		
		actionLogger.addAction(a);

	}
	
	
	
	/**
	 * Test addAction with invalid Msgtype ""
	 * @throws InterruptedException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddInvalidMsgType() throws InterruptedException {
		Action a = new Action();
		a.setEndTime(new Date());
		a.setActionName("anAction");
		Thread.sleep(1000);
		a.setStartTime(new Date());
		actionLogger.addAction("","anAction",new Date(),new Date());
	}
	
	
	
	

	/**
	 * Test addAction with argument equals to null
	 */
	@Test(expected = NullPointerException.class)
	public void testNullAction() {
		actionLogger.addAction("MsgType",null,new Date(),new Date());
	}

	/**
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testSaveAndLoad() throws IOException {
		// check the format
		Date d1_a1 = new Date();
		Date d1_a2 = new Date();
		Action a1 = new Action();
		a1.setActionName("anAction1");
		a1.setStartTime(d1_a1);
		Date d2_a1 = new Date();
		a1.setEndTime(d2_a1);
		actionLogger.addAction("MsgTypea","anAction1",new Date(),new Date());
		Action a2 = new Action();
		a2.setActionName("anAction2");
		a2.setStartTime(d1_a2);
		Date d2_a2 = new Date();
		a2.setEndTime(d2_a2);
		actionLogger.addAction("MsgTypeb","anAction2",new Date(),new Date());	
		File fichier = new File(Platform.TMP_DIR+Platform.FILE_SEPARATOR+"tmpTest.txt");
		OutputStream out = new FileOutputStream(fichier);
		actionLogger.save(out);
		out.close();
		
		ActionsLogger newActionLogger = new ActionsLogger(Platform.TMP_DIR);
		newActionLogger.load(fichier);
		Vector<Action> actions = newActionLogger.getActions();

		assertEquals(2, actions.size());
		a1 = actions.get(0);
		a2 = actions.get(1);
		if (a1.getActionName().equals("anAction1")) {
			assertEquals("anAction2", a2.getActionName());
			assertEquals(d1_a1.getTime(), a1.getStartTime().getTime());
			assertEquals(d1_a2.getTime(), a1.getEndTime().getTime());
			assertEquals(d2_a1.getTime(), a2.getStartTime().getTime());
			assertEquals(d2_a2.getTime(), a2.getEndTime().getTime());
			assertEquals("MsgTypeb", a2.getMsgType());

		} else {
			assertEquals("anAction1", a2.getActionName());
			assertEquals(d2_a1.getTime(), a1.getStartTime().getTime());
			assertEquals(d2_a2.getTime(), a1.getEndTime().getTime());
			assertEquals(d1_a1.getTime(), a2.getStartTime().getTime());
			assertEquals(d1_a2.getTime(), a2.getEndTime().getTime());
			assertEquals("MsgTypea", a2.getMsgType());
			
		}

	}
}
