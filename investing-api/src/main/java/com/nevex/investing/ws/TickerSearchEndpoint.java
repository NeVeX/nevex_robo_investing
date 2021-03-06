package com.nevex.investing.ws;

import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.Ticker;
import com.nevex.investing.ws.model.ErrorDto;
import com.nevex.investing.ws.model.TickerDto;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/18/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/api/search")
public class TickerSearchEndpoint {

    private final TickerService tickerService;

    @Autowired
    public TickerSearchEndpoint(TickerService tickerService) {
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided ticker service is null"); }
        this.tickerService = tickerService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> searchForTicker(
            @NotBlank(message = "Invalid ticker query provided") @RequestParam(value = "name") String inputName) {
        String searchName = StringUtils.trim(inputName);
        if ( StringUtils.isBlank(searchName) || searchName.length() < 2) {
            return ResponseEntity.status(422).body(new ErrorDto("Search name is not valid"));
        }

        Set<Ticker> foundTickers = tickerService.searchForTicker(searchName);
        List<TickerDto> returnList = new ArrayList<>();
        if ( foundTickers != null && !foundTickers.isEmpty()) {
            returnList = foundTickers.parallelStream().map(TickerDto::new).collect(Collectors.toList());
        }
        return ResponseEntity.ok(returnList);
    }


}
