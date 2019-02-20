package com.synopsys.integration.detect.boot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.lifecycle.boot.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.BootDecider;
import com.synopsys.integration.detect.lifecycle.boot.decision.BootDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class BootDeciderTest {

    @Test()
    public void shouldRunPolaris() throws DetectUserFriendlyException {
        File userHome = Mockito.mock(File.class);
        DetectConfiguration detectConfiguration = polarisConfiguration("POLARIS_ACCESS_TOKEN", "access token text", "POLARIS_URL", "http://polaris.com");

        BootDecider bootDecider = new BootDecider();
        BootDecision bootDecision = bootDecider.decide(detectConfiguration, userHome);

        Assert.assertTrue(bootDecision.getPolarisDecision().shouldRun());
    }

    @Test()
    public void shouldRunBlackDuckOffline() throws DetectUserFriendlyException {
        File userHome = Mockito.mock(File.class);
        DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None)).thenReturn(true);

        BootDecider bootDecider = new BootDecider();
        BootDecision bootDecision = bootDecider.decide(detectConfiguration, userHome);

        Assert.assertTrue(bootDecision.getBlackDuckDecision().shouldRun());
        Assert.assertTrue(bootDecision.getBlackDuckDecision().isOffline());
    }

    @Test()
    public void shouldRunBlackDuckOnline() throws DetectUserFriendlyException {
        File userHome = Mockito.mock(File.class);
        DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None)).thenReturn("some-url");

        BootDecider bootDecider = new BootDecider();
        BootDecision bootDecision = bootDecider.decide(detectConfiguration, userHome);

        Assert.assertTrue(bootDecision.getBlackDuckDecision().shouldRun());
        Assert.assertFalse(bootDecision.getBlackDuckDecision().isOffline());
    }

    @Test()
    public void decidesNone() throws DetectUserFriendlyException {
        File userHome = Mockito.mock(File.class);
        DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);

        BootDecider bootDecider = new BootDecider();
        BootDecision bootDecision = bootDecider.decide(detectConfiguration, userHome);

        Assert.assertFalse(bootDecision.willRunAny());
    }

    private DetectConfiguration polarisConfiguration(String... polarisKeys) {
        Map<String, String> keyMap = new HashMap<>();
        for (int i = 0; i < polarisKeys.length; i += 2){
            keyMap.put(polarisKeys[i], polarisKeys[i + 1]);
        }
        DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperties(Mockito.any())).thenReturn(keyMap);

        return detectConfiguration;
    }
}
