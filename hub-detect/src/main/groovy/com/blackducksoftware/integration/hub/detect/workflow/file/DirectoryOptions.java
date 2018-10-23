package com.blackducksoftware.integration.hub.detect.workflow.file;

public class DirectoryOptions {
    private String sourcePath;
    private String outputPath;
    private String bdioOutputPath;
    private String scanOutputPath;

    public DirectoryOptions(final String sourcePath, final String outputPath, final String bdioOutputPath, final String scanOutputPath) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.bdioOutputPath = bdioOutputPath;
        this.scanOutputPath = scanOutputPath;
    }

    public String getSourcePathOverride() {
        return sourcePath;
    }

    public String getOutputPathOverride() {
        return outputPath;
    }

    public String getBdioOutputPathOverride() {
        return bdioOutputPath;
    }

    public String getScanOutputPathOverride() {
        return scanOutputPath;
    }
}
