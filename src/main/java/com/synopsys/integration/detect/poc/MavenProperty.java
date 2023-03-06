package com.synopsys.integration.detect.poc;

public class MavenProperty {
    private String value;
    private int lineNo;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public MavenProperty() {
    }

    public MavenProperty(String value, int lineNo) {
        this.value = value;
        this.lineNo = lineNo;
    }
}
