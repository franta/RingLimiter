package net.gajdusek.ringlimiter;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
//import android.util.Log;

public class FilterData extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ringlimiter.db";
	private static final int DATABASE_VERSION = 6;

	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private static FilterData instance = null;
	private Context context;

	private Filters standard;
	private Filters vip;
	private boolean filter = true;
	private boolean active = true;

	private FilterData(Context _context) {
		super(_context, DATABASE_NAME, null, DATABASE_VERSION);
		SQLiteDatabase db = getReadableDatabase();
		String[] activemod = { "active", };
		Cursor cursor = db.query("active", activemod, "id = 0", null, null, null, "id ASC");
		if (cursor.moveToNext()) {
			if (cursor.getInt(0) == 0) {
				active = false;
			}
		} else {
			setActive(true);
		}
		cursor.close();
		context = _context;
	}

	public static FilterData getInstance(Context _context) {
		if (instance == null) {
			instance = new FilterData(_context);
			instance.standard = new Filters(instance, 0, instance.context);
			instance.vip = new Filters(instance, 1, instance.context);
			instance.context.startService(new Intent(_context, RingLimiterService.class));
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS filter");
		db.execSQL("DROP TABLE IF EXISTS contacts");
		db.execSQL("DROP TABLE IF EXISTS settings");
		db.execSQL("DROP TABLE IF EXISTS active");
		db
		    .execSQL("CREATE TABLE filter ( id INTEGER,  name TEXT, fromHour INTEGER, fromMinute INTEGER, toHour INTEGER, toMinute INTEGER, days INTEGER, charging INTEGER, orientation INTEGER, calendar INTEGER, mode INTEGER, type INTEGER);");
		db.execSQL("CREATE TABLE  contacts ( phone TEXT, mode INTEGER);");
		// db.execSQL("INSERT INTO filter VALUES ( 0, 'jmeno', 0, 0, 0, 0, 0, 0)");
		db.execSQL("CREATE TABLE settings ( id INTEGER PRIMARY KEY, mode INTEGER );");
		db.execSQL("CREATE TABLE active ( id INTEGER PRIMARY KEY, active INTEGER );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

	public Filters getDefault() {
		return standard;
	}

	public Filters getVip() {
		return vip;
	}

	public boolean isFilter() {
		return filter;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean _active) {
		active = _active;
		int activeint = 0;
		if (active) {
			activeint = 1;
		}

		SQLiteDatabase db = getWritableDatabase();
		db.delete("active", "id = 0", null);
		db.execSQL("INSERT INTO active VALUES ( 0, " + activeint + " );");
		db.close();
	}

	public void setFilter(boolean _filter) {
		filter = _filter;
	}

	public void setSilent() {
		AudioManager m = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		m.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}

	public void setVibrate() {
		AudioManager m = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		m.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}

	public void setNormal() {
		AudioManager m = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		m.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}

	public void apply(String phone) {
		if (active) {
			if (phone == "") {
				standard.applyFilters();
			} else {
				int mode = 0;
				SQLiteDatabase db = getReadableDatabase();
				String[] coulomns = { "mode" };
				Cursor cursor = db.query("contacts", coulomns, "phone = \"" + phone + "\"", null, null, null, null);
				if (cursor.moveToNext()) {
					mode = cursor.getInt(0);
				}
				switch (mode) {

				case 0:
					standard.applyFilters();
					break;

				case 1:
					vip.applyFilters();
					break;

				case 2:
					setSilent();
					break;

				case 3:
					setVibrate();
					break;

				case 4:
					setNormal();
					break;
				}

			}
		}
	}

	public void loadContact(String name, String phone) {
		int mode = 0;
		SQLiteDatabase db = getReadableDatabase();
		String[] coulomns = { "mode" };
		Cursor cursor = db.query("contacts", coulomns, "phone = \"" + phone + "\"", null, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		Contact c = new Contact(name, phone, mode);
		contacts.add(c);
		cursor.close();
	}

	public ArrayList<Contact> getContacts() {
		return contacts;
	}

}
