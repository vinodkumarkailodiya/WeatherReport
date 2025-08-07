package com.mindprove.weather.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindprove.weather.constant.ResponseMessages;
import com.mindprove.weather.dto.ResponseDTO;
import com.mindprove.weather.service.WeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/")
public class WeatherController {

	private final WeatherService weatherService;

	@GetMapping("weather")
	public ResponseEntity<ResponseDTO> getWeather(@RequestParam String city) {
		 log.info("Received request for get weather");
		return ResponseEntity.ok(new ResponseDTO(ResponseMessages.SUCCESS, weatherService.getWeather(city),null, HttpStatus.OK));
	}
}
