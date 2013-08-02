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
 * File Name   : TestDocumentLogger.java
 *
 * Created     : 20/05/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.log.DocumentLogger;
import com.orange.atk.results.logger.log.Message;

public class TestDocumentLogger {
	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestDocumentLogger.class);
	}

	private static final String LOG_DIR = Platform.TMP_DIR;
	DocumentLogger dl = null;

	@Before
	public void setUp() {
		URL configfile = TestDocumentLogger.class.getResource("file/config.xml");
		dl = new DocumentLogger(LOG_DIR);
		dl.load(configfile.getFile());

	}

	@Test
	public void testDefaultGetMsgsLogged() {
		assertNotNull("dl.getMsgsLogged() is null", dl.getMsgsLogged());
		assertEquals(0, dl.getMsgsLogged().size());
	}

	@Test
	public void testLogMessages() {
		dl.addInfoToLog("info", 2, "aScript1");
		dl.addErrorToLog("error", 3, "aScript2");
		dl.addWarningToLog("warning", 4, "aScript3");

		if (dl.getMsgsLogged().size() != 3) {
			fail("More messages logged than expected");
		}

		Message msgsInfo = dl.getMsgsLogged().get(0);
		Message msgsErro = dl.getMsgsLogged().get(1);
		Message msgsWarn = dl.getMsgsLogged().get(2);

		if (!((msgsInfo.getType() == Message.INFO_MSG)
				&& (msgsInfo.getLine() == 2)
				&& ("info".equals(msgsInfo.getMessage())) && ("aScript1"
					.equals(msgsInfo.getScriptName())))) {
			fail("Info message has not been saved correctly");
		}

		if (!((msgsErro.getType() == Message.ERROR_MSG)
				&& (msgsErro.getLine() == 3)
				&& ("error".equals(msgsErro.getMessage())) && ("aScript2"
					.equals(msgsErro.getScriptName())))) {
			fail("Error message has not been saved correctly");
		}

		if (!((msgsWarn.getType() == Message.WARN_MSG)
				&& (msgsWarn.getLine() == 4)
				&& ("warning".equals(msgsWarn.getMessage())) && ("aScript3"
					.equals(msgsWarn.getScriptName())))) {
			fail("Warning message has not been saved correctly");
		}
	}

	@Test
	public void testGetBatPNGFiles() {
		assertNotNull(dl.getPNGpath("BATTERY"));
		assertTrue(isValidFileName(dl.getPNGpath("BATTERY")));
	}

	@Test
	public void testGetCpuPNGFiles() {
		assertNotNull(dl.getPNGpath("BATTERY"));
		assertTrue(isValidFileName(dl.getPNGpath("CPU")));
	}

	@Test
	public void testGetMemPNGFiles() {
		assertNotNull(dl.getPNGpath("BATTERY"));
		assertTrue(isValidFileName(dl.getPNGpath("MEMORY")));
	}

	@Test
	public void testGetStoNGFiles() {
		assertNotNull(dl.getPNGpath("BATTERY"));
		assertTrue(isValidFileName(dl.getPNGpath("STORAGE")));
	}

	@Test
	public void testDefaultMinValue() {
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("Battery"));
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("Cpu"));
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("Memory"));
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("Storage"));
	}

	@Test
	public void testDefaultMaxValue() {
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("Battery"));
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("Cpu"));
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("Memory"));
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("Storage"));
	}

	@Test
	public void testDefaultAveValue() {
		assertEquals(Double.NaN, dl.getAveValueFromList("Battery"), 0.1);
		assertEquals(Double.NaN, dl.getAveValueFromList("Cpu"), 0.1);
		assertEquals(Double.NaN, dl.getAveValueFromList("Memory"), 0.1);
		assertEquals(Double.NaN, dl.getAveValueFromList("Storage"), 0.1);
	}

	@Test
	public void testAddDataToList() {
		dl.addDataToList("Battery", 1L, 10F);

		dl.addDataToList("Cpu", 1L, -10F);
		dl.addDataToList("Cpu", 2L, 10F);

		dl.addDataToList("Memory", 1L, 10000F);
		dl.addDataToList("Memory", 3L, -30F);
		dl.addDataToList("Memory", 8L, -10F);

		dl.addDataToList("Storage", 100L, 10F);
		dl.addDataToList("Storage", 10000L, 10F);
		dl.addDataToList("Storage", 10001L, 10F);
		dl.addDataToList("Storage", 11000L, 30F);

		assertEquals(10L, dl.getMinValueFromList("Battery"));
		assertEquals(-10L, dl.getMinValueFromList("Cpu"));
		assertEquals(-30L, dl.getMinValueFromList("Memory"));
		assertEquals(10L, dl.getMinValueFromList("Storage"));

		assertEquals(10L, dl.getMaxValueFromList("Battery"));
		assertEquals(10L, dl.getMaxValueFromList("Cpu"));
		assertEquals(10000L, dl.getMaxValueFromList("Memory"));
		assertEquals(30L, dl.getMaxValueFromList("Storage"));

		assertEquals(10L, dl.getAveValueFromList("Battery"), 0.1);
		assertEquals(0, dl.getAveValueFromList("Cpu"), 0.1);
		assertEquals(3320L, dl.getAveValueFromList("Memory"), 0.1);
		assertEquals(15L, dl.getAveValueFromList("Storage"), 0.1);

		assertEquals(1, dl.getPlotList("Battery").getSize());
		assertEquals(2, dl.getPlotList("Cpu").getSize());
		assertEquals(3, dl.getPlotList("Memory").getSize());
		assertEquals(4, dl.getPlotList("Storage").getSize());
	}

	public boolean isValidFileName(String name) {
		if (name == null) {
			return false;
		}
		File f = new File(name);
		if (!f.exists()) {
			try {
				boolean b = f.createNewFile();
				if (b) {
					if (!f.delete())
						Logger.getLogger(this.getClass()).warn(
								"Can't  delete   " + f.getPath());

				}
				return b;
			} catch (IOException ioe) {
				return false;
			}
		}
		return true;
	}
}
