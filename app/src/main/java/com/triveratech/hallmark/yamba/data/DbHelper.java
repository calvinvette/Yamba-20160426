package com.triveratech.hallmark.yamba.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by calvin on 4/28/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private SQLiteDatabase db;

    public SQLiteDatabase getDb() {
        return db;
    }

    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s ("
                + "%s int primary key, "    // Yamba message ID
                + "%s text, "               // User
                + "%s text, "               // Message
                + "%s int, "                // Created_At
                + "%s real, "               // latitude
                + "%s real"               // longitude
            + ")",
                StatusContract.TABLE,
                StatusContract.Column.ID,
                StatusContract.Column.USER,
                StatusContract.Column.MESSAGE,
                StatusContract.Column.CREATED_AT,
                StatusContract.Column.LATITUDE,
                StatusContract.Column.LONGITUDE
        );
        Log.d(TAG, "Timeline DB Create Table for Status: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + StatusContract.TABLE);
        onCreate(db);
    }

    // TODO - provide DAO interface:
    /*
    public YambaClient.Status insert(YambaClient.Status status) {
        //getDb().insert(StatusContract.TABLE, null, statusAsValues);
        return null;
    }

    public YambaClient.Status delete(YambaClient.Status status) {
        return null;
    }

    public YambaClient.Status update(YambaClient.Status status) {
        return null;
    }

    public YambaClient.Status findById(int id) {
        return null;
    }

    public List<YambaClient.Status> findAll() {
        return null;
    }

    public List<YambaClient.Status> findByUser(String user) {
        return null;
    }
    */




}
