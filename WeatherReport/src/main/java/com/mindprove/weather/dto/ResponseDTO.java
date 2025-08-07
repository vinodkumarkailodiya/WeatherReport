package com.mindprove.weather.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ResponseDTO {

	private String message;
	private Object object;
	private String failureDescription;
	private HttpStatus httpStatus;
	private LocalDateTime localDateTime;
	
	public ResponseDTO(String message,Object object,String failureDescription,HttpStatus httpStatus) {
		this.message=message;
		this.object=object;
		this.failureDescription=failureDescription;
		this.httpStatus=httpStatus;
		this.localDateTime=LocalDateTime.now();
	}
}
