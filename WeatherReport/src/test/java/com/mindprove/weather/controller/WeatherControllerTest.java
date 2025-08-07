package com.mindprove.weather.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mindprove.weather.constant.ResponseMessages;
import com.mindprove.weather.model.Weather;
import com.mindprove.weather.service.WeatherService;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private WeatherService weatherService;

	@Test
	public void 	testReturnsBadRequestWhenCityParameterIsMissing() throws Exception {
		mockMvc.perform(get("/api/v1/weather")).andExpect(status().isBadRequest());
	}

	@Test
	void testReturnsWeatherDataForValidCity() throws Exception {
		Weather mockWeather = new Weather();
		mockWeather.setTemperature(20.5);
		mockWeather.setWindSpeed(12.3);

		when(weatherService.getWeather("melbourne")).thenReturn(mockWeather);

		mockMvc.perform(get("/api/v1/weather").param("city", "melbourne")).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value(ResponseMessages.SUCCESS))
				.andExpect(jsonPath("$.object.temperature").value(20.5))
				.andExpect(jsonPath("$.object.windSpeed").value(12.3))
				.andExpect(jsonPath("$.failureDescription").doesNotExist());
	}

	@Test
	void testThrowsBadRequestWhenCityIsInvalid() throws Exception {
		when(weatherService.getWeather("sydney")).thenThrow(
				new com.mindprove.weather.exception.BadRequestException("Only the city melbourne is allowed."));

		mockMvc.perform(get("/api/v1/weather").param("city", "sydney")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(ResponseMessages.FAILED))
				.andExpect(jsonPath("$.failureDescription").value("Only the city melbourne is allowed."));
	}

	@Test
	void testReturnsInternalServerErrorWhenUnexpectedFailureOccurs() throws Exception {
		when(weatherService.getWeather("melbourne")).thenThrow(new RuntimeException("Unexpected failure"));

		mockMvc.perform(get("/api/v1/weather").param("city", "melbourne")).andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.message").value(ResponseMessages.FAILED))
				.andExpect(jsonPath("$.failureDescription").exists());

	}

}
