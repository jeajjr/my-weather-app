package com.homespotter.weatherinternshipproject.ui;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.homespotter.weatherinternshipproject.data.CurrentConditions;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;
import com.homespotter.weatherinternshipproject.data.WeatherClient;

public class WeatherDataService extends IntentService {
    private static final String TAG = "WeatherDataService";

    // Input arguments
    public static final String ARG_CITYNAME = "city";
    public static final String ARG_SETTINGS = "settings";

    // Result arguments
    public static final String ARG_RESULT = "result";
    public static final String ARG_CURRENT_DATA = "current";
    public static final String ARG_THREE_HOUR_FORECAST = "threehourforecast";
    public static final String ARG_DAILY_FORECAST = "dailyforecast";

    public static final String BROADCAST_ACTION = "BROADCAST";

    public static final int SERVICE_RESULT_OK = 0;
    public static final int SERVICE_RESULT_NO_INTERNET = 1;
    public static final int SERVICE_RESULT_CONNECTION_ERROR = 2;

    private String cityName;
    private SettingsProfile settingsProfile;

    protected CurrentConditions currentConditions = null;
    protected boolean currentConditionThreadDone;
    protected MultipleWeatherForecast threeHoursForecast = null;
    protected boolean threeHoursForecastThreadDone ;
    protected MultipleWeatherForecast dailyForecast = null;
    protected boolean dailyForecastThreadDone;


    public WeatherDataService() {
        super(TAG);
    }

    /**
     * Returns a runnable that will fetch and parse currentConditions data for the current city.
     */
    private Runnable getFetchCurrentConditions() {
        return new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "fetchCurrentConditions runnable");
                currentConditionThreadDone = false;

                try { Thread.sleep(2000 + (long) (Math.random() * 1000.0)); } catch (Exception e) {Log.d(TAG, "error sleep", e); }

                String data = "{\"coord\":{\"lon\":-0.13,\"lat\":51.51},\"sys\":{\"message\":0.0774,\"country\":\"GB\",\"sunrise\":1426227494,\"sunset\":1426269695},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":278.742,\"temp_min\":278.742,\"temp_max\":278.742,\"pressure\":1029.22,\"sea_level\":1039.72,\"grnd_level\":1029.22,\"humidity\":71},\"wind\":{\"speed\":4.12,\"deg\":54.501},\"clouds\":{\"all\":32},\"dt\":1426282599,\"id\":2643743,\"name\":\"London\",\"cod\":200}";
                //WeatherClient.getInstance().getCurrentConditionsData(cityName, settingsProfile.getUnits());
                currentConditions = DataParser.parseCurrentConditions(data);

