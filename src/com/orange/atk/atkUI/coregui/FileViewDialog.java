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
 * File Name   : FileViewDialog.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import com.orange.atk.atkUI.corecli.Alert;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.corecli.utils.Out;
import com.orange.atk.atkUI.corecli.utils.PdfUtilities;
import com.orange.atk.atkUI.coregui.utils.IconResizer;
import com.orange.atk.platform.Platform;

/**
 * A dialog to display files.
 * @author apenault
 *
 */
public class FileViewDialog extends /*JDialog*/JFrame {

	private static final long serialVersionUID = 1L;

	static public final int JAD = 1;
	static public final int JAR = 2;
	static public final int REPORT = 3;
	static public final int LOG = 4;
	static public final int FILE = 5;
	public int fileType = REPORT;

	Frame owner;
	JEditorPane editorPane;
	String fileURI;

	boolean loadError = false;

	private String login = null;
	private String password = null;
	private String user_agent = null;


	public FileViewDialog(Frame owner, String uri, int fileType){
		this(owner, uri, fileType, null, null, null);
	}

	public FileViewDialog(Frame owner, String uri, int fileType, String login, String password, String user_agent){
		//super(CoreGUI.frame); // to have the same icon than the CoreGUI
		//super(owner);
		setIconImage(owner.getIconImage());
		this.owner = owner;
		this.login = login;
		this.password = password;
		this.user_agent = user_agent;
		initFileViewDialog(uri, fileType);
	}

