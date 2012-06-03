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
 * File Name   : IMeasureListener.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.manageListener;

import java.util.EventListener;

public interface IMeasureListener extends EventListener{
    void addactionChangee(String value);
    void addOutputChangee(String value);
    void addLoopChangee(String value);
    void LongValueChangee(long value,String cle);
    void FloatValueChangee(float value,String cle);
    void StringValueChangee(String value,String cle);
    public void StdOutputChangee(String value);

}
