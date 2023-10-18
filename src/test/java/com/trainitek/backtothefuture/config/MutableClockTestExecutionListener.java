package com.trainitek.backtothefuture.config;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import spock.util.time.MutableClock;

import java.time.Clock;

/**
 * Example of resetting the clock before each test.
 */
public class MutableClockTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        // Retrieve the MutableClock bean from the Spring context
        var mutableClock = testContext.getApplicationContext().getBean(MutableClock.class);

        // Set the MutableClock to the current system instant
        mutableClock.setInstant(Clock.systemDefaultZone().instant());
    }
}