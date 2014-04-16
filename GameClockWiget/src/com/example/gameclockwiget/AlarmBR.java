package com.example.gameclockwiget;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

//TODO:VIBRATE,sound and notification!

public class AlarmBR extends BroadcastReceiver {
	public final static String TAG = "ALARM BR";

	@Override
	public void onReceive(Context context, Intent intent) {

		Vibrator vibrator;
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(500);

		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Exception e) {
			Log.d(TAG, "ringtone except: " + e);
		}

		Notification nof = new Notification.Builder(context)
				.setContentTitle(context.getString(R.string.notification_title))
				.setContentText(context.getString(R.string.notification_text))
				.setSmallIcon(R.drawable.ic_launcher).build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, nof);
	}
}