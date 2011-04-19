package net.gajdusek.ringlimiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.BatteryManager;
import android.util.Log;

public class SystemStatus extends BroadcastReceiver implements SensorEventListener {

	private static boolean charging;
	private FilterData filterdata;
	private static SensorEvent accelerometer;

	@Override
	public void onReceive(Context context, Intent intent) {
		filterdata = FilterData.getInstance(context);
		int batteryStatus = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
		if (batteryStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
			charging = false;
		} else {
			charging = true;
		}
		if(RingLimiter.filterdata == null) {
			new RingLimiter();
		}
		filterdata.apply("");
	}

	@Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

	@Override
  public void onSensorChanged(SensorEvent event) {
	  if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
	  	accelerometer = event;
	  }
  }
	
	public static boolean isCharging() {
		return charging;
	}
	
	public static int getOrientation() {
		int i = 0;
		while(accelerometer==null && i < 10000) {
			i++;
		}
		if(accelerometer == null) {
			return 0;
		}
		Log.d("accelerometer", "" + accelerometer.values[0] + " " + accelerometer.values[1] + " " + accelerometer.values[2]);
		int orientation0 = Math.round(accelerometer.values[0]);
  	int orientation1 = Math.round(accelerometer.values[1]);
  	int orientation2 = Math.round(accelerometer.values[2]);
  	if(orientation0 == 0 && orientation1 == 0 && orientation2 == 10 ) {
  		return 1;  //displayem nahoru
  	} else if(orientation0 == 0 && orientation1 == 0 && orientation2 == -10 ) {
  		return 2;  //displayem dolu
  	} else {
  		return 3; //ostatni polohy
  	}
	}

}
