package com.synopsys.integration.detect.boot;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.config.DetectConfig;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityResult;
import com.synopsys.integration.detect.lifecycle.boot.product.PolarisConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.PolarisConnectivityResult;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class ProductBootTest {
    @Test(expected = DetectUserFriendlyException.class)
    public void bothProductsSkippedThrows() throws DetectUserFriendlyException {
        testBoot(BlackDuckDecision.skip(), PolarisDecision.skip());
    }

    @Test(expected = DetectUserFriendlyException.class)
    public void blackDuckConnectionFailureThrows() throws DetectUserFriendlyException {
        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.failure("Failed to connect");
        testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, new HashMap<>());
    }

    @Test(expected = DetectUserFriendlyException.class)
    public void polarisConnectionFailureThrows() throws DetectUserFriendlyException {
        final PolarisConnectivityResult connectivityResult = PolarisConnectivityResult.failure("Failed to connect");
        testBoot(BlackDuckDecision.skip(), PolarisDecision.runOnline(null), null, connectivityResult, new HashMap<>());
    }

    @Test()
    public void blackDuckFailureWithIgnoreReturnsFalse() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_IGNORE_CONNECTION_FAILURES, true);

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.failure("Failed to connect");

        final ProductRunData productRunData = testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);

        Assert.assertFalse(productRunData.shouldUseBlackDuckProduct());
        Assert.assertFalse(productRunData.shouldUsePolarisProduct());
    }

    @Test(expected = DetectUserFriendlyException.class)
    public void blackDuckConnectionFailureWithTestThrows() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.failure("Failed to connect");

        testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);
    }

    @Test(expected = DetectUserFriendlyException.class)
    public void polarisConnectionFailureWithTestThrows() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final PolarisConnectivityResult connectivityResult = PolarisConnectivityResult.failure("Failed to connect");

        testBoot(BlackDuckDecision.skip(), PolarisDecision.runOnline(null), null, connectivityResult, properties);
    }

    @Test()
    public void blackDuckConnectionSuccessWithTestReturnsNull() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.success(Mockito.mock(BlackDuckServicesFactory.class), Mockito.mock(BlackDuckServerConfig.class));

        final ProductRunData productRunData = testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);

        Assert.assertNull(productRunData);
    }

    @Test()
    public void polarisConnectionSuccessWithTestReturnsNull() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final PolarisConnectivityResult connectivityResult = PolarisConnectivityResult.success();

        final ProductRunData productRunData = testBoot(BlackDuckDecision.skip(), PolarisDecision.runOnline(null), null, connectivityResult, properties);

        Assert.assertNull(productRunData);
    }

    @Test()
    public void blackDuckOnlyWorks() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.success(Mockito.mock(BlackDuckServicesFactory.class), Mockito.mock(BlackDuckServerConfig.class));

        final ProductRunData productRunData = testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);

        Assert.assertTrue(productRunData.shouldUseBlackDuckProduct());
        Assert.assertFalse(productRunData.shouldUsePolarisProduct());
    }

    @Test()
    public void polarisOnlyWorks() throws DetectUserFriendlyException {
        final PolarisDecision polarisDecision = PolarisDecision.runOnline(Mockito.mock(PolarisServerConfig.class));

        final PolarisConnectivityResult polarisConnectivityResult = Mockito.mock(PolarisConnectivityResult.class);
        Mockito.when(polarisConnectivityResult.isSuccessfullyConnected()).thenReturn(true);

        final ProductRunData productRunData = testBoot(BlackDuckDecision.skip(), polarisDecision, null, polarisConnectivityResult, new HashMap<>());

        Assert.assertFalse(productRunData.shouldUseBlackDuckProduct());
        Assert.assertTrue(productRunData.shouldUsePolarisProduct());
    }

    private ProductRunData testBoot(final BlackDuckDecision blackDuckDecision, final PolarisDecision polarisDecision) throws DetectUserFriendlyException {
        return testBoot(blackDuckDecision, polarisDecision, null, null, new HashMap<>());
    }

    private ProductRunData testBoot(final BlackDuckDecision blackDuckDecision, final PolarisDecision polarisDecision, final BlackDuckConnectivityResult blackDuckconnectivityResult, final PolarisConnectivityResult polarisConnectivityResult,
        final Map<DetectProperty, Boolean> properties) throws DetectUserFriendlyException {
        final DetectConfig detectConfiguration = Mockito.mock(DetectConfig.class);
        //TODO: Fix - this might break this test.
        //properties.forEach((key, value) -> Mockito.when(detectConfiguration.getBooleanProperty(key, PropertyAuthority.NONE)).thenReturn(value));

        final ProductBootFactory productBootFactory = Mockito.mock(ProductBootFactory.class);
        Mockito.when(productBootFactory.createPhoneHomeManager(Mockito.any())).thenReturn(null);

        final ProductDecision productDecision = new ProductDecision(blackDuckDecision, polarisDecision);

        final ProductBoot productBoot = new ProductBoot();

        final BlackDuckConnectivityChecker blackDuckConnectivityChecker = Mockito.mock(BlackDuckConnectivityChecker.class);
        Mockito.when(blackDuckConnectivityChecker.determineConnectivity(Mockito.any())).thenReturn(blackDuckconnectivityResult);

        final PolarisConnectivityChecker polarisConnectivityChecker = Mockito.mock(PolarisConnectivityChecker.class);
        Mockito.when(polarisConnectivityChecker.determineConnectivity(Mockito.any())).thenReturn(polarisConnectivityResult);

        return productBoot.boot(productDecision, detectConfiguration, blackDuckConnectivityChecker, polarisConnectivityChecker, productBootFactory);
    }
}
