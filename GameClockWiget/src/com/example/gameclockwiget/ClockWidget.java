package com.example.gameclockwiget;

import java.util.Calendar;
import java.util.Timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

//TODO: Only one instance becouse of static fields

public class ClockWidget extends AppWidgetProvider {
	public static String TAG = "CLOCK WIDGET";

	public static String MINUTE_ADD_ACTION = "addMinute";
	public static String MINUTE_SUB_ACTION = "subMinute";
	public static String MINUTE_RES_ACTION = "resMinute";

	public static boolean alarmSet = false;
	private static Time time = new Time();
	private static Integer minuteOffset = 0;
	private static Timer timer = null;
	public static boolean startCounting = false;
	public static String alarmTime = "test";

	public static int delay = 3;
	public static int timeToStart = delay;

	private AppWidgetManager aWM;
	private int[] ids;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		aWM = appWidgetManager;

		ids = new int[appWidgetIds.length];
		for (int i = 0; i < appWidgetIds.length; i++)
			ids[i] = appWidgetIds[i];

		for (int widgetId : ids) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget);

			updateUI(remoteViews);

			if (timer == null) {

				timer = new Timer();
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, 1);
				cal.set(Calendar.MILLISECOND, 0);

				timer.scheduleAtFixedRate(new Refresher(context, this),
						cal.getTime(), 100);
			}

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

			aWM.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (intent.getAction().equals(MINUTE_SUB_ACTION)) {
			minuteOffset--;
			startCounting = true;
			timeToStart = delay;

		} else if (intent.getAction().equals(MINUTE_ADD_ACTION)) {
			minuteOffset++;
			startCounting = true;
			timeToStart = delay;

		} else if (intent.getAction().equals(MINUTE_RES_ACTION)) {

			alarmSet = false;
			startCounting = false;
			minuteOffset = 0;
			timeToStart = delay;

			deleteAlarm(context);
		}

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		updateUI(remoteViews);
	}

	public void updateUI(RemoteViews remoteViews) {

		time.setToNow();

		time.set((long) (1000 * 60 * minuteOffset) + time.toMillis(false));

		if (timeToStart == 0 || timeToStart == delay)
			remoteViews.setTextViewText(R.id.clock, time.format("%k:%M"));

		if (alarmSet) {
			remoteViews.setViewVisibility(R.id.alarm, View.VISIBLE);
		} else
			remoteViews.setViewVisibility(R.id.alarm, View.INVISIBLE);

		if (alarmSet && timeToStart == 0) {
			remoteViews.setTextViewText(R.id.clock, alarmTime);
			remoteViews.setViewVisibility(R.id.alarm, View.VISIBLE);
			Log.d(TAG, "time to start:" + timeToStart);
		}

		if (null != aWM)
			aWM.updateAppWidget(ids, remoteViews);
	}

	public void updateUIWithContext(Context context) {

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		updateUI(remoteViews);

	}

	public void setAlarm(Context context) {

		try {

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
			alarmTime = time.hour + ":"
					+ ((time.minute > 9) ? time.minute : "0" + time.minute);

		} catch (Exception e) {
			Log.d(TAG, "setAlarm" + e);

		}

	}

	private void deleteAlarm(Context context) {

		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmBR.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		alarmMgr.cancel(pendingIntent);
	}

}