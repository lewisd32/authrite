package com.lewisd.authrite.acctests.framework.valueholder;

import io.github.unacceptable.alias.AliasStore;

public class AliasValueHolder<T> extends AbstractValueHolder<T> {

    private final AliasStore<T> aliasStore;

    public AliasValueHolder(final AliasStore<T> aliasStore) {
        this.aliasStore = aliasStore;
    }

    public T get() {
        return aliasStore.resolve(getRawValue());
    }

}
