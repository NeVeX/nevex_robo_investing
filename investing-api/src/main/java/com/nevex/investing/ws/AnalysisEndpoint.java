package com.nevex.investing.ws;

import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.Ticker;
import com.nevex.investing.ws.model.AnalysisDto;
import com.nevex.investing.ws.model.TickerAnalyzerSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    public AnalysisEndpoint(TickerAnalyzersService tickerAnalyzersService, TickerService tickerService) {
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        this.tickerAnalyzersService = tickerAnalyzersService;
        this.tickerService = tickerService;
    }

    @RequestMapping(value = "/top/buys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<AnalysisDto>> getTopStockBuys() {
        List<AnalyzerSummaryResult> topBuys = this.tickerAnalyzersService.getTopBestWeightedTickerSummaryAnalyzers();
        return returnAnalysis(topBuys);
    }

    @RequestMapping(value = "/top/sells", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<AnalysisDto>> getTopStockSells() {
        List<AnalyzerSummaryResult> topSells = this.tickerAnalyzersService.getTopWorstWeightedTickerSummaryAnalyzers();
        return returnAnalysis(topSells);
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
