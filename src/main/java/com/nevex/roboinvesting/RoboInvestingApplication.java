package com.nevex.roboinvesting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@EnableScheduling
@SpringBootApplication
public class RoboInvestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoboInvestingApplication.class, args);
    }

}
