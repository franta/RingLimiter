package net.gajdusek.ringlimiter;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Contact {

	private String name;
	private String phone;
	private int mode;
	
	public Contact(String _name, String _phone, int _mode) {
		name = _name;
		phone = _phone;
		mode = _mode;
	}
	
	public void setName(String _name) {
		name = _name;
	}
	
	public void setPhone(String _phone) {
		phone = _phone;
	}
	
	public void setMode(int _mode) {
		mode = _mode;
		FilterData filterdata = FilterData.getInstance(null);
		SQLiteDatabase db = filterdata.getWritableDatabase();
		db.execSQL("DELETE FROM contacts WHERE phone = \"" + phone + "\";");
		if(mode != 0) {
			ContentValues values = new ContentValues();
			values.put("phone", phone);
			values.put("mode", mode);
			db.insertOrThrow("contacts", null, values);
		}
		db.close();
	}
	
	public String getName() {
		return name;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public int getMode() {
		return mode;
	}

}
