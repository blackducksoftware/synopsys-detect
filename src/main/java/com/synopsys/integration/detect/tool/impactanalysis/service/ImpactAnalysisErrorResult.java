package com.synopsys.integration.detect.tool.impactanalysis.service;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class ImpactAnalysisErrorResult {
    @SerializedName("status")
    public final String status;

    @SerializedName("matchCount")
    public final Integer matchCount;

    @SerializedName("numDirs")
    public final Integer numberOfDirectories;

    @SerializedName("numNonDirFiles")
    public final Integer numberOfNonDirectoryFiles;

    @SerializedName("scanTime")
    public final Integer scanTime;

    @SerializedName("timeLastModified")
    public final Integer timeLastModified;

    @SerializedName("timeToPersistMs")
    public final Integer timeToPersistMs;

    @Nullable
    @SerializedName("errorCode")
    public final String errorMessage;

    public ImpactAnalysisErrorResult(String status, Integer matchCount, Integer numberOfDirectories, Integer numberOfNonDirectoryFiles, Integer scanTime, Integer timeLastModified, Integer timeToPersistMs, @Nullable String errorMessage) {
        this.status = status;
        this.matchCount = matchCount;
        this.numberOfDirectories = numberOfDirectories;
        this.numberOfNonDirectoryFiles = numberOfNonDirectoryFiles;
        this.scanTime = scanTime;
        this.timeLastModified = timeLastModified;
        this.timeToPersistMs = timeToPersistMs;
        this.errorMessage = errorMessage;
    }

}
