package com.mindprove.weather.exception;
@SuppressWarnings({ "serial" })
public class BadRequestException extends RuntimeException {

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message) {
		super(message);
	}

}
