package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;

public class ProjectVersionOptions {
    public String sourcePathName;
    public String projectBomTool;
    public String overrideProjectName;
    public String overrideProjectVersionName;
    public String defaultProjectVersionText;
    public String defaultProjectVersionScheme;
    public String defaultProjectVersionFormat;

    public ProjectVersionOptions(DetectConfiguration detectConfiguration) {
        sourcePathName = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH, PropertyAuthority.None)).getName();
        projectBomTool = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME, PropertyAuthority.None);
        overrideProjectName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME, PropertyAuthority.None);
        overrideProjectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NAME, PropertyAuthority.None);
        defaultProjectVersionText = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TEXT, PropertyAuthority.None);
        defaultProjectVersionScheme = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_SCHEME, PropertyAuthority.None);
        defaultProjectVersionFormat = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT, PropertyAuthority.None);
    }
}
