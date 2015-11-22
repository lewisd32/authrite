package com.lewisd.authrite.acctests.framework.valueholder;

import java.util.function.Function;

public class FunctionValueHolder<T> extends AbstractValueHolder<T> {

    private final Function<String, T> translator;

    public FunctionValueHolder(final Function<String, T> translator) {
        this.translator = translator;
    }

    public T get() {
        return translator.apply(getRawValue());
    }

}
