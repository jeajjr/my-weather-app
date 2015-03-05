package com.homespotter.weatherinternshipproject.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.CurrentConditions;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.WeatherClient;

/**
 * Weather app template project.
 */
public class ActivityMain extends ActionBarActivity implements DataProviderInterface {
    private final static String TAG = "ActivityMain";

    private CurrentConditions currentConditions = null;
    private MultipleWeatherForecast threeHoursForecast = null;
    private MultipleWeatherForecast dailyForecast = null;

    private FragmentCurrentConditions fragmentCurrentConditions = null;
    private FragmentThreeHoursForecast fragmentThreeHoursForecast = null;

    private String cityName;

    private int units;
    private final String SPEED_UNIT_IMPERIAL = "mph";
    private final String SPEED_UNIT_METRIC = "km/h";
    private final String TEMPERATURE_UNIT_IMPERIAL = "F";
    private final String TEMPERATURE_UNIT_METRIC = "C";

    /*
        Methods of DataProviderInterface
     */
    @Override
    public void setCurrentConditionsFragment(FragmentCurrentConditions fragmentCurrentConditions) {
        this.fragmentCurrentConditions = fragmentCurrentConditions;

        // If currentConditions is already fetched, send it to fragment
        if (currentConditions != null)
            fragmentCurrentConditions.setConditions(currentConditions);
    }

    @Override
    public void setThreeHoursForecastFragment(FragmentThreeHoursForecast fragmentThreeHoursForecast) {
        this.fragmentThreeHoursForecast = fragmentThreeHoursForecast;

        // If threeHoursForecast is already fetched, send it to fragment
        if (threeHoursForecast != null)
            fragmentThreeHoursForecast.setConditions(threeHoursForecast);
    }

    public boolean checkInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    /*
        Local methods.
     */
    public void fetchCurrentConditions() {
        new Thread() {
            public void run() {
                Log.d(TAG, "Getting current conditions");

                //try { Thread.sleep(5000); } catch (Exception e) { Log.d(TAG, "error sleep "); }

                String currentData = WeatherClient.getInstance().getCurrentConditionsData(cityName, units);

                Log.d(TAG, "Parsing current conditions");
                currentConditions = DataParser.parseCurrentConditions(currentData);
                currentConditions.temperatureUnit = (units == WeatherClient.IMPERIAL_UNITS) ?
                        TEMPERATURE_UNIT_IMPERIAL : TEMPERATURE_UNIT_METRIC;
                currentConditions.speedUnit = (units == WeatherClient.IMPERIAL_UNITS) ?
                        SPEED_UNIT_IMPERIAL : SPEED_UNIT_METRIC;

                // If fragmentCurrentConditions has been created and is waiting for the data, send it
                if (fragmentCurrentConditions != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentCurrentConditions.setConditions(currentConditions);
                        }
                    });
                }
            }
        }.start();
    }

    public void fetchThreeHoursForecast() {
        new Thread() {
            public void run() {
                Log.d(TAG, "Getting 3 hour forecast");

                //try { Thread.sleep(5000); } catch (Exception e) { Log.d(TAG, "error sleep "); }

                String data = WeatherClient.getInstance().getFiveDaysForecastData(cityName, units);

                Log.d(TAG, "Parsing current conditions");
                threeHoursForecast = DataParser.parseFiveDaysForecast(data);
                threeHoursForecast.temperatureUnit = (units == WeatherClient.IMPERIAL_UNITS) ?
                        TEMPERATURE_UNIT_IMPERIAL : TEMPERATURE_UNIT_METRIC;
                threeHoursForecast.speedUnit = (units == WeatherClient.IMPERIAL_UNITS) ?
                        SPEED_UNIT_IMPERIAL : SPEED_UNIT_METRIC;

                // If fragmentCurrentConditions has been created and is waiting for the data, send it
                if (fragmentCurrentConditions != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentThreeHoursForecast.setConditions(threeHoursForecast);
                        }
                    });
                }
            }
        }.start();
    }

    /*
        Activity methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = getIntent().getExtras().getString("cityName");
        units = WeatherClient.METRIC_UNITS;

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(viewPagerAdapter);

        fetchCurrentConditions();
        fetchThreeHoursForecast();
    }
}
