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
 * File Name   : FileUtilities.java
 *
 * Created     : 26/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class FileUtilities {

	private FileUtilities() {
	}
	/**
	 * A file filter that accepts specified files.
	 */
	public static class Filter extends javax.swing.filechooser.FileFilter {
		private String description = "";
		private String extension = "";

		public Filter(String desc, String ext) {
			description = desc;
			extension = ext;
		}
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String nomFichier = f.getPath().toLowerCase();
			if (nomFichier.endsWith(extension)) {
				return true;
			}
			return false;
		}
		public String getDescription() {
			return description;
		}
	}

	/**
	 * Verify the extension of a given file
	 * 
	 * @param src
	 *            the given file path
	 * @param extension
	 *            the extension to add if it is necessary
	 * @return the new file path
	 */
	public static String verifyExtension(String src, String extension) {
		if (src.indexOf('.') != -1) {
			if (!src.substring(src.lastIndexOf('.')).equals(extension)) {
				src += extension;
			}
		} else {
			src += extension;
		}
		return src;
	}

	/**
	 * A file filter that accepts only directories.
	 */
	public static class FilterDir extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			return false;
		}
		public String getDescription() {
			return "All directories";
		}
	}

	/**
	 * Copy a source text file into a destination file.
	 * 
	 * @param in
	 *            source file
	 * @param out
	 *            destination file
	 * @throws Exception
	 */
	public static void copyTextFile(File in, File out) throws Exception {
		FileReader fis = new FileReader(in);
		FileWriter fos = new FileWriter(out);
		char[] buf = new char[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}

	/**
	 * Copy a source file into a destination file in a binary way.
	 * 
	 * @param in
	 *            source file
	 * @param out
	 *            destination file
	 * @throws Exception
	 */
	public static void copyBinaryFile(File in, File out) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}

	/**
	 * Copy a source html file into a destination file, patching the style sheet
	 * on the fly for the given one.
	 * 
	 * @param in
	 *            source html file
	 * @param out
	 *            destination file
	 * @param newStyleSheetPath
	 *            the new css style sheet absolute path
	 * @throws Exception
	 */
	public static void copyHTMLFile(File in, File out, String newStyleSheetPath) throws Exception {
		FileReader fis = new FileReader(in);
		FileWriter fos = new FileWriter(out);
		char[] buf = new char[1024];
		int i = 0;

		// if the file contains enough character
		int byteToRead = 250;
		if (fis.ready()) {
			// 1. read first characters (up to path of css file)
			String text = "";
			while ((i < byteToRead) || (i == -1)) {
				i = fis.read(buf);
				text = text + new String(buf);
			}
			// 2. change css style sheet
			if (newStyleSheetPath != null) {
				String newText = changeStyleFile(text, newStyleSheetPath);
				fos.write(newText);
			}
			// 3. read remainings, if any...
			if (i != -1) {
				while ((i = fis.read(buf)) != -1) {
					fos.write(buf, 0, i);
				}
			}
		}
		fis.close();
		fos.close();
	}

	/**
	 * Copy a source html file into a destination file, patching the style sheet
	 * on the fly for the given one.
	 * 
	 * @param in
	 *            source html file
	 * @param out
	 *            destination file
	 * @param newStyleSheetPath
	 *            the new css style sheet absolute path
	 * @throws Exception
	 */
	public static void copyHTMLFilePrettyPrint(File in, File out, String newStyleSheetPath)
			throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(in.getAbsolutePath());
		Element linkElem = (Element) document.selectSingleNode("/html/head/link");
		if (linkElem != null) {
			linkElem.addAttribute("href", newStyleSheetPath);
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileOutputStream(out), format);
		writer.write(document);
		writer.close();
	}

	private static String changeStyleFile(String htmlText, String newStyleFilePath) {
		String regexp = "file:.*[.]css";
		String replacement = /* "file:" + */newStyleFilePath;
		return htmlText.replaceFirst(regexp, replacement);
	}

	/**
	 * Replace color name by color value from file 'in' to file 'out'. In order
	 * to resolve form a file, conciders copying it in a temp file before:
	 * <code>
	 *  FileUtilities.copyFile( new File(filePath), tmp);
	 * 	FileUtilities.resolveColor(tmp, new File(filePath), "orange", "#FF6600");
	 * </code>
	 * 
	 * @param in
	 *            source html file
	 * @param out
	 *            destination file
	 * @param colorName
	 *            the new name of the color (ex: orange)
	 * @param colorValue
	 *            the value to use (ex: #FF6A0)
	 * @throws IOException
	 */
	public static void resolveHTMLColor(File in, File out, String colorName, String colorValue)
			throws IOException {
		BufferedReader fis = new BufferedReader(new FileReader(in));
		FileWriter fos = new FileWriter(out);

		String readText = "";
		while (readText != null) {
			readText = fis.readLine();
			String regexp = "<font color=\"" + colorName + "\"";
			String replacement = "<font color=\"" + colorValue + "\"";
			if (readText != null) {
				if (readText.indexOf(regexp) > 0) {
					readText = readText.replaceFirst(regexp, replacement);
				}
				fos.write(readText);
			}
		}

		fis.close();
		fos.close();
	}

}
