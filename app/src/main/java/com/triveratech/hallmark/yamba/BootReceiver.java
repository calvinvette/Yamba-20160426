package com.triveratech.hallmark.yamba;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    public static final String TAG = BootReceiver.class.getSimpleName();
    private static final long DEFAULT_INTERVAL = 150000; // Every 2.5 minutes, measured in milliseconds
    // AlarmManager.INTERVAL_FIFTEEN_MINUTES; // 900000
    private static PendingIntent op;
    private static AlarmManager alarmManager;

    public static AlarmManager getAlarmManager(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return BootReceiver.alarmManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setRefreshAlarm(context);
        Log.d(TAG, "Boot Receiver got onReceive");
        //context.startService(new Intent(context, RefreshService.class));
    }

    public static void cancelRefreshAlarm(Context context) {
        getAlarmManager(context).cancel(op);
        Log.d(TAG, "Cancelling Refresh Alarm");
    }

    public static void setRefreshAlarm(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String intervalPref = prefs.getString("interval", "-1");
        long interval = Long.parseLong(intervalPref);
        if (interval < 0) {
            interval = DEFAULT_INTERVAL; // -1 was the default above if it wasn't found
        } else {
            interval = interval * 60 * 1000; // interval in Prefs is in minutes; convert to milliseconds
        }
        op = PendingIntent.getService(context, -1, new Intent(context, RefreshService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        if (interval == 0) {
            cancelRefreshAlarm(context);
        } else {
            getAlarmManager(context).setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, op);
            Log.d(TAG, "Setting repeating Refresh for every " + interval/1000 + " seconds.");
        }
    }

    public static void resetAlarm(Context context) {
        BootReceiver.cancelRefreshAlarm(context);
        BootReceiver.setRefreshAlarm(context);
    }
}
