package com.orange.atk.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.orange.atk.monitoring.MonitoringConfig;

public class TestMonitoringConfiguration {

	@Test
	public void testFromFile() {
		URL file = TestMonitoringConfiguration.class.getResource("file/config.xml");
		try {
			MonitoringConfig c = MonitoringConfig.fromFile(file.getFile());
			assertEquals(true, c.getAroSettings().isEnabled());
		} catch (IOException e) {
			fail("file not found: " + file.getFile());
		}

	}
}
