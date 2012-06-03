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
		dl = new DocumentLogger("tests\\file");
		File file = new File("C:\\Program Files\\JATK\\log\\SEphoneConfile.xml");
		dl.load(file);

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
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("BATTERY"));
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("CPU"));
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("MEMORY"));
		assertEquals(Long.MAX_VALUE, dl.getMinValueFromList("STORAGE"));
	}

	@Test
	public void testDefaultMaxValue() {
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("BATTERY"));
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("CPU"));
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("MEMORY"));
		assertEquals(Long.MIN_VALUE, dl.getMaxValueFromList("STORAGE"));
	}

	@Test
	public void testDefaultAveValue() {
		assertEquals(Double.NaN, dl.getAveValueFromList("BATTERY"));
		assertEquals(Double.NaN, dl.getAveValueFromList("CPU"));
		assertEquals(Double.NaN, dl.getAveValueFromList("MEMORY"));
		assertEquals(Double.NaN, dl.getAveValueFromList("STORAGE"));
	}

	@Test
	public void testAddDataToList() {
		dl.addDataToList("BATTERY", 1L, 10F);

		dl.addDataToList("CPU", 1L, -10F);
		dl.addDataToList("CPU", 2L, 10F);

		dl.addDataToList("MEMORY", 1L, 10000F);
		dl.addDataToList("MEMORY", 3L, -30F);
		dl.addDataToList("MEMORY", 8L, -10F);

		dl.addDataToList("STORAGE", 100L, 10F);
		dl.addDataToList("STORAGE", 10000L, 10F);
		dl.addDataToList("STORAGE", 10001L, 10F);
		dl.addDataToList("STORAGE", 11000L, 30F);

		assertEquals(10L, dl.getMinValueFromList("BATTERY"));
		assertEquals(-10L, dl.getMinValueFromList("CPU"));
		assertEquals(-30L, dl.getMinValueFromList("MEMORY"));
		assertEquals(10L, dl.getMinValueFromList("STORAGE"));

		assertEquals(10L, dl.getMaxValueFromList("BATTERY"));
		assertEquals(10L, dl.getMaxValueFromList("CPU"));
		assertEquals(10000L, dl.getMaxValueFromList("MEMORY"));
		assertEquals(30L, dl.getMaxValueFromList("STORAGE"));

		assertEquals(10L, dl.getAveValueFromList("BATTERY"));
		assertEquals(0, dl.getAveValueFromList("CPU"));
		assertEquals(3320L, dl.getAveValueFromList("MEMORY"));
		assertEquals(15L, dl.getAveValueFromList("STORAGE"));

		assertEquals(1, dl.getPlotList("BATTERY").getSize());
		assertEquals(2, dl.getPlotList("CPU").getSize());
		assertEquals(3, dl.getPlotList("MEMORY").getSize());
		assertEquals(4, dl.getPlotList("STORAGE").getSize());
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
