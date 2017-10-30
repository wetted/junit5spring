package com.objectcomputing.junit5spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class Junit5springApplication {
    private static final Logger logger = LoggerFactory.getLogger(Junit5springApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(Junit5springApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunnerStart(ApplicationContext ctx) {
        logger.debug("Beans provided by Spring Boot:");
        return args -> Arrays.stream(ctx.getBeanDefinitionNames()).sorted().forEach(logger::debug);
    }
}
