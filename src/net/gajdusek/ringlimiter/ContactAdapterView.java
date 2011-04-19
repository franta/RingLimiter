package net.gajdusek.ringlimiter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

public class ContactAdapterView extends TableLayout {
	
	private Contact entry;

	public ContactAdapterView(Context context, Contact _entry) {
	  super(context);
	  entry = _entry;
	  View v = inflate(context, R.layout.row, null);
	  TextView tvContact = (TextView)v.findViewById(R.id.TextView01);
		tvContact.setText(entry.getName() + ": " + entry.getPhone());
		addView(v);
		
		Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.contactmode, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(entry.getMode());
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				entry.setMode(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
  }

}
