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
 * File Name   : PDFGenerator.java
 *
 * Created     : 09/04/2008
 * Author(s)   : France Télécom
 */
package com.orange.atk.results.logger.documentGenerator;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.orange.atk.platform.Platform;
import com.orange.atk.results.logger.log.Action;
import com.orange.atk.results.logger.log.ActionsLogger;
import com.orange.atk.results.logger.log.DocumentLogger;
import com.orange.atk.results.logger.log.Message;
import com.orange.atk.results.measurement.PlotList;

/**
 * This class creates a report stored in pdf format. Informations, measurements
 * graphs and tables are saved.
 */

public class PDFGenerator implements DocumentGenerator {
	private static final Font FONT_PAR_TITLE = FontFactory.getFont(BaseFont.HELVETICA,
			BaseFont.WINANSI,
			BaseFont.NOT_EMBEDDED, 16);

	private static final Color ORANGE_COLOR = new Color(0xFF, 0x66, 0x00);

	private OutputStream outputStream;

	private String author;
	private String group;
	private String script;
	private String dir;
	// private boolean isTableEnabled;

	/**
	 * Constructor
	 * 
	 * @param outputStream
	 *            stream where the pdf report would be saved
	 * @param dir
	 *            Folder where results while be saved
	 * @param author
	 *            person which executes the test
	 * @param group
	 *            group of author
	 * @param script
	 *            name of the executed script
	 * @param isTableEnabled
	 *            indicates if you want measurements data
	 */
	public PDFGenerator(OutputStream outputStream, String dir, String author,
			String group, String script, boolean isTableEnabled) {
		this.outputStream = outputStream;
		this.author = author;
		this.group = group;
		this.script = script;
		this.dir = dir;
		// this.isTableEnabled = isTableEnabled;
	}

