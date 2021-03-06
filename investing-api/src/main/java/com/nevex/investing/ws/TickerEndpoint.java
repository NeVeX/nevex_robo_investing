package com.nevex.investing.ws;

import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.PageableData;
import com.nevex.investing.service.model.Ticker;
import com.nevex.investing.ws.model.PageableDataDto;
import com.nevex.investing.ws.model.TickerDto;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/api/tickers")
public class TickerEndpoint {

    private final TickerService tickerService;

    @Autowired
    public TickerEndpoint(TickerService tickerService) {
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickers service is null"); }
        this.tickerService = tickerService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<PageableDataDto<TickerDto>> getTickersForPage(
            @Min(value = 0, message = "Invalid page size provided") @RequestParam(value = "page", defaultValue = "0") Integer pageNumber) {
        PageableData<Ticker> pageableData = tickerService.getActiveTickers(pageNumber);
        if ( pageableData == null || !pageableData.hasData() ) {
            return ResponseEntity.ok(new PageableDataDto<>()); // empty response
        }
        return ResponseEntity.ok(new PageableDataDto<TickerDto>(pageableData, TickerDto::new));
    }

    @RequestMapping(value = "/{symbol}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getTickerForSymbol(
            @Length(min = 2, max = 20, message = "Symbol must be a valid length")
            @NotEmpty(message = "Symbol cannot be blank")
            @PathVariable(name = "symbol") String inputSymbol) {
        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        Optional<Ticker> tickerSymbolOpt = tickerService.getTickerInformation(symbol);
        if ( tickerSymbolOpt.isPresent()) {
            return ResponseEntity.ok(new TickerDto(tickerSymbolOpt.get()));
        }
        // this symbol does not exist, so return a 404
        return ResponseEntity.notFound().build();
    }

}
