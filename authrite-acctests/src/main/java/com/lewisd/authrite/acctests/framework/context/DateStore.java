package com.lewisd.authrite.acctests.framework.context;

import com.google.common.annotations.VisibleForTesting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateStore {
    private final Map<String, Date> recordedTimes = new HashMap<>();

    public void recordTime(final String alias) {
        recordedTimes.put(alias, new Date());
    }

    public Date getTime(final String alias) {
        if ("now".equalsIgnoreCase(alias)) {
            return new Date();
        }
        return recordedTimes.get(alias);
    }

    @VisibleForTesting
    public void storeTime(final String alias, final Date date) {
        recordedTimes.put(alias, date);
    }
}
