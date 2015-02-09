package mg.twitter.feed.service.resource;

import mg.twitter.feed.contract.Tweet;
import mg.twitter.feed.contract.UpdateStatus;
import mg.twitter.feed.domain.TweetRepository;
import mg.twitter.feed.domain.User;
import mg.twitter.feed.domain.UserRepository;
import org.glassfish.jersey.server.JSONP;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

public class TweetsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TweetsResource.class);

    private static final int MAX_COUNT = 100;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserRepository userRepository;

    @GET
    @Path("/latest")
    @JSONP(queryParam = "callback")
    @Produces({"application/x-javascript", "application/json"})
    @Profiled(tag = "TweetsResource.latestTweets][since_id:{$0}")
    public List<Tweet> latestTweets(@QueryParam("since_id") Long sinceId,@QueryParam("count") Integer count,
                                    @QueryParam("callback") String callback) {
        int itemCount = (count == null || count > MAX_COUNT) ? MAX_COUNT : count;
        long sinceTweetId = sinceId == null ? 0 : sinceId;

        List<mg.twitter.feed.domain.Tweet> domainTweets = tweetRepository.findLatestTweets(sinceTweetId, itemCount);

        Set<Long> userIds = domainTweets.stream().map(t -> t.getUserId()).collect(Collectors.toSet());
        List<User> users = userRepository.findAll(userIds);
        Map<Long, User> id2DomainUserMap = new HashMap<>();
        users.stream().forEach(u -> id2DomainUserMap.put(u.getId(), u));


        List<Tweet> tweets = new ArrayList<>(domainTweets.size());
        domainTweets.stream().forEach(t -> tweets.add(createTweetResponse(t, id2DomainUserMap.get(t.getUserId()))));
        return tweets;
    }


    @GET
    @Path("/updatestatus")
    @JSONP(queryParam = "callback")
    @Produces({"application/x-javascript", "application/json"})
    @Profiled(tag = "TweetsResource.checkForUpdates][since_id:{$0}")
    public UpdateStatus checkForUpdates(@QueryParam("since_id") long sinceId,
                                        @QueryParam("callback") String callback){
        UpdateStatus updateStatus= new UpdateStatus();
        updateStatus.setSinceTweetId(sinceId);
        updateStatus.setAvailable(tweetRepository.updatesAvaiable(sinceId));
        return updateStatus;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Profiled(tag = "TweetsResource.read][id:{$0}")
    public Response readTweet(@PathParam("id") Long id) {
        mg.twitter.feed.domain.Tweet domainTweet = tweetRepository.findOne(id);
        if (domainTweet == null) {
            return status(NOT_FOUND).build();
        }

        User domainTweetUser = userRepository.findOne(domainTweet.getUserId());

        return Response.ok(createTweetResponse(domainTweet, domainTweetUser)).build();
    }

    private Tweet createTweetResponse(mg.twitter.feed.domain.Tweet domainTweet, User domainUser) {
        Tweet tweet = new Tweet();
        tweet.setId(domainTweet.getId());
        tweet.setCreatedAt(domainTweet.getCreatedAt());
        tweet.setText(domainTweet.getText());
        mg.twitter.feed.contract.User user = new mg.twitter.feed.contract.User();
        user.setId(domainUser.getId());
        user.setName(domainUser.getName());
        user.setDescription(domainUser.getDescription());
        user.setScreenName(domainUser.getScreenName());
        user.setProfileImageUrl(domainUser.getProfileImageUrl());
        tweet.setUser(user);

        return tweet;
    }

}
