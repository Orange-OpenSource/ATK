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
 * File Name   : TextGenerator.java
 *
 * Created     : 09/04/2008
 * Author(s)   : France Télécom
 */
package com.orange.atk.results.logger.documentGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.log.DocumentLogger;
import com.orange.atk.results.logger.log.Message;

/**
 * This class is used to create a simple report in text format.
 */

public class TextGenerator implements DocumentGenerator {
	private OutputStream outputStream;

	/**
	 * Constructor
	 * 
	 * @param outputStream
	 *            stream where log messages will be written
	 */
	public TextGenerator(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @see com.orange.atk.results.logger.documentGenerator.DocumentGenerator#dumpInStream(boolean,
	 *      com.orange.atk.results.logger.log.DocumentLogger)
	 */
	public void dumpInStream(boolean isParseException, DocumentLogger dl) {
		List<Message> v = dl.getMsgsLogged();
		for (int i = 0; i < v.size(); i++) {
			try {
				Message m = v.get(i);
				SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ssSSS");
				String dateString = formatter.format(m.getTimestamp());
				switch (m.getType()) {
					case Message.INFO_MSG :
						outputStream.write(("[" + dateString + "] "
								+ m.getMessage() + Platform.LINE_SEP).getBytes());
						break;
					case Message.WARN_MSG :
						outputStream.write(("[" + dateString + "] WARN : "
								+ m.getMessage() + Platform.LINE_SEP).getBytes());
						break;
					case Message.ERROR_MSG :
						outputStream.write(("[" + dateString + "] ERROR : "
								+ m.getMessage() + " l." + m.getLine() + " "
								+ m.getScriptName() + Platform.LINE_SEP).getBytes());
						break;
					default :
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
