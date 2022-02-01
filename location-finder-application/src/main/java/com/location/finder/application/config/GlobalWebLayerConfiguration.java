package com.location.finder.application.config;

import static com.location.finder.application.config.WebConstants.ALLOWED_METHODS_FOR_APP;
import static com.location.finder.application.config.WebConstants.CORS_HEADER_MAX_AGE;
import static com.location.finder.application.config.WebConstants.INDEX_HTML_LOCATION;
import static com.location.finder.application.config.WebConstants.ROOT_PATH;
import static com.location.finder.application.config.WebConstants.ROOT_PATH_PATTERN;
import static com.location.finder.application.config.WebConstants.STATIC_CONTENT_LOCATION;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Global configuration for initializing web layer securely. The allowed origin property must be
 * configured to ensure only authorized web applications can consume the API.
 */
@Configuration
@EnableWebFlux
public class GlobalWebLayerConfiguration implements WebFluxConfigurer {

  /** The origin hostname where UI is hosted. */
  private final String allowedOrigin;

  /**
   * Contractor for initializing the configurator.
   *
   * @param allowedOrigin the origin that is allowed to access API.
   */
  public GlobalWebLayerConfiguration(@Value("${cors.allowed.origin}") String allowedOrigin) {
    this.allowedOrigin = allowedOrigin;
  }

  /**
   * The router configuration to serve static content.
   *
   * @return RouterFunction that servers the static content.
   */
  @Bean
  RouterFunction<ServerResponse> router() {
    return resources(ROOT_PATH_PATTERN, new ClassPathResource(STATIC_CONTENT_LOCATION));
  }

  /**
   * Router function to redirect root path to index.html.
   *
   * @param indexHtml content of the index.html page.
   * @return RouterFunction that returns the content of the index.html.
   */
  @Bean
  public RouterFunction<ServerResponse> indexRouter(
      @Value(INDEX_HTML_LOCATION) final Resource indexHtml) {
    return route(
        GET(ROOT_PATH), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
  }

  /**
   * Configure Spring Boot Cors headers based on allowed origin property.
   *
   * @param corsRegistry Spring Boot's cors registry to configure the headers.
   */
  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {
    corsRegistry
        .addMapping(ROOT_PATH_PATTERN)
        .allowedOrigins(allowedOrigin)
        .allowedMethods(ALLOWED_METHODS_FOR_APP)
        .maxAge(CORS_HEADER_MAX_AGE);
  }
}
