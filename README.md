# Location Finder

Location Finder is a skeleton setup to create custom location search applications. Location Finder is useful
when you want to create your own location search API but not sure where to start. This codebase has only 150 lines of code (less lines than Readme or Javadocs) for the
whole backend system split into few modules, and few additional components for data import and UI.

You can also jump into the Demo before checking the details, which helps you find the nearest food trucks in San Francisco. 🚚 🌮

Demo: https://desolate-taiga-61470.herokuapp.com/ (First access may take 10-20 seconds, as application pauses after a period of inactivity on Heroku)

## Location Finder Overview

### Location Finder Use Case

- Exposing a custom location dataset to consumers on top of a location search API.
- The search feature should be exposed as a Rest Service. The rest endpoint should take search center,
  search radius and number of results to be returned as inputs.
- The search response should include the Longitude, Latitude and Name of the location so that it can be shown on a map.
- Search application should aim returning a response under 50ms %95 of the time with high availability and scalability.

### Additional features not considered

This is the list of features and use cases that can be added to the Location Finder but not considered at the moment due to time constraints.

- **Authentication and Authorization:** The usage of Rest Endpoint can be authorized with an OAuth 2.0 Identity Provider.
  The service usage limits can be defined per user to prevent abuse of the service. A premium subscription can be introduced
  for high-volume traffic. Users can also be identified using same Identity provider by adding oidc scope to the Oauth 2.0 Authorization Code flow to personalize the application.
- **Management portal with RBAC:** Currently, location data import is done via a command line interface using database credentials. First, data import
  operation should be a separate administrative endpoint, direct communication to database should be prevented. Secondly, the data import must be done by privileged users
  who have a particular role assigned in the Identity Provider. So Location Finder service should check the access token validity and role claims before any other data operation.
  Ideally, access token should be encrypted using JWE specification.
- **Modern user interface:** Current user interface is implemented with plain Javascript, HTML and CSS. However, a modern UI can be implemented with frameworks
  like React or Flutter, which allows generating PWA and Native Mobile applications.
- **Advanced Caching:** Currently a caching mechanism is not considered as underlying database infrastructure can index the location data and
  can easily return search results under 30ms. However, a smart caching mechanism can be added for city centers or high-population areas. The caching mechanism
  would keep a copy of locations in memory for most-frequently-searched areas, and run the search if the search fits into location range of in-memory data. This will require implementing
  an efficient caching and search algorithm.
- **Enable data encryption:** Enable encryption in transit for MongoDB using SSL/TLS, also enable encryption at rest if user data is added to application.
- **Duplicate detection:** Example Food Truck data contains multiple entries for different days for the same truck. The schedule can be taken into account. 
- **Enable WebSockets:** The location data is being pushed by Spring WebFlux using Server Send Events semantics, which does not react to user activity on the browser. Actually, RxJs supports websockets
  and locations can be published reactively as user scrolls through map. This would complete the end-to-end reactive flow.

### Architecture 

Location Finder consist of two main components, Spring Boot application with WebFlux support and MongoDB database. You will find
the motivation behind using these two platforms in this section. Main drivers of this architecture;

- Using specifications and standards as much as possible.
- Designing for scalability and availability as the location data is not transactional.
- Enabling reactive semantics for optimal resource usage.
- Avoiding intensive usage of third-party libraries to reduce the security surface.
- Separating application into multiple components for future extensibility and reacting ever-changing third party vulnerabilities when needed.

#### Data Architecture

As this architecture aims to use specifications as much as possible; GeoJSON model becomes the best option after investigating cloud offerings and open source databases. 

It has been observed that all major public cloud providers support the GeoJSON format for geospatial queries as well as geospatial indexing.
So it becomes clear that GeoJSON data will be stored in a document db instead of a relational database. Azure CosmosDB, AWS DynamoDB and GCP Big Query supports the GeoJSON format
in case application deployed on a managed document database.

