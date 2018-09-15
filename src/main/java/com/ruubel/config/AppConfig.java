package com.ruubel.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class AppConfig {

    public void configureUtc() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

    @PostConstruct
    public void init() {
        configureUtc();
    }

}
