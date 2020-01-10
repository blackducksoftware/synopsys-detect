package com.synopsys.integration.detect.boot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.configuration.config.DetectConfig;
import com.synopsys.integration.configuration.config.DetectPropertySource;
import com.synopsys.integration.detect.DetectTool;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;

//TODO: Consider separating configuration.
public class ProductDeciderTest {

    @Test()
    public void shouldRunPolaris() throws DetectUserFriendlyException {
        final File userHome = Mockito.mock(File.class);
        final DetectConfig detectConfiguration = configuration("POLARIS_ACCESS_TOKEN", "access token text", "POLARIS_URL", "http://polaris.com");

        final ProductDecider productDecider = new ProductDecider();
        final DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(Boolean.TRUE);
        final ProductDecision productDecision = productDecider.decide(detectConfiguration, userHome, detectToolFilter);

        Assert.assertTrue(productDecision.getPolarisDecision().shouldRun());
    }

    @Test()
    public void shouldRunPolarisWhenExcluded() throws DetectUserFriendlyException {
        final File userHome = Mockito.mock(File.class);
        final DetectConfig detectConfiguration = configuration("POLARIS_ACCESS_TOKEN", "access token text", "POLARIS_URL", "http://polaris.com");

        final ProductDecider productDecider = new ProductDecider();
        final DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(Boolean.FALSE);
        final ProductDecision productDecision = productDecider.decide(detectConfiguration, userHome, detectToolFilter);

        Assert.assertFalse(productDecision.getPolarisDecision().shouldRun());
    }

    @Test()
    public void shouldRunBlackDuckOffline() throws DetectUserFriendlyException {
        final File userHome = Mockito.mock(File.class);
        final DetectConfig detectConfiguration = Mockito.mock(DetectConfig.class);
        Mockito.when(detectConfiguration.getValueOrDefault(DetectProperties.Companion.getBLACKDUCK_OFFLINE_MODE())).thenReturn(true);

        final ProductDecider productDecider = new ProductDecider();
        final DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(Boolean.TRUE);
        final ProductDecision productDecision = productDecider.decide(detectConfiguration, userHome, detectToolFilter);

        Assert.assertTrue(productDecision.getBlackDuckDecision().shouldRun());
        Assert.assertTrue(productDecision.getBlackDuckDecision().isOffline());
    }

    @Test()
    public void shouldRunBlackDuckOnline() throws DetectUserFriendlyException {
        final File userHome = Mockito.mock(File.class);
        final DetectConfig detectConfiguration = Mockito.mock(DetectConfig.class);
        Mockito.when(detectConfiguration.getValueOrNull(DetectProperties.Companion.getBLACKDUCK_URL())).thenReturn("some-url");

        final ProductDecider productDecider = new ProductDecider();
        final DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(Boolean.TRUE);
        final ProductDecision productDecision = productDecider.decide(detectConfiguration, userHome, detectToolFilter);

        Assert.assertTrue(productDecision.getBlackDuckDecision().shouldRun());
        Assert.assertFalse(productDecision.getBlackDuckDecision().isOffline());
    }

    @Test()
    public void decidesNone() throws DetectUserFriendlyException {
        final File userHome = Mockito.mock(File.class);
        final DetectConfig detectConfiguration = Mockito.mock(DetectConfig.class);

        final ProductDecider productDecider = new ProductDecider();
        final DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(Boolean.TRUE);
        final ProductDecision productDecision = productDecider.decide(detectConfiguration, userHome, detectToolFilter);

        Assert.assertFalse(productDecision.willRunAny());
    }

    private DetectConfig configuration(final String... keys) {
        final Map<String, String> keyMap = new HashMap<>();
        for (int i = 0; i < keys.length; i += 2) {
            keyMap.put(keys[i], keys[i + 1]);
        }
        final DetectPropertySource detectPropertySource = new DetectPropertySource() {
            @Override
            public boolean hasKey(@NotNull final String key) {
                return keyMap.containsKey(key);
            }

            @Nullable
            @Override
            public String getKey(@NotNull final String key) {
                return keyMap.get(key);
            }

            @NotNull
            @Override
            public String getName() {
                return "Test";
            }
        };
        final List<DetectPropertySource> propertySources = new ArrayList<>();
        propertySources.add(detectPropertySource);

        return new DetectConfig(propertySources);
    }
}
