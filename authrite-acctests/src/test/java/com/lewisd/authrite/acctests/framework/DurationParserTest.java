package com.lewisd.authrite.acctests.framework;

import com.lewisd.authrite.acctests.framework.time.ParseResult;
import com.lewisd.authrite.acctests.framework.time.DurationParser;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DurationParserTest {

    @Test
    public void testParseBasicDuration() {
        final ParseResult<Duration> result = new DurationParser().parse("28 days");
        assertThat(result.getParsedObject(),
                   equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28))));
        assertThat(result.getRemainingText(), equalTo(""));
    }

    @Test
    public void testParseDurationWithMultipleComponents() {
        final ParseResult<Duration> result = new DurationParser().parse("28 days 3 hours");
        assertThat(result.getParsedObject(),
                   equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28) + TimeUnit.HOURS.toMillis(3))));
        assertThat(result.getRemainingText(), equalTo(""));
    }

    @Test
    public void testParseDurationWithTrailingText() {
        final ParseResult<Duration> result = new DurationParser().parse("28 days later");
        assertThat(result.getParsedObject(),
                   equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28))));
        assertThat(result.getRemainingText(), equalTo("later"));
    }

    @Test
    public void testParseDurationWithMoreTrailingText() {
        final ParseResult<Duration> result = new DurationParser().parse("28 days later is a British movie");
        assertThat(result.getParsedObject(),
                   equalTo(Duration.ofMillis(TimeUnit.DAYS.toMillis(28))));
        assertThat(result.getRemainingText(),
                   equalTo("later is a British movie"));
    }

    @Test
    public void testParseSinglePartIsError() {
        try {
            new DurationParser().parse("28");
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testParseUnknownUnitIsError() {
        try {
            new DurationParser().parse("28 something");
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testParseMissingUnitIsError() {
        try {
            new DurationParser().parse("28 something something");
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

}