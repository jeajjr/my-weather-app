package com.homespotter.weatherinternshipproject;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CurrentConditionsFragment extends Fragment {
    private static String TAG = "CurrentConditionsFragment";

    // pack of weather data
    CurrentConditions currentConditions;

    // data provider (roughly, the activity)
    DataProvider dataProvider;

    // UI widgets
    TextView updatedTime;
    ImageView icon;
    TextView description;
    TextView currentTemperature;
    TextView maxTemperature;
    TextView minTemperature;
    TextView humidity;
    TextView wind;
    TextView pressure;
    TextView sunrise;
    TextView sunset;
    TextView lastHours;

    public CurrentConditionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dataProvider = (DataProvider) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_current_conditions, container, false);

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null) {
            new Thread() {
                public void run() {
                    Log.d(TAG, "City: " + dataProvider.getCityName());
                    String currentData = WeatherClient.getCurrentConditionsData(dataProvider.getCityName());

                    Log.d(TAG, "data: " + currentData);

                    currentConditions = DataParser.parseCurrentConditions(currentData);

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            updateScreenData();
                        }
                    });
                }
            }.start();
        }
        else {
            Log.d(TAG, "no internet connection");
        }

        updatedTime = (TextView) v.findViewById(R.id.textViewCurrUpdated);
        icon = (ImageView) v.findViewById(R.id.imageViewCurrIcon);
        description = (TextView) v.findViewById(R.id.textViewCurrDescription);
        currentTemperature = (TextView) v.findViewById(R.id.textViewCurrTemperature);
        maxTemperature = (TextView) v.findViewById(R.id.textViewCurrMaxTemperature);
        minTemperature = (TextView) v.findViewById(R.id.textViewCurrMinTemperature);
        humidity = (TextView) v.findViewById(R.id.textViewCurrHumidity);
        wind = (TextView) v.findViewById(R.id.textViewCurrWind);
        pressure = (TextView) v.findViewById(R.id.textViewCurrPressure);
        sunrise = (TextView) v.findViewById(R.id.textViewCurrSunrise);
        sunset = (TextView) v.findViewById(R.id.textViewCurrSunset);
        lastHours = (TextView) v.findViewById(R.id.textViewCurrLastHours);

        return v;
    }

    private void updateScreenData() {

        //updatedTime.setText(currentConditions.weatherInfo.get());
        //icon = (ImageView) v.findViewById(R.id.imageViewCurrIcon);
        description.setText(currentConditions.weatherInfo.get(WeatherParameters.weatherDescription).toString());
        currentTemperature.setText(currentConditions.weatherInfo.get(WeatherParameters.temperature).toString() + "º" + dataProvider.getTemperatureUnit());
        maxTemperature.setText(currentConditions.weatherInfo.get(WeatherParameters.temperatureMax).toString() + "º" + dataProvider.getTemperatureUnit());
        minTemperature.setText(currentConditions.weatherInfo.get(WeatherParameters.temperatureMin).toString() + "º" + dataProvider.getTemperatureUnit());
        humidity.setText(getResources().getString(R.string.weather_humidity) + ": " + currentConditions.weatherInfo.get(WeatherParameters.humidity).toString() + "%");
        /*
        wind = (TextView) v.findViewById(R.id.textViewCurrUpdated);
        pressure = (TextView) v.findViewById(R.id.textViewCurrUpdated);
        sunrise = (TextView) v.findViewById(R.id.textViewCurrUpdated);
        sunset = (TextView) v.findViewById(R.id.textViewCurrUpdated);
        lastHours
        */
    }


}
