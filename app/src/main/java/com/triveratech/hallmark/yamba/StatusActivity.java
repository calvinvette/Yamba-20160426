package com.triveratech.hallmark.yamba;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class StatusActivity extends AppCompatActivity implements StatusFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "FragInt: " + uri.toString(), Toast.LENGTH_SHORT);
    }
}
