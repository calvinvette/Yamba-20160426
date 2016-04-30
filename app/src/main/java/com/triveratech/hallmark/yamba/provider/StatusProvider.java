package com.triveratech.hallmark.yamba.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.triveratech.hallmark.yamba.data.DbHelper;
import com.triveratech.hallmark.yamba.data.StatusContract;

public class StatusProvider extends ContentProvider {
    private static final String TAG = StatusProvider.class.getSimpleName();
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR);          // content://com.aqwekjksdfsjkdhf.yamba/status
        matcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#", StatusContract.STATUS_ITEM);  // content://com.aqwekjksdfsjkdhf.yamba/status/1314
    }

    private DbHelper helper;
    private SQLiteDatabase db;

    public StatusProvider() {
    }

    public SQLiteDatabase getDb() {
        return helper.getWritableDatabase();
    }

    public SQLiteDatabase getRODB() {
        return helper.getReadableDatabase();
    }

    @Override
    public boolean onCreate() {
        helper = new DbHelper(getContext());
        Log.d(TAG, "onCreate, dbHelper assigned");
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                return StatusContract.STATUS_TYPE_DIR;
            case StatusContract.STATUS_ITEM:
                return StatusContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal Content URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;
        switch(matcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                where = (selection == null) ? "1" : selection;
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID + "=" + id + (TextUtils.isEmpty(selection) ? "" : " and (" + selection + ")");
                break;
            default:
                throw new IllegalArgumentException("Illegal Content URI: " + uri);
        }

        int ret = getDb().delete(StatusContract.TABLE, where, selectionArgs);
        return ret;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;
        if (matcher.match(uri) != StatusContract.STATUS_DIR) {
            throw new IllegalArgumentException("Illegal URI: " + uri);
        }
        long rowId = getDb().insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        long id = values.getAsLong(StatusContract.Column.ID);
        ret = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(uri, null);
        if (rowId != -1) { // Record wasn't inserted, probably because we already had a copy of it

        } else {
            Log.d(TAG, "Failed to insert: " + ret);
            //throw new IllegalArgumentException("Not inserted: " + values);
        }
        return ret;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(StatusContract.TABLE);
        switch(matcher.match(uri)) {
            case StatusContract.STATUS_DIR: break;
            case StatusContract.STATUS_ITEM:
                qb.appendWhere(StatusContract.Column.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        String orderBy = (TextUtils.isEmpty(sortOrder)) ? StatusContract.DEFAULT_SORT : sortOrder;
        Cursor cursor = qb.query(getRODB(), projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
