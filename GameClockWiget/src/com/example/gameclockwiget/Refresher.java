package com.example.gameclockwiget;

import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;

public class Refresher extends TimerTask {
	static final String TAG = "REFRESHER";
	

	AppWidgetManager appWidgetManager;
	ClockWidget parent;
	Context context;

	public Refresher(Context context, ClockWidget parent) {
		this.parent = parent;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			parent.updateUIWithContext(context);

			if (parent.timeToStart > 0 && parent.startCounting)
				parent.timeToStart--;
			else if (parent.timeToStart == 0) {
				parent.alarmSet = true;
				parent.setAlarm(context);
				parent.startCounting=false;
			}
		} catch (Exception e) {

			Log.d(TAG, "run" + e);
		}
	}
}