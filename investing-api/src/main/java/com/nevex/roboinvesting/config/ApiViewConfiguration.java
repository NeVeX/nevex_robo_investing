package com.nevex.roboinvesting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Mark Cunningham on 6/24/2017.
 */
@Configuration
class ApiViewConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourcePattern = "/resources/**";
        if ( !registry.hasMappingForPattern(resourcePattern)) {
            registry.addResourceHandler(resourcePattern).addResourceLocations("/resources/");
        }
    }

}
