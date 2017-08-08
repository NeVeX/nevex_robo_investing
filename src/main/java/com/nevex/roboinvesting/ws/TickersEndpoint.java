package com.nevex.roboinvesting.ws;

import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import com.nevex.roboinvesting.ws.model.ErrorDto;
import com.nevex.roboinvesting.ws.model.TickerDto;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@RestController
@Validated
@RequestMapping("/tickers")
public class TickersEndpoint {

    private static final ErrorDto NO_SYMBOL_ERROR = new ErrorDto("No symbol provided");
    private final TickersRepository tickersRepository;

    @Autowired
    public TickersEndpoint(TickersRepository tickersRepository) {
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        this.tickersRepository = tickersRepository;
    }

    // TODO: Restrict the input - min/max chars etc...
    @RequestMapping(value = "/{symbol}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getSymbol(@NotBlank @PathVariable(name = "symbol") String inputSymbol) {
        String symbol = StringUtils.trim(inputSymbol).toUpperCase();
        Optional<TickersEntity> entityOptional = tickersRepository.findBySymbol(symbol);
        if ( entityOptional.isPresent()) {
            return ResponseEntity.ok(TickerDto.from(entityOptional.get()));
        }
        // this symbol does not exist, so return a 404
        return ResponseEntity.notFound().build();
    }

}
