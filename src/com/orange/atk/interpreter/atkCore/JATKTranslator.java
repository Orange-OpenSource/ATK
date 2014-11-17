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
 * File Name   : JATKTranslator.java
 *
 * Created     : 08/07/2014
 * Author(s)   : François Jégou
 */
package com.orange.atk.interpreter.atkCore;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.orange.atk.interpreter.ast.ASTBOOLEAN;
import com.orange.atk.interpreter.ast.ASTCOMMENT;
import com.orange.atk.interpreter.ast.ASTFLOAT;
import com.orange.atk.interpreter.ast.ASTFUNCTION;
import com.orange.atk.interpreter.ast.ASTINCLUDE;
import com.orange.atk.interpreter.ast.ASTLOOP;
import com.orange.atk.interpreter.ast.ASTNUMBER;
import com.orange.atk.interpreter.ast.ASTSETVAR;
import com.orange.atk.interpreter.ast.ASTSTRING;
import com.orange.atk.interpreter.ast.ASTStart;
import com.orange.atk.interpreter.ast.ASTTABLE;
import com.orange.atk.interpreter.ast.ASTVARIABLE;
import com.orange.atk.interpreter.ast.SimpleNode;
import com.orange.atk.interpreter.ast.SoloMethods;
import com.orange.atk.interpreter.parser.ATKScriptParserVisitor;
import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.platform.Platform;


/**
 * Interpreter for the Java Accelerator ToolKit (JATK)
 */

public class JATKTranslator implements ATKScriptParserVisitor {

    // Common errors
    private static final String LOOP_VALUE_IS_NEGATIVE = "Loop value is negative"; //$NON-NLS-1$
    private static final String STOPMAIN_HAS_NOT_BEEN_CALLED = "StopMainLog has not been called"; //$NON-NLS-1$
    private static final String IS_NOT_A_VARIABLE = " is not a variable..."; //$NON-NLS-1$

    // Stack used to store temporary informations
    private JATKInterpreterStack stack = new JATKInterpreterStack();
    // Table which stores(variable,value) information
    private JATKVariableTable variables = new JATKVariableTable();
    // Internal state of the translator
    private JATKInterpreterInternalState internalState = new JATKInterpreterInternalState();
    // Proxy for actions translation
    private ActionToTranslate actions;

    private PhoneInterface phoneInterface = null;

    /**
     * Constructor
     *
     * @param p
     *            phone tested
      * @param currentScript
     *            current script tested
     * @param logDir
     *            folder where result files are stored
     * @param includeDir
     *            folder where include files are stored
     */
    public JATKTranslator(PhoneInterface p, String currentScript,
                           String logDir, String includeDir) {
        internalState.setCurrentScript(currentScript);
        internalState.setLogDir(logDir + Platform.FILE_SEPARATOR);
        phoneInterface = p;
        actions = new ActionToTranslate(this);
    }

    public Object visit(ASTFUNCTION node, Object data) {
        return runAction(node, data);
    }

    private Object runAction(SimpleNode node, Object data) {
        // visit children
        node.childrenAccept(this, data);

        // analyze the name part
        String functionName = node.getValue();

       //get variables
        Variable[] tablevariables = new Variable[stack.size()];
        tablevariables = stack.toArray(tablevariables);
        //empty stack
        while(! stack.empty()) stack.pop();
        if(new SoloMethods().isSoloMethod(functionName)){
            try {
                return actions.actionExecuteSoloMethod((ASTFUNCTION)node, tablevariables);
            } catch (Exception e) {
                Logger.getLogger(this.getClass() ).debug("erreur during execution of "+functionName);
                e.printStackTrace();
            }
        } else {
            //SEARCH function in action class and invoke it.
            Method[] functions = actions.getClass().getMethods();
            for (Method function : functions)
                if(function.getName().toLowerCase().
                        equals("action"+functionName.toLowerCase()) ) {
                    try {
                        return function.invoke(actions, node,  tablevariables  );

                    } catch (Exception e) {
                        Logger.getLogger(this.getClass() ).debug("erreur during execution of "+function.getName());
                        e.printStackTrace();
                    }
                }
        }



        //we don't have found the function
        /*getMainLogger().addErrorToDocumentLogger(
                functionName + " is not a valid function",
                node.getLineNumber(), internalState.getCurrentScript());*/
        return Boolean.FALSE;
		
		/*
		 * Could not happens anymore due to control in actions class } catch
		 * (ClassCastException ex) { mainLogger.addErrorToLog("Invalid given
		 * argument", node .getLineNumber(), internalState.getCurrentScript());
		 * ex.printStackTrace(); return Boolean.FALSE; }
		 */
    }

    public Object visit(ASTCOMMENT node, Object data) {
//		Logger.getLogger(this.getClass() ).debug("read comment value : "+node.getValue());
        return node.childrenAccept(this, data);
    }

