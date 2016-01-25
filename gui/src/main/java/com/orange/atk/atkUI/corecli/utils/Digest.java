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
 * File Name   : Digest.java
 *
 * Created     : 18/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;

import org.apache.log4j.Logger;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class Digest {

	private static final int SIZE = 16384;
	private static final String base = "0123456789abcdef";

	private Digest() {
	}
	/**
	 * Compute the SHA-1 digest of a file from a FileInputStream
	 * 
	 * @param fis
	 *            a FileInputStream
	 * @return SHA-1 as a String
	 */
	public static String runSHA1(FileInputStream fis) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			byte buffer[] = new byte[SIZE];
			while (true) {
				int l = fis.read(buffer);
				if (l == -1)
					break;
				sha1.update(buffer, 0, l);
			}
			fis.close();
			byte digest[] = sha1.digest();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				buf.append(base.charAt((digest[i] >> 4) & 0xf));
				buf.append(base.charAt(digest[i] & 0xf));
			}
			return buf.toString();
		} catch (Exception e) {
			Logger.getLogger(Digest.class.getName()).error(e);
			return null;
		}
	}
	/**
	 * Compute the SHA-1 digest of a file from a FileInputStream
	 * 
	 * @param fis
	 *            a FileInputStream
	 * @return SHA-1 as a String
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be opened for
	 *             reading.
	 */
	public static String runSHA1(File f) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(f);
		return Digest.runSHA1(fis);
	}

	/**
	 * Compute the MD5 digest of a file from a FileInputStream
	 * 
	 * @param fis
	 *            a FileInputStream
	 * @return MD5 as a String
	 */
	public static String runMD5(FileInputStream fis) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("MD5");
			byte buffer[] = new byte[SIZE];
			while (true) {
				int l = fis.read(buffer);
				if (l == -1)
					break;
				sha1.update(buffer, 0, l);
			}
			fis.close();
			byte digest[] = sha1.digest();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				buf.append(base.charAt((digest[i] >> 4) & 0xf));
				buf.append(base.charAt(digest[i] & 0xf));
			}
			return buf.toString();
		} catch (Exception e) {
			Logger.getLogger(Digest.class.getName()).error(e);
			return null;
		}
	}
	/**
	 * Compute the MD5 digest of a file from a FileInputStream
	 * 
	 * @param fis
	 *            a FileInputStream
	 * @return MD5 as a String
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be opened for
	 *             reading.
	 */
	public static String runMD5(File f) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(f);
		return Digest.runMD5(fis);
	}

}
