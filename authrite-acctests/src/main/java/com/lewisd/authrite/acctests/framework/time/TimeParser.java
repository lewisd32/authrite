package com.lewisd.authrite.acctests.framework.time;

import com.lewisd.authrite.acctests.framework.context.DateStore;

import java.time.Duration;
import java.util.Date;
import java.util.Deque;

public class TimeParser {

    private final DateStore dates;
    private final String fieldName;

    public TimeParser(final DateStore dates, final String fieldName) {
        this.dates = dates;
        this.fieldName = fieldName;
    }

    public Date parse(final String description) {
        return parse(Tokenizer.parse(description));
    }

    public Date parse(final Deque<String> tokenList) {
        final ParseResult<Date> result = parsePartial(tokenList);
        if (!result.getRemainingTokens().isEmpty()) {
            throw new IllegalArgumentException("Trailing tokens found:" + result.getRemainingTokens());
        }
        return result.getParsedObject();
    }

    ParseResult<Date> parsePartial(final String description) {
        return parsePartial(Tokenizer.parse(description));
    }

    ParseResult<Date> parsePartial(final Deque<String> tokenList) {
        final String firstToken = tokenList.getFirst();
        if (Character.isDigit(firstToken.charAt(0))) {
            final ParseResult<Duration> result = new DurationParser().parsePartial(tokenList);

            final String preposition = tokenList.removeFirst();

            final String alias = tokenList.removeFirst();

            final Date date = dates.getTime(alias);
            if (date == null) {
                throw new IllegalArgumentException("No date named '" + alias + "' found for " + fieldName);
            }

            final long durationSign;
            if ("after".equalsIgnoreCase(preposition)) {
                durationSign = 1;
            } else if ("before".equalsIgnoreCase(preposition)) {
                durationSign = -1;
            } else {
                throw new IllegalArgumentException("Unknown preposition '" + preposition + "'. Should be 'before' or 'after'");
            }

            final Date adjustedDate = new Date(date.getTime() + result.getParsedObject().toMillis() * durationSign);
            return new ParseResult<>(adjustedDate, tokenList);
        }

        final String alias = tokenList.removeFirst();
        final Date date = dates.getTime(alias);
        if (date == null) {
            throw new IllegalArgumentException("No date named '" + alias + "' found for " + fieldName);
        }
        return new ParseResult<>(date, tokenList);
    }

}
