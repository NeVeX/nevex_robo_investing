package com.nevex.investing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Mark Cunningham on 8/17/2017.
 */
@EnableScheduling
@SpringBootApplication
public class DataLoaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderApplication.class, args);
    }
}
