package com.lewisd.authrite.acctests.framework.context;

import io.github.unacceptable.alias.Generator;

import java.util.Date;

public class DateGenerator implements Generator<Date> {

    @Override
    public Date generate(final String alias) {
        return new Date();
    }
}
