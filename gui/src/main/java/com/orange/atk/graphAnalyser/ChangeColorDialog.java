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
 * File Name   : ChangeColorDialog.java
 *
 * Created     : 03/08/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.interpreter.config.ConfigFile;

@SuppressWarnings("serial")
public class ChangeColorDialog extends JDialog {

	Vector componentvector =null;
	
	//TODO usage ?
	//private JTextField inputcsvfile = null;
    //private LectureJATKResult frameAnalyser =null;
    //private String color =null;
	private JComboBox colorcombo;
	Map<String, PerformanceGraph> graph=null;
	
	
	public ChangeColorDialog(LectureJATKResult owner, boolean modal,Map<String, PerformanceGraph> graph){
		super(owner, modal);
		this.graph=graph;

		//this.frameAnalyser= owner;

        this.setTitle("add a Graph...");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Commons", getCommonsPanel());
		this.add(tabs, BorderLayout.CENTER);
		this.add(getOKCancelPanel(), BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true); 
	}
	
	/**
	 * Builds the panel that allow to modify commons congiguration parameters 
	 * @return a JPanel to config commons parameters
	 */
	private JPanel getCommonsPanel() {
		JPanel commons = new JPanel();
		commons.setLayout(new BoxLayout(commons, BoxLayout.Y_AXIS));
		
		
		
		JPanel results = new JPanel();
		results.setLayout(new BorderLayout());
		results.setBorder(BorderFactory.createTitledBorder("Change color"));

        //Select graph location
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		inputPanel.add(Box.createHorizontalStrut(5));
		
		results.add(inputPanel, BorderLayout.SOUTH);
		JPanel storagePane2 = new JPanel(new BorderLayout());
		storagePane2.add(inputPanel, BorderLayout.SOUTH);
		results.add(storagePane2, BorderLayout.NORTH);
		JPanel storagePanel = new JPanel(new BorderLayout());
		JPanel colorPanel = new JPanel(new BorderLayout());
	    JPanel chooseColor = new JPanel(new FlowLayout(FlowLayout.LEFT));	

	    componentvector=new Vector();
	    
	    Set<String> cles = graph.keySet();
        Iterator<String> it = cles.iterator();
        while (it.hasNext()) {
            String cle = (String) it.next();
            PerformanceGraph perfgraph = (PerformanceGraph) graph.get(cle);
            colorcombo = new JComboBox(new String[] { "blue","yellow", "red","green","black","gray","cyan","magenta","orange","pink" });
            colorcombo.setName("combo"+cle);
            componentvector.add(colorcombo);
            Color color =perfgraph.getColor();
            String scolor =getcolor(color);
            if(scolor!=null)
            colorcombo.setSelectedItem(scolor);
            chooseColor.add(new JLabel(cle));
    		chooseColor.add(colorcombo);
    		chooseColor.add(Box.createHorizontalStrut(5));
    		colorPanel.add(chooseColor, BorderLayout.SOUTH);
            
        }
	    
	    
			
		

		
		
		storagePanel.add(colorPanel, BorderLayout.NORTH);
		results.add(storagePanel, BorderLayout.SOUTH);
		commons.add(results);

		return commons;
	}
	
	/**
	 * Action performed when user clicks on "OK" button or presses 
	 * the "Enter" key.
	 */
	protected void okAction() {
		Cursor lastCursor = ChangeColorDialog.this.getCursor();
		ChangeColorDialog.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        //color =(String)colorcombo.getSelectedItem();
		// save commons config parameters
        
        Set<String> cles = graph.keySet();
        Iterator<String> it = cles.iterator();
        while (it.hasNext()) {
            String cle = (String) it.next();
            PerformanceGraph perfgraph = (PerformanceGraph) graph.get(cle);
            JComboBox combo=  (JComboBox) getComponent("combo"+cle);
            String newcolor =(String) combo.getSelectedItem();
            Logger.getLogger(this.getClass() ).debug(cle+" "+newcolor);
    		Map<String, Color> mapColor =CreateGraph.getMapColor();
    		perfgraph.setColor(mapColor.get(newcolor));

        }
        
        
		ChangeColorDialog.this.setCursor(lastCursor);
		ChangeColorDialog.this.dispose();
	}
	
	public String getcolor(Color color)
	{
		Map<String, Color> mapColor =CreateGraph.getMapColor();
		
		if(mapColor.containsValue(color))
		{
		
			  Set<String> cles = mapColor.keySet();
		        Iterator<String> it = cles.iterator();
		        while (it.hasNext()) {
		            String cle = (String) it.next();
		            Color tempcolor =  mapColor.get(cle);
		            if(tempcolor.equals(color))
		            	return cle;
        
		        }
			
		}
		return null;
	}
	
	
	/**
	 * Builds the OK-Cancel panel
	 * @return ok and Cancel buttons in a panel panel
	 */
	private JPanel getOKCancelPanel() {
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				okAction();				
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChangeColorDialog.this.dispose();
			}
		});
		JPanel OKCancelPanel = new JPanel();
		OKCancelPanel.add(ok);
		OKCancelPanel.add(cancel);

		return OKCancelPanel;
	}
	
	/**
	 * Open a file chooser initialized with the content of the given textfield and put the chosen path
	 * in it at the closing of the file chooser.
	 * @param textField text field which contains the file path.
	 */
	private void openFileChooser(JTextField textField, boolean dir) {
		JFileChooser fileChooser = null;
		if (textField.getText()!=null && !textField.getText().equals("")){
			fileChooser = new JFileChooser(textField.getText());
		}else{
			fileChooser = new JFileChooser();
		}
		String title = null;
		if (dir) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			title = "Select a directory";
		} else {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileUtilities.Filter("csv file [*.csv]", ".csv"));
			title = "Select CSV file";
		}
		int returnVal = 0;
		returnVal =  fileChooser.showDialog(ChangeColorDialog.this, title);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String src = fileChooser.getSelectedFile().getAbsolutePath();
			if (!dir)
			src = FileUtilities.verifyExtension(src, ".csv");
			textField.setText(src);
		}
	}
	
	
	public String getvalueconfigfile(String pathihmconfig,String value)
	{
		
		File ihmconfig =new File(pathihmconfig);
		try {
			ConfigFile configFile;
			if(ihmconfig.exists())
			{
				configFile = new ConfigFile(ihmconfig);
				configFile.loadConfigFile();
					String result =configFile.getOption(value);
					return result;

			}
			else  {
				ihmconfig.createNewFile();
				Logger.getLogger(this.getClass() ).debug("New config file created");
				//configFile = new ConfigFile(ihmconfig);

			}
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass() ).debug( e.getMessage());
		}	
		
		return null;
		
	}
	
	public Component getComponent(String name) {
		Component comp=null;
		  for (int i = 0; i <componentvector.size();i++ ) {
		    Component current = (Component) componentvector.get(i);
		 if(current!=null)
		 {
		    if (name.equals(current.getName())) {
		      comp = current;
		      break;
		    }
		  }
		  } 	
		  return comp;
		}
	
}
