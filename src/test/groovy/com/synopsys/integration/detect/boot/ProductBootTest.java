package com.synopsys.integration.detect.boot;

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
import com.synopsys.integration.detect.lifecycle.boot.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.BootDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class ProductBootTest {

    @Test(expected = DetectUserFriendlyException.class)
    public void bothProductsSkippedThrows() throws DetectUserFriendlyException {
        testBoot(BlackDuckDecision.skip(), PolarisDecision.skip(), new HashMap<>());
    }

    @Test(expected = DetectUserFriendlyException.class)
    public void blackDuckConnectionFailureThrows() throws DetectUserFriendlyException {
        testBoot(BlackDuckDecision.runOnlineDisconnected("Failed to connect"), PolarisDecision.skip(), new HashMap<>());
    }

    @Test()
    public void blackDuckConnectionFailureWithDisableReturnsNull() throws DetectUserFriendlyException {
        HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK, true);

        ProductRunData productRunData = testBoot(BlackDuckDecision.runOnlineDisconnected("Failed to connect"), PolarisDecision.skip(), properties);

        Assert.assertNull(productRunData);
    }

    @Test(expected = DetectUserFriendlyException.class)
    public void blackDuckConnectionFailureWithTestThrows() throws DetectUserFriendlyException {
        HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        testBoot(BlackDuckDecision.runOnlineDisconnected("Failed to connect"), PolarisDecision.skip(), properties);
    }

    @Test()
    public void blackDuckConnectionSuccessWithTestReturnsNull() throws DetectUserFriendlyException {
        HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        BlackDuckDecision blackDuckDecision = BlackDuckDecision.runOnlineConnected(Mockito.mock(BlackDuckServicesFactory.class), Mockito.mock(BlackDuckServerConfig.class));
        ProductRunData productRunData = testBoot(blackDuckDecision, PolarisDecision.skip(), properties);

        Assert.assertNull(productRunData);
    }

    @Test()
    public void blackDuckOnlyWorks() throws DetectUserFriendlyException {
        HashMap<DetectProperty, Boolean> properties = new HashMap<>();

        BlackDuckDecision blackDuckDecision = BlackDuckDecision.runOnlineConnected(Mockito.mock(BlackDuckServicesFactory.class), Mockito.mock(BlackDuckServerConfig.class));

        ProductRunData productRunData = testBoot(blackDuckDecision, PolarisDecision.skip(), properties);

        Assert.assertTrue(productRunData.shouldUseBlackDuckProduct());
        Assert.assertFalse(productRunData.shouldUsePolarisProduct());
    }

    @Test()
    public void polarisOnlyWorks() throws DetectUserFriendlyException {
        HashMap<DetectProperty, Boolean> properties = new HashMap<>();

        PolarisDecision polarisDecision = PolarisDecision.runOnline(Mockito.mock(PolarisServerConfig.class));

        ProductRunData productRunData = testBoot(BlackDuckDecision.skip(), polarisDecision, properties);

        Assert.assertFalse(productRunData.shouldUseBlackDuckProduct());
        Assert.assertTrue(productRunData.shouldUsePolarisProduct());
    }

    private ProductRunData testBoot(BlackDuckDecision blackDuckDecision, PolarisDecision polarisDecision, Map<DetectProperty, Boolean> properties) throws DetectUserFriendlyException {
        DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        properties.forEach((key, value) -> Mockito.when(detectConfiguration.getBooleanProperty(key, PropertyAuthority.None)).thenReturn(value));

        ProductBootFactory productBootFactory = Mockito.mock(ProductBootFactory.class);
        Mockito.when(productBootFactory.createPhoneHomeManager(Mockito.any())).thenReturn(null);

        BootDecision bootDecision = new BootDecision(blackDuckDecision, polarisDecision);

        ProductBoot productBoot = new ProductBoot();
        return productBoot.boot(bootDecision, detectConfiguration, productBootFactory);
    }
}
