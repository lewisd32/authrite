package com.lewisd.authrite.acctests.framework.time;

import com.lewisd.authrite.acctests.framework.context.DateStore;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class TimeAssertionFactoryTest {

    private static final Date FIRST_DATE = new Date(499051260000L);
    private static final Date SECOND_DATE = new Date(499137660000L);
    private final DateStore dates = new DateStore();

    @Before
    public void setupDates() {
        dates.store("firstDate", FIRST_DATE);
        dates.store("secondDate", SECOND_DATE);
    }

    @Test
    public void assertBetweenTwoAliases() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("between firstDate and secondDate");
        assertThat(timeAssertion.getBeginDate(), equalTo(FIRST_DATE));
        assertThat(timeAssertion.getEndDate(), equalTo(SECOND_DATE));
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }

    @Test
    public void assertBetweenTwoAliasesWithOffsets() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("between 1 minute after firstDate and 1 minute before secondDate");
        assertThat(timeAssertion.getBeginDate(), equalTo(new Date(FIRST_DATE.getTime() + 60000)));
        assertThat(timeAssertion.getEndDate(), equalTo(new Date(SECOND_DATE.getTime() - 60000)));
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }

    @Test
    public void assertBetweenTwoAliasesWithOffsetOnFirst() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("between 1 minute after firstDate and secondDate");
        assertThat(timeAssertion.getBeginDate(), equalTo(new Date(FIRST_DATE.getTime() + 60000)));
        assertThat(timeAssertion.getEndDate(), equalTo(SECOND_DATE));
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }

    @Test
    public void assertBetweenTwoAliasesWithOffsetOnSecond() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("between firstDate and 1 minute before secondDate");
        assertThat(timeAssertion.getBeginDate(), equalTo(FIRST_DATE));
        assertThat(timeAssertion.getEndDate(), equalTo(new Date(SECOND_DATE.getTime() - 60000)));
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }

    @Test
    public void assertIsNever() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("is never");
        assertThat(timeAssertion.getBeginDate(), nullValue());
        assertThat(timeAssertion.getEndDate(), nullValue());
        assertThat(timeAssertion.isExpectingNull(), equalTo(true));
    }

    @Test
    public void assertAfterAlias() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("after firstDate");
        assertThat(timeAssertion.getBeginDate(), equalTo(FIRST_DATE));
        assertThat(timeAssertion.getEndDate(), nullValue());
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }

    @Test
    public void assertAfterAliasWithOffset() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("after 1 minute after firstDate");
        assertThat(timeAssertion.getBeginDate(), equalTo(new Date(FIRST_DATE.getTime() + 60000)));
        assertThat(timeAssertion.getEndDate(), nullValue());
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }

    @Test
    public void assertBeforeAlias() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("before firstDate");
        assertThat(timeAssertion.getBeginDate(), nullValue());
        assertThat(timeAssertion.getEndDate(), equalTo(FIRST_DATE));
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }

    @Test
    public void assertBeforeAliasWithOffset() {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(dates, "field").parse("before 1 minute after firstDate");
        assertThat(timeAssertion.getBeginDate(), nullValue());
        assertThat(timeAssertion.getEndDate(), equalTo(new Date(FIRST_DATE.getTime() + 60000)));
        assertThat(timeAssertion.isExpectingNull(), equalTo(false));
    }


}