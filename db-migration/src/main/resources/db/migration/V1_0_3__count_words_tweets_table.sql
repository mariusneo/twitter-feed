CREATE TABLE count_words_tweets
(
    tweet_id BIGINT PRIMARY KEY NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    status VARCHAR(16) NOT NULL
) WITHOUT OIDS;


CREATE INDEX idx_count_words_tweets_status  ON count_words_tweets  (tweet_id)
    WHERE status='INITIAL';



CREATE OR REPLACE FUNCTION insert_tweet_into_count_words_tweets()
  RETURNS trigger AS
$BODY$
BEGIN

INSERT INTO count_words_tweets(tweet_id,created_at, updated_at, status)
VALUES(NEW.id,now(),now(),'INITIAL');

RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql;


CREATE TRIGGER tweets_count_words_trigger
  AFTER INSERT
  ON tweets
  FOR EACH ROW
  EXECUTE PROCEDURE insert_tweet_into_count_words_tweets();
