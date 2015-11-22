package com.lewisd.authrite.acctests.framework.valueholder;

import java.util.LinkedList;
import java.util.List;

public class MultipleValueHolder extends AbstractValueHolder<List<String>> {

    private final List<String> values = new LinkedList<>();

    @Override
    public List<String> get() {
        return new LinkedList<>(values);
    }

    @Override
    public void accept(final String value) {
        values.add(value);
    }

    @Override
    public boolean isSet() {
        return !values.isEmpty();
    }

    @Override
    public boolean hasValue() {
        return !values.isEmpty();
    }

}
