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
 * File Name   : DeviceDetectionFrame.java
 *
 * Created     : 18/03/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.deviceDetectionUI;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.orange.atk.internationalization.ResourceManager;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.phone.detection.DeviceDetectionListener;



public class DeviceDetectionFrame extends JFrame implements DeviceDetectionListener {
	private static final long serialVersionUID = 1L;
	JLabel jlbDeviceDetection = new JLabel();
	String[] columnNames = {ResourceManager.getInstance().getString("COL_SELECTED"), ResourceManager.getInstance().getString("COL_PHONE_DESC"), ResourceManager.getInstance().getString("COL_STATUS")};
	private static final int[] columnSizes = {60, 250, 120};
	private static final int rowHeight = 30;
	private static int frameWidth = 0; 
	private static int frameHeight= 0; 
	Dimension screenDim;
	JTable jtbDeviceTable;
	private final String DEVICE_CONNECTED = " "+ResourceManager.getInstance().getString("SELECT_ONE_CONNECTED_DEVICE");
	private final String NO_DEVICE_CONNECTED = " "+ResourceManager.getInstance().getString("NO_DEVICE_CONNECTED");
	JPanel panel;
	ImageIcon selIcon;
	ImageIcon emptyIcon;
	private final Object lock = new Object();
	private static DeviceDetectionFrame instance=null;
	private ColorCellRenderer colorCellRenderer = new ColorCellRenderer();
 	public static final String FAILED_STATUS_LABEL = " "+ResourceManager.getInstance().getString("FAILED");
 	public static final String AVAILABLE_STATUS_LABEL = " "+ResourceManager.getInstance().getString("AVAILABLE");
 	public static final String BUSY_STATUS_LABEL = " "+ResourceManager.getInstance().getString("BUSY");
 	public static final String DISCONNECTED_STATUS_LABEL = " "+ResourceManager.getInstance().getString("DISCONNECTED");

 	public static DeviceDetectionFrame getInstance(){
		if(instance ==null) {
			instance = new DeviceDetectionFrame();
		}
		return instance;
	}

	private DeviceDetectionFrame() {
		super("Connected Devices");
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
	    screenDim = toolkit.getScreenSize();

		Vector<String> columns = new Vector<String>();
		try {
			selIcon = new ImageIcon(new File("res/tango/apply.png").toURI().toURL());
			emptyIcon = new ImageIcon(new File("res/tango/empty.png").toURI().toURL());
		} catch (MalformedURLException e1) {
			//silently ignored 
		}
		for (int i=0; i<columnNames.length; i++) columns.addElement(columnNames[i]);
		jtbDeviceTable = new JTable (new DeviceTableModel(columns, 0)) {
			public TableCellRenderer getCellRenderer(int row, int column) {
		        if ((column == 2)) { // COL_STATUS index
		            return colorCellRenderer;
		        }
		        return super.getCellRenderer(row, column);
		    }
		};
		for (int i=0; i<columnNames.length; i++) {
			TableColumn col = jtbDeviceTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(columnSizes[i]);
			col.setMinWidth(columnSizes[i]);
			//rowWidth += columnSizes[i];
		}
		jtbDeviceTable.setRowHeight(rowHeight);
		jtbDeviceTable.getTableHeader().setPreferredSize(new Dimension(getWidth(), rowHeight));
		jtbDeviceTable.getTableHeader().setBackground(Color.ORANGE);
		jtbDeviceTable.getTableHeader().setResizingAllowed(false);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		jtbDeviceTable.getSelectionModel().addListSelectionListener(new RowListener());
		jtbDeviceTable.setSelectionBackground(Color.WHITE);
		Border focusCellHighlightBorder = new BorderUIResource.LineBorderUIResource(jtbDeviceTable.getSelectionBackground());
		UIManager.put("Table.focusCellHighlightBorder", focusCellHighlightBorder);
	    panel = new JPanel(gridbag);
	    panel.setBackground(Color.WHITE);
	    jlbDeviceDetection.setText(NO_DEVICE_CONNECTED);
		addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}

