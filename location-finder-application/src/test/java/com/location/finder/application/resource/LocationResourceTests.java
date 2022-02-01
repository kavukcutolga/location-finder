package com.location.finder.application.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.location.finder.core.dto.LocationCriteria;
import com.location.finder.core.dto.LocationDTO;
import com.location.finder.core.service.LocationFinder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@WebFluxTest(controllers = LocationResource.class)
class LocationResourceTests {

  @Autowired private WebTestClient webTestClient;

  @MockBean private LocationFinder locationFinder;

  @Test
  void testGetLocations() {

    LocationDTO locationDTO = new LocationDTO();
    locationDTO.setName("Food Truck 1");
    locationDTO.setLongitude(-122.414408);
    locationDTO.setLatitude(37.784683);
    Flux<LocationDTO> result = Flux.just(locationDTO);

    doReturn(result).when(locationFinder).findByCoordinates(any(LocationCriteria.class));

    webTestClient
        .get()
        .uri(
            "/location?longitude=-122.414408&latitude=37.784683&type=FoodTruck&numberOfLocations=5&radius=5")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .json("[{\"name\":\"Food Truck 1\",\"latitude\":37.784683,\"longitude\":-122.414408}]");
  }
}
