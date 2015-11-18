package com.lewisd.authrite.acctests.framework.time;

import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DurationParserTest {

    @Test
    public void testParseBasicDuration() {
        Duration duration = new DurationParser().parse("28 days");
        assertThat(duration, equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28))));
    }

    @Test
    public void testParseDurationWithMultipleComponents() {
        Duration duration = new DurationParser().parse("28 days 3 hours");
        assertThat(duration,
                   equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28) + TimeUnit.HOURS.toMillis(3))));
    }

    @Test
    public void testParseDurationWithTrailingText() {
        try {
            new DurationParser().parse("28 days later");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), equalTo("Trailing tokens found: [later]"));
        }
    }

    @Test
    public void testParsePartialDurationWithTrailingText() {
        ParseResult<Duration> result = new DurationParser().parsePartial("28 days later");
        assertThat(result.getParsedObject(),
                   equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28))));
        assertThat(result.getRemainingTokens(), equalTo(asTokenList("later")));
    }

    @Test
    public void testParseDurationWithMoreTrailingText() {
        try {
            new DurationParser().parse("28 days later is a British movie");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), equalTo("Trailing tokens found: [later, is, a, British, movie]"));
        }
    }

    @Test
    public void testParsePartialDurationWithMoreTrailingText() {
         ParseResult<Duration> result = new DurationParser().parsePartial("28 days later is a British movie");
        assertThat(result.getParsedObject(),
                   equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28))));
        assertThat(result.getRemainingTokens(),
                   equalTo(asTokenList("later", "is", "a", "British", "movie")));
    }

    @Test
    public void testParseSinglePartIsError() {
        try {
            new DurationParser().parse("28");
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected exception
        }
    }

    @Test
    public void testParseUnknownUnitIsError() {
        try {
            new DurationParser().parse("28 something");
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), equalTo("Unknown time unit: something"));
        }
    }

    @Test
    public void testParseMissingUnitIsError() {
        try {
            new DurationParser().parsePartial("28 something something");
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), equalTo("Unknown time unit: something"));
        }
    }

    private Deque<String> asTokenList(String... items) {
        return new LinkedList<>(Lists.newArrayList(items));
    }

}