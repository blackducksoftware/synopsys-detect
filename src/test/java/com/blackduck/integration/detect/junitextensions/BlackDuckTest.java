package com.blackduck.integration.detect.junitextensions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.blackduck.integration.blackduck.api.manual.view.ProjectView;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.log.BufferedIntLogger;
import com.blackduck.integration.log.LogLevel;

@ExtendWith(BlackDuckParametersExtension.class)
public class BlackDuckTest {
    //optional, but if you want to inject a logger into the factory, name it like this
    private final BufferedIntLogger blackDuckLogger = new BufferedIntLogger();

    @Disabled("This test is an example of how to use the BlackDuckParametersExtension")
    @Test
    public void testBlackDuck(@BlackDuck BlackDuckServicesFactory blackDuckServicesFactory) throws IntegrationException {
        blackDuckServicesFactory
            .createProjectService()
            .getAllProjects()
            .stream()
            .map(ProjectView::getName)
            .forEach(System.out::println);

        assertTrue(blackDuckLogger.getOutputString(LogLevel.TRACE).length() > 100);
    }

}
