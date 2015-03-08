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

public class ActivityStart extends ActionBarActivity implements DialogFragmentSearchCity.DialogFragmentSearchCityResultListener{
    private static final String TAG ="ActivityStart";

    ArrayList<String> cityList;
    ProgressDialog progressDialog;
    View findCityDialogView;

    private void createFindCityDialog() {
        Log.d(TAG, "createFindCityDialog");

        findCityDialogView = getLayoutInflater().inflate(R.layout.layout_city_search_dialog, null);

        final EditText cityName = (EditText) findCityDialogView.findViewById(R.id.editTextDialog);

        final ImageView searchButton = (ImageView) findCityDialogView.findViewById(R.id.imageViewDialogSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityName.getText().toString().length() != 0) {
                    searchCity(cityName.getText().toString());
                }
                else
                    Toast.makeText(ActivityStart.this, getString(R.string.start_dialog_city_empty), Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(findCityDialogView)
                .setTitle(getString(R.string.start_dialog_title))
                .setMessage(getString(R.string.start_dialog_message))
                .setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void searchCity(final String city) {
        progressDialog.show();

        if (checkInternetAccess()) {
            new Thread() {
                public void run() {
                    Log.d(TAG, "Searching city: " + city);

                    String data = WeatherClient.getInstance().findCity(city);

                    cityList = DataParser.parseCitySearchResults(data);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            citySearchDone(city);
                        }
                    });

                }
            }.start();
        }
        else
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
    }

    private void citySearchDone(final String citySearched) {
        if (cityList == null) {
            Toast.makeText(this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
        }
        else if (cityList.size() == 0) {
            Toast.makeText(this, getString(R.string.warning_city_not_found_with_name) + citySearched, Toast.LENGTH_LONG).show();
        }
        else if (cityList.size() == 1) {
            // Save the city and open its weather forecast
            FilesHandler.getInstance().createCityList(getApplicationContext(), cityList.get(0));
            callMainActivity();
        }
        else {
            TextView listTitle = (TextView) findCityDialogView.findViewById(R.id.textViewDialogListTitle);
            ListView listView = (ListView) findCityDialogView.findViewById(R.id.listViewDialog);

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, cityList);
            listView.setAdapter(adapter);

            listView.setVisibility(View.VISIBLE);
            listTitle.setVisibility(View.VISIBLE);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Save selected city and open its weather forecast
                    FilesHandler.getInstance().createCityList(getApplicationContext(), cityList.get(position));
                    callMainActivity();
                }
            });

            findCityDialogView.invalidate();
        }
    }

    private boolean checkInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
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


        // If no file directory was found, a new city must be added. Otherwise, open
        // forecast of main city.
        String cityName = FilesHandler.getInstance().getFirstSavedCity(this);

        if (cityName == null) {
            Log.d(TAG, "no city saved");
            createFindCityDialog();
        }
        else {
            Log.d(TAG, "city saved:" + cityName);
            callMainActivity();
        }
    }

    @Override
    public void onComplete() {

    }
}
