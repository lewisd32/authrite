package com.lewisd.authrite.acctests.framework.time;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

public class TimeAssertion {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final String fieldName;
    private final Date beginDate;
    private final Date endDate;
    private boolean expectingNull;

    public TimeAssertion(final String fieldName, final Date beginDate, final Date endDate, final boolean expectingNull) {
        this.fieldName = fieldName;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.expectingNull = expectingNull;
    }

    public void assertMatches(final Date date) {
        if (expectingNull) {
            if (date != null) {
                throw new AssertionError(fieldName + " of " + formatDate(date) + " was expected to be never");
            }
            return;
        }

        if (date == null) {
            throw new AssertionError(fieldName + " was never");
        }
        if (beginDate != null) {
            if (date.before(beginDate)) {
                throw new AssertionError(fieldName + " of " + formatDate(date) + " was before begin date of " + formatDate(beginDate));
            }
        }
        if (endDate != null) {
            if (date.after(endDate)) {
                throw new AssertionError(fieldName + " of " + formatDate(date) + " was after end date of " + formatDate(endDate));
            }
        }
    }

    private String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    @VisibleForTesting
    public Date getBeginDate() {
        return beginDate;
    }

    @VisibleForTesting
    public Date getEndDate() {
        return endDate;
    }

    @VisibleForTesting
    public boolean isExpectingNull() {
        return expectingNull;
    }
}
