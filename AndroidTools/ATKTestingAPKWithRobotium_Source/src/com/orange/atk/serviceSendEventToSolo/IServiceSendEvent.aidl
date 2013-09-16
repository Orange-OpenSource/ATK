/**
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
 * File Name   : IServiceSendEvent.aidl
 *
 * Created     : 02/05/2013
 * Author(s)   : France Telecom
 */
package com.orange.atk.serviceSendEventToSolo;

interface IServiceSendEvent {

   String[] getEvent();
   void setViews(String Views);
   String getViewsCommand();

}