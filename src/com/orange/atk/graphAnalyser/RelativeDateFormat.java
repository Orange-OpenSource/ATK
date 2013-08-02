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
 * File Name   : RelativeDateFormat.java
 *
 * Created     : 02/12/2010
 * Author(s)   : HENAFF Mari-Mai
 */
package com.orange.atk.graphAnalyser;


import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;


public class RelativeDateFormat extends DateFormat {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The base milliseconds for the elapsed time calculation. */
    private long baseMillis;
    
    /**
     * A flag that controls whether or not a zero day count is displayed.
     */
    private boolean showZeroDays;
    
    /**
     * A flag that controls whether or not a zero day count is displayed.
     */
    private boolean showZeroHours;

    /** 
     * A formatter for the day count (most likely not critical until the
     * day count exceeds 999). 
     */
    private NumberFormat dayFormatter;
    
    /**
     * A string appended after the day count.
     */
    private String daySuffix;
    
    /**
     * A string appended after the hours.
     */
    private String hourSuffix;
    
    /**
     * A string appended after the minutes.
     */
    private String minuteSuffix;
    
    /**
     * A formatter for the seconds (and milliseconds).
     */
    private NumberFormat secondFormatter;
    
    /**
     * A string appended after the seconds.
     */
    private String secondSuffix;

    /**
     * A constant for the number of milliseconds in one hour.
     */
    private static long MILLISECONDS_IN_ONE_HOUR = 60 * 60 * 1000L;

    /**
     * A constant for the number of milliseconds in one day.
     */
    private static long MILLISECONDS_IN_ONE_DAY = 24 * MILLISECONDS_IN_ONE_HOUR;
    
    /**
     * Creates a new instance.
     */
    public RelativeDateFormat() {
        this(0L);  
    }
    
    /**
     * Creates a new instance.
     * 
     * @param time  the date/time (<code>null</code> not permitted).
     */
    public RelativeDateFormat(Date time) {
        this(time.getTime());
    }
    
    /**
     * Creates a new instance.
     * 
     * @param baseMillis  the time zone (<code>null</code> not permitted).
     */
    public RelativeDateFormat(long baseMillis) {
        super();
        this.baseMillis = baseMillis;
        this.showZeroDays = false;
        this.showZeroHours = false;
        this.dayFormatter = NumberFormat.getInstance();
        this.daySuffix = "d ";
        this.hourSuffix = "h ";
        this.minuteSuffix = ":";
        this.secondFormatter = NumberFormat.getNumberInstance();
        this.secondFormatter.setMaximumFractionDigits(3);
        this.secondFormatter.setMinimumFractionDigits(3);
        this.secondSuffix = "";
    }
    
    /**
     * Returns the base date/time used to calculate the elapsed time for 
     * display.
     * 
     * @return The base date/time in milliseconds since 1-Jan-1970.
     * 
     * @see #setBaseMillis(long)
     */
    public long getBaseMillis() {
        return this.baseMillis;
    }
    
    /**
     * Sets the base date/time used to calculate the elapsed time for display.  
     * This should be specified in milliseconds using the same encoding as
     * <code>java.util.Date</code>.
     * 
     * @param baseMillis  the base date/time in milliseconds.
     * 
     * @see #getBaseMillis()
     */
    public void setBaseMillis(long baseMillis) {
        this.baseMillis = baseMillis;
    }
    
    /**
     * Returns the flag that controls whether or not zero day counts are 
     * shown in the formatted output.
     * 
     * @return The flag.
     * 
     * @see #setShowZeroDays(boolean)
     */
    public boolean getShowZeroDays() {
        return this.showZeroDays;
    }
    
    /**
     * Sets the flag that controls whether or not zero day counts are shown
     * in the formatted output.
     * 
     * @param show  the flag.
     * 
     * @see #getShowZeroDays()
     */
    public void setShowZeroDays(boolean show) {
        this.showZeroDays = show;
    }
    
    /**
     * Returns the string that is appended to the day count.
     * 
     * @return The string.
     * 
     * @see #setDaySuffix(String)
     */
    public String getDaySuffix() {
        return this.daySuffix;
    }
    
