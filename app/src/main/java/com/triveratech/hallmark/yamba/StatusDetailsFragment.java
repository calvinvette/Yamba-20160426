package com.triveratech.hallmark.yamba;


import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.triveratech.hallmark.yamba.data.StatusContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusDetailsFragment extends Fragment {
    private TextView txtUser, txtMessage, txtCreatedAt, txtLatitude, txtLongitude;


    public StatusDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_details, container, false);
        txtUser = (TextView) view.findViewById(R.id.status_list_item_user);
        txtMessage = (TextView) view.findViewById(R.id.status_list_item_message);
        txtCreatedAt = (TextView) view.findViewById(R.id.status_list_item_created_at);
        txtLatitude = (TextView) view.findViewById(R.id.status_list_item_latitude);
        txtLongitude = (TextView) view.findViewById(R.id.status_list_item_longitude);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        long id = getActivity().getIntent().getLongExtra(StatusContract.Column.ID, -1);
        updateView(id);
    }

    public void updateView(long id) {
        String user = "", message = "", createdAt = "";
        if (id != -1) {
            Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (!cursor.moveToFirst()) {
                return;
            }
            user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
            message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
            createdAt = DateUtils.getRelativeTimeSpanString(cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT))).toString();
        }
        txtUser.setText(user);
        txtMessage.setText(message);
        txtCreatedAt.setText(createdAt);
    }
}
