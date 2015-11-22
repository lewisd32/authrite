package com.lewisd.authrite.acctests.framework.valueholder;

import io.github.unacceptable.alias.AliasStore;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ValueHolder<T> extends Consumer<String> {

    T get();

    String getRawValue();

    boolean hasValue();

    boolean isSet();

    @SuppressWarnings("unchecked")
    static <T> ValueHolder<T> fromStore(final AliasStore emailAddresses) {
        return new AliasValueHolder<>(emailAddresses);
    }

    @SuppressWarnings("unchecked")
    static ValueHolder<String> singleValue() {
        return new SingleValueHolder();
    }

    @SuppressWarnings("unchecked")
    static ValueHolder<List<String>> multipleValues() {
        return new MultipleValueHolder();
    }

    @SuppressWarnings("unchecked")
    static <T> ValueHolder<T> withFunction(final Function<String, T> translator) {
        return new FunctionValueHolder(translator);
    }
}
