package com.triveratech.hallmark.yamba;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 92; // made up a random number
    public static NotificationManager notificationManager;

    public static NotificationManager getNotificationManager(Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public static void setNotificationManager(NotificationManager notificationManager) {
        NotificationReceiver.notificationManager = notificationManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int count = intent.getIntExtra("count", 0);
        PendingIntent op = PendingIntent.getActivity(context, -1, new Intent(context, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification.Builder(context)
                .setContentTitle("New Yamba Tweets!")
                .setContentText("You have " + count + " new tweets.")
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setContentIntent(op)
                .setAutoCancel(true)
                .build();
        getNotificationManager(context).notify(NOTIFICATION_ID, notification);
    }
}
