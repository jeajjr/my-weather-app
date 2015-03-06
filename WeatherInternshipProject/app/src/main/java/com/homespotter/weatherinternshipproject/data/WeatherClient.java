package com.homespotter.weatherinternshipproject.data;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherClient {
    // Class instance
    private static WeatherClient instance = null;

    // Requests URLs
	private static String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static String FIND_URL = "find?q=";
    private static String CURRENT_COND_URL = "weather?q=";
	private static String FIVE_DAYS_FORECAST_URL = "forecast?q=";
	private static String SIXTEEN_DAYS_FORECAST_URL = "forecast/daily?q=";
	private static String COUNT_URL = "&cnt=";
	private static int COUNT = 10;
    private static String METRIC_UNITS_URL = "&units=metric";
    private static String IMPERIAL_UNITS_URL = "&units=imperial";

    // Units options
    public static int METRIC_UNITS = 0;
    public static int IMPERIAL_UNITS = 1;

    // Using singleton pattern to guarantee a single instance
    public static WeatherClient getInstance() {
        if (instance == null) {
            instance = new WeatherClient();
        }
        return instance;
    }

    private WeatherClient() {
    }

    public String findCity(String city) {
        return sendRequest(BASE_URL + FIND_URL + city );
    }
    public String getCurrentConditionsData(String location, int units) {
        return sendRequest(BASE_URL + CURRENT_COND_URL + location + (units == IMPERIAL_UNITS ? IMPERIAL_UNITS_URL : METRIC_UNITS_URL) );
    }

    public String getFiveDaysForecastData (String location, int units) {
        return sendRequest(BASE_URL + FIVE_DAYS_FORECAST_URL + location + (units == IMPERIAL_UNITS ? IMPERIAL_UNITS_URL : METRIC_UNITS_URL) );
    }

    public String getSixteenDaysForecastData (String location, int units) {
        return sendRequest(BASE_URL + SIXTEEN_DAYS_FORECAST_URL + location + COUNT_URL + COUNT  + (units == IMPERIAL_UNITS ? IMPERIAL_UNITS_URL : METRIC_UNITS_URL) );
    }

	public String sendRequest (String requestString) {
		HttpURLConnection con = null;
		InputStream is = null;

		try {
			con = (HttpURLConnection) ( new URL(requestString)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();

			// Let's read the response
			StringBuffer buffer = new StringBuffer();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while (  (line = br.readLine()) != null )
				buffer.append(line + "\r\n");

			is.close();
			con.disconnect();
			return buffer.toString();
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		return null;
	}
}
