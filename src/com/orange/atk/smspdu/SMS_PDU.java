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
 * File Name   : SMS_PDU.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.smspdu;

import java.util.BitSet;

/**
 *
 * @author ywil8421
 */
public class SMS_PDU {

    	public static final int MESSAGE_ENCODING_7BIT = 1;
	public static final int MESSAGE_ENCODING_8BIT = 2;
	public static final int MESSAGE_ENCODING_UNICODE = 3;

	public static final int TYPE_INCOMING = 1;
	public static final int TYPE_OUTGOING = 2;
 private String SCA ="";
 private String pdu ="";
 




	/**
		Return a SMS pdu.

		@param	smscNumber	 smscNumber.
		@param	Phone_Number	 Destination Phone Number
         *      @param	MessageEncoding	 Message encoding (7 BIT,8 BIT or UNICODE)
         *      @param	Msg	          SMS Message
		@return  SMS pdu.
	*/


	public String Create_SMS_PDU(String smscNumber,String Phone_Number,int MessageEncoding,String Msg)
	{

		calculateSCA( smscNumber);
		// 11 First octet of the SMS-SUBMIT message. 	
		String PDU_Type = "11";
		// 00 TP-Message-Reference. The "00" value here lets the phone set the message reference number itself. 
		String MR=  "00";
		
		Phone_Number = ConverttoBCDFormat(Phone_Number.substring(1));
		String Phone_number_Hexa_length  = Integer.toHexString(Phone_Number.length() - 1);
		if (Phone_number_Hexa_length.length() != 2) Phone_number_Hexa_length = "0" + Phone_number_Hexa_length;

                //Type-of-Number. (91 indicates international format of the phone number).  
                String Number_type="91";
                
                String PID="00"; 
                
                 String DCS="00"; 
		switch (MessageEncoding)
		{
			case MESSAGE_ENCODING_7BIT:
				DCS= "00";
				break;
			case MESSAGE_ENCODING_8BIT:
				DCS= "04";
				break;
			case MESSAGE_ENCODING_UNICODE:
				DCS= "08";
				break;
		}
                
                //Validity period
		String VP=  "AA";
                 pdu = SCA +PDU_Type+MR+Phone_number_Hexa_length+Number_type+Phone_Number+ PID+DCS+VP;
                 //Encodage du message
                 Messagencoding( MessageEncoding, Msg);
		 return pdu.toUpperCase();
	}

        
        /**
		Encode pdu.
         *      @param	MessageEncoding	 Message encoding (7 BIT,8 BIT or UNICODE)
         *      @param	Msg	          SMS Message

	*/
        
        
        private void Messagencoding(int MessageEncoding,String Msg)
                
        {
           String Msg_length_hexa ="";
           String tmpStringInt_toHex = "";
         switch (MessageEncoding)
		{
			case MESSAGE_ENCODING_7BIT:
				String Msg_length = Integer.toHexString(Msg.length());
				if (Msg_length.length() != 2) Msg_length = "0" + Msg_length;
				pdu = pdu + Msg_length + textToPDU(Msg);
				break;
			case MESSAGE_ENCODING_8BIT:
				for (int i = 0; i < Msg.length(); i ++)
				{
					char c = Msg.charAt(i);
					tmpStringInt_toHex = tmpStringInt_toHex + ((Integer.toHexString((int) c).length() < 2) ? "0" + Integer.toHexString((int) c) : Integer.toHexString((int) c));  
				}
				 Msg_length_hexa = Integer.toHexString(Msg.length());
				if (Msg_length_hexa.length() != 2) Msg_length_hexa = "0" + Msg_length_hexa;
				pdu = pdu + Msg_length_hexa + tmpStringInt_toHex;
				break;
			case MESSAGE_ENCODING_UNICODE:
				for (int i = 0; i < Msg.length(); i ++)
				{
					char c = Msg.charAt(i);
		 	                int high = (int) (c / 256);
					int low = c % 256;
					tmpStringInt_toHex = tmpStringInt_toHex + ((Integer.toHexString(high).length() < 2) ? "0" + Integer.toHexString(high) : Integer.toHexString(high));
					tmpStringInt_toHex = tmpStringInt_toHex + ((Integer.toHexString(low).length() < 2) ? "0" + Integer.toHexString(low) : Integer.toHexString(low));
				}
				 Msg_length_hexa = Integer.toHexString(Msg.length() * 2);
				if (Msg_length_hexa.length() != 2) Msg_length_hexa = "0" + Msg_length_hexa;
				pdu = pdu + Msg_length_hexa + tmpStringInt_toHex;
				break;
		}   
            
        }

        
        
