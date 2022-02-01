package com.location.finder.core.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * LocationCriteria is a POJO class to represent all the parameters that location search can take.
 * The parameters are tagged with @javax.validation constraints to ensure proper criteria is passed
 * to the data source.
 */
public class LocationCriteria {

  /** Longitude of the center location to search for. */
  @Min(-180)
  @Max(180)
  @NotNull
  private double longitude;

  /** Latitude of the center location to search for. */
  @Min(-180)
  @Max(180)
  @NotNull
  private double latitude;

  /** Number of locations to retrieve from search result. */
  @Min(1)
  @Max(20)
  private int numberOfLocations = 5;

  /** The search radius from the center locations in miles. */
  @Min(1)
  @Max(10)
  private int radius = 5;

  /**
   * The type of locations to search, the type value will be mapped to a particular data source for
   * searching for a specific type of locations. For example, FoodTruck is implemented by default,
   * and it will map to a specific MongoDB db data source, which implements LocationRepository
   * interface. The data type could have been an Enum, but it would be less dynamic.
   *
   * <p>The value is mandatory and must be provided via query parameter.
   */
  @NotBlank private String type;

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public int getNumberOfLocations() {
    return numberOfLocations;
  }

  public void setNumberOfLocations(int numberOfLocations) {
    this.numberOfLocations = numberOfLocations;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
