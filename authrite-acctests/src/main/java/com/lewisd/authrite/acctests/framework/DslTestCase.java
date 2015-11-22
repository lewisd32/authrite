package com.lewisd.authrite.acctests.framework;

import com.jayway.restassured.RestAssured;
import com.lewisd.authrite.acctests.framework.context.TestContext;
import com.lewisd.authrite.acctests.framework.driver.DslTools;
import com.lewisd.authrite.acctests.framework.driver.SystemDriver;
import com.lewisd.authrite.acctests.framework.time.DurationParser;
import com.lmax.simpledsl.DslParams;
import com.lmax.simpledsl.OptionalParam;
import com.lmax.simpledsl.RequiredParam;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;

import java.time.Duration;
import java.util.Date;

import static com.jayway.restassured.config.RedirectConfig.redirectConfig;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;

//CHECKSTYLE.OFF: VisibilityModifier
//CHECKSTYLE.OFF: ClassDataAbstractionCoupling
//CHECKSTYLE.OFF: DeclarationOrder
public class DslTestCase {

    private final SystemDriver systemDriver = new SystemDriver();
    private final TestContext testContext = new TestContext();

    protected final PublicApi publicApi = new PublicApi(systemDriver, testContext);
    protected final PublicApi publicApi2 = new PublicApi(systemDriver, testContext);
    protected final PublicApi adminApi = new PublicApi(systemDriver, testContext);
    protected final DslTools dslTools = new DslTools(testContext);

    @Rule
    public final TestRule systemDriveRules = systemDriver.rules();

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = systemDriver.baseUrl();
        RestAssured.basePath = "/api";
        RestAssured.config = newConfig().redirect(redirectConfig().followRedirects(false));
    }

    @Before
    public void recordStartTime() {
        recordTime("testStart");
    }

    protected void recordTime(final String... args) {
        final DslParams params = new DslParams(
                args,
                new RequiredParam("alias"),
                new OptionalParam("padding").setDefault("10 millis")
        );
        final String alias = params.value("alias");
        final String padding = params.value("padding");

        // Short sleeps to make sure we don't record two times that are the same
        // by calling this twice too closely.
        sleep(padding);

        testContext.dates.store(alias, new Date());

        sleep(padding);
    }

    protected void sleep(final String... args) {
        final DslParams params = new DslParams(
                args,
                new RequiredParam("duration")
        );

        final Duration duration = new DurationParser().parse(params.value("duration"));

        try {
            Thread.sleep(duration.toMillis());
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
