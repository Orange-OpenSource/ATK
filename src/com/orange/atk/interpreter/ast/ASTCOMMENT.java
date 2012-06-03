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
 * File Name   : ASTCOMMENT.java
 *
 * Created     : 21/05/2010
 * Author(s)   : HENAFF Mari-Mai
 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orange.atk.interpreter.ast;

import com.orange.atk.interpreter.parser.ATKScriptParser;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;

public
class ASTCOMMENT extends SimpleNode {
  public ASTCOMMENT(int id) {
    super(id);
  }

  public ASTCOMMENT(ATKScriptParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ATKScriptParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=cb1327c8a1a0e653990421bf5ac94eb7 (do not edit this line) */
