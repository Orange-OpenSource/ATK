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
 * File Name   : Emulator.java
 *
 * Created     : 16/06/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.orange.atk.phone.PhoneException;
import com.orange.atk.util.Position;




public class Emulator extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5706132534486760234L;
	private ScriptController controller;
	private Screenshot imageLabel;
//	final String default_image = Platform.TMP_DIR+"screenshots"+Platform.FILE_SEPARATOR+"s.jpeg";

	private Date startPress= new Date();
	private Date stopPress= new Date();
	
	//for build keyboards
	private Set<String> keysList;
	private HashMap<String, String> keysIcons;
	private boolean landscapeformat=false;
	
	
	public Emulator(final RecorderFrame owner, ScriptController sc) throws HeadlessException {
		super();
		controller=sc;
		this.setLocationRelativeTo(owner);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0; c.gridy=0;c.gridwidth=1; c.gridheight=5;
		c.fill=GridBagConstraints.BOTH; c.weightx=1; c.weighty=1;
		
		//images
		try {
			imageLabel = new Screenshot((BufferedImage) sc.getPhone().screenShot());
		} catch (PhoneException e) {
			e.printStackTrace();
		}
		add(imageLabel,c);
		
		String[] layout = sc.getPhone().getKeyLayouts();
		keysIcons = sc.getPhone().getKeys();
		keysList = keysIcons.keySet();
		
		
		//Standards keyboards
		c.gridheight=1;
		c.gridx=1;
		if(layout!=null) {
			for(int i=0 ; i<layout.length ; i++) {
				if(layout[i].equals("QWERTY"))
					add(buildQwertyKeyboard(),c );
				if(layout[i].equals("9KEY_NAVIGATION"))
					add(build9KeyNavigation(),c);
				if(layout[i].equals("ANDROID_NAVIGATION"))
					add(buildAndroidNavigation(),c);
				if(layout[i].equals("PHONE"))
					add(buildPhoneKeyboard(),c);
				c.gridy++;
			}
		}
		
		//toolbar
		JPanel jtb = new JPanel(new FlowLayout());
		jtb.setBorder(BorderFactory.createTitledBorder("Tool bar"));

		final JComboBox otherkey = new JComboBox();
		otherkey.addItem("-select a key-");
		//show all keys wich aren't show yet
		for(String key : keysList) 
			otherkey.addItem(key);
		
		otherkey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key_Pressed = (String) ((JComboBox) e.getSource()).getSelectedItem();
				String value_Pressed_Key = "";
				
				for(int i = 0; i<keysIcons.size(); i++){
					if (keysIcons.containsKey(key_Pressed))
						value_Pressed_Key = (String) keysIcons.get(key_Pressed);
				}
				
				Logger.getLogger(this.getClass() ).debug("Press : "+key_Pressed+" Value : "+value_Pressed_Key);
				startPress = new Date();
				if (stopPress!=null)
					controller.addEvent("Sleep("+(startPress.getTime()-stopPress.getTime())+")");	
				stopPress=new Date();
				
				controller.addEvent("Key('"+value_Pressed_Key+"', 0, 0 )");
				try {
					controller.getPhone().keyPress(value_Pressed_Key, 0, 0);
					//controller.getPhone().keyPress(key_Pressed, 0, 0);
					// MMH not needed, screenshot thread do i 
					// setImage((BufferedImage) controller.getPhone().screenShot());
				} catch (PhoneException e1) {
					e1.printStackTrace();
				}
				otherkey.setSelectedIndex(0);
			}	
		});

		
		jtb.add(new JLabel("Other keys : "));
		jtb.add(otherkey);
		JButton rotate = new JButton("Rotate");
		rotate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				landscapeformat= !landscapeformat ;
				// MMH not needed, screenshot thread do i 
				/*				try {
					setImage((BufferedImage) controller.getPhone().screenShot());
				} catch (PhoneException e) {
					Logger.getLogger(this.getClass() ).warn("error on refresh when rotate "+e.getMessage());
				}*/
			}
		});
		jtb.add(rotate);
		add(jtb,c);
		
		//Keylistener
		startPress= new Date();
		stopPress= new Date();

		
		//global parameter
	
		//setResizable(false); //do not active because rotation of the screen need a resizable window
		setTitle("Emulator");
		pack();
		setVisible(true);
	//	setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent arg0) {
				imageLabel.stop();
				owner.stop();
			}
		});
		
		
	}

	private Component buildAndroidNavigation() {
		JPanel phonekeyboard = new JPanel(new GridLayout(1,5));
		phonekeyboard.setBorder(BorderFactory.createTitledBorder("Android Navigation Keys") );
		for (String key : new String[]{"CALL","HOME","MENU","BACK","END_CALL"}){
			String icone = keysIcons.get(key);
			phonekeyboard.add(new KeyTouch(icone,key));
			keysList.remove(key);
		}
		return phonekeyboard;
	}

	private Component build9KeyNavigation() {
		JPanel phonenavigation = new JPanel(new GridBagLayout());
		phonenavigation.setBorder(BorderFactory.createTitledBorder("Phone Navigation") );
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0; c.gridy=0;c.weightx=1; c.weighty=0;
		
		//softkey 1
		String icone = keysIcons.get("[");
		phonenavigation.add(new KeyTouch(icone,"["),c);
		keysList.remove("[");

		//uparrow
		c.gridheight=1; c.gridx=2;
		icone = keysIcons.get("^");
		phonenavigation.add(new KeyTouch(icone,"^"),c);
		keysList.remove("^");
		
		//softkey 2
		c.gridheight=2; c.gridx=4;
		icone = keysIcons.get("]");
		phonenavigation.add(new KeyTouch(icone,"]"),c);
		keysList.remove("]");
		
		//leftarrow
		c.gridheight=2; c.gridx=1;c.gridy=1;
		icone = keysIcons.get("<");
		phonenavigation.add(new KeyTouch(icone,"<"),c);
		keysList.remove("<");
		
		//rightarrow
		c.gridx=3;
		icone = keysIcons.get(">");
		phonenavigation.add(new KeyTouch(icone,">"),c);
		keysList.remove(">");
		
		//centerpress
		 c.gridx=2;
		icone = keysIcons.get(":J");
		phonenavigation.add(new KeyTouch(icone,":J"),c);
		keysList.remove(":J");
		
		//green phone key
		/*c.gridheight=2; c.gridx=0;c.gridy=2;
		icone = keysIcons.get(":J");
		phonenavigation.add(new KeyTouch(icone,":J"),c);
		keysList.remove(":J");
		*/
		//down arrow
		c.gridheight=1; c.gridx=2;c.gridy=3;
		icone = keysIcons.get("V");
		phonenavigation.add(new KeyTouch(icone,"V"),c);
		keysList.remove("V");
		
		//red phone key
		c.gridheight=2; c.gridx=4;c.gridy=2;
		icone = keysIcons.get("E");
		phonenavigation.add(new KeyTouch(icone,"E"),c);
		keysList.remove("E");
		
		
		return phonenavigation;
	}


	/**
	 * 
	 * @return a JPanel representing a phone number keyboard
	 */
	private JPanel buildPhoneKeyboard() {
		JPanel phonekeyboard = new JPanel(new GridLayout(4,3));
		phonekeyboard.setBorder(BorderFactory.createTitledBorder("Phone Number Keys") );
		for (String key : new String[]{"1","2","3","4","5","6","7","8","9","*","0","#"}){
			String icone = keysIcons.get(key);
			phonekeyboard.add(new KeyTouch(icone,key));
			keysList.remove(key);
		}
		return phonekeyboard;
	}

	
	
	private Component buildQwertyKeyboard() {
		JPanel Qwertykeyboard = new JPanel(new GridBagLayout());

		Qwertykeyboard.setBorder(BorderFactory.createTitledBorder("Qwerty Keyboard") );
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0; c.gridy=0;c.gridwidth=1; c.gridheight=1;
		c.fill=GridBagConstraints.BOTH; c.weightx=0.1; c.weighty=0.1;
		
		int i=0;
		for (String key : new String[]{"1","2","3","4","5","6","7","8","9","0",
						"Q","W","E","R","T","Y","U","I","O","P",
						"A", "S", "D", "F", "G", "H", "J", "K", "L","DEL",
						"Z", "X", "C", "V", "B", "N", "M", "COMMA","ENTER",
						"SHIFT_L", "ALT_L", "SEARCH", "@","SPACE", "POINT", "ALT_R","SHIFT_R"}){

			String icone = keysIcons.get(key);
			c.gridy = (int) i/10;
			c.gridx = i %10;
			if (key.equals("ENTER"))
				c.gridwidth=2;
			if (key.equals(" "))
				c.gridwidth=3;
			
			Qwertykeyboard.add(new KeyTouch(icone,key), c);
			i++;
			if(key.equals("ENTER")) {
				i++;
				c.gridwidth=1;
			}
			if(key.equals(" ")) {
				i=i+2;
				c.gridwidth=1;
			}
				
			keysList.remove(key);
		}
		
		
		//TODO: find a synchronization
		try {//Time to load every Image by the Toolkit
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		return Qwertykeyboard;
	}

	public void setImage(Image im){
	//	Logger.getLogger(this.getClass() ).debug(default_image);		
		imageLabel.setImage(im,landscapeformat);
		pack();
	}

	
	
	
	
	
	
	
	/**
	 * class represent button when pressed
	 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
	 *
	 */
	class KeyTouch extends JPanel {
		private static final long serialVersionUID = 7533329665423237475L;
		private Image img = null;
		private String key_Pressed;
		
		KeyTouch(String path, String key) {
			super();
			key_Pressed = key;
			img = getToolkit().createImage(getClass().getResource(path));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Logger.getLogger(this.getClass() ).debug("Press :"+key_Pressed);
					startPress = new Date();
					if (stopPress!=null)
						controller.addEvent("Sleep("+(startPress.getTime()-stopPress.getTime())+")");	
					stopPress=new Date();
					controller.addEvent("Key('"+key_Pressed+"', 0, 0 )");	
					try {
						controller.getPhone().keyPress(key_Pressed, 0, 0);
					} catch (PhoneException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			setMinimumSize(new Dimension(img.getWidth(this), img.getHeight(this)));
		}
		
		public void paint(Graphics g) {
			super.paintComponent(g);
			if(img != null) 
				g.drawImage(img, 0, 0, this); 
		}
		
		public Dimension getPreferredSize() {

//Logger.getLogger(this.getClass() ).debug("getpreferedsize(on "+key_Pressed + " "+img.getWidth(this)+" "+img.getHeight(this));
			return new Dimension(img.getWidth(this),img.getHeight(this));
		}
	}
	
	
	
	/**
	 * class showing screenshot
	 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
	 *
	 */
	class Screenshot extends JPanel {

		private static final long serialVersionUID = 5714691002759634005L;
		private Image img = null;
		private Thread autorefresh;
		private boolean done = false;
		
		public Screenshot(Image im) {
			super();
			img = im;
			
			addMouseListener(new MouseAdapter(){
				
				private int yorigin;
				private int xorigin;

				public void mousePressed(MouseEvent arg0) {
					Logger.getLogger(this.getClass() ).debug("mousePress"+arg0.getX()+","+arg0.getY());
					startPress=new Date();
					xorigin=arg0.getX();
					yorigin=arg0.getY();
					
					if (stopPress!=null)		
						controller.addEvent("Sleep("+(startPress.getTime()-stopPress.getTime())+")");
					
				}

				public void mouseReleased(MouseEvent arg0) {
					Logger.getLogger(this.getClass() ).debug("mouseRelease"+arg0.getX()+","+arg0.getY());
					ArrayList<Position> positions = new ArrayList<Position>();
					stopPress=new Date();
					int x=arg0.getX();
					int y=arg0.getY();
					if (x!=xorigin && y!=yorigin && arg0.getButton() != MouseEvent.BUTTON1){
						//Slide
						Logger.getLogger(this.getClass() ).debug("listener.runSlide");
						//controller.addEvent("SlideEvent",new String[]{""+xorigin,""+yorigin,""+x,""+y});
						try {
							Position posOrigin = new Position(xorigin, yorigin, 0);
							Position posFinal = new Position(x, y, 0);
							positions.add(posOrigin);
							positions.add(posFinal);
							controller.getPhone().touchScreenSlide(positions);
							refresh();
						} catch (PhoneException e) {
							e.printStackTrace();
						}
							
					}else{
						//Mouse
						Logger.getLogger(this.getClass() ).debug("listener.runMousereleased");
						//controller.addEvent("Mouse",new String[]{""+xorigin,""+yorigin});
						try {
							controller.getPhone().touchScreenPress(new Position(x, y,stopPress.getTime() - startPress.getTime()));
							refresh();
						} catch (PhoneException e) {
							e.printStackTrace();
						}
						
					}
				}
			});	
			
			
			
			//Auto-Refresh (3 fps to 4 fps)
			autorefresh = new Thread(){
				@Override
				public void run() {
					try {
						while(!done) {
							Image phoneimg = (BufferedImage) controller.getPhone().screenShot() ;
							setImage(phoneimg, landscapeformat);
							repaint();
							sleep(100);
						}
						
					//interupt in order to stop it
					} catch (InterruptedException e) {
						Logger.getLogger(this.getClass() ).warn("screenshot auto refresh interrupted");
					} catch (PhoneException e) {
						Logger.getLogger(this.getClass() ).warn("screenshot auto refresh error");
					}
				}
			};
			autorefresh.setPriority(Thread.MIN_PRIORITY);
			autorefresh.setDaemon(true);
			autorefresh.start();
			
		}
		
		public void refresh(){
			try {
				Image phoneimg = (BufferedImage) controller.getPhone().screenShot() ;
				setImage(phoneimg, landscapeformat);
				repaint();
				
			} catch (PhoneException e) {
				Logger.getLogger(this.getClass() ).warn("screenshot auto refresh error");
			}
		}
		
		public void stop() {
			done = true;
		}

		public void paint(Graphics g) {
			super.paintComponent(g);
			if(img != null) 
				g.drawImage(img, 0, 0, this); 
		}
		
		public Dimension getPreferredSize() {
			if(img != null)  {
//Logger.getLogger(this.getClass() ).debug("get pref size screenshot"+img.getWidth(this)+" "+img.getHeight(this) );
				return new Dimension(img.getWidth(this),img.getHeight(this));
				}
			return new Dimension(20,20);
		}
	
		
		public void setImage(Image img, boolean Landscapeformat) {

			if(Landscapeformat) {
				 float angle = (float) Math.toRadians(-90);
				   // Gets the rotation center.

				     float centerX = img.getWidth(this) / 2f;
				     float centerY = img.getHeight(this) / 2f;
				      
				    // Rotates the original image.
				    ParameterBlock pb = new ParameterBlock();
				   pb.addSource(img);
				    pb.add(centerX);
				    pb.add(centerY);
				    pb.add(angle);
				    pb.add(new InterpolationBilinear());
				    // Creates a new, rotated image and uses it on the DisplayJAI component
				   this.img = JAI.create("rotate", pb).getAsBufferedImage();
			} else {
				this.img = img;
			}
			repaint();


		}
		
		
	}
	
}
