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
 * File Name   : AROLauncher.java
 *
 * Created     : 14/03/14
 * Author(s)   : pcrepieux
 */
package com.orange.atk.atkUI.coregui;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.platform.Platform;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class AROLauncher {
    private static final String LAUNCHARO = Configuration.getProperty(Configuration.AROPATH);
    private static final String AROPATH = LAUNCHARO.substring(0,LAUNCHARO.lastIndexOf("aro")-1);

    private static ProcessBuilder initProcessBuilder(){
        Logger.getLogger(AROLauncher.class).info(LAUNCHARO + " from " + AROPATH);
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(AROPATH));
        pb.inheritIO();
        return pb;
    }

    public static void start(){
        try {
            ProcessBuilder pb = initProcessBuilder();
            pb.command(LAUNCHARO);
            Process p = pb.start();
        } catch (IOException e) {
            Logger.getLogger(AROLauncher.class).error("Unable to start ARODataAnalyzer", e);
        }
    }

    public static void start(String path){
        try {
            ProcessBuilder pb = initProcessBuilder();
            pb.command(LAUNCHARO,"-d",path);
            Process p = pb.start();
        } catch (IOException e) {
            Logger.getLogger(AROLauncher.class).error("Unable to start ARODataAnalyzer", e);
        }
    }

}
