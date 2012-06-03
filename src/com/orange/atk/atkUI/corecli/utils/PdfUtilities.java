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
 * File Name   : PdfUtilities.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.messaging.MessageHandler;
import org.xml.sax.InputSource;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.platform.Platform;

public class PdfUtilities {

	private File xsltFile;
	private File tmpDir;
	private Logger logger;
	private boolean signed = false;
	private String keystore;
	private String typeKeystore;
	private String passwordKeystore;
	private String aliasCertificate;
	private String pdfEncryptionUserPassword;
	private String pdfEncryptionOwnerPassword;

	/**
	 * Builds a new PdfUtilities
	 */
	public PdfUtilities() {
		String xsltFileName = Configuration.getProperty("xhtml2foXLSTFile");
		xsltFile = new File(xsltFileName);
		tmpDir = new File(Platform.TMP_DIR);
		this.logger = new MatosFOPLogger(MatosFOPLogger.LEVEL_FATAL);
		pdfEncryptionUserPassword = Configuration.getProperty(Configuration.pdfEncryptionUserPassword, "");
		pdfEncryptionOwnerPassword = Configuration.getProperty(Configuration.pdfEncryptionOwnerPassword, "");
		signed = Boolean.valueOf(Configuration.getProperty(Configuration.pdfSignature, "false"));
		if (signed){
			keystore = Configuration.getProperty(Configuration.keystore);
			typeKeystore = Configuration.getProperty(Configuration.typeKeystore, "jks");
			passwordKeystore = Configuration.getProperty(Configuration.passwordKeystore);
			aliasCertificate = Configuration.getProperty(Configuration.aliasCertificate);
		}
	}

