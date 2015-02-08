package mg.twitter.feed.service.resource;

import mg.twitter.feed.contract.Tweet;
import mg.twitter.feed.domain.TweetRepository;
import mg.twitter.feed.domain.User;
import mg.twitter.feed.domain.UserRepository;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

@Component
@Produces(MediaType.APPLICATION_JSON)
public class TweetsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TweetsResource.class);

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserRepository userRepository;

    @GET
    @Path("{id}")
    @Profiled(tag="TweetsResource.read][id:{$0}")
    public Response readTweet(@PathParam("id") Long id){
        mg.twitter.feed.domain.Tweet domainTweet = tweetRepository.findOne(id);
        if (domainTweet == null){
            return status(NOT_FOUND).build();
        }

        User domainTweetUser = userRepository.findOne(domainTweet.getUserId());

        return Response.ok(createTweetResponse(domainTweet, domainTweetUser)).build();
    }

    private Tweet createTweetResponse(mg.twitter.feed.domain.Tweet domainTweet, User domainUser){
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
