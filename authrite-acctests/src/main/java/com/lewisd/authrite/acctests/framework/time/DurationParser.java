package com.lewisd.authrite.acctests.framework.time;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.Duration;
import java.util.Arrays;
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

    public ParseResult<Duration> parse(final String description) {
        final String[] parts = StringUtils.split(description, ' ');
        return parse(parts);
    }

    public ParseResult<Duration> parse(final String[] parts) {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Description must contain at least two components (was '" +
                                                       Joiner.on(' ').join(parts) + "')");
        }

        long duration = 0;
        int partsConsumed = 0;

        do {
            final Optional<Long> maybeDuration = parseTimeNumeric(parts[partsConsumed], parts[partsConsumed + 1]);
            // return early if we reach something unparseable
            if (!maybeDuration.isPresent()) {
                return new ParseResult<>(Duration.ofMillis(duration), getRemaining(parts, partsConsumed));
            }
            duration += maybeDuration.get();
            partsConsumed += 2;
        } while (partsConsumed+1 < parts.length);

        return new ParseResult<>(Duration.ofMillis(duration), getRemaining(parts, partsConsumed));
    }

    private static Optional<Long> parseTimeNumeric(final String number, final String unit) {
        final int numericComponent;
        try {
            numericComponent = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        final Optional<TimeUnit> maybeTimeUnit = TIME_UNIT_NAMES
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(unit.toLowerCase()))
                .map(Map.Entry::getKey)
                .findFirst();
        final Optional<Long> maybeMillis = maybeTimeUnit.map(timeUnit -> timeUnit.toMillis(numericComponent));


        if (maybeMillis.isPresent()) {
            return maybeMillis;
        } else {
            throw new IllegalArgumentException("Unknown time unit: " + unit);
        }
    }

    private static String getRemaining(final String[] parts, final int partsConsumed) {
        return Joiner.on(' ').join(Arrays.copyOfRange(parts, partsConsumed, parts.length));
    }
}
