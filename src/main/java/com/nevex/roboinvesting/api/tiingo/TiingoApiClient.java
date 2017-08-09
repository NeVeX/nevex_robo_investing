package com.nevex.roboinvesting.api.tiingo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nevex.roboinvesting.api.tiingo.model.TiingoPriceDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class TiingoApiClient {

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

    public Optional<TiingoPriceDto> getCurrentPriceForSymbol(String symbol) throws TiingoApiException {

        String url = StringUtils.replace(currentStockPriceUrl, "{SYMBOL}", symbol);
        Request request = buildDefaultRequest().url(url).build();

        TiingoPriceDto priceDto = null;
        List<TiingoPriceDto> prices = getStockPrices(request);
        if ( prices != null && !prices.isEmpty()) {
            priceDto = prices.get(0); // There should only ever be one
        }
        return Optional.ofNullable(priceDto);
    }

    public List<TiingoPriceDto> getHistoricalPricesForSymbol(String symbol, int maxDaysToFetch) throws TiingoApiException {
        // Build the date to use
        LocalDate todaysDate = LocalDate.now();
        LocalDate earliestDate = todaysDate.minus(maxDaysToFetch, ChronoUnit.DAYS);

        String todaysDateAsString = todaysDate.format(HISTORICAL_DATE_FORMATTER);
        String earliestDateAsString = earliestDate.format(HISTORICAL_DATE_FORMATTER);

        String url = StringUtils.replace(historicalStockPriceUrl, "{SYMBOL}", symbol);
        url = StringUtils.replace(url, "{START_DATE}", earliestDateAsString);
        url = StringUtils.replace(url, "{END_DATE}", todaysDateAsString);
        Request request = buildDefaultRequest().url(url).build();
        List<TiingoPriceDto> allPrices = getStockPrices(request);
        return allPrices;
    }

    private List<TiingoPriceDto> getStockPrices(Request request) throws TiingoApiException {
        try {
            Response response = httpClient.newCall(request).execute();
            if ( response.isSuccessful()) {
                return objectMapper.readValue(response.body().byteStream(), new TypeReference<List<TiingoPriceDto>>(){});
            }
            return new ArrayList<>();
        } catch (IOException ioException ) {
            throw new TiingoApiException("Could not get the current price using the Tiingo API for url ["+request.url()+"]", ioException);
        }
    }

    private Request.Builder buildDefaultRequest() {
        return new Request.Builder()
                .addHeader("Authorization", "Token "+apiKey)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

}
