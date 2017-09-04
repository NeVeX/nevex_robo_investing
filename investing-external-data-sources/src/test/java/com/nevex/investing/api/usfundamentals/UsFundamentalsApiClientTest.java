package com.nevex.investing.api.usfundamentals;

import com.nevex.investing.usfundamentals.UsFundamentalsApiClient;
import com.nevex.investing.usfundamentals.model.UsFundamentalsResponse;
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


        UsFundamentalsResponse response = new UsFundamentalsApiClient(host, apiKey).getFundamentalsForCik(cik);
        response.toString();



    }
}
