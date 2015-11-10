package com.lewisd.authrite.acctests.framework.time;

import java.time.Duration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DurationAssertion {

    private final String fieldName;
    private final Duration matchExact;

    public DurationAssertion(final String fieldName, final Duration matchExact) {
        this.fieldName = fieldName;
        this.matchExact = matchExact;
    }

    public void assertMatches(final Duration duration) {
        assertThat(duration, equalTo(matchExact));
    }
}
