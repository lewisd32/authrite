package com.lewisd.authrite.acctests.framework.time;

import java.time.Duration;

public class DurationAssertionFactory {
    public DurationAssertion parse(final String fieldName, final String description) {
        final Duration duration = new DurationParser().parse(Tokenizer.parse(description));
        return new DurationAssertion(fieldName, duration);
    }
}
