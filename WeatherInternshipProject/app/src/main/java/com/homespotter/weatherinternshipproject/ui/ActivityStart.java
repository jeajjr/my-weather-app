package com.homespotter.weatherinternshipproject.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.FilesHandler;

public class ActivityStart extends ActionBarActivity {
    private static final String TAG ="ActivityStart";
    //public static final String

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FilesHandler.getInstance().setCityName(getApplicationContext(), "syracuse, ny");

        // If no file directory was found, a new city must be added. Otherwise, open
        // forecast of main city.
        String cityName = FilesHandler.getInstance().getSavedCity(getApplicationContext());
        if (cityName == null) {
            Log.d(TAG, "no city saved");
            // intent to find city
        }
        else {
            Log.d(TAG, "city saved:" + cityName);

            Intent intent = new Intent(this, ActivityMain.class);
            intent.putExtra("cityName", cityName);
            startActivity(intent);
        }
    }
}
