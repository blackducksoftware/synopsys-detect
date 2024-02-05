package com.synopsys.integration.detect.workflow.blackduck.integratedmatching.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class ScanCounts extends Stringable {

    @SerializedName("PACKAGE_MANAGER")
    private int packageManager;

    @SerializedName("SIGNATURE")
    private int signature;

    @SerializedName("BINARY")
    private int binary;

    public ScanCounts(final int packageManager, final int signature, final int binary) {
        this.packageManager = packageManager;
        this.signature = signature;
        this.binary = binary;
    }

    public int getPackageManager() {
        return packageManager;
    }

    public int getSignature() {
        return signature;
    }

    public int getBinary() {
        return binary;
    }
}
