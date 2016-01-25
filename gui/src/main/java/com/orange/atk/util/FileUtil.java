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
 * File Name   : FileUtil.java
 *
 * Created     : 05/03/2010
 * Author(s)   : gottella
 */
package com.orange.atk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.orange.atk.launcher.LaunchJATK;

public class FileUtil {
	public static boolean copyfile(File newfile,File originalFile)
	{
		if(originalFile.exists())
		{
			Logger.getLogger(LaunchJATK.class ).
			debug("copy file "+originalFile.getPath()+" to "+newfile.getPath());
			if(newfile.exists())
			{
				if(!newfile.delete())
					Logger.getLogger(LaunchJATK.class ).warn("Can't delete dir "+newfile.getPath());
			}

			//copy file to output dir	
			try {
				newfile.createNewFile();
			} catch (IOException e1) {
                Logger.getLogger(LaunchJATK.class ).error("Could not create "+newfile.getPath());
                return false;
			}
			FileChannel in = null; // canal d'entrée
			FileChannel out = null; // canal de sortie

			try {
				// Init
				in = new FileInputStream(originalFile).getChannel();
				out = new FileOutputStream(newfile).getChannel();

				// Copie depuis le in vers le out
				in.transferTo(0, in.size(), out);
			} catch (Exception e) {
                Logger.getLogger(LaunchJATK.class ).error("Could not copy "+newfile.getPath());
                return false;
			} finally {
				if(in != null) {
					try {
						in.close();
					} catch (IOException e) {}
				}
				if(out != null) {
					try {
						out.close();
					} catch (IOException e) {}
				}
			}
		}
		return true;
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns false.
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static String createOrDeleteDir(String d) {
		Logger.getLogger(FileUtil.class).debug("createOrDelete " + d);

		File dir = new File(d);
		if(dir.exists()){
			if(!FileUtil.deleteDir(dir))
				return("Can't delete dir "+ dir.getPath() +" It may be used by an other program or check file permission" );

			if(!dir.mkdir())
				return("Can't make dir "  + dir.getPath() +" Please check file permission");

		} else {
			if(!dir.mkdir())
				return("Can't make dir "  + dir.getPath() +" Please check file permission");

		}
		return "ok";
	}
}
