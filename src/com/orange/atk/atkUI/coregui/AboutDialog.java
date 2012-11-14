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
 * File Name   : AboutDialog.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.orange.atk.platform.Platform;


@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	private transient Thread _thread;
	private JScrollPane jscrollp;
	private JEditorPane editorPane;
	protected boolean stop=false;
	private boolean autoscroll = false;
	private static String version = "2.15";
	
	protected static final String HELP_DOC_PATH=Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"doc"+File.separator+"help"+File.separator;

	public AboutDialog(JFrame owner) {
		super(owner); // to have the same icon than the owner
		
		setTitle("ATK - release "+version);

		JPanel frontPanel = new JPanel(new BorderLayout());
		String header = "<html><br><center><b>ATK - release "+version+"</b></center>"+
		"<center>Copyright (C) 2007 - 2012 France Télécom – All rights reserved</center>"+ "<br>" + 
		"</html>";
		frontPanel.add(new JLabel(header, JLabel.CENTER), BorderLayout.CENTER);
		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.setPreferredSize(new Dimension(600, 350));
		//textPanel.setDoubleBuffered(true);
		//textPanel.setFont(new Font("Monospaced", 0, 11));       

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		String aboutURL = HELP_DOC_PATH+"about.html";
		try {
			editorPane.setPage("file:///"+aboutURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		jscrollp = new JScrollPane( editorPane );
		jscrollp.setAutoscrolls(true);

//		MouseMotionListener doScroll1 = new MouseMotionAdapter() {
//		public void mouseDragged(MouseEvent e) {
//		if (stop) {
//		stop=false;
//		_thread=new ScrollerThread(jscrollp);
//		_thread.start();
//		}
//		}
//		jscrollp.addMouseMotionListener(doScroll1);
//		editorPane.addMouseMotionListener(doScroll1);
//		jscrollp.getVerticalScrollBar().addMouseMotionListener(doScroll1);

		MouseListener doScroll2 = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				stop=true;
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		};
		jscrollp.addMouseListener(doScroll2);
		editorPane.addMouseListener(doScroll2);
		jscrollp.getVerticalScrollBar().addMouseListener(doScroll2);

		MouseWheelListener doScroll3 = new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				stop=true;
			}
		};
		jscrollp.addMouseWheelListener(doScroll3);
		jscrollp.getVerticalScrollBar().addMouseWheelListener(doScroll3);

		textPanel.add(jscrollp, BorderLayout.CENTER);
		//textPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		Container contentPaneFrame = this.getContentPane();
		contentPaneFrame.add(frontPanel, BorderLayout.NORTH);
		contentPaneFrame.add(textPanel, BorderLayout.CENTER);
		pack(); 

		int dec_x = (owner.getWidth()-this.getWidth())/2;
		int dec_y = (owner.getHeight()-this.getHeight())/2;
		this.setLocation(owner.getLocation().x+dec_x,
				owner.getLocation().y+dec_y);

		setVisible(true);
	}

	// Methode appelée pour montrer la fenètre
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (autoscroll) {
			if (b) {
				if (_thread==null) {
					_thread = new ScrollerThread(jscrollp);
					_thread.setDaemon(true);
					_thread.setPriority( Thread.NORM_PRIORITY );
				}
				stop = false;
				_thread.start();
			} else {
				_thread=null;
				stop = true;
			}
		}
	}    

	public class ScrollerThread extends Thread {

		private JScrollPane _jscrollpane;
		private int incr = 1;
		private int max;     
		private int min;

		public ScrollerThread(JScrollPane jsp) {
			_jscrollpane = jsp;
			max = _jscrollpane.getVerticalScrollBar().getModel().getMaximum() 
			- _jscrollpane.getVerticalScrollBar().getModel().getExtent();
			min = _jscrollpane.getVerticalScrollBar().getModel().getMinimum();
		}

		public void run() {
			while ( !stop )	{
				if (_jscrollpane.getVerticalScrollBar().getModel().getValue() >= max) incr = -1;
				if (_jscrollpane.getVerticalScrollBar().getModel().getValue() <= min) incr = 1;

				try {
					_jscrollpane.getVerticalScrollBar().getModel().setValue( _jscrollpane.getVerticalScrollBar().getModel().getValue() + incr );
				} catch (ArrayIndexOutOfBoundsException aiobe) {
					// nop
				}
//				Rectangle newVisibleRectangle = new Rectangle(_editorPane.getVisibleRect().x, _editorPane.getVisibleRect().y+incr, 1, 1);
//				_editorPane.scrollRectToVisible(newVisibleRectangle);
				try { 
					Thread.sleep( 100 ); 
				} catch (java.lang.InterruptedException ie) { 
					/*ie.printStackTrace();*/ 
					// java.lang.InterruptedException levée quand la fenetre se cache (voir AboutDialog.hide())
				}
			}
		}
	}
}