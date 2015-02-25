package jeajjr.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jeajjr.weatherData.*;


public class Main {

	public static void main(String[] args) {
		String city = "berlin";
		
		String currentData = WeatherClient.getCurrentConditionsData(city);

		System.out.println(currentData);
		CurrentConditions currentConditions = new CurrentConditions();
		
		currentConditions = DataParser.parseCurrentConditions(currentData);
		
		if (currentConditions != null) {
			System.out.println("City ID: " + currentConditions.locationInfo.get(WeatherParameters.cityID));
			System.out.println("City Name: " + currentConditions.locationInfo.get(WeatherParameters.cityName));
			System.out.println("Country name: " + currentConditions.locationInfo.get(WeatherParameters.countryName));
			
			System.out.println("Humidity: " + currentConditions.locationInfo.get(WeatherParameters.humidity));
			System.out.println("Temperature: " + ((double) currentConditions.locationInfo.get(WeatherParameters.temperature)-273.15));
			System.out.println("Description: " + currentConditions.locationInfo.get(WeatherParameters.weatherDescription));
			System.out.println("Wind speed: " + currentConditions.locationInfo.get(WeatherParameters.windSpeed));
			System.out.println("Wind gusts: " + currentConditions.locationInfo.get(WeatherParameters.windGusts));
			
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy kk:mm");
			System.out.println("Sunrise: " + sf.format(((Calendar) currentConditions.locationInfo.get(WeatherParameters.sunrise)).getTime()) );
			
			System.out.println("Date taken: " + sf.format(((Calendar) currentConditions.locationInfo.get(WeatherParameters.dateReceived)).getTime()));
			
		}
		else
			System.out.println("Error retrieving or parsing data");
			
		
		String fiveDaysData = WeatherClient.getFiveDaysForecastData(city);
		System.out.println(fiveDaysData);
		
		MultipleWeatherForecast fiveDays = DataParser.parseFiveDaysForecast(fiveDaysData);

		System.out.println("City name: " + fiveDays.locationInfo.get(WeatherParameters.cityName));
		for (int i = 0; i < 5; i++) {
			System.out.println("temp: " + fiveDays.weatherInfoList.get(i).get(WeatherParameters.temperature));
			System.out.println("temp_max: " + fiveDays.weatherInfoList.get(i).get(WeatherParameters.temperatureMax));
			System.out.println("temp_min: " + fiveDays.weatherInfoList.get(i).get(WeatherParameters.temperatureMin));
			System.out.println("temp_max: " + fiveDays.weatherInfoList.get(i).get(WeatherParameters.weatherGroup));
			
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy kk:mm");
			System.out.println("forecastDate: " + sf.format(((Calendar) fiveDays.weatherInfoList.get(i).get(WeatherParameters.forecastDate)).getTime()) );
			System.out.println("date received: " + sf.format(((Calendar) fiveDays.weatherInfoList.get(i).get(WeatherParameters.dateReceived)).getTime()) );
			
		}
		
		String sixteenDaysData = WeatherClient.getSixteenDaysForecastData(city);
		
		MultipleWeatherForecast sixteenDays = DataParser.parseSixteenDaysForecast(sixteenDaysData);
		
		System.out.println("City name: " + sixteenDays.locationInfo.get(WeatherParameters.cityName));
		for (int i = 0; i < 5; i++) {
			System.out.println("temp: " + sixteenDays.weatherInfoList.get(i).get(WeatherParameters.temperature));
			System.out.println("temp_max: " + sixteenDays.weatherInfoList.get(i).get(WeatherParameters.temperatureMax));
			System.out.println("temp_min: " + sixteenDays.weatherInfoList.get(i).get(WeatherParameters.temperatureMin));
			System.out.println("temp_max: " + sixteenDays.weatherInfoList.get(i).get(WeatherParameters.weatherGroup));
			
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy kk:mm");
			System.out.println("forecastDate: " + sf.format(((Calendar) sixteenDays.weatherInfoList.get(i).get(WeatherParameters.forecastDate)).getTime()) );
			System.out.println("date received: " + sf.format(((Calendar) sixteenDays.weatherInfoList.get(i).get(WeatherParameters.dateReceived)).getTime()) );
			
		}
		
	}
}
