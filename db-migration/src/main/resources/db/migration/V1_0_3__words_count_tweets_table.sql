CREATE TABLE words_count_tweets
(
    tweet_id BIGINT PRIMARY KEY NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    status VARCHAR(16) NOT NULL
) WITHOUT OIDS;


CREATE INDEX idx_words_count_tweets_status  ON words_count_tweets  (tweet_id)
    WHERE status='INITIAL';



CREATE OR REPLACE FUNCTION insert_tweet_into_words_count_tweets()
  RETURNS trigger AS
$BODY$
BEGIN

INSERT INTO words_count_tweets(tweet_id,created_at, updated_at, status)
VALUES(NEW.id,now(),now(),'INITIAL');

RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql;


CREATE TRIGGER tweets_words_count_trigger
  AFTER INSERT
  ON tweets
  FOR EACH ROW
  EXECUTE PROCEDURE insert_tweet_into_words_count_tweets();
