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

    public RecylerViewAdapter(DataProviderInterface dataProvider, MultipleWeatherForecast dataSet, int forecastType) {
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
                    String temperatureUnit = "C"; //TODO
                    //icon;

                    Calendar time = (Calendar) data.get(WeatherParameters.forecastDate);
                    SimpleDateFormat sf = new SimpleDateFormat("dd/MM, hh:mm aa");
                    period.setText(sf.format(time.getTime()));

                    Double temperatureDouble = (Double) data.get(WeatherParameters.temperature);
                    if (temperatureUnit.compareTo("F") == 0)
                        temperatureDouble = (temperatureDouble - 273.15)*1.8 + 32.0;
                    else
                        temperatureDouble -= 273.15;
                    temperature.setText(String.format("%.0f", temperatureDouble) + "º" + temperatureUnit);
                    /*
                    description = (TextView) v.findViewById(R.id.textViewDescription);
                    humidity = (TextView) v.findViewById(R.id.textViewHumidity);
                    wind = (TextView) v.findViewById(R.id.textViewWind);
                    pressure
                    */
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