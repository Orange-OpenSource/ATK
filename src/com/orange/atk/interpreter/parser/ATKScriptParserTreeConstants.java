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
 * File Name   : ATKScriptParserTreeConstants.java
 *
 * Created     : 21/05/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.interpreter.parser;


public interface ATKScriptParserTreeConstants
{
  public int JJTSTART = 0;
  public int JJTVOID = 1;
  public int JJTCOMMENT = 2;
  public int JJTFUNCTION = 3;
  public int JJTINCLUDE = 4;
  public int JJTSETVAR = 5;
  public int JJTLOOP = 6;
  public int JJTTABLE = 7;
  public int JJTSTRING = 8;
  public int JJTNUMBER = 9;
  public int JJTVARIABLE = 10;


  public String[] jjtNodeName = {
    "Start",
    "void",
    "COMMENT",
    "FUNCTION",
    "Include",
    "SETVAR",
    "LOOP",
    "TABLE",
    "STRING",
    "NUMBER",
    "VARIABLE",
  };
}
/* JavaCC - OriginalChecksum=57c704ef408dae07643263df9507ee34 (do not edit this line) */
