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
 * File Name   : SetScreenShotReferenceDir.java
 *
 * Created     : 18/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.atkUI.guiScript.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.RandomAccessFile;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.utils.FileUtilities;
import com.orange.atk.atkUI.coregui.actions.MatosAbstractAction;
import com.orange.atk.atkUI.guiScript.GuiJatkLink;
import com.orange.atk.atkUI.guiScript.JatkCheckListTable;
import com.orange.atk.atkUI.guiScript.JatkGUI;
import com.orange.atk.platform.Platform;

public class SetScreenShotReferenceDir extends MatosAbstractAction {

	private static final long serialVersionUID = 1L;

	public SetScreenShotReferenceDir(String name, Icon icon,String shortDescription) {
		super(name, icon, shortDescription);
	}

	public void actionPerformed(ActionEvent arg0) {

		JatkGUI jatkGUI = GuiJatkLink.getFlashGUI();
		JTable _table = jatkGUI.getCheckListTable().getTable();
		int row=_table.getSelectedRow();
		//String testname =(String) _table.getModel().getValueAt(row, JatkCheckListTable.COLUMN_TESTNAME);
		String testScriptPath =((JatkCheckListTable)GuiJatkLink.getFlashGUI().getCheckListTable()).getToolTipFlashFile().elementAt(row);
		File file =new File(testScriptPath);
		if(file.exists())
		{
			try {
				RandomAccessFile raf =new RandomAccessFile(testScriptPath,"rw");
				
				String ligne;
				long cursor=0;
				String refPath=null;
				String contenu="";
				while ((ligne=raf.readLine())!=null){
					Logger.getLogger(this.getClass() ).debug("ligne :"+ligne);
					
					if (ligne.contains("<ref directory>")&& ligne.contains("</ref directory>")){
						String [] arrayString = ligne.split("<ref directory>");
						ligne=arrayString[1];
						arrayString = ligne.split("</ref directory>");
						refPath=arrayString[0];
						Logger.getLogger(this.getClass() ).debug("refPath :"+refPath);

						cursor = raf.getFilePointer()-ligne.getBytes().length;
						contenu+="<@ICI@>";
						//break;
					}else{
						contenu+=ligne+Platform.LINE_SEP;
					}
				}
				
					JFileChooser fc = new JFileChooser(refPath);
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setFileFilter(new FileUtilities.FilterDir());
					int res=fc.showDialog(null, "Select as Reference directory for screenshot comparator");
					if (res==JFileChooser.APPROVE_OPTION){
						refPath=fc.getSelectedFile().getAbsolutePath();
//						byte[] b= null;
						String ext = testScriptPath.substring(testScriptPath.lastIndexOf("."));
						String toAdd="";
						if (ext.toLowerCase().equals(".xml")){
							toAdd = "<!--<ref directory>"+refPath+"</ref directory>-->"+Platform.LINE_SEP;
							
						}else if(ext.toLowerCase().equals(".tst")){
							toAdd = "//<ref directory>"+refPath+"</ref directory>"+Platform.LINE_SEP;
						}
						else {
							Logger.getLogger(this.getClass() ).warn("The test file is not a .tst or .xml file");
						}
//						b=new byte[(int) (raf.length()+toAdd.length())];
//						byte[] b2=toAdd.getBytes();
//						for (int i = 0; i < b2.length; i++) {
//							 b[i]=b2[i];
//						}
						if (contenu.contains("<@ICI@>")){
								contenu= contenu.replace("<@ICI@>", toAdd);
						}else{
							contenu=toAdd+contenu;
						}
//						raf.read(b, b2.length, (int) raf.length() );
						raf.seek(0);
//						raf.write(b);
						raf.writeBytes(contenu);
						Logger.getLogger(this.getClass() ).debug("refpath inserted in "+ext+" test file");
					}
				raf.close();
			}catch (Exception e ) {
				e.printStackTrace();
			}


		}
	}


}
