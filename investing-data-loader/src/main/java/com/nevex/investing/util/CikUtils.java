package com.nevex.investing.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class CikUtils {

    public static Optional<Long> parseCik(String cik) {
        String strippedCik = StringUtils.stripStart(cik, "0");
        try {
            return Optional.of(Long.valueOf(strippedCik));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
