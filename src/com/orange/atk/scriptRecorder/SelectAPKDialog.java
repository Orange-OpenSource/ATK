package com.orange.atk.scriptRecorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;

import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

public class SelectAPKDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField fileFilter;
	private JButton ok;
	private JButton cancel;
	private  JScrollPane jScrollPane1=null;
	private  String[] AllInstalledApk;//contains all informations about an apk
	private  String[] AllInstalledApkBup;//contains just the package name of apk
	private  String[] listApks;
	private    JList jList1;
	private  RecorderFrame parent;

	public SelectAPKDialog (RecorderFrame fr, ArrayList<String> AllAPK) {
		super(fr,ModalityType.APPLICATION_MODAL );
		parent=fr;
		fileFilter = new JTextField(100);
		fileFilter.setEditable(true);
		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filePanel.add(Box.createHorizontalStrut(10));
		filePanel.add(fileFilter);
		fileFilter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				filterList();
			}
		});
		JPanel globalFilePanel = new JPanel();
		globalFilePanel.setLayout(new BoxLayout(globalFilePanel, BoxLayout.Y_AXIS));
		globalFilePanel.add(filePanel);
		PhoneInterface currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
		if(currentPhone instanceof DefaultPhone ){ 
			JOptionPane.showMessageDialog(null, "Can't Detect device");
			return;
		}
		AllInstalledApk =null;
		Object[] apks = AllAPK.toArray();
		AllInstalledApk = Arrays.copyOf(apks, apks.length, String[].class);
		AutomaticPhoneDetection.getInstance().resumeDetection();
		if (AllInstalledApk!=null) {
			AllInstalledApkBup= new String [AllInstalledApk.length];
			for(int i=0; i< AllInstalledApk.length; i++) {
				int index = AllInstalledApk[i].indexOf(",");
				if(index!=-1) {
					AllInstalledApkBup[i]= AllInstalledApk[i].substring(0, AllInstalledApk[i].indexOf(","));
				} else { 
					AllInstalledApkBup[i]= AllInstalledApk[i];
				} 
			}
		}
		if (AllInstalledApk!=null) {
			jList1 = new JList(AllInstalledApkBup);
			listApks = AllInstalledApk;
			jList1.setDoubleBuffered(false);
			jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jList1.setCellRenderer(new MyListRenderer());
			jScrollPane1 = new JScrollPane(jList1);
			globalFilePanel.setBorder(new TitledBorder("Start writing and press enter to filter"));
			ok = new JButton("OK");
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					int indice =jList1.getSelectedIndex();
					String selectedApk="";
					if(indice>=0) {
						selectedApk=selectedApk+listApks[indice];
						Logger.getLogger(this.getClass() ).debug("Selected array "+selectedApk);
						fileFilter.setText(selectedApk);
						SelectAPKDialog.this.dispose();
						RecorderFrame.MainActivityName="";
						RecorderFrame.PackageName="";
						RecorderFrame.PackageSourceDir="";
						RecorderFrame.Versioncode=-1;
						if(selectedApk.contains("Foreground App")) {
							RecorderFrame.MainActivityName="CurrentApp";
							RecorderFrame.PackageName="CurrentApp";
							RecorderFrame.PackageSourceDir="CurrentApp";
							RecorderFrame.Versioncode=-1;
						} else {
							if (selectedApk.indexOf(",")!=-1) {
								RecorderFrame.PackageName=selectedApk.substring(0, selectedApk.indexOf(","));
								selectedApk=selectedApk.substring( selectedApk.indexOf(",")+1);
							}
							if (selectedApk.indexOf(",")!=-1) {
								RecorderFrame.MainActivityName=selectedApk.substring(0, selectedApk.indexOf(","));
								selectedApk=selectedApk.substring( selectedApk.indexOf(",")+1);
							}
							if (selectedApk.indexOf(",")!=-1) {
								RecorderFrame.PackageSourceDir=selectedApk.substring(0, selectedApk.indexOf(","));
								selectedApk=selectedApk.substring( selectedApk.indexOf(",")+1);
							}
							RecorderFrame.Versioncode=Integer.valueOf(selectedApk);
							Logger.getLogger(this.getClass() ).debug("Package: "+RecorderFrame.PackageName+" || VersionCode :  "
									+RecorderFrame.Versioncode+" || MainActivity :  " +RecorderFrame.MainActivityName+" || Source Directory : "
									+RecorderFrame.PackageSourceDir);
						}
					} else {
						JOptionPane.showMessageDialog(parent, "You must Select a package (apk) ","Error",JOptionPane.ERROR_MESSAGE);
						show();
					}
				}
			});
		} else {
			globalFilePanel.setBorder(new TitledBorder("Please enter manually the application name(s)"));
			ok = new JButton("OK");
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { }
			});
		}
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 
				RecorderFrame.MainActivityName="NONE";
				RecorderFrame.PackageName="NONE";
				RecorderFrame.PackageSourceDir="NONE";
				RecorderFrame.Versioncode=-1;
				SelectAPKDialog.this.dispose();	
			}
		});
		JPanel buttonsPanel = new JPanel();	
		buttonsPanel.add(ok);
		buttonsPanel.add(cancel);
		getRootPane().setDefaultButton(ok);
		this.add(globalFilePanel, BorderLayout.NORTH);
		if(jScrollPane1!=null) {
			this.add(jScrollPane1, BorderLayout.CENTER);
		}
		this.add(buttonsPanel, BorderLayout.SOUTH);
		this.setTitle("Select APK to test with Robotium");
		if (AllInstalledApk!=null){ 
			this.setSize(new Dimension(370,400));
		} else {
			this.setSize(new Dimension(370,130));
		}
		ok.requestFocusInWindow();
		setLocationRelativeTo(fr);
	}
	private void filterList() {
		String filterValue = fileFilter.getText();
		int number = 0;
		for(String UID : AllInstalledApk) {
			if(UID.contains(filterValue)) {
				number++;
			}
		}
		listApks =  new String[number];
		int i=0;
		for(String UID : AllInstalledApk) {
			if(UID.contains(filterValue)){
				listApks[i] = UID;
				i++;
			}
		}
		String []listApksBup=new String[number];
		for(int j=0; j< listApks.length; j++) {
			int index = AllInstalledApk[j].indexOf(",");
			if(index!=-1) {
				listApksBup[j]= listApks[j].substring(0, listApks[j].indexOf(","));
			} else {
				listApksBup[j]= listApks[j];
			}
		}
		jList1.setListData(listApksBup);
	}

	private class MyListRenderer extends DefaultListCellRenderer  {  
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			String selectedApk=""+listApks[index];
			String apkpath = "";
			if (selectedApk.indexOf(",")!=-1) {
				selectedApk=selectedApk.substring( selectedApk.indexOf(",")+1);
			}
			if (selectedApk.indexOf(",")!=-1) {
				selectedApk=selectedApk.substring( selectedApk.indexOf(",")+1);
			}
			if (selectedApk.indexOf(",")!=-1) {
				apkpath=selectedApk.substring(0, selectedApk.indexOf(","));
			}
			if (apkpath.startsWith("/system")) { 
				setForeground(Color.LIGHT_GRAY);
			} else {
				setForeground(Color.darkGray);
				if ((index == 0)&&(value.toString().contains("Foreground App"))) { 
					setForeground(Color.BLUE);
				}
			}
			return this;
		}
	}
}
