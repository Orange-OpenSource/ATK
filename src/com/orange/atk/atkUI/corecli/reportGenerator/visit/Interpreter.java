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
 * File Name   : Interpreter.java
 *
 * Created     : 25/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.corecli.reportGenerator.visit;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.regexp.RE;

import com.orange.atk.atkUI.corecli.Step.Verdict;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.A;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Action;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.B;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprAnd;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprEqual;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprExists;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprForall;
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
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Content;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Do;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Else;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Elseif;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Em;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Expr;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Font;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Foreach;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H1;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H2;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H3;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H4;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H5;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.H6;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.HTMLElement;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.If;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Img;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.In;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Inline;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Let;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Li;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Ol;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.P;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Report;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Resultvalue;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.ResultvalueOr;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Separator;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExpr;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprDiff;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprInter;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprResults;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.SetExprUnion;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Table;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Td;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Then;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Tr;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Ul;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.Var;
import com.orange.atk.atkUI.corecli.reportGenerator.bind.BoolExprMatches.Matches;
import com.orange.atk.atkUI.corecli.reportGenerator.resultLink.Result;
import com.orange.atk.atkUI.corecli.reportGenerator.resultLink.ResultLink;
import com.orange.atk.atkUI.corecli.reportGenerator.util.SymbolTable;
import com.orange.atk.atkUI.corecli.reportGenerator.util.TypedObject;

@SuppressWarnings("unchecked")

