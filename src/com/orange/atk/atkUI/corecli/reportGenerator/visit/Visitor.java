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
 * File Name   : Visitor.java
 *
 * Created     : 04/04/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.visit;

import com.orange.atk.atkUI.corecli.reportGenerator.bind.A;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.B;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprAnd;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprEqual;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprExists;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprForall;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprIsImplemented;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprIsempty;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprIspseudoconstant;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprMatches;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprMember;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprNot;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprOr;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprProvides;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Br;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Center;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Code;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Do;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Else;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Elseif;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Em;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Font;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Foreach;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H1;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H2;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H3;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H4;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H5;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H6;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.HTMLValue;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.If;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Img;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.In;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Let;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Li;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Ol;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.P;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Report;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueAnd;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueBinop;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueLiteral;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueNoderef;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueOr;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnknown;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnop;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueUnopref;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Separator;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprDiff;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprInter;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprProperties;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprResults;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprUnion;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Table;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Td;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Text;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Then;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Tr;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Ul;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.UsedJSRs;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Var;

/**
 * Interface <code>Visitor</code> of the Visitor design pattern. There is an <code>accept</code> method
 * by class from the <code>reportGenerator.bind</code> package.
 * @author penaulau
 * @since JDK5.0
 */
public interface Visitor {

	public enum EvalMode{REPORT_MODE, EVAL_MODE, SAVE_MODE, PSEUDO_CONSTANT_MODE, STRING_MODE, ACCEPT_MODE};

	public void visit_Do(Do doo);
	public void visit_Let(Let let);
	public void visit_Foreach(Foreach foreach);
	public void visit_If(If if1);
	public void visit_Report(Report report);
	public void visit_Union(SetExprUnion union);
	public void visit_Inter(SetExprInter inter);
	public void visit_Diff(SetExprDiff diff);
	public void visit_Results(SetExprResults results);
	public void visit_Equal(BoolExprEqual equal);
	public void visit_And(BoolExprAnd and);
	public void visit_Or(BoolExprOr or);
	public void visit_Not(BoolExprNot not);
	public void visit_IsEmpty(BoolExprIsempty isempty);
	public void visit_Provides(BoolExprProvides provides);
	public void visit_Matches(BoolExprMatches matches);
	public void visit_Var(Var var);
	public void visit_Member(BoolExprMember member);
	public void visit_Exists(BoolExprExists exists);
	public void visit_Forall(BoolExprForall forall);
	public void visit_And(ResultvalueAnd and);
	public void visit_Or(ResultvalueOr or);
	public void visit_Binop(ResultvalueBinop binop);
	public void visit_Unop(ResultvalueUnop unop);
	public void visit_Unopref(ResultvalueUnopref unopref);
	public void visit_Noderef(ResultvalueNoderef noderef);
	public void visit_Literal(ResultvalueLiteral literal);
	public void visit_Unknown(ResultvalueUnknown unknown);
	public void visit_In(In in);
	public void visit_Separator(Separator separator);
	public void visit_Text(Text text);
	public void visit_Font(Font font);
	public void visit_Code(Code code);
	public void visit_Em(Em em);
	public void visit_B(B b);
	public void visit_A(A a);
	public void visit_Img(Img img);
	public void visit_Br(Br br);
	public void visit_P(P p);
	public void visit_H1(H1 h1);
	public void visit_H2(H2 h2);
	public void visit_H3(H3 h3);
	public void visit_H4(H4 h4);
	public void visit_H5(H5 h5);
	public void visit_H6(H6 h6);
	public void visit_Ul(Ul ul);
	public void visit_Ol(Ol ol);
	public void visit_Table(Table table);
	public void visit_Li(Li li);
	public void visit_Tr(Tr tr);
	public void visit_Td(Td td);
	public void visit_Center(Center center);
	public void visit_Then(Then then);
	public void visit_Else(Else else1);
	public void visit_HTMLValue(HTMLValue java);
	public void visit_Elseif(Elseif elseif);
	public void visit_UsedJSRs(UsedJSRs rs);
	public void visit_Ispseudoconstant(BoolExprIspseudoconstant ispseudoconstant);
	public void visit_IsImplemented(BoolExprIsImplemented implemented);
	public void visit_Properties(SetExprProperties properties);
}
