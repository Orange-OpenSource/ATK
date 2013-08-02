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
 * File Name   : JatkCheckListTable.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiScript;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.anaScript.JatkCampaign;
import com.orange.atk.atkUI.anaScript.JatkStep;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.Step.Verdict;
import com.orange.atk.atkUI.corecli.StepAnalysisResult;
import com.orange.atk.atkUI.corecli.utils.StringUtilities;
import com.orange.atk.atkUI.coregui.CheckListTable;
import com.orange.atk.atkUI.coregui.CheckListTableModel;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.JATKcomboBoxListener;
import com.orange.atk.atkUI.coregui.actions.MatosAction;
import com.orange.atk.atkUI.coregui.tasks.LoadCheckListTask;
import com.orange.atk.atkUI.guiScript.actions.JatkGUIAction;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class JatkCheckListTable extends CheckListTable {

	private static final int TOOLTIP_DISMISS_AFTER = 10000; // 10sec

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// First columns defined in CheckListTable
	static final int COLUMN_COMMENTS = COLUMN_VERDICT + 1;
	static final int COLUMN_SCREENSHOT = COLUMN_COMMENTS + 1;

	private static final int NUMBER_OF_COLUMN = COLUMN_SCREENSHOT;

	private Object[] longValues = {"1000", "ALongWordForAFlashFile.swf", "Config file", "*",
			"Passed***", "A long comment", "Analyse"};

	private boolean completeView = false;

	// -- Table management --
	private TableColumn screenShotsColumn;
	private TableColumn commentsColumn;

	/**
	 * Builds and initialize a new CheckList Table for Flash content.
	 * 
	 */
	@SuppressWarnings("serial")
	public JatkCheckListTable() {
		super();
		campaign = new JatkCampaign();

		ToolTipManager.sharedInstance().setDismissDelay(TOOLTIP_DISMISS_AFTER);

		model = new FlashCheckListTableModel();
		model.setLongValues(longValues);
		table = new JTable(model) {
			public boolean getScrollableTracksViewportHeight() {
				if (getParent() instanceof JViewport) {
					return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
				}
				return false;
			}
		};

		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// On met en place le D&D sur la JTable
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setDragEnabled(true);
		table.setTransferHandler(new MyTransfertHandler());

		table.setPreferredScrollableViewportSize(new Dimension(500, 200));
		tablePane = new JScrollPane(table);

		model.addColumn("#");
		model.addColumn("Test FILE");
		model.addColumn("Monitoring Config");
		model.addColumn("M");
		model.addColumn("Verdict");
		model.addColumn("Comments");
		model.addColumn("ScreenShots Comparison");

		constructTable();

		// Set up column sizes.
		model.initColumnSizes(model, table, NUMBER_OF_COLUMN);

		setRenderer();

		table.setRowHeight(25);

		// table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				CoreGUIPlugin.mainFrame.updateButtons();
			}
		});

		givePopupMenuToTable(table);

		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, tablePane);
		this.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

	}

	/**
	 * Associates a popup menu to a table.
	 * 
	 * @param table
	 *            the target table
	 */
	private void givePopupMenuToTable(JTable table) {
		JPopupMenu popUp = createTablePopUp();
		table.addMouseListener(new JatkMouseListener(popUp, this));
	}

	/**
	 * Creates the popup menu.
	 * 
	 * @return the popup menu
	 */
	private JPopupMenu createTablePopUp() {
		JPopupMenu popup = new JPopupMenu();

		popup.add(JatkGUIAction.ANALYSESELECTEDTASKS.getAsMenuItem("Launch selection"));
		popup.add(MatosAction.VIEWANALYZER.getAsMenuItem("Analyzer"));
		popup.add(MatosAction.VIEWREPORT.getAsMenuItem("Open latest report"));
		popup.add(JatkGUIAction.SETSCREENSHOTREFERENCEDIR
				.getAsMenuItem("Set Reference Screenshots"));
		popup.addSeparator();
		popup.add(MatosAction.COPY.getAsMenuItem("Copy"));
		popup.add(MatosAction.PASTE.getAsMenuItem("Paste under"));
		popup.add(MatosAction.REMOVE.getAsMenuItem("Remove"));
		popup.addSeparator();
		// popup.add(MatosAction.CONFIRMVERDICT.getAsMenuItem("Confirm the verdict"));
		// popup.add(MatosAction.MODIFYVERDICT.getAsMenuItem("Modify the verdict"));
		popup.addSeparator();
		popup.add(MatosAction.PROPERTIES.getAsMenuItem("Properties..."));

		return popup;
	}

	/**
	 * Associates a renderer to this table.
	 */
	protected void setRenderer() {
		nbStepColumn = table.getColumnModel().getColumn(COLUMN_NBROW);
		GeneralRenderer nbStepRenderer = new GeneralRenderer();
		nbStepColumn.setMinWidth(30);
		nbStepColumn.setMaxWidth(70);
		nbStepColumn.setCellRenderer(nbStepRenderer);

		flashfileColumn = table.getColumnModel().getColumn(COLUMN_TESTNAME);
		GeneralRenderer flashRenderer = new GeneralRenderer();
		flashfileColumn.setCellRenderer(flashRenderer);

		phoneconfigColumn = table.getColumnModel().getColumn(COLUMN_PHONECONFIG);
		phoneconfigColumn.setCellEditor(new DefaultCellEditor(comboBoxPhoneConfig));
		JATKcomboBoxListener comboBoxListener = new JATKcomboBoxListener(comboBoxPhoneConfig, this);
		comboBoxPhoneConfig.addActionListener(comboBoxListener);
		comboBoxPhoneConfig.addMouseListener(comboBoxListener);

		modifiedColumn = table.getColumnModel().getColumn(COLUMN_MODIFIED);
		modifiedColumn.setMinWidth(30);
		modifiedColumn.setMaxWidth(50);
		GeneralRenderer modifiedRenderer = new GeneralRenderer();
		modifiedColumn.setCellRenderer(modifiedRenderer);

		verdictColumn = table.getColumnModel().getColumn(COLUMN_VERDICT);
		GeneralRenderer verdictRenderer = new GeneralRenderer();
		verdictColumn.setCellRenderer(verdictRenderer);

		commentsColumn = table.getColumnModel().getColumn(COLUMN_COMMENTS);
		GeneralRenderer commentsRenderer = new GeneralRenderer();
		commentsColumn.setCellRenderer(commentsRenderer);
		screenShotsColumn = table.getColumnModel().getColumn(COLUMN_SCREENSHOT);
		GeneralRenderer screenShotsRenderer = new GeneralRenderer();
		screenShotsColumn.setCellRenderer(screenShotsRenderer);
	}

	public JComboBox getComboBoxPhoneConfig() {
		return comboBoxPhoneConfig;
	}

	/**
	 * Implémentation De transfertHandler correspondant à la JTable.
	 * 
	 * @author
	 * 
	 */
	static class MyTransfertHandler extends TransferHandler {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int getSourceActions(JComponent c) {
			// TODO Auto-generated method stub
			return TransferHandler.MOVE;
		}

		private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";

		@Override
		protected Transferable createTransferable(JComponent c) {

			// on récupère la donnée qui nous intéresse (c'est a dire
			// l'emplacement de la ligne que l'on veut bouger)
			// Puis on l'enveloppe dans un Objet héritant de transferable. (une
			// StringSelection en l'occurence)
			JTable t = (JTable) c;
			StringSelection s = new StringSelection(String.valueOf(t.getSelectedRow()));
			return s;
		}

		public boolean canImport(TransferHandler.TransferSupport info) {

			// pour ne gérer que le drop et pas le paste
			// if (!info.isDrop()) {
			// return false;
			// }

			if (Campaign.isExecute()) {
				return false;
			}

			// On ne supporte que les string et les file en entree
			if (!((info.isDataFlavorSupported(DataFlavor.stringFlavor)) || (info
					.isDataFlavorSupported(DataFlavor.javaFileListFlavor)))) {
				return false;
			}

			// On recherche l'emplacement du drop
			JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();

			// On ne supporte que les emplacements de drop valides
			return dl.getDropPoint() != null;
		}

		public boolean importData(TransferHandler.TransferSupport info) {
			// dans le cas ou l'on ne pourrait supporter l'import

			if (!canImport(info)) {
				return false;
			}

			Transferable transferable = info.getTransferable();

			DataFlavor uriListFlavor = null;
			try {
				uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
			} catch (ClassNotFoundException e) {
				Logger.getLogger(this.getClass()).error(e);
			}

			try {
				Vector<File> dd_flashPathVect = new Vector<File>();
				// 1. get back files
				if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) { // windows's
																							// way
					List<File> list = (List<File>) transferable
							.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<File> it = list.iterator();
					while (it.hasNext()) {
						File f = it.next();
						String extension = ".tst";

						if (f.getAbsolutePath().endsWith(extension)) {
							dd_flashPathVect.add(new File(f.getAbsolutePath()));
						}
					}

				} else if (transferable.isDataFlavorSupported(uriListFlavor)) {
					String s = (String) transferable.getTransferData(uriListFlavor);
					String[] uris = s.split(System.getProperty("line.separator"));
					for (int i = 0; i < uris.length; i++) {
						if (uris[i].trim().length() > 0) {
							File f = new File(new URI(uris[i].trim()));
							String extension = ".tst";

							if (f.exists() && f.getAbsolutePath().endsWith(extension)) {
								dd_flashPathVect.add(new File(f.getAbsolutePath()));
							}
						}
					}
				} else {
					// On récupère l'emplacement du Drop
					JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();

					// On récupère la ligne de destinatop du drop
					int dstRow = dl.getRow();

					// on récupère l'objet de transfert
					Transferable trans = info.getTransferable();

					// On récupère la donnée utile depuis l'objet de
					// transfert (l'emplacement d'origine de la ligne à
					// bouger)
					try {
						trans.getTransferData(DataFlavor.stringFlavor);
					} catch (UnsupportedFlavorException e) {
						Logger.getLogger(this.getClass()).error(e);
						return false;
					} catch (IOException e) {
						Logger.getLogger(this.getClass()).error(e);
						return false;
					}

					// on effectue les modifications sur la JTable
					JTable table = (JTable) info.getComponent();
					FlashCheckListTableModel m = (FlashCheckListTableModel) table.getModel();

					if (dstRow < 0) {
						dstRow = 0;
					}
					if (dstRow > m.getRowCount() - 1) {
						dstRow = m.getRowCount() - 1;
					}

					SortedSet<Integer> campaignIndexRemovedRows = new TreeSet<Integer>();
					int[] temp = table.getSelectedRows();

					for (int i = 0; i < temp.length; i++) {
						campaignIndexRemovedRows.add(temp[i]);
					}

					m.moveRow(campaignIndexRemovedRows, dstRow);
					return true;
				}

				JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();

				// On récupère la ligne de destinatop du drop
				dl.getRow();

				// 2. add droped files into the check list
				if (dd_flashPathVect.size() > 0) {
					Campaign tmpCamp = new Campaign();
					Iterator<File> itSWF = dd_flashPathVect.iterator();
					while (itSWF.hasNext()) {
						File swfFile = itSWF.next();
						String swfPath = swfFile.getAbsolutePath();
						JatkStep flashStep = new JatkStep(swfPath, swfFile);
						tmpCamp.add(flashStep);
					}
					new LoadCheckListTask(CoreGUIPlugin.mainFrame.statusBar, tmpCamp, -1, false,
							tmpCamp.size());

					return true;
				}

			} catch (UnsupportedFlavorException e) {
				Logger.getLogger(this.getClass()).error(e);
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).error(e);
			} catch (URISyntaxException e) {
				Logger.getLogger(this.getClass()).error(e);
			}

			return false;

		}

	}

	/**
	 * The renderer for this check-list table.
	 */
	public class GeneralRenderer extends JLabel implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ImageIcon[] suitImages;

		/**
		 * Creates the renderer.
		 * 
		 */
		public GeneralRenderer() {
			setOpaque(true);
			suitImages = new ImageIcon[5];
			java.net.URL passedURL = CoreGUIPlugin.getIconURL("tango/apply.png");
			java.net.URL failedURL = CoreGUIPlugin.getIconURL("tango/messagebox_warning.png");
			java.net.URL skippedURL = CoreGUIPlugin.getIconURL("tango/cache.png");
			suitImages[0] = new ImageIcon(passedURL, Step.verdictAsString.get(Verdict.PASSED));
			suitImages[1] = new ImageIcon(failedURL, Step.verdictAsString.get(Verdict.FAILED));
			suitImages[2] = new ImageIcon(skippedURL, Step.verdictAsString.get(Verdict.SKIPPED));
			suitImages[3] = new ImageIcon(failedURL, Step.verdictAsString.get(Verdict.INITFAILED));
			suitImages[4] = new ImageIcon(failedURL, Step.verdictAsString.get(Verdict.TESTFAILED));

		}

		/**
		 * Gets the renderer for each cell of the table.
		 */
		public Component getTableCellRendererComponent(JTable table, Object color,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				this.setBackground(table.getSelectionBackground());
			} else {
				if (column == COLUMN_TESTNAME) {
					this.setBackground(new Color(253, 245, 230));
				} else {
					this.setBackground(table.getBackground());
				}
			}
			try {
				this.setText((String) model.getValueAt(row, column));
				int numRow = Integer.valueOf((String) model.getValueAt(row, COLUMN_NBROW)) - 1;
				if (column == COLUMN_TESTNAME) {
					this.setToolTipText((String) toolTipFlashFile.get(numRow));
				}
				if (column == COLUMN_MODIFIED) {
					this.setToolTipText((String) toolTipModified.get(numRow));
					this.setHorizontalAlignment(JLabel.CENTER);
				}
				if (column == COLUMN_VERDICT) {
					this.setToolTipText((String) toolTipReport.get(numRow));
					Verdict verdict = Verdict.NONE;
					Verdict userVerdict = Verdict.NONE;
					if (numRow < campaign.size()) {
						verdict = ((Step) campaign.get(numRow)).getVerdict();
						userVerdict = ((Step) campaign.get(numRow)).getUserVerdict();
					}
					if (userVerdict != Verdict.NONE) {
						if (userVerdict == Verdict.PASSED) {
							this.setIcon(suitImages[0]);
						} else if (userVerdict == Verdict.FAILED) {
							this.setIcon(suitImages[1]);
						} else if (userVerdict == Verdict.SKIPPED) {
							this.setIcon(suitImages[2]);
						}
					} else {
						if (verdict == Verdict.PASSED) {
							this.setIcon(suitImages[0]);
						} else if (verdict == Verdict.FAILED) {
							this.setIcon(suitImages[1]);
						} else if (verdict == Verdict.SKIPPED) {
							this.setIcon(suitImages[2]);
						} else if (verdict == Verdict.NONE) {
							this.setIcon(null);
						} else {
							this.setIcon(null);
						}
					}
					if (userVerdict == verdict && verdict != Verdict.NONE) {
						this.setBackground(new Color(224, 238, 224));
					} else if (userVerdict != verdict && userVerdict != Verdict.NONE) {
						this.setBackground(new Color(238, 213, 210));
					}
					if (verdict == Verdict.SKIPPED) {
						this.setBackground(new Color(255, 250, 205));
					}
				}
				if (column == COLUMN_COMMENTS) {
					this.setToolTipText((String) model.getValueAt(row, column));

					table.getCellEditor(row, column).addCellEditorListener(
							new CellEditorListener() {
								public void editingCanceled(ChangeEvent e) {
								}
								public void editingStopped(ChangeEvent e) {
									int row = JatkCheckListTable.this.table.getSelectedRow();
									Step c = (Step) campaign.get(row);
									c.setUserComment((String) model
											.getValueAt(row, COLUMN_COMMENTS));
								}
							});
				}
				if (column == COLUMN_SCREENSHOT) {

					this.setToolTipText((String) toolTipReport.get(numRow));
					Verdict screenshotVerdict = Verdict.NONE;
					Verdict verdict = Verdict.NONE;

					if (numRow < campaign.size()) {
						screenshotVerdict = ((Step) campaign.get(numRow)).getScreenshotVerdict();
						verdict = ((Step) campaign.get(numRow)).getVerdict();

					}
					if (screenshotVerdict != Verdict.NONE) {
						if (screenshotVerdict == Verdict.PASSED) {
							this.setIcon(suitImages[0]);
						} else if (screenshotVerdict == Verdict.FAILED) {
							this.setIcon(suitImages[1]);
						} else if (screenshotVerdict == Verdict.SKIPPED) {
							this.setIcon(suitImages[2]);
						}
					} else {
						this.setIcon(new ImageIcon(CoreGUIPlugin
								.getIconURL("tango/camera_icon.png"),
								"Launch ScreenShot Comparison"));

					}

					if (verdict == Verdict.SKIPPED) {
						this.setBackground(new Color(255, 250, 205));
					}
				}
				if (completeView) {
					if (column == COLUMN_COMMENTS) {
						this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
					}
				}
				return this;
			} catch (ArrayIndexOutOfBoundsException ae) {
				// throwed when table is modified (cleared,...) and repainted at
				// same time...
				// when numRow becomes > nb row in table
				Logger.getLogger(this.getClass()).error("error", ae);
			} catch (NumberFormatException nfe) {
				// case numRow retriving get a null (see overwritten method
				// MyTableModel.getValueAt(..))
				Logger.getLogger(this.getClass()).error("error", nfe);
			}
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.CheckListTable#addRow(com.orange.atk.atkUI
	 * .corecli.Step, int, boolean, boolean)
	 */
	@Override
	public void addRow(Step step, int rowNumberInGUI, boolean selectIt, boolean checkPreviousResults) {
		if (!(step instanceof JatkStep)) {
			// Out.log.println("addRow(..): Warning, trying to add a non FlashStep into FlashCheckListTable... step skipped.");
			return;
		}
		JatkStep flashStep = (JatkStep) step;
		String flashURI = flashStep.getFlashFilePath();
		String flashName = "";
		if (flashURI.endsWith("tst")) {
			flashName = StringUtilities.guessName(flashURI, "tst");
		} else {// .sis
			flashName = StringUtilities.guessName(flashURI, "xml");
		}
		String numRowInNumbers = null;
		Vector<String> rowData = new Vector<String>();
		if (rowNumberInGUI == -1) {
			rowNumberInGUI = model.getRowCount();
		}

		if (model.getRowCount() == 0)
			rowNumberInGUI = table.getRowCount();
		numRowInNumbers = Integer.valueOf(rowNumberInGUI + 1).toString();

		int numRowInCampaign = campaign.size();
		rowData.add(numRowInNumbers);// rowNumber

		rowData.add(flashName);
		toolTipFlashFile.add(numRowInCampaign, flashURI);

		if (checkPreviousResults) {
			// Configuration column
			if (null != flashStep.getXmlfilepath()) {
				File configfile = new File(flashStep.getXmlfilepath());
				rowData.add(configfile.getName());
			} else {
				PhoneInterface phone = AutomaticPhoneDetection.getInstance().getDevice();
				String defaultConfigFileName = Configuration.getInstance().getDefaultConfig()
						.get(phone.getClass().getName());
				if (defaultConfigFileName != null) {
					rowData.add(defaultConfigFileName);
					File file = new File(Configuration.defaultPhoneConfigPath
							+ defaultConfigFileName);
					flashStep.setXmlfilepath(file.toString());
				} else
					rowData.add(NOT_SELECTED);
			}

			rowData.add(""); // Modified column
			toolTipModified.add(numRowInCampaign, "");
			rowData.add(""); // Verdict column
			rowData.add(""); // Comments column
			toolTipReport.add(numRowInCampaign, "");

		}

		model.insertRow(rowNumberInGUI, rowData);
		campaign.add(numRowInCampaign, flashStep);
		if (selectIt) {
			selectARow(rowNumberInGUI);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.CheckListTable#clear()
	 */
	@Override
	public void clear() {
		if (table.getRowCount() > 0) { // / if needed ...
			campaign = new JatkCampaign();

			model = new FlashCheckListTableModel();
			model.setLongValues(longValues);

			table.removeAll();
			table.setModel(model);

			model.addColumn("#");
			model.addColumn("Test FILE");
			model.addColumn("Monitoring Config");
			model.addColumn("M");
			model.addColumn("Verdict");
			model.addColumn("Comments");
			model.addColumn("Analyser");

			// Set up column sizes.
			model.initColumnSizes(model, table, NUMBER_OF_COLUMN);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.CheckListTable#updateAllAfterRemoving(java
	 * .util.Vector)
	 */
	@Override
	public void updateAllAfterRemoving(Vector<Integer> campRemovedRows) {
		if (table.getRowCount() == 0) {
			CoreGUIPlugin.mainFrame.setCheckListFileName(null);
			campaign = new JatkCampaign();
			toolTipFlashFile.removeAllElements();
			toolTipModified.removeAllElements();
			toolTipReport.removeAllElements();
		} else {
			updateTableAfterRemoving(campRemovedRows);
			updateCampaign(campRemovedRows);
			updateVector(toolTipFlashFile, campRemovedRows);
			updateVector(toolTipModified, campRemovedRows);
			updateVector(toolTipReport, campRemovedRows);
		}
		model.fireTableDataChanged();
		CoreGUIPlugin.mainFrame.updateButtons();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.CheckListTable#updateStep(com.orange.atk
	 * .atkUI.corecli.Step)
	 */
	@Override
	public void updateStep(Step step) {
		Step flashStep = (Step) step;

		int indexInCampaign = getCampaign().indexOf(step);
		int indexInTable = getIndexInTable(indexInCampaign);
		centerRow(indexInTable);

		Verdict userVerdict = flashStep.getUserVerdict();
		if (userVerdict != Verdict.NONE) {
			if (userVerdict == Verdict.PASSED || userVerdict == Verdict.FAILED) {
				model.setValueAt(Step.verdictAsString.get(userVerdict), indexInTable,
						COLUMN_VERDICT);
				String repPath = flashStep.getOutFilePath();
				toolTipReport.set(indexInCampaign, repPath);
			} else { // verdict is 'skipped'
				if (flashStep.getSkippedMessage() != null
						&& flashStep.getSkippedMessage().length() != 0) {
					model.setValueAt(
							Step.verdictAsString.get(userVerdict) + ": "
									+ flashStep.getSkippedMessage(), indexInTable, COLUMN_VERDICT);
					toolTipReport.set(indexInCampaign, flashStep.getSkippedMessage());
				} else {
					model.setValueAt(Step.verdictAsString.get(userVerdict), indexInTable,
							COLUMN_VERDICT);
				}
			}
		} else {
			Verdict verdict = flashStep.getVerdict();
			if (verdict == Verdict.PASSED || verdict == Verdict.FAILED) {
				model.setValueAt(Step.verdictAsString.get(verdict), indexInTable, COLUMN_VERDICT);
				String repPath = flashStep.getOutFilePath();
				toolTipReport.set(indexInCampaign, repPath);
			} else if (verdict == Verdict.NONE) {
				model.setValueAt(Step.verdictAsString.get(verdict), indexInTable,
						COLUMN_VERDICT);
			} else { // verdict is 'Skipped'
				if (flashStep.getSkippedMessage() != null
						&& flashStep.getSkippedMessage().length() != 0) {
					model.setValueAt(
							Step.verdictAsString.get(verdict) + ": "
									+ flashStep.getSkippedMessage(), indexInTable,
							COLUMN_VERDICT);
					toolTipReport.set(indexInCampaign, flashStep.getSkippedMessage());
				} else {
					model.setValueAt(Step.verdictAsString.get(verdict), indexInTable,
							COLUMN_VERDICT);
				}
			}
		}

		StepAnalysisResult sar = flashStep.getLastAnalysisResult();
		String toolTip = "";
		if (sar != null) {
			toolTip = sar.toHTML(flashStep);
			boolean modified = toolTip.indexOf("red") > 0;
			model.setValueAt(modified ? "M" : "", indexInTable, COLUMN_MODIFIED);
			toolTipModified.set(indexInCampaign, toolTip);
		}

	}

	/**
	 * The model for this table for.
	 */
	private class FlashCheckListTableModel extends CheckListTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int col) {
			if (col == COLUMN_COMMENTS || col == COLUMN_PHONECONFIG) {
				return true;
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		public void moveRow(SortedSet<Integer> campaignIndexRemovedRows, int rowIndexDst) {

			Iterator<Integer> it = campaignIndexRemovedRows.iterator();
			Vector addvect = new Vector();
			int index = 0;
			while (it.hasNext()) {
				int rowIndexSrc = it.next();
				Vector r = (Vector) dataVector.get(rowIndexSrc + index);
				addvect.add(r);
				model.removeRow(rowIndexSrc + index);
				index--;
			}

			// add
			if (rowIndexDst < campaignIndexRemovedRows.first()) {
				for (int i = 0; i < addvect.size(); i++) {
					Vector r = (Vector) addvect.get(i);
					model.insertRow(rowIndexDst + i, r);
				}

			} else {
				for (int i = 0; i < addvect.size(); i++) {
					Vector r = (Vector) addvect.get(i);
					model.insertRow(rowIndexDst - campaignIndexRemovedRows.size() + 1 + i, r);

				}
				// dataVector.addAll(rowIndexDst-campaignIndexRemovedRows.size()+1,addvect);
			}

			// update campaign
			campaign.movesample(campaignIndexRemovedRows, rowIndexDst);
			// order IHM
			reorderrow();
		}

		public void reorderrow() {

			for (int i = 0; i < dataVector.size(); i++) {
				model.setValueAt(String.valueOf(i + 1), i, COLUMN_NBROW);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.CheckListTable#isRowModified(int)
	 */
	@Override
	public boolean isRowModified(int row) {
		String m = (String) model.getValueAt(row, COLUMN_MODIFIED);
		return !m.equals("");
	}

	public Vector<String> getToolTipFlashFile() {
		return toolTipFlashFile;
	}

	public void setToolTipFlashFile(Vector<String> toolTipFlashFile) {
		this.toolTipFlashFile = toolTipFlashFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.CheckListTable#getNumColumnVerdict()
	 */
	@Override
	public int getNumColumnVerdict() {
		return COLUMN_VERDICT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orange.atk.atkUI.coregui.CheckListTable#getValueAt(int)
	 */
	public String getValueAt(int numRow) {
		return (String) model.getValueAt(numRow, COLUMN_NBROW);
	}

	private boolean enabledUserAction = true;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.orange.atk.atkUI.coregui.CheckListTable#enableUserActions(boolean)
	 */
	@Override
	public void enableUserActions(boolean b) {
		enabledUserAction = b;
		table.setEnabled(b);
		tablePane.setEnabled(b);
	}

	public boolean isEnableUserActions() {
		return enabledUserAction;
	}

}
