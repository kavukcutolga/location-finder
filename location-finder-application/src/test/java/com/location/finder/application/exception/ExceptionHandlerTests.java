package com.location.finder.application.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

class ExceptionHandlerTests {

  @Test
  void testWebExchangeBindException() {
    MethodParameter mockMethodParameter = mock(MethodParameter.class);
    BindingResult mockBindingResult = mock(BindingResult.class);
    LocationFinderExceptionHandler locationFinderExceptionHandler =
        new LocationFinderExceptionHandler();

    WebExchangeBindException exception =
        new WebExchangeBindException(mockMethodParameter, mockBindingResult);
    doReturn(List.of(new FieldError("objectName", "longitude", "Should be less than 180")))
        .when(mockBindingResult)
        .getFieldErrors();

    ValidationError validationError =
        locationFinderExceptionHandler.onWebExchangeBindException(exception);
    assertThat(validationError.getViolations().size(), equalTo(1));
    assertThat(
        validationError.getViolations().get(0).getMessage(), equalTo("Should be less than 180"));
  }

  @Test
  void testIllegalArgumentException() {

    LocationFinderExceptionHandler locationFinderExceptionHandler =
        new LocationFinderExceptionHandler();

    ValidationError validationError =
        locationFinderExceptionHandler.onIllegalArgumentException(
            new IllegalArgumentException("Search type is not supported"));
    assertThat(validationError.getViolations().size(), equalTo(1));
    assertThat(
        validationError.getViolations().get(0).getMessage(),
        equalTo("Search type is not supported"));
  }

  @Test
  void testUnknownException() {

    LocationFinderExceptionHandler locationFinderExceptionHandler =
        new LocationFinderExceptionHandler();

    Object error =
        locationFinderExceptionHandler.onGenericException(
            new RuntimeException("An error occurred"));
    assertThat(error, is(nullValue()));
  }
}
