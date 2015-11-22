package com.lewisd.authrite.acctests.framework.time;

import java.time.Duration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DurationAssertion {

    private final Duration matchExact;

    public DurationAssertion(final Duration matchExact) {
        this.matchExact = matchExact;
    }

    public void assertMatches(final Duration duration) {
        assertThat(duration, equalTo(matchExact));
    }
}
