package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import com.google.gson.annotations.SerializedName;

public class SolveMeta {
    @SerializedName("inputs-digest")
    private String inputsDigest;

    @SerializedName("analyzer-name")
    private String analyzerName;

    @SerializedName("analyzer-version")
    private Integer analyzerVersion;

    @SerializedName("solver-name")
    private String solverName;

    @SerializedName("solver-version")
    private Integer solverVersion;

    public String getInputsDigest() {
        return inputsDigest;
    }

    public void setInputsDigest(final String inputsDigest) {
        this.inputsDigest = inputsDigest;
    }

    public String getAnalyzerName() {
        return analyzerName;
    }

    public void setAnalyzerName(final String analyzerName) {
        this.analyzerName = analyzerName;
    }

    public Integer getAnalyzerVersion() {
        return analyzerVersion;
    }

    public void setAnalyzerVersion(final Integer analyzerVersion) {
        this.analyzerVersion = analyzerVersion;
    }

    public String getSolverName() {
        return solverName;
    }

    public void setSolverName(final String solverName) {
        this.solverName = solverName;
    }

    public Integer getSolverVersion() {
        return solverVersion;
    }

    public void setSolverVersion(final Integer solverVersion) {
        this.solverVersion = solverVersion;
    }

}
