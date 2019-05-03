package com.ruubel.config;

import com.rollbar.notifier.Rollbar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

@Configuration
public class AppConfig {

    public void configureUtc() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

    @PostConstruct
    public void init() {
        configureUtc();
    }

    @Bean
    public Rollbar rollbar() {
        Rollbar rollbar = Rollbar.init(withAccessToken("223d856c453b4c1cae3e0a9503c4e0c0").build());
        return rollbar;
    }

}
