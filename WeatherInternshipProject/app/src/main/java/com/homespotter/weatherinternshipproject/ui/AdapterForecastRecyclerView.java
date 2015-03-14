package com.homespotter.weatherinternshipproject.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;
import com.homespotter.weatherinternshipproject.data.WeatherParameters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by José Ernesto on 12/02/2015.
 */
public class AdapterForecastRecyclerView extends RecyclerView.Adapter<AdapterForecastRecyclerView.ViewHolder> {
    private static String TAG = "RecylerViewAdapter";

    public static final int THREE_HOUR_FORECAST = 0;
    public static final int DAILY_FORECAST = 1;

    private Context context;
    private MultipleWeatherForecast dataSet;
    private SettingsProfile settingsProfile;
    private int forecastType;

    public AdapterForecastRecyclerView(Context context, MultipleWeatherForecast dataSet, SettingsProfile settingsProfile, int forecastType) {
        this.context = context;
        this.dataSet = dataSet;
        this.settingsProfile = settingsProfile;
        this.forecastType = forecastType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView period;
        public TextView temperature;
        public TextView temperatureMax;
        public TextView temperatureMin;
        public TextView temperatureMorning;
        public TextView temperatureEvening;
        public TextView temperatureNight;
        public TextView description;
        public TextView humidity;
        public TextView wind;
        public TextView pressure;

        public ViewHolder(View v) {
            super(v);
            switch (forecastType) {
                case THREE_HOUR_FORECAST:
                    icon = (ImageView) v.findViewById(R.id.imageViewIcon);
                    period = (TextView) v.findViewById(R.id.textViewPeriod);
                    temperature = (TextView) v.findViewById(R.id.textViewTemperature);
                    description = (TextView) v.findViewById(R.id.textViewDescription);
                    humidity = (TextView) v.findViewById(R.id.textViewHumidity);
                    wind = (TextView) v.findViewById(R.id.textViewWind);
                    pressure = (TextView) v.findViewById(R.id.textViewPressure);
                    break;

                case DAILY_FORECAST:
                    icon = (ImageView) v.findViewById(R.id.imageViewIcon);
                    period = (TextView) v.findViewById(R.id.textViewPeriod);
                    temperatureMax = (TextView) v.findViewById(R.id.textViewTemperatureMax);
                    temperatureMin = (TextView) v.findViewById(R.id.textViewTemperatureMin);
                    description = (TextView) v.findViewById(R.id.textViewDescription);
                    humidity = (TextView) v.findViewById(R.id.textViewHumidity);
                    wind = (TextView) v.findViewById(R.id.textViewWind);
                    pressure = (TextView) v.findViewById(R.id.textViewPressure);
                    temperatureMorning = (TextView) v.findViewById(R.id.textViewTemperatureMorning);
                    temperatureEvening = (TextView) v.findViewById(R.id.textViewTemperatureEvening);
                    temperatureNight = (TextView) v.findViewById(R.id.textViewTemperatureNight);
                    break;
            }
        }

        public void bindForecastData (Map<String, ?> data) {
            switch (forecastType) {
                case THREE_HOUR_FORECAST:
                    bindThreeHourForecastData(data);
                    break;

                case DAILY_FORECAST:
                    bindDailyForecastData(data);
                    break;
            }
        }

        private void bindThreeHourForecastData(Map<String, ?> data) {
            String iconString = (String) data.get(WeatherParameters.weatherIconID);
            if (iconString != null)
                icon.setImageResource(DataParser.getIconResource(iconString));

            Calendar time = (Calendar) data.get(WeatherParameters.forecastDate);

            String dateFormat = "";
            // set date format
            if (settingsProfile.getDateFormat() == SettingsProfile.DATE_FORMAT_MM_DD)
                dateFormat += "MM/dd";
            else
                dateFormat += "dd/MM";

            dateFormat += ", ";
            // set hour format
            if (settingsProfile.getHourFormat() == SettingsProfile.HOUR_FORMAT_12)
                dateFormat += "hh:mm aa";
            else
                dateFormat += "HH:mm";
            SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
            period.setText(sf.format(time.getTime()));

            Double temperatureDouble = (Double) data.get(WeatherParameters.temperature);
            temperature.setText(String.format("%.0f", temperatureDouble) + "º" + dataSet.temperatureUnit);

            description.setText(((String) data.get(WeatherParameters.weatherDescription)).toUpperCase());

            int humidityInt = (Integer) data.get(WeatherParameters.humidity);
            humidity.setText(context.getResources().getString(R.string.weather_humidity) + ": " + humidityInt + "%");

            Double windSpeed = (Double) data.get(WeatherParameters.windSpeed);
            Double windDirection = (Double) data.get(WeatherParameters.windDegrees);
            Double windGusts = (Double) data.get(WeatherParameters.windGusts);

            String windInfo = context.getResources().getString(R.string.weather_wind) + ": ";
            windInfo += String.format("%.0f", windSpeed) + " " + dataSet.speedUnit;
            windInfo += ", " + String.format("%.0f", windDirection) + "º";
            // Some locations may not support detailed information
            if (windGusts != null) {
                windInfo += ", " + String.format("%.0f", windGusts) + " " + dataSet.speedUnit;
            }
            wind.setText(windInfo);

            pressure.setText(context.getResources().getString(R.string.weather_pressure) + ": " +
                    String.format("%.1f", data.get(WeatherParameters.pressure)) + " mb");
        }

        private void bindDailyForecastData(Map<String, ?> data) {
            String iconString = (String) data.get(WeatherParameters.weatherIconID);
            if (iconString != null)
                icon.setImageResource(DataParser.getIconResource(iconString));

            Calendar time = (Calendar) data.get(WeatherParameters.forecastDate);

            String dateFormat = "";
            // set date format
            if (settingsProfile.getDateFormat() == SettingsProfile.DATE_FORMAT_MM_DD)
                dateFormat += "MM/dd";
            else
                dateFormat += "dd/MM";
            SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
            period.setText(sf.format(time.getTime()));

            Double temperatureMaxDouble = (Double) data.get(WeatherParameters.temperatureMax);
            temperatureMax.setText(String.format("%.0f", temperatureMaxDouble) + "º" + dataSet.temperatureUnit);

            Double temperatureMinDouble = (Double) data.get(WeatherParameters.temperatureMin);
            temperatureMin.setText(String.format("%.0f", temperatureMinDouble) + "º" + dataSet.temperatureUnit);

            Double temperatureMorningDouble = (Double) data.get(WeatherParameters.morningTemperature);
            temperatureMorning.setText(String.format("%.0f", temperatureMorningDouble) + "º" + dataSet.temperatureUnit);

            Double temperatureEveningDouble = (Double) data.get(WeatherParameters.eveningTemperature);
            temperatureEvening.setText(String.format("%.0f", temperatureEveningDouble) + "º" + dataSet.temperatureUnit);

            Double temperatureNightDouble = (Double) data.get(WeatherParameters.nightTemperature);
            temperatureNight.setText(String.format("%.0f", temperatureNightDouble) + "º" + dataSet.temperatureUnit);

            description.setText(((String) data.get(WeatherParameters.weatherDescription)).toUpperCase());

            int humidityInt = (Integer) data.get(WeatherParameters.humidity);
            humidity.setText(context.getResources().getString(R.string.weather_humidity) + ": " + humidityInt + "%");

            Double windSpeed = (Double) data.get(WeatherParameters.windSpeed);
            Double windDirection = (Double) data.get(WeatherParameters.windDegrees);
            Double windGusts = (Double) data.get(WeatherParameters.windGusts);

            if (windSpeed != null) {
                String windInfo = context.getResources().getString(R.string.weather_wind) + ": ";
                windInfo += String.format("%.0f", windSpeed) + " " + dataSet.speedUnit;
                windInfo += ", " + String.format("%.0f", windDirection) + "º";

                // Some locations may not support detailed information
                if (windGusts != null) {
                    windInfo += ", " + String.format("%.0f", windGusts) + " " + dataSet.speedUnit;
                }

                wind.setText(windInfo);
            }
            else
                wind.setVisibility(View.GONE);

            pressure.setText(context.getResources().getString(R.string.weather_pressure) + ": " +
                    String.format("%.1f", data.get(WeatherParameters.pressure)) + " mb");
        }
    }

    @Override
    public AdapterForecastRecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = null;

        switch (forecastType) {
            case THREE_HOUR_FORECAST:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_3_hour_item, parent, false);
                break;
            case DAILY_FORECAST:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_daily_item, parent, false);
                break;
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        Map<String, ?> data = dataSet.weatherInfoList.get(position);
        holder.bindForecastData(data);
    }

    @Override
    public int getItemCount() {
        return dataSet.weatherInfoList.size();
    }
}