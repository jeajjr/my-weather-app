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

    CurrentConditions currentConditions = null;
    MultipleWeatherForecast threeHoursForecast = null;
    MultipleWeatherForecast dailyForecast = null;

    FragmentCurrentConditions fragmentCurrentConditions = null;
    FragmentThreeHoursForecast fragmentThreeHoursForecast = null;

    String cityName = "sao carlos, brazil";

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
    }

    @Override
    public String getCityName() {
        return "Syracuse, NY";
    }

    @Override
    public String getTemperatureUnit() {
        return "C";
    }

    @Override
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

                String currentData = WeatherClient.getInstance().getCurrentConditionsData(cityName);

                Log.d(TAG, "Parsing current conditions");
                currentConditions = DataParser.parseCurrentConditions(currentData);

                Log.d(TAG, "before sleep");
                try { Thread.sleep(5000); } catch (Exception e) { Log.d(TAG, "error sleep "); }
                Log.d(TAG, "after sleep");

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

    /*
        Activity methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(viewPagerAdapter);

        fetchCurrentConditions();
    }
}
