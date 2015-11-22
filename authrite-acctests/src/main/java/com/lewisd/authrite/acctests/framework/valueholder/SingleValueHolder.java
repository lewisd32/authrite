package com.lewisd.authrite.acctests.framework.valueholder;

public class SingleValueHolder extends AbstractValueHolder<String> {

    @Override
    public String get() {
        return getRawValue();
    }
}