**Alternatives Investigated:** WKT data model was also investigated, indeed WKT is more comprehensive than GeoJSON. It allows users to define a coordinate system and make more 
sophisticated calculations. However, it would take quite effort to represent the WKT system in the application model, and it would require making transformations to return easy to understand
API responses. It also becomes a problem to maintain coordinate system when we want to move from a database to another.

After selection of the data format, MongoDB was selected as the target database for LocationFinder. Mainly for few reasons;

1. MongoDB is a well-maintained open source document database with good support of GeoJSON format.
2. MongoDB can support reactive queries and Spring Boot has out-of-the-box integration support for reactive integration.
3. MongoDB has sharding capabilities for distributing load across instances in a cluster and supports active-active clustering with various consistency levels.
4. Major cloud providers have support for MongoDB API such as Azure CosmosDB or AWS Document DB if a managed planet-scale data source required.
5. MongoDB supports Geospatial index which speeds-up location search operation.

#### Application Architecture

The location finder application uses Java 11, Spring Boot with Spring WebFlux for reactive stack. There are various reasons to use this stack;

1. The developer of this project is quite comfortable with Java and Spring Boot. Which means common, well-known performance and security issues 
   are already considered in deep during development.
2. Java is preferred as the programming language, as it provides great OOP capabilities and type-safety. The recent developments in the Java landscape such as
   GraalVM removes the potential overhead of heap memory in large scale. Major tech companies also support Java, which means community is strong.
3. Spring has very good support of Reactive MongoDB database connection and Reactive Web Layer. Especially, Spring's auto configurators eliminates boilerplate
   code and reduces complexity.

**Alternative:** An alternative framework would be Quarkus instead of Spring Boot. Quarkus has better support for Java JAX-RS and JSR 365 (CDI) specifications.
So, that would make the application more abstract in terms of web framework but Quarkus lacks a built-in ORM-like support for Reactive MongoDB connection. As the application
consist of multiple modules, it will be easier to switch another framework later if needed. 

#### Application Components

`location-finder-application`: This module has only application initialization classes, and imports all other sub-modules for composing the Spring Boot application context.
It also scans for the beans in other modules to expose desired features. This module already decouples most of the Spring Boot dependencies for the applicaiton.
Additionally, static files for UI served by this module.

