package com.homespotter.weatherinternshipproject.ui;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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
    public static final String TAG = "FragmentDailyForecast";

    private RecyclerView recyclerView;
    private AdapterForecastRecyclerView adapterForecastRecyclerView;

    Drawable fadingForeground;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DataProviderInterface dataProvider;
    private MultipleWeatherForecast multipleWeatherForecast;
    private SettingsProfile settingsProfile;

    public FragmentDailyForecast() {
        // Required empty public constructor
    }

    public void setError() {
        mSwipeRefreshLayout.setRefreshing(false);
        fadingForeground.setAlpha(200);
    }

    public void setRefreshing(boolean refreshing) {
        Log.d(TAG, "setRefreshing: " + refreshing);

        mSwipeRefreshLayout.setRefreshing(refreshing);

        if (refreshing) {
            fadingForeground.setAlpha(200);
        }
        else {
            fadingForeground.setAlpha(0);
        }
    }

    public void setConditions (MultipleWeatherForecast multipleWeatherForecast, SettingsProfile settingsProfile) {
        Log.d(TAG, "setConditions");

        this.multipleWeatherForecast = multipleWeatherForecast;
        this.settingsProfile = settingsProfile;

        createRecyclerViewAdapter();
    }

    public void createRecyclerViewAdapter() {
        Log.d(TAG, "createRecyclerViewAdapter");

        adapterForecastRecyclerView = new AdapterForecastRecyclerView(getActivity(),
                multipleWeatherForecast, settingsProfile, AdapterForecastRecyclerView.DAILY_FORECAST);
        recyclerView.setAdapter(adapterForecastRecyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mSwipeRefreshLayout.setEnabled(
                        ((LinearLayoutManager) recyclerView.getLayoutManager())
                                .findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
        setRefreshing(false);
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
        fadingForeground = ((FrameLayout) v.findViewById(R.id.layout)).getForeground();
        fadingForeground.setAlpha(200);

        recyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataProvider.requestUpdate();
            }
        });

        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);

        dataProvider.setDailyForecastFragment(this);

        return v;
    }


}
