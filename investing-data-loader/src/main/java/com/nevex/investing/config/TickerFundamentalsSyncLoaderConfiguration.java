//package com.nevex.investing.config;
//
//import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
//import com.nevex.investing.database.TickerToCikRepository;
//import com.nevex.investing.database.TickersRepository;
//import com.nevex.investing.dataloader.DataLoaderService;
//import com.nevex.investing.service.EdgarAdminService;
//import com.nevex.investing.service.TickerFundamentalsAdminService;
//import com.nevex.investing.service.TickerService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;
//
///**
// * Created by Mark Cunningham on 9/4/2017.
// */
//@Configuration
//@ConfigurationProperties(prefix = TickerFundamentalsSyncLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
//@ConditionalOnProperty(name = TickerFundamentalsSyncLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
//public class TickerFundamentalsSyncLoaderConfiguration {
//
//    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".ticker-fundamentals-snyc-loader";
//    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";
//
//    @Autowired
//    private DataLoaderService dataLoaderService;
//    @Autowired
//    private TickerService tickerService;
//    @Autowired
//    private TickersRepository tickersRepository;
//    @Autowired
//    private TickerToCikRepository tickerToCikRepository;
//    @Autowired
//    private TickerFundamentalsAdminService tickerFundamentalsAdminService;
//    @Autowired
//    private UsFundamentalsApiClient usFundamentalsApiClient;
//    @Autowired
//    private EdgarAdminService edgarAdminService;
//
//    @Bean
//    TickerFundamentalsSyncLoader tickerFundamentalsSyncLoader() {
//        return new TickerFundamentalsSyncLoader(dataLoaderService, tickersRepository, tickerFundamentalsAdminService,
//                edgarAdminService, usFundamentalsApiClient);
//    }
//}
