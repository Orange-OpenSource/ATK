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
 * File Name   : Out.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */

package com.orange.atk.atkUI.corecli.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

/**
 *  Class carrying the two outputs for messages: main and log.
 *  To be initialized at the very beginning of the program.
 *  All messages should be printed using exclusively Out.main 
 *  or/and Out.log print streams.
 */
public class Out {
		
		public static PrintStream main = System.out; //null;
		public static PrintStream log = System.out; //null;
		public static void setMain(PrintStream stream) { main = stream; }
		public static void setLog(PrintStream stream) { log = new LogPrintStream(stream); }
		public static PrintStream getMain() { return main; }
		public static PrintStream getLog() { return log; }
		
		public static class LogPrintStream extends PrintStream {
			public LogPrintStream(OutputStream out) {
				super(out);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println()
			 */
			public void println() {
				printTimeStamp();
				super.println();
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(boolean)
			 */
			public void println(boolean x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(char)
			 */
			public void println(char x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(char[])
			 */
			public void println(char[] x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(double)
			 */
			public void println(double x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(float)
			 */
			public void println(float x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(int)
			 */
			public void println(int x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(long)
			 */
			public void println(long x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(java.lang.Object)
			 */
			public void println(Object x) {
				printTimeStamp();
				super.println(x);
			}

			/* (non-Javadoc)
			 * @see java.io.PrintStream#println(java.lang.String)
			 */
			public void println(String x) {
				printTimeStamp();
				super.println(x);
			}
			
			private void printTimeStamp() {
				Date now = new Date();
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
//				super.print("["+now.toString()+"] ");
				super.print("["+df.format(now)+"] ");
			}
		}
}