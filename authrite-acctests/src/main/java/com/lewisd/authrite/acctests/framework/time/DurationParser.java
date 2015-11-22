package com.lewisd.authrite.acctests.framework.time;

import com.google.common.collect.Sets;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.Duration;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@NotThreadSafe
public class DurationParser {

    static final Map<TimeUnit, Set<String>> TIME_UNIT_NAMES = new HashMap<>();
    static {
        TIME_UNIT_NAMES.put(TimeUnit.DAYS, Sets.newHashSet("day", "days", "d"));
        TIME_UNIT_NAMES.put(TimeUnit.HOURS, Sets.newHashSet("hour", "hours", "h", "hr"));
        TIME_UNIT_NAMES.put(TimeUnit.MINUTES, Sets.newHashSet("minute", "minutes", "m", "min"));
        TIME_UNIT_NAMES.put(TimeUnit.SECONDS, Sets.newHashSet("second", "seconds", "s", "sec"));
        TIME_UNIT_NAMES.put(TimeUnit.MILLISECONDS, Sets.newHashSet("milli", "millis", "milliseconds", "ms"));
    }

    public Duration parse(final String description) {
        return parse(Tokenizer.parse(description));
    }

    public Duration parse(final Deque<String> tokenList) {
        final ParseResult<Duration> result = parsePartial(tokenList);
        if (!result.getRemainingTokens().isEmpty()) {
            throw new IllegalArgumentException("Trailing tokens found: " + result.getRemainingTokens());
        }
        return result.getParsedObject();
    }

    ParseResult<Duration> parsePartial(final String description) {
        return parsePartial(Tokenizer.parse(description));
    }

    ParseResult<Duration> parsePartial(final Deque<String> tokenList) {
        if (tokenList.size() < 2) {
            throw new IllegalArgumentException("Description must contain at least two components (was '"
                                                       + tokenList + "')");
        }

        long duration = 0;

        do {
            final Optional<Long> maybeDuration = parseTimeNumeric(tokenList);
            // return early if we reach something unparseable
            if (!maybeDuration.isPresent()) {
                return new ParseResult<>(Duration.ofMillis(duration), tokenList);
            }
            duration += maybeDuration.get();
        } while (!tokenList.isEmpty());

        return new ParseResult<>(Duration.ofMillis(duration), tokenList);
    }

    private static Optional<Long> parseTimeNumeric(final Deque<String> tokenList) {
        final int numericComponent;
        try {
            numericComponent = Integer.parseInt(tokenList.getFirst());
        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
        tokenList.removeFirst();

        final String timeUnitToken = tokenList.getFirst();

        final Optional<TimeUnit> maybeTimeUnit = TIME_UNIT_NAMES
                .entrySet()
                .stream()
                .filter(entry -> {
                    return entry.getValue().contains(timeUnitToken.toLowerCase());
                })
                .map(Map.Entry::getKey)
                .findFirst();
        final Optional<Long> maybeMillis = maybeTimeUnit.map(timeUnit -> timeUnit.toMillis(numericComponent));


        if (maybeMillis.isPresent()) {
            tokenList.removeFirst();
            return maybeMillis;
        } else {
            throw new IllegalArgumentException("Unknown time unit: " + timeUnitToken);
        }
    }

}
