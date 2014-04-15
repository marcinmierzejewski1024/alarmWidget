package com.example.gameclockwiget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

//TODO: each minute refresh widget if no alarm set
//TODO: delete alarm

public class ClockWidget extends AppWidgetProvider {

	public static String MINUTE_ADD_ACTION = "addHour";
	public static String MINUTE_SUB_ACTION = "subHour";
	public static String MINUTE_RES_ACTION = "resHour";

	private static boolean alarmSet = false;
	private static Time time = new Time();
	private static Integer minuteOffset = 0;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		ComponentName thisWidget = new ComponentName(context, ClockWidget.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		for (int widgetId : allWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget);

			updateUI(remoteViews);

			Intent hourAddIntent = new Intent(context, ClockWidget.class);
			hourAddIntent.setAction(MINUTE_ADD_ACTION);
			PendingIntent pi1 = PendingIntent.getBroadcast(context, 0,
					hourAddIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.timeForward, pi1);

			Intent hourSubIntent = new Intent(context, ClockWidget.class);
			hourSubIntent.setAction(MINUTE_SUB_ACTION);
			PendingIntent pi2 = PendingIntent.getBroadcast(context, 0,
					hourSubIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.timeBack, pi2);

			Intent hourResIntent = new Intent(context, ClockWidget.class);
			hourResIntent.setAction(MINUTE_RES_ACTION);
			PendingIntent pi3 = PendingIntent.getBroadcast(context, 0,
					hourResIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.timeRestore, pi3);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (intent.getAction().equals(MINUTE_SUB_ACTION)) {
			minuteOffset--;
			alarmSet = true;
			setAlarm(context);

		} else if (intent.getAction().equals(MINUTE_ADD_ACTION)) {
			minuteOffset++;
			alarmSet = true;
			setAlarm(context);

		} else if (intent.getAction().equals(MINUTE_RES_ACTION)) {

			alarmSet = false;
			minuteOffset = 0;
			deleteAlarm(context);
		}

		// refresh widget
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		ComponentName thisWidget = new ComponentName(context, ClockWidget.class);
		updateUI(remoteViews);
		AppWidgetManager.getInstance(context).updateAppWidget(thisWidget,
				remoteViews);
	}

	private void updateUI(RemoteViews remoteViews) {

		time.setToNow();
		time.set((long) (1000 * 60 * minuteOffset) + time.toMillis(false));

		remoteViews.setTextViewText(R.id.clock, time.format("%k:%M"));

		if (alarmSet) {
			remoteViews.setViewVisibility(R.id.alarm, View.VISIBLE);
			remoteViews.setTextViewText(R.id.clock, time.format("%k:%M"));
		} else
			remoteViews.setViewVisibility(R.id.alarm, View.INVISIBLE);
	}

	private void setAlarm(Context context) {

		try {
			Toast.makeText(context, "kontekst", Toast.LENGTH_LONG).show();

			Calendar cal = Calendar.getInstance();

			cal.setTimeInMillis(System.currentTimeMillis());

			cal.set(Calendar.HOUR_OF_DAY, time.hour);
			cal.set(Calendar.MINUTE, time.minute);
			cal.set(Calendar.SECOND, 0);

			AlarmManager alarmMgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, AlarmBR.class);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					context.getApplicationContext(), 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
					cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					pendingIntent);

		} catch (Exception e) {
			Toast.makeText(context, "???" + e, Toast.LENGTH_LONG).show();

		}

	}

	private void deleteAlarm(Context context) {
		// TODO Auto-generated method stub

	}

}