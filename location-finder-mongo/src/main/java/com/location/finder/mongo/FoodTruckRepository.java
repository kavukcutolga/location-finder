package com.location.finder.mongo;

import com.location.finder.core.datasource.LocationRepository;
import com.location.finder.model.MapLocation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * FoodTruckRepository is a reactive MongoDB repository that implements the LocationRepository. The
 * implementation of this interface is generated by Spring Data in runtime. So, beans that are
 * injecting the interface will receive an implementation. This repository will only return
 * FoodTruck locations as FoodTruck data is a separate collection in the MongoDB.
 *
 * <p>Repository annotation marks this interface as a repository declaration and provides a
 * user-friendly bean name so that it becomes easier to lookup.
 */
@Repository("FoodTruck")
public interface FoodTruckRepository
    extends ReactiveMongoRepository<FoodTruck, String>, LocationRepository {

  /**
   * This method defines the query to be executed on MongoDB server reactively. It uses a
   * parameterized query for preventing injection attacks. The generated implementation will use the
   * method arguments in order to set query parameters.
   *
   * @param longitude The longitude of the center location to search for other locations.
   * @param latitude The latitude of the center location to search for other locations.
   * @param maxDistance The max distance to search from center, in kilometers.
   * @return Flux<MapLocation> a stream of food truck locations.
   */
  @Override
  @Query(
      "{ location: "
          + "{ $nearSphere: { $geometry: { type: \"Point\", coordinates: [ ?0, ?1 ] }, $maxDistance: ?2 } } "
          + "}")
  Flux<MapLocation> findByCoordinates(double longitude, double latitude, double maxDistance);
}