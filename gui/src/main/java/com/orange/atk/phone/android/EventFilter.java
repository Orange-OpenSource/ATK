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
 * File Name   : EventFilter.java
 *
 * Created     : 25/11/2009
 * Author(s)   : France Telecom
 */
package com.orange.atk.phone.android;

import java.io.UnsupportedEncodingException;

import com.android.ddmlib.IShellOutputReceiver;

/**
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
/**
 * Class used to filter output of android phone, a bit different from Multilinereceiver
 * @author Moreau Fabien - GFI - FMOREAU@gfi.fr
 *
 */
public abstract class EventFilter implements IShellOutputReceiver {
	
    private String mUnfinishedLine = null;
   


	private boolean cancel=false;
    
    public final void addOutput(byte[] data, int offset, int length) {
        if (!isCancelled()) {
            String s = null;
            try {
                s = new String(data, offset, length, "ISO-8859-1"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException e) {
                // normal encoding didn't work, try the default one
                s = new String(data, offset,length);
            }

            // ok we've got a string
            if (s != null) {
                // if we had an unfinished line we add it.
                if (mUnfinishedLine != null) {
                    s = mUnfinishedLine + s;
                    mUnfinishedLine = null;
                }

                int start = 0;
                do {
                    int index = s.indexOf("\r\n", start); //$NON-NLS-1$

                    // if \r\n was not found, this is an unfinished line
                    // and we store it to be processed for the next packet
                    if (index == -1) {
                        mUnfinishedLine = s.substring(start);
                        break;
                    }

                    // so we found a \r\n;
                    // extract the line
                    processline( s.substring(start, index) );

                    // move start to after the \r\n we found
                    start = index + 2;
                } while (!isCancelled());

            }
        }
    }

	public void flush() {
		if (mUnfinishedLine != null) {
           processline( mUnfinishedLine );
        }
		
	}

	public boolean isCancelled() {
		return cancel;
	}

	
	public void setCancelled(boolean cancel) {
			this.cancel = cancel;
	}
	//Specific to each phone and driver
	public abstract void processline(String line);
}