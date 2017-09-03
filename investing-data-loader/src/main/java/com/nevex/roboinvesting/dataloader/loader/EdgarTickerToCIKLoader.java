package com.nevex.roboinvesting.dataloader.loader;

import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickerEntity;
import com.nevex.roboinvesting.dataloader.DataLoaderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.nevex.roboinvesting.dataloader.loader.DataLoaderOrder.TICKER_TO_CIK_LOADER;

/**
 * Created by Mark Cunningham on 8/31/2017.
 * <br> This is the starting point: https://www.sec.gov/cgi-bin/browse-edgar?CIK=lc&owner=exclude&action=getcompany
 * <br> Basicaly, to use Edgar, we need the CIK number, which can be gotten by parsing the html page (scraping)
 */
public class EdgarTickerToCIKLoader extends DataLoaderWorker {

    private final TickersRepository tickersRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public EdgarTickerToCIKLoader(DataLoaderService dataLoaderService, TickersRepository tickersRepository) {
        super(dataLoaderService);
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided tickersRepository is null"); }
        this.tickersRepository = tickersRepository;
    }

    @Override
    int getOrderNumber() {
        return TICKER_TO_CIK_LOADER;
    }

    @Override
    String getName() {
        return "ticker-to-cik-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        int tickersProcessed = super.processAllPagesForRepo(tickersRepository, this::processTicker, 400);
        return new DataLoaderWorkerResult(tickersProcessed);
    }

    private void processTicker(TickerEntity tickerEntity) {
        String ticker = tickerEntity.getSymbol();
        // https://www.sec.gov/cgi-bin/browse-edgar?owner=exclude&action=getcompany&CIK=LC
        String urlToInvoke = "https://www.sec.gov/cgi-bin/browse-edgar?owner=exclude&action=getcompany&CIK="+ticker;

        ResponseEntity<String> response = restTemplate.getForEntity(urlToInvoke, String.class);
        if ( response != null && response.getStatusCode().is2xxSuccessful()) {
            String html = response.getBody();
            // Search for the string that we know has the cik in it
            // Namely: <a href="/cgi-bin/browse-edgar?action=getcompany&amp;CIK=0001409970&amp;owner=exclude&amp;count=40">0001409970 (see all company filings)</a>
            String stringToFind = "href=\"/cgi-bin/browse-edgar?action=getcompany&amp;CIK=";
            int index = StringUtils.indexOf(html, stringToFind);
            if ( index > 0 ) {
                int startIndex = index + stringToFind.length();
                int nextSplit = StringUtils.indexOf(html, "&", startIndex);
                String cikValue = StringUtils.substring(html, startIndex, nextSplit);
                String lower = cikValue.toLowerCase();

            }
        }

    }
}
