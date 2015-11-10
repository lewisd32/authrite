package com.lewisd.authrite.acctests.framework;

import com.jayway.restassured.RestAssured;
import com.lewisd.authrite.acctests.framework.driver.DslTools;
import com.lewisd.authrite.acctests.framework.time.ParseResult;
import com.lewisd.authrite.acctests.framework.driver.SystemDriver;
import com.lewisd.authrite.acctests.framework.context.TestContext;
import com.lewisd.authrite.acctests.framework.time.DurationParser;
import com.lmax.simpledsl.DslParams;
import com.lmax.simpledsl.OptionalParam;
import com.lmax.simpledsl.RequiredParam;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;

import java.time.Duration;

import static com.jayway.restassured.config.RedirectConfig.redirectConfig;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;

public class DslTestCase {

    private static String serverUri = "http://localhost:8080";

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
        DslParams params = new DslParams(
                args,
                new RequiredParam("alias"),
                new OptionalParam("padding").setDefault("10 millis")
        );
        String alias = params.value("alias");
        String padding = params.value("padding");

        // Short sleeps to make sure we don't record two times that are the same
        // by calling this twice too closely.
        sleep(padding);

        testContext.dates.recordTime(alias);

        sleep(padding);
    }

    protected void sleep(final String... args) {
        DslParams params = new DslParams(
                args,
                new RequiredParam("duration")
        );

        final ParseResult<Duration> result = new DurationParser().parse(params.value("duration"));
        if (!result.getRemainingText().isEmpty()) {
            throw new IllegalArgumentException("Trailing text in duration: " + result.getRemainingText());
        }

        try {
            Thread.sleep(result.getParsedObject().toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
