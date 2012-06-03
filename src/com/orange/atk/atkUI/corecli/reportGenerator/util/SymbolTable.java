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
 * File Name   : SymbolTable.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.util;

import java.util.Hashtable;

/**
 * Management of the symbols table
 * The symbol table has as key the depth of variables and as value another Hashtable
 * which contains the pair "name-value"
 * @since JDK5.0
 */
public class SymbolTable {
	private Hashtable<Integer, Hashtable<String, TypedObject>> symbols;

	public SymbolTable(){
		symbols = new Hashtable<Integer, Hashtable<String, TypedObject>>();
		//default, depth=0
		symbols.put(Integer.valueOf(0), new Hashtable<String, TypedObject>());
	}

	/**
	 * Adds a depth to the symbols table
	 * @param depth the new depth
	 */
	public void add_entry(Integer depth){
		Hashtable<String, TypedObject> depthN = new Hashtable<String, TypedObject>();
		symbols.put(depth, depthN);
		if (depth.intValue()>0){
			Hashtable<String, TypedObject> depthN_1 = symbols.get(Integer.valueOf(depth.intValue()-1));
			for (String name : depthN_1.keySet()) {
				TypedObject value = depthN_1.get(name);
				depthN.put(name, value);
			}
		}
	}

	/**
	 * Removes a depth from the symbols table
	 * @param depth
	 */
	public void remove_entry(Integer depth){
		symbols.remove(depth);
	}

	/**
	 * Adds a variable with a given depth
	 * @param depth
	 * @param name
	 */
	public void add_var(Integer depth, String name){
		symbols.get(depth).put(name, null);
	}

	/**
	 * Gives a value to a variable
	 * @param name
	 * @param value
	 */
	public void set_var(String name, TypedObject value){
		Integer current_depth=Integer.valueOf(symbols.size()-1);
		symbols.get(current_depth).put(name, value);
		//Logger.getLogger(this.getClass() ).debug("profondeur : "+current_depth.intValue()+", nom_var : "+name+ ", valeur : "+value.toString());
	}

	/**
	 * Returns true if the symbols table contains the variable
	 * @param name
	 * @return true if the symbols table contains the variable
	 */
	public boolean contains_var(String name){
		Integer current_depth=Integer.valueOf(symbols.size()-1);
		return symbols.get(current_depth).contains(name);
	}

	/**
	 * Returns the value of the variable
	 * @param name
	 * @return the value of the variable
	 */
	public TypedObject var_value(String name){
		Integer current_depth=Integer.valueOf(symbols.size()-1);
		return symbols.get(current_depth).get(name);
	}

}
