package com.triveratech.hallmark.yamba;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusActivity extends AppCompatActivity {
    public static final String TAG = "yamba.StatusActivity";
    private EditText editStatus;
    private Button btnTweet;
    private TextView txtCount;
    private int defaultTxtCountColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        editStatus = (EditText) findViewById(R.id.editStatus);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        txtCount = (TextView) findViewById(R.id.txtCount);
        defaultTxtCountColor = txtCount.getTextColors().getDefaultColor();

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = editStatus.getText().toString();
                Log.d(TAG, "Status to send" + status);
               new PostTask().execute(status);

            }
        });

        editStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = 140 - editStatus.length();
                txtCount.setText(Integer.toString(count));
                if (count < 10) {
                    txtCount.setTextColor(Color.RED);
                } else {
                    txtCount.setTextColor(defaultTxtCountColor);
                }

                if (count <= 0) {
                    btnTweet.setEnabled(false);
                } else {
                    btnTweet.setEnabled(true);
                }
            }
        });

    }

    private final class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String status = params[0];
            String result = "Unknown Result!";
            try {
                YambaClient yamba = new YambaClient("student", "password", "http://yamba.newcircle.com/api");
                yamba.postStatus(status);
                Log.e(TAG, "Status Sent: " + status);
                result = "Status Sent: " + status;
            } catch (YambaClientException e) {
                Log.e(TAG, "Yamba Exception: " + e.getMessage());
                e.printStackTrace();
                result =  "Yamba Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
