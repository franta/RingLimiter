package net.gajdusek.ringlimiter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ContactAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Contact> contacts;
	
	public ContactAdapter(Context context, ArrayList<Contact> listContacts){
		this.context = context;
		this.contacts = listContacts;
	}
	
	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Contact entry = contacts.get(position);
		return new ContactAdapterView(context,entry);
	}

}
