package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;

/**
 * Weather app template project.
 */
public class ActivityMain extends ActionBarActivity implements DataProviderInterface, DialogFragmentSearchCity.DialogFragmentSearchCityResultListener {
    private final static String TAG = "ActivityMain";

    private DrawerLayout drawerLayout;

    private CurrentConditions currentConditions = null;
    private boolean currentConditionsDataReady = false;
    private MultipleWeatherForecast threeHoursForecast = null;
    private boolean threeHoursForecastDataReady = false;
    private MultipleWeatherForecast dailyForecast = null;
    private boolean DailyForecastDataReady = false;

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

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PROGRESS_DIALOG_STACK_START = 2;
    private int progressDialogStack;

    /*
        Methods of DataProviderInterface
     */
    @Override
    public void setCurrentConditionsFragment(FragmentCurrentConditions fragmentCurrentConditions) {
        Log.d(TAG, "CurrCond frag is calling to get data");
        this.fragmentCurrentConditions = fragmentCurrentConditions;

        // If currentConditions is already fetched, send it to fragment
        if (currentConditions != null && currentConditionsDataReady) {

            fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
        }
    }

    @Override
    public void setThreeHoursForecastFragment(FragmentThreeHoursForecast fragmentThreeHoursForecast) {
        Log.d(TAG, "3Hour frag is calling to get data");
        this.fragmentThreeHoursForecast = fragmentThreeHoursForecast;

        // If threeHoursForecast is already fetched, send it to fragment
        if (threeHoursForecast != null && threeHoursForecastDataReady)
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
                    currentConditionsDataReady = false;
                    Log.d(TAG, "Getting current conditions");

                    //try { Thread.sleep(5000); } catch (Exception e) { Log.d(TAG, "error sleep "); }

                    Log.d(TAG, "fetchCurrentConditions with " + settingsProfile + " for " + cityName);

                    try {
                        String currentData = WeatherClient.getInstance().getCurrentConditionsData(cityName, settingsProfile.getUnits());
                        Log.d(TAG, "Current raw data " + currentData);//TODO
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Check if data is not null
                            if (currentConditions != null) {
                                currentConditions.temperatureUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                                        TEMPERATURE_UNIT_IMPERIAL : TEMPERATURE_UNIT_METRIC;
                                currentConditions.speedUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                                        SPEED_UNIT_IMPERIAL : SPEED_UNIT_METRIC;

                                currentConditionsDataReady = true;

                                // If fragmentCurrentConditions has been created and is waiting for the data, send it
                                if (fragmentCurrentConditions != null) {
                                    fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
                                }
                            }
                            else {
                                Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                            }

                            decreaseProgressDialogStack();
                        }
                    });
                    Log.d(TAG, "end fetchCurrentConditions");
                }
            }.start();
        }
        else {
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
            decreaseProgressDialogStack();
        }
    }

    public void fetchThreeHoursForecast() {
        if (checkInternetAccess()) {
        new Thread() {
            public void run() {
                threeHoursForecastDataReady = false;
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Check if data is not null
                        if (threeHoursForecast != null) {
                            threeHoursForecast.temperatureUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                                    TEMPERATURE_UNIT_IMPERIAL : TEMPERATURE_UNIT_METRIC;
                            threeHoursForecast.speedUnit = (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL) ?
                                    SPEED_UNIT_IMPERIAL : SPEED_UNIT_METRIC;

                            // If fragmentCurrentConditions has been created and is waiting for the data, send it
                            if (fragmentCurrentConditions != null) {
                                fragmentThreeHoursForecast.setConditions(threeHoursForecast, settingsProfile);
                            }
                        }
                        else {
                            Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                        }

                        threeHoursForecastDataReady = true;

                        decreaseProgressDialogStack();
                    }
                });
                Log.d(TAG, "end fetchThreeHoursForecast");
            }
        }.start();
        }
        else {
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
            decreaseProgressDialogStack();
        }
    }

    private void decreaseProgressDialogStack() {
        progressDialogStack--;
        if (progressDialogStack == 0)
            mSwipeRefreshLayout.setRefreshing(false);
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

        // check if the device is a tablet or not
        if (findViewById(R.id.container1) == null) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);

            ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
            mViewPager.setAdapter(viewPagerAdapter);
        }
        else {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container1, new FragmentCurrentConditions())
                        .commit();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container2, new FragmentThreeHoursForecast())
                        .commit();
            }
        }

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

            @Override
            public void onItemLongClick(int uniqueID) {
                processDrawerLongClick(uniqueID);
            }

            @Override
            public void onSetMainCity(int position) {
                Log.d(TAG, "Activity got setMainCity on city " + position);

                int oldPosition = 0;
                String newMainCity = cityList.get(position);

                for (int i = 0; i < cityList.size(); i++)
                    if (cityList.get(i).compareTo(cityList.get(position)) == 0) {
                        oldPosition = i;
                        break;
                    }

                cityList.remove(oldPosition);
                cityList.add(0, newMainCity);

                drawerRecyclerViewAdapter.dataSetChanged(cityList);
                changeCurrentCity(0);

                //drawerRecyclerViewAdapter.notifyMainCityChanged(oldPosition);
            }
        });
        drawerList.setAdapter(drawerRecyclerViewAdapter);

        drawerLayout.setDrawerListener(drawerToggle);
        // only open navigation drawer via button on toolbar
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ImageView drawerButton = (ImageView) findViewById(R.id.imageViewToolboxLeftButton);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        progressDialogStack = PROGRESS_DIALOG_STACK_START;
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressDialogStack = PROGRESS_DIALOG_STACK_START;
                mSwipeRefreshLayout.setRefreshing(true);

                fetchCurrentConditions();
                fetchThreeHoursForecast();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        if (mSwipeRefreshLayout != null)
            Log.d(TAG, "found mSwipeRefreshLayout");
        else
            Log.d(TAG, "mSwipeRefreshLayout null");

        toolboxTitle = (TextView) findViewById(R.id.textViewToolboxTitle);
        toolboxTitle.setText(cityName);

        fetchCurrentConditions();
        fetchThreeHoursForecast();
    }

    private static final int SETTING_REQUEST = 0;

    public void processDrawerLongClick(final int uniqueID) {
        if ((uniqueID & DrawerItemsLister.ITEM_CITY_MASK) == DrawerItemsLister.ITEM_CITY_MASK) {
            Log.d(TAG, "long click on city " + uniqueID);

            // Create dialog to delete city
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(getString(R.string.delete_city));
            alertDialogBuilder.setMessage(getString(R.string.delete_city_confirmation) + " " +
                                    cityList.get(uniqueID - DrawerItemsLister.ITEM_CITY_MASK) + "?");

            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_ok),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    cityList.remove(uniqueID - DrawerItemsLister.ITEM_CITY_MASK);
                    FilesHandler.getInstance().setCityList(ActivityMain.this, cityList);

                    if (cityList.size() != 0) {
                        drawerRecyclerViewAdapter.setCurrentCity(0);
                        drawerRecyclerViewAdapter.dataSetChanged(cityList);
                        changeCurrentCity(0);
                    }
                    else {
                        startActivity(new Intent(ActivityMain.this, ActivityStart.class));
                    }
                }
            });

            alertDialogBuilder.setNeutralButton(getString(R.string.dialog_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void processDrawerClick(int uniqueID) {
        Log.d(TAG, "activity received click on item " + uniqueID);

        // if it is a city item
        if ((uniqueID & DrawerItemsLister.ITEM_CITY_MASK) == DrawerItemsLister.ITEM_CITY_MASK) {
            Log.d(TAG, "click on city " + uniqueID);

            changeCurrentCity(uniqueID - DrawerItemsLister.ITEM_CITY_MASK);
        }
        else {
            Intent intent;

            switch (uniqueID) {
                case DrawerItemsLister.ADD_NEW_CITY:
                    DialogFragmentSearchCity.newInstance(false).show(getSupportFragmentManager(), null);
                    break;
                case DrawerItemsLister.SETTINGS:
                    intent = new Intent(this, ActivitySettings.class);
                    startActivityForResult(intent, SETTING_REQUEST);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    break;
                case DrawerItemsLister.ABOUT_THIS_APP:
                    intent = new Intent(this, ActivityAboutApp.class);
                    startActivityForResult(intent, SETTING_REQUEST);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult");

        if (resultCode == Activity.RESULT_OK && requestCode == SETTING_REQUEST) {
            settingsProfile = (SettingsProfile) data.getSerializableExtra("settings");
            Log.d(TAG,"got settings " + settingsProfile);

            mSwipeRefreshLayout.setRefreshing(true);
            progressDialogStack = PROGRESS_DIALOG_STACK_START;

            fetchCurrentConditions();
            fetchThreeHoursForecast();
        }
    }

    public void changeCurrentCity(int position) {
        cityName = cityList.get(position);

        mSwipeRefreshLayout.setRefreshing(true);
        progressDialogStack = PROGRESS_DIALOG_STACK_START;

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
        drawerRecyclerViewAdapter.setCurrentCity(cityList.size() - 1);
        drawerRecyclerViewAdapter.dataSetChanged(cityList);

        changeCurrentCity(cityList.size()-1);
    }
}
