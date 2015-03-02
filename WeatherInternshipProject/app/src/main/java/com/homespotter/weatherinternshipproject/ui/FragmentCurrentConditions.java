package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.CurrentConditions;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.WeatherClient;
import com.homespotter.weatherinternshipproject.data.WeatherParameters;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class FragmentCurrentConditions extends Fragment {
    private static String TAG = "CurrentConditionsFragment";

    // pack of weather data
    CurrentConditions currentConditions;

    // data provider (roughly, the activity)
    DataProviderInterface dataProvider;

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

    // Loading dialog
    ProgressDialog progressDialog;

    public FragmentCurrentConditions() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dataProvider = (DataProviderInterface) activity;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_current_conditions, container, false);

        if (dataProvider.checkInternetAccess()) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.warning_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Thread() {
                public void run() {
                    Log.d(TAG, "Getting current conditions for " + dataProvider.getCityName());
                    String currentData = WeatherClient.getInstance().getCurrentConditionsData(dataProvider.getCityName());

                    Log.d(TAG, "Parsing current conditions");
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
        Log.d(TAG, "updateScreenData");

        String temperatureUnit = dataProvider.getTemperatureUnit();

        //updatedTime.setText(currentConditions.weatherInfo.get());
        //icon = (ImageView) v.findViewById(R.id.imageViewCurrIcon);

        description.setText(currentConditions.weatherInfo.get(WeatherParameters.weatherDescription).toString());

        Double temperature = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperature);
        if (temperatureUnit.compareTo("F") == 0)
            temperature = (temperature - 273.15)*1.8 + 32.0;
        else
            temperature -= 273.15;
        currentTemperature.setText(String.format("%.0f", temperature) + "º" + temperatureUnit);

        Double temperatureMax = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperatureMax);
        if (temperatureUnit.compareTo("F") == 0)
            temperatureMax = (temperatureMax - 273.15)*1.8 + 32.0;
        else
            temperatureMax -= 273.15;
        maxTemperature.setText(String.format("%.0f", temperatureMax) + "º" + temperatureUnit);

        Double temperatureMin = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperatureMin);
        if (temperatureUnit.compareTo("F") == 0)
            temperatureMin = (temperatureMin - 273.15)*1.8 + 32.0;
        else
            temperatureMin -= 273.15;
        minTemperature.setText(String.format("%.0f", temperatureMin) + "º" + temperatureUnit);

        Double windSpeed = (Double) currentConditions.weatherInfo.get(WeatherParameters.windSpeed);
        Double windDirection = (Double) currentConditions.weatherInfo.get(WeatherParameters.windDegrees);
        Double windGusts = (Double) currentConditions.weatherInfo.get(WeatherParameters.windGusts);

        String windInfo = getResources().getString(R.string.weather_wind) + ": ";
        if (temperatureUnit.compareTo("F") == 0)
            windInfo += String.format("%.0f", windSpeed) + " mph";
        else
            windInfo += String.format("%.0f", windSpeed*1.609) + " km/h";

        windInfo += ", " + String.format("%.0f", windDirection) + "º";

        // Some locations may not support detailed information
        if (windGusts != null) {
            if (temperatureUnit.compareTo("F") == 0)
                windInfo += String.format("%.0f", windGusts) + " mph";
            else
                windInfo += String.format("%.0f", windGusts * 1.609) + " km/h";
        }

        wind.setText(windInfo);

        humidity.setText(getResources().getString(R.string.weather_humidity) + ": " + currentConditions.weatherInfo.get(WeatherParameters.humidity).toString() + "%");

        pressure.setText(getResources().getString(R.string.weather_pressure) + ": " + currentConditions.weatherInfo.get(WeatherParameters.pressure).toString() + " mb");

        Calendar sunriseCal = (Calendar) currentConditions.weatherInfo.get(WeatherParameters.sunrise);
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm aa");
        sunrise.setText(sf.format(sunriseCal.getTime()));

        Calendar sunsetCal = (Calendar) currentConditions.weatherInfo.get(WeatherParameters.sunset);
        sunset.setText(sf.format(sunsetCal.getTime()));

        String lastHoursText = getResources().getString(R.string.weather_last_three_hours);
        Double rain = (Double) currentConditions.weatherInfo.get(WeatherParameters.rainPrecipitation);
        if (rain != null) {
            if (temperatureUnit.compareTo("F") == 0)
                lastHoursText += String.format("%.1f", rain*0.03937) + " inches";
            else
                lastHoursText += rain + " mm";
        }
        else {
            Double snow = (Double) currentConditions.weatherInfo.get(WeatherParameters.snowPrecipitation);
            if (snow != null) {
                if (temperatureUnit.compareTo("F") == 0)
                    lastHoursText += String.format("%.1f", snow*0.03937) + " inches";
                else
                    lastHoursText += snow + " mm";
            }
            else {
                lastHoursText = "";
            }
        }
        lastHours.setText(lastHoursText);

        progressDialog.dismiss();
        Log.d(TAG, "updateScreenData done");
    }
}
