package jeajjr.weatherData;

import java.util.Calendar;
import java.util.HashMap;
import org.json.*;

/**
 * This class will parse the data String received by the JSON request,
 * returning the parsed data in an appropriate class which implements
 * WeatherInfo.
 * 
 * It uses part of the package org.json, developed by Douglas Crockford
 * (douglas@crockford.com), found at github.com/douglascrockford/JSON-java
 * 
 * @author José Ernesto
 *
 */
public class DataParser {
	
	/**
	 * Parse a current weather request data.
	 * @param data: data received from the current conditions JSON request.
	 * @return: a CurrentWeather instance filled with the parsed data.
	 */
	
	public static CurrentConditions parseCurrentConditions(String data) {
		// object that will store all data
		CurrentConditions currentConditions = new CurrentConditions();

		// JSON object
		JSONObject jObject;
	
		/* If an exception is thrown in the creationg of the JSONObject or
		 * while parsing the basic information, the data is broken or useless.
		 */
		try { 
			
			jObject = new JSONObject(data);		
		 
			// City location information
			currentConditions.locationInfo.put(WeatherParameters.cityID, jObject.getInt("id"));
			
			currentConditions.locationInfo.put(WeatherParameters.cityName, jObject.getString("name"));
			currentConditions.locationInfo.put(WeatherParameters.countryName, jObject.getJSONObject("sys").getString("country"));

			// main data
			currentConditions.locationInfo.put(WeatherParameters.temperature, jObject.getJSONObject("main").getDouble("temp"));
			currentConditions.locationInfo.put(WeatherParameters.humidity, jObject.getJSONObject("main").getInt("humidity"));
			currentConditions.locationInfo.put(WeatherParameters.temperatureMin, jObject.getJSONObject("main").getDouble("temp"));
			currentConditions.locationInfo.put(WeatherParameters.temperatureMax,jObject.getJSONObject("main").getDouble("temp_min"));
			
			// weather data
			currentConditions.locationInfo.put(WeatherParameters.weatherID, jObject.getJSONArray("weather").getJSONObject(0).getInt("id"));
			currentConditions.locationInfo.put(WeatherParameters.weatherGroup, jObject.getJSONArray("weather").getJSONObject(0).getString("main"));
			currentConditions.locationInfo.put(WeatherParameters.weatherDescription, jObject.getJSONArray("weather").getJSONObject(0).getString("description"));
			currentConditions.locationInfo.put(WeatherParameters.weatherIconID, jObject.getJSONArray("weather").getJSONObject(0).getString("icon"));
			
		} catch (JSONException e) {
			return null;
		}
		
		try { currentConditions.locationInfo.put(WeatherParameters.pressure, jObject.getJSONObject("main").getDouble("temp_max")); } catch (JSONException e) {
		}
		// wind data
		try { currentConditions.locationInfo.put(WeatherParameters.windSpeed, jObject.getJSONObject("wind").getDouble("speed")); } catch (JSONException e) {}
		try { currentConditions.locationInfo.put(WeatherParameters.windDegrees, jObject.getJSONObject("wind").getDouble("deg")); } catch (JSONException e) {}
		try { currentConditions.locationInfo.put(WeatherParameters.windGusts, jObject.getJSONObject("wind").getDouble("gust")); } catch (JSONException e) {}
		
		// clouds data
		try { currentConditions.locationInfo.put(WeatherParameters.cloudiness, jObject.getJSONObject("clouds").getInt("all")); } catch (JSONException e) {}
		
		// rain data
		try { currentConditions.locationInfo.put(WeatherParameters.rainPrecipitation, jObject.getJSONObject("rain").getInt("3h")); } catch (JSONException e) {}
		
		// snow data
		try { currentConditions.locationInfo.put(WeatherParameters.snowPrecipitation, jObject.getJSONObject("snow").getInt("3h")); } catch (JSONException e) {}
		
		// store when data was received
		currentConditions.locationInfo.put(WeatherParameters.dateReceived, Calendar.getInstance());
		((Calendar) currentConditions.locationInfo.get(WeatherParameters.dateReceived)).setTimeInMillis(jObject.getInt("dt") * 1000L);

		currentConditions.locationInfo.put(WeatherParameters.sunrise, Calendar.getInstance());
		((Calendar) currentConditions.locationInfo.get(WeatherParameters.sunrise)).setTimeInMillis(jObject.getJSONObject("sys").getInt("sunrise") * 1000L);
		
		currentConditions.locationInfo.put(WeatherParameters.sunset, Calendar.getInstance());
		((Calendar) currentConditions.locationInfo.get(WeatherParameters.sunset)).setTimeInMillis(jObject.getJSONObject("sys").getInt("sunset") * 1000L);

		return currentConditions;
	}
	
