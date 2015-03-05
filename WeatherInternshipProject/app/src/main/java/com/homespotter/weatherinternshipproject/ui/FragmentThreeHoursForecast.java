package com.homespotter.weatherinternshipproject.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.MultipleWeatherForecast;
import com.homespotter.weatherinternshipproject.data.WeatherParameters;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThreeHoursForecast extends Fragment {
    public static final String TAG = "FragmentThreeHoursForecast";

    private RecyclerView recyclerView;
    private RecylerViewAdapter recylerViewAdapter;

    FrameLayout mainLayout;

    private DataProviderInterface dataProvider;
    private MultipleWeatherForecast multipleWeatherForecast;

    private ProgressDialog progressDialog;

    public FragmentThreeHoursForecast() {
        // Required empty public constructor
    }

    public void setConditions (MultipleWeatherForecast multipleWeatherForecast) {
        Log.d(TAG, "setConditions");

        this.multipleWeatherForecast = multipleWeatherForecast;

        createRecyclerViewAdapter();
    }

    public void createRecyclerViewAdapter() {
        recylerViewAdapter = new RecylerViewAdapter(getActivity(), dataProvider, multipleWeatherForecast, RecylerViewAdapter.THREE_HOUR_FORECAST);
        recyclerView.setAdapter(recylerViewAdapter);

        progressDialog.dismiss();
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
        View v = inflater.inflate(R.layout.fragment_three_hours_forecast, container, false);

        // show progress dialog while data is being fetched
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        // fade list while loading
        mainLayout = (FrameLayout) v.findViewById(R.id.layout);
        mainLayout.getForeground().setAlpha(200);

        recyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dataProvider.setThreeHoursForecastFragment(this);

        return v;
    }
}
