package com.homespotter.weatherinternshipproject.data;

import java.io.Serializable;

/**
 * Created by José Ernesto on 06/03/2015.
 */
public class SettingsProfile implements Serializable {

    private int units;
    public static final int UNIT_METRIC = 0;
    public static final int UNIT_IMPERIAL = 1;

    private int dateFormat;
    public static final int DATE_FORMAT_MM_DD = 0;
    public static final int DATE_FORMAT_DD_MM = 1;

    private int hourFormat;
    public static final int HOUR_FORMAT_12 = 0;
    public static final int HOUR_FORMAT_24 = 1;

    public SettingsProfile() {
        this.units = UNIT_IMPERIAL;
        this.dateFormat = DATE_FORMAT_MM_DD;
        this.hourFormat = HOUR_FORMAT_12;
    }

    public SettingsProfile(int units, int dateFormat, int hourFormat) {
        this.units = units;
        this.dateFormat = dateFormat;
        this.hourFormat = hourFormat;
    }

    @Override
    public String toString() {
        String toString = "";

        toString += ( units == UNIT_IMPERIAL) ? "imperial" : "metric";
        toString += ", ";

        toString += ( dateFormat == DATE_FORMAT_MM_DD) ? "MM/DD" : "DD/MM";
        toString += ", ";

        toString += ( hourFormat == HOUR_FORMAT_12) ? "12 hour" : "24 hour";

        return toString;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(int dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getHourFormat() {
        return hourFormat;
    }

    public void setHourFormat(int hourFormat) {
        this.hourFormat = hourFormat;
    }
}
