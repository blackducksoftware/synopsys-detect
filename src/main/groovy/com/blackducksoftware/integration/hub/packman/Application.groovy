package com.blackducksoftware.integration.hub.packman

import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class Application {
    private final Logger logger = LoggerFactory.getLogger(Application.class)

    static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(true).run(args)
    }

    @PostConstruct
    void init() {
    }
}
