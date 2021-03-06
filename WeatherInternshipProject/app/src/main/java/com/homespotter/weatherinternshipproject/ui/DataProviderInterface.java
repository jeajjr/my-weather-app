package com.homespotter.weatherinternshipproject.ui;


/**
 * This interface should be implemented by the Activity and will ease the
 * asynchronous communication between fragment and activity. Fragments receive
 * the application instance on the method onAttach(Activity), and will cast it
 * to this interface saving it in a local field. This field will be used as the
 * path to send and received information.
 */
public interface DataProviderInterface {
    public void setCurrentConditionsFragment(FragmentCurrentConditions fragmentCurrentConditions);
    public void setThreeHoursForecastFragment(FragmentThreeHoursForecast fragmentThreeHoursForecast);
    public void setDailyForecastFragment(FragmentDailyForecast fragmentDailyForecast);
    public void requestUpdate();
}
