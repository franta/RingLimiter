package net.gajdusek.ringlimiter;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.Contacts.People.Phones;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.app.Activity;
import android.appwidget.AppWidgetManager;

public class RingLimiter extends Activity implements OnClickListener {

	public static int edit = -1;
	public static int type = 0;
	public static FilterData filterdata;
	public static Activity activity;

	// public RingLimiter() {
	// filterdata = FilterData.getInstance(this);
	// }

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);

		tabs.setup();
		TabSpec settingstab = tabs.newTabSpec("settingstab_btn_tab");
		settingstab.setContent(R.id.TableLayout04);
		settingstab.setIndicator(getString(R.string.settings));
		
		TabSpec defaulttab = tabs.newTabSpec("defaulttab_btn_tab");
		defaulttab.setContent(R.id.TableLayout01);
		defaulttab.setIndicator(getString(R.string.def));

		TabSpec viptab = tabs.newTabSpec("viptab_btn_tab");
		viptab.setContent(R.id.TableLayout02);
		viptab.setIndicator(getString(R.string.vip));

		TabSpec contacttab = tabs.newTabSpec("contacttab_btn_tab");
		contacttab.setContent(R.id.TableLayout03);
		contacttab.setIndicator(getString(R.string.contacts));

		tabs.addTab(settingstab);
		tabs.addTab(defaulttab);
		tabs.addTab(viptab);
		tabs.addTab(contacttab);
		tabs.setCurrentTab(0);

		filterdata = FilterData.getInstance(this);
		activity = this;
		findViewById(R.id.Button01).setOnClickListener(this);
		findViewById(R.id.Button02).setOnClickListener(this);
		// inicializace prepinace
		Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mode, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(filterdata.getDefault().getDefaultMode());
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterdata.getDefault().setDefaultMode(position);
				filterdata.apply("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		Spinner spinner2 = (Spinner) findViewById(R.id.Spinner02);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.mode, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setSelection(filterdata.getVip().getDefaultMode());
		spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterdata.getVip().setDefaultMode(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// vypis kontaktu

		String[] projection = new String[] { People._ID, People.NAME, People.NUMBER };

		ListView contactlv = (ListView) findViewById(R.id.ListView03);

		Cursor contactsCursor = this.managedQuery(People.CONTENT_URI, projection, null, null, People.NAME + " ASC");
		// startManagingCursor(contactsCursor);
		contactsCursor.moveToFirst();

		int nameId = contactsCursor.getColumnIndex(People._ID);
		int nameCol = contactsCursor.getColumnIndex(People.NAME);

		do {
			int personId = contactsCursor.getInt(nameId);

			Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI, personId);
			Uri phonesUri = Uri.withAppendedPath(personUri, People.Phones.CONTENT_DIRECTORY);
			String[] proj = new String[] { Phones._ID, Phones.TYPE, Phones.NUMBER, Phones.LABEL };
			Cursor cursor = managedQuery(phonesUri, proj, null, null, null);
			int numCol = contactsCursor.getColumnIndex(People.NUMBER);
			while (cursor.moveToNext()) {
				filterdata.loadContact(contactsCursor.getString(nameCol), cursor.getString(numCol));
			}

			// Do something
		} while (contactsCursor.moveToNext());
		
		ContactAdapter contactadapter = new ContactAdapter(this, filterdata.getContacts());
		contactlv.setAdapter(contactadapter);
		
		CheckBox settings = (CheckBox) findViewById(R.id.CheckBox01);
		settings.setChecked(filterdata.isActive());
		settings.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	      filterdata.setActive(isChecked);
	      RemoteViews view = new RemoteViews("net.gajdusek.ringlimiter", R.layout.widget);
	      if (isChecked) { 
	        filterdata.apply("");
	        view.setImageViewResource(R.id.ImageView01, R.drawable.stop);
	      } else {
	      	view.setImageViewResource(R.id.ImageView01, R.drawable.start);
	      }
	      if(Widget.instance != null) {
	        ComponentName thisWidget = new ComponentName(Widget.instance, Widget.class);
				  AppWidgetManager manager = AppWidgetManager.getInstance(Widget.instance);
				  manager.updateAppWidget(thisWidget, view);
	      }
      }
});

	}

	@Override
	public void onResume() {
		super.onResume();
		edit = -1;
		// vypisuju seznam filtru

		ListView defaultlv = (ListView) findViewById(R.id.ListView01);
		//defaultlv.setId(0);
		writeFilters(filterdata.getDefault(), defaultlv);
		
    defaultlv = (ListView) findViewById(R.id.ListView02);
		//defaultlv.setId(1);
		writeFilters(filterdata.getVip(), defaultlv);
		
		
	}
	
	public void writeFilters(Filters filters, ListView lv) {
		ArrayAdapter<String> list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		lv.setAdapter(list);
		for (int i = 0; i < filters.getFilters().size(); i++) {
			Filter f = filters.getFilters().get(i);
			String title = "";
			title += (f.getId() + 1) + ". " + f.getName();

			if (f.getFromHour() == f.getToHour() && f.getFromMinute() == f.getToMinute()) {
				title += "\n" + getString(R.string.time) + " " + getString(R.string.alltime);
			} else {
				title += "\n" + getString(R.string.time) + " " + f.getFromHour() + ":" + f.getFromMinute() + " - " + f.getToHour() + ":" + f.getToMinute();
			}
			title += "\n" + getString(R.string.weekdays) + ": ";
			if (f.getDays() == 127) {
				title += getString(R.string.everyday);
			} else {
				if ((f.getDays() % 2) == 1) {
					title += getString(R.string.sunday) + ", ";
				}
				if (((f.getDays() / 2) % 2) == 1) {
					title += getString(R.string.monday) + ", ";
				}
				if (((f.getDays() / 4) % 2) == 1) {
					title += getString(R.string.tuesday) + ", ";
				}
				if (((f.getDays() / 8) % 2) == 1) {
					title += getString(R.string.wednesday) + ", ";
				}
				if (((f.getDays() / 16) % 2) == 1) {
					title += getString(R.string.thursday) + ", ";
				}
				if (((f.getDays() / 32) % 2) == 1) {
					title += getString(R.string.friday) + ", ";
				}
				if (((f.getDays() / 64) % 2) == 1) {
					title += getString(R.string.saturday);
				}

			}

			if (f.getCharging() == 1) {
				title += "\n" + getString(R.string.charging);
			} else if (f.getCharging() == 2) {
				title += "\n" + getString(R.string.nocharging);
			}
			if (f.getOrientation() == 1) {
				title += "\n" + getString(R.string.displayup);
			} else if (f.getOrientation() == 2) {
				title += "\n" + getString(R.string.displaydown);
			} else if (f.getOrientation() == 3) {
				title += "\n" + getString(R.string.displayupdown);
			} else if (f.getOrientation() == 4) {
				title += "\n" + getString(R.string.displaynotup);
			} else if (f.getOrientation() == 5) {
				title += "\n" + getString(R.string.displaynotdown);
			} else if (f.getOrientation() == 6) {
				title += "\n" + getString(R.string.displaynotupdown);
			}

			if (f.getCalendar() == 1) {
				title += "\n" + getString(R.string.calendarevent);
			} else if (f.getCalendar() == 2) {
				title += "\n" + getString(R.string.nocalendarevent);
			}

			if (f.getMode() == 1) {
				title += "\n" + getString(R.string.setsillent);
			} else if (f.getMode() == 2) {
				title += "\n" + getString(R.string.setvibrate);
			} else if (f.getMode() == 3) {
				title += "\n" + getString(R.string.setnormal);
			}

			list.add(title);
		}
		registerForContextMenu(lv);
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		int groupid = 0;
		if(v.getId() == R.id.ListView02) {
			groupid = 1;
		}
		menu.add(groupid, 0, 0, getString(R.string.up));
		menu.add(groupid, 1, 0, getString(R.string.down));
		menu.add(groupid, 2, 0, getString(R.string.edit));
		menu.add(groupid, 3, 0, getString(R.string.remove));
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			// nahoru
			if(item.getGroupId() == 0) {
				filterdata.getDefault().swap(info.position - 1);
			} else {
				filterdata.getVip().swap(info.position - 1);
			}
			this.onResume();
			return true;

		case 1:
			// dolu
			if(item.getGroupId() == 0) {
				filterdata.getDefault().swap(info.position);
			} else {
				filterdata.getVip().swap(info.position);
			}
			this.onResume();
			return true;

		case 2:
			// edit
			edit = info.position;
			type = item.getGroupId();
			Intent intent = new Intent(this, NewFilter.class);
			startActivity(intent);
			return true;

		case 3:
			// smazat
			if(item.getGroupId() == 0) {
				filterdata.getDefault().removeFilter(info.position);
			} else {
				filterdata.getVip().removeFilter(info.position);
			}
			this.onResume();
			return true;

		default:
			return super.onContextItemSelected(item);
		}

	}

	@Override
	public void onClick(View v) {
  if(v.getId() == R.id.Button01) {
  	type = 0;
  } else {
  	type = 1;
  }
		Intent i = new Intent(this, NewFilter.class);
		startActivity(i);
	}

}