          /**
		convert decimal to semi decimal octet
         *      @param	s	 Message to encode
         *     
	*/
        
        
	private String ConverttoBCDFormat(String s)
	{
		String bcd;
		int i;

		if ((s.length() % 2) != 0) s = s + "F";
		bcd = "";
		for (i = 0; i < s.length(); i += 2) bcd = bcd + s.charAt(i + 1) + s.charAt(i);
		return bcd; 
	}

        
        
        
          /**
		convert text to PDU
         *      @param	text	 Message to encode
         *     
	*/
        
        private String textToPDU(String text)
	{
		String pdu, str1;
		byte[] oldBytes, newBytes;
		BitSet bitSet;
		int i, j, k, value1, value2,Char_code;

		str1 = "";	
                //Convert Text
		text = CGSMAlphabets.text2Hex(text, CGSMAlphabets.GSM7BITDEFAULT);
		for (i = 0; i < text.length(); i += 2)
		{
			Char_code = (Integer.parseInt("" + text.charAt(i), 16) * 16) + Integer.parseInt("" + text.charAt(i + 1), 16);
			str1 += (char) Char_code;
		}
		text = str1; 
		oldBytes = text.getBytes();
		bitSet = new BitSet(text.length() * 8);

		value1 = 0;
		for (i = 0; i < text.length(); i ++)
			for (j = 0; j < 7; j ++)
			{
				value1 = (i * 7) + j;
				if ((oldBytes[i] & (1 << j)) != 0) bitSet.set(value1);
			}
		value1 ++;

		if (((value1 / 56) * 56) != value1) value2 = (value1 / 8) + 1;
		else value2 = (value1 / 8);
		if (value2 == 0) value2 = 1;

		newBytes = new byte[value2];
		for (i = 0; i < value2; i ++)
			for (j = 0; j < 8; j ++)
				if ((value1 + 1) > ((i * 8) + j))
					if (bitSet.get(i * 8 + j)) newBytes[i] |= (byte) (1 << j);

		pdu = "";
		for (i = 0; i < value2; i ++)
		{
			str1 = Integer.toHexString((int) newBytes[i]);
			if (str1.length() != 2) str1 = "0" + str1;
			str1 = str1.substring(str1.length() - 2, str1.length());
			pdu += str1;
		}
		return pdu;
	}
        
        
        
          /**
		Calculate SCA
           * 
         *      @param	smscNumber	 smsc Number
         *     
	*/
        
        protected String  calculateSCA(String smscNumber)
{
if ((smscNumber != null) && (smscNumber.length() != 0))
		{
			//91 for international code (eg: fr 33) and convert phone number in semi decimal octet
			String Phone_number = "91" + ConverttoBCDFormat(smscNumber.substring(1));
			
			//Hexadecimale string length
			String Phone_number_Hexa_length = Integer.toHexString(Phone_number.length() / 2);
			
			//Zero means that the mobile will handle with default smscNumber
			if (Phone_number_Hexa_length.length() != 2) Phone_number_Hexa_length = "0" + Phone_number_Hexa_length;
			SCA = SCA + Phone_number_Hexa_length + Phone_number;
		}
		else if ((smscNumber != null) && (smscNumber.length() == 0)) 
			 // 00 Length of SMSC information. Here the length is 0, which means that the SMSC stored in the phone should be used. 		
			SCA= "00";  
return SCA;
 
    
    
}
}