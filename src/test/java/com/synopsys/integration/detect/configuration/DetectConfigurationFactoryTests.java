package com.synopsys.integration.detect.configuration;

import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.factoryOf;
import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.spyFactoryOf;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.common.util.Bdo;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DefaultDetectorSearchExcludedDirectories;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.RapidCompareMode;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectSyncOptions;
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
    public void testIsStatelessIsEnabled() {
        DetectConfigurationFactory factory = factoryOf(
                Pair.of(DetectProperties.DETECT_TOOLS, DetectTool.SIGNATURE_SCAN.toString()),
                Pair.of(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, BlackduckScanMode.STATELESS.toString()));
        
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = factory.createBlackDuckSignatureScannerOptions();

        Assertions.assertTrue(blackDuckSignatureScannerOptions.getIsStateless());
    }
    
    @Test
    public void testNoPersistenceModeSpecified() {
        DetectConfigurationFactory factory = factoryOf(
                Pair.of(DetectProperties.DETECT_BLACKDUCK_RAPID_COMPARE_MODE, RapidCompareMode.BOM_COMPARE_STRICT.toString()));
        
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = factory.createBlackDuckSignatureScannerOptions();

        Assertions.assertTrue(RapidCompareMode.BOM_COMPARE_STRICT.equals(blackDuckSignatureScannerOptions.getBomCompareMode()));
    }
    
    @Test
    public void testAllCloneCategories() {
        DetectConfigurationFactory factory = factoryOf(Pair.of(DetectProperties.DETECT_PROJECT_CLONE_CATEGORIES, "ALL"));
        
        ProjectSyncOptions projectSyncOptions = factory.createDetectProjectServiceOptions();

        List<ProjectCloneCategoriesType> cloneCategories = projectSyncOptions.getCloneCategories();
        
        Assertions.assertTrue(cloneCategories == null);
    }
    
    @Test
    public void testNoCloneCategories() {
        DetectConfigurationFactory factory = factoryOf(Pair.of(DetectProperties.DETECT_PROJECT_CLONE_CATEGORIES, "NONE"));
        ProjectSyncOptions projectSyncOptions = factory.createDetectProjectServiceOptions();

        List<ProjectCloneCategoriesType> cloneCategories = projectSyncOptions.getCloneCategories();
        
        Assertions.assertTrue(cloneCategories.isEmpty()); 
    }
    
    @Test
    public void testSpecificCloneCategories() {
        DetectConfigurationFactory factory = factoryOf(
                Pair.of(DetectProperties.DETECT_PROJECT_CLONE_CATEGORIES, 
                        ProjectCloneCategoriesType.CUSTOM_FIELD_DATA.toString() 
                        + "," 
                        + ProjectCloneCategoriesType.DEEP_LICENSE.toString()
                ));
        
        ProjectSyncOptions projectSyncOptions = factory.createDetectProjectServiceOptions();

        List<ProjectCloneCategoriesType> cloneCategories = projectSyncOptions.getCloneCategories();
        
        Assertions.assertTrue(cloneCategories.contains(ProjectCloneCategoriesType.CUSTOM_FIELD_DATA));
        Assertions.assertTrue(cloneCategories.contains(ProjectCloneCategoriesType.DEEP_LICENSE));
    }
}