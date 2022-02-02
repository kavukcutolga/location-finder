package com.location.finder.application.config;

/** Constants for web layer configurations. */
public class WebConstants {

  /** Private contractor to prevent inheritance. */
  private WebConstants() {}

  /** The max age of cors headers. */
  protected static final int CORS_HEADER_MAX_AGE = 3600;

  /** Methods exposes by the application for cors filter. */
  protected static final String[] ALLOWED_METHODS_FOR_APP = {"OPTIONS", "GET"};

  /** Root path pattern of the application for static content. */
  protected static final String ROOT_PATH_PATTERN = "/**";

  /** Location of the static content under classpath. */
  protected static final String STATIC_CONTENT_LOCATION = "/static/";

  /** Root application path for redirection to index.html. */
  protected static final String ROOT_PATH = "/";

  /** Location of index.html. */
  protected static final String INDEX_HTML_LOCATION = "classpath:/static/index.html";
}
