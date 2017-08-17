package com.nevex.roboinvesting.ws;

import com.nevex.roboinvesting.service.StockPriceService;
import com.nevex.roboinvesting.service.model.StockPrice;
import com.nevex.roboinvesting.ws.model.StockPriceDto;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/tickers/{symbol}/prices")
public class StockPriceEndpoint {

    private final StockPriceService stockPriceService;

    @Autowired
    public StockPriceEndpoint(StockPriceService stockPriceService) {
        if ( stockPriceService == null ) { throw new IllegalArgumentException("Provided stockPriceService is null"); }
        this.stockPriceService = stockPriceService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getCurrentPrice(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol) {

        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        Optional<StockPrice> stockPriceOpt = stockPriceService.getCurrentPrice(symbol);

        if ( stockPriceOpt.isPresent()) {
            StockPrice stockPrice = stockPriceOpt.get();
            return ResponseEntity.ok(new StockPriceDto(stockPrice));
        }
        // this symbol does not exist, so return a 404
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/historical", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getHistoricalPrices(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol) {

        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        List<StockPrice> historicalPrices = stockPriceService.getHistoricalPrices(symbol);

        if ( !historicalPrices.isEmpty()) {
            List<StockPriceDto> stockPriceDtos = historicalPrices.stream().map(StockPriceDto::new).collect(Collectors.toList());
            return ResponseEntity.ok(stockPriceDtos);
        }

        return ResponseEntity.ok(new ArrayList<>()); // empty response
    }

}
