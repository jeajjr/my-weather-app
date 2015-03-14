package com.homespotter.weatherinternshipproject.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.CurrentConditions;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.FilesHandler;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;
import com.homespotter.weatherinternshipproject.data.WeatherClient;
import com.homespotter.weatherinternshipproject.data.WeatherParameters;

public class WeatherAppWidgetProvider extends AppWidgetProvider{
    private static final String TAG = "WeatherAppWidgetProvider";

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
                         final int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onThread");

                String cityName = FilesHandler.getInstance().getSavedCities(context).get(0);
                SettingsProfile settingsProfile = FilesHandler.getInstance().getSettingProfile(context);

                try {

                    Log.d(TAG, "getting weather for " + cityName);

                    String data;
                    // TODO remove
                    if (ActivityMain.debugConnection) {
                        data = "{\"coord\":{\"lon\":-0.13,\"lat\":51.51},\"sys\":{\"message\":0.0774,\"country\":\"GB\",\"sunrise\":1426227494,\"sunset\":1426269695},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":278.742,\"temp_min\":278.742,\"temp_max\":278.742,\"pressure\":1029.22,\"sea_level\":1039.72,\"grnd_level\":1029.22,\"humidity\":71},\"wind\":{\"speed\":4.12,\"deg\":54.501},\"clouds\":{\"all\":32},\"dt\":1426282599,\"id\":2643743,\"name\":\"London\",\"cod\":200}";

                        try { Thread.sleep(1500 + (long) (Math.random() * 1000.0)); } catch (Exception e) {}
                    }
                    else {
                        data = WeatherClient.getInstance().getCurrentConditionsData(cityName, SettingsProfile.UNIT_METRIC);
                    }
                    CurrentConditions currentConditions = DataParser.parseCurrentConditions(data);
                    currentConditions.temperatureUnit = settingsProfile.getTemperatureUnitString();
                    currentConditions.speedUnit = settingsProfile.getSpeedUnitString();

                    for (int i = 0; i < appWidgetIds.length; i++) {
                        int currentWidgetId = appWidgetIds[i];

                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                        Double temperature = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperature);
                        views.setTextViewText(R.id.textViewWidgetTemperature,
                                String.format("%.0f", temperature) + "º" + currentConditions.temperatureUnit);

                        Double temperatureMax = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperatureMax);
                        views.setTextViewText(R.id.textViewWidgetTemperatureMax,
                                String.format("%.0f", temperatureMax) + "º" + currentConditions.temperatureUnit);

                        Double temperatureMin = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperatureMin);
                        views.setTextViewText(R.id.textViewWidgetTemperatureMin,
                                String.format("%.0f", temperatureMin) + "º" + currentConditions.temperatureUnit);

                        views.setTextViewText(R.id.textViewWidgetDescription,
                                ((String) currentConditions.weatherInfo.get(WeatherParameters.weatherDescription)).toUpperCase());

                        views.setTextViewText(R.id.textViewWidgetCity,
                                ((String) currentConditions.locationInfo.get(WeatherParameters.cityName)) + ", " +
                                ((String) currentConditions.locationInfo.get(WeatherParameters.countryName))
                        );

                        views.setImageViewResource(R.id.imageViewWidgetIcon,
                                DataParser.getIconResource((String) currentConditions.weatherInfo.get(WeatherParameters.weatherIconID)));

                        appWidgetManager.updateAppWidget(currentWidgetId, views);
                    }
                    Log.d(TAG, "done updating views");
                } catch (Exception e) {
                    String notAvailable = context.getString(R.string.not_available);

                    for (int i = 0; i < appWidgetIds.length; i++) {
                        int currentWidgetId = appWidgetIds[i];

                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                        views.setTextViewText(R.id.textViewWidgetTemperature, notAvailable);
                        views.setTextViewText(R.id.textViewWidgetTemperatureMax, notAvailable);
                        views.setTextViewText(R.id.textViewWidgetTemperatureMin, notAvailable);
                        views.setTextViewText(R.id.textViewWidgetDescription, notAvailable);
                        views.setTextViewText(R.id.textViewWidgetCity, notAvailable);

                        appWidgetManager.updateAppWidget(currentWidgetId, views);
                    }
                }
            }
        }).start();

    }
}