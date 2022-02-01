package com.location.finder.core.datasource;

import com.location.finder.model.MapLocation;
import reactor.core.publisher.Flux;

/**
 * LocationRepository is a base interface for all datasource's, that can search a particular type of
 * location. The successors of this interface, should initialize a bean with a non-default
 * identifier because LocationService implementation injects all the instances of the
 * LocationRepository interface and picks the correct data source implementation to perform the
 * location search.
 */
public interface LocationRepository {

  /**
   * This method finds the closest locations based on given center coordinates. The caller of this
   * method, should take the first n items from the stream as these records are returned ordered by
   * distance.
   *
   * <p>
   *
   * @param longitude The longitude of the center location to search for other locations.
   * @param latitude The latitude of the center location to search for other locations.
   * @param maxDistance The max distance to search from center, in kilometers.
   * @return Flux<MapLocation> a stream of locations ordered by distance.
   */
  Flux<MapLocation> findByCoordinates(double longitude, double latitude, double maxDistance);
}
