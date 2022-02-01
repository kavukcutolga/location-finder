package com.location.finder.application.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

/** POJO for returning validation violations. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationResult {

  /** Name of the field that was violated. */
  private final String fieldName;

  /** Message to return as part of the response. */
  private final String message;

  /**
   * Constructor for validation result.
   *
   * @param fieldName name of the field that was violated.
   * @param message message to be returned as part of response.
   */
  public ValidationResult(String fieldName, String message) {
    this.fieldName = fieldName;
    this.message = message;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getMessage() {
    return message;
  }
}
