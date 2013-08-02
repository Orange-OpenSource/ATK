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
 * File Name   : CommandLineParser.java
 *
 * Created     : 16/02/2007
 * Author(s)   : Nicolas MOTEAU
 */
package com.orange.atk.atkUI.corecli;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The CommandLineParser parses command line options.
 * 
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class CommandLineParser {

	/**
	 * parser comming from registered extensions
	 */
	private Collection<ICommandLineParser> cmdLineParsers = new ArrayList<ICommandLineParser>();

	private String[] args;

	public CommandLineParser(String[] arg1) {
		args = arg1;
	}

	/**
	 * Register a new parser. If the given parser is null, nothing is done.
	 * 
	 * @param parser
	 *            parser to add
	 */
	public void registerParser(ICommandLineParser parser) {
		if (parser != null)
			cmdLineParsers.add(parser);
	}

	/**
	 * Parses the command line arguments
	 * 
	 * @args the arguments
	 */
	public CommandLine parse() {
		CommandLine globalOptions = new CommandLine();

		// general treatment of options here..
		if ((args.length == 0) || ((args.length == 1) && (!args[0].startsWith("-")))) {
			globalOptions.setMode(CommandLine.Modes.GUI);
		}

		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();
			if (arg.contains("-help")) {
				System.out.println();
				System.out.println(getSynopsis());
			}
		}

		// conciders extension's options
		for (ICommandLineParser p : cmdLineParsers) {
			CommandLine op = p.parse(args);
			if (op != null) {
				globalOptions.agregate(op);
			}
		}

		return globalOptions;
	}

	/**
	 * Gets back the synopsis of the command line
	 * 
	 * @return
	 */
	public String getSynopsis() {
		StringBuffer synopsis = new StringBuffer();
		synopsis.append("Mobile Code Analysis TOols release " + Configuration.getVersion()
				+ " (rev." + Configuration.getRevision() + ") help:\n");
		return synopsis.toString();
	}

}