	/**
	 * Convert an HTML file into a PDF file
	 * @param htmlFileName
	 * @param pdfFileName
	 * @return false if an error occurs, true otherwise
	 * @throws Alert in case of error during convertion
	 */
	public void convertHTMLTOPDF(String htmlFileName, String pdfFileName) throws Alert { //throws TransformerException, IOException, FOPException, DocumentException{

		// 1. create a temporary xsl:fo file
		File xslfoFile = new File(tmpDir, "xslfo.xml");
		Source xmlSource = new StreamSource(htmlFileName);
		Source xsltSource = new StreamSource(xsltFile);
		Result result = null;

		try {
			result = new StreamResult(new FileOutputStream(xslfoFile));
			TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.setErrorListener( new ErrorListener() {
				public void error(TransformerException exception) throws TransformerException {
					//throw exception;
					Out.log.println("[javax.xml.transform.Transformer Error] " + exception.getMessage());
					exception.printStackTrace(Out.log);
				}
				public void fatalError(TransformerException exception) throws TransformerException {
					//throw exception;
					Out.log.println("[javax.xml.transform.Transformer Fatal Error] " + exception.getMessage());
					exception.printStackTrace(Out.log);
				}

				public void warning(TransformerException exception) throws TransformerException {
					//throw exception;
					Out.log.println("[javax.xml.transform.Transformer Warning] " + exception.getMessage());
					//exception.printStackTrace(Out.log);
				}
			});
			// Note : errors are reported to System.err while ErrorListener is registered
			// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4661371
			trans.transform(xmlSource, result);
			// 2. convert xsl:fo to pdf
			convertFO2PDF(xslfoFile, new File(pdfFileName));
			encryptPDF(pdfFileName);
			if (signed){
				signDocument(pdfFileName);
			}
		} catch (TransformerException e1) {
			Out.log.println("[javax.xml.transform.Transformer Error] : " + e1.getMessage());
			e1.printStackTrace(Out.log);
			throw new Alert(e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace(Out.log);
			throw new Alert(e1.getMessage());
		} catch (FOPException e1) {
			e1.printStackTrace(Out.log);
			throw new Alert(e1.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace(Out.log);
			throw new Alert(e1.getMessage());
		}
	}

	/**
	 * Convert an xsl:fo file into a pdf file
	 * @param fo
	 * @param pdf
	 * @throws IOException
	 * @throws FOPException
	 * @throws DocumentException
	 * @throws Exception
	 */
	private void convertFO2PDF(File fo, File pdf) throws IOException, FOPException{
		//Construct driver
		Driver driver = new Driver();
		//Setup logger
		driver.setLogger(logger);
		MessageHandler.setScreenLogger(logger);
		//Setup Renderer (output format)
		driver.setRenderer(Driver.RENDER_PDF);

//		// PDF Encryption options ---> NEEDS Java 1.4 at compiletime and runtime + a provider supporting RC4
//		// see http://xmlgraphics.apache.org/fop/0.20.5/pdfencryption.html
//		Map rendererOptions = new java.util.HashMap();
//		//rendererOptions.put("ownerPassword", "mypassword");
//		//rendererOptions.put("userPassword", "mypassword");
//		rendererOptions.put("allowCopyContent", "FALSE");
//		rendererOptions.put("allowEditContent", "FALSE");
//		rendererOptions.put("allowEditAnnotations", "FALSE");
//		rendererOptions.put("allowPrint", "TRUE");
//		driver.getRenderer().setOptions(rendererOptions);

		//Setup output
		OutputStream out = new FileOutputStream(pdf);
		try {
			driver.setOutputStream(out);

			//Setup input
			InputStream in = new java.io.FileInputStream(fo);
			try {
				driver.setInputSource(new InputSource(in));
				//Process FO
				driver.run();
			} finally {
				in.close();
			}
		} finally {
			out.close();
		}
	}

	private void signDocument(String pdfFileName){
		try{
			// 1. copy
			File tmpPDFFile = new File(tmpDir, "tmp2PDF.pdf");
			copyFile(new File(pdfFileName), tmpPDFFile);
			// 2. sign
			KeyStore ks = KeyStore.getInstance(typeKeystore);
			FileInputStream fis = new FileInputStream(keystore);
			ks.load(fis, passwordKeystore.toCharArray());
			PrivateKey key = (PrivateKey)ks.getKey(aliasCertificate, passwordKeystore.toCharArray());
			Certificate [] chain = ks.getCertificateChain(aliasCertificate);
			PdfReader reader = new PdfReader(tmpPDFFile.getAbsolutePath());
			FileOutputStream fout = new FileOutputStream(pdfFileName);
			PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');
			PdfSignatureAppearance sap = stp.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			sap.setVisibleSignature(new Rectangle(450, 730, 550, 780), 1, null);
			stp.close();
			fis.close();
		}catch (Exception e){
			e.printStackTrace(Out.log);
		}
	}

	/**
	 * Copy a source file into a destination file.
	 * @param in source file
	 * @param out destination file
	 * @throws IOException
	 */
	private void copyFile(File in, File out) throws IOException{
	    FileInputStream fis  = new FileInputStream(in);
	    FileOutputStream fos = new FileOutputStream(out);
	    byte[] buf = new byte[1024];
	    int i = 0;
	    while((i=fis.read(buf))!=-1) {
	      fos.write(buf, 0, i);
	      }
	    fis.close();
	    fos.close();
	}

	/**
	 * Encrypt (allow only printing permission) the given pdf file with the given passwords.
	 * Encryption must be the latest operation on the file.
	 * @param pdfFileName
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void encryptPDF(String pdfFileName) throws IOException, DocumentException{
		// 1. copy
		File tmpPDFFile = new File(tmpDir, "tmpPDF.pdf");
		copyFile( new File(pdfFileName), tmpPDFFile);
		// 2. encrypt
		PdfReader reader = new PdfReader(tmpPDFFile.getAbsolutePath());
		PdfEncryptor.encrypt(reader, new FileOutputStream(pdfFileName),
				true, pdfEncryptionUserPassword, pdfEncryptionOwnerPassword, PdfWriter.AllowPrinting /*| PdfWriter.AllowCopy*/);
	}

