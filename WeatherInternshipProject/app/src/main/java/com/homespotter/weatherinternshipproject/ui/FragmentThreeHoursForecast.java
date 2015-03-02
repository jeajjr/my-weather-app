package com.homespotter.weatherinternshipproject.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThreeHoursForecast extends Fragment {
    public static final String TAG = "FragmentThreeHoursForecast";

    private RecyclerView recyclerView;
    private DataProviderInterface dataProvider;

    public FragmentThreeHoursForecast() {
        // Required empty public constructor
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
        View v = inflater.inflate(R.layout.fragment_three_hours_forecast, container, false);

        MultipleWeatherForecast data = new MultipleWeatherForecast();
        data.weatherInfoList.add(new HashMap<String, Object>());
        data.weatherInfoList.add(new HashMap<String, Object>());
        data.weatherInfoList.add(new HashMap<String, Object>());
        data.weatherInfoList.add(new HashMap<String, Object>());
        data.weatherInfoList.add(new HashMap<String, Object>());

        recyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecylerViewAdapter recylerViewAdapter = new RecylerViewAdapter(dataProvider, data, RecylerViewAdapter.THREE_HOUR_FORECAST);
        recyclerView.setAdapter(recylerViewAdapter);

        return v;
    }


}
