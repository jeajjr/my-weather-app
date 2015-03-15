package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import com.homespotter.weatherinternshipproject.data.WeatherParameters;

import java.util.ArrayList;

/**
 * Weather app template project.
 */
public class ActivityMain extends ActionBarActivity implements DataProviderInterface, DialogFragmentSearchCity.DialogFragmentSearchCityResultListener {
    private final static String TAG = "ActivityMain";

    // Weather related data
    private CurrentConditions currentConditions = null;
    private MultipleWeatherForecast threeHoursForecast = null;
    private MultipleWeatherForecast dailyForecast = null;

    // Fragments
    private FragmentCurrentConditions fragmentCurrentConditions = null;
    private FragmentThreeHoursForecast fragmentThreeHoursForecast = null;
    private FragmentDailyForecast fragmentDailyForecast = null;

    private boolean weatherDataIsValid;

    private static final int FRAGMENT_STATE_IDLE = 0;
    private static final int FRAGMENT_STATE_REFRESHING = 1;
    private static final int FRAGMENT_STATE_ERROR = 2;
    private int fragmentsState;

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

        switch (fragmentsState) {
            case FRAGMENT_STATE_IDLE:
                // If currentConditions is already fetched, send it to fragment
                if (currentConditions != null && weatherDataIsValid) {
                    fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
                }
                break;

            case FRAGMENT_STATE_REFRESHING:
                fragmentCurrentConditions.setRefreshing(true);
                break;

            case FRAGMENT_STATE_ERROR:
                fragmentCurrentConditions.setError();
                break;
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

        switch (fragmentsState) {
            case FRAGMENT_STATE_IDLE:
                // If currentConditions is already fetched, send it to fragment
                if (threeHoursForecast != null && weatherDataIsValid) {
                    fragmentThreeHoursForecast.setConditions(threeHoursForecast, settingsProfile);
                }
                break;

            case FRAGMENT_STATE_REFRESHING:
                fragmentThreeHoursForecast.setRefreshing(true);
                break;

            case FRAGMENT_STATE_ERROR:
                fragmentThreeHoursForecast.setError();
                break;
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

        switch (fragmentsState) {
            case FRAGMENT_STATE_IDLE:
                // If currentConditions is already fetched, send it to fragment
                if (dailyForecast != null && weatherDataIsValid) {
                    fragmentDailyForecast.setConditions(dailyForecast, settingsProfile);
                }
                break;

            case FRAGMENT_STATE_REFRESHING:
                fragmentDailyForecast.setRefreshing(true);
                break;

            case FRAGMENT_STATE_ERROR:
                fragmentDailyForecast.setError();
                break;
        }
    }

    @Override
    public void requestUpdate() {
        Log.d(TAG, "requestUpdate");

        setFragmentsState(FRAGMENT_STATE_REFRESHING);

        weatherDataIsValid = false;

        Intent serviceIntent = new Intent(this, WeatherDataService.class);
        serviceIntent.putExtra(WeatherDataService.ARG_CITYNAME, cityName);
        serviceIntent.putExtra(WeatherDataService.ARG_SETTINGS, settingsProfile);
        startService(serviceIntent);
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

        requestUpdate();

        toolboxTitle.setText(cityList.get(position));

        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void setFragmentsState(int state) {
        switch (state) {
            case FRAGMENT_STATE_IDLE:
                fragmentsState = FRAGMENT_STATE_IDLE;
                if (fragmentCurrentConditions != null) fragmentCurrentConditions.setRefreshing(false);
                if (fragmentThreeHoursForecast != null) fragmentThreeHoursForecast.setRefreshing(false);
                if (fragmentDailyForecast != null) fragmentDailyForecast.setRefreshing(false);
                break;
            case FRAGMENT_STATE_REFRESHING:
                fragmentsState = FRAGMENT_STATE_REFRESHING;
                if (fragmentCurrentConditions != null) fragmentCurrentConditions.setRefreshing(true);
                if (fragmentThreeHoursForecast != null) fragmentThreeHoursForecast.setRefreshing(true);
                if (fragmentDailyForecast != null) fragmentDailyForecast.setRefreshing(true);
                break;
            case FRAGMENT_STATE_ERROR:
                fragmentsState = FRAGMENT_STATE_ERROR;
                if (fragmentCurrentConditions != null) fragmentCurrentConditions.setError();
                if (fragmentThreeHoursForecast != null) fragmentThreeHoursForecast.setError();
                if (fragmentDailyForecast != null) fragmentDailyForecast.setError();
                break;
        }
    }

    public void onServiceResult(Intent intent) {
        Log.d(TAG, "onServiceResult");

        int result = intent.getExtras().getInt(WeatherDataService.ARG_RESULT);

        if (result == WeatherDataService.SERVICE_RESULT_OK) {
            Log.d(TAG, "service returned result OK");

            currentConditions =
                    (CurrentConditions) intent.getExtras().getSerializable(WeatherDataService.ARG_CURRENT_DATA);
            threeHoursForecast =
                    (MultipleWeatherForecast) intent.getExtras().getSerializable(WeatherDataService.ARG_THREE_HOUR_FORECAST);
            dailyForecast =
                    (MultipleWeatherForecast) intent.getExtras().getSerializable(WeatherDataService.ARG_DAILY_FORECAST);

            weatherDataIsValid = true;

            if (fragmentCurrentConditions != null)
                fragmentCurrentConditions.setConditions(currentConditions, settingsProfile);
            if (fragmentThreeHoursForecast != null)
                fragmentThreeHoursForecast.setConditions(threeHoursForecast, settingsProfile);
            if (fragmentDailyForecast != null)
                fragmentDailyForecast.setConditions(dailyForecast, settingsProfile);

            setFragmentsState(FRAGMENT_STATE_IDLE);
        }
        else if (result == WeatherDataService.SERVICE_RESULT_CONNECTION_ERROR) {
            Log.d(TAG, "service returned result connection error");

            setFragmentsState(FRAGMENT_STATE_ERROR);
            Toast.makeText(ActivityMain.this, getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
        }
        else if (result == WeatherDataService.SERVICE_RESULT_NO_INTERNET) {
            Log.d(TAG, "service returned result no internet");

            setFragmentsState(FRAGMENT_STATE_ERROR);
            Toast.makeText(ActivityMain.this, getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
        }
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

            requestUpdate();

            updateWidgets();
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

        // Registers the ResponseReceiver
        IntentFilter mStatusIntentFilter = new IntentFilter(WeatherDataService.BROADCAST_ACTION);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, mStatusIntentFilter);

        requestUpdate();
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG + "ResponseReceiver", "onReceive");

            ActivityMain.this.onServiceResult(intent);
        }
    }
}
