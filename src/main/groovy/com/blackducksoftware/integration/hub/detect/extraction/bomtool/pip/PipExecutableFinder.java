package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PipExecutableFinder {
    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String resolvedPip = null;
    private boolean hasLookedForPip = false;

    public String findPip(final StrategyEnvironment environment) throws StrategyException {
        try {
            if (!hasLookedForPip) {
                hasLookedForPip = true;
                ExecutableType pipType = ExecutableType.PIP;
                if (detectConfiguration.getPythonThreeOverride()) {
                    pipType = ExecutableType.PIP3;
                }
                resolvedPip = executableManager.getExecutablePathOrOverride(pipType, true, environment.getDirectory(), null);
            }
            return resolvedPip;
        }catch (final Exception e) {
            throw new StrategyException(e);
        }
    }
}
