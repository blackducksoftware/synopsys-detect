package com.synopsys.integration.detect.workflow.codelocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.util.NameVersion;

public class CodeLocationManagerTest {

    @Test
    public void test() {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME, PropertyAuthority.None)).thenReturn("myscanname");
        final CodeLocationNameGenerator codeLocationNameGenerator = Mockito.mock(CodeLocationNameGenerator.class);
        final CodeLocationNameManager mgr = new CodeLocationNameManager(detectConfiguration, codeLocationNameGenerator);
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
}
