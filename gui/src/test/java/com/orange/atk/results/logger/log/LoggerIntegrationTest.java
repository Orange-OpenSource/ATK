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
 * File Name   : com.orange.atk.results.logger.log.TestIntegrationLogger.java
 *
 * Created     : 20/05/2008
 * Author(s)   : France Telecom
 */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

import com.orange.atk.interpreter.atkCore.JATKInterpreter;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.documentGenerator.DocumentGenerator;
import com.orange.atk.results.measurement.PlotList;

public class LoggerIntegrationTest {

	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LoggerIntegrationTest.class);
	}

	ResultLogger l = null;
	DummyDocGen dcg = null;
	DefaultPhone dp = new DefaultPhone();
	@Before
	public void setUp() {
		dcg = new DummyDocGen();
		l = new ResultLogger(Platform.TMP_DIR + "/", dcg,
				"C:\\Program Files\\JATK\\Salome-script\\Confile.xml");
	}

	@Test
	public void testAddMessage() {
		l.addInfoToDocumentLogger("info", 2, "aScript1");
		l.addErrorToDocumentLogger("error", 3, "aScript2");
		l.addWarningToDocumentLogger("warning", 4, "aScript3");
		l.dumpInStream(false);

		if (dcg.msgs == null) {
			fail("No messages logged...");
		}
		if (dcg.msgs.size() != 3) {
			fail("More messages logged than expected");
		}

		Message msgsInfo = dcg.msgs.get(0);
		Message msgsErro = dcg.msgs.get(1);
		Message msgsWarn = dcg.msgs.get(2);

		if (!((msgsInfo.getType() == Message.INFO_MSG) &&
				(msgsInfo.getLine() == 2) &&
				("info".equals(msgsInfo.getMessage())) && ("aScript1".equals(msgsInfo
				.getScriptName())))) {
			fail("Info message has not been saved correctly");
		}

		if (!((msgsErro.getType() == Message.ERROR_MSG) &&
				(msgsErro.getLine() == 3) &&
				("error".equals(msgsErro.getMessage())) && ("aScript2".equals(msgsErro
				.getScriptName())))) {
			fail("Error message has not been saved correctly");
		}

		if (!((msgsWarn.getType() == Message.WARN_MSG) &&
				(msgsWarn.getLine() == 4) &&
				("warning".equals(msgsWarn.getMessage())) && ("aScript3".equals(msgsWarn
				.getScriptName())))) {
			fail("Warning message has not been saved correctly");
		}
	}

	@Test
	public void testThread() {
		JATKInterpreter interpreter = new JATKInterpreter(dp, l, "aScript", "aLogDir",
				"anIncludeDir");
		l.setInterpreter(interpreter);
		l.start(500);
		assertTrue("Thread is not alive", l.isAlive());
		l.interrupt();
		l.join();
		assertFalse("Thread is alive", l.isAlive());
	}

	@Test
	public void testMeasurementThread() throws InterruptedException {
		JATKInterpreter interpreter = new JATKInterpreter(dp, l, "aScript", "aLogDir",
				"anIncludeDir");
		l.setInterpreter(interpreter);
		l.start(500);
		Thread.sleep(9750);
		l.interrupt();
		l.join();

		l.dumpInStream(false);

		if ((dcg.plBat == null) ||
				(dcg.plCpu == null) ||
				(dcg.plMem == null) ||
				(dcg.plSto == null)) {
			fail("Some measurement data are not logged...");
		}
		if (!((dcg.plBat.getSize() == dcg.plCpu.getSize()) &&
				(dcg.plBat.getSize() == dcg.plMem.getSize()) &&
				(dcg.plBat.getSize() == dcg.plSto.getSize()) && (dcg.plBat.getSize() == 20))) {
			fail("Invalid number of measurement data logged (bat=" + dcg.plBat.getSize() +
					", cpu=" + dcg.plCpu.getSize() +
					", Mem=" + dcg.plMem.getSize() +
					", Sto=" + dcg.plBat.getSize() + ")");
		}
	}
	@Test
	public void testGenerateGraph() throws InterruptedException {
		JATKInterpreter interpreter = new JATKInterpreter(dp, l, "aScript", Platform.TMP_DIR,
				"anIncludeDir");
		l.setInterpreter(interpreter);
		l.start(500);
		Thread.sleep(9750);
		l.interrupt();
		l.join();
		l.generateGraphFile();

		assertTrue(new File(Platform.TMP_DIR + "/memlist.png").exists());
		assertTrue(new File(Platform.TMP_DIR + "/cpulist.png").exists());
		assertTrue(new File(Platform.TMP_DIR + "/batlist.png").exists());
		assertTrue(new File(Platform.TMP_DIR + "/stolist.png").exists());
	}
}

class DummyDocGen implements DocumentGenerator {
	List<Message> msgs = null;
	PlotList plBat = null;
	PlotList plCpu = null;
	PlotList plMem = null;
	PlotList plSto = null;
	public void dumpInStream(boolean isParseException, DocumentLogger dl) {
		msgs = dl.getMsgsLogged();

		plBat = dl.getPlotList("BATTERY");
		plCpu = dl.getPlotList("CPU");
		plMem = dl.getPlotList("MEMORY");
		plSto = dl.getPlotList("STORAGE");
	}

}
