package com.nevex.investing.ws;

import com.nevex.investing.analyzer.model.AnalyzerPricePerformance;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.service.AnalyzerPricePerformanceService;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.util.DateUtils;
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
            @PathVariable(name = "symbol") String inputSymbol,
            @RequestParam(name="date", required = false) String date) {
        Integer tickerId = getTickerForSymbol(inputSymbol);
        if ( tickerId == null) { return notFound(); }

        Optional<LocalDate> dateOpt = DateUtils.tryGetDate(date);
        List<AnalyzerResult> results;
        if (dateOpt.isPresent()) {
            results = tickerAnalyzersService.getAnalyzers(tickerId, dateOpt.get());
        } else {
            results = tickerAnalyzersService.getLatestAnalyzers(tickerId);
        }
        return ResponseEntity.ok(results.stream().map(TickerAnalyzerDto::new).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/performance/price", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getTickerAnalyzerPerformancePrice(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol,
            @RequestParam(name="date", required = false) String date) {

        Integer tickerId = getTickerForSymbol(inputSymbol);
        if ( tickerId == null) { return notFound(); }

        Optional<AnalyzerPricePerformance> pricePerfOpt;
        Optional<LocalDate> dateOpt = DateUtils.tryGetDate(date);
        if ( dateOpt.isPresent()) {
            pricePerfOpt = analyzerPricePerformanceService.getAnalyzerPricePerformance(tickerId, dateOpt.get());
        } else {
            pricePerfOpt = analyzerPricePerformanceService.getLatestAnalyzerPricePerformance(tickerId);
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
            @PathVariable(name = "symbol") String inputSymbol,
            @RequestParam(name="date", required = false) String date) {

        Integer tickerId = getTickerForSymbol(inputSymbol);
        if ( tickerId == null) { return notFound(); }

        Optional<AnalyzerSummaryResult> summaryResultOpt;
        Optional<LocalDate> dateOpt = DateUtils.tryGetDate(date);
        if ( dateOpt.isPresent()) {
            summaryResultOpt = tickerAnalyzersService.getAnalyzerSummary(tickerId, dateOpt.get());
        } else {
            summaryResultOpt = tickerAnalyzersService.getLatestAnalyzerSummary(tickerId);
        }
        if ( summaryResultOpt.isPresent()) {
            return ResponseEntity.ok(new TickerAnalyzerSummaryDto(summaryResultOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    private Integer getTickerForSymbol(String inputSymbol) {
        try {
            String symbol = StringUtils.trim(inputSymbol).toUpperCase();
            return tickerService.getIdForSymbol(symbol);
        } catch (TickerNotFoundException ignore) { }
        return null;
    }

    private ResponseEntity notFound() {
        return ResponseEntity.notFound().build();
    }
}
