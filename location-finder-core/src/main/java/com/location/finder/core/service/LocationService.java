package com.location.finder.core.service;

import com.location.finder.core.datasource.LocationRepository;
import com.location.finder.core.dto.LocationCriteria;
import com.location.finder.core.dto.LocationDTO;
import com.location.finder.model.MapLocation;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * LocationService is a particular implementation of LocationFinder interface, which uses all the
 * LocationRepositories available in the runtime to run the search operation.
 *
 * <p>LocationService uses the MapLocation data model provided by location-finder-data-model
 * artifact which follows the GeoJSON spec. But eventually makes the necessary conversions to
 * publish a LocationDTO stream.
 */
@Service
public class LocationService implements LocationFinder {

  /**
   * The constant for converting miles into kilometers, as datasource expects the search radius in
   * kilometers.
   */
  private static final double KM_PER_MILE = 1609.34;

  /**
   * The map of all available location repositories. The key will be the bean name and value is the
   * instance of the implementation. Each implementation is responsible for searching for a
   * different type of location.
   */
  private final Map<String, LocationRepository> locationRepositories;

  /**
   * The public constructor of LocationService implementation, and it takes all the location
   * repositories available in the Spring Context. This constructor takes all the beans as Map, to
   * be able to look up very fast. Spring CDI takes care of preparing the Map and injecting into the
   * constructor.
   *
   * @param locationRepositories All the LocationRepository instances available in the Spring
   *     context.
   */
  public LocationService(Map<String, LocationRepository> locationRepositories) {
    this.locationRepositories = locationRepositories;
  }

  /**
   * This method is the main logic to start a stream with the given criteria. It finds the correct
   * data source based on given location type and starts a stream from the data source instance. The
   * stream is also stopped by this method based on requested number of locations.
   *
   * <p>Since this is a stream, all the operations are being executed asynchronously without
   * blocking the main thread. Please note that, we define the behaviour or flow by returning a
   * Flux.
   *
   * @param locationCriteria LocationCriteria provided by the user to perform the search.
   * @return Flux<LocationDTO> that is converted from a MapLocation data model.
   * @throws IllegalArgumentException if the requested location type is not recognized.
   */
  @Override
  public Flux<LocationDTO> findByCoordinates(LocationCriteria locationCriteria) {
    return Optional.ofNullable(locationRepositories.get(locationCriteria.getType()))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("Location Type %s is not supported", locationCriteria.getType())))
        .findByCoordinates(
            locationCriteria.getLongitude(),
            locationCriteria.getLatitude(),
            locationCriteria.getRadius() * KM_PER_MILE)
        .take(locationCriteria.getNumberOfLocations())
        .map(this::mapModelToDomainObject);
  }

  /**
   * Map a mapLocation model to a LocationDTO POJO.
   *
   * @param mapLocation mapLocation model to be converted.
   * @return LocationDTO instance converted from MapLocation model.
   */
  private LocationDTO mapModelToDomainObject(MapLocation mapLocation) {
    LocationDTO locationDTO = new LocationDTO();
    locationDTO.setName(mapLocation.getName());
    locationDTO.setLatitude(mapLocation.getLocation().getCoordinates().get(1));
    locationDTO.setLongitude(mapLocation.getLocation().getCoordinates().get(0));
    return locationDTO;
  }
}
