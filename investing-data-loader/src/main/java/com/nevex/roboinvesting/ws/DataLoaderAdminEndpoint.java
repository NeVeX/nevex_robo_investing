package com.nevex.roboinvesting.ws;

import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.DataLoaderRunsRepository;
import com.nevex.roboinvesting.database.entity.DataLoaderErrorEntity;
import com.nevex.roboinvesting.database.entity.DataLoaderRunEntity;
import com.nevex.roboinvesting.service.TickerService;
import com.nevex.roboinvesting.service.model.PageableData;
import com.nevex.roboinvesting.service.model.Ticker;
import com.nevex.roboinvesting.ws.model.DataLoaderErrorDto;
import com.nevex.roboinvesting.ws.model.DataLoaderRunDto;
import com.nevex.roboinvesting.ws.model.ErrorDto;
import com.nevex.roboinvesting.ws.model.PageableDataDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/30/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/admin")
public class DataLoaderAdminEndpoint {
    private final static Logger LOGGER = LoggerFactory.getLogger(DataLoaderAdminEndpoint.class);
    private final DataLoaderRunsRepository dataLoaderRunsRepository;
    private final DataLoaderErrorsRepository dataLoaderErrorsRepository;
    private final static String ADMIN_HEADER_KEY = "Admin-Key";
    private final String adminKey;
    private final static int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    public DataLoaderAdminEndpoint(DataLoaderRunsRepository dataLoaderRunsRepository,
                                   DataLoaderErrorsRepository dataLoaderErrorsRepository,
                                   String adminKey) {
        if ( dataLoaderRunsRepository == null) { throw new IllegalArgumentException("Provided dataLoaderRunsRepository is null"); }
        if ( dataLoaderErrorsRepository == null) { throw new IllegalArgumentException("Provided dataLoaderErrorsRepository is null"); }
        if ( StringUtils.isBlank(adminKey) ) { throw new IllegalArgumentException("Provided adminKey is blank"); }
        this.dataLoaderErrorsRepository = dataLoaderErrorsRepository;
        this.dataLoaderRunsRepository = dataLoaderRunsRepository;
        this.adminKey = adminKey;
        LOGGER.info("The admin key in use is [{}]", adminKey);
    }

    @RequestMapping(value = "/runs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getDataLoaderRuns (
            @RequestHeader(ADMIN_HEADER_KEY) String adminHeaderKey,
            @Min(value = 0, message = "Invalid page size provided") @RequestParam(value = "page", defaultValue = "0") Integer pageNumber) {
        ResponseEntity<?> forbiddenResponse = checkHeaderKey(adminHeaderKey);
        if ( forbiddenResponse != null ) { return forbiddenResponse; }

        Pageable pageable = new PageRequest(pageNumber, DEFAULT_PAGE_SIZE);
        Page<DataLoaderRunEntity> pageData = dataLoaderRunsRepository.findAll(pageable);
        if ( pageData == null || !pageData.hasContent() ) {
            return ResponseEntity.ok(new PageableDataDto<>()); // empty response
        }
        PageableData<DataLoaderRunEntity> pageableData = new PageableData<>(pageData.getContent(), pageData);
        return ResponseEntity.ok(new PageableDataDto<DataLoaderRunDto>(pageableData, DataLoaderRunDto::new));
    }

    @RequestMapping(value = "/errors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<?> getDataLoaderErrors (
            @RequestHeader(ADMIN_HEADER_KEY) String adminHeaderKey,
            @Min(value = 0, message = "Invalid page size provided") @RequestParam(value = "page", defaultValue = "0") Integer pageNumber) {
        ResponseEntity<?> forbiddenResponse = checkHeaderKey(adminHeaderKey);
        if ( forbiddenResponse != null ) { return forbiddenResponse; }

        Pageable pageable = new PageRequest(pageNumber, DEFAULT_PAGE_SIZE);
        Page<DataLoaderErrorEntity> pageData = dataLoaderErrorsRepository.findAll(pageable);
        if ( pageData == null || !pageData.hasContent() ) {
            return ResponseEntity.ok(new PageableDataDto<>()); // empty response
        }
        PageableData<DataLoaderErrorEntity> pageableData = new PageableData<>(pageData.getContent(), pageData);
        return ResponseEntity.ok(new PageableDataDto<DataLoaderErrorDto>(pageableData, DataLoaderErrorDto::new));
    }

    @Deprecated // TODO: We should not do this here - but implement security earlier. Doing it here for poc work
    private ResponseEntity<ErrorDto> checkHeaderKey(String headerValue) {
        if ( !StringUtils.equalsIgnoreCase(headerValue, adminKey) ) {
            LOGGER.warn("Refusing access to admin api since given key is not correct [{}]", headerValue);
            return ResponseEntity.status(403).body(new ErrorDto("You are not authorized to access this resource"));
        }
        return null;
    }

}
