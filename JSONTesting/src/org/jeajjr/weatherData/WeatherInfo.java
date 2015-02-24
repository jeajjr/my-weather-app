package org.jeajjr.weatherData;
import java.util.Calendar;
import java.util.Locale;

/**
 * This class will be the base class to store the weather or forecast data.
 * @author José Ernesto
 *
 */
public class WeatherInfo {
	
	// City location information
	public Integer cityID;
	public String cityName;
	public String countryName;
	
	// main data
	public Integer humidity;
	public Double temperatureMin;
	public Double temperatureMax;
	public Double pressure;
	public Double seaLevelPressure;
	public Double groundLevelPressure;

	// wind data
	public Double windSpeed;
	public Double windDegrees;
	public Double windGusts;
	
	// clouds data
	public Integer cloudiness;
	
	// weather data
	public Integer weatherID;
	public String weatherGroup;
	public String weatherDescription;
	public String weatherIconID;
	
	// rain data
	public Integer rainPrecipitation;
	
	// snow data
	public Integer snowPrecipitation;
	
	// store when data was received
	public Calendar dataReceived;
}
