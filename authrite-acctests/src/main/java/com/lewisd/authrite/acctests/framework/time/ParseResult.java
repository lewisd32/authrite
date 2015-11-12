package com.lewisd.authrite.acctests.framework.time;

import java.util.Deque;

public class ParseResult<T> {

    private final T parsedObject;
    private final Deque<String> remainingTokens;

    public ParseResult(final T parsedObject, final Deque<String> remainingTokens) {
        this.parsedObject = parsedObject;
        this.remainingTokens = remainingTokens;
    }

    public T getParsedObject() {
        return parsedObject;
    }

    public Deque<String> getRemainingTokens() {
        return remainingTokens;
    }
}
