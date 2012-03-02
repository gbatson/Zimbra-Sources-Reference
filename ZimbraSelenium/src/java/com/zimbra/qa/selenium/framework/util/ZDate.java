package com.zimbra.qa.selenium.framework.util;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;

public class ZDate {
	Logger logger = LogManager.getLogger(ZDate.class);

	public static final TimeZone TimeZoneUTC = TimeZone.getTimeZone("UTC");
	
	
	protected Calendar calendar = null;
	
	public ZDate(int year, int month, int monthday, int hour, int minutes, int seconds) {
		
		// TODO: Handle errors (such as month = 0)
		
		calendar = Calendar.getInstance();
		
		calendar.setTimeZone(TimeZoneUTC);
		
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, monthday);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, seconds);
		
		logger.info("New "+ ZDate.class.getName());
	}
	
	public ZDate(Element e) throws HarnessException {
		
		String d = e.getAttribute("d", null);
		String tz = e.getAttribute("tz", null);
		String u = e.getAttribute("u", null);
		
		if ( tz == null ) {
			// Assume GMT
			// TODO
		}
		
		if ( u != null ) {
			
			// Parse the unix time
			long unix = new Long(u).longValue();
			calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZoneUTC);
			calendar.setTimeInMillis(unix);
			return;
			
		}
		
		if ( d != null ) {
			
			// TODO
			calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZoneUTC);
			return;
			
		}
		
		throw new HarnessException("Unable to parse time element "+ e.prettyPrint());
	}
	
	public long toMillis() {
		if ( calendar == null ) {
			calendar = Calendar.getInstance();
		}
		long t = calendar.getTimeInMillis();
		return ( (t / 1000) * 1000); // strip any millisecond blah
	}
	
	public String toYYYYMMDDTHHMMSSZ() throws HarnessException {
		return (format("yyyyMMdd'T'HHmmss'Z'"));
	}
	
	public String toYYYYMMDD() throws HarnessException {
		return (format("yyyyMMdd"));
	}

	public String toYYYYMMDDHHMMSSZ() throws HarnessException {
		return (format("yyyyMMddHHmmss"));
	}

	public String toMMM_dC_yyyy() throws HarnessException {
		return (format("MMM d, yyyy"));
	}

	public Object toMMM_dd_yyyy_A_hCmm_a() throws HarnessException {
		return (format("MMM d, yyyy @ h:mm a"));
	}


	protected String format(String format) throws HarnessException {
		try {
			SimpleDateFormat converter = new SimpleDateFormat(format);
			converter.setTimeZone(calendar.getTimeZone());
			return (converter.format(calendar.getTime()));
		} catch (IllegalArgumentException e) {
			throw new HarnessException("Unable to format date: "+ calendar, e);
		}
	}

	public ZDate addDays(int amount) {
		return (addHours(amount * 24));
	}

	public ZDate addHours(int amount) {
		return (addMinutes(amount * 60));
	}

	public ZDate addMinutes(int amount) {
		return (addSeconds(amount * 60));
	}

	public ZDate addSeconds(int amount) {
		
		// Create the new object to return
		ZDate other = new ZDate(
				this.calendar.get(Calendar.YEAR),
				this.calendar.get(Calendar.MONTH) + 1,
				this.calendar.get(Calendar.DAY_OF_MONTH),
				this.calendar.get(Calendar.HOUR_OF_DAY),
				this.calendar.get(Calendar.MINUTE),
				this.calendar.get(Calendar.SECOND)
			);
		other.calendar.setTimeZone(this.calendar.getTimeZone());
		
		// Adjust it
		other.calendar.add(Calendar.SECOND, amount);
		
		// return it
		return (other);
	}

	@Override
	public String toString() {
		try {
			return (format("MM/dd/yyyy HH:mm:ss z"));
		} catch (HarnessException e) {
			logger.error(e);
			return (calendar.toString());
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZDate other = (ZDate) obj;
		return (toMillis() == other.toMillis());
	}


}