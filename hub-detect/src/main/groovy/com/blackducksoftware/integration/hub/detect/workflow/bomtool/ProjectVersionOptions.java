package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;

public class ProjectVersionOptions {
    public String sourcePathName;
    public String projectBomTool;
    public String overrideProjectName;
    public String overrideProjectVersionName;
    public String defaultProjectVersionText;
    public String defaultProjectVersionScheme;
    public String defaultProjectVersionFormat;

    public ProjectVersionOptions (DetectConfiguration detectConfiguration){
        sourcePathName = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH)).getName();
        projectBomTool = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME);
        overrideProjectName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME);
        overrideProjectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NAME);
        defaultProjectVersionText = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TEXT);
        defaultProjectVersionScheme = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_SCHEME);
        defaultProjectVersionFormat = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT);
    }
}
