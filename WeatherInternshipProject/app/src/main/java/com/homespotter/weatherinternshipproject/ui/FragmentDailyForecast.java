package com.homespotter.weatherinternshipproject.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDailyForecast extends Fragment {
    public static final String TAG = "FragmentThreeHoursForecast";

    private RecyclerView recyclerView;
    private AdapterForecastRecyclerView adapterForecastRecyclerView;

    FrameLayout mainLayout;

    private DataProviderInterface dataProvider;
    private MultipleWeatherForecast multipleWeatherForecast;
    private SettingsProfile settingsProfile;

    public FragmentDailyForecast() {
        // Required empty public constructor
    }

    public void setConditions (MultipleWeatherForecast multipleWeatherForecast, SettingsProfile settingsProfile) {
        Log.d(TAG, "setConditions");

        this.multipleWeatherForecast = multipleWeatherForecast;
        this.settingsProfile = settingsProfile;

        createRecyclerViewAdapter();
    }

    public void createRecyclerViewAdapter() {
        adapterForecastRecyclerView = new AdapterForecastRecyclerView(getActivity(), multipleWeatherForecast, settingsProfile, AdapterForecastRecyclerView.DAILY_FORECAST);
        recyclerView.setAdapter(adapterForecastRecyclerView);

        mainLayout.getForeground().setAlpha(0);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dataProvider = (DataProviderInterface) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_daily_forecast, container, false);

        // fade list while loading
        mainLayout = (FrameLayout) v.findViewById(R.id.layout);
        mainLayout.getForeground().setAlpha(200);

        recyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dataProvider.setDailyForecastFragment(this);

        return v;
    }


}
