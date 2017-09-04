package com.nevex.investing.usfundamentals;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.usfundamentals.model.UsFundamentalIndicator;
import com.nevex.investing.usfundamentals.model.UsFundamentalsResponse;
import okhttp3.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Mark Cunningham on 9/4/2017.
 * <br> See more: http://www.usfundamentals.com/
 */
public class UsFundamentalsApiClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(UsFundamentalsApiClient.class);
    private final static String EARNINGS_PER_SHARE_BASIC = "EarningsPerShareBasic";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final String host;
    private final String apiKey;

    private final String indicatorsToGet = EARNINGS_PER_SHARE_BASIC;

    public UsFundamentalsApiClient(String host, String apiKey) {
        if (StringUtils.isBlank(host)) { throw new IllegalArgumentException("Provided host is blank"); }
        if (StringUtils.isBlank(apiKey)) { throw new IllegalArgumentException("Provided apiKey is blank"); }
        this.host = host;
        this.apiKey = apiKey;
    }

    public UsFundamentalsResponse getFundamentalsForCik(long cik) throws ApiException {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment("v1/indicators/xbrl")
                .addQueryParameter("token", apiKey)
                .addQueryParameter("companies", ""+cik)
                .addQueryParameter("frequency", "q")
                .addQueryParameter("indicators", indicatorsToGet)
                .addQueryParameter("period_type", "end_date")
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .addHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                .build();

        String csvFundamentals = getFundamentals(request);
        Collection<UsFundamentalIndicator> parsedFundamentals = parseCsvFundamentals(csvFundamentals, cik);

        // todo: get yearly indicators

        UsFundamentalsResponse response = new UsFundamentalsResponse(cik);
        parsedFundamentals.stream().forEach(response::addQuarterlyIndicator);
        return response;
    }

    // Todo: clean this up and make it perform better (and read better)
    private Collection<UsFundamentalIndicator> parseCsvFundamentals(String csvFundamentals, long cikRequested) throws ApiException {
        try {
            StringReader stringReader = new StringReader(csvFundamentals);
            CSVParser parser = CSVFormat.RFC4180
                    .withFirstRecordAsHeader()
                    .withSkipHeaderRecord(false)
                    .withIgnoreEmptyLines()
                    .withIgnoreSurroundingSpaces()
                    .withTrim()
                    .parse(stringReader);

            Map<String, UsFundamentalIndicator> indicators = new HashMap<>();

            // parse all the date columns to create objects to store each row later
            for ( Map.Entry<String, Integer> columnMapEntry : parser.getHeaderMap().entrySet()) {

                if ( columnMapEntry.getValue() < 2 ) { continue; } // this is not a date column
                String localDateRaw = columnMapEntry.getKey();
                LocalDate localDate = LocalDate.parse(localDateRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // The above iterator returns in order, so we don't need to worry about insertion order here
                indicators.put(localDateRaw, new UsFundamentalIndicator(localDate)); // add to specific index
            }

            for (CSVRecord csvRecord : parser) {

                if ( csvRecord.size() < 3 ) {
                    continue; // not enough records
                }

                String companyCik = csvRecord.get(0);
                if ( Long.valueOf(companyCik) != cikRequested ) {
                    LOGGER.warn("The Us-fundamentals gave a company cik [{}] that we did not request [{}]. Will skip this record.", companyCik, cikRequested);
                    continue;
                }
                String indicatorName = csvRecord.get(1);
                if ( StringUtils.equalsIgnoreCase(indicatorName, EARNINGS_PER_SHARE_BASIC)) {
                    indicators.keySet().stream().forEach( colName -> indicators.get(colName).setEarningsPerShareBasic(new BigDecimal(csvRecord.get(colName))));
                }
            }
            return indicators.values();
        } catch (Exception e) {
            throw new ApiException("Could not parse csv response for us-fundamentals", e);
        }
    }

    private String getFundamentals(Request request) throws ApiException {
        try(Response response = httpClient.newCall(request).execute()) {
            if ( response.isSuccessful()) {
                if ( response.body() == null ) {
                    throw new ApiException("Expected a body in the response from the UsFundamentals API, but there was none. "+request);
                }
                return new String(response.body().bytes());
            }
            throw new ApiException("Response was not successful from us-fundamentals API. Request ["+request+"]");
        } catch (IOException ioException ) {
            throw new ApiException("Could not get UsFundamentals for url ["+request.url()+"]", ioException);
        }
    }




}
