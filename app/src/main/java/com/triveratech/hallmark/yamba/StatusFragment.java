package com.triveratech.hallmark.yamba;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MESSAGE = "Message"; // Key name in bundle
    private static final String ARG_ADD_LOCATION = "SendLocation"; // Key name in bundle

    public static final String TAG = "yamba.StatusFragment";
    private EditText editStatus;
    private Button btnTweet;
    private TextView txtCount;
    private int defaultTxtCountColor;


    // TODO: Rename and change types of parameters
    private String message;
    private Boolean sendLocation;

    private String userId = "student";
    private String password = "password";
    private String serverUrl = "http://yamba.marakana.com/api"; // the old value is wrong - change in settings page!

    private boolean allowPostingGeoLocation = false; // Default to false for safety reasons - user has to explicitly enable this in prefs
    private double latitude = 42.5, longitude = -83.285;

    private OnFragmentInteractionListener mListener;


    private final class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            allowPostingGeoLocation = prefs.getBoolean("allowPostingGeoLocation", false);
            String status = params[0];
            String result = "Unknown Result!";
            try {
                YambaClient yamba = new YambaClient(userId, password, serverUrl);
                if (allowPostingGeoLocation) {
                    yamba.postStatus(status, latitude, longitude);
                } else {
                    yamba.postStatus(status);
                }
                Log.e(TAG, "Status Sent: " + status);
                result = "Status Sent: " + status;
            } catch (YambaClientException e) {
                Log.e(TAG, "Yamba Exception: " + e.getMessage());
                e.printStackTrace();
                result = "Yamba Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusFragment.this.getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }


    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message The message we'll be posting to the microblog site.
     * @param sendLocation Should we also send geolocation data to the microblog site? Yes if Boolean.TRUE.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String message, Boolean sendLocation) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_ADD_LOCATION, sendLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Used to restore the fragment later after it was destroyed
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE);
            sendLocation = getArguments().getBoolean(ARG_ADD_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        editStatus = (EditText) view.findViewById(R.id.editStatus);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        txtCount = (TextView) view.findViewById(R.id.txtCount);
        defaultTxtCountColor = txtCount.getTextColors().getDefaultColor();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userId = prefs.getString("userName", userId);
        password = prefs.getString("password", password);
        serverUrl = prefs.getString("serverUrl", serverUrl);

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

                if (count < 0) {
                    btnTweet.setEnabled(false);
                } else {
                    btnTweet.setEnabled(true);
                }
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
