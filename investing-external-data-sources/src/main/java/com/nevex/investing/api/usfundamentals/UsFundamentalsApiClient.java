package com.nevex.investing.api.usfundamentals;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalIndicator;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponse;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    private final static String COMMON_STOCK_SHARES_OUTSTANDING = "CommonStockSharesOutstanding";
    private final static String STOCK_HOLDERS_EQUITY = "StockholdersEquity";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final String host;
    private final String apiKey;
    private final String indicatorsToGet;

    public UsFundamentalsApiClient(String host, String apiKey) {
        if (StringUtils.isBlank(host)) { throw new IllegalArgumentException("Provided host is blank"); }
        if (StringUtils.isBlank(apiKey)) { throw new IllegalArgumentException("Provided apiKey is blank"); }
        this.host = host;
        this.apiKey = apiKey;

        // Build a map of all the indicators we need
        Set<String> indicatorsToGet = new HashSet<>();
        indicatorsToGet.add(EARNINGS_PER_SHARE_BASIC);
        indicatorsToGet.add(COMMON_STOCK_SHARES_OUTSTANDING);
        indicatorsToGet.add(STOCK_HOLDERS_EQUITY);
        this.indicatorsToGet = StringUtils.join(indicatorsToGet, ",");
    }

    public UsFundamentalsResponse getAllFundamentalsForCik(long cik) throws ApiException {
        UsFundamentalsResponse response = new UsFundamentalsResponse(cik, System.nanoTime());
        getAllQuarterlyFundamentalsForCik(cik).stream().forEach(response::addQuarterlyIndicator);
        getAllYearlyFundamentalsForCik(cik).stream().forEach(response::addYearlyIndicator);
        return response;
    }

    private Collection<UsFundamentalIndicator> getAllQuarterlyFundamentalsForCik(long cik) throws ApiException {
        HttpUrl quarterlyHttpUrl = buildCompanyIndicatorRequest(cik, "q"); // quarterly request
        return getAllFundamentalsForCik(cik, quarterlyHttpUrl);
    }

    private Collection<UsFundamentalIndicator> getAllYearlyFundamentalsForCik(long cik) throws ApiException {
        HttpUrl quarterlyHttpUrl = buildCompanyIndicatorRequest(cik, "y"); // quarterly request
        return getAllFundamentalsForCik(cik, quarterlyHttpUrl);
    }

    private Collection<UsFundamentalIndicator> getAllFundamentalsForCik(long cik, HttpUrl httpUrl) throws ApiException {
        Request request = buildRequest(httpUrl);
        String csvFundamentals = getFundamentalsForRequest(request);
        return parseCsvFundamentals(csvFundamentals, cik);
    }

    private Request buildRequest(HttpUrl httpUrl) {
        return new Request.Builder()
                .url(httpUrl)
                .addHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                .build();
    }

    private HttpUrl buildCompanyIndicatorRequest(long cik, String frequency) {
        return buildBaseHttpRequest()
                .addPathSegment("v1/indicators/xbrl")
                .addQueryParameter("companies", ""+cik)
                .addQueryParameter("frequency", frequency)
                .addQueryParameter("indicators", indicatorsToGet)
                .addQueryParameter("period_type", "end_date")
                .build();
    }

    private HttpUrl.Builder buildBaseHttpRequest() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addQueryParameter("token", apiKey);
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

            Map<String, UsFundamentalIndicator> indicatorsDateMap = new HashMap<>();

            // parse all the date columns to create objects to store each row later
            for ( Map.Entry<String, Integer> columnMapEntry : parser.getHeaderMap().entrySet()) {

                if ( columnMapEntry.getValue() < 2 ) { continue; } // this is not a date column
                String localDateRaw = columnMapEntry.getKey();
                LocalDate localDate = LocalDate.parse(localDateRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // The above iterator returns in order, so we don't need to worry about insertion order here
                indicatorsDateMap.put(localDateRaw, new UsFundamentalIndicator(localDate)); // add to specific index
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
                final ParseDateColumnIndicatorValue mapperToInvoke;

                if ( StringUtils.equalsIgnoreCase(indicatorName, EARNINGS_PER_SHARE_BASIC)) {
                    mapperToInvoke = this::setEarningsPerShareBasic;
                } else if (StringUtils.equalsIgnoreCase(indicatorName, COMMON_STOCK_SHARES_OUTSTANDING)) {
                    mapperToInvoke = this::setCommonStockSharesOutstanding;
                } else if (StringUtils.equalsIgnoreCase(indicatorName, STOCK_HOLDERS_EQUITY)) {
                    mapperToInvoke = this::setStockHoldersEquity;
                } else {
                    continue; // no mapper for this indicator, so we don't do anything
                }

                indicatorsDateMap.keySet().stream().forEach( colName -> {
                            String colValue = csvRecord.get(colName);
                            if ( StringUtils.isNotBlank(colValue)) {
                                mapperToInvoke.setValue(csvRecord.get(colName), indicatorsDateMap.get(colName));
                            }
                        }
                );
            }
            return indicatorsDateMap.values();
        } catch (Exception e) {
            throw new ApiException("Could not parse csv response for us-fundamentals", e);
        }
    }

    private void setEarningsPerShareBasic(String value, UsFundamentalIndicator indicator) {
        indicator.setEarningsPerShareBasic(new BigDecimal(value));
    }

    private void setCommonStockSharesOutstanding(String value, UsFundamentalIndicator indicator) {
        indicator.setCommonStockSharesOutstanding(Long.valueOf(value));
    }

    private void setStockHoldersEquity(String value, UsFundamentalIndicator indicator) {
        indicator.setStockHoldersEquity(Long.valueOf(value));
    }

    private String getFundamentalsForRequest(Request request) throws ApiException {
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

    /**
     * Interface that each type of data mapper can use for mapping various indicator values
     */
    @FunctionalInterface
    interface ParseDateColumnIndicatorValue {
        void setValue(String value, UsFundamentalIndicator indicator);
    }

}
