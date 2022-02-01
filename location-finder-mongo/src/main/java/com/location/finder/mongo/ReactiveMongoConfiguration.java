package com.location.finder.mongo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * The base configuration for enabling reactive Mongo spring repository.
 *
 * <p>Configuration annotation make this class a configuration bean which can be picked up the
 * Application Context.
 *
 * <p>EnableAutoConfiguration let's ReactiveMongoRepository autoconfiguration to configure MongoDB
 * connection automatically. It also disables non-reactive configurator to avoid conflicts.
 *
 * <p>EnableReactiveMongoRepositories enables scanning of Spring Repositories that implements
 * ReactiveMongoRepository
 */
@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
@EnableReactiveMongoRepositories
public class ReactiveMongoConfiguration {}
