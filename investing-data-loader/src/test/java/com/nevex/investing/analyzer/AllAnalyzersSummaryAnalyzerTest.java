package com.nevex.investing.analyzer;

import com.nevex.investing.service.TickerAnalyzersAdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by NeVeX on 11/25/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AllAnalyzersSummaryAnalyzerTest {

    @Mock
    private TickerAnalyzersAdminService tickerAnalyzersAdminService;
    @Mock
    private AnalyzerServiceV2 analyzerServiceV2;

    @Test
    public void makeSureAdjustedWeightWorks() {
        AllAnalyzersSummaryAnalyzer summaryAnalyzer = new AllAnalyzersSummaryAnalyzer(tickerAnalyzersAdminService, analyzerServiceV2);

        assertEquals(-0.005, summaryAnalyzer.getAdjustedWeight(100, 99), 0.0);
        assertEquals(1.0, summaryAnalyzer.getAdjustedWeight(100, 300), 0.0);
        assertEquals(1.0, summaryAnalyzer.getAdjustedWeight(100, 200), 0.0);
        assertEquals(0, summaryAnalyzer.getAdjustedWeight(100, 100), 0.0);
        assertEquals(-0.045, summaryAnalyzer.getAdjustedWeight(100, 91), 0.0);
        assertEquals(-0.25, summaryAnalyzer.getAdjustedWeight(100, 50), 0.0);
        assertEquals(-0.45, summaryAnalyzer.getAdjustedWeight(100, 10), 0.0);
        assertEquals(-0.5, summaryAnalyzer.getAdjustedWeight(100, 0), 0.0);
        assertEquals(-1.0, summaryAnalyzer.getAdjustedWeight(100, -100), 0.0);
    }

}
