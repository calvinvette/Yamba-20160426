package com.triveratech.hallmark.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RefreshService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
//    public static final String ACTION_FOO = "com.triveratech.hallmark.yamba.action.FOO";
//    public static final String ACTION_BAZ = "com.triveratech.hallmark.yamba.action.BAZ";
//
//    // TODO: Rename parameters
//    public static final String EXTRA_PARAM1 = "com.triveratech.hallmark.yamba.extra.PARAM1";
//    public static final String EXTRA_PARAM2 = "com.triveratech.hallmark.yamba.extra.PARAM2";

    public static final String TAG = "RefreshService";

    public RefreshService() {
        super(TAG);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // TODO - Refactor this out!
    private String userId = "student";
    private String password = "password";
    private String serverUrl = "http://yamba.marakana.com/api"; // the old value is wrong - change in settings page! 

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent Delivered: " + intent);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userId = prefs.getString("userName", userId);
        password = prefs.getString("password", password);
        serverUrl = prefs.getString("serverUrl", serverUrl);

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Update your userName and password in Settings!", Toast.LENGTH_LONG).show();
            return;
        }

        YambaClient yambaClient = new YambaClient(userId, password, serverUrl);
        try {
            List<YambaClient.Status> timeLine = yambaClient.getTimeline(20);
            for (YambaClient.Status s: timeLine) {
                Log.d(TAG, s.getUser() + ": " + s.getMessage());
            }
        } catch (YambaClientException e) {
            e.printStackTrace();
        }

        return;
    }


    //    @Override
//    protected void onHandleIntent(Intent intent) {
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
//        }
//    }
//
//    /**
//     * Handle action Foo in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionFoo(String param1, String param2) {
//        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}
