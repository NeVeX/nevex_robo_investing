package com.nevex.roboinvesting.api.edgar;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Mark Cunningham on 9/2/2017.
 */
public class EdgarCikLookupClientTest {

    private EdgarCikLookupClient edgarCikLookupClient;

    @Test
    public void makeSureWeCanParseACikSearch() throws Exception {

        edgarCikLookupClient = new EdgarCikLookupClient();
        Optional<String> cikOptional = edgarCikLookupClient.getCikForTicker("MSFT");
        assertThat(cikOptional.isPresent()).isTrue();
        // Note it's possible the cik changes over time by the SEC
        assertThat(cikOptional.get()).isEqualToIgnoringCase("0000789019");
    }


}
