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
@Order(1)
@Qualifier("weatherStackProvider")
public class WeatherStackProvider implements WeatherProvider {

	@Value("${weatherstack.api.key}")
	private String apiKey;
	
	@Value("${city}")
	private String city;
	
	private final RestTemplate restTemplate;

	@Override
	public Weather getWeather() throws Exception {
		log.info("Fetching weather from WeatherStackProvider");
		String url = "http://api.weatherstack.com/current?access_key="+apiKey+"&query="+city;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.containsKey("error")) {
        		log.info("WeatherStackProvider failed: {}", response);
            return null;
        }
        
        Map current = (Map) response.get("current");
        Weather weather = new Weather();
        double temperature = current.get("temperature") instanceof Number
                ? ((Number) current.get("temperature")).doubleValue()
                : 0.0;
        double windSpeed = current.get("wind_speed") instanceof Number
                ? ((Number) current.get("wind_speed")).doubleValue()
                : 0.0;
        
        weather.setTemperature(temperature);
        weather.setWindSpeed(windSpeed);
        return weather;
	}

}
