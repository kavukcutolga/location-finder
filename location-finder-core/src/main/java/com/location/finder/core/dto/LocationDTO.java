package com.location.finder.core.dto;

/**
 * LocationDTO is the response model of the location search operation. This is an independent POJO
 * that is being converted from the data model. This POJO has two main purposes, one is to avoid
 * exposing the datasource entities directly to rest endpoints. Another reason is simplifying the
 * response format to make API spec more fluent.
 */
public class LocationDTO {

  /** Name of the location. */
  private String name;

  /** Latitude of the location. The value can vary between -180 and 180. */
  private double latitude;

  /** Longitude of the location. The value can vary between -180 and 180. */
  private double longitude;

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
