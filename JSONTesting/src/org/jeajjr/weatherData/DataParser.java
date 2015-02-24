package org.jeajjr.weatherData;
import java.util.Calendar;
import java.util.Locale;

import org.json.*;

/**
 * This class will parse the data String received by the JSON request,
 * returning the parsed data in an appropriate class which implements
 * WeatherInfo.
 * 
 * @author José Ernesto
 *
 */
public class DataParser {
	
	/**
	 * Parse a current weather request data.
	 * @param data: data received from the JSON request.
	 * @return: a CurrentWeather instance filled with the parsed data.
	 */
	public static CurrentWeather parseCurrentWeather(String data) {
		// object that will store all data
		CurrentWeather currentWeather = new CurrentWeather();
		
		// JSON objects
		JSONObject jObject = new JSONObject(data);
		
		// City location information
		currentWeather.cityID = jObject.getInt("id");
		
		currentWeather.cityName = jObject.getString("name");
		currentWeather.countryName = jObject.getJSONObject("sys").getString("country");
		
		// main data
		currentWeather.humidity = jObject.getJSONObject("main").getInt("humidity");
		/*
		currentWeather.temperatureMin;
		currentWeather.temperatureMax;
		currentWeather.pressure;
		currentWeather.seaLevelPressure;
		currentWeather.groundLevelPressure;

		// wind data
		currentWeather.windSpeed;
		currentWeather.windDegrees;
		currentWeather.windGusts;
		
		// clouds data
		currentWeather.cloudiness;
		
		// weather data
		currentWeather.weatherID;
		currentWeather.weatherGroup;
		currentWeather.weatherDescription;
		currentWeather.weatherIconID;
		
		// rain data
		currentWeather.rainPrecipitation;
		
		// snow data
		currentWeather.snowPrecipitation;
		
		// store when data was received
		currentWeather.dataReceived;


		currentWeather.sunrise;
		currentWeather.sunset;
*/
		return currentWeather;
	}
}