	public void createHTMLFile(DocumentLogger dl)
	{
		File htmlfile = new File(dir + Platform.FILE_SEPARATOR + "report.html");
		FileOutputStream out = null;
		PrintStream ps = null;
		try {
			out = new FileOutputStream(htmlfile);
			ps = new PrintStream(out);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.getLogger(this.getClass()).warn("Can't Create HTML file");
			return;
		}

		ps.println("<html>");
		ps.println("<body>");
		long endTime = new Date().getTime();
		ps.println("<span style=\"font-weight: bold;\">Summary</span>");
		ps.println("<br>");
		ps.println("Author : " + author);
		ps.println("<br>");
		ps.println("Group : " + group);
		ps.println("<br>");
		ps.println("Script : " + script);
		ps.println("<br>");
		SimpleDateFormat formatter = new SimpleDateFormat(
				"MMM d, yyyy - hh:mm aaa");
		String dateString = formatter.format(endTime);
		ps.println("Date : " + dateString);
		ps.println("<br>");
		ps.println("<br>");

		ps.println("<span style=\"font-weight: bold;\">Last logged lines :</span>");
		ps.println("<br>");
		ps.println("<br>");

		List<Message> msgLogged = dl.getMsgsLogged();

		int startIndex = msgLogged.size() > 5 ? msgLogged.size() - 5 : 0;
		for (int i = startIndex; i < msgLogged.size(); i++) {
			Message m = msgLogged.get(i);
			switch (m.getType()) {
				case Message.INFO_MSG :
					ps.println("INFO : " + m.getMessage());
					ps.println("<br>");
					break;
				case Message.WARN_MSG :
					ps.println(" <span style=\"color: rgb(255, 102, 0);\"> WARN : "
							+ m.getMessage() + "</span>");
					ps.println("<br>");

					break;
				case Message.ERROR_MSG :
					ps.println(" <span style=\"color: red;\">ERROR : " + m.getMessage() + "</span>");
					ps.println("<br>");

					break;
				default :
					break;

			}
		}

		ps.println("<br>");
		ps.println("<br>");

		ps.println("<span style=\"font-weight: bold;\">Executive summary</span>");
		ps.println("<br>");

		// Chapter 2 : Log information
		ps.println("<span style=\"font-weight: bold;\">Log information:</span>");
		ps.println("<br>");

		// step 4: we add a paragraph to the document
		// logged messages
		for (int i = 0; i < msgLogged.size(); i++) {
			Message msg = msgLogged.get(i);
			formatter = new SimpleDateFormat("H:mm:ssSSS");
			dateString = formatter.format(msg.getTimestamp());
			switch (msg.getType()) {
				case Message.INFO_MSG :
					ps.println("[" + dateString + "] " + msg.getMessage());
					ps.println("<br>");
					break;
				case Message.WARN_MSG :
					ps.println(" <span style=\"color: rgb(255, 102, 0);\">[" + dateString
							+ "] WARN : " + msg.getMessage()
							+ " at line : " + msg.getLine() + "</span><br>");
					ps.println("<br>");

					break;
				case Message.ERROR_MSG :
					ps.println(" <span style=\"color: red;\">[" + dateString + "] ERROR : "
							+ msg.getMessage()
							+ " at line : " + msg.getLine() + "</span><br>");
					ps.println("<br>");

					break;
				default :
					break;

			}
		}

		// Add generated pictures
		ps.println("<span style=\"font-weight: bold;\">Graphics</span>");
		ps.println("<br>");
		Map<String, PlotList> mapint = dl.getMapint();
		Set<String> cles = mapint.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			if (new File(dl.getPNGpath(cle)).exists()) {
				Image jpg1 = null;
				try {
					jpg1 = Image.getInstance(dl.getPNGpath(cle));
				} catch (BadElementException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				PlotList plotlist = mapint.get(cle);
				DecimalFormat df = new DecimalFormat("#,###.##");
				ps.println("<br>");
				if (plotlist.getType() == PlotList.TYPE_SUM) {
					ps.println("<span style=\"font-weight: bold;\">" + plotlist.getYComment() + ":"
							+ " (Total : " + df.format(plotlist.getTotal() / plotlist.getScale())
							+ " " + plotlist.getunit() + ")</span>");
				} else { // PlotList.TYPE_AVG
					ps.println("<span style=\"font-weight: bold;\">" + plotlist.getYComment() + ":"
							+ " (Average : "
							+ df.format(plotlist.getAverage() / plotlist.getScale()) + " "
							+ plotlist.getunit() + ")</span>");
				}
				ps.println("<br>");
				ps.println("<br>");

				ps.println("<img style=\"width: 640px; height: 480px;\" alt=\"\"title=\"" + cle
						+ "\" src=\"" + cle + ".png\"><br>&nbsp;</span><br>");

			}
		}

		ps.println("</body>");
		ps.println("</html>");
		ps.flush();
		ps.close();
	}

