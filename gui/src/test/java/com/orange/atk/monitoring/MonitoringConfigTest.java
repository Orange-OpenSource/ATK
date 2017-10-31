package com.orange.atk.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class MonitoringConfigTest {

	@Test
	public void testFromFile() {
		URL file = this.getClass().getResource("/config.xml");
		try {
			MonitoringConfig c = MonitoringConfig.fromFile(file.getFile());
			assertEquals(true, c.getAroSettings().isEnabled());
		} catch (IOException e) {
			fail("file not found: " + file.getFile());
		}

	}
}
