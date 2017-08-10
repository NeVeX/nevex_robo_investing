package com.nevex.roboinvesting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