	public void createHTMLFileactionlog(ActionsLogger actionlog, DocumentLogger dl)
	{

		Vector VectAction = actionlog.getActions();
		// step 2:
		// we create a writer that listens to the document
		// and directs a PDF-stream to the outputStream

		File htmlfile = new File(dir + Platform.FILE_SEPARATOR + "report.html");
		FileOutputStream out = null;
		PrintStream ps = null;
		try {
			out = new FileOutputStream(htmlfile);
			ps = new PrintStream(out);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.getLogger(this.getClass()).warn("Can't Create PDF file");
			return;
		}

		ps.println("<html>");
		ps.println("<body>");
		long endTime = new Date().getTime();
		ps.println("<span style=\"font-weight: bold;\">Summary</span>");
		ps.println("<br>");
		ps.println("Author : " + author);
		ps.println("<br>");
		ps.println("Group : " + group);
		ps.println("<br>");
		ps.println("Script : " + script);
		ps.println("<br>");
		SimpleDateFormat formatter = new SimpleDateFormat(
				"MMM d, yyyy - hh:mm aaa");
		String dateString = formatter.format(endTime);
		ps.println("Date : " + dateString);
		ps.println("<br>");
		ps.println("<br>");

		ps.println("<span style=\"font-weight: bold;\">Last logged lines :</span>");
		ps.println("<br>");
		ps.println("<br>");

		int startIndex = VectAction.size() > 5 ? VectAction.size() - 5 : 0;
		for (int i = startIndex; i < VectAction.size(); i++) {

			Action action = (Action) VectAction.get(i);
			SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
			dateString = spf.format(action.getStartTime());
			ps.println("[ " + dateString + "] : " + action.getActionName());
			ps.println("<br>");

		}

		ps.println("<br>");
		ps.println("<br>");

		ps.println("<span style=\"font-weight: bold;\">Executive summary</span>");
		ps.println("<br>");

		// Chapter 2 : Log information
		ps.println("<span style=\"font-weight: bold;\">Log information:</span>");
		ps.println("<br>");

		// step 4: we add a paragraph to the document
		// logged messages
		for (int i = 0; i < VectAction.size(); i++) {
			// formatter = new SimpleDateFormat("H:mm:ssSSS");
			Action action = (Action) VectAction.get(i);
			SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
			dateString = spf.format(action.getStartTime());
			ps.println("[ " + dateString + "] : " + action.getActionName());
			ps.println("<br>");
		}

		// Add generated pictures
		ps.println("<span style=\"font-weight: bold;\">Graphics</span>");
		ps.println("<br>");
		Map<String, PlotList> mapint = dl.getMapint();
		Set<String> cles = mapint.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			if (new File(dl.getPNGpath(cle)).exists()) {
				Image jpg1 = null;
				try {
					jpg1 = Image.getInstance(dl.getPNGpath(cle));
				} catch (BadElementException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				PlotList plotlist = mapint.get(cle);
				DecimalFormat df = new DecimalFormat("#,###.##");
				ps.println("<br>");
				if (plotlist.getType() == PlotList.TYPE_SUM) {
					ps.println("<span style=\"font-weight: bold;\">" + plotlist.getYComment() + ":"
							+ " (Total : " + df.format(plotlist.getTotal() / plotlist.getScale())
							+ " " + plotlist.getunit() + ")</span>");
				} else { // PlotList.TYPE_AVG
					ps.println("<span style=\"font-weight: bold;\">" + plotlist.getYComment() + ":"
							+ " (Average : "
							+ df.format(plotlist.getAverage() / plotlist.getScale()) + " "
							+ plotlist.getunit() + ")</span>");
				}
				ps.println("<br>");
				ps.println("<br>");

				ps.println("<img style=\"width: 640px; height: 480px;\" alt=\"\"title=\"" + cle
						+ "\" src=\"" + cle + ".png\"><br>&nbsp;</span><br>");

			}
		}

		ps.println("</body>");
		ps.println("</html>");
		ps.flush();
		ps.close();
	}

