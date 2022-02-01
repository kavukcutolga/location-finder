package com.location.finder.model;

import java.util.Objects;

/**
 * MapLocation is a base class that represents the GeoJSON data model as a POJO. This is an abstract
 * class because, implementation specific models have special additions like annotations, methods.
 * So, any datasource should drive the data model from this base POJO without the need of adjusting
 * the fields.
 */
public abstract class MapLocation {

  /**
   * Unique id of the location. If the implementation uses a document model, implementation will not
   * need to make modifications, because id field is mapped to document id automatically in most
   * document ORMs.
   */
  private String id;

  /** LocationCoordinates of the location. */
  private LocationCoordinates location;

  /** Name of the location. */
  private String name;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocationCoordinates getLocation() {
    return location;
  }

  public void setLocation(LocationCoordinates location) {
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MapLocation that = (MapLocation) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
