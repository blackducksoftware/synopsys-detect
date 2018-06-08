package com.blackducksoftware.integration.hub.detect.manager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.codelocation.BomCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.codelocation.DockerCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.codelocation.ScanCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class CodeLocationNameManager {

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    private BomCodeLocationNameService bomCodeLocationNameService;

    @Autowired
    private DockerCodeLocationNameService dockerCodeLocationNameService;

    @Autowired
    private ScanCodeLocationNameService scanCodeLocationNameService;

    private int givenCodeLocationOverrideCount = 0;

    public boolean useCodeLocationOverride() {
        if (StringUtils.isNotBlank(detectConfiguration.getCodeLocationNameOverride())) {
            return true;
        } else {
            return false;
        }
    }

    public String getNextCodeLocationOverrideName() {
        final String base = detectConfiguration.getCodeLocationNameOverride();
        final String codeLocationName = base + " " + Integer.toString(givenCodeLocationOverrideCount);
        givenCodeLocationOverrideCount++;
        return codeLocationName;
    }

    public String createAggregateCodeLocationName() {
        if (useCodeLocationOverride()) {
            return getNextCodeLocationOverrideName();
        } else {
            return ""; //it is overridden in bdio creation later.
        }
    }

    public String createCodeLocationName(final DetectCodeLocation detectCodeLocation, final String detectSourcePath, final String projectName, final String projectVersionName,
            final String prefix, final String suffix) {

        if (useCodeLocationOverride()) {
            return getNextCodeLocationOverrideName();
        } else if (BomToolType.DOCKER == detectCodeLocation.getBomToolType()) {
            return dockerCodeLocationNameService.createCodeLocationName(detectCodeLocation.getSourcePath(), projectName, projectVersionName, detectCodeLocation.getDockerImage(), detectCodeLocation.getBomToolType(), prefix, suffix);
        } else {
            return bomCodeLocationNameService.createCodeLocationName(detectSourcePath, detectCodeLocation.getSourcePath(), detectCodeLocation.getExternalId(), detectCodeLocation.getBomToolType(), prefix, suffix);
        }
    }

    public String createScanCodeLocationName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        if (useCodeLocationOverride()) {
            return getNextCodeLocationOverrideName();
        } else {
            return scanCodeLocationNameService.createCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);
        }
    }

}
