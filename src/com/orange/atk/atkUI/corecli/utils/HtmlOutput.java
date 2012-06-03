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
 * File Name   : HtmlOutput.java
 *
 * Created     : 19/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.utils;

/**
 * A class that contains useful functions to create HTML or regular text.
 * A boolean parameter select whether the output is in text or html mode.
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class HtmlOutput {

	public static boolean apacheMode = false;

	/**
	 * Quote the four special HTML characters
	 */
	public static String unhtmlize(String s) {
		if (s==null) return "";
		if (s.indexOf('<') >= 0 || s.indexOf('>') >= 0 || s.indexOf('"') >= 0 || s.indexOf('&') >= 0)
			return (s.replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("\\\"","&quot;"));
		else return s;
	}

	public static String link(String s, String url) {
		return ("<a href=\"" + url + "\">" + s + "</a>\n");
	}

	public static String warning(String msg){
		if (apacheMode) return (paragraph("<b class=\"matosOrange\">Warning: </b>"+bold(msg)));
		else return (paragraph(bold(color("orange", "Warning: "))+bold(msg)));
	}

	public static String paragraph(String s) {
		return ("<p>" + s + "</p>\n");
	}

	public static String color(String color, String s) {
		if (apacheMode){
			if (color.equals("orange")) return ("<font color=\"" + color + "\" class=\"matosOrange\">" + s + "</font>");
			else if (color.equals("green")) return ("<font color=\"" + color + "\" class=\"matosGreen\">" + s + "</font>");
			else if (color.equals("blue")) return ("<font color=\"" + color + "\" class=\"matosBlue\">" + s + "</font>");
			else if (color.equals("red")) return ("<font color=\"" + color + "\" class=\"matosRed\">" + s + "</font>");
			else return ("<font color=\"" + color + "\">" + s + "</font>");
		} else return ("<font color=\"" + color + "\">" + s + "</font>");
	}

	public static String header(int i, String s) {
		if (apacheMode) return ("<h" + i + " class=\"matos\">" + s + "</h" + i + ">\n");
		else  return ("<h" + i + ">" + s + "</h" + i + ">\n");
	}

	public static String bold(String s) {
		return ("<b>" + s + "</b>");
	}

	public static String cell(String opt, String s) {
		return ("  <td" + opt + ">" + s + "</td>\n");
	}

	public static String cell(String s) {
		return ("  <td>" + s + "</td>\n");
	}

	public static String cellHead(String s) {
		return ("  <th>" + s + "</th>\n");
	}

	public static String row(String opt, String s) {
		return ("<tr" + opt + ">" + s + "</tr>\n");
	}

	public static String row(String s) {
		return ("<tr>" + s + "</tr>\n");
	}

	public static String openTable() {
		if (apacheMode) return "<table id=\"matosTable\">";
		else return "<table>";
	}

	public static String closeTable() {
		return "</table>";
	}

	public static String hrule() {
		return "<hr width='100%' />";
	}
	public static String br() {
		return "<br />";
	}

	public static String list(Object l[]) {
		StringBuffer buf = new StringBuffer();
		buf.append("<ul type=\"disc\">\n");
		for(int i=0; i < l.length; i++) {
			buf.append ("<li>"); buf.append(l[i]); buf.append("</li>\n");
		}
		buf.append("</ul>\n");
		return buf.toString();
	}
}
