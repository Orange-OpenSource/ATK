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
 * File Name   : AbstractStepDialog.java
 *
 * Created     : 26/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * Abstract class which represents dialog for creation/edition of
 * <code>step</code>.
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class AbstractStepDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	protected AuthenticationPanel authenticationPanel;

	protected String login = null;
	protected String password = null;
	protected String user_agent = null;

	protected boolean fileError = false;

	public AbstractStepDialog() {
		super(CoreGUIPlugin.mainFrame, true);
	}

	/**
	 * Shows an error message
	 * 
	 * @param msg
	 *            the error message
	 */
	protected void showError(String msg) {
		Logger.getLogger(this.getClass()).error(msg);
		JOptionPane.showMessageDialog(CoreGUIPlugin.mainFrame, msg, "Error !",
				JOptionPane.ERROR_MESSAGE);
		fileError = true;
	}

	/**
	 * Guess the short name of a file given its URI and its extension
	 * 
	 * @param uri
	 *            file URI
	 * @param ext
	 *            file extension
	 * @return short name for the given file
	 */
	public static String guessName(String uri, String ext) {
		String name = "";
		if (uri.lastIndexOf(ext) != -1) {
			if (uri.startsWith("http:") && uri.lastIndexOf(ext) > uri.lastIndexOf("/")) {
				name = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf(ext) + 3);
			} else
				if (uri.lastIndexOf(File.separator) != -1
						&& uri.lastIndexOf(ext) > uri.lastIndexOf(File.separator)) {
					name = uri.substring(uri.lastIndexOf(File.separator) + 1,
							uri.lastIndexOf(ext) + 3);
				} else {
					name = uri;
				}
		} else {
			name = uri;
		}
		return name;
	}

}
