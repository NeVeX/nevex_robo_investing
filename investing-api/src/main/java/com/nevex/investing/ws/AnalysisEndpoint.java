package com.nevex.investing.ws;

import com.nevex.investing.analyzer.model.AnalyzerPricePerformanceSummary;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.service.AnalyzerPricePerformanceService;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.Ticker;
import com.nevex.investing.ws.model.AnalysisDto;
import com.nevex.investing.ws.model.AnalyzerPricePerformanceSummaryDto;
import com.nevex.investing.ws.model.NoDataDto;
import com.nevex.investing.ws.model.TickerAnalyzerSummaryDto;
import org.apache.commons.lang3.StringUtils;
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

import static com.nevex.investing.ws.EndpointConstants.DATE_FORMATTER;

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
        if ( StringUtils.isBlank(date)) {
            topBuys = this.tickerAnalyzersService.getLatestTopBestWeightedTickerSummaryAnalyzers();
        } else {
            topBuys = this.tickerAnalyzersService.getTopBestWeightedTickerSummaryAnalyzers(LocalDate.parse(date, DATE_FORMATTER));
        }
        return returnAnalysis(topBuys);
    }

    @RequestMapping(value = "/top/sells", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<AnalysisDto>> getTopStockSells(@RequestParam(name="date", required = false) String date) {
        List<AnalyzerSummaryResult> topSells;
        if (StringUtils.isBlank(date)) {
            topSells = this.tickerAnalyzersService.getLatestTopWorstWeightedTickerSummaryAnalyzers();
        } else {
            topSells = this.tickerAnalyzersService.getTopWorstWeightedTickerSummaryAnalyzers(LocalDate.parse(date, DATE_FORMATTER));
        }
        return returnAnalysis(topSells);
    }

    @RequestMapping(value = "/performance/prices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<?> getPricePerformance(@RequestParam(name="date", required = false) String date) {
        Optional<AnalyzerPricePerformanceSummary> pricePerfSummaryOpt;
        if ( StringUtils.isBlank(date)) {
            pricePerfSummaryOpt = analyzerPricePerformanceService.getLatestPricePerformanceSummary();
        } else {
            pricePerfSummaryOpt = this.analyzerPricePerformanceService.getPricePerformanceSummary(LocalDate.parse(date, DATE_FORMATTER));
        }

        if ( !pricePerfSummaryOpt.isPresent()) {
            return ResponseEntity.ok(new NoDataDto());
        }
        return ResponseEntity.ok(new AnalyzerPricePerformanceSummaryDto(pricePerfSummaryOpt.get()));
    }

    private ResponseEntity<List<AnalysisDto>> returnAnalysis(List<AnalyzerSummaryResult> summaries) {
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
        return ResponseEntity.ok(topAnalysis);
    }
}