                if (currentConditions == null) {
                    Log.d(TAG, "fetchCurrentConditions finishes on error");

                    currentConditionThreadDone = true;
                    onThreadDone(SERVICE_RESULT_CONNECTION_ERROR);
                } else {
                    currentConditions.temperatureUnit = settingsProfile.getTemperatureUnitString();
                    currentConditions.speedUnit = settingsProfile.getSpeedUnitString();

                    currentConditionThreadDone = true;
                    onThreadDone(SERVICE_RESULT_OK);
                }
            }
        };
    }

    /**
     * Returns a runnable that will fetch and parse the three hours forecast data for the current city.
     */
    private Runnable getFetchThreeHoursForecast() {
        return new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "fetchthreeHoursForecast runnable");
                threeHoursForecastThreadDone = false;

                try { Thread.sleep(2000 + (long) (Math.random() * 1000.0)); } catch (Exception e) {Log.d(TAG, "error sleep", e); }

                String data = "{\"cod\":\"200\",\"message\":9.4357,\"city\":{\"id\":2643743,\"name\":\"London\",\"coord\":{\"lon\":-0.12574,\"lat\":51.50853},\"country\":\"GB\",\"population\":0,\"sys\":{\"population\":0}},\"cnt\":2,\n" +
                        "\"list\":[{\"dt\":1426269600,\"main\":{\"temp\":278.74,\"temp_min\":278.74,\"temp_max\":281.083,\"pressure\":1027.39,\"sea_level\":1037.72,\"grnd_level\":1027.39,\"humidity\":61,\"temp_kf\":-2.34},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":56},\"wind\":{\"speed\":3.92,\"deg\":56.0004},\"rain\":{\"3h\":0},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2015-03-13 18:00:00\"},{\"dt\":1426280400,\"main\":{\"temp\":276.52,\"temp_min\":276.52,\"temp_max\":278.742,\"pressure\":1029.22,\"sea_level\":1039.72,\"grnd_level\":1029.22,\"humidity\":71,\"temp_kf\":-2.22},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"clouds\":{\"all\":32},\"wind\":{\"speed\":4.12,\"deg\":54.501},\"rain\":{\"3h\":0},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2015-03-13 21:00:00\"}]}";
                //WeatherClient.getInstance().getThreeHoursForecastData(cityName, settingsProfile.getUnits());
                threeHoursForecast = DataParser.parseThreeHourForecast(data);

                if (threeHoursForecast == null) {
                    Log.d(TAG, "fetchThreeHoursForecast finishes on error");

                    threeHoursForecastThreadDone = true;
                    onThreadDone(SERVICE_RESULT_CONNECTION_ERROR);
                } else {
                    threeHoursForecast.temperatureUnit = settingsProfile.getTemperatureUnitString();
                    threeHoursForecast.speedUnit = settingsProfile.getSpeedUnitString();

                    threeHoursForecastThreadDone = true;
                    onThreadDone(SERVICE_RESULT_OK);
                }
            }
        };
    }

    /**
     * Returns a runnable that will fetch and parse the three hours forecast data for the current city.
     */
    private Runnable getFetchDailyForecast() {
        return new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "fetchDailyForecast runnable");
                dailyForecastThreadDone = false;

                try { Thread.sleep(2000 + (long) (Math.random() * 1000.0)); } catch (Exception e) { Log.d(TAG, "error sleep", e); }

                String data = "{\"cod\":\"200\",\"message\":0.6164,\"city\":{\"id\":2643743,\"name\":\"London\",\"coord\":{\"lon\":-0.12574,\"lat\":51.50853},\"country\":\"GB\",\"population\":0,\"sys\":{\"population\":0}},\"cnt\":7,\"list\":[{\"dt\":1426248000,\"temp\":{\"day\":280.85,\"min\":276.51,\"max\":280.85,\"night\":276.51,\"eve\":280.85,\"morn\":280.85},\"pressure\":1031.66,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.77,\"deg\":59,\"clouds\":29,\"rain\":1.65},{\"dt\":1426334400,\"temp\":{\"day\":279.7,\"min\":276.46,\"max\":279.92,\"night\":278.16,\"eve\":278.77,\"morn\":276.46},\"pressure\":1037.73,\"humidity\":83,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":5.62,\"deg\":54,\"clouds\":64,\"rain\":0.41},{\"dt\":1426420800,\"temp\":{\"day\":279.76,\"min\":276.62,\"max\":279.89,\"night\":276.62,\"eve\":278.64,\"morn\":277.94},\"pressure\":1035.61,\"humidity\":86,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":6.41,\"deg\":52,\"clouds\":88,\"rain\":0.45},{\"dt\":1426507200,\"temp\":{\"day\":280.51,\"min\":277.43,\"max\":280.82,\"night\":277.43,\"eve\":280.32,\"morn\":277.56},\"pressure\":1030.74,\"humidity\":91,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":3.57,\"deg\":75,\"clouds\":88,\"rain\":0.62},{\"dt\":1426593600,\"temp\":{\"day\":280.69,\"min\":276.12,\"max\":281.54,\"night\":276.93,\"eve\":280.46,\"morn\":276.12},\"pressure\":1036.07,\"humidity\":94,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.06,\"deg\":335,\"clouds\":92},{\"dt\":1426680000,\"temp\":{\"day\":282.54,\"min\":272.98,\"max\":282.54,\"night\":275.73,\"eve\":281.42,\"morn\":272.98},\"pressure\":1042.47,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.56,\"deg\":75,\"clouds\":10},{\"dt\":1426766400,\"temp\":{\"day\":281.47,\"min\":274.95,\"max\":281.47,\"night\":277.85,\"eve\":281.25,\"morn\":274.95},\"pressure\":1030.88,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.75,\"deg\":60,\"clouds\":79,\"rain\":0.62}]}";

                //WeatherClient.getInstance().getDailyForecastData(cityName, settingsProfile.getUnits());
                dailyForecast = DataParser.parseDailyForecast(data);

                if (dailyForecast == null) {
                    Log.d(TAG, "fetchDailyForecast finishes on error");

                    dailyForecastThreadDone = true;
                    onThreadDone(SERVICE_RESULT_CONNECTION_ERROR);
                } else {
                    dailyForecast.temperatureUnit = settingsProfile.getTemperatureUnitString();
                    dailyForecast.speedUnit = settingsProfile.getSpeedUnitString();

                    dailyForecastThreadDone = true;
                    onThreadDone(SERVICE_RESULT_OK);
                }
            }
        };
    }

    /**
     * This function will be called by the data fetch threads. If any of them return an error result,
     * the service will terminate and return an error result.
     * @param result: the calling thread's result
     */
    private synchronized void onThreadDone(int result) {
        if (result != SERVICE_RESULT_OK) {
            // finish and return error
            returnErrorResult(result);
        }
        else {
            // if all threads are done
            if (currentConditionThreadDone &&
                threeHoursForecastThreadDone &&
                dailyForecastThreadDone) {
                // return result OK and data
                sendWeatherData();
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "onHandleIntent");

        cityName = workIntent.getExtras().getString(ARG_CITYNAME);
        settingsProfile = (SettingsProfile) workIntent.getExtras().getSerializable(ARG_SETTINGS);

        if (checkInternetAccess()) {
            new Thread(getFetchCurrentConditions()).start();
            new Thread(getFetchThreeHoursForecast()).start();
            new Thread(getFetchDailyForecast()).start();
        }
        else {
            returnErrorResult(SERVICE_RESULT_NO_INTERNET);
        }
    }

    private void returnErrorResult(int result) {
        Intent localIntent = new Intent("BROADCAST")
                .putExtra(ARG_RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void sendWeatherData() {
        Intent localIntent = new Intent("BROADCAST")
                .putExtra(ARG_RESULT, SERVICE_RESULT_OK)
                .putExtra(ARG_CURRENT_DATA, currentConditions)
                .putExtra(ARG_THREE_HOUR_FORECAST, threeHoursForecast)
                .putExtra(ARG_DAILY_FORECAST, dailyForecast);

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    /**
     * Check if internet access is available.
     * @return true if available, false otherwise.
     */
    private boolean checkInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }
}
