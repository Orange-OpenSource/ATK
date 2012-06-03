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
 * File Name   : CommentNode.java
 *
 * Created     : 26/10/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.scriptRecorder.scriptJpanel;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
class CommentNode extends DefaultMutableTreeNode {

	

	/**
	 * @param userObject
	 */
	public CommentNode(Object userObject) {
		super(userObject);
	}

	/**
	 * @param userObject
	 * @param allowsChildren
	 */
	public CommentNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

}
