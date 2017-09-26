package com.nevex.investing.api.usfundamentals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponseDto;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsUpdatableDto;
import org.junit.Test;

import java.util.List;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class UsFundamentalsApiClientTest {

    @Test
    public void makeSureCompanyFundamentalCanBeObtained() throws Exception {
        long cik = 789019;
        String apiKey = "J01Rz3qHKjP1YOoCiUl_rw";
        String host = "api.usfundamentals.com";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());

        UsFundamentalsApiClient client = new UsFundamentalsApiClient(host, apiKey, objectMapper);

//        List<UsFundamentalsUpdatableDto> updatableDtos = client.getAllQuarterlyFundamentalsThatNeedsUpdates(System.nanoTime());

        UsFundamentalsResponseDto response = client.getAllFundamentalsForCik(cik);

//        BigDecimal marketCap = response.getQuarterlyIndicators().

        response.toString();

    }
}