	/**
	 * Parse a 5-days (3 hours resolution) request data.
	 * @param data: data received from the 5-days/3 hours forecast JSON request.
	 * @return: a MultipleWeatheForecast instance, where its field weatherInfoList
	 * is filled with one item from the parsed data.
	 */
	public static MultipleWeatherForecast parseFiveDaysForecast(String data) {
		// ArrayList that will store all data
		MultipleWeatherForecast fiveDaysForecast = new MultipleWeatherForecast();

		// JSON object
		JSONObject jObject;
		
		/* If an exception is thrown in the creationg of the JSONObject or
		 * while parsing the basic information, the data is broken or useless.
		 */
		try {
			jObject = new JSONObject(data);		
		 
			// City location information
			fiveDaysForecast.locationInfo.put(WeatherParameters.cityID, jObject.getJSONObject("city").getInt("id"));
			
			fiveDaysForecast.locationInfo.put(WeatherParameters.cityName, jObject.getJSONObject("city").getString("name"));
			fiveDaysForecast.locationInfo.put(WeatherParameters.countryName, jObject.getJSONObject("city").getString("country"));
			
			int cnt = jObject.getInt("cnt");
			System.out.println(cnt);
			
			JSONArray weatherJSONArray = jObject.getJSONArray("list");
			
			// iterate through the entire weather list
			for (int i = 0; i < cnt; i++) {
				
				HashMap<String, Object> weatherInstance = new HashMap<String, Object>();
				
				// temp data
				weatherInstance.put(WeatherParameters.temperature, weatherJSONArray.getJSONObject(i).getJSONObject("main").getDouble("temp"));				
				weatherInstance.put(WeatherParameters.humidity, weatherJSONArray.getJSONObject(i).getJSONObject("main").getInt("humidity"));
				weatherInstance.put(WeatherParameters.temperatureMin, weatherJSONArray.getJSONObject(i).getJSONObject("main").getDouble("temp_min"));
				weatherInstance.put(WeatherParameters.temperatureMax, weatherJSONArray.getJSONObject(i).getJSONObject("main").getDouble("temp_max"));
				try { weatherInstance.put(WeatherParameters.pressure, weatherJSONArray.getJSONObject(i).getJSONObject("main").getDouble("pressure")); } catch (JSONException e) {}
				
				// weather data
				weatherInstance.put(WeatherParameters.weatherID, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getInt("id"));
				weatherInstance.put(WeatherParameters.weatherGroup, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main"));
				weatherInstance.put(WeatherParameters.weatherDescription, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));
				weatherInstance.put(WeatherParameters.weatherIconID, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon"));
				
				// wind data
				try { weatherInstance.put(WeatherParameters.windSpeed, weatherJSONArray.getJSONObject(i).getJSONObject("wind").getDouble("speed")); } catch (JSONException e) {}
				try { weatherInstance.put(WeatherParameters.windDegrees, weatherJSONArray.getJSONObject(i).getJSONObject("wind").getDouble("deg")); } catch (JSONException e) {}
				try { weatherInstance.put(WeatherParameters.windGusts, weatherJSONArray.getJSONObject(i).getJSONObject("wind").getDouble("gust")); } catch (JSONException e) {}
				
				// clouds data
				try { weatherInstance.put(WeatherParameters.cloudiness, weatherJSONArray.getJSONObject(i).getJSONObject("clouds").getInt("all")); } catch (JSONException e) {}
				
				// rain data
				try { weatherInstance.put(WeatherParameters.rainPrecipitation, weatherJSONArray.getJSONObject(i).getJSONObject("rain").getInt("3h")); } catch (JSONException e) {}
				
				// snow data
				try { weatherInstance.put(WeatherParameters.snowPrecipitation, weatherJSONArray.getJSONObject(i).getJSONObject("snow").getInt("3h")); } catch (JSONException e) {}
				
				// store when data was received
				weatherInstance.put(WeatherParameters.dateReceived, Calendar.getInstance());
				try { ((Calendar)weatherInstance.get(WeatherParameters.dateReceived)).setTimeInMillis(weatherJSONArray.getJSONObject(i).getInt("dt_txt") * 1000L); } catch (JSONException e) {}
			
				// store forecast date and time
				weatherInstance.put(WeatherParameters.forecastDate, Calendar.getInstance());
				try { ((Calendar)weatherInstance.get(WeatherParameters.forecastDate)).setTimeInMillis(weatherJSONArray.getJSONObject(i).getInt("dt") * 1000L); } catch (JSONException e) {}
				 
				fiveDaysForecast.weatherInfoList.add(weatherInstance);
			}
			
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		return fiveDaysForecast;
	}
	
	/**
	 * Parse a 16-day (1 day resolution) request data.
	 * @param data: data received from the 16-days/daily forecast JSON request.
	 * @return: a MultipleWeatheForecast instance, where its field weatherInfoList
	 * is filled with one item from the parsed data.
	 */
	public static MultipleWeatherForecast parseSixteenDaysForecast(String data) {
		// ArrayList that will store all data
		MultipleWeatherForecast fiveDaysForecast = new MultipleWeatherForecast();

		// JSON object
		JSONObject jObject;
		
		/* If an exception is thrown in the creationg of the JSONObject or
		 * while parsing the basic information, the data is broken or useless.
		 */
		try {
			jObject = new JSONObject(data);		
		 
			// City location information
			fiveDaysForecast.locationInfo.put(WeatherParameters.cityID, jObject.getJSONObject("city").getInt("id"));
			
			fiveDaysForecast.locationInfo.put(WeatherParameters.cityName, jObject.getJSONObject("city").getString("name"));
			fiveDaysForecast.locationInfo.put(WeatherParameters.countryName, jObject.getJSONObject("city").getString("country"));
			
			int cnt = jObject.getInt("cnt");
			System.out.println(cnt);
			
			JSONArray weatherJSONArray = jObject.getJSONArray("list");
			
			// iterate through the entire weather list
			for (int i = 0; i < cnt; i++) {
				
				HashMap<String, Object> weatherInstance = new HashMap<String, Object>();
				
				// temp data
				weatherInstance.put(WeatherParameters.temperature, weatherJSONArray.getJSONObject(i).getJSONObject("temp").getDouble("day"));
				weatherInstance.put(WeatherParameters.temperatureMin, weatherJSONArray.getJSONObject(i).getJSONObject("temp").getDouble("min"));
				weatherInstance.put(WeatherParameters.temperatureMax, weatherJSONArray.getJSONObject(i).getJSONObject("temp").getDouble("max"));
				weatherInstance.put(WeatherParameters.nightTemperature, weatherJSONArray.getJSONObject(i).getJSONObject("temp").getDouble("night"));
				weatherInstance.put(WeatherParameters.eveningTemperature, weatherJSONArray.getJSONObject(i).getJSONObject("temp").getDouble("eve"));
				weatherInstance.put(WeatherParameters.morningTemperature, weatherJSONArray.getJSONObject(i).getJSONObject("temp").getDouble("morn"));
				
				weatherInstance.put(WeatherParameters.humidity, weatherJSONArray.getJSONObject(i).getInt("humidity"));
				try { weatherInstance.put(WeatherParameters.pressure, weatherJSONArray.getJSONObject(i).getDouble("pressure")); } catch (JSONException e) {}
				
				// weather data
				weatherInstance.put(WeatherParameters.weatherID, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getInt("id"));
				weatherInstance.put(WeatherParameters.weatherGroup, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main"));
				weatherInstance.put(WeatherParameters.weatherDescription, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));
				weatherInstance.put(WeatherParameters.weatherIconID, weatherJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon"));
			
				
				// wind data
				try { weatherInstance.put(WeatherParameters.windSpeed, weatherJSONArray.getJSONObject(i).getJSONObject("wind").getDouble("speed")); } catch (JSONException e) {}
				try { weatherInstance.put(WeatherParameters.windDegrees, weatherJSONArray.getJSONObject(i).getJSONObject("wind").getDouble("deg")); } catch (JSONException e) {}
				try { weatherInstance.put(WeatherParameters.windGusts, weatherJSONArray.getJSONObject(i).getJSONObject("wind").getDouble("gust")); } catch (JSONException e) {}
				
				// clouds data
				try { weatherInstance.put(WeatherParameters.cloudiness, weatherJSONArray.getJSONObject(i).getJSONObject("clouds").getInt("all")); } catch (JSONException e) {}
				
				// rain data
				try { weatherInstance.put(WeatherParameters.rainPrecipitation, weatherJSONArray.getJSONObject(i).getJSONObject("rain").getInt("3h")); } catch (JSONException e) {}
				
				// snow data
				try { weatherInstance.put(WeatherParameters.snowPrecipitation, weatherJSONArray.getJSONObject(i).getJSONObject("snow").getInt("3h")); } catch (JSONException e) {}
				
				// store when data was received
				weatherInstance.put(WeatherParameters.dateReceived, Calendar.getInstance());
				try { ((Calendar)weatherInstance.get(WeatherParameters.dateReceived)).setTimeInMillis(weatherJSONArray.getJSONObject(i).getInt("dt_txt") * 1000L); } catch (JSONException e) {}
			
				// store forecast date and time
				weatherInstance.put(WeatherParameters.forecastDate, Calendar.getInstance());
				try { ((Calendar)weatherInstance.get(WeatherParameters.forecastDate)).setTimeInMillis(weatherJSONArray.getJSONObject(i).getInt("dt") * 1000L); } catch (JSONException e) {}
				 
				fiveDaysForecast.weatherInfoList.add(weatherInstance);
			}
			
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		return fiveDaysForecast;
	}
}
