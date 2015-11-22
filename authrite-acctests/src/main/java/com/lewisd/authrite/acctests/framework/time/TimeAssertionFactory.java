package com.lewisd.authrite.acctests.framework.time;

import com.lewisd.authrite.acctests.framework.context.DateStore;

import java.util.Date;
import java.util.Deque;

public class TimeAssertionFactory {

    private final String fieldName;
    private final TimeParser timeParser;

    public TimeAssertionFactory(final DateStore dates, final String fieldName) {
        this.fieldName = fieldName;
        timeParser = new TimeParser(dates, fieldName);
    }

    //CHECKSTYLE.OFF: ReturnCount
    public TimeAssertion parse(final String description) {
        final Deque<String> tokenList = Tokenizer.parse(description);
        if (tokenList.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " can't be empty");
        }

        final String firstToken = tokenList.removeFirst();
        if ("is".equalsIgnoreCase(firstToken)) {
            final String secondToken = tokenList.removeFirst();
            if ("never".equalsIgnoreCase(secondToken)) {
                return new TimeAssertion(fieldName, null, null, true);
            }
            throw new IllegalArgumentException("Expected format: is never");
        } else if ("between".equalsIgnoreCase(firstToken)) {

            final ParseResult<Date> firstDateResult = timeParser.parsePartial(tokenList);
            final String conjunction = tokenList.removeFirst();
            if (!conjunction.equalsIgnoreCase(conjunction)) {
                throw new IllegalArgumentException("Expected format: between date1 and date2");
            }

            final Date date1 = firstDateResult.getParsedObject();
            final Date date2 = timeParser.parse(tokenList);

            return new TimeAssertion(fieldName, date1, date2, false);
        } else if ("after".equalsIgnoreCase(firstToken)) {
            final Date date = timeParser.parse(tokenList);
            return new TimeAssertion(fieldName, date, null, false);
        } else if ("before".equalsIgnoreCase(firstToken)) {
            final Date date = timeParser.parse(tokenList);
            return new TimeAssertion(fieldName, null, date, false);
        }
        throw new IllegalArgumentException("Unknown date expectation format: " + description);
    }
    //CHECKSTYLE.ON: ReturnCount

}
