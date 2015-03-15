package com.homespotter.weatherinternshipproject.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.FilesHandler;

import java.util.ArrayList;

public class ActivityStart extends ActionBarActivity implements DialogFragmentSearchCity.DialogFragmentSearchCityResultListener {
    private static final String TAG ="ActivityStart";

    ArrayList<String> cityList;
    ProgressDialog progressDialog;
    View findCityDialogView;

    @Override
    public void onComplete() {
        Log.d(TAG, "DialogFragment completed");
        callMainActivity();
    }

    public void callMainActivity() {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // show progress dialog while data is being fetched
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        // If no file directory was found, a new city must be added. Otherwise, open
        // forecast of main city.
        String cityName = FilesHandler.getInstance().getFirstSavedCity(this);

        if (cityName == null) {
            Log.d(TAG, "no city saved");
            DialogFragmentSearchCity.newInstance(true).show(getSupportFragmentManager(), null);
        }
        else {
            Log.d(TAG, "city saved:" + cityName);
            callMainActivity();
        }
    }
}
