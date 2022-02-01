package com.location.importer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class initializes the command line application that is based on Spring Boot for data import.
 */
@SpringBootApplication(
    scanBasePackages = {
      "com.location.finder.mongo",
      "com.location.importer",
      "com.location.finder.application"
    })
public class MongoDBDataImporterApplication implements CommandLineRunner {

  /** The data import service for reading food truck csv and inserting into MongoDB. */
  private final CSVToGeoJSONConverter csvToGeoJSONConverter;

  public MongoDBDataImporterApplication(CSVToGeoJSONConverter csvToGeoJSONConverter) {
    this.csvToGeoJSONConverter = csvToGeoJSONConverter;
  }

  /**
   * This is the main method that is triggered by Spring CommandLineRunner.
   *
   * @param args command arguments.
   */
  @Override
  public void run(String... args) {
    csvToGeoJSONConverter.importFromCsv();
    System.exit(0);
  }

  /**
   * Entrypoint for the application.
   *
   * @param args arguments from command line.
   */
  public static void main(String[] args) {
    SpringApplication.run(MongoDBDataImporterApplication.class, args);
  }
}
