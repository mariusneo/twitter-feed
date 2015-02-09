Tweets importer tool
================================

Simple application responsible for retrieving new tweets from Twitter API via twitter4j-stream library.
In order to speed up the import of the requests, the tweets are inserted in the database asynchronously
by using @Async mechanism provided by Spring framework.