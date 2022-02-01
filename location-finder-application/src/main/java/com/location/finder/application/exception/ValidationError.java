package com.location.finder.application.exception;

import java.util.ArrayList;
import java.util.List;

/** Error structure for bad requests. */
public class ValidationError {

  /** List of validation issues. */
  private final List<ValidationResult> violations = new ArrayList<>();

  public List<ValidationResult> getViolations() {
    return violations;
  }
}
