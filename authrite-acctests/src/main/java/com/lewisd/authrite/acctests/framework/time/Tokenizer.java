package com.lewisd.authrite.acctests.framework.time;

import java.util.Deque;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Tokenizer {

    private Tokenizer() {
        // singleton
    }

    public static Deque<String> parse(String text) {
        final StringTokenizer tokenizer = new StringTokenizer(text, " ", false);

        // TODO: I feel like there must be a better way to do this, but I can't figure it out
        // right now (on an airplane)
        final Deque<String> tokens = new LinkedList<>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }
}
