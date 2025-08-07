package com.mindprove.weather.service;

import com.mindprove.weather.exception.BadRequestException;
import com.mindprove.weather.exception.NoWeatherDataException;
import com.mindprove.weather.model.Weather;
import com.mindprove.weather.provider.WeatherProvider;
import com.mindprove.weather.service.impl.WeatherCache;
import com.mindprove.weather.service.impl.WeatherServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WeatherServiceImplTest {

	private WeatherService weatherService;
	@Mock
	private WeatherCache weatherCache;
	@Mock
	private WeatherProvider provider;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		weatherCache = mock(WeatherCache.class);
		provider = mock(WeatherProvider.class);
		weatherService = new WeatherServiceImpl(weatherCache, List.of(provider));
	}

	@Test
	void testUsesProviderDataWhenCacheIsOutdated() throws Exception {
	    WeatherCache weatherCache = new WeatherCache();
	    WeatherServiceImpl weatherService = new WeatherServiceImpl(weatherCache, List.of(provider));
	    
	    Weather mockWeather = new Weather();
	    when(provider.getWeather()).thenReturn(mockWeather);
	    weatherCache.update(new Weather()); 
	    Thread.sleep(4000);
	    
	    Weather result = weatherService.getWeather("melbourne");
	    assertEquals(mockWeather, result);
	    verify(provider, times(1)).getWeather();
	}


	@Test
	void testReturnsWeatherFromProviderWhenCacheIsStale() throws Exception {
		Weather mockWeather = new Weather();
		when(weatherCache.isFresh()).thenReturn(false);

		when(provider.getWeather()).thenReturn(mockWeather);
		Weather result = weatherService.getWeather("melbourne");

		assertEquals(mockWeather, result);
		verify(weatherCache).update(mockWeather);
	}
	
	@Test
	void testReturnsFromSecondaryProviderWhenPrimaryFails() throws Exception{
	
	    WeatherProvider primaryProvider = mock(WeatherProvider.class);
	    when(primaryProvider.getWeather()).thenThrow(new RuntimeException("Primary failed"));
	    WeatherProvider secondaryProvider = mock(WeatherProvider.class);
	    Weather fallbackWeather = new Weather();
	    fallbackWeather.setTemperature(18.0);
	    fallbackWeather.setWindSpeed(10.0);
	    when(secondaryProvider.getWeather()).thenReturn(fallbackWeather);
	    when(weatherCache.isFresh()).thenReturn(false);
	    WeatherService weatherService = new WeatherServiceImpl(weatherCache, List.of(primaryProvider, secondaryProvider));
	    Weather result = weatherService.getWeather("melbourne");
	    
	    assertNotNull(result);
	    assertEquals(18.0, result.getTemperature());
	    assertEquals(10.0, result.getWindSpeed());
	    
	    verify(primaryProvider).getWeather();
	    verify(secondaryProvider).getWeather();
	    verify(weatherCache).update(fallbackWeather);
	}

	@Test
	void testReturnsStaleWeatherWhenProviderFails() throws Exception {
		Weather staleWeather = new Weather();
		when(weatherCache.isFresh()).thenReturn(false);

		when(provider.getWeather()).thenThrow(new RuntimeException("Mock failure"));

		when(weatherCache.get()).thenReturn(staleWeather);

		Weather result = weatherService.getWeather("melbourne");

		assertEquals(staleWeather, result);
		assertEquals(staleWeather.getWindSpeed(), result.getWindSpeed());
	}

	@Test
	void testThrowsExceptionWhenProviderFailsAndNoStaleDataAvailable() throws Exception {
		when(weatherCache.isFresh()).thenReturn(false);

		when(provider.getWeather()).thenReturn(null);
		when(weatherCache.get()).thenReturn(null);

		NoWeatherDataException e = assertThrows(NoWeatherDataException.class, () -> weatherService.getWeather("melbourne"));
		assertEquals("All weather providers failed and no stale data available.", e.getMessage());
	}

	@Test
	void testFallsBackToStaleCacheWhenProviderThrowsException() throws Exception {
		Weather stale = new Weather();
		when(weatherCache.isFresh()).thenReturn(false);
			when(provider.getWeather()).thenThrow(new RuntimeException("Provider error"));
		when(weatherCache.get()).thenReturn(stale);

		Weather result = weatherService.getWeather("melbourne");

		assertEquals(stale, result);
		assertEquals(stale.getWindSpeed(), result.getWindSpeed());
	}

	@Test
	void testThrowsBadRequestWhenCityIsNull() {
		BadRequestException b = assertThrows(BadRequestException.class, () -> weatherService.getWeather(null));
		assertEquals("Only the city melbourne is allowed.", b.getMessage());
	}

	@Test
	void testThrowsBadRequestWhenCityIsEmpty() {
		BadRequestException b = assertThrows(BadRequestException.class, () -> weatherService.getWeather(""));
		assertEquals("Only the city melbourne is allowed.", b.getMessage());
	}
	
}