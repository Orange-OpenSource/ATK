package com.orange.atk.scriptRecorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.TimeoutException;
import com.orange.atk.phone.DefaultPhone;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

public class SelectAPKDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField fileFilter;
	private JButton ok;
	private JButton cancel;
	private  JScrollPane jScrollPane1=null;
	private  String[] allUID;
	private  String[] allUIDBup;
	private  String[] listUID;
	private    JList jList1;
	//private JDialog contentPaneFrame = new JDialog(this, ModalityType.APPLICATION_MODAL);
	 
     public SelectAPKDialog (JFrame fr, ArrayList<String> AllAPK)
     {
    	 super(fr,ModalityType.APPLICATION_MODAL );
    	 
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
	 if(currentPhone instanceof DefaultPhone )
		{ 
			JOptionPane.showMessageDialog(null, "Can't Detect device");
				return;
		}
	 allUID =null;
	 AutomaticPhoneDetection.getInstance().pauseDetection();
	 Object[] apks = AllAPK.toArray();
	 allUID = Arrays.copyOf(apks, apks.length, String[].class);
	 AutomaticPhoneDetection.getInstance().resumeDetection();
	 if (allUID!=null) {
		 allUIDBup= new String [allUID.length];
		 for(int i=0; i< allUID.length; i++)
	 {
		 int index = allUID[i].indexOf(" et ");
	      if(index!=-1)
		  allUIDBup[i]= allUID[i].substring(0, allUID[i].indexOf(" et "));
	      else
	      allUIDBup[i]= allUID[i];  
	 }
		 
	 }
	 if (allUID!=null) {
			jList1 = new JList(allUIDBup);
			jList1.setDoubleBuffered(false);
			jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jScrollPane1 = new JScrollPane(jList1);
			listUID = allUID;
			globalFilePanel.setBorder(new TitledBorder("Start writing and press enter to filter"));

			ok = new JButton("OK");
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
	
					int indice =jList1.getSelectedIndex();
					String listProg="";
					if(indice>=0)
					{
					   listProg=listProg+listUID[indice];
					}
					Logger.getLogger(this.getClass() ).debug("Selected array"+listProg);
	
					fileFilter.setText(listProg);
					
					
					SelectAPKDialog.this.dispose();
					RecorderFrame.MainActivityName="";
					RecorderFrame.PackageName="";
					RecorderFrame.PackageSourceDir="";
					if (listProg.indexOf(" et ")!=-1)
					{
					RecorderFrame.MainActivityName=listProg.substring(0, listProg.indexOf(" et "));
					listProg=listProg.substring( listProg.indexOf(" et ")+4);
					}
					if (listProg.indexOf(" et ")!=-1)
					{
					RecorderFrame.PackageName=listProg.substring(0, listProg.indexOf(" et "));
					listProg=listProg.substring( listProg.indexOf(" et ")+4);
					}
					RecorderFrame.PackageSourceDir=listProg;
					Logger.getLogger(this.getClass() ).debug("Package: "+RecorderFrame.MainActivityName+" || MainActivity :  " +RecorderFrame.PackageName+
							" || Source Directory : "+RecorderFrame.PackageSourceDir);
					//launchAction();
				}
			});
		} else {
			globalFilePanel.setBorder(new TitledBorder("Please enter manually the application name(s)"));

		
			ok = new JButton("OK");
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					
				}
			});
		}
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SelectAPKDialog.this.dispose();
				
			}
		});
		
		JPanel buttonsPanel = new JPanel();	

		buttonsPanel.add(ok);
		buttonsPanel.add(cancel);

		getRootPane().setDefaultButton(ok);

		//Container contentPaneFrame = this.getContentPane();
		this.add(globalFilePanel, BorderLayout.NORTH);
		if(jScrollPane1!=null)
			this.add(jScrollPane1, BorderLayout.CENTER);
		this.add(buttonsPanel, BorderLayout.SOUTH);
		this.setTitle("Select APK to test with Robotium");
		
		//setLocationRelativeTo(CoreGUIPlugin.mainFrame);
		if (allUID!=null) this.setSize(new Dimension(370,400));
		else this.setSize(new Dimension(370,130));
		ok.requestFocusInWindow();
		setLocationRelativeTo(fr);
		
	
		
     }
     
   
       private void filterList(){
       	String filterValue = fileFilter.getText();
       	int number = 0;
           for(String UID : allUID){
       		if(UID.contains(filterValue)){
       			number++;
       		}
       	}
           listUID =  new String[number];
           int i=0;
           for(String UID : allUID){
       		if(UID.contains(filterValue)){
       			listUID[i] = UID;
       			i++;
       		}
       	}
       
        String []listUIDBup=new String[number];
        
       for(int j=0; j< listUID.length; j++)
      	 {
      		 int index = allUID[j].indexOf(" et ");
      	      if(index!=-1)
      	    	listUIDBup[j]= listUID[j].substring(0, listUID[j].indexOf(" et "));
      	      else
      	    	listUIDBup[j]= listUID[j];  
      	 }
      		
       	jList1.setListData(listUIDBup);

       }
     
    
}
