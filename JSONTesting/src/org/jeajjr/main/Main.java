package org.jeajjr.main;

import org.jeajjr.weatherData.CurrentWeather;
import org.jeajjr.weatherData.DataParser;
import org.jeajjr.weatherData.WeatherClient;


public class Main {

	public static void main(String[] args) {
		String city = "sao carlos, brasil";
		
		String data = WeatherClient.getWeatherData(city);

		System.out.println(data);
		CurrentWeather currentWeather = new CurrentWeather();
		
		currentWeather = DataParser.parseCurrentWeather(data);
		
		System.out.println("City ID: " + currentWeather.cityID);
		System.out.println("City Name: " + currentWeather.cityName);
		System.out.println("Country name: " + currentWeather.countryName);
		System.out.println("Humidity: " + currentWeather.humidity);
		
	}

}
