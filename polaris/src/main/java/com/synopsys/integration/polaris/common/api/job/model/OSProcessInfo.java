/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
