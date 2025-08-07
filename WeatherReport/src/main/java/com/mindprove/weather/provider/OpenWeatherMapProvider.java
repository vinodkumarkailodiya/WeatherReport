package com.mindprove.weather.provider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mindprove.weather.model.Weather;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Order(2)
@Qualifier("openWeatherMapProvider")
public class OpenWeatherMapProvider implements WeatherProvider {

	@Value("${openweather.api.key}")
	private String apiKey;
	
	@Value("${city}")
	private String city;

	private final RestTemplate restTemplate;

	@Override
	public Weather getWeather() throws Exception {
		log.info("Fetching weather from OpenWeatherMap");
		String url = "http://api.openweathermap.org/data/2.5/weather?q="+city+",AU&appid=" + apiKey;
		Map<String, Object> response = restTemplate.getForObject(url, Map.class);
		if (response == null || response.get("main") == null) {
    			log.info("OpenWeatherMapProvider failed: {}", response);
			return null;
		}
		Weather weather = new Weather();
		double temp = ((Number) ((Map) response.get("main")).get("temp")).doubleValue();
		double wind = ((Number) ((Map) response.get("wind")).get("speed")).doubleValue();
		weather.setTemperature(temp);
		weather.setWindSpeed(wind);
		return weather;
	}

}
