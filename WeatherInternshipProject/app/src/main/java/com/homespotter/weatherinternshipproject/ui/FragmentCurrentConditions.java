package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.CurrentConditions;
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

    FrameLayout mainLayout;

    // Loading dialog
    ProgressDialog progressDialog;

    // Schedule a refresh for the last update tag
    private int refreshInterval = 60 * 1000; // 60 seconds refresh
    private Handler refreshHandler;
    private boolean handlerScheduled = false;
    Runnable refresherRunnable = new Runnable() {
        @Override
        public void run() {
            refreshLastUpdateTag();
            refreshHandler.postDelayed(refresherRunnable, refreshInterval);
            Log.d(TAG, "last update tag updated");
        }
    };

    public FragmentCurrentConditions() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dataProvider = (DataProviderInterface) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_current_conditions, container, false);

        refreshHandler = new Handler();

        // fade list while loading
        mainLayout = (FrameLayout) v.findViewById(R.id.layout);
        mainLayout.getForeground().setAlpha(200);

        // show progress dialog while data is being fetched
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

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

        dataProvider.setCurrentConditionsFragment(this);

        Log.d(TAG, "done onCreateView");
        return v;
    }

    private void scheduleRefresh() {
        refresherRunnable.run();
    }

    public void setConditions (CurrentConditions currentConditions) {
        Log.d(TAG, "setConditions");

        Log.d(TAG, (String) currentConditions.weatherInfo.get(WeatherParameters.weatherDescription));
        this.currentConditions = currentConditions;

        updateScreenData();
    }

    private void refreshLastUpdateTag() {
        Calendar updatedTimeCal = (Calendar) currentConditions.weatherInfo.get(WeatherParameters.dateReceived);
        Calendar now = Calendar.getInstance();
        long difference =  now.getTimeInMillis() - updatedTimeCal.getTimeInMillis();
        difference /= 1000; // difference is now in seconds

        if (isAdded()) {
            String timeStamp = "";
            if (difference < 60)
                timeStamp = getResources().getString(R.string.update_time_less_than_one_minute_ago);
            else if ((difference /= 60) < 60) // difference is now in minutes
                timeStamp = getResources().getString(R.string.update_updated) + " " + difference + " " +
                        getResources().getString(R.string.update_time_minutes_ago);
            else if ((difference /= 60) < 24) // difference is now in hours
                timeStamp = getResources().getString(R.string.update_updated) + " " + difference + " " +
                        getResources().getString(R.string.update_time_hours_ago);

            updatedTime.setText(timeStamp);
        }
    }

    private void updateScreenData() {
        Log.d(TAG, "updateScreenData");

        if (!handlerScheduled) {
            scheduleRefresh();
            handlerScheduled = true;
        }

        progressDialog.show();

        refreshLastUpdateTag();

        //icon = (ImageView) v.findViewById(R.id.imageViewCurrIcon);

        description.setText( (String) currentConditions.weatherInfo.get(WeatherParameters.weatherDescription));

        Double temperature = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperature);
        currentTemperature.setText(String.format("%.0f", temperature) + "º" + currentConditions.temperatureUnit);

        Double temperatureMax = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperatureMax);
        maxTemperature.setText(String.format("%.0f", temperatureMax) + "º" + currentConditions.temperatureUnit);

        Double temperatureMin = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperatureMin);
        minTemperature.setText(String.format("%.0f", temperatureMin) + "º" + currentConditions.temperatureUnit);

        Double windSpeed = (Double) currentConditions.weatherInfo.get(WeatherParameters.windSpeed);
        Double windDirection = (Double) currentConditions.weatherInfo.get(WeatherParameters.windDegrees);
        Double windGusts = (Double) currentConditions.weatherInfo.get(WeatherParameters.windGusts);

        String windInfo = getResources().getString(R.string.weather_wind) + ": ";
        windInfo += String.format("%.0f", windSpeed) + " " + currentConditions.speedUnit;
        windInfo += ", " + String.format("%.0f", windDirection) + "º";
        // Some locations may not support detailed information
        if (windGusts != null) {
            windInfo += ", " + String.format("%.0f", windGusts) + " " + currentConditions.speedUnit;
        }
        wind.setText(windInfo);

        int humidityInt = (int) currentConditions.weatherInfo.get(WeatherParameters.humidity);
        humidity.setText(getResources().getString(R.string.weather_humidity) + ": " + humidityInt + "%");

        pressure.setText(getResources().getString(R.string.weather_pressure) + ": " + String.format("%.1f",
                currentConditions.weatherInfo.get(WeatherParameters.pressure)) + " mb");

        Calendar sunriseCal = (Calendar) currentConditions.weatherInfo.get(WeatherParameters.sunrise);
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm aa");
        sunrise.setText(sf.format(sunriseCal.getTime()));

        Calendar sunsetCal = (Calendar) currentConditions.weatherInfo.get(WeatherParameters.sunset);
        sunset.setText(sf.format(sunsetCal.getTime()));

        String lastHoursText = getResources().getString(R.string.weather_last_three_hours);
        Integer rain = (Integer) currentConditions.weatherInfo.get(WeatherParameters.rainPrecipitation);
        if (rain != null) {
            lastHoursText += rain + " mm";
        }
        else {
            Integer snow = (Integer) currentConditions.weatherInfo.get(WeatherParameters.snowPrecipitation);
            if (snow != null) {
                lastHoursText += snow + " mm";
            }
            else {
                lastHoursText = "";
            }
        }
        lastHours.setText(lastHoursText);

        progressDialog.dismiss();
        mainLayout.getForeground().setAlpha(0);
        Log.d(TAG, "updateScreenData done");
    }
}
