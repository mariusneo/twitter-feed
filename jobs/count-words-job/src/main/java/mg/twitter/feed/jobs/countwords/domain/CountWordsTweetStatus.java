package mg.twitter.feed.jobs.countwords.domain;

public enum CountWordsTweetStatus {
    /**
     * The tweet is available to be taken by a count words job.
     */
    INITIAL,

    /**
     * The tweet is currently being processed by a count words job
     */
    PROCESSING
}
