/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class OSProcessInfo extends PolarisComponent {
    @SerializedName("executable")
    private String executable;

    @SerializedName("commandArguments")
    private List<String> commandArguments = null;

    @SerializedName("exitCode")
    private Integer exitCode;

    @SerializedName("stdoutTail")
    private List<String> stdoutTail = null;

    @SerializedName("stderrTail")
    private List<String> stderrTail = null;

    /**
     * Get executable
     * @return executable
     */
    public String getExecutable() {
        return executable;
    }

    /**
     * Get commandArguments
     * @return commandArguments
     */
    public List<String> getCommandArguments() {
        return commandArguments;
    }

    /**
     * Get exitCode
     * @return exitCode
     */
    public Integer getExitCode() {
        return exitCode;
    }

    /**
     * Get stdoutTail
     * @return stdoutTail
     */
    public List<String> getStdoutTail() {
        return stdoutTail;
    }

    /**
     * Get stderrTail
     * @return stderrTail
     */
    public List<String> getStderrTail() {
        return stderrTail;
    }

}
