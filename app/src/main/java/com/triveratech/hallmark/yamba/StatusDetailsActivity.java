package com.triveratech.hallmark.yamba;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by calvin on 4/29/16.
 */
public class StatusDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            StatusDetailsFragment fragment = new StatusDetailsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }
}
