package net.gajdusek.ringlimiter;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;


public class SMS extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
		smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}
		RingLimiterService.filterdata.setFilter(false);
		RingLimiterService.filterdata.apply(smsMessage[0].getOriginatingAddress());
		
		Log.d("sms", smsMessage[0].getOriginatingAddress());
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				RingLimiterService.filterdata.apply("");
				RingLimiterService.filterdata.setFilter(true);
			}

		}, 10000);
			  
	}

}
