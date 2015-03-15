package mg.twitter.feed.jobs.countwords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class JedisConfig {
    @Value("${redis.datasource.url}")
    private String redisUrl;

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool(redisUrl);
    }
}
