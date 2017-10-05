package com.nevex.investing.ws;

import com.nevex.investing.analyzer.model.AnalyzerPricePerformanceSummary;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.service.AnalyzerPricePerformanceService;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.Ticker;
import com.nevex.investing.util.DateUtils;
import com.nevex.investing.ws.model.AnalysisDto;
import com.nevex.investing.ws.model.AnalyzerPricePerformanceSummaryDto;
import com.nevex.investing.ws.model.NoDataDto;
import com.nevex.investing.ws.model.TickerAnalyzerSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/26/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/api/analysis")
public class AnalysisEndpoint {

    private final TickerAnalyzersService tickerAnalyzersService;
    private final TickerService tickerService;
    private final AnalyzerPricePerformanceService analyzerPricePerformanceService;

    @Autowired
    public AnalysisEndpoint(TickerAnalyzersService tickerAnalyzersService, TickerService tickerService, AnalyzerPricePerformanceService analyzerPricePerformanceService) {
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        if ( analyzerPricePerformanceService == null ) { throw new IllegalArgumentException("Provided analyzerPricePerformanceService is null"); }
        this.tickerAnalyzersService = tickerAnalyzersService;
        this.tickerService = tickerService;
        this.analyzerPricePerformanceService = analyzerPricePerformanceService;
    }

    @RequestMapping(value = "/top/buys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<AnalysisDto>> getTopStockBuys(@RequestParam(name="date", required = false) String date) {
        List<AnalyzerSummaryResult> topBuys;

        Optional<LocalDate> dateOpt = DateUtils.tryGetDate(date);
        if ( dateOpt.isPresent() ) {
            topBuys = this.tickerAnalyzersService.getTopBestWeightedTickerSummaryAnalyzers(dateOpt.get());
        } else {
            topBuys = this.tickerAnalyzersService.getLatestTopBestWeightedTickerSummaryAnalyzers();
        }
        return returnAnalysis(topBuys);
    }

    @RequestMapping(value = "/top/sells", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<AnalysisDto>> getTopStockSells(@RequestParam(name="date", required = false) String date) {
        List<AnalyzerSummaryResult> topSells;
        Optional<LocalDate> dateOpt = DateUtils.tryGetDate(date);
        if (dateOpt.isPresent()) {
            topSells = this.tickerAnalyzersService.getTopWorstWeightedTickerSummaryAnalyzers(dateOpt.get());
        } else {
            topSells = this.tickerAnalyzersService.getLatestTopWorstWeightedTickerSummaryAnalyzers();
        }
        return returnAnalysis(topSells);
    }

    @RequestMapping(value = "/performance/prices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> getPricePerformance(@RequestParam(name="date", required = false) String date) {
        Optional<AnalyzerPricePerformanceSummary> pricePerfSummaryOpt;
        Optional<LocalDate> dateOpt = DateUtils.tryGetDate(date);
        if ( dateOpt.isPresent()) {
            pricePerfSummaryOpt = this.analyzerPricePerformanceService.getPricePerformanceSummary(dateOpt.get());
        } else {
            pricePerfSummaryOpt = analyzerPricePerformanceService.getLatestPricePerformanceSummary();
        }

        if ( !pricePerfSummaryOpt.isPresent()) {
            return ResponseEntity.ok(new NoDataDto());
        }
        return ResponseEntity.ok(new AnalyzerPricePerformanceSummaryDto(pricePerfSummaryOpt.get(),
                convertToSymbols(pricePerfSummaryOpt.get().getCorrectRecommendedBuys()),
                convertToSymbols(pricePerfSummaryOpt.get().getCorrectRecommendedSells()),
                convertToSymbols(pricePerfSummaryOpt.get().getInCorrectRecommendedBuys()),
                convertToSymbols(pricePerfSummaryOpt.get().getInCorrectRecommendedSells())
                ));
    }

    private ResponseEntity<List<AnalysisDto>> returnAnalysis(List<AnalyzerSummaryResult> summaries) {
        return ResponseEntity.ok(convertToAnalysisDtos(summaries));
    }

    private List<AnalysisDto> convertToAnalysisDtos(List<AnalyzerSummaryResult> summaries) {
        List<AnalysisDto> topAnalysis = new ArrayList<>();
        for ( AnalyzerSummaryResult summaryResult : summaries) {
            Optional<String> symbolOpt = tickerService.tryGetSymbolForId(summaryResult.getTickerId());
            if ( !symbolOpt.isPresent()) {
                // todo: do stuff
                continue;
            }
            String symbol = symbolOpt.get();
            Optional<Ticker> tickerOpt = tickerService.getTickerInformation(symbol);
            if ( !tickerOpt.isPresent()) {
                // todo: do stuff
                continue;
            }
            Ticker ticker = tickerOpt.get();
            topAnalysis.add(new AnalysisDto(ticker.getSymbol(), ticker.getName(), new TickerAnalyzerSummaryDto(summaryResult)));
        }
        return topAnalysis;
    }

    private List<String> convertToSymbols(List<AnalyzerSummaryResult> results) {
        List<String> symbols = new ArrayList<>();
        for ( AnalyzerSummaryResult summaryResult : results) {
            Optional<String> symbolOpt = tickerService.tryGetSymbolForId(summaryResult.getTickerId());
            if ( !symbolOpt.isPresent()) {
                // todo: do stuff
                continue;
            }
            symbols.add(symbolOpt.get());
        }
        return symbols;
    }

}
