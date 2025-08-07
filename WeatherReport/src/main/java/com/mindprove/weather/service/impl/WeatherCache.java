package com.mindprove.weather.service.impl;

import org.springframework.stereotype.Component;

import com.mindprove.weather.model.Weather;


@Component
public class WeatherCache {
	
	private Weather lastResponse;
	private long lastFetchTime = 0;

	public synchronized void update(Weather weather) {
		lastResponse = weather;
		lastFetchTime = System.currentTimeMillis();
	}

	public synchronized Weather get() {
		return lastResponse;
	}

	public synchronized boolean isFresh() {
		return System.currentTimeMillis() - lastFetchTime <= 3000;
	}
}
