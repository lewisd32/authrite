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

    public TimeAssertion parse(final String description) {
        final Deque<String> tokenList = Tokenizer.parse(description);
        if (tokenList.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " can't be empty");
        }

        String firstToken = tokenList.removeFirst();
        if (firstToken.equalsIgnoreCase("is")) {
            String secondToken = tokenList.removeFirst();
            if (secondToken.equalsIgnoreCase("never")) {
                return new TimeAssertion(fieldName, null, null, true);
            }
            throw new IllegalArgumentException("Expected format: is never");
        } else if (firstToken.equalsIgnoreCase("between")) {

            ParseResult<Date> firstDateResult = timeParser.parsePartial(tokenList);
            String conjunction = tokenList.removeFirst();
            if (!conjunction.equalsIgnoreCase("and")) {
                throw new IllegalArgumentException("Expected format: between date1 and date2");
            }

            final Date date1 = firstDateResult.getParsedObject();
            final Date date2 = timeParser.parse(tokenList);

            return new TimeAssertion(fieldName, date1, date2, false);
        } else if (firstToken.equalsIgnoreCase("after")) {
            Date date = timeParser.parse(tokenList);
            return new TimeAssertion(fieldName, date, null, false);
        } else if (firstToken.equalsIgnoreCase("before")) {
            Date date = timeParser.parse(tokenList);
            return new TimeAssertion(fieldName, null, date, false);
        }
        throw new IllegalArgumentException("Unknown date expectation format: " + description);
    }

}
