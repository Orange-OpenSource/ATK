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
 * File Name   : CommandExecutor.java
 *
 * Created     : 27/03/2009
 * Author(s)   : Qinghua Zhang
 */
package com.orange.atk.phone.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * @deprecated no more used in android phone.
 * Will disappear in future.
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
public class CommandExecutor {
	public String  execute(String cmd) {
		Logger.getLogger(this.getClass() ).debug("Command is: "+cmd);

		String result = null;
		try {
			result = exec(cmd);
		} catch (Exception e) {
			return ( e.getMessage());
		}
		return result;
	}

	/**
	 * @param youCmd
	 * @return
	 * @throws Exception
	 */
	private static String exec(String youCmd) throws Exception {
		String msg = null;
		if (youCmd == null || youCmd.length() < 1) {
			throw new Exception("Please enter valid command line");
		}

		try {
			String osName = System.getProperty("os.name");
			String[] cmd = new String[3];
			if (("Windows 2000").equalsIgnoreCase(osName)
					|| "Windows XP".equalsIgnoreCase(osName) 
					|| "Windows 7".equalsIgnoreCase(osName)) {
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";
				cmd[2] = youCmd;
			} else if (osName.equals("Windows 95")) {
				cmd[0] = "command.com";
				cmd[1] = "/C";
				cmd[2] = youCmd;
			}

			// Logger.getLogger(this.getClass() ).debug("cmd:\r\n" + cmd[0] + " " + cmd[1] + " " +
			// cmd[2]);
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			InputStreamPipe errorGobbler = new InputStreamPipe(proc
					.getErrorStream(), "ERROR");
			InputStreamPipe outputGobbler = new InputStreamPipe(proc
					.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();

			int exitVal = proc.waitFor();
			if (exitVal != 0) {
				throw new Exception(errorGobbler.getOutputMsg() + "(exitVal="
						+ exitVal + ")");
			} else {
				msg = outputGobbler.getOutputMsg();
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		return msg;
	}
}

/**
 * Output Stream Read Class
 * 
 * @author
 * 
 */
class InputStreamPipe extends Thread {
	InputStream inputStream;
	StringBuffer bufMsg = new StringBuffer();

	InputStreamPipe(InputStream inputStream, String msgType) {
		this.inputStream = inputStream;
	}

	public String getOutputMsg() {
		return bufMsg.toString();
	}

	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					bufMsg.append(line + "\r\n");
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
