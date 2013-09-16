/**
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
 * File Name   : TestApk.java
 *
 * Created     : 02/05/2013
 * Author(s)   : France Telecom
 */
package com.orange.atk.soloGetViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;


import com.jayway.android.robotium.solo.Solo;
import com.orange.atk.serviceSendEventToSolo.IServiceSendEvent;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
@SuppressWarnings("unchecked")
public class TestApk extends ActivityInstrumentationTestCase2{
	private static Class launcherActivityClass;
	private static  String TARGET_PACKAGE_ID;
	private Solo solo;
	protected ServiceConnection remoteConnection=null;
	private IServiceSendEvent serviceb=null;
	private Context ctx;
	private static String logTag="ROBOTIUM GET VIEWS";
	private XmlSerializer xmlSerializer ;
	private float maxWidh;
	private float maxheight;
	private ExecuteSoloCommand executeSoloCommand;
	private String cmd;
	private String viewsPrec=null;
	public TestApk()throws ClassNotFoundException {
		super(null,null);
		xmlSerializer = Xml.newSerializer();

	}
	@Override
	protected void setUp() {
		InputStream stream = TestApk.class.getResourceAsStream("init.prop");
		if(stream!=null){
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			try {
				line=br.readLine();
				if(line!=null){
					try{
						line=line.replace("\n", "");
						launcherActivityClass=Class.forName(line);
					} catch (ClassNotFoundException e){
						Log.e(logTag,e.getMessage(),e);
					} 
				}
				line=br.readLine();
				if(line!=null){
					TARGET_PACKAGE_ID=line.replace("\n", "");
				}
				br.close();
			} catch (IOException e) {
				Log.e(logTag,e.getMessage(),e);
			}
		}
		ctx=getInstrumentation().getContext();
		Intent intent = new Intent();
		intent.setClassName("com.orange.atk.serviceSendEventToSolo", "com.orange.atk.serviceSendEventToSolo.ServiceSendEvent");
		remoteConnection  = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				serviceb = IServiceSendEvent.Stub.asInterface(service);
			}
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};

		ctx.bindService(intent, remoteConnection, Context.BIND_AUTO_CREATE);
		Activity a= launchActivity(TARGET_PACKAGE_ID, launcherActivityClass, null);
		super.setActivity(a);
		solo = new Solo(getInstrumentation(),getActivity());
		executeSoloCommand=new ExecuteSoloCommand(solo);
	}

	public void testDisplayBlackBox() throws Throwable {  
		do {
			if(remoteConnection!=null) {
				try  {
					cmd=serviceb.getViewsCommand();
					if(!cmd.equalsIgnoreCase("")&&!cmd.equalsIgnoreCase("exit")) {
						if(cmd.equalsIgnoreCase("views")){
							String views=getviewXML();
							if(viewsPrec==null){
								viewsPrec=views;
								try {
									serviceb.setViews(views);
								} catch (RemoteException e) {
									Log.e(logTag,e.getMessage());
								}
							} else {
								if(viewsPrec.equals(views)){
									for(int i=0;i<4;i++) {
										try {
											Thread.sleep(500);
										} catch (InterruptedException ignored) {}
										views=getviewXML();
										if(!viewsPrec.equals(views)) {
											viewsPrec=views;
											break;
										}
									}
								}
								try {
									serviceb.setViews(views);
								} catch (RemoteException e) {
									Log.e(logTag,e.getMessage());
								}
							}
						} else {
							String[] tab = cmd.split(",");
							if(executeSoloCommand.execute(tab)) {
								serviceb.setViews(cmd +" sucess");
							} else {
								serviceb.setViews(cmd +" error");
							}
							Log.d(logTag, cmd);
						}
					}
				} catch (RemoteException e) {
					Log.d(logTag,e.getMessage(),e);	
				}
			}
		} while(!cmd.equalsIgnoreCase("exit"));
	}

	public String getviewXML (){

		StringWriter writer = new StringWriter();

		try {
			xmlSerializer.setOutput(writer);
			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.startTag(null, "hierarchy");
			xmlSerializer.attribute(null, "rotation","0");
		} catch (IllegalArgumentException e) {
			Log.e(logTag,e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(logTag,e.getMessage());
		} catch (IOException e) {
			Log.e(logTag,e.getMessage());
		}



		ArrayList<View> liste1= solo.getViews();
		View TopParent= solo.getTopParent(liste1.get(liste1.size()-1));
		int[] xyTop = new int[2];
		TopParent.getLocationOnScreen(xyTop);
		maxWidh =xyTop[0]+ TopParent.getWidth();
		maxheight= xyTop[1]+TopParent.getHeight();

		prepareXmlFile(TopParent);

		try {
			xmlSerializer.endTag(null, "hierarchy");
			xmlSerializer.endDocument();
		} catch (IllegalArgumentException e) {
			Log.e(logTag,e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(logTag,e.getMessage());
		} catch (IOException e) {
			Log.e(logTag,e.getMessage());
		}
		Log.d(logTag, writer.toString());

		return writer.toString();
	}



	public ArrayList<View> getDirectChildViews(View parent) {
		ArrayList<View> liste= solo.getViews(parent);
		ArrayList<View> listeWithoutParent =new ArrayList<View>();
		ArrayList<View> listeDirectChild=new ArrayList<View>();
		for (int i=1; i<liste.size(); i++) {
			listeWithoutParent.add(liste.get(i));
		}
		for (int i=0; i<listeWithoutParent.size(); i++) {
			if (((View) listeWithoutParent.get(i).getParent()).equals(parent)) {
				listeDirectChild.add(listeWithoutParent.get(i));	
			}
		}
		return listeDirectChild;
	}


	public void prepareXmlFile (View v) {

		ArrayList<View> listeViews= getDirectChildViews(v);
		for(int i=0; i<listeViews.size(); i++) {
			int[] xy = new int[2];
			listeViews.get(i).getLocationOnScreen(xy);
			if((xy[0]<=maxWidh) && (xy[1]<=maxheight)) {
				try {
					xmlSerializer.startTag(null, "node");
				} catch (IllegalArgumentException e) {
					Log.e(logTag,e.getMessage());
				} catch (IllegalStateException e) {
					Log.e(logTag,e.getMessage());
				} catch (IOException e) {
					Log.e(logTag,e.getMessage());
				}

				printNode(listeViews.get(i), i);
			}

			prepareXmlFile (listeViews.get(i));

			if((xy[0]<=maxWidh) && (xy[1]<=maxheight)) {
				try {
					xmlSerializer.endTag(null, "node");
				} catch (IllegalArgumentException e) {
					Log.e(logTag,e.getMessage());
				} catch (IllegalStateException e) {
					Log.e(logTag,e.getMessage());
				} catch (IOException e) {
					Log.e(logTag,e.getMessage());
				}
			}

		}
	}
	public void printNode(View view, int index) {

		boolean checkable=false;
		boolean checked=false;
		boolean clickable=view.isClickable();
		boolean enabled=view.isEnabled();
		boolean focusable=view.isFocusable();
		boolean focused=view.isFocused();
		boolean scrollable=false;
		boolean long_clickable=view.isLongClickable();
		boolean selected=view.isSelected();

		String text="";
		String classe="";
		String packg="";
		String cont_desc="";
		String password="";


		if(view.getClass().getName()!=null) {
			classe=view.getClass().getName();

		}
		if(view.getContext().getPackageName()!=null) {
			packg=view.getContext().getPackageName();

		}
		if(view.getContentDescription()!=null) {
			cont_desc=(String)view.getContentDescription();
		}


		if(view instanceof TextView) {
			TextView tv= (TextView) view;
			text=tv.getText().toString();
		} else {
			if(view instanceof Button) {

				Button b= (Button) view;
				text= b.getText().toString();
			}
		}

		int[] xy = new int[2];
		view.getLocationOnScreen(xy);
		try {

			xmlSerializer.attribute(null, "index", String.valueOf(index));
			xmlSerializer.attribute(null, "text", text);
			xmlSerializer.attribute(null, "class",classe );
			xmlSerializer.attribute(null, "package", packg);
			xmlSerializer.attribute(null, "content-desc", cont_desc);
			xmlSerializer.attribute(null, "checkable", String.valueOf(checkable));
			xmlSerializer.attribute(null, "checked", String.valueOf(checked));
			xmlSerializer.attribute(null, "clickable", String.valueOf(clickable));
			xmlSerializer.attribute(null, "enabled", String.valueOf(enabled));
			xmlSerializer.attribute(null, "focusable", String.valueOf(focusable));
			xmlSerializer.attribute(null, "focused", String.valueOf(focused));
			xmlSerializer.attribute(null, "scrollable", String.valueOf(scrollable));
			xmlSerializer.attribute(null, "long-clickable", String.valueOf(long_clickable));
			xmlSerializer.attribute(null, "password",password);
			xmlSerializer.attribute(null, "selected", String.valueOf(selected));
			//pattern [x,y][x+width,y+height]
			xmlSerializer.attribute(null, "bounds", "["+ xy[0]+","+ xy[1]+"]["+(xy[0]+view.getWidth())+","+(view.getHeight()+ xy[1])+"]");

		} catch (IllegalArgumentException e) {
			Log.e(logTag,e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(logTag,e.getMessage());
		} catch (IOException e) {
			Log.e(logTag,e.getMessage());
		}
	}
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		ctx.unbindService(remoteConnection);
	}

}