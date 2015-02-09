Twitter Feed
=============================================

The purpose of the project is to make some experimentation with twitter feeds in order to get an idea on how to
structure the tweets in order to make them available in a timely fashion when their number grows.

Tweets are imported via a quartz job directly from Twitter and stored in a Postgres database.

## Frameworks

There are different frameworks which are used within the project. In the lines below these frameworks and the purpose
for which they are used are enumerated.

### spring-boot

spring-boot-starter-jersey is used for delivering webservice functionality in the project service
In order to enable the Javascript client to receive directly the flow of tweets, there is JSONP functionality exposed
by the webservices.

spring-boot-starter-web and spring-boot-starter-thymeleaf frameworks are used for delivering web MVC functionality in
the project web.

spring-boot uses an embedded version of Tomcat 8 which makes the development & testing part quite fast in comparison
to the times when Tomcat was used in a different process.


### twitter4j

twitter4j-stream library is used in order to import the tweets from Twitter API.


### perf4j

The webservice calls are profiled in order to have a clear idea on how much time is spent for handling each REST
request in the project service.

### jquery

The javascript functionality on the web UI client side is implemented by using this library.

