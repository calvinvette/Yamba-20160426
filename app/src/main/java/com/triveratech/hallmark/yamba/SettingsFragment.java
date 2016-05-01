package com.triveratech.hallmark.yamba;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment
        // implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = SettingsFragment.class.getSimpleName();
    // private SharedPreferences prefs;

    public SettingsFragment() {
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }


    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Settings Destroyed");
        BootReceiver.resetAlarm(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}
