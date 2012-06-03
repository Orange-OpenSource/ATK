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
 * File Name   : CGSMAlphabets.java
 *
 * Created     : 02/03/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.smspdu;

/**
This class contains the conversion routines to and from the standard 7bit
GSM alphabet.
<br><br>
Every normal ASCII character must be converted according to the GSM 7bit
default alphabet before dispatching through the GSM device. The opposite
conversion is made when a message is received.
<br><br>
Since some characters in 7bit alphabet are in the position where control
characters exist in the ASCII alphabet, each message is represented in
HEX format as well (field hexText in CMessage class and descendants).
When talking to the GSM device, either for reading messages, or for
sending messages, a special mode is used where each character of the
actual message is represented by two hexadecimal digits.
So there is another conversion step here, in order to get the ASCII
character from each pair of hex digits, and vice verca.
<br><br>
Note: currently, only GSM default 7Bit character set is supported.
In all routines, you may assume the "charSet" parameter as constant.
*/
class CGSMAlphabets
{
protected static final int GSM7BITDEFAULT = 1;

//private static final String alphabet = "@�$�@@@@@@@@@@@@�_���������@@@@@ !\"#�%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ@@@@�?abcdefghijklmnopqrstuvwxyz@@@@@";
private static final String alphabet = "@£$\u00A5\u00E8\u00E9\u00F9\u00EC\u00F2\u00C7\n\u00D8\u00F8\r\u00C5\u00E5Ä_ÖÃËÙÐØÓÈÎ@\u00C6\u00E6\u00DF\u00C9 !\"#\u00A4%&\'()*+,-./0123456789:;<=>?\u00A1ABCDEFGHIJKLMNOPQRSTUVWXYZ\u00C4\u00D6\u00D1\u00DC§\u00BFabcdefghijklmnopqrstuvwxyz\u00E4\u00F6\u00F1\u00FC\u00E0";

/**
	Converts an ASCII character to its hexadecimal pair.

	@param	c	the ASCII character.
	@param	charSet	the target character set for the conversion.
	@return	the two hex digits which represent the character in the
			specific character set.
*/
protected static String char2Hex(char c, int charSet)
{
	switch (charSet)
	{
		case GSM7BITDEFAULT:
			for (int i = 0; i < alphabet.length(); i ++)
				if (alphabet.charAt(i) == c) return (i <= 15 ? "0" + Integer.toHexString(i) : Integer.toHexString(i)); 
			break;
	}
	return (Integer.toHexString((int) c).length() < 2) ? "0" + Integer.toHexString((int) c) : Integer.toHexString((int) c);
}

/**
	Converts a hexadecimal value to the ASCII character it represents.

	@param	index	 the hexadecimal value.
	@param	charSet	the character set in which "index" is represented.
	@return  the ASCII character which is represented by the hexadecimal value.
*/
protected static char hex2Char(int index, int charSet)
{
	switch (charSet)
	{
		case GSM7BITDEFAULT:
			if (index < alphabet.length()) return alphabet.charAt(index);
			else return '?';
	}
	return '?';			
}

/**
	Converts a int value to the extended ASCII character it represents.
	@author George Karadimas
	@param	ch	 the int value.
	@param	charSet	the character set in which "ch" is represented.
	@return  the extended ASCII character which is represented by the int value.
*/
protected static char hex2ExtChar(int ch, int charSet)
{
	switch (charSet)
	{
		case GSM7BITDEFAULT:
			switch (ch)
			{
				case 10:
					return '\f';
				case 20:
					return '^';
				case 40:
					return '{';
				case 41:
					return '}';
				case 47:
					return '\\';
				case 60:
					return '[';
				case 61:
					return '~';
				case 62:
					return ']';
				case 64:
					return '|';
				case 101:
					return '\u20AC';
				default:
					return '?';
			}
		default:
			return '?';
	}
}

/**
	Converts the given ASCII string to a string of hexadecimal pairs.

	@param	text	the ASCII string.
	@param	charSet	the target character set for the conversion.
	@return	the string of hexadecimals pairs which represent the "text"
			parameter in the specified "charSet".
*/
protected static String text2Hex(String text, int charSet)
{
	String outText = "";

	for (int i = 0; i < text.length(); i ++)
	{
		switch (text.charAt(i))
		{
			case 'Á': case 'á': case 'Ü':
				outText = outText + char2Hex('A', charSet);
				break;
			case 'Â': case 'â':
				outText = outText + char2Hex('B', charSet);
				break;
			case 'Ã': case 'ã':
				outText = outText + char2Hex('Ã', charSet);
				break;
			case 'Ä': case 'ä':
				outText = outText + char2Hex('Ä', charSet);
				break;
			case 'Å': case 'å': case 'Ý':
				outText = outText + char2Hex('E', charSet);
				break;
			case 'Æ': case 'æ':
				outText = outText + char2Hex('Z', charSet);
				break;
			case 'Ç': case 'ç': case 'Þ':
				outText = outText + char2Hex('H', charSet);
				break;
			case 'È': case 'è':
				outText = outText + char2Hex('È', charSet);
				break;
			case 'É': case 'é': case 'ß':
				outText = outText + char2Hex('I', charSet);
				break;
			case 'Ê': case 'ê':
				outText = outText + char2Hex('K', charSet);
				break;
			case 'Ë': case 'ë':
				outText = outText + char2Hex('Ë', charSet);
				break;
			case 'Ì': case 'ì':
				outText = outText + char2Hex('M', charSet);
				break;
			case 'Í': case 'í':
				outText = outText + char2Hex('N', charSet);
				break;
			case 'Î': case 'î':
				outText = outText + char2Hex('Î', charSet);
				break;
			case 'Ï': case 'ï': case 'ü':
				outText = outText + char2Hex('O', charSet);
				break;
			case 'Ð': case 'ð':
				outText = outText + char2Hex('Ð', charSet);
				break;
			case 'Ñ': case 'ñ':
				outText = outText + char2Hex('P', charSet);
				break;
			case 'Ó': case 'ó': case 'ò':
				outText = outText + char2Hex('Ó', charSet);
				break;
			case 'Ô': case 'ô':
				outText = outText + char2Hex('T', charSet);
				break;
			case 'Õ': case 'õ': case 'ý':
				outText = outText + char2Hex('Y', charSet);
				break;
			case 'Ö': case 'ö':
				outText = outText + char2Hex('Ö', charSet);
				break;
			case '×': case '÷':
				outText = outText + char2Hex('X', charSet);
				break;
			case 'Ø': case 'ø':
				outText = outText + char2Hex('Ø', charSet);
				break;
			case 'Ù': case 'ù': case 'þ':
				outText = outText + char2Hex('Ù', charSet);
				break;
			case '\f':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(10);
				break;
			case '^':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(20);
				break;
			case '{':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(40);
				break;
			case '}':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(41);
				break;
			case '\\':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(47);
				break;
			case '[':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(60);
				break;
			case '~':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(61);
				break;
			case ']':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(62);
				break;
			case '|':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(64);
				break;
			case '\u20AC':
				outText = outText + Integer.toHexString(27) + Integer.toHexString(101);
				break;
			default:
				outText = outText + char2Hex(text.charAt(i), charSet);
				break;
		}
	}
	return outText;
}


/**
	Converts the given string of hexadecimal pairs to its ASCII equivalent string.

	@param	text	the hexadecimal pair string.
	@param	charSet	the target character set for the conversion.
	@return	the ASCII string.
*/
protected static String hex2Text(String text, int charSet)
{
	String outText = "";

	for (int i = 0; i < text.length(); i += 2)
	{
		String hexChar = "" + text.charAt(i) + text.charAt(i + 1);
		int c = Integer.parseInt(hexChar, 16);
		if (c == 27)
		{
			i ++;
			outText = outText + hex2ExtChar((char) c, charSet);
		}
		else outText = outText + hex2Char((char) c, charSet);
	}
	return outText;
}
}