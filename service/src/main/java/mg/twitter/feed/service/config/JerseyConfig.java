package mg.twitter.feed.service.config;

import mg.twitter.feed.service.resource.RootResource;
import mg.twitter.feed.service.resource.TweetsResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(RootResource.class);
        register(TweetsResource.class);
    }
}