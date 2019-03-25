package com.synopsys.integration.detect.workflow.codelocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.util.NameVersion;

public class CodeLocationManagerTest {

    @Test
    public void testOverride() {
        // TODO this is really testing code location name generator; move it to the right test
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME, PropertyAuthority.None)).thenReturn("myscanname");
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(detectConfiguration);
        final CodeLocationNameManager mgr = new CodeLocationNameManager(codeLocationNameGenerator);
        final String firstScanName = mgr.createScanCodeLocationName("sourcePath", "targetPath", null, "projectName", "projectVersionName", "prefix", "suffix");
        assertEquals("myscanname SCAN", firstScanName);

        final String secondScanName = mgr.createScanCodeLocationName("sourcePath", "targetPath", null, "projectName", "projectVersionName", "prefix", "suffix");
        assertEquals("myscanname SCAN 2", secondScanName);

        final NameVersion nameVersion = new NameVersion("projectName", "projectVersion");
        final String firstBomName = mgr.createAggregateCodeLocationName(nameVersion);
        assertEquals("myscanname BOM", firstBomName);

        final String secondBomName = mgr.createAggregateCodeLocationName(nameVersion);
        assertEquals("myscanname BOM 2", secondBomName);

        // make sure behavior is not dependent on order
        final String thirdScanName = mgr.createScanCodeLocationName("sourcePath", "targetPath", null, "projectName", "projectVersionName", "prefix", "suffix");
        assertEquals("myscanname SCAN 3", thirdScanName);
    }

    // TODO
//    @Test
//    public void testNoOverride() {
//        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
//        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME, PropertyAuthority.None)).thenReturn(null);
//        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(detectConfiguration);
//        final CodeLocationNameManager mgr = new CodeLocationNameManager(detectConfiguration, codeLocationNameGenerator);
//
//        final DetectCodeLocation detectCodeLocation = Mockito.mock(DetectCodeLocation.class);
//        Mockito.when(detectCodeLocation.getSourcePath()).thenReturn(new File("sourcePath"));
//        final String detectSourcePath = "sourcePath";
//        final String projectName = "projectName";
//        final String projectVersionName = "projectVersion";
//        final String prefix = "prefix";
//        final String suffix = "suffix";
//        final String bomCodeLocationName = mgr.createCodeLocationName(detectCodeLocation,   detectSourcePath,   projectName,   projectVersionName,   prefix,   suffix);
//        assertEquals("tbd", bomCodeLocationName);
//    }
}
