package com.homespotter.weatherinternshipproject.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.FilesHandler;
import com.homespotter.weatherinternshipproject.data.WeatherClient;

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

        //TODO: remove
//        FilesHandler.getInstance().removeSavedCities(this);

        ArrayList<String> cities = new ArrayList<>();
        cities.add("Syracuse, NY");
        cities.add("Sao Carlos, Brazil");
        cities.add("NYC");

        FilesHandler.getInstance().setCityList(this, cities);
        callMainActivity();

        /*
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
        */
    }
}
