package com.location.finder.core.service;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.location.finder.core.datasource.LocationRepository;
import com.location.finder.core.dto.LocationCriteria;
import com.location.finder.model.LocationCoordinates;
import com.location.finder.model.MapLocation;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class LocationServiceTests {

  @Test
  void testFindByCoordinates() {
    LocationRepository locationRepository = mock(LocationRepository.class);
    List<MapLocation> mockLocations = new LinkedList<>();
    for (int i = 0; i < 50; i++) {
      MapLocation mapLocation = new TestLocation();
      mapLocation.setId(Integer.toString(i));
      mapLocation.setName(String.format("Location %s", i));
      LocationCoordinates locationCoordinates = new LocationCoordinates();
      locationCoordinates.setType("Point");
      locationCoordinates.setCoordinates(List.of(122.414408, 37.784683));
      mapLocation.setLocation(locationCoordinates);
      mockLocations.add(mapLocation);
    }
    doReturn(Flux.fromIterable(mockLocations))
        .when(locationRepository)
        .findByCoordinates(anyDouble(), anyDouble(), anyDouble());
    LocationService locationService = new LocationService(Map.of("FoodTruck", locationRepository));
    LocationCriteria locationCriteria = new LocationCriteria();
    locationCriteria.setLatitude(37.784683);
    locationCriteria.setLongitude(-122.414408);
    locationCriteria.setType("FoodTruck");
    StepVerifier.create(locationService.findByCoordinates(locationCriteria))
        .expectNextMatches(next -> next.getName().equals("Location 0"))
        .expectNextMatches(next -> next.getName().equals("Location 1"))
        .expectNextMatches(next -> next.getName().equals("Location 2"))
        .expectNextMatches(next -> next.getName().equals("Location 3"))
        .expectNextMatches(next -> next.getName().equals("Location 4"))
        .expectComplete();
  }

  @Test
  void testFindByCoordinatesError() {
    LocationRepository locationRepository = mock(LocationRepository.class);
    LocationService locationService = new LocationService(Map.of("FoodTruck", locationRepository));
    LocationCriteria locationCriteria = new LocationCriteria();
    locationCriteria.setType("FoodHall");

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> locationService.findByCoordinates(locationCriteria));

    Assertions.assertEquals("Location Type FoodHall is not supported", exception.getMessage());
  }

  static class TestLocation extends MapLocation {}
}
