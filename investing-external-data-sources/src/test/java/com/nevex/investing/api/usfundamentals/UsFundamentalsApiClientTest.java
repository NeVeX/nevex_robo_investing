package com.nevex.investing.api.usfundamentals;

import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponse;
import org.junit.Test;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class UsFundamentalsApiClientTest {

    @Test
    public void makeSureCompanyFundamentalCanBeObtained() throws Exception {
        long cik = 789019;
        String apiKey = "";
        String host = "api.usfundamentals.com";


        long nanoTime = System.nanoTime();

        UsFundamentalsResponse response = new UsFundamentalsApiClient(host, apiKey).getAllFundamentalsForCik(cik);

//        BigDecimal marketCap = response.getQuarterlyIndicators().

        response.toString();

    }
}
