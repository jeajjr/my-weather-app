package com.homespotter.weatherinternshipproject.data;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * This class will be responsible for handling files saved on the device's internal store.
 */
public class FilesHandler {
    private static final String TAG = "FilesHandler";

    private static FilesHandler instance;

    private static String CITY_FILE = "city.txt";
    private static String SETTINGS_FILE = "settings.txt";


    // Using singleton pattern to guarantee a single instance
    public static FilesHandler getInstance() {
        if (instance == null) {
            instance = new FilesHandler();
        }
        return instance;
    }

    private FilesHandler() {
    }

    public SettingsProfile getSettingProfile(Context context) {
        Log.d(TAG, "getSettingProfile");
        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + SETTINGS_FILE);
            SettingsProfile settingsProfile;

            // If there is not a created settings file, create a standard one
            if (! f.exists()) {
                settingsProfile = new SettingsProfile();
                setSettingProfile(context, settingsProfile);

                Log.d(TAG, "setting file does not exist");
            }
            else {
                FileInputStream fin = new FileInputStream(context.getFilesDir().getAbsolutePath() + SETTINGS_FILE);
                ObjectInputStream ois = new ObjectInputStream(fin);

                settingsProfile = (SettingsProfile) ois.readObject();

                Log.d(TAG, "setting file exists: " + settingsProfile);

                ois.close();
                fin.close();
            }

            return settingsProfile;

        } catch (Exception e) {
            return null;
        }
    }

    public void setSettingProfile(Context context, SettingsProfile settingsProfile) {
        Log.d(TAG, "setSettingProfile");

        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + SETTINGS_FILE);

            if (!f.exists()) {
                f.createNewFile();
            }

            FileOutputStream fout = new FileOutputStream(context.getFilesDir().getAbsolutePath() + SETTINGS_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);

            oos.writeObject(settingsProfile);
            Log.d(TAG, "settingsProfile saved: " + settingsProfile);

            oos.close();
            fout.close();
        } catch (Exception e) {
            return;
        }
    }

    public void removeSavedCities(Context context) {
        Log.d(TAG, "removeSavedCities");
        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + CITY_FILE);

            f.delete();
            Log.d(TAG, "deleted city file");

        } catch (Exception e) {
            return;
        }
    }

    public void addCityName(Context context, String cityName) {
        Log.d(TAG, "addCityName");

        ArrayList<String> cityList = getSavedCities(context);

        if (cityList == null)
            cityList = new ArrayList<String>();

        cityList.add(cityName);
        setCityList(context, cityList);

    }

    public void setCityList(Context context, String cityName) {
        Log.d(TAG, "setCityList");

        ArrayList<String> cityList = new ArrayList<String>();
        cityList.add(cityName);
        setCityList(context, cityList);
    }

    public void setCityList(Context context, ArrayList<String> cityList) {
        Log.d(TAG, "setCityList");

        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + CITY_FILE);

            if (!f.exists())
                f.createNewFile();

            FileOutputStream fout = new FileOutputStream(context.getFilesDir().getAbsolutePath() + CITY_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(cityList);

            oos.close();
            fout.close();
        } catch (Exception e) {
            return;
        }
    }

    public String getFirstSavedCity(Context context) {
        Log.d(TAG, "getFirstSavedCity");

        ArrayList<String> cityList = getSavedCities(context);

        if (cityList != null) {
            if (cityList.size() > 0)
                return cityList.get(0);
            else
                return null;
        }
        else
            return null;
    }

    public ArrayList<String> getSavedCities(Context context) {
        Log.d(TAG, "getSavedCities");

        ArrayList<String> cityList = null;

        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + CITY_FILE);

            if (!f.exists())
                return null;

            FileInputStream fin = new FileInputStream(context.getFilesDir().getAbsolutePath() + CITY_FILE);
            ObjectInputStream ois = new ObjectInputStream(fin);

            cityList = (ArrayList<String>) ois.readObject();

            ois.close();
            fin.close();
        } catch (Exception e) {
            return null;
        }
        return cityList;
    }
}
