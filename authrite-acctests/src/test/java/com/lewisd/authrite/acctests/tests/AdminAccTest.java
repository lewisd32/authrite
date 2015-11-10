package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Before;
import org.junit.Test;

public class AdminAccTest extends DslTestCase {

    @Before
    public void beforeEachTest() {
        adminApi.login("admin@lewisd.com", "<password>");
    }

    @Test
    public void shouldGetOwnUser() {
        adminApi.assertUser("admin@lewisd.com", "displayName: Admin");
    }

    @Test
    public void shouldGetOtherUser() {
        publicApi.createUser("test@test.com", "Tester", "password123");

        adminApi.assertUser("test@test.com", "displayName: Tester");
    }

}
