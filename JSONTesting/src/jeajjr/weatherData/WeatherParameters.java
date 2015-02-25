package jeajjr.weatherData;

/**
 * This class stores the String index of the HashMaps that hold the city
 * and weather data.
 * @author José Ernesto
 *
 */
public class WeatherParameters {

	// --- parameters common to all type of forecasts ---
	// City location information
	public static String cityID = "city_id";
	public static String cityName = "city_name";
	public static String countryName = "country_name";

	// main data
	public static String humidity = "humidity";
	public static String temperature = "temperature";
	public static String temperatureMin = "temperature_min";
	public static String temperatureMax = "temperature_max";
	public static String pressure = "";

	// wind data
	public static String windSpeed = "wind_speed";
	public static String windDegrees = "wind_degrees";
	public static String windGusts = "wind_gusts";

	// clouds data
	public static String cloudiness = "cloudiness";

	// weather data
	public static String weatherID = "weather_id";
	public static String weatherGroup = "weather_group";
	public static String weatherDescription = "weather_description";
	public static String weatherIconID = "weather_icon_id";

	// rain data
	public static String rainPrecipitation = "rain_precipitation";

	// snow data
	public static String snowPrecipitation = "snow_precipitation";

	// store when data was received
	public static String dateReceived = "date_received";

	// save date and time of forecast
	public static String forecastDate = "forecast_date";
		
	// --- parameters unique to the current weather conditions ---
	// sunrise and sunset times
	public static String sunrise = "sunrise";
	public static String sunset = "sunset";

	// --- parameters unique to the sixteen days forecast ---
	// temperatures through the day
	public static String morningTemperature = "morning_temperature";
	public static String eveningTemperature = "evening_temperature";
	public static String nightTemperature = "night_temperature";
}
