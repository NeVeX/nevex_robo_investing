package com.nevex.investing.ws;

import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.service.StockPriceService;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.StockPrice;
import com.nevex.investing.service.model.Ticker;
import com.nevex.investing.ws.model.StockPriceDto;
import com.nevex.investing.ws.model.TickerAnalyzerDto;
import com.nevex.investing.ws.model.TickerAnalyzerSummaryDto;
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
@RequestMapping(value = "/api/tickers/{symbol}/analyzers")
public class StockAnalyzerEndpoint {

    private final TickerAnalyzersService tickerAnalyzersService;
    private final TickerService tickerService;

    @Autowired
    public StockAnalyzerEndpoint(TickerAnalyzersService tickerAnalyzersService, TickerService tickerService) {
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        this.tickerAnalyzersService = tickerAnalyzersService;
        this.tickerService = tickerService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getLatestTickerAnalyzers(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol) {

        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        int tickerId;
        try {
             tickerId = tickerService.getIdForSymbol(symbol);
        } catch (TickerNotFoundException tickerNotFoundEx) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tickerAnalyzersService.getLatestAnalyzers(tickerId).stream().map(TickerAnalyzerDto::new).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getTickerAnalyzerSummary(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol) {
        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        int tickerId;
        try {
            tickerId = tickerService.getIdForSymbol(symbol);
        } catch (TickerNotFoundException tickerNotFoundEx) {
            return ResponseEntity.notFound().build();
        }
        Optional<AnalyzerSummaryResult> summaryResultOpt = tickerAnalyzersService.getLatestAnalyzerSummary(tickerId);
        if ( summaryResultOpt.isPresent()) {
            return ResponseEntity.ok(new TickerAnalyzerSummaryDto(summaryResultOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

}
