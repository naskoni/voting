package com.naskoni.voting.controller;

import com.naskoni.voting.dto.ApiErrorDto;
import com.naskoni.voting.exception.InvalidPathVariableException;
import com.naskoni.voting.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@ResponseBody
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorDto handleNotFoundException(NotFoundException e) {
    log.error(e.getMessage(), e);

    return createApiError(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(InvalidPathVariableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorDto handleInvalidPathVariableException(InvalidPathVariableException e) {
    log.error(e.getMessage(), e);

    return createApiError(HttpStatus.BAD_REQUEST, e);
  }

  @ExceptionHandler(DataAccessException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public ApiErrorDto handleDataAccessException(DataAccessException e) {
    log.error(e.getMessage(), e);

    return createApiError(HttpStatus.SERVICE_UNAVAILABLE, e);
  }

  private ApiErrorDto createApiError(HttpStatus httpStatus, Exception e) {
    ApiErrorDto dto = new ApiErrorDto();
    dto.setStatus(httpStatus.value());
    dto.setMessage(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
    dto.setRootCauseMessage(ExceptionUtils.getRootCauseMessage(e));

    return dto;
  }
}
