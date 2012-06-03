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
 * File Name   : FlashReportGenerator.java
 *
 * Created     : 24/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaScript.reportGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.anaScript.reportGenerator.resultLink.JatkResultLink;
import com.orange.atk.atkUI.anaScript.reportGenerator.visit.JatkInterpreter;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.Step.Verdict;
import com.orange.atk.atkUI.corecli.reportGenerator.ReportGenerator;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Do;
import com.orange.atk.atkUI.corecli.utils.Digest;
import com.orange.atk.atkUI.corecli.utils.HtmlOutput;
import com.orange.atk.atkUI.corecli.utils.XMLParser;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class FlashReportGenerator extends ReportGenerator {

	private JatkResultLink resultLink;

	public FlashReportGenerator(XMLParser parser, File flashResultsFile, File reportFile) {
		this.outputFile = reportFile;
		this.resultsFile = flashResultsFile;
		this.profileParser = parser;
	}

	/**
	 * @return
	 */
	public Verdict generateResult() throws Exception {
		resultLink = new JatkResultLink(resultsFile);
		if (!outputFile.exists()) {
			File parentDir = outputFile.getParentFile();
			if (!parentDir.exists()) 
				{
				if(!parentDir.mkdirs())
		     Logger.getLogger(this.getClass() ).debug("Can't Create "+parentDir.getParent());

				}
			if(!outputFile.createNewFile())
			Logger.getLogger(this.getClass() ).debug("Can't Create "+outputFile.getParent());

		}
		PrintStream out = new PrintStream(outputFile);
		Logger.getLogger(this.getClass() ).debug("outputFile : "+outputFile.getAbsolutePath());
		// generate HTML headers
		generateHeaders(out);
		// generate Flash file description
		generateFlashFileDescription(out);
		// generate report
		Verdict flashVerdict = generateFlashReport(out);
		out.println("</body>");
		out.println("</html>");
		out.close();
		return flashVerdict;
	}

	/**
	 * Generate report for the given flash file
	 * @param out
	 * @return
	 * @throws Exception
	 */
	private Verdict generateFlashReport(PrintStream out) throws Exception {
		Do doo = getDo();
		JatkInterpreter interpreter = new JatkInterpreter(out, resultLink);
		resultLink.setUnmarshaller(unmar);
		interpreter.play(doo);
		if (resultLink.isEmptyResults()) {
			out.println(HtmlOutput.paragraph(HtmlOutput.color("green", "No problem detected in the analysis of this flash file.")));
		}
		return interpreter.getVerdict();
	}

	/**
	 * Generate part of the report which describes the analysed flash file
	 * @param out
	 */
	private void generateFlashFileDescription(PrintStream out) throws Exception {
		out.println("<table>");
		out.println("<tr>");
		out.println("<th>Flash file analyzed</th>");
		out.println("<td>"+resultLink.getFlashFilePath()+"</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<th>SHA1 of Flash file analyzed</th>");
		out.println("<td>"+Digest.runSHA1(new FileInputStream(Configuration.fileResolver.getFile(resultLink.getFlashFilePath(), "matos", ".swf", null, null, null)))+"</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<th>Date</th>");
		String date = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM,
											 Locale.US).format(new GregorianCalendar().getTime ());
		out.println("<td>"+date+"</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<th>Rule file</th>");
		out.println("<td>"+profileParser.getFile().getAbsolutePath()+"</td>");
		out.println("</tr>");
		if (resultLink.getAnalyserVersion() != null) {
			out.println("<tr>");
			out.println("<th>Analyser version</th>");
			out.println("<td>"+resultLink.getAnalyserVersion()+"</td>");
			out.println("</tr>");
		}
		out.println("</table>");
		out.println("<hr width=\"100%\" />");
	}

	/**
	 * Generate Headers of the HTML report
	 * @param out
	 */
	private void generateHeaders(PrintStream out) {
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Flash Analyser report</title>");
		String css = new File(Configuration.getProperty(Configuration.CSS)).toURI().toString();
		out.println("<link href=\""+css+"\" rel=\"stylesheet\" type=\"text/css\"></link>");
		out.println("</head>");
		out.println("<body>");
	}

}
