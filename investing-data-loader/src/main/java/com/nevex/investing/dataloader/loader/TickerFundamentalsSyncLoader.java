//package com.nevex.investing.dataloader.loader;
//
//import com.nevex.investing.api.ApiException;
//import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
//import com.nevex.investing.api.usfundamentals.model.UsFundamentalIndicatorDto;
//import com.nevex.investing.api.usfundamentals.model.UsFundamentalsUpdatableDto;
//import com.nevex.investing.database.TickersRepository;
//import com.nevex.investing.database.entity.TickerEntity;
//import com.nevex.investing.database.entity.TickerFundamentalsEntity;
//import com.nevex.investing.dataloader.DataLoaderService;
//import com.nevex.investing.service.EdgarAdminService;
//import com.nevex.investing.service.ServiceException;
//import com.nevex.investing.service.TickerFundamentalsAdminService;
//import com.nevex.investing.service.model.TickerFundamentalsSync;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
//import static com.nevex.investing.dataloader.loader.DataLoaderOrder.TICKER_FUNDAMENTALS_SYNC_LOADER;
//
///**
// * Created by Mark Cunningham on 9/4/2017.
// */
//public class TickerFundamentalsSyncLoader extends DataLoaderWorker {
//
//    private final Logger LOGGER = LoggerFactory.getLogger(TickerFundamentalsSyncLoader.class);
//    private final TickerFundamentalsAdminService tickerFundamentalsAdminService;
//    private final UsFundamentalsApiClient usFundamentalsApiClient;
//    private final TickersRepository tickersRepository;
//    private final EdgarAdminService edgarAdminService;
//    private final ThreadLocal<Long> threadLocalNanoTime = new ThreadLocal<>();
//    private final ThreadLocal<List<UsFundamentalsUpdatableDto>> threadLocalUpdatableCompanies = new ThreadLocal<>();
//
//    public TickerFundamentalsSyncLoader(DataLoaderService dataLoaderService,
//                                        TickersRepository tickersRepository,
//                                        TickerFundamentalsAdminService tickerFundamentalsAdminService,
//                                        EdgarAdminService edgarAdminService,
//                                        UsFundamentalsApiClient usFundamentalsApiClient) {
//        super(dataLoaderService);
//        if ( tickerFundamentalsAdminService == null) { throw new IllegalArgumentException("Provided tickerFundamentalsAdminService is null"); }
//        if ( usFundamentalsApiClient == null) { throw new IllegalArgumentException("Provided usFundamentalsApiClient is null"); }
//        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickersRepository is null"); }
//        if ( edgarAdminService == null) { throw new IllegalArgumentException("Provided edgarAdminService is null"); }
//        this.tickerFundamentalsAdminService = tickerFundamentalsAdminService;
//        this.usFundamentalsApiClient = usFundamentalsApiClient;
//        this.tickersRepository = tickersRepository;
//        this.edgarAdminService = edgarAdminService;
//    }
//
//    @Override
//    public int getOrderNumber() {
//        return TICKER_FUNDAMENTALS_SYNC_LOADER;
//    }
//
//    @Override
//    public String getName() {
//        return "ticker-fundamentals-sync-loader";
//    }
//
//    @Override
//    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
//
//        long newNanoSeconds = System.nanoTime();
//
//        // Get the earliest nano time from the database
//        Optional<Long> earliestNanoTimeOpt = tickerFundamentalsAdminService.getLatestNanoTime();
//        if ( !earliestNanoTimeOpt.isPresent() ) {
//            LOGGER.info("[{}] will not do any work since there is no earliest nano time found in the database", getName());
//            return DataLoaderWorkerResult.nothingDone();
//        }
//        long earliestNanoTime = earliestNanoTimeOpt.get();
//
//        DataLoaderWorkerResult quarterlyResult = doWork(earliestNanoTime, usFundamentalsApiClient::getAllQuarterlyFundamentalsThatNeedsUpdates, this::processQuarterlyUpdateForTicker);
//        DataLoaderWorkerResult yearlyResults = doWork(earliestNanoTime, usFundamentalsApiClient::getAllYearlyFundamentalsThatNeedsUpdates, this::processYearlyUpdateForTicker);
//
//        saveNewSync(newNanoSeconds);
//
//        return new DataLoaderWorkerResult(quarterlyResult.getRecordsProcessed() + yearlyResults.getRecordsProcessed());
//    }
//
//    DataLoaderWorkerResult doWork(long earliestNanoTime,
//                                 CompanyUpdateProvider companyUpdateProvider,
//                                 Consumer<TickerEntity> consumerOfTickers) throws DataLoaderWorkerException {
//        // use this to get all the companies that need updating
//        List<UsFundamentalsUpdatableDto> updatableDtos;
//        LOGGER.info("Getting all updatable companies from us-fundamentals");
//        try {
//            // TODO: This may get too big
//            updatableDtos = companyUpdateProvider.getUpdates(earliestNanoTime);
//
//        } catch (Exception e) {
//            saveExceptionToDatabase("Could not get all the updatable companies using nano-time ["+earliestNanoTime+"]. Reason: "+e.getMessage());
//            LOGGER.error("Could not get all the updatable companies using nano-time [{}]", earliestNanoTime, e);
//            return DataLoaderWorkerResult.nothingDone();
//        }
//
//        if ( updatableDtos.isEmpty()) {
//            LOGGER.info("No updatable companies returned from us-fundamentals - nothing to update");
//            return DataLoaderWorkerResult.nothingDone();
//        }
//        LOGGER.info("Received a total of [{}] updatable companies from us-fundamentals", updatableDtos.size());
//        // Save all of these into a thread local for later use
//        threadLocalNanoTime.set(earliestNanoTime);
//        threadLocalUpdatableCompanies.set(updatableDtos);
//
//        try {
//            int amountProcessed = super.processAllPagesIndividuallyForRepo(tickersRepository, consumerOfTickers, 500);
//            return new DataLoaderWorkerResult(amountProcessed);
//        } finally {
//            // clear the thread locals
//            threadLocalNanoTime.remove();
//            threadLocalUpdatableCompanies.remove();
//        }
//    }
//
//    private void saveNewSync(long nanoSeconds) {
//        try {
//            tickerFundamentalsAdminService.saveSync(nanoSeconds);
//        } catch (Exception e) {
//            saveExceptionToDatabase("Could not save new sync for us-fundamentals with nanoSeconds ["+nanoSeconds+"]. Reason: "+e.getMessage());
//        }
//    }
//
//    private void processQuarterlyUpdateForTicker(TickerEntity tickerEntity) {
//        processTicker(tickerEntity, 'q');
//    }
//
//    private void processYearlyUpdateForTicker(TickerEntity tickerEntity) {
//        processTicker(tickerEntity, 'y');
//    }
//
//    private void processTicker(TickerEntity tickerEntity, char periodType) {
//        Optional<Long> cikOptional = edgarAdminService.getCikForTicker(tickerEntity.getId());
//        if ( !cikOptional.isPresent()) {
//            saveExceptionToDatabase("Could not get a CIK for ticker ["+tickerEntity.getSymbol()+"]");
//            return;
//        }
//        long cik = cikOptional.get();
//
//        List<UsFundamentalsUpdatableDto> updatableDtos = threadLocalUpdatableCompanies.get();
//
//        // see if this ticker was in the list of updatable companies
//        List<UsFundamentalsUpdatableDto> companyUpdates = updatableDtos.stream().filter( dto -> dto.getCompanyId() == cik).collect(Collectors.toList());
//        if ( companyUpdates.isEmpty()) {
//            return;
//        }
//        applyUpdates(tickerEntity.getId(), companyUpdates, periodType);
//    }
//
//    private void applyUpdates(int tickerId, List<UsFundamentalsUpdatableDto> companyUpdates, char periodType) {
//        for ( UsFundamentalsUpdatableDto dto : companyUpdates) {
//
//            // get the fundamentals from the database
//            Optional<TickerFundamentalsEntity> existingFundamentalsOpt = tickerFundamentalsAdminService.getFundamentals(tickerId, dto.getPeriodDate(), periodType);
//
//            Collection<UsFundamentalIndicatorDto> updatedIndicators;
//            try {
//                updatedIndicators = usFundamentalsApiClient.getFundamentalsForPeriod(dto.getCompanyId(), dto.getPeriodDate(), periodType);
//            } catch (Exception e) {
//                saveExceptionToDatabase("Could not get period fundamentals for CIK ["+dto.getCompanyId()+"]. Reason: "+e.getMessage());
//                continue;
//            }
//
//            if ( updatedIndicators.isEmpty()) {
//                // we are told to delete the records if this happens!
//            }
//        }
//    }
//
//
//    @FunctionalInterface
//    private interface CompanyUpdateProvider {
//        List<UsFundamentalsUpdatableDto> getUpdates(long nanoTime) throws ApiException;
//    }
//
//}
