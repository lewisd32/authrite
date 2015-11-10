package com.lewisd.authrite.acctests.framework.time;

public class ParseResult<T> {

    private final T parsedObject;
    private final String remainingText;

    public ParseResult(final T parsedObject, final String remainingText) {
        this.parsedObject = parsedObject;
        this.remainingText = remainingText;
    }

    public T getParsedObject() {
        return parsedObject;
    }

    public String getRemainingText() {
        return remainingText;
    }
}
