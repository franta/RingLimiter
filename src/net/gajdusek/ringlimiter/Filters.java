package net.gajdusek.ringlimiter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
//import android.util.Log;

public class Filters {

	private FilterData filterdata;
	private int id;
	private Context context;
	private int filterCount = 0;
	private ArrayList<Filter> filters = new ArrayList<Filter>();
	private int defaultmode = 0;

	public Filters(FilterData _filterdata, int _id, Context _context) {
		filterdata = _filterdata;
		id = _id;
		context = _context;

		SQLiteDatabase db = filterdata.getReadableDatabase();
		String[] coulomns = { "id", "name", "fromHour", "fromMinute", "toHour", "toMinute", "days", "charging", "orientation", "calendar", "mode" };
		Cursor cursor = db.query("filter", coulomns, "type = " + id, null, null, null, "id ASC");
		while (cursor.moveToNext()) {
			Filter f = new Filter(context, cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7), cursor
			    .getInt(8), cursor.getInt(9), cursor.getInt(10), id);
			filters.add(f);
			filterCount++;
		}
		cursor.close();

		String[] defaultmod = { "mode", };
		cursor = db.query("settings", defaultmod, "id = " + id, null, null, null, "id ASC");
		if (cursor.moveToNext()) {
			defaultmode = cursor.getInt(0);
		}
		cursor.close();
	}
	
	public ArrayList<Filter> getFilters() {
		return filters;
	}

	public int getDefaultMode() {
		return defaultmode;
	}

	public void setDefaultMode(int mode) {
		defaultmode = mode;
		SQLiteDatabase db = filterdata.getWritableDatabase();
		db.delete("settings", "id = " + id, null);
		db.execSQL("INSERT INTO settings VALUES ( " + id + ", " + defaultmode + " );");
		db.close();
		//applyFilters();
	}

	private void saveToDB(Filter f) {
		SQLiteDatabase db = filterdata.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", f.getId());
		values.put("name", f.getName());
		values.put("fromHour", f.getFromHour());
		values.put("fromMinute", f.getFromMinute());
		values.put("toHour", f.getToHour());
		values.put("toMinute", f.getToMinute());
		values.put("days", f.getDays());
		values.put("charging", f.getCharging());
		values.put("orientation", f.getOrientation());
		values.put("calendar", f.getCalendar());
		values.put("mode", f.getMode());
		values.put("type", id);
		db.insertOrThrow("filter", null, values);
		db.close();
	}

	private void removeFromDB(int position) {
		SQLiteDatabase db = filterdata.getWritableDatabase();
		db.execSQL("DELETE FROM filter WHERE id = " + position + " AND type = " + id + ";");
		db.close();
	}

	public void addFilter(String name, int fromHour, int fromMinute, int toHour, int toMinute, int days, int charging, int orientation, int calendar, int mode) {
		Filter f = new Filter(context, filterCount, name, fromHour, fromMinute, toHour, toMinute, days, charging, orientation, calendar, mode, id);
		filters.add(f);
		saveToDB(f);
		filterCount++;
		//applyFilters();
	}

	public void editFilter(int _id, String name, int fromHour, int fromMinute, int toHour, int toMinute, int days, int charging, int orientation, int calendar, int mode) {
		removeFromDB(_id);

		Filter f = filters.get(_id);
		f.setName(name);
		f.setFromHour(fromHour);
		f.setFromMinute(fromMinute);
		f.setToHour(toHour);
		f.setToMinute(toMinute);
		f.setDays(days);
		f.setCharging(charging);
		f.setOrientation(orientation);
		f.setCalendar(calendar);
		f.setMode(mode);
		saveToDB(f);
		//applyFilters();
	}
	
	public void swap(int position) {
		if (position >= 0 && (position + 1) < filterCount) {
			removeFromDB(position);
			removeFromDB(position + 1);
			Filter f = filters.get(position);
			f.setId(position+1);
			
			filters.set(position, filters.get(position+1) );
			filters.get(position).setId(position);
			filters.set(position+1, f);
			saveToDB(filters.get(position));
			saveToDB(filters.get(position + 1));
		}
		//applyFilters();
	}
	
	public void removeFilter(int position) {
		removeFromDB(position);
		filters.remove(position);
		filterCount--;
		
		while(position < filterCount) {
			filters.get(position).setId(position);
			position++;
		}
		//applyFilters();
	}
	
	public void applyFilters() {
		boolean apply = false;
		
		for(int i = 0; i < filterCount; i++) {
			Filter f = filters.get(i);
			if (f.isMatching()) {
				AudioManager m = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				if (f.getMode() == 0) {
					// nedelat nic
				} else if (f.getMode() == 1) {
					m.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				} else if (f.getMode() == 2) {
					m.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				} else if (f.getMode() == 3) {
					m.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}
				apply = true;
				break;
			}
		}
		if (!apply) {
			AudioManager m = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (getDefaultMode() == 0) {
				// nedelat nic
			} else if (getDefaultMode() == 1) {
				m.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			} else if (getDefaultMode() == 2) {
				m.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			} else if (getDefaultMode() == 3) {
				m.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
		}
	}

}
