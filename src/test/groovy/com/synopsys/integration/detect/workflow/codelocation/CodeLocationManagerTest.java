package com.synopsys.integration.detect.workflow.codelocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;

public class CodeLocationManagerTest {

    @Test
    public void test() {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME, PropertyAuthority.None)).thenReturn("myscanname");
        final CodeLocationNameGenerator codeLocationNameGenerator = Mockito.mock(CodeLocationNameGenerator.class);
        final CodeLocationNameManager mgr = new CodeLocationNameManager(detectConfiguration, codeLocationNameGenerator);
        final String firstName = mgr.createScanCodeLocationName("sourcePath", "targetPath", null, "projectName", "projectVersionName", "prefix", "suffix");
        assertEquals("myscanname SCAN", firstName);

        final String secondName = mgr.createScanCodeLocationName("sourcePath", "targetPath", null, "projectName", "projectVersionName", "prefix", "suffix");
        assertEquals("myscanname SCAN 2", secondName);
    }
}
