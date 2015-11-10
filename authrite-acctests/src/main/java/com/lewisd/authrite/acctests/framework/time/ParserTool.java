package com.lewisd.authrite.acctests.framework.time;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class ParserTool {

    private ParserTool() {
        // Singleton :|
    }

    public static String getFirstWord(final String text) {
        final String[] parts = StringUtils.split(text, ' ');
        return parts[0];
    }

    public static String removeFirstWord(final String text) {
        final String[] parts = StringUtils.split(text, ' ');
        return removeFirstWord(parts);
    }

    public static String removeFirstWord(final String[] parts) {
        final String[] remainingParts = Arrays.copyOfRange(parts, 1, parts.length);
        return Joiner.on(' ').join(remainingParts);
    }


}
