package com.lewisd.authrite.acctests.framework.time;

import java.time.Duration;

public class DurationAssertionFactory {
    public DurationAssertion parse(final String fieldName, final String description) {
        final ParseResult<Duration> result = new DurationParser().parse(description);
        if (!result.getRemainingText().isEmpty()) {
            throw new IllegalArgumentException("Trailing text in description: " + result.getRemainingText());
        }
        return new DurationAssertion(fieldName, result.getParsedObject());
    }
}
