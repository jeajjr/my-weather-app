package com.homespotter.weatherinternshipproject.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * Created by José Ernesto on 01/03/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter (FragmentManager fm) {
        super(fm);
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

    /**
     * Just for future customization.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        String name;

        switch (position) {
            case 0:
                name = "current conditions";
                break;
            case 1:
                name = "3 hours forecast";
                break;
            default:
                name = "current conditions";
                break;
        }
        return name.toUpperCase(l);
    }
}