package mg.twitter.feed.service;

import mg.twitter.feed.service.config.JdbConfig;
import mg.twitter.feed.service.config.JerseyConfig;
import mg.twitter.feed.service.config.Perf4jConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan
@EnableJpaRepositories(value = "mg.twitter.feed.domain")
@EnableAutoConfiguration
@Import({JerseyConfig.class, JdbConfig.class, Perf4jConfig.class})
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        new Application().configure(new SpringApplicationBuilder(Application.class)).run(args);
    }

}