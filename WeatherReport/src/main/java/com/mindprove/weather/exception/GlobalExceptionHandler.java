package com.mindprove.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mindprove.weather.constant.ResponseMessages;
import com.mindprove.weather.dto.ResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoWeatherDataException.class)
	public ResponseEntity<ResponseDTO> noWeatherDataException(NoWeatherDataException e){
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseDTO(ResponseMessages.FAILED, null,e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE));
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ResponseDTO(ResponseMessages.FAILED, null, e.getMessage(), HttpStatus.BAD_REQUEST));
    }
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO> handleException(Exception e){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(ResponseMessages.FAILED, null,e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ResponseDTO> handleBadRequestException(BadRequestException e){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(ResponseMessages.FAILED,null ,e.getMessage(), HttpStatus.BAD_REQUEST));
		
	}
	
}