   public Object visit(ASTLOOP node, Object data) {
        Integer nbLoop = null;
        //int nbNodes = node.jjtGetNumChildren();


        // On obtient le nombre de loop (=n)
        // puis on repetera n fois children.Accept
        node.jjtGetChild(0).jjtAccept(this, data);


        nbLoop = stack.popInteger();
        /*if (nbLoop < 0) {
            getMainLogger()
                    .addErrorToDocumentLogger(LOOP_VALUE_IS_NEGATIVE,
                            node.getLineNumber(),
                            getInternalState().getCurrentScript());
        }*/
        //Logger.getLogger(this.getClass() ).debug("Loop n = " + nbLoop);

        int previousLoopValue = internalState.getLoopValue();
        // number of loop specified for the loop
        for (int i = 0; i < nbLoop; i++) {
            // childs are visited, excepted the first one (the number of
            // loop)
            //Logger.getLogger(this.getClass() ).debug("NbLoop = " + i);
            internalState.setLoopValue(i);
            for (int j = 1; j < node.jjtGetNumChildren(); j++) {
                Object res = node.jjtGetChild(j).jjtAccept(this, data);
                if (res != null) {
                    if (!((Boolean) res).booleanValue()) {
                        return Boolean.FALSE;
                    }
                }
            }
        }
        internalState.setLoopValue(previousLoopValue);
        return Boolean.TRUE;

    }

   public Object visit(ASTINCLUDE node, Object data) {
        // If include as not been already parsed
        if (node.jjtGetNumChildren()==1) {
            return runAction(node, data);
        } else {
            for (int j = 1; j < node.jjtGetNumChildren(); j++) {
                Object res = node.jjtGetChild(j).jjtAccept(this, data);
                if (res != null) {
                    if (!((Boolean) res).booleanValue()) {
                        return Boolean.FALSE;
                    }
                }
            }
        }
        return Boolean.TRUE;
    }


   public Object visit(ASTNUMBER node, Object data) {

        // push the value of integer
        stack.pushInteger(Integer.parseInt(node.getValue()));

        return Boolean.TRUE;
    }

   public Object visit(ASTSETVAR node, Object data) {

        // We expect two elements in the stack
        node.childrenAccept(this, data);
        // The stack should contain two elements:
        // One string for the variable name
        // One element (a String or a Integer)


        // First element of the stack is the variable
        // value
        if (stack.isTopInteger()) {
            // Variable type is an integer
            Integer o = stack.popInteger();
            String variableName = "_"+stack.popString().trim();
            // Save the variable
            getVariables().addIntegerVariable(variableName, o);
        } else {
            // Variable type is a String
            String o = stack.popString();
            String variableName = "_"+stack.popString().trim();
            // Save the variable
            getVariables().addStringVariable(variableName, o);
        }

        return Boolean.TRUE;

    }

    // Entry point of the interpreter
    /**
     * Entry point of the interpreter
     */
    public Object visit(ASTStart node, Object data) {
        // Start of interpreter
        Boolean res = (Boolean) node.childrenAccept(this, Boolean.TRUE);
        if (res == null) return Boolean.FALSE;
        return res;
    }

     public Object visit(ASTSTRING node, Object data) {

        // push the value of integer
        stack.pushString(node.getValue());

        return Boolean.TRUE;
    }

    public Object visit(ASTTABLE node, Object data) {

        String marker = "TABLEMARKER#"+Math.random();
        stack.pushString(marker);

        // visit children
        node.childrenAccept(this, data);

        //empty stack until marker
        ArrayList<Variable> reversetable = new ArrayList<Variable>();
        Variable temp = stack.pop();
        while ( !(temp.isString() && temp.getString().equals(marker) )){
            reversetable.add(temp);
            temp = stack.pop();
        }

        //reverse table
        ArrayList<Variable> table = new ArrayList<Variable>();
        for(int i=reversetable.size()-1 ; i>=0; i--)
            table.add(reversetable.get(i));

        stack.pushTable(table);

        return Boolean.TRUE;
    }


   public Object visit(ASTVARIABLE node, Object data) {

        // check if the referenced value has been previously defined
        if (!getVariables().isVariable(node.getValue().trim())) {
            /*getMainLogger().addErrorToDocumentLogger(node.getValue() + IS_NOT_A_VARIABLE,
                    node.getLineNumber(), internalState.getCurrentScript());*/
            return Boolean.FALSE;
        }

        // if yes, get the value and return it
        Variable var = getVariables().getVariable(node.getValue().trim());
        if (var.isInteger()) {
            stack.pushInteger(var.getInteger());
        } else if (var.isString()) {
            stack.pushString(var.getString());
        }
		/*
		 * else { mainLogger.addErrorToLog("Unknown type... ",
		 * node.getLineNumber(), internalState.getCurrentScript()); return
		 * Boolean.FALSE; }
		 */

        return Boolean.TRUE;
    }

   public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

/*
    protected ResultLogger getMainLogger() {
        return null;
    }
*/

    protected JATKInterpreterStack getStack() {
        return stack;
    }

    protected JATKVariableTable getVariables() {
        return variables;
    }

    /**
     * This function returns the object which stores current state of
     * interpreter
     *
     * @return an OCTKInterpreterInternalState object
     */
    protected JATKInterpreterInternalState getInternalState() {
        return internalState;
    }

    /**
     * @return interface to the phone tested
     */
    public PhoneInterface getPhoneInterface() {
        return phoneInterface;
    }

    @Override
    public Object visit(ASTBOOLEAN node, Object data) {
        stack.pushBoolean(Boolean.parseBoolean(node.getValue()));
        return Boolean.TRUE;
    }

    @Override
    public Object visit(ASTFLOAT node, Object data) {
        stack.pushFloat(Float.parseFloat(node.getValue()));
        return Boolean.TRUE;
    }
}