    /**
     * Sets the string that is appended to the day count.
     * 
     * @param suffix  the suffix.
     * 
     * @see #getDaySuffix()
     */
    public void setDaySuffix(String suffix) {
        this.daySuffix = suffix;
    }

    /**
     * Returns the string that is appended to the hour count.
     * 
     * @return The string.
     * 
     * @see #setHourSuffix(String)
     */
    public String getHourSuffix() {
        return this.hourSuffix;
    }
    
    /**
     * Sets the string that is appended to the hour count.
     * 
     * @param suffix  the suffix.
     * 
     * @see #getHourSuffix()
     */
    public void setHourSuffix(String suffix) {
        this.hourSuffix = suffix;
    }

    /**
     * Returns the string that is appended to the minute count.
     * 
     * @return The string.
     * 
     * @see #setMinuteSuffix(String)
     */
    public String getMinuteSuffix() {
        return this.minuteSuffix;
    }
    
    /**
     * Sets the string that is appended to the minute count.
     * 
     * @param suffix  the suffix.
     * 
     * @see #getMinuteSuffix()
     */
    public void setMinuteSuffix(String suffix) {
        this.minuteSuffix = suffix;
    }

    /**
     * Returns the string that is appended to the second count.
     * 
     * @return The string.
     * 
     * @see #setSecondSuffix(String)
     */
    public String getSecondSuffix() {
        return this.secondSuffix;
    }
    
    /**
     * Sets the string that is appended to the second count.
     * 
     * @param suffix  the suffix.
     * 
     * @see #getSecondSuffix()
     */
    public void setSecondSuffix(String suffix) {
        this.secondSuffix = suffix;
    }
    
    /**
     * Sets the formatter for the seconds and milliseconds.
     * 
     * @param formatter  the formatter (<code>null</code> not permitted).
     */
    public void setSecondFormatter(NumberFormat formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.secondFormatter = formatter;
    }

    /**
     * Formats the given date as the amount of elapsed time (relative to the
     * base date specified in the constructor).
     * 
     * @param date  the date.
     * @param toAppendTo  the string buffer.
     * @param fieldPosition  the field position.
     * 
     * @return The formatted date.
     */
    public StringBuffer format(Date date, StringBuffer toAppendTo,
                               FieldPosition fieldPosition) {
        long currentMillis = date.getTime();
        if (currentMillis==0) return toAppendTo;
        
        long elapsed = currentMillis - baseMillis;
        
        long days = elapsed / MILLISECONDS_IN_ONE_DAY;
        elapsed = elapsed - (days * MILLISECONDS_IN_ONE_DAY);
        long hours = elapsed / MILLISECONDS_IN_ONE_HOUR;
        elapsed = elapsed - (hours * MILLISECONDS_IN_ONE_HOUR);
        long minutes = elapsed / 60000L;
        elapsed = elapsed - (minutes * 60000L);
        double seconds = elapsed / 1000.0;
        if (days != 0 || this.showZeroDays) {
            toAppendTo.append(this.dayFormatter.format(days) + getDaySuffix());
        }
        if (hours !=0  ||  this.showZeroHours || days !=0 || this.showZeroDays) {
        	toAppendTo.append(String.valueOf(hours) + getHourSuffix());
        }
        toAppendTo.append(String.valueOf(minutes) + getMinuteSuffix());
        toAppendTo.append(this.secondFormatter.format(seconds) 
                + getSecondSuffix());
        return toAppendTo;   
    }

    /**
     * Parses the given string (not implemented).
     * 
     * @param source  the date string.
     * @param pos  the parse position.
     * 
     * @return <code>null</code>, as this method has not been implemented.
     */
    public Date parse(String source, ParsePosition pos) {
        return null;   
    }

    /**
     * Tests this formatter for equality with an arbitrary object.
     * 
     * @param obj  the object.
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RelativeDateFormat)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        RelativeDateFormat that = (RelativeDateFormat) obj;
        if (this.baseMillis != that.baseMillis) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode(){
    	final int prime = 31;
		int result = 1;
		result = (int) (prime * result + super.hashCode() + (int)(this.baseMillis^(this.baseMillis>>>32)));
		return result;
    }

 }