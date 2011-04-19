package net.gajdusek.ringlimiter;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
//import android.util.Log;

public class Call extends PhoneStateListener {

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);

		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			RingLimiterService.filterdata.setFilter(false);
			RingLimiterService.filterdata.apply(incomingNumber);
			break;

		case TelephonyManager.CALL_STATE_IDLE:
			RingLimiterService.filterdata.setFilter(true);
			RingLimiterService.filterdata.apply("");
			break;

		case TelephonyManager.CALL_STATE_OFFHOOK:
			break;

		}
	}
}
