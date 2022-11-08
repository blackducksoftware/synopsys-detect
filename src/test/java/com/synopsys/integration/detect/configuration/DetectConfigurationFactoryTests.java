package com.synopsys.integration.detect.configuration;

import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.factoryOf;
import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.spyFactoryOf;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.common.util.Bdo;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DefaultDetectorSearchExcludedDirectories;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.rest.credentials.Credentials;

public class DetectConfigurationFactoryTests {

    //#region Proxy
    @Test
    public void proxyUsesCredentials() throws DetectUserFriendlyException {
        DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.BLACKDUCK_PROXY_HOST, "host"),
            Pair.of(DetectProperties.BLACKDUCK_PROXY_PORT, "20"),
            Pair.of(DetectProperties.BLACKDUCK_PROXY_USERNAME, "username"),
            Pair.of(DetectProperties.BLACKDUCK_PROXY_PASSWORD, "password")
        );
        Bdo<Credentials> result = Bdo.of(factory.createBlackDuckProxyInfo().getProxyCredentials());

        Assertions.assertEquals(Optional.of("username"), result.flatMap(Credentials::getUsername).toOptional());
        Assertions.assertEquals(Optional.of("password"), result.flatMap(Credentials::getPassword).toOptional());
    }
    //#endregion Proxy

    //#region Parallel Processors
    @Test
    public void parallelProcessorsDefaultsToOne() {
        // Using the property default is the safe choice. See IDETECT-1970 - JM
        DetectConfigurationFactory factory = spyFactoryOf();
        Integer defaultValue = DetectProperties.DETECT_PARALLEL_PROCESSORS.getDefaultValue();

        Assertions.assertEquals(defaultValue.intValue(), factory.findParallelProcessors());
        Mockito.verify(factory, Mockito.never()).findRuntimeProcessors();
    }

    @Test
    public void parallelProcessorsPrefersProperty() {
        DetectConfigurationFactory factory = factoryOf(Pair.of(DetectProperties.DETECT_PARALLEL_PROCESSORS, "3"));

        Assertions.assertEquals(3, factory.findParallelProcessors());
    }

    //#endregion Parallel Processors

    @Test
    public void testDefaultSignatureScannerExcludedDirectories() {
        DetectConfigurationFactory factory = factoryOf();

        List<String> actualExcludedDirectories = factory.collectDetectorSearchDirectoryExclusions();

        List<String> defaultExcludedDirectories = DefaultDetectorSearchExcludedDirectories.getDirectoryNames();

        Assertions.assertEquals(defaultExcludedDirectories, actualExcludedDirectories);
    }
    
    @Test
    public void testIsEphemeralIsEnabled() {
        DetectConfigurationFactory factory = factoryOf(
                Pair.of(DetectProperties.DETECT_TOOLS, DetectTool.SIGNATURE_SCAN.toString()),
                Pair.of(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, BlackduckScanMode.EPHEMERAL.toString()));
        
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = factory.createBlackDuckSignatureScannerOptions();

        Assertions.assertTrue(blackDuckSignatureScannerOptions.getIsEphemeral());
    }
}