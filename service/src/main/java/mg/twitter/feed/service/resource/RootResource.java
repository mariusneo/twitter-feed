package mg.twitter.feed.service.resource;

import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

@Component
@Path(RootResource.BASE_URL)
public class RootResource {
    public static final String BASE_URL = "/api";

    @Path("tweets")
    public TweetsResource tweets(@Context ResourceContext resourceContext) {
        return resourceContext.getResource(TweetsResource.class);
    }
}
