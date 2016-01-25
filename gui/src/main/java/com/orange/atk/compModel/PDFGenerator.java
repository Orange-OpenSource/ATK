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
 * Created     : 04/06/2009
 * Author(s)   : France Télécom
 */
package com.orange.atk.compModel;
//package interpreter.logger.DocumentsGenerator;


//import interpreter.Measurement.PlotList;
//import interpreter.logger.Log.DocumentLogger;
//import interpreter.logger.Log.Message;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

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
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class creates a report stored in pdf format. Informations, measurements
 * graphs and tables are saved.
 */

public class PDFGenerator /*implements DocumentGenerator*/ {
	private static final Font FONT_PAR_TITLE = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.WINANSI,
			BaseFont.NOT_EMBEDDED, 16);

	private static final Color ORANGE_COLOR = new Color(0xFF, 0x66, 0x00);

	private OutputStream outputStream;

	private String author;
	private String group;
	private String script;
	//private boolean isTableEnabled;

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
	 * @param isTableEnabled indicates if you want measurements data
	 */
	public PDFGenerator(OutputStream outputStream, String dir, String author,
			String group, String script, boolean isTableEnabled) {
		this.outputStream = outputStream;
		this.author = author;
		this.group = group;
		this.script = script;
		//this.isTableEnabled = isTableEnabled;
	}

	/**
	 * @see com.orange.atk.results.logger.documentGenerator.DocumentGenerator#dumpInStream(boolean, interpreter.logger.DocumentLogger)
	 */

	public void dumpInStream(boolean isParseErrorHappened, /*DocumentLogger dl,*/ org.w3c.dom.Document xmlDoc, Model model3) {
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
		document.addTitle("REPORT");
		document.addCreationDate();

		HeaderFooter headerPage = new HeaderFooter(new Phrase("ScreenShot report"),
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
		pSum.add("Reference directory: " + model3.getRefDirectory());
		pSum.add(Chunk.NEWLINE);
		pSum.add("Test directory: " + model3.getTestDirectory());
		pSum.add(Chunk.NEWLINE);
		SimpleDateFormat formatter = new SimpleDateFormat(
		"MMMMM dd, yyyy - hh:mm aaa");
		String dateString = formatter.format(endTime);
		pSum.add("Date : " + dateString);
		pSum.add(Chunk.NEWLINE);
		pSum.add(Chunk.NEWLINE);
		if(model3.getNbFail()>0){
			pSum.add(model3.getNbFail()+"/"+model3.getNbImages()+" images didn't succeed the test.");
		}else{
			pSum.add("All images ("+model3.getNbImages()+") have succeed this comparaison.");
		}
		pSum.add(Chunk.NEWLINE);
		pSum.setIndentationLeft(20);
		section1.add(pSum);
		section1.add(new Paragraph(Chunk.NEXTPAGE));

		try {
			document.add(chapter1);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		if (isParseErrorHappened) {
			document.close();
			return;
		}

		// Add generated pictures
		Chunk c3 = new Chunk("ScreenShots");
		c3.setBackground(ORANGE_COLOR, 200, 3f, 200f, 3f);
		c3.setFont(FONT_PAR_TITLE);
		Paragraph p3 = new Paragraph(c3);
		p3.setAlignment("CENTER");
		p3.setLeading(20);
		Chapter chapter3 = new Chapter(p3, 1);
		chapter3.setNumberDepth(0);


		NodeList imgs = xmlDoc.getElementsByTagName("img");
		for (int i =0; i<imgs.getLength();  i++){
			org.w3c.dom.Element eImg=(org.w3c.dom.Element) imgs.item(i);
			if (eImg.getElementsByTagName("Pass").item(0).getTextContent().equals(Model.FAIL)){

				org.w3c.dom.Element eRef=(org.w3c.dom.Element) eImg.getElementsByTagName("Ref").item(0);
				org.w3c.dom.Element eTest=(org.w3c.dom.Element) eImg.getElementsByTagName("Test").item(0);
				Paragraph pScreen= new Paragraph(eTest.getAttributes().getNamedItem("name").getNodeValue()+" "+eImg.getElementsByTagName("Pass").item(0).getTextContent());
				Section section31 = chapter3.addSection(pScreen);
				PdfPTable scTable = null;
				scTable = new PdfPTable(2);
				scTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				scTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
				scTable.getDefaultCell().setPadding(5);
				if (new File (eRef.getTextContent()).exists()){
					Image refImg;
					try {
						refImg = Image.getInstance(eRef.getTextContent());
						refImg.scaleToFit(310,260);
						refImg.setAlignment(Element.ALIGN_BASELINE);
						scTable.addCell(refImg);
					} catch (BadElementException e) {
						e.printStackTrace();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (DOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				if (new File (eTest.getTextContent()).exists()){
					Image testImg;
					try {
						BufferedImage bi=ImageIO.read(new File(eTest.getTextContent()));
						Graphics2D g2d=(Graphics2D) bi.getGraphics();
						g2d.setStroke(new BasicStroke(1.5f));
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));

						Boolean2D dif=model3.getCouplesComparaison().get(i).getDifWithMask();
						//					if (squarable){
						g2d.setColor(Color.red);
						int w=bi.getWidth();
						int h=bi.getHeight();
						//TODO: Gurvan, Maybe we need to have the list of masks in the report?
						Mask mask = model3.getCouplesComparaison().get(i).getMaskSum();
						//g2d.drawRect(0, 0, mask.getWidth()*2*sampleWidth, mask.getHeight()*2*sampleHeight);
						for (int x=0; x<mask.getWidth(); x++){

							for (int y=0; y<mask.getHeight(); y++){

								if (mask.getCell(x, y)){
									g2d.setColor(Color.blue);
									////Logger.getLogger(this.getClass() ).debug("grise");
									g2d.fillRect(x*2*Mask.getCELL_HALF_SIZE(),y*2*Mask.getCELL_HALF_SIZE(),2*Mask.getCELL_HALF_SIZE(),2*Mask.getCELL_HALF_SIZE());
								}


								if( !dif.get(x, y)){
									g2d.setColor(Color.green);
									g2d.fillRect(x*2*Mask.getCELL_HALF_SIZE(),y*2*Mask.getCELL_HALF_SIZE(),2*Mask.getCELL_HALF_SIZE(),2*Mask.getCELL_HALF_SIZE());
								}
							}
						}
						testImg = Image.getInstance(bi,null);
						testImg.scaleToFit(310,260);
						testImg.setAlignment(Element.ALIGN_BASELINE);
						scTable.addCell(testImg);
						section31.add(Chunk.NEWLINE);
						section31.add(scTable);
					} catch (BadElementException e) {
						e.printStackTrace();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (DOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				section31.add(new Paragraph("Reference screenshot description : "+eImg.getElementsByTagName("RefDescription").item(0).getTextContent()));
				section31.add(Chunk.NEWLINE);
				
				section31.add(new Paragraph("Mask : "+eImg.getElementsByTagName("Mask").item(0).getTextContent()));
				section31.add(Chunk.NEWLINE);
	
				section31.add(new Paragraph("Comment : "+eImg.getElementsByTagName("Comment").item(0).getTextContent()));
				section31.add(new Paragraph(Chunk.NEXTPAGE));

			}
		}
		try {
			document.add(chapter3);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.newPage();

		// step 5: we close the document
		document.close();
	}
}
