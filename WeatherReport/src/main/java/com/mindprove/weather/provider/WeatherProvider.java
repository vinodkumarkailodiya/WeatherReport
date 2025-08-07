package com.mindprove.weather.provider;

import com.mindprove.weather.model.Weather;

public interface WeatherProvider {

	 Weather getWeather() throws Exception;
}
