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
 * File Name   : TestPlotList.java
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

import com.orange.atk.platform.Platform;
import com.orange.atk.results.measurement.PlotList;


/**
 * Unit tests associated to {@link PlotList}
 * 
 */
public class TestPlotList {
	private static String logDir = Platform.TMP_DIR;
	PlotList list1 = null;

	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestPlotList.class);
	}

	@Before
	public void setUp() {
		list1 = new PlotList("test","c:","c:","x comment","y comment",1,true,"blue");
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetSize() {
		PlotList list1=null;
		list1 = new PlotList("test","c:","c:","x comment","y comment",1,true,"blue");
		list1.addValue(10F);
		assertEquals(1, list1.getSize());
		list1.addValue(10F);
		assertEquals(2, list1.getSize());

		for (int i = 0; i < 100; i++) {
			list1.addValue(10F);
		}
		assertEquals(102, list1.getSize());
	}

	@Test
	public void testGetMin() {
		list1.addValue(10F);
		list1.addValue(-3F);
		list1.addValue(3F);
		list1.addValue(30F);
		list1.addValue(-30F);
		list1.addValue(30000F);
		assertEquals(-30L, list1.getMin().longValue());
	}

	@Test
	public void testGetMinIfEmpty() {
		assertEquals(Long.MAX_VALUE, list1.getMin().longValue());
	}

	@Test
	public void testGetMax() {
		list1.addValue(10F);
		list1.addValue(-3F);
		list1.addValue(3F);
		list1.addValue(30F);
		list1.addValue(-30F);
		list1.addValue(30000F);
		assertEquals(30000L, list1.getMax().longValue());
	}

	@Test
	public void testGetMaxIfEmpty() {
		assertEquals(Long.MIN_VALUE, list1.getMax().longValue());
	}

	@Test
	public void testAddValue2Params() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		assertEquals(1, list1.getSize());
		list1.addValue(30L, 3F);
		assertEquals(2, list1.getSize());
		list1.addValue(2L, -100F);
		assertEquals(2, list1.getSize());
		list1.addValue(31L, -100F);
		assertEquals(3, list1.getSize());
	}

	@Test
	public void testIsEmpty() {
		assertTrue(list1.isEmpty());
		list1.addValue(-1L, 10F);
		assertFalse(list1.isEmpty());
		list1.addValue(-2L, -3F);
		assertFalse(list1.isEmpty());
		list1.addValue(30L, 3F);
		assertFalse(list1.isEmpty());
	}

	@Test
	public void testGetX() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -100F);
		/* expected = [ -1, 30, 31] */
		assertEquals(-1L, list1.getX(0).longValue());
		assertEquals(30L, list1.getX(1).longValue());
		assertEquals(31L, list1.getX(2).longValue());
	}

	@Test
	public void testGetY() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -100F);
		/* expected = [ 10, 3, -100] */
		assertEquals(10L, list1.getY(0).longValue());
		assertEquals(3L, list1.getY(1).longValue());
		assertEquals(-100L, list1.getY(2).longValue());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidGetXWith0() {
		list1.getX(0);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidGetXWithMinus1() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -100F);
		list1.getX(-1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidGetXWithGetSize() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -100F);
		list1.getX(list1.getSize());

	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidGetYWith0() {
		list1.getY(0);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidGetYWithMinus1() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -100F);
		list1.getY(-1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidGetYWithGetSize() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -100F);
		list1.getY(list1.getSize());
	}

	@Test
	public void testGetAverage() {
		assertEquals(Double.NaN, list1.getAverage());
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -102F);
		/* expected = Sum([ 10, 3, -100]) = -89/3 ~ -29.67 */
		assertEquals(-29.67, list1.getAverage());
	}

	@Test
	public void testGetDelta() {
		assertEquals(Double.NaN, list1.getDelta());
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -102F);
		/* expected = sqrt((10+89/3)^2+(3+89/3)^2+(-102+89/3)^2)/3 */
		assertEquals(29.03, list1.getDelta());
	}

	@Test
	public void testGetScale() {
		list1.addValue(-1L, 10F);
		list1.addValue(-2L, -3F);
		list1.addValue(30L, 3F);
		list1.addValue(2L, -100F);
		list1.addValue(31L, -102F);
		/* expected = 32 / range */

		assertEquals(10L, list1.getXScale(3).longValue());
		assertEquals(6L, list1.getXScale(5).longValue());
	}

	@Test
	public void testGetScaleIfEmpty() {
		assertEquals(0, list1.getXScale(3).longValue());
		assertEquals(0, list1.getXScale(5).longValue());
	}

	
	


}