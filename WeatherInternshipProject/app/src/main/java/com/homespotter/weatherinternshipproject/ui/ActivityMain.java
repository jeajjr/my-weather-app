package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.CurrentConditions;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.FilesHandler;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;
import com.homespotter.weatherinternshipproject.data.WeatherClient;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Weather app template project.
 */
public class ActivityMain extends ActionBarActivity implements DataProviderInterface, DialogFragmentSearchCity.DialogFragmentSearchCityResultListener {
    private final static String TAG = "ActivityMain";

    DrawerLayout drawerLayout;

    private CurrentConditions currentConditions = null;
    private MultipleWeatherForecast threeHoursForecast = null;
    private MultipleWeatherForecast dailyForecast = null;

    private FragmentCurrentConditions fragmentCurrentConditions = null;
    private FragmentThreeHoursForecast fragmentThreeHoursForecast = null;

    private String cityName;
    private ArrayList<String> cityList;

    private AdapterDrawerMenuRecyclerView drawerRecyclerViewAdapter;

    private SettingsProfile settingsProfile;

    private final String SPEED_UNIT_IMPERIAL = "mph";
    private final String SPEED_UNIT_METRIC = "km/h";
    private final String TEMPERATURE_UNIT_IMPERIAL = "F";
    private final String TEMPERATURE_UNIT_METRIC = "C";

    private TextView toolboxTitle;

    /*
        Methods of DataProviderInterface
     */
    @Override
    public void setCurrentConditionsFragment(FragmentCurrentConditions fragmentCurrentConditions) {
        this.fragmentCurrentConditions = fragmentCurrentConditions;

        // If currentConditions is already fetched, send it to fragment
        if (currentConditions != null)
            fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
    }

    @Override
    public void setThreeHoursForecastFragment(FragmentThreeHoursForecast fragmentThreeHoursForecast) {
        this.fragmentThreeHoursForecast = fragmentThreeHoursForecast;

        // If threeHoursForecast is already fetched, send it to fragment
        if (threeHoursForecast != null)
            fragmentThreeHoursForecast.setConditions(threeHoursForecast, settingsProfile);
    }

    private boolean checkInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    /*
        Local methods.
     */
    public void fetchCurrentConditions() {
        if (checkInternetAccess()) {
            new Thread() {
                public void run() {
                    Log.d(TAG, "Getting current conditions");

                    //try { Thread.sleep(5000); } catch (Exception e) { Log.d(TAG, "error sleep "); }

                    Log.d(TAG, "fetchCurrentConditions with " + settingsProfile);


                    try {
                        String currentData = WeatherClient.getInstance().getCurrentConditionsData(cityName, settingsProfile.getUnits());
                        currentConditions = DataParser.parseCurrentConditions(currentData);
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    Log.d(TAG, "Parsing current conditions");

                    if (currentConditions != null) {
                        currentConditions.temperatureUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                                TEMPERATURE_UNIT_IMPERIAL : TEMPERATURE_UNIT_METRIC;
                        currentConditions.speedUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                                SPEED_UNIT_IMPERIAL : SPEED_UNIT_METRIC;
                    }

                    // If fragmentCurrentConditions has been created and is waiting for the data, send it
                    if (fragmentCurrentConditions != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
                            }
                        });
                    }
                }
            }.start();
        }
        else
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
    }

    public void fetchThreeHoursForecast() {
        if (checkInternetAccess()) {
        new Thread() {
            public void run() {
                Log.d(TAG, "Getting 3 hour forecast");

                //try { Thread.sleep(5000); } catch (Exception e) { Log.d(TAG, "error sleep "); }

                try {
                    String data = WeatherClient.getInstance().getFiveDaysForecastData(cityName, settingsProfile.getUnits());
                    threeHoursForecast = DataParser.parseFiveDaysForecast(data);
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (threeHoursForecast != null) {
                    threeHoursForecast.temperatureUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                            TEMPERATURE_UNIT_IMPERIAL : TEMPERATURE_UNIT_METRIC;
                    threeHoursForecast.speedUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                            SPEED_UNIT_IMPERIAL : SPEED_UNIT_METRIC;
                }
                
                // If fragmentCurrentConditions has been created and is waiting for the data, send it
                if (fragmentCurrentConditions != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentThreeHoursForecast.setConditions(threeHoursForecast, settingsProfile);
                        }
                    });
                }
            }
        }.start();
        }
        else
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
    }

    /*
            Activity methods
         */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this activity is only called when there is a saved city, so there is no need to check it
        cityList = FilesHandler.getInstance().getSavedCities(this);
        cityName = cityList.get(0);
        settingsProfile = FilesHandler.getInstance().getSettingProfile(this);

        if (settingsProfile == null) {
            Log.d(TAG, "settingsProfile null");
        }
        else {
            Log.d(TAG, "settingsProfile not null");
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(viewPagerAdapter);

        fetchCurrentConditions();
        fetchThreeHoursForecast();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer);

        RecyclerView drawerList = (RecyclerView) findViewById(R.id.drawer_list);
        drawerList.setLayoutManager(new LinearLayoutManager(this));
        drawerRecyclerViewAdapter =
                new AdapterDrawerMenuRecyclerView(this, cityList, settingsProfile);

        drawerRecyclerViewAdapter.setOnDrawerItemClickListener(new AdapterDrawerMenuRecyclerView.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(int uniqueID) {
                processDrawerClick(uniqueID);
            }
        });
        drawerList.setAdapter(drawerRecyclerViewAdapter);

        drawerLayout.setDrawerListener(drawerToggle);
        // only open navigation drawer via button on toolbar
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ImageView drawerButton = (ImageView) findViewById(R.id.imageViewToolboxButton);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        toolboxTitle = (TextView) findViewById(R.id.textViewToolboxTitle);
        toolboxTitle.setText(cityName);
    }

    private static final int SETTING_REQUEST = 0;

    public void processDrawerClick(int uniqueID) {
        Log.d(TAG, "activity received click on item " + uniqueID);

        // if it is a city item
        if ((uniqueID & DrawerItemsLister.ITEM_CITY_MASK) == DrawerItemsLister.ITEM_CITY_MASK) {
            Log.d(TAG, "click on city " + uniqueID);

            changeCurrentCity(uniqueID - DrawerItemsLister.ITEM_CITY_MASK);
        }
        else {
            switch (uniqueID) {
                case DrawerItemsLister.ADD_NEW_CITY:
                    DialogFragmentSearchCity.newInstance(false).show(getSupportFragmentManager(), null);
                    break;
                case DrawerItemsLister.SETTINGS:
                    Intent intent = new Intent(this, ActivitySettings.class);
                    startActivityForResult(intent, SETTING_REQUEST);
                    break;
                case DrawerItemsLister.ABOUT_THIS_APP:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult");

        if (resultCode == Activity.RESULT_OK && requestCode == SETTING_REQUEST) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            settingsProfile = (SettingsProfile) data.getSerializableExtra("settings");
            Log.d(TAG,"got settings " + settingsProfile);

            fetchCurrentConditions();
            fetchThreeHoursForecast();
        }
    }

    public void changeCurrentCity(int position) {
        cityName = cityList.get(position);

        fetchCurrentConditions();
        fetchThreeHoursForecast();

        toolboxTitle.setText(cityList.get(position));

        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");

        cityList = FilesHandler.getInstance().getSavedCities(this);
        drawerLayout.closeDrawer(Gravity.LEFT);
        drawerRecyclerViewAdapter.dataSetChanged(cityList);

        changeCurrentCity(cityList.size()-1);
    }
}
