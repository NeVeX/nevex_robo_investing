package com.nevex.roboinvesting.ws;

import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import com.nevex.roboinvesting.model.TickerSymbol;
import com.nevex.roboinvesting.service.TickerSymbolService;
import com.nevex.roboinvesting.ws.model.TickerDto;
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

import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/tickers")
public class TickersEndpoint {

    private final TickerSymbolService tickerSymbolService;

    @Autowired
    public TickersEndpoint(TickerSymbolService tickerSymbolService) {
        if ( tickerSymbolService == null ) { throw new IllegalArgumentException("Provided tickers service is null"); }
        this.tickerSymbolService = tickerSymbolService;
    }

    @RequestMapping(value = "/{symbol}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getSymbol(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol) {
        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        Optional<TickerSymbol> tickerSymbolOpt = tickerSymbolService.getTickerInformation(symbol);
        if ( tickerSymbolOpt.isPresent()) {
            return ResponseEntity.ok(TickerDto.from(tickerSymbolOpt.get()));
        }
        // this symbol does not exist, so return a 404
        return ResponseEntity.notFound().build();
    }

}
