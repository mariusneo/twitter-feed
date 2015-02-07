package mg.twitter.feed.service.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
public class TweetsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TweetsResource.class);

    @GET
    public Response tweets() {
        return Response.ok().build();
    }
}
