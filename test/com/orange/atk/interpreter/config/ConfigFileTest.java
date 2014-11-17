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
 * File Name   : com.orange.atk.interpreter.config.TestConfigFile.java
 *
 * Created     : 16/05/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.interpreter.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import com.orange.atk.platform.Platform;


public class ConfigFileTest {
	private static final String BIN_SH = "/bin/sh";

	// To allow the test runner to run this test class
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ConfigFileTest.class);
	}
	
	@Test 
	public void testAddOption(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("An_option", "A value");
		assertEquals(fichier.getOption("An_option"), "A value");
	}
	
	@Test 
	public void testSaveOption(){
		File file1 = new File(Platform.TMP_DIR  + Platform.FILE_SEPARATOR + "configFile1.conf");
		try {
			file1.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConfigFile fichier = new ConfigFile(file1);
		fichier.setOption("An_option", "A value");
		assertTrue(fichier.saveConfigFile());
	}
	
	@Test 
	public void testLoadOption(){
		File file1 = new File(Platform.TMP_DIR  + Platform.FILE_SEPARATOR + "configFile2.conf");
		try {
			file1.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConfigFile fichier = new ConfigFile(file1);
		fichier.setOption("An_option", "A value");
		fichier.saveConfigFile();
		fichier = new ConfigFile(file1);
		assertTrue(fichier.loadConfigFile());
	}
	
	@Test 
	public void testSaveAndLoadOption(){
		File file1 = new File(Platform.TMP_DIR  + Platform.FILE_SEPARATOR + "configFile2.conf");
		try {
			file1.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConfigFile fichier = new ConfigFile(file1);
		fichier.setOption("An_option", "A value");
		fichier.saveConfigFile();
		fichier = new ConfigFile(file1);
		fichier.loadConfigFile();
		assertEquals(fichier.getOption("An_option"), "A value");
	}
	
	@Test 
	public void testMultipleSaveAndLoadOption(){
		File file1 = new File(Platform.TMP_DIR + Platform.FILE_SEPARATOR + "configFile2.conf");
		try {
			file1.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConfigFile fichier = new ConfigFile(file1);
		fichier.setOption("An_option", "A value");
		fichier.saveConfigFile();
		fichier = new ConfigFile(file1);
		fichier.loadConfigFile();
		fichier.setOption("Another_option", "Another value");
		fichier.saveConfigFile();
		fichier = new ConfigFile(file1);
		fichier.loadConfigFile();
		assertEquals("A value", fichier.getOption("An_option"));
		assertEquals("Another value", fichier.getOption("Another_option"));
	}
	@Test 
	public void testOverwriteConfigFile(){
		File file1 = new File(Platform.TMP_DIR + Platform.FILE_SEPARATOR + "configFile2.conf");
		try {
			file1.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConfigFile fichier = new ConfigFile(file1);
		fichier.setOption("An_option", "A value");
		fichier.saveConfigFile();
		fichier = new ConfigFile(file1);
		fichier.setOption("An_option1", "A value");
		fichier.saveConfigFile();
		fichier = new ConfigFile(file1);
		fichier.loadConfigFile();
		assertEquals(null, fichier.getOption("An_option"));
		assertEquals("A value", fichier.getOption("An_option1"));
	}
	@Test
	public void testRedefineOption(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("An_option", "A value");
		fichier.setOption("An_option", "Another value");
		assertEquals(fichier.getOption("An_option"), "Another value");
	}
	
	@Test
	public void testNullConfigFile(){
		ConfigFile fichier = new ConfigFile();
		assertFalse(fichier.saveConfigFile());
		assertFalse(fichier.loadConfigFile());
	}
	@Test
	public void testConfigFileDoesNotExist(){
		ConfigFile fichier = new ConfigFile(new File("/tmp/NotAValid/NotAGoodFile.txt"));
		assertFalse(fichier.saveConfigFile());
		assertFalse(fichier.loadConfigFile());		
	}
	@Test
	public void testConfigFileNotValid(){
		ConfigFile fichier = new ConfigFile(new File(BIN_SH));
		assertFalse(fichier.saveConfigFile());
		assertFalse(fichier.loadConfigFile());		
	}
	
	@Test(expected=NullPointerException.class)
	public void testInvalidSetOption1(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption(null, "A value");
	}
	@Test(expected=NullPointerException.class)
	public void testInvalidSetOption2(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("An_Option", null);		
	}
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidSetOption3(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("", "A value");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidSetOption4(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("An_Option", "");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidSetOption5(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("An_Op#\"tion", "");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidSetOption6(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("An_Option", "A val=ue");
	}
	
	@Test
	public void testValidSetOption(){
		ConfigFile fichier = new ConfigFile();
		fichier.setOption("abcdefghijklmnopqrstuvwxyz" +
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
				"/\\ _0123456789", "abcdefghijklmnopqrstuvwxyz" +
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
				"/\\ _0123456789");
		assertEquals(fichier.getOption("abcdefghijklmnopqrstuvwxyz" +
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
				"/\\ _0123456789"),"abcdefghijklmnopqrstuvwxyz" +
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
				"/\\ _0123456789");
	}
}
