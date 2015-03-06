package mg.twitter.feed.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {

    @Value("${redis.datasource.url}")
    private String redisUrl;

    @Bean
    public Jedis jedis() {
        return new Jedis(redisUrl);
    }
}
