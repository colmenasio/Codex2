package com.kor.tomcat.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelloService {

    private static final Logger logger = LogManager.getLogger(HelloService.class);

    public String getGreeting(String name) {
        if (StringUtils.isBlank(name)) {
            logger.debug("No name provided, using default");
            return "Hello, World!";
        }
        logger.debug("Generating greeting for: {}", name);
        return String.format("Hello, %s!", name.trim());
    }

    public String createPersonalizedMessage(String name) {
        if (StringUtils.isBlank(name)) {
            return "Please provide a name";
        }
        return String.format("Welcome %s! Your message has been received.", name.trim());
    }
}
