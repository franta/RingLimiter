package net.gajdusek.ringlimiter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

public class Filter implements Comparable<Filter> {

	private Context context;
	private int id;
	private String name;
	private int fromHour;
	private int fromMinute;
	private int toHour;
	private int toMinute;
	private int days;
	private int charging;
	private int orientation;
	private int calendar;
	private int mode;
	private int type;

	public Filter(Context cont, int filterid, String filterName, int filterFromHour, int filterFromMinute, int filterToHour, int filterToMinute, int filterDays, int filterCharging ,int filterOrientation, int filterCalendar, int filterMode, int filterType) {
		context=cont;
		id = filterid;
		name = filterName;
		fromHour = filterFromHour;
		fromMinute = filterFromMinute;
		toHour = filterToHour;
		toMinute = filterToMinute;
		days = filterDays;
		charging = filterCharging;
		orientation = filterOrientation;
		calendar = filterCalendar;
		mode = filterMode;
		type = filterType;
	}

	@Override
	public int compareTo(Filter another) {
		Integer hash = this.hashCode();
		if (hash.compareTo(another.hashCode()) != 0) {
			return hash.compareTo(another.id);
		} else {
			return this.name.compareTo(another.name);
		}
	}

	@Override
	public int hashCode() {
		return id;
	}

	public int getId() {
		return id;
	}

	public void setId(int filterid) {
		id = filterid;
	}

	public boolean isMatching() {
		return (isMatchingTime() && isMatchingCharging() && isMatchingOrientation() && isMatchingDays() && isMatchingCalendar());
	}

	private boolean isMatchingTime() {
		GregorianCalendar date = new GregorianCalendar();
		int minutesFrom = fromHour * 60 + fromMinute;
		int minutesTo = toHour * 60 + toMinute;
		int minutes = date.get(Calendar.HOUR_OF_DAY) * 60 + date.get(Calendar.MINUTE);
		if (minutesFrom < minutesTo) { // filtr neni pres pulnoc, stejny cas spadne do kategorie pres pulnoc a vyhovi vzdy
			return (minutes >= minutesFrom && minutes < minutesTo);
		} else {
			return (minutes >= minutesFrom || minutes < minutesTo);
		}
	}
	
	private boolean isMatchingDays() {
		GregorianCalendar date = new GregorianCalendar();
		switch (date.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			return (days % 2) == 1;
		case Calendar.MONDAY:
			return ((days / 2) % 2) == 1;
		case Calendar.TUESDAY:
			return ((days / 4) % 2) == 1;
		case Calendar.WEDNESDAY:
			return ((days / 8) % 2) == 1;
		case Calendar.THURSDAY:
			return ((days / 16) % 2) == 1;
		case Calendar.FRIDAY:
			return ((days / 32) % 2) == 1;
		case Calendar.SATURDAY:
			return ((days / 64) % 2) == 1;
		}
		return false;
	}

	private boolean isMatchingCharging() {
		switch(charging) {
		case 0:
			return true;
		case 1: 
			return SystemStatus.isCharging();
		case 2:
			return !(SystemStatus.isCharging());
		}
    return false;
  }
	
	private boolean isMatchingOrientation() {
		int systemorientation = SystemStatus.getOrientation(); // 0 == nahoru, 1 == dolu, 2 == jinak
		switch(orientation) {
		case 0: //Nezaleží
			return true;
		case 1: //Displayem nahoru
			if(systemorientation == 1) {
				return true;
			} else {
				return false;
			}
		case 2: //Displayem dolů
			if(systemorientation == 2) {
				return true;
			} else {
				return false;
			}
		case 3: //Displayem nahoru nebo dolů
			if(systemorientation == 1 || systemorientation == 2) {
				return true;
			} else {
				return false;
			}
		case 4: //Displayem jinak než nahoru
			if(systemorientation != 1 && systemorientation != 0) {
				return true;
			} else {
				return false;
			}
		case 5: //Displayem jinak než dolů
			if(systemorientation != 2 && systemorientation != 0) {
				return true;
			} else {
				return false;
			}
		case 6: //Displayem jinak než nahoru nebo dolů
			if(systemorientation == 3) {
				return true;
			} else {
				return false;
			}
		}
    return false;
	}
	
	private boolean isMatchingCalendar() {
		if(calendar == 0) {
			return true;
		}
		ContentResolver cr = context.getContentResolver();
		//String[] projection = new String[] { "_id", "dtstart", "dtend" };
		long now = new Date().getTime();
		Uri.Builder builder = Uri.parse("content://calendar/instances/when").buildUpon();
		ContentUris.appendId(builder, now - DateUtils. WEEK_IN_MILLIS*4);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS*4);
		Cursor managedCursor = cr.query(builder.build(),	new String[] { "title", "begin", "end", "allDay"}, null , null, "startDay ASC, startMinute ASC");
		//Cursor managedCursor =  cr.query(Uri.parse("content://calendar/events"), projection,	null, null, null);
		while (managedCursor.moveToNext()) {
			long begin = managedCursor.getLong(1);
			long end = managedCursor.getLong(2);
			if(begin<now && end > now && managedCursor.getInt(3) == 0) {
				managedCursor.close();
				if(calendar == 1) {
				  return true;
				} else {
					return false;
				}
			}
		}
		managedCursor.close();
		if(calendar == 1) {
		  return false;
		} else {
			return true;
		}
	}
	
	public int getMode() {
		return mode;
	}
	
	public String getName() {
		return name;
	}
	
	public int getFromHour() {
		return fromHour;
	}
	
	public int getFromMinute() {
		return fromMinute;
	}
	
	public int getToHour() {
		return toHour;
	}
	
	public int getToMinute() {
		return toMinute;
	}
	
	public int getDays() {
		return days;
	}
	
	public int getCharging() {
		return charging;
	}
	
	public int getOrientation() {
		return orientation;
	}
	public int getCalendar() {
		return calendar;
	}
	
	public int getType() {
		return type;
	}
	
	public void setMode(int filterMode) {
		mode = filterMode;
	}
	
	public void setName(String filterName) {
		name = filterName;
	}
	
	public void setFromHour(int filterFromHour) {
		fromHour = filterFromHour;
	}
	
	public void setFromMinute(int filterFromMinute) {
		fromMinute = filterFromMinute;
	}
	
	public void setToHour(int filterToHour) {
		toHour = filterToHour;
	}
	
	public void setToMinute(int filterToMinute) {
		toMinute = filterToMinute;
	}
	
	public void setDays(int filterDays) {
		days = filterDays;
	}
	
	public void setCharging(int filterCharging) {
		charging = filterCharging;
	}
	
	public void setOrientation(int filterOrientation) {
		orientation = filterOrientation;
	}
	
	public void setCalendar(int filterCalendar) {
		calendar = filterCalendar;
	}
	
	public void setType(int filterType) {
		type = filterType;
	}
}
