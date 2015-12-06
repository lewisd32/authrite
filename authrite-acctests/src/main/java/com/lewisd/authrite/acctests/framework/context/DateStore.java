package com.lewisd.authrite.acctests.framework.context;

import io.github.unacceptable.alias.AliasStore;

import java.util.Date;

public class DateStore extends AliasStore<Date> {

    public DateStore() {
        super(new DateGenerator()::generate);
    }

    @Override
    public Date resolve(final String alias) {
        if ("now".equals(alias)) {
            return new Date();
        }
        return super.resolve(alias);
    }
}
