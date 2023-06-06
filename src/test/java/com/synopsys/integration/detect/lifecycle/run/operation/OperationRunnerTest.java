package com.synopsys.integration.detect.lifecycle.run.operation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.mockito.Mockito;

import com.synopsys.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.exception.IntegrationException;

public class OperationRunnerTest {
    private static OperationRunner operationRunner;
    @BeforeAll
    public static void setUp() {
        operationRunner = new OperationRunner(
            Mockito.mock(DetectDetectableFactory.class),
            Mockito.mock(DetectFontLoaderFactory.class),
            Mockito.mock(BootSingletons.class),
            Mockito.mock(UtilitySingletons.class),
            Mockito.mock(EventSingletons.class)
        );
    }

    @Test
    public void testCalculateMaxWaitInSeconds() throws IntegrationException {
        // Lower bound edge cases
        Assertions.assertEquals(5, operationRunner.calculateMaxWaitInSeconds(0));
        Assertions.assertEquals(5, operationRunner.calculateMaxWaitInSeconds(-1));
        Assertions.assertEquals(5, operationRunner.calculateMaxWaitInSeconds(2));
        Assertions.assertEquals(5, operationRunner.calculateMaxWaitInSeconds(3));
        Assertions.assertEquals(5, operationRunner.calculateMaxWaitInSeconds(4));

        // Core cases
        Assertions.assertEquals(5, operationRunner.calculateMaxWaitInSeconds(5));
        Assertions.assertEquals(8, operationRunner.calculateMaxWaitInSeconds(6));
        Assertions.assertEquals(13, operationRunner.calculateMaxWaitInSeconds(7));
        Assertions.assertEquals(21, operationRunner.calculateMaxWaitInSeconds(8));
        Assertions.assertEquals(34, operationRunner.calculateMaxWaitInSeconds(9));
        Assertions.assertEquals(55, operationRunner.calculateMaxWaitInSeconds(10));

        // Upper bound edge cases
        Assertions.assertEquals(55, operationRunner.calculateMaxWaitInSeconds(11));
        Assertions.assertEquals(55, operationRunner.calculateMaxWaitInSeconds(100));
    }
}
