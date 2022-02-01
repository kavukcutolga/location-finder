package com.location.finder.application.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

/** Global exception handler for returning meaningful messages to API consumer. */
@ControllerAdvice
public class LocationFinderExceptionHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(LocationFinderExceptionHandler.class);

  /**
   * Handle WebExchangeBindException which is thrown by the Spring validator.
   *
   * @param e exception instance.
   * @return ValidationError instance that holds the validation details/
   */
  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ValidationError onWebExchangeBindException(WebExchangeBindException e) {
    ValidationError error = new ValidationError();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      error
          .getViolations()
          .add(new ValidationResult(fieldError.getField(), fieldError.getDefaultMessage()));
    }
    return error;
  }

  /**
   * Handle IllegalArgumentException which is thrown by service implementations.
   *
   * @param e exception instance.
   * @return ValidationError instance that holds the validation details/
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ValidationError onIllegalArgumentException(IllegalArgumentException e) {
    ValidationError error = new ValidationError();
    error.getViolations().add(new ValidationResult(null, e.getMessage()));
    return error;
  }

  /**
   * Handle unknown exceptions.
   *
   * @param e exception instance.
   * @return always return null to prevent system information to leak as exception can include any
   *     kind of system information.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  Object onGenericException(Exception e) {
    logger.error("Unexpected error occurred", e);
    return null;
  }
}
