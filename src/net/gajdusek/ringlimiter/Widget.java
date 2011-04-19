package net.gajdusek.ringlimiter;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
	
	public static String ACTION_WIDGET_RECEIVER = "net.gajdusek.ringlimiter.ActionReceiverWidget";
	public static FilterData filterdata;
	public static WidgetService instance=null;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		filterdata = FilterData.getInstance(context);
		context.startService(new Intent(context, WidgetService.class));
	}

	public static class WidgetService extends Service {

		public static String ACTION_WIDGET_RECEIVER = "net.gajdusek.ringlimiter.ActionReceiverWidget";

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

		@Override
		public void onStart(Intent intent, int startId) {
			RemoteViews view = new RemoteViews(this.getPackageName(), R.layout.widget);
			Intent toggleIntent = new Intent(this, WidgetService.class);
			toggleIntent.setAction(ACTION_WIDGET_RECEIVER);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, toggleIntent, 0);
			view.setOnClickPendingIntent(R.id.ImageView01, pendingIntent);
			if (intent.getAction() != null && intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {

				if (filterdata.isActive()) {
					filterdata.setActive(false);
				} else {
					filterdata.setActive(true);
					filterdata.apply("");
				}
			}

			if (filterdata.isActive()) {
				
				view.setImageViewResource(R.id.ImageView01, R.drawable.stop);
			} else {
				view.setImageViewResource(R.id.ImageView01, R.drawable.start);
			}
			ComponentName thisWidget = new ComponentName(this, Widget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(thisWidget, view);
			instance = this;
		}

	}

}