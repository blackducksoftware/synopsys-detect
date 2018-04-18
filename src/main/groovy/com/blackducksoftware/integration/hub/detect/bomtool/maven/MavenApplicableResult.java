package com.blackducksoftware.integration.hub.detect.bomtool.maven;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class MavenApplicableResult  extends BomToolApplicableResult {
    private final File pomXmlPath;
    private final File pomWrapperPath;
    private final String mavenExe;

    public MavenApplicableResult(final File directory, final File pomXmlPath, final File pomWrapperPath, final String mavenExe) {
        super(directory, BomToolType.MAVEN);
        this.pomXmlPath = pomXmlPath;
        this.pomWrapperPath = pomWrapperPath;
        this.mavenExe = mavenExe;
    }

    public File getPomXmlPath() {
        return pomXmlPath;
    }

    public File getPomWrapperPath() {
        return pomWrapperPath;
    }

    public String getMavenExe() {
        return mavenExe;
    }

}