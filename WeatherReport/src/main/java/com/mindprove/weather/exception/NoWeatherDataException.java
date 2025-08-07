package com.mindprove.weather.exception;

public class NoWeatherDataException extends RuntimeException {

	public NoWeatherDataException() {
		super();
	}

	public NoWeatherDataException(String message) {
		super(message);
	}

}
