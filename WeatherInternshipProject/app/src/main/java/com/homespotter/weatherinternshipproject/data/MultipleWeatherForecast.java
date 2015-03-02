package com.homespotter.weatherinternshipproject.data;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class will store the data of multiple moments forecast.
 * the HashMap locationInfo will hold the location information.
 * the list of HashMaps weatherInfoList will hold all weather 
 * forecast instances.
 * The HashMaps will use the string in WeatherParameters
 * as indices.
 * @author José Ernesto
 *
 */
public class MultipleWeatherForecast {
	public HashMap<String, Object> locationInfo;
	public ArrayList<HashMap<String, ?>> weatherInfoList;
	
	public MultipleWeatherForecast() {
		locationInfo = new HashMap<String, Object>();
		weatherInfoList = new ArrayList<HashMap<String, ?>>();
	}
	
}
