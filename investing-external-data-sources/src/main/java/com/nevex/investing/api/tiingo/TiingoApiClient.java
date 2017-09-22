package com.nevex.investing.api.tiingo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.ApiStockPrice;
import com.nevex.investing.api.ApiStockPriceClient;
import com.nevex.investing.api.tiingo.model.TiingoPriceDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class TiingoApiClient implements ApiStockPriceClient {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final DateTimeFormatter HISTORICAL_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String currentStockPriceUrl;
    private final String historicalStockPriceUrl;

    public TiingoApiClient(String apiKey, String tiingoHost) {
        if (StringUtils.isBlank(apiKey)) { throw new IllegalArgumentException("Provided apiKey is blank"); }
        if (StringUtils.isBlank(tiingoHost)) { throw new IllegalArgumentException("Provided tiingoHost is blank"); }
        this.apiKey = apiKey;
        this.currentStockPriceUrl = tiingoHost + "/tiingo/daily/{SYMBOL}/prices";
        this.historicalStockPriceUrl = currentStockPriceUrl + "?startDate={START_DATE}&endDate={END_DATE}";
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For better support of LocalDate...etc
    }

    public Optional<ApiStockPrice> getPriceForSymbol(String symbol) throws ApiException {

        String url = StringUtils.replace(currentStockPriceUrl, "{SYMBOL}", symbol);
        Request request = buildDefaultRequest().url(url).build();

        Set<ApiStockPrice> prices = convertToApiStockPrices(getStockPrices(request));
        return ApiStockPrice.getLatestPrice(prices);
    }

    @Override
    public Map<String, Optional<ApiStockPrice>> getPriceForSymbols(List<String> symbols) throws ApiException {
        Map<String, Optional<ApiStockPrice>> results = new HashMap<>();
        for (String symbol : symbols) {
            results.put(symbol, getPriceForSymbol(symbol));
        }
        return results;
    }

    public Set<ApiStockPrice> getHistoricalPricesForSymbol(String symbol, LocalDate asOfDate, int maxDaysToFetch) throws ApiException {
        // Build the date to use
        LocalDate earliestDate = asOfDate.minus(maxDaysToFetch, ChronoUnit.DAYS);

        String todaysDateAsString = asOfDate.format(HISTORICAL_DATE_FORMATTER);
        String earliestDateAsString = earliestDate.format(HISTORICAL_DATE_FORMATTER);

        String url = StringUtils.replace(historicalStockPriceUrl, "{SYMBOL}", symbol);
        url = StringUtils.replace(url, "{START_DATE}", earliestDateAsString);
        url = StringUtils.replace(url, "{END_DATE}", todaysDateAsString);
        Request request = buildDefaultRequest().url(url).build();
        return convertToApiStockPrices(getStockPrices(request));
    }

    @Override
    public Map<String, Set<ApiStockPrice>> getHistoricalPricesForSymbols(List<String> symbols, LocalDate asOfDate, int maxDaysToFetch) throws ApiException {
        Map<String, Set<ApiStockPrice>> results = new HashMap<>();
        for (String symbol : symbols) {
            results.put(symbol, getHistoricalPricesForSymbol(symbol, asOfDate, maxDaysToFetch));
        }
        return results;
    }

    private Set<TiingoPriceDto> getStockPrices(Request request) throws ApiException {
        try(Response response = httpClient.newCall(request).execute()) {
            if ( response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if ( responseBody == null ) {
                    throw new ApiException("Expected a body in the response from the Tiingo API, but there was none. "+request);
                }
                return objectMapper.readValue(responseBody.byteStream(), new TypeReference<Set<TiingoPriceDto>>(){});
            }
            throw new ApiException("Response was not successful from the Tiingo API. Request ["+request+"]");
        } catch (IOException ioException ) {
            throw new ApiException("Could not get the current price using the Tiingo API for url ["+request.url()+"]", ioException);
        }
    }

    private Request.Builder buildDefaultRequest() {
        return new Request.Builder()
                .addHeader("Authorization", "Token "+apiKey)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    private Set<ApiStockPrice> convertToApiStockPrices(Set<TiingoPriceDto> tiingoPriceDtos) throws ApiException {
        Set<ApiStockPrice> convertedPrices = new HashSet<>();
        for ( TiingoPriceDto tiingoPriceDto : tiingoPriceDtos ) {
            convertedPrices.add(convertToApiStockPrice(tiingoPriceDto));
        }
        return convertedPrices;
    }

    private ApiStockPrice convertToApiStockPrice(TiingoPriceDto priceDto) throws ApiException {
        try {
            return ApiStockPrice.builder()
                    .withDate(priceDto.getDate().toLocalDate())
                    .withAdjustedClose(priceDto.getAdjClose())
                    .withClose(priceDto.getClose())
                    .withHigh(priceDto.getHigh())
                    .withLow(priceDto.getLow())
                    .withOpen(priceDto.getOpen())
                    .withVolume(priceDto.getVolume())
                    .build();
        } catch (ApiStockPrice.BuilderException be) {
            throw new ApiException("Could not convert Tiingo stock price data into "+ApiStockPrice.class.getSimpleName(), be);
        }
    }

}
