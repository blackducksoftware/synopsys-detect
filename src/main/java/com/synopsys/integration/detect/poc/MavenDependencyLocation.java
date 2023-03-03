package com.synopsys.integration.detect.poc;

public class MavenDependencyLocation {
    private String pomFilePath; // relative to the proj src dir (detect.source.path)
    private int lineNo;

    public MavenDependencyLocation(String pomFilePath, int lineNo) {
        this.pomFilePath = pomFilePath;
        this.lineNo = lineNo;
    }

    public String getPomFilePath() {
        return pomFilePath;
    }

    public void setPomFilePath(String pomFilePath) {
        this.pomFilePath = pomFilePath;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }
}
