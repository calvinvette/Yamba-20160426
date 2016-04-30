package com.triveratech.hallmark.yamba;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.triveratech.hallmark.yamba.data.StatusContract;

/**
 * A fragment representing a list of Items.
 * <p/>
 *
 */
public class TimelineFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = TimelineFragment.class.getSimpleName();
    private static final String[] FROM = {
            StatusContract.Column.USER,
            StatusContract.Column.MESSAGE,
            StatusContract.Column.CREATED_AT
    };
    private static final int[] TO = {
            R.id.status_list_item_user,
            R.id.status_list_item_message,
            R.id.status_list_item_created_at
    };
    private SimpleCursorAdapter adapter;
    private static final int LOADER_ID = 42;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.status_list_item, null, FROM, TO, 0);
        // Altered to use anonymous inline class rather than one-off private static class
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.status_list_item_created_at:
                        long timeStamp = cursor.getLong(columnIndex);
                        ((TextView) view).setText(DateUtils.getRelativeTimeSpanString(timeStamp));
                        return true;
                    default:
                        return false;
                }
            }
        });
        setListAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        StatusDetailsFragment fragment = (StatusDetailsFragment) getFragmentManager().findFragmentById(R.id.fragment_status_details_wrapper);
        if (fragment != null && fragment.isVisible()) {
            fragment.updateView(id);
        } else {
            startActivity(new Intent(getActivity(), StatusDetailsActivity.class).putExtra(StatusContract.Column.ID, id));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), StatusContract.CONTENT_URI, null, null, null, StatusContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished with cursor: " + data.getCount());
        adapter.swapCursor(data);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //adapter.getCursor().close(); // ??? CV - is this the appropriate place to close the cursor - does it run AFTER the view has been created and the List populated?
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
