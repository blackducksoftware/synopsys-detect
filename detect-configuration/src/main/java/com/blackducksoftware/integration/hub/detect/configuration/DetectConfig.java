package com.blackducksoftware.integration.hub.detect.configuration;

import java.io.File;

import org.apache.commons.lang3.BooleanUtils;

public class DetectConfig extends BaseConfig {

    private boolean usingDefaultSourcePath;
    private boolean usingDefaultOutputPath;

    private File sourceDirectory;
    private File outputDirectory;

    public void initialize(final ValueContainer valueContainer, final boolean usingDefaultSourcePath, final boolean usingDefaultOutputPath, final File sourceDirectory, final File outputDirectory) {
        this.usingDefaultSourcePath = usingDefaultSourcePath;
        this.usingDefaultOutputPath = usingDefaultOutputPath;
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    // properties start

    private Boolean failOnConfigWarning;

    private Boolean forceSuccess;

    private Boolean suppressConfigurationOutput;

    private Boolean suppressResultsOutput;

    private Boolean cleanupDetectFiles;

    private Boolean resolveTildeInPaths;

    private String sourcePath;

    private String outputDirectoryPath;

    private String bdioOutputDirectoryPath;

    private String scanOutputDirectoryPath;

    private Integer searchDepth;

    private String loggingLevel;

    private String defaultProjectVersionScheme;

    private String defaultProjectVersionText;

    private String defaultProjectVersionTimeformat;

    private String aggregateBomName;

    public boolean getCombineCodeLocations() {
        return false; //for now this is always false, in the future we could introduce a property.
    }

    public Boolean getCleanupDetectFiles() {
        return BooleanUtils.toBoolean(cleanupDetectFiles);
    }

    public boolean getResolveTildeInPaths() {
        return BooleanUtils.toBoolean(resolveTildeInPaths);
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getOutputDirectoryPath() {
        return outputDirectoryPath;
    }

    public String getBdioOutputDirectoryPath() {
        return bdioOutputDirectoryPath;
    }

    public String getScanOutputDirectoryPath() {
        return scanOutputDirectoryPath;
    }

    public int getSearchDepth() {
        return convertInt(searchDepth);
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public Boolean getFailOnConfigWarning() {
        return BooleanUtils.toBoolean(failOnConfigWarning);
    }

    public boolean getSuppressConfigurationOutput() {
        return BooleanUtils.toBoolean(suppressConfigurationOutput);
    }

    public boolean getForceSuccess() {
        return BooleanUtils.toBoolean(forceSuccess);
    }

    public boolean getSuppressResultsOutput() {
        return BooleanUtils.toBoolean(suppressResultsOutput);
    }

    public String getDefaultProjectVersionScheme() {
        return defaultProjectVersionScheme == null ? null : defaultProjectVersionScheme.trim();
    }

    public String getDefaultProjectVersionText() {
        return defaultProjectVersionText == null ? null : defaultProjectVersionText.trim();
    }

    public String getDefaultProjectVersionTimeformat() {
        return defaultProjectVersionTimeformat == null ? null : defaultProjectVersionTimeformat.trim();
    }

    public String getAggregateBomName() {
        return aggregateBomName == null ? null : aggregateBomName.trim();
    }

    // properties end
}