	private void initFileViewDialog(String uri, int fileType) {
		this.fileURI = uri;
		this.fileType = fileType;

		FileViewListener listener = new FileViewListener();
		JMenuItem itemQuit = new JMenuItem("Close");
		itemQuit.setToolTipText("Close this dialog.");
		itemQuit.setActionCommand("quit");
		itemQuit.setIcon(IconResizer.resize16x16(new ImageIcon(CoreGUIPlugin.getIconURL("tango/exit.png"))));
		itemQuit.addActionListener(listener);

		JMenu menuFile = new JMenu("File");

		switch (fileType) {
		case REPORT:
			this.setTitle("Report - " + uri);
			JMenuItem itemExportReportHTML = new JMenuItem("Export report as HTML...");
			itemExportReportHTML.setToolTipText("Export this report as HTML.");
			itemExportReportHTML.setActionCommand("exportReportHTML");
			itemExportReportHTML.setIcon(IconResizer.resize16x16(new ImageIcon(CoreGUIPlugin.getIconURL("tango/html.png"))));
			itemExportReportHTML.addActionListener(listener);
			menuFile.add(itemExportReportHTML);
			JMenuItem itemExportReportPDF = new JMenuItem("Export report as PDF...");
			itemExportReportPDF.setToolTipText("Export this report as PDF.");
			itemExportReportPDF.setActionCommand("exportReportPDF");
			itemExportReportPDF.addActionListener(listener);
			itemExportReportPDF.setIcon(IconResizer.resize16x16(new ImageIcon(CoreGUIPlugin.getIconURL("tango/pdf.png"))));
			menuFile.add(itemExportReportPDF);
			menuFile.add(new JSeparator());
			try {
				fileURI = new File(fileURI).toURL().toString();
			} catch (MalformedURLException e1) {
				showError("URL not valid : "+fileURI);
			}
			editorPane = createEditorPaneHTML();
			break;
		case JAD:
			editorPane = createEditorPaneTXT();
			this.setTitle("JAD file - " + uri);
			break;
		case JAR:
			editorPane = createEditorPaneTXT();
			this.setTitle("JAR manifest - " + uri);
			break;
		case LOG:
			editorPane = createEditorPaneTXT();
			this.setTitle("Log file - " + uri);
			JMenuItem itemRefresh = new JMenuItem("Refresh");
			itemRefresh.setToolTipText("Reload the file.");
			itemRefresh.setActionCommand("refresh");
			itemRefresh.addActionListener(listener);
			menuFile.add(itemRefresh);
			JMenuItem itemSaveAs = new JMenuItem("Save log as...");
			itemSaveAs.setToolTipText("Save this log as...");
			itemSaveAs.setActionCommand("saveLogAs");
			itemSaveAs.setIcon(IconResizer.resize16x16(new ImageIcon(CoreGUIPlugin.getIconURL("tango/txt2.png"))));
			itemSaveAs.addActionListener(listener);
			menuFile.add(itemSaveAs);

			break;
		case FILE:
			editorPane = createEditorPaneTXT();
			this.setTitle("File - " + uri);
			break;
		default :
			break;
		}

		menuFile.add(itemQuit);

		JScrollPane scrollPane = new JScrollPane(editorPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(CoreGUIPlugin.mainFrame.getWidth() * 4 / 5,
				CoreGUIPlugin.mainFrame.getHeight() * 2 / 3));
		editorPane.setEditable(false);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileViewDialog.this.dispose();
			}
		});
		close.grabFocus();

		getRootPane().setDefaultButton(close);
		Container contentPaneFrame = this.getContentPane();

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuFile);

		contentPaneFrame.setLayout(new BorderLayout());
		contentPaneFrame.add(menuBar, BorderLayout.NORTH);
		contentPaneFrame.add(scrollPane, BorderLayout.CENTER);
		contentPaneFrame.add(close, BorderLayout.SOUTH);
		setLocationRelativeTo(CoreGUIPlugin.mainFrame);
		this.pack();
		close.requestFocusInWindow();

		if (!loadError){
			int dec_x = (CoreGUIPlugin.mainFrame.getWidth()-this.getWidth())/2;
			int dec_y = (CoreGUIPlugin.mainFrame.getHeight()-this.getHeight())/2;
			this.setLocation(CoreGUIPlugin.mainFrame.getLocationX()+dec_x,
							 CoreGUIPlugin.mainFrame.getLocationY()+dec_y);

			this.setVisible(true);
		}
	}

	private JEditorPane createEditorPaneTXT(){
		JEditorPane editorPane = new JEditorPane();
		Reader reader = null;
		switch (fileType) {
		case JAD:
			File jadFile = null;
			try {
				jadFile = Configuration.fileResolver.getFile(fileURI, "matos", ".jad", login, password, user_agent);
			} catch (Alert e) {
				Out.log.println(e.getMessage());
				//e.printStackTrace(Out.log);
				showError(e.getMessage());
				return editorPane;
			}
			if (jadFile!=null && jadFile.exists()) {
				try {
					reader = new FileReader(jadFile);
				} catch (FileNotFoundException e) {
					showError("The specified JAD file can't be found.");
				}
			} else {
				showError("The specified JAD file can't be found.");
			}
			break;
		case JAR:
			File jarFile = null;
			try {
				jarFile = Configuration.fileResolver.getFile(fileURI, "matos", ".jar", login, password, user_agent);
			} catch (Alert e) {
				Out.log.println(e.getMessage());
				//e.printStackTrace(Out.log);
				showError(e.getMessage());
				return editorPane;
			}
			if (jarFile != null && jarFile.exists()) {
				ZipFile zf=null;
				ZipEntry manifest = null;
				try {
					zf = new ZipFile(jarFile);
				} catch (Exception e) {
					showError("The specified JAR file can't be found");
				}
				if (zf != null) {
					manifest = zf.getEntry("META-INF/MANIFEST.MF");
				}
				if (manifest == null) {
					showError("No Manifest found in JAR file (looking for META-INF/MANIFEST.MF).");
				} else {
					InputStream is = null;
					try {
						is = zf.getInputStream(manifest);
					} catch (IOException e) {
						showError("The JAR manifest can't be read.");
					}
					reader = new InputStreamReader(is);
				}
			} else {
				showError("The specified JAR file can't be found.");
			}
			break;
		case LOG:
			File logFile = new File(fileURI);
			if (logFile!=null && logFile.exists()) {
				try {
					reader = new FileReader(logFile);
				} catch (FileNotFoundException e){
					showError("The log file can't be found.");
				}
			} else {
				showError("The log file can't be found.");
			}
			break;
		case REPORT:
			break;
		default:
			File file = new File(fileURI);
			if (file!=null && file.exists()) {
				try {
					reader = new FileReader(file);
				} catch (FileNotFoundException e){
					showError("The log file can't be found.");
				}
			} else {
				showError("The log file can't be found.");
			}
			break;
		}

		if (reader !=null) {
			try {
				editorPane.read(reader, null);
			} catch (IOException e) {
				showError("The specified file can't be read");
			}
		}
		return editorPane;
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(
				owner,
				msg,
				"Error !",
				JOptionPane.ERROR_MESSAGE);
		loadError = true;
	}

	private JEditorPane createEditorPaneHTML() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		if (fileURI != null && !fileURI.equals("")){
			try {
				// 1. patch stylesheet
				File tmp = new File( Platform.TMP_DIR, "tmpreport.html");
				String filePath = fileURI.substring("file:".length());
				FileUtilities.copyTextFile( new File(filePath), tmp);
				FileUtilities.copyHTMLFile( tmp, new File(filePath), new File(Configuration.getProperty(Configuration.CSS)).toURI().toString() /*System.getProperty("LIB") + File.separator + Configuration.styleSheetName*/);
				// 1.1 resolve 'orange' color since JEditorPane is not able to handle it by it's name
				FileUtilities.copyTextFile( new File(filePath), tmp);
				FileUtilities.resolveHTMLColor(tmp, new File(filePath), "orange", "#FF6A00");
				// 2. display report
				editorPane.setPage(fileURI);
			}catch (IOException ioe){
				showError("Attempt to read a bad URL: "+fileURI);
			} catch (Exception e) {
				e.printStackTrace(Out.log);
			}
		}
		return editorPane;
	}

	private void refresh() {
		Point loc = this.getLocation();
		dispose();
		FileViewDialog fvd = new FileViewDialog(owner, fileURI, fileType, login, password, user_agent);
		fvd.setLocation(loc);
	}
	/**
	 * Command listener that treats the commands from the menu (fixed part of it).
	 */
	private class FileViewListener extends WindowAdapter implements ActionListener{

		//-- WindowListener method
		public void windowClosing(WindowEvent e) {
			dispose();
		}

		//-- ActionListener method
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command == "quit"){
				dispose();
			}else if (command == "refresh") {
				refresh();
			}else if (command == "saveLogAs") {
				JFileChooser fileChooser = new JFileChooser();
				Calendar now = Calendar.getInstance();
				String now_str = now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+
						"-"+now.get(Calendar.DAY_OF_MONTH)+"_"+now.get(Calendar.HOUR_OF_DAY)+
						"-"+now.get(Calendar.MINUTE)+"-"+now.get(Calendar.SECOND);
				fileChooser.setSelectedFile(new File("log_"+now_str+".txt"));
				fileChooser.setFileFilter(new FileUtilities.Filter("TXT [*.txt]", ".txt"));

				int returnVal =  fileChooser.showDialog(FileViewDialog.this, "Save log file as...");
				String targetFileName = "";
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					targetFileName = fileChooser.getSelectedFile().getAbsolutePath();
					targetFileName = FileUtilities.verifyExtension(targetFileName, ".txt");
					if (targetFileName==null || targetFileName.equals("")){
						JOptionPane.showMessageDialog(
								FileViewDialog.this,
								"You must indicate the file name",
								"Error !",
								JOptionPane.ERROR_MESSAGE);
					} else {
						File f = new File(targetFileName);
						if (f.exists()) {
							//WARNING : selected file already exists !
							int res = JOptionPane.showConfirmDialog(
									FileViewDialog.this,
									targetFileName + " already exists.\n"
									+ " Do you want to overwrite ?",
									fileChooser.getDialogTitle(),
									JOptionPane.YES_NO_OPTION);
							if (res==(JOptionPane.NO_OPTION) || (res==JOptionPane.CLOSED_OPTION)) {
								return;
							}
						}

						try {
							FileUtilities.copyTextFile(new File(fileURI),f);
						} catch (Exception ex) {
							FileViewDialog.this.showError(ex.getMessage());
							ex.printStackTrace(Out.log);
						}

					}
				}
			} else if (command == "exportReportHTML") {
				JFileChooser fileChooser = new JFileChooser();
				if (fileURI.lastIndexOf(File.separator)!=-1 && fileURI.lastIndexOf("html")>fileURI.lastIndexOf(File.separator)) {
					String defaultHTMLName = fileURI.substring(fileURI.lastIndexOf(File.separator)+1, fileURI.lastIndexOf("html")+4);
					fileChooser.setSelectedFile(new File(defaultHTMLName));
				}
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setFileFilter(new FileUtilities.Filter("HTML [*.html]", ".html"));
				int returnVal =  fileChooser.showDialog(FileViewDialog.this, "Export in HTML");
				String targetFileName = "";
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					targetFileName = fileChooser.getSelectedFile().getAbsolutePath();
					targetFileName = FileUtilities.verifyExtension(targetFileName, ".html");
					if (targetFileName==null || targetFileName.equals("")){
						JOptionPane.showMessageDialog(
								FileViewDialog.this,
								"You must indicate the file name",
								"Error !",
								JOptionPane.ERROR_MESSAGE);
					} else {
						File f = new File(targetFileName);
						if (f.exists()) {
							//WARNING : selected file already exists !
							int res = JOptionPane.showConfirmDialog(
									FileViewDialog.this,
									targetFileName + " already exists.\n"
									+ " Do you want to overwrite ?",
									fileChooser.getDialogTitle(),
									JOptionPane.YES_NO_OPTION);
							if (res==(JOptionPane.NO_OPTION) || (res==JOptionPane.CLOSED_OPTION)) {
								return;
							}
						}

						try {
							String cssPath = Configuration.getProperty(Configuration.CSS);
							String cssFileName = cssPath.substring(cssPath.lastIndexOf("/")+1);
							String cssFileName_base = cssFileName.substring(0, cssFileName.indexOf('.'));
							// 1. copy the style sheet in target directory
							String dest_styleFileName = fileChooser.getSelectedFile().getParent() + File.separator + cssFileName;
							File target = new File(dest_styleFileName);
							int i=1;
							while (target.exists()){
								dest_styleFileName = fileChooser.getSelectedFile().getParent() + File.separator + cssFileName_base+i+".css";
						
								target = new File(dest_styleFileName);
								i++;
							}

							File matos_styleFile = new File(cssPath);
							File destCssFile = new File(dest_styleFileName);
							FileUtilities.copyTextFile(matos_styleFile, destCssFile);

							// 2. copy html report in dest. file, with on-the-fly css style sheet patch
							File htmlFile = new File( fileURI.substring( "file:".length()) );
							FileUtilities.copyHTMLFilePrettyPrint(htmlFile, new File(targetFileName), dest_styleFileName.substring(dest_styleFileName.lastIndexOf('/')+1));
						} catch (Exception ex) {
							FileViewDialog.this.showError(ex.getMessage());
							ex.printStackTrace(Out.log);
						}
					}
				}
			}else if (command == "exportReportPDF"){
				JFileChooser fileChooser = new JFileChooser();
				if (fileURI.lastIndexOf(File.separator)!=-1 && fileURI.lastIndexOf("html")>fileURI.lastIndexOf(File.separator)) {
					String defaultPDFName = fileURI.substring(fileURI.lastIndexOf(File.separator)+1, fileURI.lastIndexOf("html")-1) + ".pdf";
					fileChooser.setSelectedFile(new File(defaultPDFName));
				}
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setFileFilter(new FileUtilities.Filter("PDF [*.pdf]", ".pdf"));
				int returnVal =  fileChooser.showDialog(FileViewDialog.this, "Export in PDF");
				String targetFileName = "";
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					targetFileName = fileChooser.getSelectedFile().getAbsolutePath();
					targetFileName = FileUtilities.verifyExtension(targetFileName, ".pdf");
					if (targetFileName==null || targetFileName.equals("")) {
						JOptionPane.showMessageDialog(
								FileViewDialog.this,
								"You must indicate the file name",
								"Error !",
								JOptionPane.ERROR_MESSAGE);
					} else {
						File f = new File(targetFileName);
						if (f.exists()) {
							//WARNING : selected file already exists !
							int res = JOptionPane.showConfirmDialog(
									FileViewDialog.this,
									targetFileName + " already exists.\n"
									+ " Do you want to overwrite ?",
									fileChooser.getDialogTitle(),
									JOptionPane.YES_NO_OPTION);
							if (res==(JOptionPane.NO_OPTION) || (res==JOptionPane.CLOSED_OPTION)) {
								return;
							}
						}

						final String pdfFileName = targetFileName;
						new Thread(new Runnable() {

							public void run() {
								PdfUtilities pdfUtil = new PdfUtilities();
								boolean error = false;
								try {
									pdfUtil.convertHTMLTOPDF(fileURI, pdfFileName);
//								pdfUtil.addWatermark( targetFileName, "/home/moteauni/tmp.pdf", "/home/moteauni/log_orange_41x41.gif", 10, 730);
//								pdfUtil.addText( "/home/moteauni/tmp.pdf", "/home/moteauni/test.pdf", "PASSED", 60, 650, 45, 1);
//								pdfUtil.addTemplate( targetFileName, "/home/moteauni/template.pdf");
								} catch (Alert a) {
									error = true;
								}

								if (error) {
									JOptionPane.showMessageDialog(
											FileViewDialog.this,
											"The PDF file has not been generated. See log file for details.",
											"Error !",
											JOptionPane.ERROR_MESSAGE);
								}
							}

						}).start();

					}
				}
			}
		}
	}
}
