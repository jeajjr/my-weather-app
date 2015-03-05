package com.homespotter.weatherinternshipproject.data;

import java.util.HashMap;

/**
 * This class will store the current weather information.
 * the HashMap locationInfo will hold the location information.
 * the HashMap weatherInfo will hold the weather information.
 * The HashMaps will use the string in WeatherParameters
 * as indices.
 * @author José Ernesto
 *
 */
public class CurrentConditions {
	public HashMap<String, Object> locationInfo;
	public HashMap<String, Object>  weatherInfo;
    public String temperatureUnit;
    public String speedUnit;

    public CurrentConditions() {
		locationInfo = new HashMap<String, Object>();
		weatherInfo = new HashMap<String, Object>();
	}
}