`location-finder-core`: This is the main module where the core interfaces and core logic resides. Any other implementation can be derived and composed with the abstractions
in this package. The interfaces expect successors to implement reactive streams (Project Reactor's Flux) for Database and Web Layer.

**Why reactive:** Using reactive streams increases the performance by efficiently using a special thread called event-loop. This mechanism uses NIO (non-blocking IO)
to communicate with downstream systems such as database if they support. In this case, MongoDB has a reactive connection support, which not only uses NIO but also enables
back-pressure mechanism. The back-pressure mechanism prevents application to crash and lowers memory-print when load increases. It fits into this architecture because
application does not make any compute or memory intensive operations, it simply maintains the integration between HTTP and database layer.

`location-finder-data-model`: As the architecture designed to serve any type of location data from any data source, a generic/shared GeoJSON data model needs to be defined.
The generic data model can be utilized by specific data source integrations. The generic data model is also required to keep core logic decoupled from data source implementations.
So, additional data sources can also be implemented outside this project and their artifacts can be added to classpath as plugins.

`location-finder-mongo`: This module has a specific implementation of data source interfaces in `location-finder-core` and `location-finder-data-model` for MongoDB,
that is imported by `location-finder-application` to compose final classpath. This module establishes the reactive MongoDB connections. Module has a 
default implementation for Food Truck data model as well.

`location-importer-mongo`: This is a cli application, which loads the Food Truck data into a MongoDB database. `location-finder-mongo` implementation was used to establish
connections to MongoDB with core data models in `location-finder-data-model`. Similarly, third-party import tools or microservices can be developed instead of a cli tool.

## Getting started

### Local Development & Installation

#### Prerequisites
 
- Java 11+ https://www.microsoft.com/openjdk
- Maven 3.6+ https://maven.apache.org
- Docker https://www.docker.com/products/docker-desktop
- Bing API KEY (Only for UI) https://docs.microsoft.com/en-us/bingmaps/getting-started/bing-maps-dev-center-help/getting-a-bing-maps-key

Set the `const bingApiKey = '<your_key>';` value with your bing API KEY under `src/main/resources/static/src/index.js`

> I recommend using Git Bash for running below commands on Windows for better compability. But you can also set environment variables on your Windows environment, and you can 
  run Maven, Docker commands as is.

Start the local MongoDB and Management UI with docker-compose

```bash
cd local
docker-compose up -d # or docker compose up -d (for latest docker versions)
```

>If you are not comfortable using Docker due to licencing problems, you can also create a free MongoDB Atlas database. In that case you will be using the Atlas Platform's
 UI to create initial `location-database`. https://www.mongodb.com/cloud/atlas/register

After docker containers start, you can access to the MongoDB management UI via http://localhost:8081

![Admin UI](local/doc/mongo-express.png)

Create a new database `location-database`. The collection `food-trucks` will be automatically created by the importer. Run the importer by setting connection settings

```bash
export SPRING_DATA_MONGODB_URI=mongodb://root:example@localhost:27017/  
# Use export SPRING_DATA_MONGODB_URI=mongodb+srv://<your_user>:<your_password>@<your_atlas_host>/ in case of Atlas
export SPRING_DATA_MONGODB_DATABASE=location-database

cd location-importer-mongo
mvn spring-boot:run

INFO 11625 --- [           main] c.l.importer.CSVToGeoJSONConverter       : Inserted 596 records
INFO 11625 --- [           main] c.l.importer.CSVToGeoJSONConverter       : Created GEO_2DSPHERE index
```

The San Francisco Food Truck location data under `src/main/resources/Mobile_Food_Facility_Permit.csv` is imported as GeoJSON at this stage.

Now you can run the application, go back to `location-finder-application` module and start the application.

```bash
cd location-finder-application 
mvn spring-boot:run

INFO 11686 --- [localhost:27017] org.mongodb.driver.connection            : Opened connection [connectionId{localValue:1, serverValue:21}] to localhost:27017
INFO 11686 --- [localhost:27017] org.mongodb.driver.connection            : Opened connection [connectionId{localValue:2, serverValue:20}] to localhost:27017
INFO 11686 --- [localhost:27017] org.mongodb.driver.cluster               : Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, minWireVersion=0, maxWireVersion=13, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=65878848}
INFO 11686 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
INFO 11686 --- [           main] c.l.f.a.LocationFinderApplication        : Started LocationFinderApplication in 6.585 seconds (JVM running for 6.901)
```

You can access the application via http://localhost:8080, (make sure that you have configured bing API KEY)

![App UI](local/doc/finder-ui.png)

You can scroll through the map to get new recommendations.

You can also use curl to invoke the API with your own inputs.
```bash
curl --location --request GET 'http://localhost:8080/location?longitude=-122.414408&latitude=37.784683&type=FoodTruck&numberOfLocations=5&radius=5'
```
## Deployment (Heroku, and Recommended)

Heroku provides a free-tier application runtime for java applications. So, it's possible to deploy the Location Finder to Heroku by creating a free-tier MongoDB Atlas database.

- https://www.mongodb.com/cloud/atlas/register 
- http://heroku.com

1. Create a new Atlas MongoDB database and get credentials. Follow the location importer steps for initial data by setting the environment variables for MongoDB connection.

```bash
export SPRING_DATA_MONGODB_URI=mongodb+srv://<your_user>:<your_password>@<your_atlas_host>/
export SPRING_DATA_MONGODB_DATABASE=<your_database>
```
2. Create a new heroku application by following below steps in the root folder. Two files called `system.properties` and `Procfile` are provided as part of the codebase to enable deployment

```bash
heroku login

heroku create
git push heroku master
```

This will build and deploy your application, but your application has not started yet. First, set the MongoDB Atlas database configuration via heroku portal. Follow 'Application -> Settings -> Config Var'
flow on the dashboard `https://dashboard.heroku.com/apps`. After setting the values set the node count to 1.

```bash
heroku ps:scale web=1 
```

At this stage your application is ready to use.

_Heroku is mainly selected for the usage of a free tier plan without providing any billing information. But free tier resources are quite limited, 
Heroku automatically scales the instance count 0 after a period of inactivity_.

**Alternative:** A real-world deployment model would be fairly different. A recommended deployment model includes;

- Serving the location data over a planet-scale MongoDB compatible Document Database like Azure CosmosDB with eventual consistency. 
- Packaging the `location-finder-application` as a Native Binary using GraalVM and publishing a Container Image.
- Deploying the Container Image to a PaaS service like Azure App Service into multiple regions with autoscaling enabled.
- Using cloud native identities for Authentication and Authorization on the Cloud Native document database, such as Azure MSI.
- Deploying a Traffic Manager like Azure Traffic Manager to route traffic geographically.
- Serving static files for UI through a CDN instead of a web application bundle.

## Code Formatting & Unit Tests

The source code is formatted based on Google Java format. A maven plugin helps to check and format the code consistently.

Check the format

```bash
mvn com.coveo:fmt-maven-plugin:2.10:check
```

Format the code

```bash
mvn com.coveo:fmt-maven-plugin:2.10:format
```

Run the unit tests

```bash
mvn test
```

## Postman & OpenAPI Specification

- You can find a postman collection under `local/postman` folder, the collection has an example API request.
- You can find an Open API 3.0 definition for this service under `local/open-api` folder

Open API is an open source and vendor natural specification for defining API models. There are many tools and platforms in the market to
generate a client-sdk in any language, or to generate a cli package for all operating systems. Hence, OpenAPI is preferred as an API specification.

Postman is a popular API execution platform, which allows sharing API collections. Considering developer adaptation, a postman collection was also prepared. 

## Monitoring

The Spring Boot application is configured to expose Prometheus metrics via Micrometer library. These two platforms are selected for few reasons;

- Micrometer is a platform independent instrumentation library, that can collect metrics with built-in bindings. Micrometer provides seamless integration
  with various Public Cloud or Open source monitoring solutions.
- Prometheus is an open source time series data system, which is supported by many platforms and frameworks.

You can retrieve the metrics via `http://localhost:8080/actuator/prometheus` and visualize them with a tool like https://metricat.dev/

## Application Configurations

Below you will find the list of configurations directly applicable to the application. Spring Boot has a relaxed binging mechanism for configurations,
which means you can represent `spring.data.mongodb.uri` property as environment variable `SPRING_DATA_MONGODB_URI`. That means, it's possible to apply
all other Spring Boot configurations to this application as well.

| Configuration Name                        | Description                                                                                   |
|-------------------------------------------|-----------------------------------------------------------------------------------------------|
| SPRING_DATA_MONGODB_URI                   | The connection string of MongoDB                                                              |
| SPRING_DATA_MONGODB_DATABASE              | MongoDB database name                                                                         |
| CORS_ALLOWED_ORIGIN                       | A custom allowed origin configuration to prevent unauthorized browser apps to consume the API |
| MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE | List of actuator endpoints to include for monitoring.                                         |
| SERVER_ERROR_WHITELABEL_ENABLED           | Configuration to disable default Spring white label error page to prevent system info leak    |

## Learnings

- The reactive support of the modern frameworks is impressive, you can get a reactive web service up and running in just few hours on top of a database if you know what you are looking for. 
  Preparing this repository was a good refresher exercise. But it's recommended to have full understanding of reactive streams before using this reference.
- Location data was something new for me, GeoJSON specification and support around it is impressive. I thought I might end up implementing a location match algorithm on top of a NoSQL or Relational database.
- Embedding an interactive map into a website is easier than I anticipated. Initially I thought of just having a static map (or link with coordinates) 
  as a demo but Bing Map has many user events that can be used to trigger a new calculation in few lines of javascript code.
- I found Quarkus framework is promising with the good support of Java specifications but did not go into that direction to time-box this exercise.
