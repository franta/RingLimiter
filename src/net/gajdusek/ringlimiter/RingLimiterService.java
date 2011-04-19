package net.gajdusek.ringlimiter;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class RingLimiterService extends Service {

	public static FilterData filterdata;
	private boolean registered = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (!registered) {
			SystemStatus systemstatus = new SystemStatus();
			registerReceiver(systemstatus, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			
			SensorManager sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

			Sensor sensor_orientation = sensor_manager.getSensorList(SensorManager.SENSOR_ORIENTATION).get(0);
			sensor_manager.registerListener(systemstatus, sensor_orientation, SensorManager.SENSOR_DELAY_NORMAL);
			
			Call call = new Call();
			((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(call, PhoneStateListener.LISTEN_CALL_STATE);
			filterdata = FilterData.getInstance(this);
			registered = true;

		}
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				if(filterdata.isFilter()) {
				  RingLimiter.filterdata.apply("");
				}
			}

		}, 10000, 60000);

	}

}