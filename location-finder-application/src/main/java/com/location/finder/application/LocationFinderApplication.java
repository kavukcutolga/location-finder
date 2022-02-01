package com.location.finder.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is an initialization class for the Spring Boot Application.
 *
 * <p>SpringBootApplication annotation marks this application as a Spring Boot application and scans
 * for beans in the dependencies. We do not use wildcard package names for bean scanning, instead we
 * use explicit package because it may cause unexpected/malicious/unsecure beans to be initialized.
 */
@SpringBootApplication(
    scanBasePackages = {
      "com.location.finder.mongo",
      "com.location.finder.core",
      "com.location.finder.application"
    })
public class LocationFinderApplication {

  /**
   * The main class of the location finder application.
   *
   * @param args command line arguments to configure the Spring Boot app.
   */
  public static void main(String[] args) {
    SpringApplication.run(LocationFinderApplication.class, args);
  }
}
