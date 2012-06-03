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
 * File Name   : MatosFOPLogger.java
 *
 * Created     : 26/05/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.atkUI.corecli.utils;

import org.apache.avalon.framework.logger.Logger;

public class MatosFOPLogger implements Logger {

	public MatosFOPLogger() {
        this(0);
    }

    public MatosFOPLogger(int logLevel) {
		m_logLevel = logLevel;
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Throwable throwable) {
        if(m_logLevel <= 0) {
            Out.log.print("[FOP DEBUG] ");
            Out.log.println(message);
            if(null != throwable)
                throwable.printStackTrace(Out.log);
        }
    }

    public boolean isDebugEnabled() {
        return m_logLevel <= 0;
    }

    public void info(String message) {
        info(message, null);
    }

    public void info(String message, Throwable throwable) {
        if(m_logLevel <= 1) {
        	Out.log.print("[FOP INFO] ");
        	Out.log.println(message);
            if(null != throwable)
                throwable.printStackTrace(Out.log);
        }
    }

    public boolean isInfoEnabled() {
        return m_logLevel <= 1;
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void warn(String message, Throwable throwable) {
        if(m_logLevel <= 2) {
        	Out.log.print("[FOP WARNING] ");
        	Out.log.println(message);
            if(null != throwable)
                throwable.printStackTrace(Out.log);
        }
    }

    public boolean isWarnEnabled() {
        return m_logLevel <= 2;
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Throwable throwable) {
        if(m_logLevel <= 3) {
        	Out.log.print("[FOP ERROR] ");
        	Out.log.println(message);
            if(null != throwable)
                throwable.printStackTrace(Out.log);
        }
    }

    public boolean isErrorEnabled() {
        return m_logLevel <= 3;
    }

    public void fatalError(String message) {
        fatalError(message, null);
    }

    public void fatalError(String message, Throwable throwable) {
        if(m_logLevel <= 4) {
        	Out.log.print("[FOP FATAL ERROR] ");
        	Out.log.println(message);
            if(null != throwable)
                throwable.printStackTrace(Out.log);
        }
    }

    public boolean isFatalErrorEnabled() {
        return m_logLevel <= 4;
    }

    public Logger getChildLogger(String name) {
        return this;
    }

    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARN = 2;
    public static final int LEVEL_ERROR = 3;
    public static final int LEVEL_FATAL = 4;
    public static final int LEVEL_DISABLED = 5;
    private final int m_logLevel;
}