package org.jeajjr.weatherData;
import java.util.Calendar;

/**
 * This class will store each entry of the 16 days/daily forecast.
 * @author José Ernesto
 *
 */
public class SixteenDaysForecastItem extends WeatherInfo {
	// temperatures through the day
	public Double morningTemperature;
	public Double eveningTemperature;
	public Double nightTemperature;
	
	// save date and time of forecast
	public Calendar forecastDate;
}
