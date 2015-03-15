package com.homespotter.weatherinternshipproject.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.CurrentConditions;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.FilesHandler;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;
import com.homespotter.weatherinternshipproject.data.WeatherClient;
import com.homespotter.weatherinternshipproject.data.WeatherParameters;

public class WeatherAppWidgetProvider extends AppWidgetProvider{
    private static final String TAG = "WeatherAppWidgetProvider";

    private String cityName;
    private SettingsProfile settingsProfile;
    private CurrentConditions currentConditions;

    private Context context;
    private AppWidgetManager appWidgetManager;
    private int[] appWidgetIds;

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
                         final int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");

        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;

        cityName = FilesHandler.getInstance().getSavedCities(context).get(0);
        settingsProfile = FilesHandler.getInstance().getSettingProfile(context);

        // Registers the ResponseReceiver
        IntentFilter mStatusIntentFilter = new IntentFilter(WeatherDataService.BROADCAST_ACTION);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(responseReceiver, mStatusIntentFilter);

        Log.d(TAG, "calling service for city " + cityName + " and settings " + settingsProfile);
        Intent serviceIntent = new Intent(context, WeatherDataService.class);
        serviceIntent.putExtra(WeatherDataService.ARG_CITYNAME, cityName);
        serviceIntent.putExtra(WeatherDataService.ARG_SETTINGS, settingsProfile);
        context.startService(serviceIntent);
    }

    public void onServiceResult(Intent intent) {
        Log.d(TAG, "onServiceResult");

        int result = intent.getExtras().getInt(WeatherDataService.ARG_RESULT);

        if (result == WeatherDataService.SERVICE_RESULT_OK) {
            Log.d(TAG, "service returned result OK");

            currentConditions =
                    (CurrentConditions) intent.getExtras().getSerializable(WeatherDataService.ARG_CURRENT_DATA);

            for (int i = 0; i < appWidgetIds.length; i++) {
                int currentWidgetId = appWidgetIds[i];

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                Double temperature = (Double) currentConditions.weatherInfo.get(WeatherParameters.temperature);
                Log.d(TAG, "temp: " + temperature);
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

                views.setTextViewText(R.id.textViewWidgetCity, cityName);

                views.setImageViewResource(R.id.imageViewWidgetIcon,
                        DataParser.getIconResource((String) currentConditions.weatherInfo.get(WeatherParameters.weatherIconID)));

                appWidgetManager.updateAppWidget(currentWidgetId, views);
            }

        }
        else {
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


    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG + "ResponseReceiver", "onReceive");

            WeatherAppWidgetProvider.this.onServiceResult(intent);
        }
    }
}