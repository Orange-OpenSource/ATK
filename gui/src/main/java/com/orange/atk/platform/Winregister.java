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
 * File Name   : Winregister.java
 *
 * Created     : 03/08/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Winregister {
String result =null;

	
	
public String getResult() {
	return result;
}


public  Winregister()
{
	
}

public  static String getRegisterValue(String RegPath ,String registername,String Defaultvalue,String regType)
{
	String Registervalue ="";
	/*
	 * Here is a sample output of the command used to read the register:
	 * H:\>reg query HKLM\SOFTWARE\Wow6432Node\ATK\Components /v ATKPath
	 *
	 * HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\ATK\Components
     *     ATKPath    REG_SZ    C:\Program Files (x86)\ATK
	 */
	String pattern = "\\s*(~?\\w+)\\s*(\\w+)\\s{1,4}(.*)";
	Pattern p = Pattern.compile(pattern);
	if (Platform.OS_NAME.toLowerCase().contains("windows")) {
		Process process;
		try {
			process = Runtime.getRuntime().exec(
			// clef de registre windows pour obtenir le port COM
					new String[] { "REG.EXE", "QUERY", "\""+RegPath+"\"", "/v", registername});
						//	"HKLM\\SOFTWARE\\JATK\\Components" });
							
				//	Logger.getLogger(this.getClass() ).debug("REG.EXE"+ "QUERY"+ RegPath+ "/v"+ registername);		
			class monRun implements Runnable {
				Process process;
				public monRun(Process p) {
					process = p;
				}
				public void run() {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(process.getErrorStream()));
					String line;
					try {
						line = reader.readLine();
						while (line != null) {
							line = reader.readLine();
							 Logger.getLogger(this.getClass() ).debug("- "+line);
						}
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			new Thread(new monRun(process)).start();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();

			//System.out.flush();

			while (((line != null) )) {
				line = reader.readLine();
				Logger.getLogger(Winregister.class).debug("+ "+line);

				Matcher m = p.matcher(line);
				if (m.find()) {
					if (m.group(1).equalsIgnoreCase(registername)) {
						Registervalue=m.group(3);
						break;
					}
				}
			}

			reader.close();
			process.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	if(Registervalue.equals(""))
		{Registervalue=Defaultvalue;
		
		Logger.getLogger(Winregister.class ).debug("Can't get register get default value");
		}
	else
	{
		//Logger.getLogger(this.getClass() ).debug("register value :"+Registervalue);
	
	}
	return Registervalue;
	}
	
	
public  static boolean writeRegisterValue(String RegPath ,String registername,String value,String regType)
{
	
	boolean returnvalue =false;
	if (Platform.OS_NAME.toLowerCase().contains("windows")) {
		Process process;
		try {
			process = Runtime.getRuntime().exec(
			// clef de registre windows pour obtenir le port COM
					new String[] { "REG.EXE", "ADD", "\""+RegPath+"\"", "/v", "\""+registername+"\"" ,"/t",regType,"/d","\""+value+"\"","/f"});
						//	"HKLM\\SOFTWARE\\JATK\\Components" });
							
							
			class monRun implements Runnable {
				Process process;
				public monRun(Process p) {
					process = p;
				}
				public void run() {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(process.getErrorStream()));
					String line;
					try {
						line = reader.readLine();
						while (line != null) {
							line = reader.readLine();
							 Logger.getLogger(this.getClass() ).debug("/ "+line);
						}
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			new Thread(new monRun(process)).start();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			//System.out.flush();

			while (((line != null) )) {
				line = reader.readLine();
			
				 if((line != null)&&(line.contains("successfully")))
				 {
					 returnvalue=true;
 
					 
					break;

				 }
			
			}
			reader.close();
			process.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	return returnvalue;
	}	


public  Winregister(String path,String regName){

if (Platform.OS_NAME.toLowerCase().contains("windows")) {
	// Use REG.EXE
	Process process;
	try {
		process = Runtime.getRuntime().exec(
		// clef de registre windows pour obtenir le port COM
				new String[] { "REG.EXE", "QUERY",path});
					//	"HKLM\\SOFTWARE\\JATK\\Components" });
						
						
		class monRun implements Runnable {
			Process process;
			public monRun(Process p) {
				process = p;
			}
			public void run() {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(process.getErrorStream()));
				String line;
				try {
					line = reader.readLine();
					while (line != null) {
						line = reader.readLine();
						 Logger.getLogger(this.getClass() ).debug("* "+line);
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		new Thread(new monRun(process)).start();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line = reader.readLine();
		//System.out.flush();

		while (((line != null) )) {
			line = reader.readLine();
		
			 if((line != null)&&(line.indexOf(regName) != -1))
			 {
				 String[] results = line.split("dir:");
				//Logger.getLogger(this.getClass() ).debug("result  = " + results[1]);
				result=results[1];

			 }
		
		}
		reader.close();
		process.waitFor();

	} catch (IOException e) {
		e.printStackTrace();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}

}
	
	
	
}