	/**
	 * @see com.orange.atk.results.logger.documentGenerator.DocumentGenerator#dumpInStream(boolean,
	 *      com.orange.atk.results.logger.log.DocumentLogger)
	 */
	public void dumpInStream(boolean isParseErrorHappened, DocumentLogger dl) {
		long endTime = new Date().getTime();
		// step 1: creation of a document-object
		Document document = new Document();
		PdfWriter writer = null;
		// step 2:
		// we create a writer that listens to the document
		// and directs a PDF-stream to the outputStream
		try {
			writer = PdfWriter.getInstance(document, outputStream);
		} catch (DocumentException e1) {
			e1.printStackTrace();
			return;
		}
		writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
		// step 3: we open the document
		document.open();
		// step 4: we add a paragraph to the document
		List<Message> msgLogged = dl.getMsgsLogged();
		Paragraph pLoggedMsg = new Paragraph();
		// logged messages
		for (int i = 0; i < msgLogged.size(); i++) {
			Message msg = msgLogged.get(i);
			SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ssSSS");
			String dateString = formatter.format(msg.getTimestamp());
			switch (msg.getType()) {
				case Message.INFO_MSG :
					pLoggedMsg.add("[" + dateString + "] " + msg.getMessage());
					break;
				case Message.WARN_MSG :
					pLoggedMsg.add("[" + dateString + "] WARN : " + msg.getMessage()
							+ " at line : " + msg.getLine());
					break;
				case Message.ERROR_MSG :
					pLoggedMsg.add("[" + dateString + "] ERROR : " + msg.getMessage()
							+ " at line : " + msg.getLine());
					break;
				default :
					break;

			}
			pLoggedMsg.add(Chunk.NEWLINE);
		}

		Paragraph pLastLogguedLines = new Paragraph();
		int startIndex = msgLogged.size() > 5 ? msgLogged.size() - 5 : 0;
		for (int i = startIndex; i < msgLogged.size(); i++) {
			Message m = msgLogged.get(i);
			switch (m.getType()) {
				case Message.INFO_MSG :
					pLastLogguedLines.add("INFO : " + m.getMessage());
					break;
				case Message.WARN_MSG :
					pLastLogguedLines.add("WARN : " + m.getMessage());
					break;
				case Message.ERROR_MSG :
					pLastLogguedLines.add("ERROR : " + m.getMessage());
					break;
				default :
					break;

			}
			pLastLogguedLines.add(Chunk.NEWLINE);
		}
		// l.setIndentationLeft(40);
		// Min/Max/Ave values
		/*
		 * PdfPTable table = new PdfPTable(4); table.addCell("");
		 * table.addCell("Min"); table.addCell("Max"); table.addCell("Avg");
		 * 
		 * 
		 * table.addCell("Battery in  %"); table.addCell(String.valueOf(dl
		 * .getMinValueFromList(dl.getplt("BATTERY"))));
		 * table.addCell(String.valueOf(dl
		 * .getMaxValueFromList(dl.getplt("BATTERY"))));
		 * table.addCell(String.valueOf(dl
		 * .getAveValueFromList(dl.getplt("BATTERY"))));
		 * table.addCell("Storage in bytes"); table.addCell(String.valueOf(dl
		 * .getMinValueFromList(dl.getplt("Storage"))));
		 * table.addCell(String.valueOf(dl
		 * .getMaxValueFromList(dl.getplt("Storage"))));
		 * table.addCell(String.valueOf(dl
		 * .getAveValueFromList(dl.getplt("Storage"))));
		 */
		document.addTitle("REPORT");
		document.addCreationDate();

		HeaderFooter headerPage = new HeaderFooter(new Phrase("Execution report"),
				false);
		HeaderFooter footerPage = new HeaderFooter(new Phrase(" - "), new Phrase(
				" - "));
		headerPage.setAlignment(Element.ALIGN_CENTER);
		footerPage.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(headerPage);
		document.setFooter(footerPage);

		// Chapter 1 : Summary
		// Section 1 : Informations

		Chunk c = new Chunk("Summary");
		c.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c.setFont(FONT_PAR_TITLE);

		Paragraph title1 = new Paragraph(c);
		title1.setAlignment("CENTER");
		title1.setLeading(20);
		Chapter chapter1 = new Chapter(title1, 1);
		chapter1.setNumberDepth(0);

		Paragraph title11 = new Paragraph("Informations");
		Section section1 = chapter1.addSection(title11);
		Paragraph pSum = new Paragraph();
		pSum.add("Author : " + author);
		pSum.add(Chunk.NEWLINE);
		pSum.add("Group : " + group);
		pSum.add(Chunk.NEWLINE);
		pSum.add("Script : " + script);
		pSum.add(Chunk.NEWLINE);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"MMM d, yyyy - hh:mm aaa");
		String dateString = formatter.format(endTime);
		pSum.add("Date : " + dateString);
		pSum.add(Chunk.NEWLINE);
		pSum.setIndentationLeft(20);
		section1.add(pSum);
		section1.add(new Paragraph(Chunk.NEXTPAGE));

