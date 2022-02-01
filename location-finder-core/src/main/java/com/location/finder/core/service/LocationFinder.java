package com.location.finder.core.service;

import com.location.finder.core.dto.LocationCriteria;
import com.location.finder.core.dto.LocationDTO;
import reactor.core.publisher.Flux;

/**
 * LocationFinder is an interface to define core search operation spec. This interface should be
 * used by user facing modules such as Rest endpoints.
 */
public interface LocationFinder {

  /**
   * This is the main interface to perform a search operation over a set of locations. The
   * implementations should return a stream and take all the criteria into account.
   *
   * @param locationCriteria LocationCriteria provided by the user to perform the search.
   * @return Flux<LocationDTO> a stream of LocationDTO POJO, which has all the locations published.
   */
  Flux<LocationDTO> findByCoordinates(LocationCriteria locationCriteria);
}
