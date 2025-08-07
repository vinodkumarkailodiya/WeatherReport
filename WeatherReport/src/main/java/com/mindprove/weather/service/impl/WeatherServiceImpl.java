package com.mindprove.weather.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.mindprove.weather.exception.BadRequestException;
import com.mindprove.weather.exception.NoWeatherDataException;
import com.mindprove.weather.model.Weather;
import com.mindprove.weather.provider.WeatherProvider;
import com.mindprove.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

	private final WeatherCache weatherCache;
	private final List<WeatherProvider> providers;

	@Override
	public Weather getWeather(String city) {
		log.info("Get weather method got called");
		
		if( city ==null || city.isEmpty() ||!city.equalsIgnoreCase("melbourne")) {
			throw new BadRequestException("Only the city melbourne is allowed.");
		}
		if (weatherCache.isFresh()) {
			log.info("Returning fresh weather data from cache");
			return weatherCache.get();
		}

		for (WeatherProvider provider : providers) {
			try {
				Weather response = provider.getWeather();
				if(response!=null) {
					weatherCache.update(response);
					log.info("Weather data successfully retrieved from provider: {}", provider.getClass().getSimpleName());
					return response;	
				}
			} catch (Exception ex) {
				log.warn("Failed to retrieve weather from provider: {}. Error: {}", provider.getClass().getSimpleName(),
						ex.getMessage());
			}
		}
		
		Weather stale = weatherCache.get();
		if (stale != null) {
			log.warn("Using stale weather data from cache due to provider failures");
			return stale;
		}
		throw new NoWeatherDataException("All weather providers failed and no stale data available.");
	}
}
