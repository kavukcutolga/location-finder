package com.location.finder.model;

import java.util.List;

/** The representation of a location as a java POJO. */
public class LocationCoordinates {

  /**
   * The longitude and latitude of the location. So, the item at index 0 will be the longitude and
   * item at index 1 will be the latitude as per spec.
   *
   * @link https://geojson.org
   */
  private List<Double> coordinates;

  /**
   * Type of the location based on GeoJSON spec, possible values are. Point, LineString, Polygon,
   * MultiPoint, MultiLineString, and MultiPolygon.
   *
   * @link https://geojson.org
   */
  private String type;

  public List<Double> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<Double> coordinates) {
    this.coordinates = coordinates;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
