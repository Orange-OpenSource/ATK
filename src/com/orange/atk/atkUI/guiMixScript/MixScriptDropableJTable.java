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
 * File Name   : MixScriptDropableJTable.java
 *
 * Created     : 04/07/2007
 * Author(s)   : Nicolas MOTEAU
 */ 
package com.orange.atk.atkUI.guiMixScript;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.anaMixScript.MixScriptStep;
import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.coregui.CoreGUIPlugin;
import com.orange.atk.atkUI.coregui.tasks.LoadCheckListTask;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;

/**
 *
 * @author Nicolas MOTEAU
 * @since JDK5.0
 */
public class MixScriptDropableJTable extends JTable implements DropTargetListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MixScriptDropableJTable(TableModel dm) {
		super(dm);
		new DropTarget(this, this);
	}
	
	//-- Implementation of interface DropTargetListener ---
	
	private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
	
	public void dragEnter(DropTargetDragEvent event) {
		if (isEnabled()) {
			event.acceptDrag(DnDConstants.ACTION_MOVE);
		} else {
			event.rejectDrag();
		}
	}
	public void dragOver(DropTargetDragEvent event) {}
	public void dropActionChanged(DropTargetDragEvent event) {}
	public void dragExit(DropTargetEvent event) {}
	public void drop(DropTargetDropEvent event) {
		Transferable transferable = event.getTransferable();

		event.acceptDrop(DnDConstants.ACTION_MOVE);

		DataFlavor uriListFlavor = null;
		try {
			uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Vector<File> dd_flashPathVect = new Vector<File>();
			// 1. get back files
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) { // windows's way 
				List<File> list = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator<File> it = list.iterator();
				while (it.hasNext()){
					File f = it.next();
					String extension=".tst";

					if(AutomaticPhoneDetection.getInstance()
							.isNokia())
						extension=".xml";	
					if (f.getAbsolutePath().endsWith(extension)){
						dd_flashPathVect.add(new File(f.getAbsolutePath()));
					}
				}

			} else if (transferable.isDataFlavorSupported(uriListFlavor)) { // gnome & KDE's way
				String s = (String)transferable.getTransferData(uriListFlavor);
				String [] uris = s.split(System.getProperty("line.separator"));
				for (int i=0; i<uris.length; i++){
					if (uris[i].trim().length()>0) {
						File f = new File(new URI(uris[i].trim()));
						String extension=".tst";

						if(AutomaticPhoneDetection.getInstance()
								.isNokia())
							extension=".xml";	
						if (f.exists() && f.getAbsolutePath().endsWith(extension)){
							dd_flashPathVect.add(new File(f.getAbsolutePath()));
						}
					}
				}
			} else {
				Logger.getLogger(this.getClass() ).debug("unsupported flavors: "+String.valueOf(transferable.getTransferDataFlavors()));
			}
			
			// 2. add droped files into the check list 
			if (dd_flashPathVect.size()>0) {
				Campaign tmpCamp = new Campaign();
				Iterator<File> itSWF = dd_flashPathVect.iterator();
				while (itSWF.hasNext()) {
					File swfFile = itSWF.next();
					String swfPath = swfFile.getAbsolutePath();
					MixScriptStep flashStep = new MixScriptStep(swfPath, swfFile);
					tmpCamp.add(flashStep);
				}
				new LoadCheckListTask(CoreGUIPlugin.mainFrame.statusBar, tmpCamp, -1, false, tmpCamp.size());

				event.dropComplete(true);
			}

		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	//-- End of implementation of interface DropTargetListener ---
}
