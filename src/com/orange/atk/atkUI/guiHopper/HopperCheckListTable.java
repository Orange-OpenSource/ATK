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
 * File Name   : HopperCheckListTable.java
 *
 * Created     : 28/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.guiHopper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.anaHopper.HopperCampaign;
import com.orange.atk.atkUI.anaHopper.HopperStep;
import com.orange.atk.atkUI.anaHopper.HopperStepAnalysisResult;
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
import com.orange.atk.atkUI.guiHopper.actions.HopperGUIAction;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HopperCheckListTable extends CheckListTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// First columns defined in CheckListTable
	public static final int COLUMN_TIME = COLUMN_VERDICT + 1;
	public static final int COLUMN_THROTTLE = COLUMN_TIME + 1;
	public static final String COLUMN_PARAM_TIME = "Time (sec.)";
	public static final String COLUMN_PARAM_EVENTS = "Nb of events";
	public static final String COLUMN_PARAM_THROTTLE = "Throttle (millisec.)";
	private boolean isNokia = false;

	private Object[] nokiaValues = {"100", "ALongWordForAScriptTestFile.tst", "Configuration", "*",
			"Passed***", "200000"};

	private Object[] otherValues = {"100", "ALongWordForAScriptTestFile.tst", "Configuration", "*",
			"Passed***", "200000", "2000"};

	boolean completeView = false;

	// -- Table management --
	private TableColumn timeColumn;
	private TableColumn throttleColumn;

	static public JMenu submenuLaunchInExternalTool;

	/**
	 * Builds and initialize a new CheckList Table for Flash content.
	 * 
	 */
	public HopperCheckListTable() {
		super();
		JATKcomboBoxListener comboBoxListener = new JATKcomboBoxListener(comboBoxPhoneConfig, this);
		comboBoxPhoneConfig.addActionListener(comboBoxListener);
		comboBoxPhoneConfig.addMouseListener(comboBoxListener);
		initTable();
	}

	@SuppressWarnings("serial")
	private void initTable() {
		campaign = new HopperCampaign();

		ToolTipManager.sharedInstance().setDismissDelay(10000); // 10sec
		model = new FlashCheckListTableModel();

		if (AutomaticPhoneDetection.getInstance().isNokia())
			isNokia = true;
		else
			isNokia = false;

		if (isNokia) {
			model.setLongValues(nokiaValues);
		} else {
			model.setLongValues(otherValues);
		}
		table = new JTable(model) {
			public boolean getScrollableTracksViewportHeight() {
				if (getParent() instanceof JViewport) {
					return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
				}
				return false;
			}
		};

		table.setPreferredScrollableViewportSize(new Dimension(500, 200));
		tablePane = new JScrollPane(table);

		model.addColumn("#");
		model.addColumn("Random Test Name");
		model.addColumn("Monitoring Config");
		model.addColumn("M");
		model.addColumn("Verdict");

		int nbCol = 0;
		if (isNokia) {
			model.addColumn(COLUMN_PARAM_TIME);
			nbCol = 6;
		} else {
			model.addColumn(COLUMN_PARAM_EVENTS);
			model.addColumn(COLUMN_PARAM_THROTTLE);
			nbCol = 7;
		}
		constructTable();

		// Set up column sizes.
		model.initColumnSizes(model, table, nbCol);

		setRenderer();

		table.setRowHeight(25);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				CoreGUIPlugin.mainFrame.updateButtons();
			}
		});

		givePopupMenuToTable(table);

		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, tablePane);
	}

	/**
	 * Associates a popup menu to a table.
	 * 
	 * @param table
	 *            the target table
	 */
	public void givePopupMenuToTable(JTable table) {
		JPopupMenu popUp = createTablePopUp();
		table.addMouseListener(new HopperMouseListener(popUp, this));
	}

	/**
	 * Creates the popup menu.
	 * 
	 * @return the popup menu
	 */
	private JPopupMenu createTablePopUp() {
		JPopupMenu popup = new JPopupMenu();

		submenuLaunchInExternalTool = new JMenu("Run external tool");
		submenuLaunchInExternalTool.setToolTipText("Launch an external tool one the selected step");
		// initLaunchExternalTool();

		popup.add(HopperGUIAction.ANALYSESELECTIONFLASH.getAsMenuItem("launch selection"));
		popup.add(MatosAction.VIEWREPORT.getAsMenuItem("Open latest report"));
		// popup.add(submenuLaunchInExternalTool);
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
	private void setRenderer() {
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

		modifiedColumn = table.getColumnModel().getColumn(COLUMN_MODIFIED);
		modifiedColumn.setMinWidth(30);
		modifiedColumn.setMaxWidth(50);
		GeneralRenderer modifiedRenderer = new GeneralRenderer();
		modifiedColumn.setCellRenderer(modifiedRenderer);

		verdictColumn = table.getColumnModel().getColumn(COLUMN_VERDICT);
		GeneralRenderer verdictRenderer = new GeneralRenderer();
		verdictColumn.setCellRenderer(verdictRenderer);

		// commentsColumn = table.getColumnModel().getColumn(COLUMN_COMMENTS);
		// GeneralRenderer commentsRenderer = new GeneralRenderer();
		// commentsColumn.setCellRenderer(commentsRenderer);

		timeColumn = table.getColumnModel().getColumn(COLUMN_TIME);
		GeneralRenderer timeRenderer = new GeneralRenderer();
		timeColumn.setCellRenderer(timeRenderer);

		if (!this.isNokia) {
			throttleColumn = table.getColumnModel().getColumn(COLUMN_THROTTLE);
			GeneralRenderer throttleRenderer = new GeneralRenderer();
			throttleColumn.setCellRenderer(throttleRenderer);
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
			suitImages = new ImageIcon[3];
			java.net.URL passedURL = CoreGUIPlugin.getIconURL("tango/apply.png");
			java.net.URL failedURL = CoreGUIPlugin.getIconURL("tango/messagebox_warning.png");
			java.net.URL skippedURL = CoreGUIPlugin.getIconURL("tango/cache.png");
			suitImages[0] = new ImageIcon(passedURL, Step.verdictAsString.get(Verdict.PASSED));
			suitImages[1] = new ImageIcon(failedURL, Step.verdictAsString.get(Verdict.FAILED));
			suitImages[2] = new ImageIcon(skippedURL, Step.verdictAsString.get(Verdict.SKIPPED));
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
				int numRow = new Integer((String) model.getValueAt(row, COLUMN_NBROW)).intValue() - 1;
				if (column == COLUMN_TESTNAME) {
					this.setToolTipText((String) toolTipFlashFile.get(numRow));
				} else
					if (column == COLUMN_PHONECONFIG) {

					} else
						if (column == COLUMN_MODIFIED) {
							this.setToolTipText((String) toolTipModified.get(numRow));
							this.setHorizontalAlignment(JLabel.CENTER);
						} else
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
									} else
										if (userVerdict == Verdict.FAILED) {
											this.setIcon(suitImages[1]);
										} else
											if (userVerdict == Verdict.SKIPPED) {
												this.setIcon(suitImages[2]);
											}
								} else {
									if (verdict == Verdict.PASSED) {
										this.setIcon(suitImages[0]);
									} else
										if (verdict == Verdict.FAILED) {
											this.setIcon(suitImages[1]);
										} else
											if (verdict == Verdict.SKIPPED) {
												this.setIcon(suitImages[2]);
											} else
												if (verdict == Verdict.NONE) {
													this.setIcon(null);
												} else {
													this.setIcon(null);
												}
								}
								if (userVerdict == verdict && verdict != Verdict.NONE) {
									this.setBackground(new Color(224, 238, 224));
								} else
									if (userVerdict != verdict && userVerdict != Verdict.NONE) {
										this.setBackground(new Color(238, 213, 210));
									}
								if (verdict == Verdict.SKIPPED) {
									this.setBackground(new Color(255, 250, 205));
								}
								// } else if (column == COLUMN_COMMENTS){
								// this.setToolTipText((String)model.getValueAt(row,
								// column));
								//
								// table.getCellEditor(row,
								// column).addCellEditorListener(
								// new CellEditorListener() {
								// public void editingCanceled(ChangeEvent e) {
								// }
								// public void editingStopped(ChangeEvent e) {
								// int row =
								// HopperCheckListTable.this.table.getSelectedRow();
								// Step c = (Step)campaign.get(row);
								// c.setUserComment((String)model.getValueAt(row,
								// COLUMN_COMMENTS));
								// }
								// });
								// }
							} else
								if (column == COLUMN_TIME) {
									this.setToolTipText((String) model.getValueAt(row, column));

									table.getCellEditor(row, column).addCellEditorListener(
											new CellEditorListener() {
												public void editingCanceled(ChangeEvent e) {
												}
												public void editingStopped(ChangeEvent e) {
													int row = HopperCheckListTable.this.table
															.getSelectedRow();
													HopperStep c = (HopperStep) campaign.get(row);
													// String
													// value=(String)model.getValueAt(row,
													// COLUMN_TIME);
													if (isNokia)
														c.getParam().put(
																HopperStep.PARAM_TIME,
																((String) model.getValueAt(row,
																		COLUMN_TIME)));
													else
														c.getParam().put(
																HopperStep.PARAM_NBEVENTS,
																((String) model.getValueAt(row,
																		COLUMN_TIME)));
													CoreGUIPlugin.mainFrame.setModified(true);

												}
											});
								} else
									if (column == COLUMN_THROTTLE) {
										this.setToolTipText((String) model.getValueAt(row, column));

										table.getCellEditor(row, column).addCellEditorListener(
												new CellEditorListener() {
													public void editingCanceled(ChangeEvent e) {
													}
													public void editingStopped(ChangeEvent e) {
														int row = HopperCheckListTable.this.table
																.getSelectedRow();
														HopperStep c = (HopperStep) campaign
																.get(row);
														// String
														// value=(String)model.getValueAt(row,
														// COLUMN_TIME);
														c.getParam().put(
																HopperStep.PARAM_THROTTLE,
																((String) model.getValueAt(row,
																		COLUMN_THROTTLE)));
														CoreGUIPlugin.mainFrame.setModified(true);

													}
												});
									}

				if (completeView) {
					if (column == COLUMN_TIME) {
						this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
					}

				}
				return this;
			} catch (ArrayIndexOutOfBoundsException ae) {
				// throwed when table is modified (cleared,...) and repainted at
				// same time...
				// when numRow becomes > nb row in table
				// ae.printStackTrace(Out.log);
			} catch (NumberFormatException nfe) {
				// case numRow retriving get a null (see overwritten method
				// MyTableModel.getValueAt(..))
				// nfe.printStackTrace(Out.log);
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
		if (!(step instanceof HopperStep)) {
			// Out.log.println("addRow(..): Warning, trying to add a non FlashStep into FlashCheckListTable... step skipped.");
			return;
		}
		HopperStep flashStep = (HopperStep) step;
		String flashURI = flashStep.getFlashFilePath();
		String flashName = "";
		if (flashURI.endsWith("tst")) {
			flashName = StringUtilities.guessName(flashURI, "tst");
		} else {// .sis
			flashName = StringUtilities.guessName(flashURI, "xml");
		}
		Vector<String> rowData = new Vector<String>();
		if (rowNumberInGUI == -1) {
			rowNumberInGUI = model.getRowCount();
		}
		String numRowInNumbers = Integer.valueOf(table.getRowCount() + 1).toString();
		int numRowInCampaign = campaign.size();
		rowData.add(numRowInNumbers);// rowNumber

		rowData.add(flashName);
		toolTipFlashFile.add(numRowInCampaign, flashURI);

		if (checkPreviousResults) {
			// check for previous results
			// List<HopperStepAnalysisResult> results = new
			// ArrayList<HopperStepAnalysisResult>();
			// List<IAnalysisResultsManager> arManagers=
			// Matos.getInstance().getAnalysisResultsManagers();
			// for (IAnalysisResultsManager arManager : arManagers) {
			// HopperStepAnalysisResult rs =
			// (HopperStepAnalysisResult)arManager.getPreviousAnalysisResult(flashStep);
			// if (rs!=null) {
			// results.add(rs);
			// }
			// }

			// looking at retreived results
			StepAnalysisResult sar = null;
			/*
			 * if (results.size()==1) { sar = results.get(0); } else if
			 * (results.size()>1) { // case of several Analysis Results Manager.
			 * Should not arrived... // TODO sort dy date and use the younger
			 * Out.log.println(
			 * "More than one previous analysis result retreived. Nothing done..."
			 * ); //javastep.updateLastAnalysisResult(results.get(0)); } else {
			 * // no results... }
			 */

			if (sar != null) {
				flashStep.updateLastAnalysisResult(sar);
				String toolTip = sar.toHTML(flashStep);
				boolean modified = toolTip.indexOf("red") > 0;
				rowData.add(modified ? "M" : ""); // Modified column
				toolTipModified.add(numRowInCampaign, toolTip);

				if (sar.getVerdict().equals(Step.verdictAsString.get(Verdict.PASSED))
						|| sar.getVerdict().equals(Step.verdictAsString.get(Verdict.FAILED))) {
					rowData.add(sar.getVerdict()); // Verdict column
					// yvain rowData.add( sar.getComment() ); //Comments column

					// yvain String repPath = sar.getReportPath();
					// if ((repPath==null)||(repPath.equals(""))) {
					// repPath = rs.report.getAbsolutePath();
					// }
					// toolTipReport.add(numRowInCampaign, repPath);
				} else { // verdict is 'Skipped'
					rowData.add(sar.getVerdict() + ": " + sar.getReason());
					toolTipReport.add(numRowInCampaign, sar.getReason());
				}
			} else {
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
				if (isNokia) {
					if (flashStep.getParam().get(HopperStep.PARAM_TIME) != null)
						rowData.add(flashStep.getParam().get(HopperStep.PARAM_TIME));
					else {
						rowData.add("20000");
						flashStep.getParam().put(HopperStep.PARAM_TIME, "20000");
					}
				} else {
					if (flashStep.getParam().get(HopperStep.PARAM_NBEVENTS) != null)
						rowData.add(flashStep.getParam().get(HopperStep.PARAM_NBEVENTS));
					else {
						rowData.add("2000");
						flashStep.getParam().put(HopperStep.PARAM_NBEVENTS, "2000");
					}
					if (flashStep.getParam().get(HopperStep.PARAM_THROTTLE) != null)
						rowData.add(flashStep.getParam().get(HopperStep.PARAM_THROTTLE));
					else {
						rowData.add("0");
						flashStep.getParam().put(HopperStep.PARAM_THROTTLE, "0");
					}
				}

				toolTipReport.add(numRowInCampaign, "");
			}
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
		remove(tablePane);
		initTable();
		repaint();
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
			campaign = new HopperCampaign();
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

		int index_in_campaign = getCampaign().indexOf(step);
		int index_in_table = getIndexInTable(index_in_campaign);
		centerRow(index_in_table);

		// gets back previous results for the step
		List<HopperStepAnalysisResult> results = new ArrayList<HopperStepAnalysisResult>();
		/*
		 * List<IAnalysisResultsManager> arManagers=
		 * Matos.getInstance().getAnalysisResultsManagers(); for
		 * (IAnalysisResultsManager arManager : arManagers) {
		 * HopperStepAnalysisResult rs =
		 * (HopperStepAnalysisResult)arManager.getPreviousAnalysisResult
		 * (flashStep); if (rs!=null) { results.add(rs); } }
		 */
		// looking at retreived results
		if (results.size() == 1) {
			flashStep.updateLastAnalysisResult(results.get(0));
		} else
			if (results.size() > 1) {
				// case of several Analysis Results Manager. Should not
				// arrived...
				// TODO sort dy date and use the younger
				Logger.getLogger(this.getClass()).warn(
						"More than one previous analysis result retreived. Nothing done...");
				// flashStep.updateLastAnalysisResult(results.get(0));
			} else {
				// no results... nothing to do
			}

		Verdict userVerdict = flashStep.getUserVerdict();
		if (userVerdict != Verdict.NONE) {
			if (userVerdict == Verdict.PASSED || userVerdict == Verdict.FAILED) {
				model.setValueAt(Step.verdictAsString.get(userVerdict), index_in_table,
						COLUMN_VERDICT);
				String repPath = flashStep.getOutFilePath();
				toolTipReport.set(index_in_campaign, repPath);
			} else { // verdict is 'skipped'
				if (flashStep.getSkippedMessage() != null
						&& flashStep.getSkippedMessage().length() != 0) {
					model.setValueAt(
							Step.verdictAsString.get(userVerdict) + ": "
									+ flashStep.getSkippedMessage(), index_in_table, COLUMN_VERDICT);
					toolTipReport.set(index_in_campaign, flashStep.getSkippedMessage());
				} else {
					model.setValueAt(Step.verdictAsString.get(userVerdict), index_in_table,
							COLUMN_VERDICT);
				}
			}
		} else {
			Verdict verdict = flashStep.getVerdict();
			if (verdict == Verdict.PASSED || verdict == Verdict.FAILED) {
				model.setValueAt(Step.verdictAsString.get(verdict), index_in_table, COLUMN_VERDICT);
				String repPath = flashStep.getOutFilePath();
				toolTipReport.set(index_in_campaign, repPath);
			} else
				if (verdict == Verdict.NONE) {
					model.setValueAt(Step.verdictAsString.get(verdict), index_in_table,
							COLUMN_VERDICT);
				} else { // verdict is 'Skipped'
					if (flashStep.getSkippedMessage() != null
							&& flashStep.getSkippedMessage().length() != 0) {
						model.setValueAt(
								Step.verdictAsString.get(verdict) + ": "
										+ flashStep.getSkippedMessage(), index_in_table,
								COLUMN_VERDICT);
						toolTipReport.set(index_in_campaign, flashStep.getSkippedMessage());
					} else {
						model.setValueAt(Step.verdictAsString.get(verdict), index_in_table,
								COLUMN_VERDICT);
					}
				}
		}

		StepAnalysisResult sar = flashStep.getLastAnalysisResult();
		String toolTip = "";
		if (sar != null) {
			toolTip = sar.toHTML(flashStep);
			boolean modified = toolTip.indexOf("red") > 0;
			model.setValueAt(modified ? "M" : "", index_in_table, COLUMN_MODIFIED);
			toolTipModified.set(index_in_campaign, toolTip);
		}

	}

	/**
	 * The model for this table for.
	 */
	private static class FlashCheckListTableModel extends CheckListTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int col) {
			if (col == COLUMN_TIME || col == COLUMN_PHONECONFIG || col == COLUMN_THROTTLE) {
				return true;
			}
			return false;
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
