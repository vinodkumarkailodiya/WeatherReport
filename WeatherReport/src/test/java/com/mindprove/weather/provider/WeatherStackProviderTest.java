package com.mindprove.weather.provider;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.mindprove.weather.model.Weather;
import static org.junit.jupiter.api.Assertions.*;

public class WeatherStackProviderTest {

	@Mock
	private RestTemplate restTemplate;

	private WeatherStackProvider weatherStackProvider;

	private final String apiKey = "dummy-key";
	private final String city = "melbourne";

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		weatherStackProvider = new WeatherStackProvider(restTemplate);
		ReflectionTestUtils.setField(weatherStackProvider, "apiKey", apiKey);
		ReflectionTestUtils.setField(weatherStackProvider, "city", city);
	}

	@Test
	void testReturnsWeatherWhenApiResponseIsValid() throws Exception {
		Map<String, Object> current = new HashMap<>();
		current.put("temperature", 22.5);
		current.put("wind_speed", 15.3);

		Map<String, Object> response = new HashMap<>();
		response.put("current", current);

		String expectedUrl = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(expectedUrl, Map.class)).thenReturn(response);

		Weather weather = weatherStackProvider.getWeather();

		assertNotNull(weather);
		assertEquals(22.5, weather.getTemperature());
		assertEquals(15.3, weather.getWindSpeed());
	}

	@Test
	void testReturnsNullWhenApiReturnsErrorResponse() throws Exception {
		Map<String, Object> response = new HashMap<>();
		response.put("error", "Invalid API key");

		String expectedUrl = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(expectedUrl, Map.class)).thenReturn(response);

		Weather weather = weatherStackProvider.getWeather();

		assertNull(weather);
	}

	@Test
	void testReturnsNullWhenApiResponseIsNull() throws Exception {
		String expectedUrl = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(expectedUrl, Map.class)).thenReturn(null);

		Weather weather = weatherStackProvider.getWeather();

		assertNull(weather);
	}

	@Test
	void testThrowsExceptionWhenCurrentKeyIsMissingInResponse() throws Exception {
		Map<String, Object> response = new HashMap<>();
		String url = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(url, Map.class)).thenReturn(response);

		assertThrows(NullPointerException.class, () -> weatherStackProvider.getWeather());
	}

	@Test
	void testSetsDefaultTemperatureWhenTemperatureIsNotANumber() throws Exception {
		Map<String, Object> current = new HashMap<>();
		current.put("temperature", "twenty-two");
		current.put("wind_speed", 10.0);

		Map<String, Object> response = new HashMap<>();
		response.put("current", current);

		String url = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(url, Map.class)).thenReturn(response);

		Weather weather = weatherStackProvider.getWeather();

		assertNotNull(weather);
		assertEquals(0.0, weather.getTemperature());
		assertEquals(10.0, weather.getWindSpeed());
	}

	@Test
	void testSetsDefaultWindSpeedWhenWindSpeedIsNotANumber() throws Exception {
		Map<String, Object> current = new HashMap<>();
		current.put("temperature", 25.0);
		current.put("wind_speed", "fast");

		Map<String, Object> response = new HashMap<>();
		response.put("current", current);

		String url = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(url, Map.class)).thenReturn(response);

		Weather weather = weatherStackProvider.getWeather();

		assertNotNull(weather);
		assertEquals(25.0, weather.getTemperature());
		assertEquals(0.0, weather.getWindSpeed());
	}

	@Test
	void testSetsDefaultTemperatureAndWindSpeedWhenBothAreMissing() throws Exception {
		Map<String, Object> current = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		response.put("current", current);

		String url = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(url, Map.class)).thenReturn(response);

		Weather weather = weatherStackProvider.getWeather();

		assertNotNull(weather);
		assertEquals(0.0, weather.getTemperature());
		assertEquals(0.0, weather.getWindSpeed());
	}

	@Test
	void testThrowsExceptionWhenRestTemplateCallFails() {
		String url = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
		when(restTemplate.getForObject(url, Map.class)).thenThrow(new RuntimeException("API call failed"));

		assertThrows(Exception.class, () -> weatherStackProvider.getWeather());
	}

}