		Chunk c11 = new Chunk("Executive summary");
		c11.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c11.setFont(FONT_PAR_TITLE);
		Paragraph title12 = new Paragraph(c11);
		Section section12 = chapter1.addSection(title12);
		Paragraph pExecSum = new Paragraph();
		// pExecSum.add("Value : ");
		// pExecSum.add(Chunk.NEWLINE);
		// pExecSum.add(table);
		pExecSum.add(Chunk.NEWLINE);
		pExecSum.add("Last logged lines : ");
		pExecSum.add(Chunk.NEWLINE);
		pExecSum.add(pLastLogguedLines);
		pExecSum.add(Chunk.NEWLINE);
		pExecSum.setIndentationLeft(20);

		section12.add(pExecSum);

		try {
			document.add(chapter1);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		// Chapter 2 : Log information
		Chunk c2 = new Chunk("Log information");
		c2.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c2.setFont(FONT_PAR_TITLE);
		Paragraph title2 = new Paragraph(c2);
		title2.setAlignment("CENTER");
		title2.setLeading(20);
		Chapter chapter2 = new Chapter(title2, 1);
		chapter2.setNumberDepth(0);

		Section section2 = chapter2.addSection("Log");
		// Add log information
		section2.add(pLoggedMsg);
		section2.add(Chunk.NEWLINE);

		// section2.add(table);
		try {
			document.add(chapter2);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		if (isParseErrorHappened) {
			document.close();
			return;
		}

		// Add generated pictures
		Chunk c3 = new Chunk("Graphics");
		c3.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c3.setFont(FONT_PAR_TITLE);
		Paragraph p3 = new Paragraph(c3);
		p3.setAlignment("CENTER");
		p3.setLeading(20);
		Chapter chapter3 = new Chapter(p3, 1);
		chapter3.setNumberDepth(0);

		// add current graph
		Map<String, PlotList> mapint = dl.getMapint();
		Set<String> cles = mapint.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			Paragraph pCPUimg = new Paragraph(cle);
			Section section31 = chapter3.addSection(pCPUimg);
			if (new File(dl.getPNGpath(cle)).exists()) {
				Image jpg1 = null;
				try {
					jpg1 = Image.getInstance(dl.getPNGpath(cle));
				} catch (BadElementException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (jpg1 == null)
				{
					Logger.getLogger(this.getClass())
							.warn("Error when Creating image jpg1 is null");
					return;
				}
				// jpg1.setRotationDegrees(270);
				jpg1.scalePercent(75);
				jpg1.setAlignment(Element.ALIGN_CENTER);
				section31.add(jpg1);
				PlotList plotlist = mapint.get(cle);
				DecimalFormat df = new DecimalFormat("#,###.##");

				if (plotlist.getType() == PlotList.TYPE_SUM) {
					section31.add(new Paragraph("Total : "
							+ df.format(plotlist.getTotal() / plotlist.getScale()) + " "
							+ plotlist.getunit()));
				} else { // PlotList.TYPE_AVG
					section31.add(new Paragraph("Average : "
							+ df.format(plotlist.getAverage() / plotlist.getScale()) + " "
							+ plotlist.getunit()));
				}

			}
			section31.add(new Paragraph(Chunk.NEXTPAGE));
		}

		/*
		 * // Section 3.1 : CPU data Paragraph pCPUimg = new
		 * Paragraph("CPU data"); Section section31 =
		 * chapter3.addSection(pCPUimg); if (new
		 * File(dl.getPNGpath("CPU")).exists()) { Image jpg1 = null; try { jpg1
		 * = Image.getInstance(dl.getPNGpath("CPU")); } catch
		 * (BadElementException e) { e.printStackTrace(); } catch
		 * (MalformedURLException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); } //jpg1.setRotationDegrees(270);
		 * jpg1.scalePercent(75); jpg1.setAlignment(Element.ALIGN_CENTER);
		 * section31.add(jpg1); } section31.add(new Paragraph(Chunk.NEXTPAGE));
		 * 
		 * // Section 3.2 : BAT data Paragraph pBATimg = new
		 * Paragraph("Battery data"); Section section32 =
		 * chapter3.addSection(pBATimg); if (new
		 * File(dl.getPNGpath("BATTERY")).exists()) { Image jpg2 = null; try {
		 * jpg2 = Image.getInstance(dl.getPNGpath("BATTERY")); } catch
		 * (BadElementException e) { e.printStackTrace(); } catch
		 * (MalformedURLException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); } //jpg2.setRotationDegrees(270);
		 * jpg2.scalePercent(75); jpg2.setAlignment(Element.ALIGN_CENTER);
		 * section32.add(jpg2); } section32.add(new Paragraph(Chunk.NEXTPAGE));
		 * 
		 * // Section 3.3 : MEM data Paragraph pMEMimg = new
		 * Paragraph("Memory data"); Section section33 =
		 * chapter3.addSection(pMEMimg); if (new
		 * File(dl.getPNGpath("MEMORY")).exists()) {
		 * 
		 * Image jpg3 = null; try { jpg3 =
		 * Image.getInstance(dl.getPNGpath("MEMORY")); } catch
		 * (BadElementException e) { e.printStackTrace(); } catch
		 * (MalformedURLException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); } //jpg3.setRotationDegrees(270);
		 * jpg3.scalePercent(75); jpg3.setAlignment(Element.ALIGN_CENTER);
		 * section33.add(jpg3); } section33.add(new Paragraph(Chunk.NEXTPAGE));
		 * 
		 * // Section 3.4 : STO data Paragraph pSTOimg = new
		 * Paragraph("Storage data"); Section section34 =
		 * chapter3.addSection(pSTOimg); if (new
		 * File(dl.getPNGpath("Storage")).exists()) {
		 * 
		 * Image jpg4 = null; try { jpg4 =
		 * Image.getInstance(dl.getPNGpath("Storage")); } catch
		 * (BadElementException e) { e.printStackTrace(); } catch
		 * (MalformedURLException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); } //jpg4.setRotationDegrees(270);
		 * jpg4.scalePercent(75); jpg4.setAlignment(Element.ALIGN_CENTER);
		 * section34.add(jpg4); }
		 * 
		 * 
		 * // Section 3.5 : Network connection //Paragraph pNetworkimg = new
		 * Paragraph("Network QoS data"); //Section section35 =
		 * chapter3.addSection(pNetworkimg); // if (new
		 * File(dl.getNetworkPNGfile()).exists()) {
		 * 
		 * // Image jpg5 = null; // try { // jpg5 =
		 * Image.getInstance(dl.getNetworkPNGfile()); // } catch
		 * (BadElementException e) { // e.printStackTrace(); // } catch
		 * (MalformedURLException e) { // e.printStackTrace(); // } catch
		 * (IOException e) { // e.printStackTrace(); // }
		 * //jpg4.setRotationDegrees(270); // jpg5.scalePercent(75); //
		 * jpg5.setAlignment(Element.ALIGN_CENTER); // section35.add(jpg5); //}
		 */
		try {
			document.add(chapter3);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		// if (isTableEnabled) {
		// // Add tables filled with measurement
		// // Chapter 4 : Measurement tables
		// Chunk c4 = new Chunk("Statistics tables");
		// c4.setBackground(new Color(0xFF, 0x66, 0x00), 200, 3f, 200f, 3f);
		// c4.setFont(FontFactory.getFont(BaseFont.HELVETICA,
		// BaseFont.WINANSI, BaseFont.NOT_EMBEDDED, 16));
		// Paragraph title4 = new Paragraph(c4);
		// title4.setAlignment("CENTER");
		// title4.setLeading(20);
		// Chapter chapter4 = new Chapter(title4, 1);
		// chapter4.setNumberDepth(0);
		//
		// // Section 4.1 : Battery value
		// Paragraph titleSection41 = new Paragraph("Battery value");
		// Section section41 = chapter4.addSection(titleSection41);
		// Paragraph pBatList = new Paragraph();
		// pBatList.add(Chunk.NEWLINE);
		// pBatList.add(createPDFTableFromList(dl
		// .getList(DocumentLogger.BATTERY), "Battery"));
		// pBatList.setAlignment("CENTER");
		// section41.add(pBatList);
		//
		// // Section 4.2 : CPU value
		// Paragraph titleSection42 = new Paragraph("CPU value");
		// Section section42 = chapter4.addSection(titleSection42);
		// Paragraph pCPUList = new Paragraph();
		// pCPUList.add(Chunk.NEWLINE);
		// pCPUList.add(createPDFTableFromList(dl.getList(DocumentLogger.CPU),
		// "CPU"));
		// pCPUList.setAlignment("CENTER");
		// section42.add(pCPUList);
		//
		// // Section 4.3 : Memory value
		// Paragraph titleSection43 = new Paragraph("Memory value");
		// Section section43 = chapter4.addSection(titleSection43);
		// Paragraph pMemList = new Paragraph();
		// pMemList.add(Chunk.NEWLINE);
		// pMemList.add(createPDFTableFromList(dl
		// .getList(DocumentLogger.MEMORY), "Memory"));
		// pMemList.setAlignment("CENTER");
		// section43.add(pMemList);
		//
		// // Section 4.4 : Storage value
		// Paragraph titleSection44 = new Paragraph("Storage value");
		// Section section44 = chapter4.addSection(titleSection44);
		// Paragraph pStoList = new Paragraph();
		// pStoList.add(Chunk.NEWLINE);
		// pStoList.add(createPDFTableFromList(dl
		// .getList(DocumentLogger.STORAGE), "Storage"));
		// pStoList.setAlignment("CENTER");
		// section44.add(pStoList);
		//
		// try {
		// document.add(chapter4);
		// } catch (DocumentException e) {
		// e.printStackTrace();
		// }
		// }

		// step 5: we close the document
		document.close();
		createHTMLFile(dl);
	}

	public void dumpInStreamactionlogger(boolean isParseErrorHappened, ActionsLogger actionlog,
			DocumentLogger dl) {
		long endTime = new Date().getTime();
		// step 1: creation of a document-object
		Document document = new Document();
		PdfWriter writer = null;
		Vector VectAction = actionlog.getActions();
		// step 2:
		// we create a writer that listens to the document
		// and directs a PDF-stream to the outputStream
		try {
			writer = PdfWriter.getInstance(document, outputStream);
		} catch (DocumentException e1) {
			e1.printStackTrace();
			return;
		}
		writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
		// step 3: we open the document
		document.open();
		// step 4: we add a paragraph to the document
		Paragraph pLoggedMsg = new Paragraph();
		// logged messages

		for (int i = 0; i < VectAction.size(); i++) {
			Action action = (Action) VectAction.get(i);
			SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ssSSS");
			SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
			String dateString = spf.format(action.getStartTime());
			pLoggedMsg.add("[" + dateString + "]  : " + action.getActionName());

			pLoggedMsg.add(Chunk.NEWLINE);

		}

		Paragraph pLastLogguedLines = new Paragraph();
		int startIndex = VectAction.size() > 5 ? VectAction.size() - 5 : 0;
		for (int i = startIndex; i < VectAction.size(); i++) {
			Action action = (Action) VectAction.get(i);
			// SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ssSSS");
			SimpleDateFormat spf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
			String dateString = spf.format(action.getStartTime());
			pLastLogguedLines.add("[" + dateString + "]  : " + action.getActionName());

			pLastLogguedLines.add(Chunk.NEWLINE);

		}

		document.addTitle("REPORT");
		document.addCreationDate();

		HeaderFooter headerPage = new HeaderFooter(new Phrase("Execution report"),
				false);
		HeaderFooter footerPage = new HeaderFooter(new Phrase(" - "), new Phrase(
				" - "));
		headerPage.setAlignment(Element.ALIGN_CENTER);
		footerPage.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(headerPage);
		document.setFooter(footerPage);

		// Chapter 1 : Summary
		// Section 1 : Informations

		Chunk c = new Chunk("Summary");
		c.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c.setFont(FONT_PAR_TITLE);

		Paragraph title1 = new Paragraph(c);
		title1.setAlignment("CENTER");
		title1.setLeading(20);
		Chapter chapter1 = new Chapter(title1, 1);
		chapter1.setNumberDepth(0);

		Paragraph title11 = new Paragraph("Informations");
		Section section1 = chapter1.addSection(title11);
		Paragraph pSum = new Paragraph();
		pSum.add("Author : " + author);
		pSum.add(Chunk.NEWLINE);
		pSum.add("Group : " + group);
		pSum.add(Chunk.NEWLINE);
		pSum.add("Script : " + script);
		pSum.add(Chunk.NEWLINE);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"MMM d, yyyy - hh:mm aaa");
		String dateString = formatter.format(endTime);
		pSum.add("Date : " + dateString);
		pSum.add(Chunk.NEWLINE);
		pSum.setIndentationLeft(20);
		section1.add(pSum);
		section1.add(new Paragraph(Chunk.NEXTPAGE));

		Chunk c11 = new Chunk("Executive summary");
		c11.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c11.setFont(FONT_PAR_TITLE);
		Paragraph title12 = new Paragraph(c11);
		Section section12 = chapter1.addSection(title12);
		Paragraph pExecSum = new Paragraph();
		// pExecSum.add("Value : ");
		// pExecSum.add(Chunk.NEWLINE);
		// pExecSum.add(table);
		pExecSum.add(Chunk.NEWLINE);
		pExecSum.add("Last logged lines : ");
		pExecSum.add(Chunk.NEWLINE);
		pExecSum.add(pLastLogguedLines);
		pExecSum.add(Chunk.NEWLINE);
		pExecSum.setIndentationLeft(20);

		section12.add(pExecSum);

		try {
			document.add(chapter1);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		// Chapter 2 : Log information
		Chunk c2 = new Chunk("Log information");
		c2.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c2.setFont(FONT_PAR_TITLE);
		Paragraph title2 = new Paragraph(c2);
		title2.setAlignment("CENTER");
		title2.setLeading(20);
		Chapter chapter2 = new Chapter(title2, 1);
		chapter2.setNumberDepth(0);

		Section section2 = chapter2.addSection("Log");
		// Add log information
		section2.add(pLoggedMsg);
		section2.add(Chunk.NEWLINE);

		// section2.add(table);
		try {
			document.add(chapter2);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		if (isParseErrorHappened) {
			document.close();
			return;
		}

		// Add generated pictures
		Chunk c3 = new Chunk("Graphics");
		c3.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c3.setFont(FONT_PAR_TITLE);
		Paragraph p3 = new Paragraph(c3);
		p3.setAlignment("CENTER");
		p3.setLeading(20);
		Chapter chapter3 = new Chapter(p3, 1);
		chapter3.setNumberDepth(0);

		// add current graph
		Map<String, PlotList> mapint = dl.getMapint();
		Set<String> cles = mapint.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()) {
			String cle = (String) it.next();
			Paragraph pCPUimg = new Paragraph(cle);
			Section section31 = chapter3.addSection(pCPUimg);
			if (new File(dl.getPNGpath(cle)).exists()) {
				Image jpg1 = null;
				try {
					jpg1 = Image.getInstance(dl.getPNGpath(cle));
				} catch (BadElementException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (jpg1 == null)
				{
					Logger.getLogger(this.getClass())
							.warn("Error when Creating image jpg1 is null");
					return;
				}
				// jpg1.setRotationDegrees(270);
				jpg1.scalePercent(75);
				jpg1.setAlignment(Element.ALIGN_CENTER);
				section31.add(jpg1);
			}
			section31.add(new Paragraph(Chunk.NEXTPAGE));
		}

		try {
			document.add(chapter3);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		document.close();
		createHTMLFileactionlog(actionlog, dl);
	}

}
