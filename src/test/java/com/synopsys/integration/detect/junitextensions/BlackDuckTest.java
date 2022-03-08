package com.synopsys.integration.detect.junitextensions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;

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
