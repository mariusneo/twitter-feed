package mg.twitter.feed.service.resource;

import org.glassfish.jersey.server.JSONP;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PopularWordsResource {

    @Autowired
    private JedisPool jedisPool;

    @GET
    @JSONP(queryParam = "callback")
    @Produces({"application/x-javascript", "application/json"})
    public Map popularWords(@QueryParam("callback") String callback) {
        Slf4JStopWatch stopWatch = new Slf4JStopWatch(getClass().getSimpleName() + ".popularwords");

        Jedis jedis = jedisPool.getResource();
        try {
            Set<Tuple> range = jedis.zrevrangeWithScores("words.totals", 0, 50);

            Map<String, Integer> result = new LinkedHashMap<>();
            range.stream().forEach(t -> result.put(t.getElement(), (int) t.getScore()));

            return result;
        } finally {
            /// ... it's important to return the Jedis instance to the pool once you've finished using it
            jedisPool.returnResource(jedis);
            stopWatch.stop();
        }
    }
}
