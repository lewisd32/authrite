package com.lewisd.authrite.acctests.framework.valueholder;

public abstract class AbstractValueHolder<T> implements ValueHolder<T> {

    private String value;
    private boolean set;

    @Override
    public void accept(final String value) {
        this.value = value;
        this.set = true;
    }

    public String getRawValue() {
        return value;
    }

    public boolean hasValue() {
        return set;
    }

    public boolean isSet() {
        return set;
    }

}
