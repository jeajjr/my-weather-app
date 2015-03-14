package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
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

    //TODO remove
    public static boolean debugConnection = false;

    // Weather related data
    private CurrentConditions currentConditions = null;
    private boolean currentConditionsDataReady = false;
    private MultipleWeatherForecast threeHoursForecast = null;
    private boolean threeHoursForecastDataReady = false;
    private MultipleWeatherForecast dailyForecast = null;
    private boolean dailyForecastDataReady = false;

    // Fragments
    private FragmentCurrentConditions fragmentCurrentConditions = null;
    private FragmentThreeHoursForecast fragmentThreeHoursForecast = null;
    private FragmentDailyForecast fragmentDailyForecast = null;

    // Current city and cities list
    private String cityName;
    private ArrayList<String> cityList;

    // Navigation drawer
    private DrawerLayout drawerLayout;
    private AdapterDrawerMenuRecyclerView drawerRecyclerViewAdapter;

    private SettingsProfile settingsProfile;

    private TextView toolboxTitle;

    /*
        Methods of DataProviderInterface
     */

    /**
     * Function called by the CurrentConditionsFragment to send its instance to the MainActivty.
     * @param fragmentCurrentConditions: the CurrentConditionsFragment instance.
     */
    @Override
    public void setCurrentConditionsFragment(FragmentCurrentConditions fragmentCurrentConditions) {
        Log.d(TAG, "CurrCond frag is calling to get data");
        this.fragmentCurrentConditions = fragmentCurrentConditions;

        // If currentConditions is already fetched, send it to fragment
        if (currentConditions != null && currentConditionsDataReady) {
            fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
            fragmentCurrentConditions.setRefreshing(false);
        }
        else {
            fragmentCurrentConditions.setRefreshing(true);
        }
    }

    /**
     * Function called by the FragmentThreeHoursForecast to send its instance to the MainActivty.
     * @param fragmentThreeHoursForecast: the FragmentThreeHoursForecast instance.
     */
    @Override
    public void setThreeHoursForecastFragment(FragmentThreeHoursForecast fragmentThreeHoursForecast) {
        Log.d(TAG, "3Hour frag is calling to get data");
        this.fragmentThreeHoursForecast = fragmentThreeHoursForecast;

        // If threeHoursForecast is already fetched, send it to fragment
        if (threeHoursForecast != null && threeHoursForecastDataReady) {
            fragmentThreeHoursForecast.setConditions(threeHoursForecast, settingsProfile);
            fragmentThreeHoursForecast.setRefreshing(false);
        }
        else {
            Log.d(TAG, "refresh in set");
            fragmentThreeHoursForecast.setRefreshing(true);
        }
    }

    /**
     * Function called by the FragmentDailyForecast to send its instance to the MainActivty.
     * @param fragmentDailyForecast: the FragmentDailyForecast instance.
     */
    @Override
    public void setDailyForecastFragment(FragmentDailyForecast fragmentDailyForecast) {
        Log.d(TAG, "daily frag is calling to get data");
        this.fragmentDailyForecast = fragmentDailyForecast;

        // If threeHoursForecast is already fetched, send it to fragment
        if (dailyForecast != null && dailyForecastDataReady) {
            fragmentDailyForecast.setConditions(dailyForecast, settingsProfile);
            fragmentDailyForecast.setRefreshing(false);
        }
        else {
            fragmentDailyForecast.setRefreshing(true);
        }
    }

    @Override
    public void requestUpdate() {
        fetchCurrentConditions();
        fetchThreeHoursForecast();
        fetchDailyForecast();
    }

    /*
        Methods of dialogFragmentSearchCity.DialogFragmentSearchCityResultListener interface
     */
    /**
     * Callback function of the search city DialogFragment.
     */
    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");

        cityList = FilesHandler.getInstance().getSavedCities(this);
        drawerLayout.closeDrawer(Gravity.LEFT);
        drawerRecyclerViewAdapter.setCurrentCity(cityList.size() - 1);
        drawerRecyclerViewAdapter.dataSetChanged(cityList);

        changeCurrentCity(cityList.size()-1);
    }

    /*
        Local methods.
     */

    /**
     * Check if internet access is available.
     * @return true if available, false otherwise.
     */
    private boolean checkInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //TODO remove
        return true;
        //return (cm.getActiveNetworkInfo() != null);
    }

    /**
     * Creates a thread to fetch the current conditions of the current city.
     */
    public void fetchCurrentConditions() {
        if (checkInternetAccess()) {
            new Thread() {
                public void run() {
                    currentConditionsDataReady = false;
                    if (fragmentCurrentConditions != null) {
                        Log.d(TAG, "refresh in thread");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentCurrentConditions.setRefreshing(true);
                            }
                        });
                    }

                    Log.d(TAG, "Getting current conditions");

                    Log.d(TAG, "fetchCurrentConditions with " + settingsProfile + " for " + cityName);

                    //TODO remove
                    if (debugConnection) {
                        String currentData = "{\"coord\":{\"lon\":-0.13,\"lat\":51.51},\"sys\":{\"message\":0.0774,\"country\":\"GB\",\"sunrise\":1426227494,\"sunset\":1426269695},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":278.742,\"temp_min\":278.742,\"temp_max\":278.742,\"pressure\":1029.22,\"sea_level\":1039.72,\"grnd_level\":1029.22,\"humidity\":71},\"wind\":{\"speed\":4.12,\"deg\":54.501},\"clouds\":{\"all\":32},\"dt\":1426282599,\"id\":2643743,\"name\":\"London\",\"cod\":200}";
                        currentConditions = DataParser.parseCurrentConditions(currentData);

                        //try { Thread.sleep(1500 + (long) (Math.random() * 1000.0));} catch (Exception e) {}
                    }
                    else {
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
                    }


                    Log.d(TAG, "Parsing current conditions");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Check if data is not null
                            if (currentConditions != null) {
                                currentConditions.temperatureUnit = settingsProfile.getTemperatureUnitString();
                                currentConditions.speedUnit = settingsProfile.getSpeedUnitString();

                                currentConditionsDataReady = true;

                                // If fragmentCurrentConditions has been created and is waiting for the data, send it
                                if (fragmentCurrentConditions != null) {
                                    fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
                                }
                            }
                            else {
                                Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                            }

                            if (fragmentDailyForecast != null) {
                                fragmentDailyForecast.setRefreshing(false);
                            }
                        }
                    });
                    Log.d(TAG, "end fetchCurrentConditions");
                }
            }.start();
        }
        else {
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
            if (fragmentDailyForecast != null) {
                fragmentDailyForecast.setRefreshing(false);
            }
        }
    }

    /**
     * Creates a thread to fetch the three hours forecast of the current city.
     */
    public void fetchThreeHoursForecast() {
        if (checkInternetAccess()) {
        new Thread() {
            public void run() {
                threeHoursForecastDataReady = false;

                if (fragmentThreeHoursForecast != null) {
                    Log.d(TAG, "refresh in thread");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentThreeHoursForecast.setRefreshing(true);
                        }
                    });
                }

                Log.d(TAG, "Getting 3 hour forecast");

                // TODO remove

                if (debugConnection) {
                    String data = "{\"cod\":\"200\",\"message\":9.4357,\"city\":{\"id\":2643743,\"name\":\"London\",\"coord\":{\"lon\":-0.12574,\"lat\":51.50853},\"country\":\"GB\",\"population\":0,\"sys\":{\"population\":0}},\"cnt\":2,\n" +
                            "\"list\":[{\"dt\":1426269600,\"main\":{\"temp\":278.74,\"temp_min\":278.74,\"temp_max\":281.083,\"pressure\":1027.39,\"sea_level\":1037.72,\"grnd_level\":1027.39,\"humidity\":61,\"temp_kf\":-2.34},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":56},\"wind\":{\"speed\":3.92,\"deg\":56.0004},\"rain\":{\"3h\":0},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2015-03-13 18:00:00\"},{\"dt\":1426280400,\"main\":{\"temp\":276.52,\"temp_min\":276.52,\"temp_max\":278.742,\"pressure\":1029.22,\"sea_level\":1039.72,\"grnd_level\":1029.22,\"humidity\":71,\"temp_kf\":-2.22},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"clouds\":{\"all\":32},\"wind\":{\"speed\":4.12,\"deg\":54.501},\"rain\":{\"3h\":0},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2015-03-13 21:00:00\"}]}";
                    threeHoursForecast = DataParser.parseThreeHourForecast(data);

                    //try { Thread.sleep(1500 + (long) (Math.random() * 1000.0)); } catch (Exception e) {}
                }
                else {
                    try {
                        String data = WeatherClient.getInstance().getThreeHoursForecastData(cityName, settingsProfile.getUnits());
                        threeHoursForecast = DataParser.parseThreeHourForecast(data);
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Check if data is not null
                        if (threeHoursForecast != null) {
                            threeHoursForecast.temperatureUnit = settingsProfile.getTemperatureUnitString();
                            threeHoursForecast.speedUnit = settingsProfile.getSpeedUnitString();

                            // If fragmentCurrentConditions has been created and is waiting for the data, send it
                            if (fragmentThreeHoursForecast != null) {
                                fragmentThreeHoursForecast.setConditions(threeHoursForecast, settingsProfile);
                            }
                        }
                        else {
                            Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                        }

                        if (fragmentThreeHoursForecast != null) {
                            fragmentThreeHoursForecast.setRefreshing(false);
                        }
                        threeHoursForecastDataReady = true;
                    }
                });
                Log.d(TAG, "end fetchThreeHoursForecast");
            }
        }.start();
        }
        else {
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
            if (fragmentThreeHoursForecast != null) {
                fragmentThreeHoursForecast.setRefreshing(false);
            }
        }
    }

    /**
     * Creates a thread to fetch the three hours forecast of the current city.
     */
    public void fetchDailyForecast() {
        if (checkInternetAccess()) {
            new Thread() {
                public void run() {
                    dailyForecastDataReady = false;
                    if (fragmentDailyForecast != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentDailyForecast.setRefreshing(true);
                            }
                        });
                    }

                    Log.d(TAG, "Getting daily forecast");

                    // TODO remove
                    if (debugConnection) {
                        //try { Thread.sleep(1500 + (long) (Math.random() * 1000.0)); } catch (Exception e) {}

                        String data = "{\"cod\":\"200\",\"message\":0.6164,\"city\":{\"id\":2643743,\"name\":\"London\",\"coord\":{\"lon\":-0.12574,\"lat\":51.50853},\"country\":\"GB\",\"population\":0,\"sys\":{\"population\":0}},\"cnt\":7,\"list\":[{\"dt\":1426248000,\"temp\":{\"day\":280.85,\"min\":276.51,\"max\":280.85,\"night\":276.51,\"eve\":280.85,\"morn\":280.85},\"pressure\":1031.66,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.77,\"deg\":59,\"clouds\":29,\"rain\":1.65},{\"dt\":1426334400,\"temp\":{\"day\":279.7,\"min\":276.46,\"max\":279.92,\"night\":278.16,\"eve\":278.77,\"morn\":276.46},\"pressure\":1037.73,\"humidity\":83,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":5.62,\"deg\":54,\"clouds\":64,\"rain\":0.41},{\"dt\":1426420800,\"temp\":{\"day\":279.76,\"min\":276.62,\"max\":279.89,\"night\":276.62,\"eve\":278.64,\"morn\":277.94},\"pressure\":1035.61,\"humidity\":86,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":6.41,\"deg\":52,\"clouds\":88,\"rain\":0.45},{\"dt\":1426507200,\"temp\":{\"day\":280.51,\"min\":277.43,\"max\":280.82,\"night\":277.43,\"eve\":280.32,\"morn\":277.56},\"pressure\":1030.74,\"humidity\":91,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":3.57,\"deg\":75,\"clouds\":88,\"rain\":0.62},{\"dt\":1426593600,\"temp\":{\"day\":280.69,\"min\":276.12,\"max\":281.54,\"night\":276.93,\"eve\":280.46,\"morn\":276.12},\"pressure\":1036.07,\"humidity\":94,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.06,\"deg\":335,\"clouds\":92},{\"dt\":1426680000,\"temp\":{\"day\":282.54,\"min\":272.98,\"max\":282.54,\"night\":275.73,\"eve\":281.42,\"morn\":272.98},\"pressure\":1042.47,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.56,\"deg\":75,\"clouds\":10},{\"dt\":1426766400,\"temp\":{\"day\":281.47,\"min\":274.95,\"max\":281.47,\"night\":277.85,\"eve\":281.25,\"morn\":274.95},\"pressure\":1030.88,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.75,\"deg\":60,\"clouds\":79,\"rain\":0.62}]}";
                        dailyForecast = DataParser.parseDailyForecast(data);

                    }
                    else {
                        try {
                            String data = WeatherClient.getInstance().getDailyForecastData(cityName, settingsProfile.getUnits());
                            dailyForecast = DataParser.parseDailyForecast(data);
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Check if data is not null
                            if (dailyForecast != null) {
                                dailyForecast.temperatureUnit = settingsProfile.getTemperatureUnitString();
                                dailyForecast.speedUnit = settingsProfile.getSpeedUnitString();

                                // If fragmentCurrentConditions has been created and is waiting for the data, send it
                                if (fragmentDailyForecast != null) {
                                    fragmentDailyForecast.setConditions(dailyForecast, settingsProfile);
                                }
                            }
                            else {
                                Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
                            }

                            if (fragmentDailyForecast != null) {
                                fragmentDailyForecast.setRefreshing(false);
                            }
                            dailyForecastDataReady = true;
                        }
                    });
                    Log.d(TAG, "end fetchDailyForecast");
                }
            }.start();
        }
        else {
            Toast.makeText(this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
            if (fragmentDailyForecast != null) {
                fragmentDailyForecast.setRefreshing(false);
            }
        }
    }

    public void updateWidgets() {
        Log.d(TAG, "sending update widgets broadcast");

        Intent intent = new Intent(this, WeatherAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.xml.widget};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
    /**
     * Process a click on the navigation drawer item with the given uniqueID.
     * @param uniqueID: uniqueID of the clicked drawer item.
     */
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
                    startActivity(intent);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    break;
            }
        }
    }

    /**
     * Process a long click on the navigation drawer item with the given uniqueID.
     * @param uniqueID: uniqueID of the long clicked drawer item.
     */
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

    private static final int SETTING_REQUEST = 0;

    /**
     * Change the current city which current weather and forecast are being shown.
     * @param position: position of the city on the ArrayList<String> cityList.
     */
    public void changeCurrentCity(int position) {
        cityName = cityList.get(position);

        drawerRecyclerViewAdapter.setCurrentCity(position);

        fetchCurrentConditions();
        fetchThreeHoursForecast();
        fetchDailyForecast();

        toolboxTitle.setText(cityList.get(position));

        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    /*
        Activity methods
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult");

        if (resultCode == Activity.RESULT_OK && requestCode == SETTING_REQUEST) {
            settingsProfile = (SettingsProfile) data.getSerializableExtra("settings");
            Log.d(TAG,"got settings " + settingsProfile);

            fetchCurrentConditions();
            fetchThreeHoursForecast();
            fetchDailyForecast();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this activity is only called when there is a saved city, so there is no need to check it
        cityList = FilesHandler.getInstance().getSavedCities(this);
        cityName = cityList.get(0);
        settingsProfile = FilesHandler.getInstance().getSettingProfile(this);

        if (settingsProfile == null)
            Log.d(TAG, "settings null");
        else
            Log.d(TAG, "settings null not");

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

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container3, new FragmentDailyForecast())
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
                R.string.close_drawer) {
            // Only lock drawer when it is closed
            public void onDrawerOpened(View view){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            public void onDrawerClosed(View view) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        };

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

                FilesHandler.getInstance().setCityList(ActivityMain.this, cityList);

                drawerRecyclerViewAdapter.dataSetChanged(cityList);
                changeCurrentCity(0);

                updateWidgets();
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

        toolboxTitle = (TextView) findViewById(R.id.textViewToolboxTitle);
        toolboxTitle.setText(cityName);

        fetchCurrentConditions();
        fetchThreeHoursForecast();
        fetchDailyForecast();
    }
}
