package com.orange.atk.phone;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class PluginManager {
	private static ArrayList<Plugin> plugins=new ArrayList<Plugin>();
	
	public static void register(String className){
		Logger.getLogger("PluginManager").info("registering className "+className);
		Class theClass;
		try {
			theClass = Class.forName(className);
			Plugin plugin = (Plugin)theClass.newInstance();
			plugins.add(plugin);
			Logger.getLogger("PluginManager").info(""+className+" registered");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Plugin> getAll(){
		return plugins;
	}
}
