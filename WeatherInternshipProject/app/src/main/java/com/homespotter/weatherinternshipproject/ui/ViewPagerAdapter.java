package com.homespotter.weatherinternshipproject.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.homespotter.weatherinternshipproject.R;

import java.util.Locale;

/**
 * Created by José Ernesto on 01/03/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    FragmentManager fm;
    Context context;
    int currentPage;

    public ViewPagerAdapter (FragmentManager fm, Context context) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }



    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentCurrentConditions();
            case 1:
                return new FragmentThreeHoursForecast();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        String name;

        switch (position) {
            case 0:
                name = context.getResources().getString(R.string.tab_title_current_conditions);
                break;
            case 1:
                name = context.getResources().getString(R.string.tab_title_three_hours_forecast);
                break;
            case 2:
                name = context.getResources().getString(R.string.tab_title_daily_forecast);
                break;
            default:
                name = context.getResources().getString(R.string.tab_title_current_conditions);
                break;
        }
        return name.toUpperCase(l);
    }
}