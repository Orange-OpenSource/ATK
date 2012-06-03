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
 * File Name   : SendMailJATK.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.smspdu;
//package smspdu;
//
//import org.apache.commons.mail.EmailException;
//import org.apache.commons.mail.HtmlEmail;
//
///**
// *
// * @author ywil8421
// */
//public class SendMailJATK {
//    
//    
//     public void sendEmail(String Subject,String Msg,String EmailDest,String NameDest,String NameSrc,String EmailSrc){
//        
//     HtmlEmail    email = new HtmlEmail();
//
//        try {
//            email.setSmtpPort(2525);
//            email.setHostName("mailhost");
//            email.addTo(EmailDest , NameDest);                      
//           // email.setFrom("yvain.leyral@orange-ftgroup.com", "Yvain Leyral");
//            email.setFrom(EmailSrc, NameSrc);
//            email.setSubject(Subject);
//            email.setHtmlMsg(Msg);
//            email.send();
//        } catch (EmailException ex) {
//            ex.printStackTrace();
//        }   
//        
//        
//    }
//
//}