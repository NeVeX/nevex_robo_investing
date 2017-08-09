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
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class TiingoApiClient {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public TiingoApiClient(String apiKey) {
        if (StringUtils.isBlank(apiKey)) { throw new IllegalArgumentException("Provided apiKey is blank"); }
        this.apiKey = apiKey;

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For better support of LocalDate...etc

    }

    public Optional<TiingoPriceDto> getCurrentPriceDataForSymbol(String symbol) throws TiingoApiException {
        TiingoPriceDto priceDto = null;

        String url = StringUtils.replace("https://api.tiingo.com/tiingo/daily/{SYMBOL}/prices", "{SYMBOL}", symbol);
        Request request = new Request.Builder()
                                    .addHeader("Authorization", "Token "+apiKey)
                                    .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .url(url)
                                    .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if ( response.isSuccessful()) {
                List<TiingoPriceDto> prices = objectMapper.readValue(response.body().byteStream(), new TypeReference<List<TiingoPriceDto>>(){});
                if ( prices != null && prices.size() == 1 ) {
                    priceDto = prices.get(0);
                }
            }

        } catch (IOException ioException ) {
            throw new TiingoApiException("Could not talk to the Tiingo API ["+url+"]", ioException);
        }

        return Optional.ofNullable(priceDto);
    }

}