			public void windowClosing(WindowEvent arg0) {
				setVisible(false);
				dispose();
			}

			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}

		});
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(jlbDeviceDetection, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(jtbDeviceTable.getTableHeader(), constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		panel.add(jtbDeviceTable, constraints);
		JScrollPane scroll = new JScrollPane(panel);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scroll);
		
		this.setMaximumSize(new Dimension(600,1000));
		addComponentListener(new ComponentAdapter() {
		      @Override
		      public void componentResized(ComponentEvent e) {
		    	JFrame f = ((JFrame) e.getSource());
		    	if (frameWidth==0) {   
		    		frameWidth = f.getWidth();   
                }   
                f.setSize(frameWidth, f.getHeight());   
		      }
		    });
		
		AutomaticPhoneDetection.getInstance().addDeviceDetectionListener(this);
	}
	

	
	private void updateUI() {
		List<PhoneInterface> connectedPhones = AutomaticPhoneDetection.getInstance().getDevices();
		PhoneInterface selectedPhone = AutomaticPhoneDetection.getInstance().getDevice();
		synchronized(lock) {
			DefaultTableModel tableModel  = (DefaultTableModel) jtbDeviceTable.getModel();
			int lastIndex = tableModel.getRowCount()-1;
			for (int i=lastIndex; i>=0; i--) tableModel.removeRow(i);
			boolean selectionDone = false;
			for (int i=0; i<connectedPhones.size(); i++) {
				PhoneInterface connectedPhone = (PhoneInterface) connectedPhones.get(i);
				Object[] row = new Object[3];
				row[1] = " "+getPhoneDescription(connectedPhone);
				row[2] = getStatusName(connectedPhone.getCnxStatus(), connectedPhone.isFailed());
				if (!(selectedPhone instanceof DefaultPhone) && connectedPhone==selectedPhone) {
					row[0] = selIcon;
					selectionDone = true;
				} else row[0] = emptyIcon;
				tableModel.addRow(row);
			}
			if (!selectionDone) {
				if (connectedPhones.size()>0) {
					tableModel.setValueAt(selIcon, 0, 0);
					AutomaticPhoneDetection.getInstance().setSelectedDevice(connectedPhones.get(0));
					jlbDeviceDetection.setText(DEVICE_CONNECTED);
				} else {
					AutomaticPhoneDetection.getInstance().setSelectedDevice(null);					
					jlbDeviceDetection.setText(NO_DEVICE_CONNECTED);
				}
			}
			if (frameWidth==0) {
				pack();
				frameWidth = getWidth();
				frameHeight = getHeight() - tableModel.getRowCount()*rowHeight;
			}
			this.setPreferredSize(new Dimension(frameWidth, Math.min(screenDim.height, tableModel.getRowCount()*rowHeight+frameHeight)));  
		}
		pack();
		repaint();
	}
	
	private String getPhoneDescription(PhoneInterface phone) {
		String desc = " ";
		desc += phone.getName();
		String uid = phone.getUID();
		if (uid!=null && !uid.equals("")) desc+=" ("+uid+") ";
		return desc;
	}
	
	private String getStatusName(int status, boolean failed) {
		String statusName = "";
		switch (status) {
			case PhoneInterface.CNX_STATUS_AVAILABLE :
				if (failed) statusName = FAILED_STATUS_LABEL;
				else statusName = AVAILABLE_STATUS_LABEL;
				break;
			case PhoneInterface.CNX_STATUS_BUSY :
				statusName = BUSY_STATUS_LABEL;
				break;
			case PhoneInterface.CNX_STATUS_DISCONNECTED:
				statusName = DISCONNECTED_STATUS_LABEL;
				break;
			default :
				break;
		}
		return statusName;
	}

	public void display() {
		updateUI();
		setVisible(true);
	}
	
    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            selectRow();
        }
    }

     public void selectRow() {
 		List<PhoneInterface> connectedPhones = AutomaticPhoneDetection.getInstance().getDevices();	
 		synchronized(lock) {
	    	int irow = jtbDeviceTable.getSelectedRow();
		    if (irow>=0) {
			 	 AutomaticPhoneDetection.getInstance().setSelectedDevice(connectedPhones.get(irow));
		    } 
 		}
     }



	public void devicesConnectedChanged() {
		 updateUI();
	}



	public void deviceSelectedChanged() {
		 updateUI();
	}
	
}
