package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.FilesHandler;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;

public class ActivitySettings extends ActionBarActivity {
    private static final String TAG = "ActivitySettings";

    public SettingsProfile settingsProfile;
    private FragmentSettings fragmentSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fragmentSettings = new FragmentSettings();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragmentSettings)
                    .commit();
        }

        settingsProfile = FilesHandler.getInstance().getSettingProfile(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((ImageView) toolbar.findViewById(R.id.imageViewToolboxButton)).setImageResource(R.drawable.gear);
        ((TextView) toolbar.findViewById(R.id.textViewToolboxTitle)).setText(getString(R.string.settings));
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        fragmentSettings.saveSettings();

        Intent data = new Intent();

        data.putExtra("settings", settingsProfile);
        setResult(Activity.RESULT_OK, data);
        finish();

        super.onBackPressed();
    }

    public class FragmentSettings extends PreferenceFragment {

        public FragmentSettings() {
        }

        public void saveSettings() {
            Preference pref = findPreference("checkbox_units");
            CheckBoxPreference check = (CheckBoxPreference) pref;
            if (check.isChecked())
                settingsProfile.setUnits(SettingsProfile.UNIT_IMPERIAL);
            else
                settingsProfile.setUnits(SettingsProfile.UNIT_METRIC);

            pref = findPreference("checkbox_date_format");
            check = (CheckBoxPreference) pref;
            if (check.isChecked())
                settingsProfile.setDateFormat(SettingsProfile.DATE_FORMAT_MM_DD);
            else
                settingsProfile.setDateFormat(SettingsProfile.DATE_FORMAT_DD_MM);

            pref = findPreference("checkbox_date_format");
            check = (CheckBoxPreference) pref;
            if (check.isChecked())
                settingsProfile.setHourFormat(SettingsProfile.HOUR_FORMAT_12);
            else
                settingsProfile.setHourFormat(SettingsProfile.HOUR_FORMAT_24);

            Log.d(TAG, "saving profile " + settingsProfile);
            FilesHandler.getInstance().setSettingProfile(getActivity(), settingsProfile);
        }
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragment_settings);

            Preference pref = findPreference("checkbox_units");
            CheckBoxPreference check = (CheckBoxPreference) pref;
            if (settingsProfile.getUnits() == SettingsProfile.UNIT_IMPERIAL)
                check.setChecked(true);
            else
                check.setChecked(false);

            pref = findPreference("checkbox_date_format");
            check = (CheckBoxPreference) pref;
            if (settingsProfile.getDateFormat() == SettingsProfile.DATE_FORMAT_MM_DD)
                check.setChecked(true);
            else
                check.setChecked(false);

            pref = findPreference("checkbox_date_format");
            check = (CheckBoxPreference) pref;
            if (settingsProfile.getHourFormat() == SettingsProfile.HOUR_FORMAT_12)
                check.setChecked(true);
            else
                check.setChecked(false);
        }
    }
}
