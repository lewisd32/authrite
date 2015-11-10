package com.lewisd.authrite.acctests.framework.time;

import com.lewisd.authrite.acctests.framework.context.DateStore;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import static com.lewisd.authrite.acctests.framework.time.ParserTool.getFirstWord;
import static com.lewisd.authrite.acctests.framework.time.ParserTool.removeFirstWord;

public class TimeAssertionFactory {

    private final String fieldName;
    private final TimeParser timeParser;

    public TimeAssertionFactory(final DateStore dates, final String fieldName) {
        this.fieldName = fieldName;
        timeParser = new TimeParser(dates, fieldName);
    }

    public TimeAssertion parse(final String description) {
        final String[] parts = StringUtils.split(description, ' ');
        if (parts.length == 0) {
            throw new IllegalArgumentException(fieldName + " can't be empty");
        }

        if (parts[0].equalsIgnoreCase("is")) {
            if (parts[1].equalsIgnoreCase("never")) {
                return new TimeAssertion(fieldName, null, null, true);
            }
            throw new IllegalArgumentException("Expected format: is never");
        } else if (parts[0].equalsIgnoreCase("between")) {

            ParseResult<Date> firstDateResult = timeParser.parse(removeFirstWord(parts));
            String remainingText = firstDateResult.getRemainingText();
            String conjunction = getFirstWord(remainingText);
            remainingText = removeFirstWord(remainingText);
            if (!conjunction.equalsIgnoreCase("and")) {
                throw new IllegalArgumentException("Expected format: between date1 and date2");
            }
            ParseResult<Date> secondDateResult = timeParser.parse(remainingText);

            final Date date1 = firstDateResult.getParsedObject();
            final Date date2 = secondDateResult.getParsedObject();

            return new TimeAssertion(fieldName, date1, date2, false);
        } else if (parts[0].equalsIgnoreCase("after")) {
            ParseResult<Date> result = timeParser.parse(removeFirstWord(parts));
            return new TimeAssertion(fieldName, result.getParsedObject(), null, false);
        } else if (parts[0].equalsIgnoreCase("before")) {
            ParseResult<Date> result = timeParser.parse(removeFirstWord(parts));
            return new TimeAssertion(fieldName, null, result.getParsedObject(), false);
        }
        throw new IllegalArgumentException("Unknown date expectation format: " + description);
    }

}
