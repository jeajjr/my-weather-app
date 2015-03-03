package com.homespotter.weatherinternshipproject.data;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class will be responsible for handling files saved on the device's internal store.
 */
public class FilesHandler {
    private static FilesHandler instance;

    private static String CITY_FILE = "city.txt";

    // Using singleton pattern to guarantee a single instance
    public static FilesHandler getInstance() {
        if (instance == null) {
            instance = new FilesHandler();
        }
        return instance;
    }

    private FilesHandler() {
    }

    public void setCityName(Context context, String cityName) {
        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + CITY_FILE);

            if (!f.exists())
                f.createNewFile();

            FileOutputStream fout = new FileOutputStream(context.getFilesDir().getAbsolutePath() + CITY_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(cityName);

            oos.close();
            fout.close();
        } catch (Exception e) {
            return;
        }
    }

    public String getSavedCity(Context context) {
        String cityName = null;
        try {
            File f = new File(context.getFilesDir().getAbsolutePath() + CITY_FILE);

            if (!f.exists())
                return null;

            FileInputStream fin = new FileInputStream(context.getFilesDir().getAbsolutePath() + CITY_FILE);
            ObjectInputStream ois = new ObjectInputStream(fin);
            cityName = (String) ois.readObject();

            ois.close();
            fin.close();
        } catch (Exception e) {
            return null;
        }
        return cityName;
    }
}
