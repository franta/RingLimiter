package net.gajdusek.ringlimiter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class NewFilter extends Activity implements OnClickListener {

	private FilterData filterdata;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.newfilter);

		filterdata = FilterData.getInstance(this);

		TimePicker time = (TimePicker) this.findViewById(R.id.TimePicker01);
		time.setIs24HourView(Boolean.TRUE);
		time.setCurrentHour(0);
		time.setCurrentMinute(0);

		time = (TimePicker) this.findViewById(R.id.TimePicker02);
		time.setIs24HourView(Boolean.TRUE);
		time.setCurrentHour(0);
		time.setCurrentMinute(0);

		Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chargingfilter, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner = (Spinner) findViewById(R.id.Spinner02);
		adapter = ArrayAdapter.createFromResource(this, R.array.mode, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner = (Spinner) findViewById(R.id.Spinner03);
		adapter = ArrayAdapter.createFromResource(this, R.array.orientation, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner = (Spinner) findViewById(R.id.Spinner04);
		adapter = ArrayAdapter.createFromResource(this, R.array.calendar, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		findViewById(R.id.Button01).setOnClickListener(this);
		if (RingLimiter.edit >= 0) { // nevytvarime novou udalost ale editujeme
			Filter f;
			if (RingLimiter.type == 0) {
				f = filterdata.getDefault().getFilters().get(RingLimiter.edit);
			} else {
				f = filterdata.getVip().getFilters().get(RingLimiter.edit);
			}
			((EditText) findViewById(R.id.EditText01)).setText(f.getName());

			time = (TimePicker) this.findViewById(R.id.TimePicker01);
			time.setCurrentHour(f.getFromHour());
			time.setCurrentMinute(f.getFromMinute());

			time = (TimePicker) this.findViewById(R.id.TimePicker02);
			time.setCurrentHour(f.getToHour());
			time.setCurrentMinute(f.getToMinute());

			spinner = (Spinner) findViewById(R.id.Spinner01);
			spinner.setSelection(f.getCharging());

			spinner = (Spinner) findViewById(R.id.Spinner02);
			spinner.setSelection(f.getMode());

			spinner = (Spinner) findViewById(R.id.Spinner03);
			spinner.setSelection(f.getOrientation());

			spinner = (Spinner) findViewById(R.id.Spinner04);
			spinner.setSelection(f.getCalendar());

			int days = f.getDays();
			if ((days % 2) == 1) {
				((CheckBox) findViewById(R.id.CheckBox01)).setChecked(true);
			} else {
				((CheckBox) findViewById(R.id.CheckBox01)).setChecked(false);
			}

			if (((days / 2) % 2) == 1) {
				((CheckBox) findViewById(R.id.CheckBox02)).setChecked(true);
			} else {
				((CheckBox) findViewById(R.id.CheckBox02)).setChecked(false);
			}

			if (((days / 4) % 2) == 1) {
				((CheckBox) findViewById(R.id.CheckBox03)).setChecked(true);
			} else {
				((CheckBox) findViewById(R.id.CheckBox03)).setChecked(false);
			}

			if (((days / 8) % 2) == 1) {
				((CheckBox) findViewById(R.id.CheckBox04)).setChecked(true);
			} else {
				((CheckBox) findViewById(R.id.CheckBox04)).setChecked(false);
			}

			if (((days / 16) % 2) == 1) {
				((CheckBox) findViewById(R.id.CheckBox05)).setChecked(true);
			} else {
				((CheckBox) findViewById(R.id.CheckBox05)).setChecked(false);
			}

			if (((days / 32) % 2) == 1) {
				((CheckBox) findViewById(R.id.CheckBox06)).setChecked(true);
			} else {
				((CheckBox) findViewById(R.id.CheckBox06)).setChecked(false);
			}

			if (((days / 64) % 2) == 1) {
				((CheckBox) findViewById(R.id.CheckBox07)).setChecked(true);
			} else {
				((CheckBox) findViewById(R.id.CheckBox07)).setChecked(false);
			}

		}
	}

	@Override
	public void onClick(View v) {

		String name = ((EditText) findViewById(R.id.EditText01)).getText().toString();
		int fromHour = ((TimePicker) findViewById(R.id.TimePicker01)).getCurrentHour();
		int fromMinute = ((TimePicker) findViewById(R.id.TimePicker01)).getCurrentMinute();
		int toHour = ((TimePicker) findViewById(R.id.TimePicker02)).getCurrentHour();
		int toMinute = ((TimePicker) findViewById(R.id.TimePicker02)).getCurrentMinute();
		int charging = ((Spinner) findViewById(R.id.Spinner01)).getSelectedItemPosition();
		int mode = ((Spinner) findViewById(R.id.Spinner02)).getSelectedItemPosition();
		int orientation = ((Spinner) findViewById(R.id.Spinner03)).getSelectedItemPosition();
		int calendar = ((Spinner) findViewById(R.id.Spinner04)).getSelectedItemPosition();

		int days = 0;
		if (((CheckBox) findViewById(R.id.CheckBox01)).isChecked())
			days += 1;
		if (((CheckBox) findViewById(R.id.CheckBox02)).isChecked())
			days += 2;
		if (((CheckBox) findViewById(R.id.CheckBox03)).isChecked())
			days += 4;
		if (((CheckBox) findViewById(R.id.CheckBox04)).isChecked())
			days += 8;
		if (((CheckBox) findViewById(R.id.CheckBox05)).isChecked())
			days += 16;
		if (((CheckBox) findViewById(R.id.CheckBox06)).isChecked())
			days += 32;
		if (((CheckBox) findViewById(R.id.CheckBox07)).isChecked())
			days += 64;

		if (RingLimiter.edit < 0) {
			// novy filtr
			if(RingLimiter.type == 0) {
				filterdata.getDefault().addFilter(name, fromHour, fromMinute, toHour, toMinute, days, charging, orientation, calendar, mode);
			} else {
				filterdata.getVip().addFilter(name, fromHour, fromMinute, toHour, toMinute, days, charging, orientation, calendar, mode);
			}

		} else {
			if(RingLimiter.type == 0) {
				filterdata.getDefault().editFilter(RingLimiter.edit, name, fromHour, fromMinute, toHour, toMinute, days, charging, orientation, calendar, mode);
			} else {
				filterdata.getVip().editFilter(RingLimiter.edit, name, fromHour, fromMinute, toHour, toMinute, days, charging, orientation, calendar, mode);
			}
		}
		this.finish();
	}
}
