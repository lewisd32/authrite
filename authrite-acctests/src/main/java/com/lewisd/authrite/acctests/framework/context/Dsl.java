package com.lewisd.authrite.acctests.framework.context;

import com.lewisd.authrite.acctests.framework.Lazily;
import com.lewisd.authrite.acctests.framework.driver.SystemDriver;

import java.util.function.Supplier;

/**
 * Provides basic infrastructure for driver-based DSL classes. This maintains the underlying driver, providing it via
 * {@link #driver()}.
 *
 * @param <D>
 *         the driver class.
 */
public abstract class Dsl<D> {
    private final Supplier<? extends D> driverFactory;
    private D driver = null;

    /**
     * The test context for the current test. DSL classes can use the context to store and generate mappings between
     * test-case aliases and test-run values, to prevent data collisions between tests from polluting the results.
     */
    protected final TestContext testContext;

    /**
     * Creates a new DSL. The <var>driverFactory</var> should normally be a method handle from {@link
     * SystemDriver}, for example:
     * <pre>
     *     public class ExampleDsl extends Dsl<ExampleDriver> {
     *         public ExampleDsl(SystemDriver systemDriver, TestContext testContext) {
     *             super(systemDriver::exampleDriver, testContext);
     *         }
     *     }
     * </pre>
     */
    public Dsl(Supplier<? extends D> driverFactory, TestContext testContext) {
        this.driverFactory = driverFactory;
        this.testContext = testContext;
    }

    /**
     * Provides (and creates, if needed) a driver for the DSL. This is postponed until the driver is actually used, to
     * allow for drivers with expensive initialization.
     *
     * @return an instance of <var>D</var> obtained from the underlying driver factory.
     */
    protected final D driver() {
        return driver = Lazily.create(driver, driverFactory);
    }
}
