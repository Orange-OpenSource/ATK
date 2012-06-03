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
 * File Name   : JatkInterpreter.java
 *
 * Created     : 25/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.anaScript.reportGenerator.visit;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.anaScript.reportGenerator.resultLink.JatkResult;
import com.orange.atk.atkUI.anaScript.reportGenerator.resultLink.JatkResultLink;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprIsImplemented;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.HTMLValue;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueAnd;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueBinop;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueLiteral;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueNoderef;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnknown;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnop;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnopref;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprProperties;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Text;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.UsedJSRs;
import com.orange.atk.atkUI.corecli.reportGenerator.util.TypedObject;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Interpreter;
import com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor;

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class JatkInterpreter extends Interpreter implements Visitor {

	public JatkInterpreter(PrintStream out, JatkResultLink resultLink) {
		this.out = out;
		this.resultLink = resultLink;
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Literal(com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueLiteral)
	 */
	public void visit_Literal(ResultvalueLiteral literal) {
		if (literal.getMode() == EvalMode.REPORT_MODE) {
			out.print("&lt;literal ");
			if (literal.getType() != null) {
				out.print("type=\""+literal.getType()+"\" ");
			}
			if (literal.getValue() != null) {
				out.print("value=\""+literal.getValue()+"\" ");
			}
			out.println("/&gt;");
		} else if (literal.getMode() == EvalMode.EVAL_MODE) {
			if (literal.getType().equals("java.lang.String")) {
				stack.push(new TypedObject(SavedObjectType.STRING_TYPE, "\""+literal.getValue()+"\""));
			} else {
				Logger.getLogger(this.getClass() ).warn("flash analysis: 'literal' element with 'type' different than 'java.lang.String': "+literal.getType());
			}
		} else if (literal.getMode() == EvalMode.PSEUDO_CONSTANT_MODE) {
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, true));
		} else if (literal.getMode() == EvalMode.SAVE_MODE) {
			stack.push(new TypedObject(SavedObjectType.RESULTVALUE_TYPE, literal));
		}
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Text(com.orange.atk.atkUI.corecli.reportGenerator.bind.Text)
	 */
	public void visit_Text(Text text) {
		String modifiedContent = text.getContent();
		int i=0;
		int j = modifiedContent.indexOf('%', i);
		String result = "";
		while (j>=0) {
			if (j>i) {
				result+=modifiedContent.substring(i,j);
			}
			JatkResult flashResult = (JatkResult)currentResult;
			switch (modifiedContent.charAt(j+1)) {
				case 'f':
					result+=flashResult.getFunction();
					break;
				case 'r':
					result+=currentString;
					break;
				case '1': case '2': case '3': case '4': case '5':
			    case '6': case '7': case '8': case '9': result+=matchExpr.get((int) modifiedContent.charAt(j+1) - (int) '0');break;
			}
			i = j+2;
			j=modifiedContent.indexOf('%', i);
		}
		result+=modifiedContent.substring(i)+" ";
		out.println(result);
	}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Unknown(com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnknown)
	 */
	public void visit_Unknown(ResultvalueUnknown unknown) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Unop(com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnop)
	 */
	public void visit_Unop(ResultvalueUnop unop) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Unopref(com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnopref)
	 */
	public void visit_Unopref(ResultvalueUnopref unopref) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_UsedJSRs(com.orange.atk.atkUI.corecli.reportGenerator.bind.UsedJSRs)
	 */
	public void visit_UsedJSRs(UsedJSRs rs) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_And(com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueAnd)
	 */
	public void visit_And(ResultvalueAnd and) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Binop(com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueBinop)
	 */
	public void visit_Binop(ResultvalueBinop binop) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Java(com.orange.atk.atkUI.corecli.reportGenerator.bind.HTMLValue)
	 */
	public void visit_HTMLValue(HTMLValue java) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Noderef(com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueNoderef)
	 */
	public void visit_Noderef(ResultvalueNoderef noderef) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_IsImplemented(com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprIsImplemented)
	 */
	public void visit_IsImplemented(BoolExprIsImplemented implemented) {}

	/* (non-Javadoc)
	 * @see com.orange.atk.atkUI.corecli.reportGenerator.visit.Visitor#visit_Properties(com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprProperties)
	 */
	public void visit_Properties(SetExprProperties properties) {}

}
