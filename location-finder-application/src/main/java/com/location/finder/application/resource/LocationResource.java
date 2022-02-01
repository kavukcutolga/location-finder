package com.location.finder.application.resource;

import com.location.finder.core.dto.LocationCriteria;
import com.location.finder.core.dto.LocationDTO;
import com.location.finder.core.service.LocationFinder;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

/**
 * LocationResource exposes a rest endpoint for searching for a location.
 *
 * <p>The location resource is a generic endpoint for searching any type of location. It returns a
 * rector stream, that pushes SSE events to client as they come from location finder.
 *
 * <p>Controller annotation makes this class a Spring Boot controller. RequestMapping annotation
 * defines the root path for this endpoint. Validated annotation enables Spring Boot to validate
 * user input which is tagged with @Valid
 */
@Controller
@RequestMapping(path = "/location")
@Validated
public class LocationResource {

  /**
   * MapLocation finder interface, the implementation will be injected by Spring CDI in the runtime
   * via constructor.
   */
  private final LocationFinder locationFinder;

  /**
   * The public constructor for the class, this constructor will be called by Spring CDI during
   * initial bean initialization. Using a constructor eliminates the need of using CDI annotations
   * and makes unit testing easier.
   *
   * @param locationFinder a LocationFinder implementation, which comes from location-finder-core
   *     package.
   */
  public LocationResource(LocationFinder locationFinder) {
    this.locationFinder = locationFinder;
  }

  /**
   * This method accepts the incoming location search request, and propagates the location search
   * criteria into the LocationFinder implementation.
   *
   * <p>@GetMapping annotation exposes this method as an HTTP GET endpoint under the root path of
   * this rest resource.
   *
   * @param locationCriteria LocationCriteria is an input POJO that holds all the search filters.
   *     Spring can convert the query parameters into a POJO, which makes code more readable as
   *     there are 5 parameters available.
   * @return Flux<LocationDTO> which is a stream of locations. The stream is published as SSE events
   *     over an HTTP connection.
   */
  @GetMapping
  public @ResponseBody Flux<LocationDTO> getLocations(@Valid LocationCriteria locationCriteria) {
    return locationFinder.findByCoordinates(locationCriteria);
  }
}
