/*
 * Software Name : ATK
 *
 * Copyright (C) 2013 France Télécom
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
 *
 * Created     : 12/06/2013
 * Author(s)   : Pierre Crepieux
 */

package com.orange.atk.monitoring;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public class MonitoringConfig {

	private List<Graph> graphs = new ArrayList<Graph>();
	private List<Event> events = new ArrayList<Event>();
	private AroSettings aroSettings;

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(path);
		try {
			Reader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[8192];
			int read;
			while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
				builder.append(buffer, 0, read);
			}
			String xml = builder.toString();
			xml = xml.replaceAll("graphlist", "graphs").replaceAll("markerlist", "events");
			return xml;
		} finally {
			// Potential issue here: if this throws an IOException,
			// it will mask any others. Normally I'd use a utility
			// method which would log exceptions and swallow them
			stream.close();
		}
	}

	private static XStream getStreamer() {
		XStream xstream = new XStream();
		xstream.alias("confile", MonitoringConfig.class);

		xstream.alias("graph", Graph.class);
		xstream.useAttributeFor(Graph.class, "name");
		xstream.useAttributeFor(Graph.class, "color");
		xstream.useAttributeFor(Graph.class, "xcomment");
		xstream.useAttributeFor(Graph.class, "ycomment");
		xstream.useAttributeFor(Graph.class, "unit");
		xstream.useAttributeFor(Graph.class, "scale");
		xstream.useAttributeFor(Graph.class, "sampled");
		xstream.useAttributeFor(Graph.class, "type");

		xstream.alias("marker", Event.class);
		xstream.useAttributeFor(Event.class, "name");
		xstream.useAttributeFor(Event.class, "color");
		xstream.useAttributeFor(Event.class, "position");

		xstream.aliasField("aro", MonitoringConfig.class, "aroSettings");
		xstream.useAttributeFor(AroSettings.class, "enabled");
		return xstream;
	}

	public static MonitoringConfig fromFile(String filename) throws IOException {
		XStream xstream = getStreamer();
		return (MonitoringConfig) xstream.fromXML(readFile(filename));
	}

	public void toFile(String filename) {
		XStream xstream = getStreamer();
		String xml = xstream.toXML(this);
		// Not very clean but will do the job for the moment
		// (there is no way with xstream to alias the list name)
		xml = xml.replaceAll("graphs", "graphlist").replaceAll("events", "markerlist");
		try {
			FileWriter f = new FileWriter(filename);
			f.write(xml);
			f.close();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	public List<Graph> getGraphs() {
		return graphs;
	}

	public void setGraphs(List<Graph> graphs) {
		this.graphs = graphs;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public AroSettings getAroSettings() {
		return aroSettings;
	}

	public void setAroSettings(AroSettings aroSettings) {
		this.aroSettings = aroSettings;
	}

	public static void main(String args[]) {
		MonitoringConfig c = new MonitoringConfig();
		c.toFile("c:\\Temp\\config.xml");
	}

}
