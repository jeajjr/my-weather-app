package com.homespotter.weatherinternshipproject.data;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherClient {
    private static WeatherClient instance = null;
	private static String BASE_URL = "http://api.openweathermap.org/data/2.5/";
	
	private static String CURRENT_COND_URL = "weather?q=";
	
	private static String FIVE_DAYS_FORECAST_URL = "forecast?q=";
	
	private static String SIXTEEN_DAYS_FORECAST_URL = "forecast/daily?q=";
	private static String COUNT_URL = "&cnt=";
	private static int COUNT = 10;

    // Using singleton pattern to guarantee a single instance
    public static WeatherClient getInstance() {
        if (instance == null) {
            instance = new WeatherClient();
        }
        return instance;
    }

    private WeatherClient() {
    }


    public static String getCurrentConditionsData(String location) {
		return sendRequest(BASE_URL + CURRENT_COND_URL + location);
	}
	
	public static String getFiveDaysForecastData (String location) {
		return sendRequest(BASE_URL + FIVE_DAYS_FORECAST_URL + location);
	}
	
	public static String getSixteenDaysForecastData (String location) {
		return sendRequest(BASE_URL + SIXTEEN_DAYS_FORECAST_URL + location + COUNT_URL + COUNT);
	}
	
	public static String sendRequest (String requestString) {
		HttpURLConnection con = null;
		InputStream is = null;

System.out.println(requestString);
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
