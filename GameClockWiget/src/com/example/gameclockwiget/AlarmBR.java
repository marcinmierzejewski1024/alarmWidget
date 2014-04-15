package com.example.gameclockwiget;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

//TODO:VIBRATE,sound and notification!

public class AlarmBR extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Vibrator vibrator;
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
		
		Notification nof = new Notification.Builder(context)
				.setContentTitle("New mail from " + "test@gmail.com")
				.setContentText("Subject").setSmallIcon(R.drawable.ic_launcher).build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, nof);
	}
}