/**
 *
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public abstract class Interpreter implements Visitor{

	public enum SavedObjectType{VECTOR_RESULT, RESULT, VECTOR_TYPED_OBJECT, SET_INT,
		BOOLEAN_TYPE, STRING_TYPE, RESULTVALUE_TYPE, INT_TYPE, FLOAT_TYPE, VECTOR_STRING};

	protected Verdict verdict = Verdict.PASSED;

	/**
	 * The depth variable represents the depth in the XML program
	 */
	protected Integer depth = Integer.valueOf(0);

	/**
	 * The result xml stream.
	 */
	protected PrintStream out;

	/**
	 * The table of symbols which saves the variables with their values is
	 * represented by the symbols variable.
	 */
	protected SymbolTable symbols = new SymbolTable();

	/**
	 * The stack allows to save the intermediate result
	 */
	protected Stack<TypedObject> stack = new Stack<TypedObject>();

	protected String currentString = null;
	protected Vector<String> matchExpr = new Vector<String>();
	protected Result currentResult;
	protected Matches matchesWay;


	/**
	 * Link to the xxxresults.xml file
	 */
	protected ResultLink resultLink;

	public void play(Do doo) {
		visit_Do(doo);
	}

	public Verdict getVerdict() {
		return verdict;
	}

	/**
	 * Getter for the property <code>depth<code>.
	 * @return Value of property <code>depth</code>.
	 */
	public Integer getDepth() {
		return depth;
	}

	/**
	 * Setter for property <code>depth</code>.
	 * @param depth New value of property <code>depth</code>.
	 */
	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	/**
	 * Implementation of the method <code>visit_Do</code> of the <code>Visitable</code> interface.
	 * @param doo
	 */
	public void visit_Do(Do doo) {
		depth = Integer.valueOf(depth.intValue() + 1);
		symbols.add_entry(depth);
		Vector<Action> list = doo.getContent();
		for (Action action : list) {
			action.accept(this);
		}
		symbols.remove_entry(depth);
		depth = Integer.valueOf(depth.intValue() - 1);
	}

	/**
	 * Implementation of the method <code>visit_Let</code> of the <code>Visitable</code> interface.
	 * @param let
	 */
	public void visit_Let(Let let) {
		let.getExpr().setMode(EvalMode.SAVE_MODE);
		let.getExpr().accept(this);
		symbols.set_var(let.getId(), stack.peek());
		stack.pop();
	}

	/**
	 * Implementation of the method <code>visit_A</code> of the <code>Visitable</code> interface.
	 * @param a
	 */
	public void visit_A(A a) {
		out.print("<a ");
		if (a.getHref()!=null) {
			out.print("href='"+a.getHref()+"' ");
		}
		if (a.getName()!=null) {
			out.print("name='"+a.getName()+"' ");
		}
		out.println(">");
		Vector<Inline> content = a.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</a>");
	}

	/**
	 * Implementation of the method <code>visit_And</code> of the <code>Visitable</code> interface.
	 * @param and
	 */
	public void visit_And(BoolExprAnd and) {
		Vector<Expr> twoExpr = and.getTwoExpr();
		twoExpr.get(0).accept(this);
		twoExpr.get(1).accept(this);
		TypedObject toLhs = stack.peek();
		stack.pop();
		TypedObject toRhs = stack.peek();
		stack.pop();
		if ((toLhs.getType() == SavedObjectType.BOOLEAN_TYPE) && (toRhs.getType() == SavedObjectType.BOOLEAN_TYPE)) {
			boolean lhs = (Boolean)toLhs.getObject();
			boolean rhs = (Boolean)toRhs.getObject();
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, Boolean.valueOf(lhs && rhs)));
		}
	}

	/**
	 * Implementation of the method <code>visit_B</code> of the <code>Visitable</code> interface.
	 * @param b
	 */
	public void visit_B(B b) {
		out.println("<b>");
		Vector<Inline> content = b.getContent();
		for (Inline inline : content) {
			inline.accept(this);
		}
		out.println("</b>");
	}

	/**
	 * Implementation of the method <code>visit_Br</code> of the <code>Visitable</code> interface.
	 * @param br
	 */
	public void visit_Br(Br br) {
		out.println("<br/>");
	}

	/**
	 * Implementation of the method <code>visit_Center</code> of the <code>Visitable</code> interface.
	 * @param center
	 */
	public void visit_Center(Center center) {
		out.println("<center>");
		Vector<HTMLElement> content = center.getContent();
		Iterator<HTMLElement> it = content.iterator();
		while (it.hasNext()) {
			HTMLElement html = it.next();
			html.accept(this);
		}
		out.println("</center>");
	}

	/**
	 * Implementation of the method <code>visit_Code</code> of the <code>Visitable</code> interface.
	 * @param code
	 */
	public void visit_Code(Code code) {
		out.println("<code>");
		Vector<Inline> content = code.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</code>");
	}

	/**
	 * Implementation of the method <code>visit_Diff</code> of the <code>Visitable</code> interface.
	 * @param diff
	 */
	public void visit_Diff(SetExprDiff diff) {
		Vector<Expr> twoSetExpr = diff.getTwoSetExpr();
		((SetExpr)twoSetExpr.get(0)).accept(this);
		((SetExpr)twoSetExpr.get(1)).accept(this);
		TypedObject toRhs = stack.peek();
		stack.pop();
		TypedObject toLhs = stack.peek();
		stack.pop();
		if (toLhs.getType() == SavedObjectType.VECTOR_RESULT && toRhs.getType() == SavedObjectType.VECTOR_RESULT) {
			Vector<Result> diffVect = new Vector<Result>();
			Vector<Result> lhsVect = (Vector<Result>)toLhs.getObject();
			Vector<Result> rhsVect = (Vector<Result>)toRhs.getObject();
			for (Result res : lhsVect) {
				if (!belong(res, rhsVect)) {
					diffVect.add(res);
				}
			}
			stack.push(new TypedObject(SavedObjectType.VECTOR_RESULT, diffVect));
		} else if(toLhs.getType() == SavedObjectType.VECTOR_TYPED_OBJECT && toRhs.getType() == SavedObjectType.VECTOR_TYPED_OBJECT) {
			Vector<TypedObject> diffVect = new Vector<TypedObject>();
			Vector<TypedObject> lhsVect = (Vector<TypedObject>)toLhs.getObject();
			Vector<TypedObject> rhsVect = (Vector<TypedObject>)toRhs.getObject();
			for (TypedObject to : lhsVect) {
				if (!belong(to, rhsVect)) {
					diffVect.add(to);
				}
			}
			stack.push(new TypedObject(SavedObjectType.VECTOR_TYPED_OBJECT, diffVect));
		}
	}

	/**
	 * Implementation of the method <code>visit_Else</code> of the <code>Visitable</code> interface.
	 * @param else1
	 */
	public void visit_Else(Else else1) {
		depth = Integer.valueOf(depth.intValue() + 1);
		symbols.add_entry(depth);
		Vector<Action> actions = else1.getActions();
		Iterator<Action> it = actions.iterator();
		while (it.hasNext()) {
			Action action = it.next();
			action.accept(this);
		}
		symbols.remove_entry(depth);
		depth = Integer.valueOf(depth.intValue() - 1);
	}

	/**
	 * Implementation of the method <code>visit_Elseif</code> of the <code>Visitable</code> interface.
	 * @param elseif
	 */
	public void visit_Elseif(Elseif elseif) {
		depth = Integer.valueOf(depth.intValue() + 1);
		symbols.add_entry(depth);
		elseif.getExpr().accept(this);
		TypedObject typedObject = stack.peek();
		stack.pop();
		if (typedObject.getType() == SavedObjectType.BOOLEAN_TYPE) {
			Boolean bool = (Boolean)typedObject.getObject();
			if (bool) {
				elseif.getThen().accept(this);
			} else {
				if (elseif.getElse1()!=null) {
					elseif.getElse1().accept(this);
				} else if(elseif.getElseif()!=null) {
					elseif.getElseif().accept(this);
				}
			}
		}
		symbols.remove_entry(depth);
		depth = Integer.valueOf(depth.intValue() - 1);
	}

	/**
	 * Implementation of the method <code>visit_Em</code> of the <code>Visitable</code> interface.
	 * @param em
	 */
	public void visit_Em(Em em) {
		out.println("<em>");
		Vector<Inline> content = em.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</em>");
	}

	/**
	 * Implementation of the method <code>visit_Equal</code> of the <code>Visitable</code> interface.
	 * @param equal
	 */
	public void visit_Equal(BoolExprEqual equal) {
		Vector<Expr> twoExpr = equal.getTwoExpr();
		twoExpr.get(0).accept(this);
		twoExpr.get(1).accept(this);
		TypedObject toLhs = stack.peek();
		stack.pop();
		TypedObject toRhs = stack.peek();
		stack.pop();
		stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, Boolean.valueOf(isEqual(toLhs, toRhs))));
	}

	/**
	 * Implementation of the method <code>visit_Exists</code> of the <code>Visitable</code> interface.
	 * @param exists
	 */
	public void visit_Exists(BoolExprExists exists) {
		exists.getIn().accept(this);
		TypedObject typedObject = stack.peek();
		stack.pop();
		boolean result = false;
		if (typedObject.getType() == SavedObjectType.VECTOR_RESULT) {
			Vector<Result> set = (Vector<Result>)typedObject.getObject();
			for (Result res : set) {
				symbols.set_var(exists.getVar(), new TypedObject(SavedObjectType.RESULT, res));
				exists.getExpr().accept(this);
				TypedObject peek = stack.peek();
				if ((Boolean)peek.getObject()) {
					result = true;
				}
				stack.pop();
			}
		} else if (typedObject.getType() == SavedObjectType.VECTOR_STRING) {
			Vector<String> set = (Vector<String>)typedObject.getObject();
			for (String string : set) {
				symbols.set_var(exists.getVar(), new TypedObject(SavedObjectType.STRING_TYPE, string));
				exists.getExpr().accept(this);
				TypedObject peek = stack.peek();
				if ((Boolean)peek.getObject()) {
					result = true;
				}
				stack.pop();
			}
		} else if (typedObject.getType() == SavedObjectType.VECTOR_TYPED_OBJECT) {
			Vector<TypedObject> set = (Vector<TypedObject>)typedObject.getObject();
			for (TypedObject to : set) {
				symbols.set_var(exists.getVar(), to);
				exists.getExpr().accept(this);
				TypedObject peek = stack.peek();
				if ((Boolean)peek.getObject()) {
					result = true;
				}
				stack.pop();
			}
		}
		stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, result));
	}

	/**
	 * Implementation of the method <code>visit_Font</code> of the <code>Visitable</code> interface.
	 * @param font
	 */
	public void visit_Font(Font font) {
		out.print("<font ");
		if (font.getColor()!=null) {
			String color = font.getColor();
			out.print("color='"+color+"' ");
		}
		if (font.getFace()!=null) {
			out.print("face='"+font.getFace()+"' ");
		}
		if (font.getSize()!=null) {
			out.print("size='"+font.getSize()+"' ");
		}
		out.println(">");
		Vector<Inline> content = font.getContent();
		for (Inline inline : content) {
			inline.accept(this);
		}
		out.println("</font>");
	}

	/**
	 * Implementation of the method <code>visit_Forall</code> of the <code>Visitable</code> interface.
	 * @param forall
	 */
	public void visit_Forall(BoolExprForall forall) {
		forall.getIn().accept(this);
		TypedObject typedObject = stack.peek();
		stack.pop();
		boolean result = true;
		if (typedObject.getType() == SavedObjectType.VECTOR_RESULT) {
			Vector<Result> set = (Vector<Result>)typedObject.getObject();
			for (Result res : set) {
				symbols.set_var(forall.getVar(), new TypedObject(SavedObjectType.RESULT, res));
				forall.getExpr().accept(this);
				TypedObject peek = stack.peek();
				if (!(Boolean)peek.getObject()) {
					result = false;
				}
				stack.pop();
			}
		} else if (typedObject.getType() == SavedObjectType.VECTOR_STRING) {
			Vector<String> set = (Vector<String>)typedObject.getObject();
			for (String string : set) {
				symbols.set_var(forall.getVar(), new TypedObject(SavedObjectType.STRING_TYPE, string));
				forall.getExpr().accept(this);
				TypedObject peek = stack.peek();
				if (!(Boolean)peek.getObject()) {
					result = false;
				}
				stack.pop();
			}
		} else if (typedObject.getType() == SavedObjectType.VECTOR_TYPED_OBJECT) {
			Vector<TypedObject> set = (Vector<TypedObject>)typedObject.getObject();
			for (TypedObject to : set) {
				symbols.set_var(forall.getVar(), to);
				forall.getExpr().accept(this);
				TypedObject peek = stack.peek();
				if (!(Boolean)peek.getObject()) {
					result = false;
				}
				stack.pop();
			}
		}
		stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, result));
	}

	/**
	 * Implementation of the method <code>visit_Foreach</code> of the <code>Visitable</code> interface.
	 * @param foreach
	 */
	public void visit_Foreach(Foreach foreach) {
		foreach.getIn().accept(this);
		TypedObject typedObject = stack.peek();
		stack.pop();
		if(typedObject.getType() == SavedObjectType.VECTOR_TYPED_OBJECT) {
			Vector<TypedObject> set = (Vector<TypedObject>)typedObject.getObject();
			int count = set.size();
			if (foreach.getCount()!=null) {
				count = new Integer(foreach.getCount()).intValue();
			} else {
				count = 100;
			}
			int i=0;
			for (Iterator<TypedObject> it = set.iterator(); it.hasNext() && i<count; ) {
				if (foreach.getSeparator()!=null && i>0) {
					foreach.getSeparator().accept(this);
				}
				TypedObject to = it.next();
				symbols.set_var(foreach.getVar(), to);
				foreach.getDoo().accept(this);
				if (i==99) {
					out.println("<br /> ... <br />");
				}
				i++;
			}
		} else if (typedObject.getType() == SavedObjectType.VECTOR_STRING) {
			Vector<String> set = (Vector<String>)typedObject.getObject();
			int count = set.size();
			if (foreach.getCount()!=null) {
				count = new Integer(foreach.getCount()).intValue();
			} else {
				count = 100;
			}
			int i=0;
			for (Iterator<String> it = set.iterator(); it.hasNext() && i<count; ) {
				if (foreach.getSeparator()!=null && i>0) {
					foreach.getSeparator().accept(this);
				}
				String stringResult = it.next();
				currentString = stringResult;
				symbols.set_var(foreach.getVar(), new TypedObject(SavedObjectType.STRING_TYPE, stringResult));
				foreach.getDoo().accept(this);
				if (i==99) {
					out.println("<br /> ... <br />");
				}
				i++;
			}
		} else if (typedObject.getType() == SavedObjectType.VECTOR_RESULT) {
			Vector<Result> resultVect = (Vector<Result>)typedObject.getObject();
			for (Result res : resultVect) {
				TypedObject resto = evalResult(res);
				if (resto.getType() == SavedObjectType.STRING_TYPE) {
					String resString = (String)resto.getObject();
					currentString = resString;
					symbols.set_var(foreach.getVar(), new TypedObject(SavedObjectType.STRING_TYPE, resString));
					foreach.getDoo().accept(this);
				} else {
					Vector<String> strings = (Vector<String>)resto.getObject();
					int count = strings.size();
					if (foreach.getCount()!=null) {
						count = new Integer(foreach.getCount()).intValue();
					} else {
						count = 100;
					}
					int i=0;
					for (Iterator<String> it = strings.iterator(); it.hasNext() && i<count; ) {
						if (foreach.getSeparator()!=null && i>0) {
							foreach.getSeparator().accept(this);
						}
						String stringResult = it.next();
						currentString = stringResult;
						symbols.set_var(foreach.getVar(), new TypedObject(SavedObjectType.STRING_TYPE, stringResult));
						foreach.getDoo().accept(this);
						if (i==99) {
							out.println("<br /> ... <br />");
						}
						i++;
					}
				}
			}
		}
	}

	/**
	 * Implementation of the method <code>visit_H1</code> of the <code>Visitable</code> interface.
	 * @param h1
	 */
	public void visit_H1(H1 h1) {
		out.println("<h1>");
		Vector<Inline> content = h1.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</h1>");
	}

	/**
	 * Implementation of the method <code>visit_H2</code> of the <code>Visitable</code> interface.
	 * @param h2
	 */
	public void visit_H2(H2 h2) {
		out.println("<h2>");
		Vector<Inline> content = h2.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</h2>");
	}

	/**
	 * Implementation of the method <code>visit_H3</code> of the <code>Visitable</code> interface.
	 * @param h3
	 */
	public void visit_H3(H3 h3) {
		out.println("<h3>");
		Vector<Inline> content = h3.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</h3>");
	}

	/**
	 * Implementation of the method <code>visit_H4</code> of the <code>Visitable</code> interface.
	 * @param h4
	 */
	public void visit_H4(H4 h4) {
		out.println("<h4>");
		Vector<Inline> content = h4.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</h4>");
	}

	/**
	 * Implementation of the method <code>visit_H5</code> of the <code>Visitable</code> interface.
	 * @param h5
	 */
	public void visit_H5(H5 h5) {
		out.println("<h5>");
		Vector<Inline> content = h5.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</h5>");
	}

	/**
	 * Implementation of the method <code>visit_H6</code> of the <code>Visitable</code> interface.
	 * @param h6
	 */
	public void visit_H6(H6 h6) {
		out.println("<h6>");
		Vector<Inline> content = h6.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</h6>");
	}

	/**
	 * Implementation of the method <code>visit_Ul</code> of the <code>Visitable</code> interface.
	 * @param ul
	 */
	public void visit_Ul(Ul ul) {
		out.println("<ul>");
		Vector<HTMLElement> content = ul.getContent();
		for (HTMLElement elem : content) {
			elem.accept(this);
		}
		out.println("</ul>");
	}

	/**
	 * Implementation of the method <code>visit_Ol</code> of the <code>Visitable</code> interface.
	 * @param ol
	 */
	public void visit_Ol(Ol ol) {
		out.println("<ol>");
		Vector<Li> content = ol.getContent();
		Iterator<Li> it = content.iterator();
		while (it.hasNext()) {
			Li li = it.next();
			li.accept(this);
		}
		out.println("</ol>");
	}

	/**
	 * Implementation of the method <code>visit_Table</code> of the <code>Visitable</code> interface.
	 * @param table
	 */
	public void visit_Table(Table table) {
		out.print("<table ");
		if (table.getHeight()!=null) {
			out.print("height='"+table.getHeight()+"' ");
		}
		if (table.getWidth()!=null) {
			out.print("width='"+table.getWidth()+"' ");
		}
		if (table.getAlign()!=null) {
			out.print("align='"+table.getAlign()+"' ");
		}
		if (table.getCellspacing()!=null) {
			out.print("cellspacing='"+table.getCellspacing()+"' ");
		}
		out.println(">");
		Vector<Tr> content = table.getContent();
		Iterator<Tr> it = content.iterator();
		while (it.hasNext()) {
			Tr tr = it.next();
			tr.accept(this);
		}
		out.println("</table>");
	}

	/**
	 * Implementation of the method <code>visit_Li</code> of the <code>Visitable</code> interface.
	 * @param li
	 */
	public void visit_Li(Li li) {
		out.println("<li>");
		Vector<Inline> content = li.getContent();
		for (Inline  inline : content) {
			inline.accept(this);
		}
		out.println("</li>");
	}

	/**
	 * Implementation of the method <code>visit_Tr</code> of the <code>Visitable</code> interface.
	 * @param tr
	 */
	public void visit_Tr(Tr tr) {
		out.print("<tr ");
		if (tr.getAlign()!=null) {
			out.print("align='"+tr.getAlign()+"' ");
		}
		if (tr.getValign()!=null) {
			out.print("valign='"+tr.getValign()+"' ");
		}
		out.println(">");
		Vector<Td> content = tr.getContent();
		Iterator<Td> it = content.iterator();
		while (it.hasNext()) {
			Td td = it.next();
			td.accept(this);
		}
		out.println("</tr>");
	}

	/**
	 * Implementation of the method <code>visit_Td</code> of the <code>Visitable</code> interface.
	 * @param td
	 */
	public void visit_Td(Td td) {
		out.print("<td ");
		if (td.getAlign() != null) {
			out.print("align='"+td.getAlign()+"' ");
		}
		if (td.getValign() != null) {
			out.print("valign='"+td.getValign()+"' ");
		}
		if (td.getHeight() != null) {
			out.print("height='"+td.getHeight()+"' ");
		}
		if (td.getWidth() != null) {
			out.print("width='"+td.getWidth()+"' ");
		}
		if (td.getRowspan() != null) {
			out.print("rowspan='"+td.getRowspan()+"' ");
		}
		if (td.getColspan() != null) {
			out.print("colspan='"+td.getColspan()+"' ");
		}
		out.println(">");
		Vector<HTMLElement> content = td.getContent();
		Iterator<HTMLElement> it = content.iterator();
		while (it.hasNext()) {
			HTMLElement html = it.next();
			html.accept(this);
		}
		out.println("</td>");
	}

	/**
	 * Implementation of the method <code>visit_Then</code> of the <code>Visitable</code> interface.
	 * @param then
	 */
	public void visit_Then(Then then) {
		depth = Integer.valueOf(depth.intValue() + 1);
		symbols.add_entry(depth);
		Vector<Action> actions = then.getActions();
		Iterator<Action> it = actions.iterator();
		while (it.hasNext()) {
			Action action = it.next();
			action.accept(this);
		}
		symbols.remove_entry(depth);
		depth = Integer.valueOf(depth.intValue() - 1);
	}

	/**
	 * Implementation of the method <code>visit_If</code> of the <code>Visitable</code> interface.
	 * @param if1
	 */
	public void visit_If(If if1) {
		if (if1.getExpr()!=null) {
			if1.getExpr().accept(this);
		} else {
			if1.getVar().accept(this);
		}
		TypedObject typedObject = stack.peek();
		stack.pop();
		if (typedObject.getType() == SavedObjectType.BOOLEAN_TYPE) {
			Boolean bool = (Boolean)typedObject.getObject();
			if (bool) {
				if1.getThen().accept(this);
			} else {
				if (if1.getElse1()!=null) {
					if1.getElse1().accept(this);
				} else if(if1.getElseif()!=null) {
					if1.getElseif().accept(this);
				}
			}
		}
	}

	/**
	 * Implementation of the method <code>visit_Report</code> of the <code>Visitable</code> interface.
	 * @param report
	 */
	public void visit_Report(Report report) {
		Vector<HTMLElement> content = report.getContent();
		for (HTMLElement elem : content) {
			elem.accept(this);
		}
		String verdictReport = report.getVerdict();
		if (verdictReport!= null && verdictReport.equals("FAILED")) verdict = Verdict.FAILED;
	}

	/**
	 * Implementation of the method <code>visit_Img</code> of the <code>Visitable</code> interface.
	 * @param img
	 */
	public void visit_Img(Img img) {
		out.print("<img ");
		if (img.getAlt()!=null) {
			out.print("alt='"+img.getAlt()+"' ");
		}
		if (img.getBorder()!=null) {
			out.print("border='"+img.getBorder()+"' ");
		}
		if (img.getHeight()!=null) {
			out.print("height='"+img.getHeight()+"' ");
		}
		if (img.getSrc()!=null) {
			out.print("src='"+img.getSrc()+"' ");
		}
		if (img.getWidth()!=null)  {
			out.print("width='"+img.getWidth()+"' ");
		}
		out.println("/>");
	}

	/**
	 * Implementation of the method <code>visit_In</code> of the <code>Visitable</code> interface.
	 * @param in
	 */
	public void visit_In(In in) {
		in.getExpr().accept(this);
	}

	/**
	 * Implementation of the method <code>visit_Inter</code> of the <code>Visitable</code> interface.
	 * @param inter
	 */
	public void visit_Inter(SetExprInter inter) {
		Vector<Expr> content = inter.getContent();
		((SetExpr)content.get(0)).accept(this);
		TypedObject first = stack.peek();
		stack.pop();
		Vector interVect = (Vector)first.getObject();
		SavedObjectType interType = first.getType();
		for (int i=1; i<content.size(); i++) {
			((SetExpr)content.get(i)).accept(this);
			TypedObject other = stack.peek();
			stack.pop();
			if (other.getType() != first.getType()) {
				Logger.getLogger(this.getClass() ).warn("all elements of the intersection does not have the same type");
			}
			if (interType == SavedObjectType.VECTOR_RESULT) {
				Vector<Result> tempInter = new Vector<Result>();
				Vector<Result> otherRes = (Vector<Result>)other.getObject();
				for (Result res : otherRes) {
					if (belong(res, interVect)) {
						tempInter.add(res);
					}
				}
				interVect = tempInter;
			} else if (interType == SavedObjectType.VECTOR_TYPED_OBJECT) {
				Vector<TypedObject> tempInter = new Vector<TypedObject>();
				Vector<TypedObject> otherTo = (Vector<TypedObject>)other.getObject();
				for (TypedObject to : otherTo) {
					if (belong(to, interVect)) {
						tempInter.add(to);
					}
				}
				interVect = tempInter;
			}
		}
		if (interType == SavedObjectType.VECTOR_RESULT) {
			stack.push(new TypedObject(SavedObjectType.VECTOR_RESULT, interVect));
		} else if (interType == SavedObjectType.VECTOR_TYPED_OBJECT) {
			stack.push(new TypedObject(SavedObjectType.VECTOR_TYPED_OBJECT, interVect));
		}
	}

	/**
	 * Implementation of the method <code>visit_IsEmpty</code> of the <code>Visitable</code> interface.
	 * @param isempty
	 */
	public void visit_IsEmpty(BoolExprIsempty isempty) {
		if (isempty.getSetExpr()!=null){
			isempty.getSetExpr().accept(this);
		}else{
			isempty.getVar().accept(this);
		}
		TypedObject to = stack.peek();
		stack.pop();
		if (to == null) {
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, true));
		} else {
			Vector vector = (Vector)to.getObject();
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, (vector.size()==0)));
		}
	}

	/**
	 * Implementation of the method <code>visit_Member</code> of the <code>Visitable</code> interface.
	 * @param member
	 */
	public void visit_Member(BoolExprMember member) {
		Vector<Expr> content = member.getExprPlusSetExpr();
		Expr lhs = content.get(0);
		SetExpr rhs = (SetExpr)content.get(1);
		lhs.accept(this);
		rhs.accept(this);
		if (lhs instanceof SetExpr) {
			TypedObject toRhs = stack.peek();
			stack.pop();
			TypedObject toLhs = stack.peek();
			stack.pop();
			if (toRhs.getType() == SavedObjectType.VECTOR_TYPED_OBJECT && toLhs.getType() == SavedObjectType.VECTOR_TYPED_OBJECT) {
				boolean inclusive = true;
				Vector<TypedObject> toLhsVect = (Vector<TypedObject>)toLhs.getObject();
				Vector<TypedObject> toRhsVect = (Vector<TypedObject>)toRhs.getObject();
				for (TypedObject to : toLhsVect) {
					if (!belong(to, toRhsVect)) {
						inclusive = false;
					}
				}
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, inclusive));
			} else if (toRhs.getType() == SavedObjectType.VECTOR_RESULT && toLhs.getType() == SavedObjectType.VECTOR_RESULT) {
				boolean inclusive = true;
				Vector<Result> toLhsVect = (Vector<Result>)toLhs.getObject();
				Vector<Result> toRhsVect = (Vector<Result>)toRhs.getObject();
				for (Result result : toLhsVect) {
					if (!belong(result, toRhsVect)) {
						inclusive = false;
					}
				}
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, inclusive));
			} else if (toRhs.getType() == SavedObjectType.VECTOR_STRING && toLhs.getType() == SavedObjectType.VECTOR_STRING) {
				boolean inclusive = true;
				Vector<String> toLhsVect = (Vector<String>)toLhs.getObject();
				Vector<String> toRhsVect = (Vector<String>)toRhs.getObject();
				for (String string : toLhsVect) {
					if (!toRhsVect.contains(string)) {
						inclusive = false;
					}
				}
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, inclusive));
			}
		} else {
			TypedObject toRhs = stack.peek();
			stack.pop();
			TypedObject toLhs = stack.peek();
			stack.pop();
			if (toLhs.getType() == SavedObjectType.STRING_TYPE && toRhs.getType() == SavedObjectType.VECTOR_STRING) {
				String lhsString = (String)toLhs.getObject();
				Vector<String> rhsVect = (Vector<String>)toRhs.getObject();
				boolean belong = rhsVect.contains(lhsString);
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, belong));
			} else if (toLhs.getType() == SavedObjectType.RESULT && toRhs.getType() == SavedObjectType.VECTOR_RESULT) {
				Result lhsResult = (Result)toLhs.getObject();
				Vector<Result> rhsVect = (Vector<Result>)toRhs.getObject();
				boolean belong = belong(lhsResult, rhsVect);
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, belong));
			} else if (toRhs.getType() == SavedObjectType.VECTOR_TYPED_OBJECT) {
				Vector<TypedObject> rhsVect = (Vector<TypedObject>)toRhs.getObject();
				boolean belong = belong(toLhs, rhsVect);
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, belong));
			} else {
				// if the type of the right hand side set is bad, the result of the member clause is false
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, false));
			}
		}
	}

	/**
	 * Implementation of the method <code>visit_Not</code> of the <code>Visitable</code> interface.
	 * @param not
	 */
	public void visit_Not(BoolExprNot not) {
		not.getExpr().accept(this);
		TypedObject to = stack.peek();
		stack.pop();
		if (to.getType() == SavedObjectType.BOOLEAN_TYPE) {
			Boolean bool = (Boolean)to.getObject();
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, !bool));
		}
	}

	/**
	 * Implementation of the method <code>visit_Or</code> of the <code>Visitable</code> interface.
	 * @param or
	 */
	public void visit_Or(BoolExprOr or) {
		Vector twoExpr = or.getTwoExpr();
		((Expr)twoExpr.get(0)).accept(this);
		((Expr)twoExpr.get(1)).accept(this);
		TypedObject toLhs = stack.peek();
		stack.pop();
		TypedObject toRhs = stack.peek();
		stack.pop();
		if ((toLhs.getType() == SavedObjectType.BOOLEAN_TYPE) && (toRhs.getType() == SavedObjectType.BOOLEAN_TYPE)) {
			boolean lhs = (Boolean)toLhs.getObject();
			boolean rhs = (Boolean)toRhs.getObject();
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, (lhs || rhs)));
		}
	}

	/**
	 * Implementation of the method <code>visit_P</code> of the <code>Visitable</code> interface.
	 * @param p
	 */
	public void visit_P(P p) {
		out.println("<p>");
		Vector<Inline> content = p.getContent();
		Iterator<Inline> it = content.iterator();
		while (it.hasNext()) {
			Inline inline = it.next();
			inline.accept(this);
		}
		out.println("</p>");
	}

	/**
	 * Implementation of the method <code>visit_Separator</code> of the <code>Visitable</code> interface.
	 * @param separator
	 */
	public void visit_Separator(Separator separator) {
		Vector<HTMLElement> content = separator.getContentOrInline();
		Iterator<HTMLElement> it = content.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof Inline) {
				((Inline)obj).accept(this);
			} else if (obj instanceof Content) {
				((Content)obj).accept(this);
			}
		}
	}

	/**
	 * Implementation of the method <code>visit_Union</code> of the <code>Visitable</code> interface.
	 * @param union
	 */
	public void visit_Union(SetExprUnion union) {
		Vector<Expr> content = union.getContent();
		content.get(0).accept(this);
		Vector unionVect = new Vector();
		SavedObjectType unionType = SavedObjectType.VECTOR_RESULT;
		for (int i=0; i<content.size(); i++) {
			content.get(i).accept(this);
			TypedObject other = stack.peek();
			stack.pop();
			if (i == 0) {
				unionType = other.getType();
			} else {
				if (other.getType() != unionType) {
					Logger.getLogger(this.getClass() ).warn("all elements of the union does not have the same type");
				}
			}
			if (unionType == SavedObjectType.VECTOR_RESULT) {
				Vector<Result> add = (Vector<Result>)other.getObject();
				for (Result result : add) {
					if (!belong(result, unionVect)) {
						unionVect.add(result);
					}
				}
			} else if (unionType == SavedObjectType.VECTOR_TYPED_OBJECT) {
				unionType = other.getType();
				Vector<TypedObject> add = (Vector<TypedObject>)other.getObject();
				for (TypedObject to : add) {
					if (!belong(to, unionVect)) {
						unionVect.add(to);
					}
				}
			}
		}
		if (unionType == SavedObjectType.VECTOR_RESULT) {
			stack.push(new TypedObject(SavedObjectType.VECTOR_RESULT, unionVect));
		} else if (unionType == SavedObjectType.VECTOR_TYPED_OBJECT) {
			stack.push(new TypedObject(SavedObjectType.VECTOR_TYPED_OBJECT, unionVect));
		}
	}

	/**
	 * Implementation of the method <code>visit_Var</code> of the <code>Visitable</code> interface.
	 * @param var
	 */
	public void visit_Var(Var var) {
		TypedObject to = symbols.var_value(var.getName());
		if (to.getType() == SavedObjectType.RESULT) {
			currentResult = (Result)to.getObject();
		} else if (to.getType() == SavedObjectType.VECTOR_RESULT) {
			Vector<Result> vect = (Vector<Result>)to.getObject();
			if (vect.size()>0) currentResult = vect.get(0);
		}
		stack.push(symbols.var_value(var.getName()));
	}

	private boolean belong(Result result, Vector<Result> resultVect) {
		boolean belong = false;
		for (Iterator<Result> iter = resultVect.iterator(); iter.hasNext() && !belong; ) {
			Result other = iter.next();
			if (result.equals(other)) {
				belong = true;
			}
		}
		return belong;
	}

	/**
	 * Implementation of the method <code>visit_Matches</code> of the <code>Visitable</code> interface.
	 * @param matches
	 */
	public void visit_Matches(BoolExprMatches matches) {
		RE re = new RE(matches.getPattern());
		if (matches.getResultvalue()!=null) {
			matches.getResultvalue().setMode(EvalMode.EVAL_MODE);
			matches.getResultvalue().accept(this);
		} else if (matches.getVar()!=null) {
			matches.getVar().accept(this);
		}
		matchExpr = new Vector<String>();
		if (stack.peek()!=null) {
			TypedObject typedObject = stack.peek();
			stack.pop();
			//treat the different types of resultvalue not only strings but also empty, null ...
			if (typedObject.getType() == SavedObjectType.STRING_TYPE) {
				String comp = (String)typedObject.getObject();
				if (comp != null) {
					Boolean bool = Boolean.valueOf(re.match(comp.trim()));
					//Logger.getLogger(this.getClass() ).debug("re : "+matches.getPattern()+" string : "+comp.trim()+" match : "+bool);
					if (bool) {
						int parenCount = re.getParenCount();
						for (int i=0;i<parenCount;i++) {
							matchExpr.add(i, re.getParen(i));
						}
					}
					stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, bool));
				} else {
					stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, false));
				}
			} else if (typedObject.getType() == SavedObjectType.RESULT) {
				Result result = (Result)typedObject.getObject();
				TypedObject resultTo = evalResult(result);
				if (resultTo.getType() == SavedObjectType.STRING_TYPE) {
					String resultString = (String)resultTo.getObject();
					if (resultString !=null) {
						currentString = resultString;
						Boolean bool = Boolean.valueOf(re.match(resultString.trim()));
						//Logger.getLogger(this.getClass() ).debug("re : "+matches.getPattern()+" string : "+resultString.trim()+" match : "+bool);
						if (bool) {
							int parenCount = re.getParenCount();
							for (int i=0;i<parenCount;i++) {
								matchExpr.add(i, re.getParen(i));
							}
						}
						stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, bool));
					} else {
						stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, false));
					}
				} else {//Vector<String>
					Vector<String> resVect = (Vector<String>)resultTo.getObject();
					boolean match = true;
					boolean evaluate = false;
					for (String s : resVect) {
						if (s!=null) {
							boolean bool = re.match(s.trim());
							evaluate = true;
							if (!bool) {
								match = false;
							} else {
								int parenCount = re.getParenCount();
								for (int i=0;i<parenCount;i++) {
									if (matchExpr.get(i) == null) {
										matchExpr.add(i, re.getParen(i));
									} else {
										String expr = matchExpr.get(i);
										String parent = re.getParen(i);
										if(!expr.equals(parent)) {
											matchExpr.add(i, expr+", "+parent);
										}
									}
								}
							}
						}
					}
					stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, (match && evaluate)));
				}
			}
		} else {
			stack.pop();
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, false));
		}
	}

	/**
	 * Implementation of the method <code>visit_Provides</code> of the <code>Visitable</code> interface.
	 * @param provides
	 */
	public void visit_Provides(BoolExprProvides provides) {
		// TODO
	}

	/**
	 * Implementation of the method <code>visit_Sum</code> of the <code>Visitable</code> interface.
	 * @param sum
	 */
	public void visit_Or(ResultvalueOr or) {
		if (or.getMode().equals(EvalMode.REPORT_MODE)) {
			out.println("&lt;or&gt;");
			Vector<Resultvalue> content = or.getContent();
			for (Resultvalue java : content) {
				java.setMode(EvalMode.REPORT_MODE);
				java.accept(this);
			}
			out.println("&lt;/or&gt;");
		} else if (or.getMode().equals(EvalMode.EVAL_MODE)) {
			//Old version without optimization
			/*Vector<Resultvalue> content = or.getContent();
			for (Resultvalue java : content) {
				java.setMode(EvalMode.EVAL_MODE);
				java.accept(this);
			}
			int size = content.size();
			boolean allString = true;
			Vector<String> result = new Vector<String>();
			for (int i=0; i<size; i++) {
				TypedObject to = stack.peek();
				stack.pop();
				if (to.getType() != SavedObjectType.STRING_TYPE && to.getType() != SavedObjectType.VECTOR_STRING) {
					allString = false;
				} else {
					if (to.getType() == SavedObjectType.STRING_TYPE) {
						String toString = (String)to.getObject();
						if (!result.contains(toString)) result.add(toString);
					} else {
						Vector<String> stringVect = (Vector<String>)to.getObject();
						for (String s : stringVect) {
							if (!result.contains(s)) result.add(s);
						}
					}
				}
			}*/

			//with optimization * | smth -> *
			Vector<Resultvalue> content = or.getContent();
			Iterator<Resultvalue> it = content.iterator();
			boolean end = false;
			while (it.hasNext() && !end) {
				Resultvalue res = it.next();
				res.setMode(EvalMode.EVAL_MODE);
				res.accept(this);
				TypedObject to = stack.peek();
				if (to.getType() == SavedObjectType.STRING_TYPE) {
					String toString = (String)to.getObject();
					if (toString.equals("*")) {
						end = true;
					}
				} else if (to.getType() == SavedObjectType.VECTOR_STRING) {
					Vector<String> stringVect = (Vector<String>)to.getObject();
					Iterator<String> its = stringVect.iterator();
					while (its.hasNext() && !end) {
						if (its.next().equals("*")) {
							stack.pop();
							stack.push(new TypedObject(SavedObjectType.STRING_TYPE, "*"));
							end = true;
						}
					}
				}
			}
			int size = content.size();
			boolean allString = true;
			Vector<String> result = new Vector<String>();
			int i=0;
			end = false;
			while (i<size && !end) {
				TypedObject to = stack.peek();
				stack.pop();
				if (to.getType() != SavedObjectType.STRING_TYPE && to.getType() != SavedObjectType.VECTOR_STRING) {
					allString = false;
					end = true;
				} else {
					if (to.getType() == SavedObjectType.STRING_TYPE) {
						String toString = (String)to.getObject();
						if (toString.equals("*")) {
							result.removeAllElements();
							result.add("*");
							end = true;
						} else {
							if (!result.contains(toString)) {
								result.add(toString);
							}
						}
					} else {
						Vector<String> stringVect = (Vector<String>)to.getObject();
						Iterator<String> itsv = stringVect.iterator();
						while (itsv.hasNext() && !end) {
							String s = itsv.next();
							if (s.equals("*")) {
								result.removeAllElements();
								result.add("*");
								end = true;
							} else {
								if (!result.contains(s)) result.add(s);
							}
						}
					}
				}
				i++;
			}
			if (content.size() == 0) {
				result.add("*");
			}
			if (!allString) {
				try {
					throw new Exception("we try to interpret a disjunction of non string values\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				stack.push(new TypedObject(SavedObjectType.VECTOR_STRING, result));
			}
		} else if (or.getMode().equals(EvalMode.PSEUDO_CONSTANT_MODE)) {
			Vector<Resultvalue> content = or.getContent();
			boolean isPseudo = true;
			int i=0;
			while (i<content.size() && isPseudo) {
				Resultvalue result = content.get(i);
				result.setMode(EvalMode.PSEUDO_CONSTANT_MODE);
				result.accept(this);
				TypedObject to = stack.peek();
				stack.pop();
				if (to.getType() == SavedObjectType.BOOLEAN_TYPE) {
					if (!(Boolean)to.getObject()) {
						isPseudo = false;
					}
				}
				i++;
			}
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, isPseudo));
		} else if (or.getMode().equals(EvalMode.STRING_MODE)) {
			Vector<Resultvalue> content = or.getContent();
			for (Resultvalue result : content) {
				result.setMode(EvalMode.STRING_MODE);
				result.accept(this);
			}
			int size = content.size();
			String resultString = "";
			for (int i=0; i<size; i++) {
				TypedObject to = stack.peek();
				stack.pop();
				String currentString = (String)to.getObject();
				if (size==1) {
					resultString = currentString;
				} else {
					if (i==0) {
						resultString += "("+currentString;
					} else if (i==(size-1)) {
						resultString += "|"+currentString+")";
					} else {
						resultString += "|"+currentString;
					}
				}
			}
			stack.push(new TypedObject(SavedObjectType.STRING_TYPE, resultString));
		} else if (or.getMode().equals(EvalMode.SAVE_MODE)) {
			stack.push(new TypedObject(SavedObjectType.RESULTVALUE_TYPE, or));
		}
	}

	/**
	 * Implementation of the method <code>visit_Results</code> of the <code>Visitable</code> interface.
	 * @param results
	 */
	public void visit_Results(SetExprResults results) {
		Vector<? extends Result> resultSet = resultLink.getResults(results.getRule());
		stack.push(new TypedObject(SavedObjectType.VECTOR_RESULT, resultSet));
	}

	/**
	 * Perform the evaluation of the resultvalue of a result
	 * @param result the <code>Result</code> object which contains resultvalue to evaluate
	 * @return a typedObject which represents the resultvalue
	 */
	public TypedObject evalResult(Result result) {
		result.getResultvalue().setMode(EvalMode.EVAL_MODE);
		result.getResultvalue().accept(this);
		TypedObject to = stack.peek();
		stack.pop();
		currentResult = result;
		String resultString = "";
		if (to.getType() == SavedObjectType.STRING_TYPE) {
			currentResult = result;
			resultString = (String)to.getObject();
		} else if (to.getType() == SavedObjectType.VECTOR_STRING) {
			currentResult = result;
			Vector<String> stringVect = (Vector<String>)to.getObject();
			stringVect = removeDouble(stringVect);
			if (stringVect.size()==1) {
				resultString+=stringVect.get(0);
			} else {
				resultString+="<ul>";
				for (String s : stringVect) {
					resultString+="<li>"+s+"</li>";
				}
				resultString+="</ul>";
			}
		} else {
			Logger.getLogger(this.getClass() ).warn("the java value is not a string: result name = "+result.getName()+" type : "+to.getType());
		}
		currentString = resultString;
		return to;
	}

	/**
	 * Remove double string in this vector
	 * @param stringVect
	 * @return
	 */
	private Vector<String> removeDouble(Vector<String> stringVect) {
		Vector<String> newVect = new Vector<String>();
		for (String s : stringVect) {
			if (!newVect.contains(s)) newVect.add(s);
		}
		return newVect;
	}

	private boolean belong(TypedObject to, Vector<TypedObject> unionVect) {
		boolean belong = false;
		Iterator it = unionVect.iterator();
		while (it.hasNext() && !belong) {
			TypedObject toRes = (TypedObject)it.next();
			if (isEqual(to, toRes)) {
				belong = true;
			}
		}
		return belong;
	}

	private boolean isEqual(Vector<TypedObject> lhs, Vector<TypedObject> rhs) {
		if (lhs.size()!=rhs.size()) {
			return false;
		} else {
			int size = lhs.size();
			boolean isequal = true;
			int i=0;
			while (i<size && isequal) {
				TypedObject lhsto = lhs.get(i);
				TypedObject rhsto = rhs.get(i);
				if (!lhsto.getType().equals(rhsto.getType())) {
					isequal =  false;
				} else {
					isequal = isEqual(lhsto, rhsto);
				}
				i++;
			}
			return isequal;
		}
	}

	private boolean isEqual(TypedObject lhsto, TypedObject rhsto) {
		if (lhsto.getType() == SavedObjectType.BOOLEAN_TYPE) {
			boolean lhsbool = (Boolean)lhsto.getObject();
			boolean rhsbool = (Boolean)rhsto.getObject();
			return (lhsbool == rhsbool);
		} else if (lhsto.getType() == SavedObjectType.INT_TYPE) {
			int lhsint = ((Integer)lhsto.getObject()).intValue();
			int rhsint = ((Integer)rhsto.getObject()).intValue();
			return (lhsint == rhsint);
		} else if(lhsto.getType() == SavedObjectType.FLOAT_TYPE) {
			float lhsint = ((Float)lhsto.getObject()).floatValue();
			float rhsint = ((Float)rhsto.getObject()).floatValue();
			return (lhsint == rhsint);
		} else if (lhsto.getType() == SavedObjectType.STRING_TYPE) {
			String lhsstring = (String)lhsto.getObject();
			String rhsstring = (String)rhsto.getObject();
			return (lhsstring.equals(rhsstring));
		} else if (lhsto.getType() == SavedObjectType.VECTOR_RESULT) {
			Vector<Result> lhsvector = (Vector<Result>)lhsto.getObject();
			Vector<Result> rhsvector = (Vector<Result>)rhsto.getObject();
			if (lhsvector.size()!= rhsvector.size()) {
				return false;
			} else {
				boolean isequal = true;
				int i=0;
				while (i<lhsvector.size() && isequal) {
					Result lhsRes = (Result)lhsvector.get(i);
					Result rhsRes = (Result)rhsvector.get(i);
					if (!lhsRes.equals(rhsRes)) {
						isequal = false;
					}
					i++;
				}
				return isequal;
			}
		} else if (lhsto.getType() == SavedObjectType.VECTOR_TYPED_OBJECT) {
			Vector<TypedObject> lhsvector = (Vector<TypedObject>)lhsto.getObject();
			Vector<TypedObject> rhsvector = (Vector<TypedObject>)rhsto.getObject();
			return (isEqual(lhsvector, rhsvector));
		} else if(lhsto.getType() == SavedObjectType.RESULTVALUE_TYPE) {
			Resultvalue javalhs = (Resultvalue)lhsto.getObject();
			Resultvalue javarhs = (Resultvalue)rhsto.getObject();
			javalhs.setMode(EvalMode.EVAL_MODE);
			javalhs.accept(this);
			javarhs.setMode(EvalMode.EVAL_MODE);
			javarhs.accept(this);
			TypedObject toLhs = stack.peek();
			stack.pop();
			TypedObject toRhs = stack.peek();
			stack.pop();
			return (isEqual(toLhs, toRhs));
		} else {
			return false;
		}
	}

	/**
	 * Implementation of the method <code>visit_Ispseudoconstant</code> of the <code>Visitable</code>
	 * interface.
	 * @param ispseudoconstant
	 */
	public void visit_Ispseudoconstant(BoolExprIspseudoconstant ispseudoconstant) {
		if (ispseudoconstant.getResultvalue()!=null) {
			ispseudoconstant.getResultvalue().setMode(EvalMode.PSEUDO_CONSTANT_MODE);
			ispseudoconstant.getResultvalue().accept(this);
		} else if (ispseudoconstant.getVar()!=null) {
			ispseudoconstant.getVar().accept(this);
		}
		if (stack.peek()!=null) {
			TypedObject typedObject = stack.peek();
			stack.pop();
			if (typedObject.getType() == SavedObjectType.BOOLEAN_TYPE) {
				Boolean bool = (Boolean)typedObject.getObject();
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, bool));
			} else if (typedObject.getType() == SavedObjectType.RESULT) {
				Result result = (Result)typedObject.getObject();
				Resultvalue res = result.getResultvalue();
				res.setMode(EvalMode.PSEUDO_CONSTANT_MODE);
				res.accept(this);
			} else if (typedObject.getType() == SavedObjectType.VECTOR_RESULT) {
				Vector<Result> results = (Vector<Result>)typedObject.getObject();
				boolean isPseudo = true;
				int i=0;
				while (i<results.size() && isPseudo) {
					Result result = results.get(i);
					Resultvalue res = result.getResultvalue();
					res.setMode(EvalMode.PSEUDO_CONSTANT_MODE);
					res.accept(this);
					TypedObject to = stack.peek();
					stack.pop();
					if (to.getType() == SavedObjectType.BOOLEAN_TYPE) {
						Boolean bool = (Boolean)to.getObject();
						isPseudo &= bool;
					}
					i++;
				}
				stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, isPseudo));
			} else {
				try {
					throw new Exception("The return type of the operation 'ispseudoconstant' " +
							"must be boolean. Given return type: "+typedObject.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			stack.pop();
			stack.push(new TypedObject(SavedObjectType.BOOLEAN_TYPE, false));
		}
	}

	public Stack<TypedObject> getStack() {
		return stack;
	}

}
