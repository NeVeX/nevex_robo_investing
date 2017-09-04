package com.nevex.investing.api.edgar;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.edgar.model.SearchCIKDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/2/2017.
 */
public class EdgarCikLookupClient {

    private final RestTemplate httpClient = new RestTemplate();
    private final static int SEARCH_COUNT = 10;
    private final static String SEARCH_CIK_URL = "https://www.sec.gov/cgi-bin/browse-edgar?owner=exclude&action=getcompany&output=atom&count="+SEARCH_COUNT+"&CIK=";
    private final static String SEARCH_COMPANY_NAME_URL = "https://www.sec.gov/cgi-bin/browse-edgar?owner=exclude&action=getcompany&output=atom&count="+SEARCH_COUNT+"&company=";

    public Optional<String> getCikForTicker(String symbol) throws ApiException {
        // E.G. https://www.sec.gov/cgi-bin/browse-edgar?owner=exclude&action=getcompany&CIK=LC
        String urlToInvoke = SEARCH_CIK_URL + symbol;
        return doSearch(urlToInvoke);
    }

    public Optional<String> getCikForCompanyName(String companyName) throws ApiException {
        String urlToInvoke = SEARCH_COMPANY_NAME_URL + companyName;
        return doSearch(urlToInvoke);
    }

    private Optional<String> doSearch(String urlToInvoke) throws ApiException {
        SearchCIKDto searchResult = invokeCikSearch(urlToInvoke);
        if ( searchResult != null && searchResult.getCompanyInfo() != null) {
            String foundCik = searchResult.getCompanyInfo().getCik();
            if ( StringUtils.isNotBlank(foundCik)) {
                return Optional.of(foundCik);
            }
        }
        return Optional.empty();
    }

    private SearchCIKDto invokeCikSearch(String urlToInvoke) throws ApiException {
        try {
            ResponseEntity<SearchCIKDto> response = httpClient.getForEntity(urlToInvoke, SearchCIKDto.class);
            if ( response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            return null;
        } catch (RestClientException restClientEx ) {
            throw new ApiException("Could not perform cik search ["+urlToInvoke+"]", restClientEx);
        }
    }

    // hacky way
//        ResponseEntity<String> response = restTemplate.getForEntity(urlToInvoke, String.class);
//        if ( response != null && response.getStatusCode().is2xxSuccessful()) {
//            String html = response.getBody();
//            // Search for the string that we know has the cik in it
//            // Namely: <a href="/cgi-bin/browse-edgar?action=getcompany&amp;CIK=0001409970&amp;owner=exclude&amp;count=40">0001409970 (see all company filings)</a>
//            String stringToFind = "href=\"/cgi-bin/browse-edgar?action=getcompany&amp;CIK=";
//            int index = StringUtils.indexOf(html, stringToFind);
//            if ( index > 0 ) {
//                int startIndex = index + stringToFind.length();
//                int nextSplit = StringUtils.indexOf(html, "&", startIndex);
//                String cikValue = StringUtils.substring(html, startIndex, nextSplit);
//                String lower = cikValue.toLowerCase();
//
//            }
//        }


}
