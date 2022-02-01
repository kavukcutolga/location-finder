package com.location.importer;

import com.location.finder.model.LocationCoordinates;
import com.location.finder.mongo.FoodTruck;
import com.location.finder.mongo.FoodTruckRepository;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * A data importer to convert CSV based location data to GeoJSON format for Food Truck locations. It
 * also inserts the data into MongoDB collection.
 */
@Service
public class CSVToGeoJSONConverter {

  /** Slf4j Logger to provide progress information to operator. */
  Logger logger = LoggerFactory.getLogger(CSVToGeoJSONConverter.class);

  /** Spring resource field to represent CSV file. */
  private final Resource resource;

  /** Reactive food truck repository to populate location data to MongoDB. */
  private final FoodTruckRepository foodTruckRepository;

  /** Reactive MongoDB template for creating GeoSpatial indexes. */
  private final ReactiveMongoTemplate reactiveMongoTemplate;

  /** Counter to keep track of insert progress. */
  private int count = 0;

  /**
   * The public constructor to be called by Spring CDI context.
   *
   * @param resource Spring resource field to represent CSV file.
   * @param foodTruckRepository Reactive food truck repository to populate location data to MongoDB.
   * @param reactiveMongoTemplate Counter to keep track of insert progress.
   */
  public CSVToGeoJSONConverter(
      @Value("classpath:Mobile_Food_Facility_Permit.csv") Resource resource,
      FoodTruckRepository foodTruckRepository,
      ReactiveMongoTemplate reactiveMongoTemplate) {
    this.resource = resource;
    this.foodTruckRepository = foodTruckRepository;
    this.reactiveMongoTemplate = reactiveMongoTemplate;
  }

  /**
   * This method runs the end-to-end flow for reading, transforming and inserting Truck location
   * data. The main thread waits for stream to be completed as this is a temporary command line
   * application.
   */
  public void importFromCsv() {
    try (CSVReader reader = new CSVReader(Files.newBufferedReader(resource.getFile().toPath()))) {
      reader.skip(1); // skip for header
      Flux<FoodTruck> truckFlux = Flux.fromIterable(reader).distinct().map(this::mapToLocation);
      this.foodTruckRepository.saveAll(truckFlux).blockLast();
      logger.info("Inserted {} records", count);
      GeospatialIndex geospatialIndex = new GeospatialIndex("location");
      geospatialIndex.typed(GeoSpatialIndexType.GEO_2DSPHERE);
      this.reactiveMongoTemplate.indexOps("food-trucks").ensureIndex(geospatialIndex).block();
      logger.info("Created {} index", GeoSpatialIndexType.GEO_2DSPHERE);
    } catch (IOException exception) {
      logger.info("Cannot read the CSV file {} under resources", resource.getFilename());
    }
  }

  /**
   * Map a csv row into Location document.
   *
   * @param csvRow an array of Strings representing a csv row.
   * @return FoodTruck location instance.
   */
  private FoodTruck mapToLocation(String[] csvRow) {
    count++;
    if (count % 10 == 0) {
      logger.info("Inserted {} records", count);
    }
    FoodTruck foodTruck = new FoodTruck();
    foodTruck.setName(csvRow[1]);
    LocationCoordinates locationCoordinates = new LocationCoordinates();
    locationCoordinates.setType("Point");
    String locationData = csvRow[23];
    String[] coordinateValues =
        locationData
            .replace('(', Character.MIN_VALUE)
            .replace(')', Character.MIN_VALUE)
            .trim()
            .split(",");
    locationCoordinates.setCoordinates(
        List.of(Double.parseDouble(coordinateValues[1]), Double.parseDouble(coordinateValues[0])));
    foodTruck.setLocation(locationCoordinates);
    foodTruck.setId(csvRow[0]);
    return foodTruck;
  }
}
