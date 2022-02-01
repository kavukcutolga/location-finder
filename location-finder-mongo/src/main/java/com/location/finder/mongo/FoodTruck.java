package com.location.finder.mongo;

import com.location.finder.model.MapLocation;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A model that defines how FoodTruck represented in a MongoDB database.
 *
 * <p>Document annotation marks this class as a model and populates collection name.
 */
@Document("food-trucks")
public class FoodTruck extends MapLocation {}
