package com.lewisd.authrite.acctests.framework.time;

import com.lewisd.authrite.acctests.framework.context.DateStore;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Date;

import static com.lewisd.authrite.acctests.framework.time.ParserTool.removeFirstWord;

public class TimeParser {

    private final DateStore dates;
    private final String fieldName;

    public TimeParser(final DateStore dates, final String fieldName) {
        this.dates = dates;
        this.fieldName = fieldName;
    }

    public ParseResult<Date> parse(String text) {
        if (Character.isDigit(text.charAt(0))) {
            final ParseResult<Duration> result = new DurationParser().parse(text);
            String remainingText = result.getRemainingText();

            String preposition = ParserTool.getFirstWord(remainingText);
            remainingText = ParserTool.removeFirstWord(remainingText);

            String alias = ParserTool.getFirstWord(remainingText);
            remainingText = ParserTool.removeFirstWord(remainingText);

            Date date = dates.getTime(alias);
            if (date == null) {
                throw new IllegalArgumentException("No date named '" + alias + "' found for " + fieldName);
            }

            final long durationSign;
            if (preposition.equalsIgnoreCase("after")) {
                durationSign = 1;
            } else if (preposition.equalsIgnoreCase("before")) {
                durationSign = -1;
            } else {
                throw new IllegalArgumentException("Unknown preposition '" + preposition + "'. Should be 'before' or 'after'");
            }

            final Date adjustedDate = new Date(date.getTime() + result.getParsedObject().toMillis() * durationSign);
            return new ParseResult<>(adjustedDate, remainingText);
        }

        final String[] parts = StringUtils.split(text, ' ');
        String alias = parts[0];
        Date date = dates.getTime(alias);
        return new ParseResult<>(date, ParserTool.removeFirstWord(parts));
    }

}
