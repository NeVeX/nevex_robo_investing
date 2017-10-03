package com.nevex.investing.config.property;

import com.nevex.investing.PropertyNames;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Mark Cunningham on 9/14/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = EventProperties.PREFIX)
public class EventProperties {

    public static final String PREFIX = PropertyNames.NEVEX_INVESTING+".events";

    @NotNull
    private Integer eventQueueSize;
    @NotNull
    @Min(value = 1)
    private Integer shardAmount;
    @NotNull
    private Boolean configurationEnabled;

    public Integer getEventQueueSize() {
        return eventQueueSize;
    }

    public void setEventQueueSize(Integer eventQueueSize) {
        this.eventQueueSize = eventQueueSize;
    }

    public Boolean getConfigurationEnabled() {
        return configurationEnabled;
    }

    public void setConfigurationEnabled(Boolean configurationEnabled) {
        this.configurationEnabled = configurationEnabled;
    }

    public Integer getShardAmount() {
        return shardAmount;
    }

    public void setShardAmount(Integer shardAmount) {
        this.shardAmount = shardAmount;
    }
}
