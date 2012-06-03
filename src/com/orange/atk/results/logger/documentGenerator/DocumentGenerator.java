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
 * File Name   : DocumentGenerator.java
 *
 * Created     : 09/04/2008
 * Author(s)   : France Telecom
 */
package com.orange.atk.results.logger.documentGenerator;

import com.orange.atk.results.logger.log.DocumentLogger;


/**
 * Interface for the document generator. If a new document generator has to be
 * added to the interpreter, it should implements this interface.
 */

public interface DocumentGenerator {

	/**
	 * Function used to create and fill the report ouput. Classes which will
	 * extend Logger have access to logged informations through getMsgsLoggued,
	 * getBatlist, getMemlist, getStolist & getCpulist.
	 * 
	 * @param isParseException
	 *            true if a ParseException is happened
	 * @param dl
	 *            document which represents data logged
	 */
	public void dumpInStream(boolean isParseException, DocumentLogger dl);
}
