package com.triveratech.hallmark.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;
import com.thenewcircle.yamba.client.YambaClientInterface;
import com.thenewcircle.yamba.client.YambaStatus;
import com.triveratech.hallmark.yamba.data.DbHelper;
import com.triveratech.hallmark.yamba.data.StatusContract;

import java.io.File;
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
    private String serverUrl = "http://yamba.newcircle.com/api"; // the old value is wrong - change in settings page! 

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent Delivered: " + intent);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userId = prefs.getString("userName", userId);
        password = prefs.getString("password", password);
        serverUrl = prefs.getString("serverUrl", serverUrl);

        // TODO - is this executing after the thread for the IntentService dies???
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Update your userName and password in Settings!", Toast.LENGTH_LONG).show();
            return;
        }

        File cwd;
        //cwd = new File("."); // Current Working Directory (CWD), defaults to "/." in modern Android
//        cwd = getFilesDir(); // /data/data/mypackage/files
        // NEED android.permission.READ_EXTERNAL_STORAGE
         cwd = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Environment.DIRECTORY_*

        Log.d(TAG, "CWD: " + cwd.getAbsolutePath());
        File[] fileList = cwd.listFiles();
        for (File f : fileList) {
            if (f.isFile()) {
                Log.d(TAG, f.getPath());
            }
        }

        YambaClientInterface yambaClient = YambaClient.getClient(userId, password, serverUrl);
        try {
            List<YambaStatus> timeLine = yambaClient.getTimeline(20);

            DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            int count = 0;

            for (YambaStatus s: timeLine) {
                Log.d(TAG, s.getUser() + "@" + s.getCreatedAt() + ": " + s.getMessage());
                values.clear();
                values.put(StatusContract.Column.ID, s.getId());
                values.put(StatusContract.Column.USER, s.getUser());
                values.put(StatusContract.Column.MESSAGE, s.getMessage());
                values.put(StatusContract.Column.CREATED_AT, s.getCreatedAt().getTime());

                // The YambaClient jar does not currently support latitude and longitude
                // It's in the return feed from the server, but not supported by the 2.1.0 jar file yet.
                // Update the build.gradle file for the app when they do with the new version number
                // and this code should work, along with storing in the database.
                //values.put(StatusContract.Column.LATITUDE, s.getLatitude());
                //values.put(StatusContract.Column.LONGITUDE, s.getLongitude());
                // Disable these two lines of code then:
                values.put(StatusContract.Column.LATITUDE, 42.5);
                values.put(StatusContract.Column.LONGITUDE, -83.285);

                // We're now using the Provider - don't use the db code directly
                // db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
                if (uri == null) {
                    Log.d(TAG, "Failed to insert with Content Resolver! " + uri);
                    Log.d(TAG, "ContentResolver inserted: " + uri);
                }
                long id = Long.parseLong(uri.getLastPathSegment());
                Log.d(TAG, "URI-based ID: " + id);
                if (id >= 0) {
                    count++;
                }
            }

            if (count > 0) {
                sendBroadcast(new Intent("com.triveratech.hallmark.yamba.NEW_STATUSES").putExtra("count", count));
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
//            if (ACTION_FOO.equals(action)) {w
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
