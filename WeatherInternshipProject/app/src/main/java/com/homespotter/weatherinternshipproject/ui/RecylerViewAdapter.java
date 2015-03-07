package com.homespotter.weatherinternshipproject.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.WeatherClient;
import com.homespotter.weatherinternshipproject.data.WeatherParameters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ernesto on 12/02/2015.
 */
public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.ViewHolder> {
    private static String TAG = "RecylerViewAdapter";

    public static final int THREE_HOUR_FORECAST = 0;
    public static final int DAILY_FORECAST = 1;

    private Context context;
    private DataProviderInterface dataProvider;
    private MultipleWeatherForecast dataSet;
    private int forecastType;

    public RecylerViewAdapter(Context context, DataProviderInterface dataProvider, MultipleWeatherForecast dataSet, int forecastType) {
        this.context = context;
        this.dataProvider = dataProvider;
        this.dataSet = dataSet;
        this.forecastType = forecastType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView period;
        public TextView temperature;
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
            }
        }

        public void bindForecastData (Map<String, ?> data) {
            switch (forecastType) {
                case THREE_HOUR_FORECAST:

                    icon.setImageResource(DataParser.getIconResource((String) data.get(WeatherParameters.weatherIconID)));

                    Calendar time = (Calendar) data.get(WeatherParameters.forecastDate);
                    SimpleDateFormat sf = new SimpleDateFormat("dd/MM, hh:mm aa");
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
                            data.get(WeatherParameters.pressure) + " mb");

                    break;
            }
        }
    }

    @Override
    public RecylerViewAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = null;

        switch (forecastType) {
            case THREE_HOUR_FORECAST:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_3_hour_item, parent, false);
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