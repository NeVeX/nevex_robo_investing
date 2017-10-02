package com.nevex.investing.ws;

import com.nevex.investing.analyzer.model.AnalyzerPricePerformance;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.service.AnalyzerPricePerformanceService;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.ws.model.AnalyzerPricePerformanceDto;
import com.nevex.investing.ws.model.TickerAnalyzerDto;
import com.nevex.investing.ws.model.TickerAnalyzerSummaryDto;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nevex.investing.ws.EndpointConstants.DATE_FORMATTER;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/api/tickers/{symbol}/analyzers")
public class StockAnalyzerEndpoint {

    private final TickerAnalyzersService tickerAnalyzersService;
    private final TickerService tickerService;
    private final AnalyzerPricePerformanceService analyzerPricePerformanceService;

    @Autowired
    public StockAnalyzerEndpoint(TickerAnalyzersService tickerAnalyzersService, TickerService tickerService,
            AnalyzerPricePerformanceService analyzerPricePerformanceService) {
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        if ( analyzerPricePerformanceService == null ) { throw new IllegalArgumentException("Provided analyzerPricePerformanceService is null"); }
        this.tickerAnalyzersService = tickerAnalyzersService;
        this.tickerService = tickerService;
        this.analyzerPricePerformanceService = analyzerPricePerformanceService;
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

    @RequestMapping(value = "/performance/price", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getTickerAnalyzerPerformancePrice(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol,
            @RequestParam(name="date", required = false) String date) {
        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        int tickerId;
        try {
            tickerId = tickerService.getIdForSymbol(symbol);
        } catch (TickerNotFoundException tickerNotFoundEx) {
            return ResponseEntity.notFound().build();
        }

        Optional<AnalyzerPricePerformance> pricePerfOpt;
        if ( StringUtils.isBlank(date)) {
            pricePerfOpt = analyzerPricePerformanceService.getLatestAnalyzerPricePerformance(tickerId);
        } else {
            pricePerfOpt = analyzerPricePerformanceService.getAnalyzerPricePerformance(tickerId, LocalDate.parse(date, DATE_FORMATTER));
        }

        if ( pricePerfOpt.isPresent()) {
            return ResponseEntity.ok(new AnalyzerPricePerformanceDto(pricePerfOpt.get()));
        }
        return ResponseEntity.notFound().build();
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