	/**
	 * Adds a logo to the given pdf file.
	 * @param pdfFileName
	 * @param imageFileName
	 * @param x position for image
	 * @param y position for image
	 * @throws Exception
	 */
	public void addWatermark(String pdfFileName, String imageFileName, int x, int y) throws Exception {
		// 1. copy
		File tmpPDFFile = new File(tmpDir, "tmpPDF.pdf");
		copyFile( new File(pdfFileName), tmpPDFFile);
		// 2. add watermark
		// we create a reader for a certain document
		PdfReader reader = new PdfReader(tmpPDFFile.getAbsolutePath());
		int n = reader.getNumberOfPages();
		// we create a stamper that will copy the document to a new file
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdfFileName));
		// adding content to each page
		int i = 0;
		PdfContentByte under;
		Image img = Image.getInstance(imageFileName);
		img.setAbsolutePosition(x, y);
		while (i < n) {
			i++;
			// watermark under the existing page
			under = stamp.getUnderContent(i);
			under.addImage(img);
		}
		// closing PdfStamper will generate the new PDF file
		stamp.close();
	}

	/**
	 * Adds a text in the given pdf file.
	 * @param pdfFileName
	 * @param text
	 * @param x
	 * @param y
	 * @param rotation
	 * @param page
	 * @throws Exception
	 */
	public void addText(String pdfFileName, String text, int x, int y, int rotation, int page) throws Exception {
		// see example on http://itextdocs.lowagie.com/examples/com/lowagie/examples/general/copystamp/AddWatermarkPageNumbers.java
		// 1. copy
		File tmpPDFFile = new File(tmpDir, "tmpPDF.pdf");
		copyFile( new File(pdfFileName), tmpPDFFile);
		// 2. add text
		// we create a reader for a certain document
		PdfReader reader = new PdfReader(tmpPDFFile.getAbsolutePath());
		// we create a stamper that will copy the document to a new file
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdfFileName));
		// adding content to each page
		PdfContentByte over;
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		over = stamp.getOverContent(page);
		over.beginText();
		over.setFontAndSize(bf, 32);
		over.setTextMatrix(30, 30);
		over.setColorFill(java.awt.Color.RED);
		over.showTextAligned(Element.ALIGN_LEFT, text, x, y, rotation);
		over.endText();
		// closing PdfStamper will generate the new PDF file
		stamp.close();
	}

//	public void tmpCreateTemplate() throws Exception {
//		// step 1: creation of a document-object
//		Document document = new Document();
//		try {
//			// step 2 : create a writer that listens to the document
//			//          and directs a PDF-stream to a file
//			PdfWriter.getInstance(document,
//					new FileOutputStream("/home/moteauni/template.pdf"));
//
//			// step 3: open the document
//			document.open();
//
//			document.add(new Paragraph(" "));
//		} catch (DocumentException de) {
//			Logger.getLogger(this.getClass() ).warn(de.getMessage());
//		} catch (IOException ioe) {
//			Logger.getLogger(this.getClass() ).warn(ioe.getMessage());
//		}
//		// close the document
//		document.close();
//
//		addWatermark( "/home/moteauni/template.pdf", "/home/moteauni/log_orange_41x41.gif", 20, 780);
//		addWatermark( "/home/moteauni/template.pdf", "/home/moteauni/ft_verti_petit.gif", 520, 760);
//
//	}

	public void addTemplate(String pdfFileName, String templatePDFFileName) throws Exception {
		// see example on http://itextdocs.lowagie.com/examples/com/lowagie/examples/general/copystamp/AddWatermarkPageNumbers.java
		// 1. copy
		File tmpPDFFile = new File(tmpDir, "tmpPDF.pdf");
		copyFile( new File(pdfFileName), tmpPDFFile);
		// 2. add template on all pages
		// we create a reader for a certain document
		PdfReader reader = new PdfReader(tmpPDFFile.getAbsolutePath());
		// we create a stamper that will copy the document to a new file
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdfFileName));
		// adding content to each page
		int n = reader.getNumberOfPages();
		int i = 0;
        // a reader for the template document
        PdfReader reader2 = new PdfReader(templatePDFFileName);

		PdfContentByte under;
		while (i < n) {
			i++;
			// template under the existing page
			under = stamp.getUnderContent(i);
	        //under.addTemplate(stamp.getImportedPage(reader2, 1), 1, 0, 0, 1, 0, 0);
			under.addTemplate(stamp.getImportedPage(reader2, 1), -10, -50);
		}
		// closing PdfStamper will generate the new PDF file
		stamp.close();

	}

}
