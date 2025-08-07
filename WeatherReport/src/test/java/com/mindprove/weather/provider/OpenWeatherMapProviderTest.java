package com.mindprove.weather.provider;

import com.mindprove.weather.model.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenWeatherMapProviderTest {

	private RestTemplate restTemplate;
	private OpenWeatherMapProvider provider;

	@BeforeEach
	void setUp() {
		restTemplate = mock(RestTemplate.class);
		provider = new OpenWeatherMapProvider(restTemplate);
		ReflectionTestUtils.setField(provider, "apiKey", "dummy-api-key");
		ReflectionTestUtils.setField(provider, "city", "melbourne");
	}

	@Test
	void testReturnsWeatherWhenApiResponseIsValid() throws Exception {
		Map<String, Object> main = new HashMap<>();
		main.put("temp", 289.5);
		Map<String, Object> wind = new HashMap<>();
		wind.put("speed", 5.6);
		Map<String, Object> response = new HashMap<>();
		response.put("main", main);
		response.put("wind", wind);
		when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);
		Weather weather = provider.getWeather();
		assertNotNull(weather);
		assertEquals(289.5, weather.getTemperature());
		assertEquals(5.6, weather.getWindSpeed());
	}

	@Test
	void testReturnsNullWhenApiResponseIsNull() throws Exception {
		when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

		Weather weather = provider.getWeather();
		assertNull(weather);
	}

	@Test
	void testThrowsClassCastExceptionWhenTemperatureIsInvalid() {
		Map<String, Object> main = new HashMap<>();
		main.put("temp", "not-a-number");
		Map<String, Object> wind = new HashMap<>();
		wind.put("speed", 4.0);
		Map<String, Object> response = new HashMap<>();
		response.put("main", main);
		response.put("wind", wind);
		when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);
		assertThrows(ClassCastException.class, () -> provider.getWeather());
	}

	@Test
	void testThrowsNullPointerExceptionWhenWindSectionIsMissing() {
		Map<String, Object> main = new HashMap<>();
		main.put("temp", 300.0);

		Map<String, Object> response = new HashMap<>();
		response.put("main", main); 

		when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

		assertThrows(NullPointerException.class, () -> provider.getWeather());
	}

	@Test
	void testThrowsExceptionWhenRestTemplateFails() {
		when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("API call failed"));

		assertThrows(Exception.class, () -> provider.getWeather());
	}
}
